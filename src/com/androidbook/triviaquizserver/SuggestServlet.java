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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;

public class SuggestServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(SuggestServlet.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = -4066626770338510331L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long playerId = null;
        String questionStr = null;
        InputStream imageUpload = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageData = null;

        boolean succeeded = false;

        resp.getWriter().println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        try {
            ServletFileUpload upload = new ServletFileUpload();
            resp.setContentType("text/xml");

            FileItemIterator iterator;
            try {
                iterator = upload.getItemIterator(req);

                while (iterator.hasNext()) {
                    FileItemStream item = iterator.next();

                    if (!item.isFormField()) {
                        log.info("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());

                        if (item.getFieldName().equals("questionImage")) {
                            imageUpload = item.openStream();
                            log.info("Length: " + imageUpload.available());

                            int len;
                            byte[] buffer = new byte[8192];
                            while ((len = imageUpload.read(buffer, 0, buffer.length)) != -1) {
                                baos.write(buffer, 0, len);
                            }

                            imageData = baos.toByteArray();
                            log.info("Size : " + imageData.length);
                            // imageUpload.read(imageData);
                        }
                    } else {
                        if (item.getFieldName().equals("playerId")) {

                            playerId = Long.valueOf(IOUtils.toString(item.openStream()));
                            log.info("Number: " + playerId);
                        } else if (item.getFieldName().equals("question")) {
                            questionStr = IOUtils.toString(item.openStream());
                            log.info("question: " + questionStr);
                        }
                    }
                }
            } catch (FileUploadException e) {
                log.log(Level.WARNING, "Failed to handle form; invalid?");
            }
            
            if (imageUpload != null) {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Blob image = new Blob(imageData);

                    // TODO: confirm playerId exists
                    // (right now, it's just used for fun)
                    TriviaQuizQuestions question = new TriviaQuizQuestions(questionStr, image, playerId);

                    pm.makePersistent(question);
                    Long id = question.getId();
                    log.info("New suggestion id: " + id);
                    succeeded = true;
                } finally {
                    pm.close();
                }
            } else {
                log.log(Level.WARNING, "Failed to get image file");
            }

        } finally {
            resp.getWriter().println("<suggest-result>" + succeeded + "</suggest-result>");
        }

    }
}
