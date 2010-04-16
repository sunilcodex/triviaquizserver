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
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;

public class QuestionsAdminServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(QuestionsAdminServlet.class.getName());
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long key = Long.valueOf(req.getParameter("key"));
        String action = req.getParameter("action");
        if (action.equals("delete")) {
            PersistenceManager pm = PMF.get().getPersistenceManager();

            TriviaQuizQuestions question = pm.getObjectById(TriviaQuizQuestions.class, key);
            pm.deletePersistent(question);
            pm.close();
        } else if (action.equals("approve")) {
            PersistenceManager pm = PMF.get().getPersistenceManager();

            TriviaQuizQuestions question = pm.getObjectById(TriviaQuizQuestions.class, key);
            question.setApproved(true);
            pm.close();
        }

        resp.sendRedirect("/admin/questions.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long number = null;
        String questionStr = null;
        InputStream imageUpload = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageData = null;

        try {
            ServletFileUpload upload = new ServletFileUpload();
            resp.setContentType("text/plain");

            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();

                if (!item.isFormField()) {
                    log.info("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());

                    if (item.getFieldName().equals("imageUpload")) {
                        imageUpload = item.openStream();
                        System.out.println("Length: " + imageUpload.available());

                        int len;
                        byte[] buffer = new byte[8192];
                        while ((len = imageUpload.read(buffer, 0, buffer.length)) != -1) {
                            baos.write(buffer, 0, len);
                        }

                        imageData = baos.toByteArray();
                        System.out.println("Size : " + imageData.length);
                        // imageUpload.read(imageData);
                    }
                } else {
                    if (item.getFieldName().equals("number")) {
                        String num = IOUtils.toString(item.openStream());
                        number = Long.valueOf(num);
                        System.out.println("Number: " + number);
                    } else if (item.getFieldName().equals("question")) {
                        questionStr = IOUtils.toString(item.openStream());
                        System.out.println("question: " + questionStr);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

        if (imageUpload != null) {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            try {
                Blob image = new Blob(imageData);

                TriviaQuizQuestions question = new TriviaQuizQuestions(number, questionStr, image);

                pm.makePersistent(question);
                Long id = question.getId();
                resp.getWriter().println("Success: " + id);
            } finally {
                pm.close();
            }
        } else {
            System.err.println("Failed to get image file: " + number);
        }
        
        resp.sendRedirect("/admin/questions.jsp");
    }

}
