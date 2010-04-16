/*
 * Copyright (c) 2009, Lauren Darcey and Shane Conder
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following disclaimer.
 *   
 * * Redistributions in binary form must reproduce the above copyright notice, this list 
 *   of conditions and the following disclaimer in the documentation and/or other 
 *   materials provided with the distribution.
 *   
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific prior 
 *   written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.androidbook.triviaquizserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;

public class ReceivePlayerDataServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(FileUpload.class.getName());
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String uniqueId = req.getParameter("uniqueId");
        String updateIdStr = req.getParameter("updateId");
        String nickname = req.getParameter("nickname");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String favePlace = req.getParameter("faveplace");
        String scoreStr = req.getParameter("score");
        Long score = 0L;
        if (scoreStr != null && scoreStr.length() > 0) {
            score = Long.valueOf(scoreStr);
        }
        String gender = req.getParameter("gender");
        
        String dobStr = req.getParameter("dob");
        Date dob = new Date();
        if (dobStr != null && dobStr.length() > 0) {
            dob.setTime(Long.valueOf(dobStr));
        }

        // Date birthdate = new Date( Date.parse(req.getParameter("birthdate")));

        System.out.println("Nick: " + nickname + "\nemail: " + email);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            if (updateIdStr == null || updateIdStr.length() == 0) {
                // new
                PlayerPersistentData ppd = new PlayerPersistentData(uniqueId, nickname, email, password, dob, gender, favePlace, score);

                pm.makePersistent(ppd);
                Long id = ppd.getId();
                resp.getWriter().print(id);

            } else {
                // update
                // TODO: either verify uniqueId, allow it to be updated, or something else...
                // Right now, this means uniqueId *must* be part of the creation data
                // As a server largely driven by the client, this will be enforced on the client side
                Long key = Long.valueOf(updateIdStr);
                System.out.println("Key to update: " + key);
                PlayerPersistentData ppd = pm.getObjectById(PlayerPersistentData.class, key);

                
                // for all of these, make sure we actually got a value via the query variables
                if (nickname != null && nickname.length() > 0 ) {
                    ppd.setNickname(nickname);
                }
                
                if (email != null && email.length() > 0) {
                    ppd.setEmail(email);
                }
                
                if (password != null && password.length() > 0) {
                    ppd.setPassword(password);
                }
                
                if (scoreStr != null && scoreStr.length() > 0) {
                    ppd.setScore(score);
                }
                
                if (dobStr != null && dobStr.length() > 0) {
                    ppd.setBirthdate(dob);
                }
                
                if (favePlace != null && favePlace.length() > 0) {
                    ppd.setFavoritePlace(favePlace);
                }
                
                if (gender != null && gender.length() > 0) {
                    ppd.setGender(gender);
                }

            }
        } finally {
            pm.close();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long updateId = null;
        InputStream avatarStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageData = null;

        try {
            ServletFileUpload upload = new ServletFileUpload();

            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();

                if (!item.isFormField()) {
                    log.info("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());

                    if (item.getFieldName().equals("avatar")) {
                        avatarStream = item.openStream();
                        log.info("Length: " + avatarStream.available());

                        int len;
                        byte[] buffer = new byte[8192];
                        while ((len = avatarStream.read(buffer, 0, buffer.length)) != -1) {
                            baos.write(buffer, 0, len);
                        }

                        imageData = baos.toByteArray();
                        log.info("Size : " + imageData.length);
                    }
                } else {
                    if (item.getFieldName().equals("updateId")) {
                        String num = IOUtils.toString(item.openStream());
                        updateId = Long.valueOf(num);
                        log.info("Number: " + updateId);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

        if (avatarStream != null) {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            try {
                Blob avatar = new Blob(imageData);

                log.info("Key to update: " + updateId);
                PlayerPersistentData ppd = pm.getObjectById(PlayerPersistentData.class, updateId);

                ppd.setAvatar(avatar);
                pm.makePersistent(ppd);

            } finally {
                pm.close();
            }
        } else {
            log.warning("Failed to get image file: " + updateId);
        }
    }
}
