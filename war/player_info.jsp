<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/xml; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="javax.jdo.Query"%>
<%@ page import="com.androidbook.triviaquizserver.PMF"%>
<%@ page import="com.androidbook.triviaquizserver.PlayerPersistentData"%>
<playerinfo> <%
     PersistenceManager pm = null;
     try {
         Long playerId = Long.valueOf(request.getParameter("playerId"));
         String password = request.getParameter("password");
         pm = PMF.get().getPersistenceManager();
         PlayerPersistentData player = pm.getObjectById(PlayerPersistentData.class, playerId);
         String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
         boolean matched = player.getPassword().equals(password);
 %> <nickname><%=player.getNickname()%></nickname> <avatarUrl><%=baseUrl%><%=player.getAvatarUrl()%></avatarUrl>
<score><%=player.getScore()%></score> <%
     if (matched) {
 %> <email><%=player.getEmail()%></email> <birthdate><%=player.getBirthdate().getTime()%></birthdate>
<favoriteplace><%=player.getFavoritePlace()%></favoriteplace> <gender><%=player.getGender()%></gender>
<%
    }
    } catch (Exception e) {
        System.err.println("Failed to get player info");
        e.printStackTrace();
    } finally {
        if (pm != null) {
            pm.close();
        }
    }
%> </playerinfo>
