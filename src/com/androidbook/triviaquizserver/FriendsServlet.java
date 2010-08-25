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

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FriendsServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Boolean friendAdded = false;
        Boolean friendFound = false;
        Boolean friendRemoved = false;
        PersistenceManager pm = null;
        try {
            String command = req.getParameter("command");
            String friendEmail = req.getParameter("friend");
            String playerId = req.getParameter("playerId");

            // first, we should make sure the playerId is good
            Long key = Long.valueOf(playerId);

            pm = PMF.get().getPersistenceManager();

            PlayerPersistentData player = pm.getObjectById(PlayerPersistentData.class, key);

            if (player != null) {
                Query playerQuery = pm.newQuery(PlayerPersistentData.class);
                playerQuery.setFilter("email == friendEmail");
                playerQuery.declareParameters("String friendEmail");
                //playerQuery.setRange(1, 2);

                Object result = playerQuery.execute(friendEmail);
                PlayerPersistentData friend;

                List<PlayerPersistentData> friends = null;
                
                // perform a bit of checking, though we can't readily remove the warning
                if (result instanceof List) {
                    friends = (List<PlayerPersistentData>) result;
                }
                
                if (friends != null && friends.size() > 0) {
                    friend = friends.get(0);
                } else {
                    friend = null;
                }

                if (friend != null) {
                    friendFound = true;
                    if (command.equals("add")) {
                        player.addFriend(friend);
                        friendAdded = true;
                    } else if (command.equals("remove")) {
                        player.removeFriend(friend);
                        friendRemoved = true;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to do friend command: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (pm != null) {
                pm.close();
            }
        }

        StringBuilder response = new StringBuilder();
        response.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        response.append("<friend-response><added>");
        response.append(friendAdded);
        response.append("</added><removed>");
        response.append(friendRemoved);
        response.append("</removed><found>");
        response.append(friendFound);
        response.append("</found></friend-response>");

        resp.getWriter().println(response);
    }

}
