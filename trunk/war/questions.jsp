<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/xml; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="javax.jdo.Query"%>
<%@ page import="com.androidbook.triviaquizserver.PMF"%>
<%@ page import="com.androidbook.triviaquizserver.TriviaQuizQuestions"%>
<%@ page import="com.androidbook.triviaquizserver.PlayerPersistentData"%>
<questions> <%
     Long startNum = 1L;
     Long max = 5L;
     String startNumStr = request.getParameter("start");
     if (startNumStr != null) {
         startNum = Long.valueOf(startNumStr);
     }

     String maxStr = request.getParameter("max");
     if (maxStr != null) {
         max = Long.valueOf(maxStr);
     }
     PersistenceManager pm = PMF.get().getPersistenceManager();

     Query query = pm.newQuery(TriviaQuizQuestions.class);
     query.setFilter("number >= startNum && number < endNum && approved == true");
     query.setOrdering("number asc");
     query.declareParameters("Long startNum, Long endNum");
     Long endNum = startNum + max;
     System.out.println("Start: " + startNum + "\nEnd: " + endNum);
     List<TriviaQuizQuestions> questions = (List<TriviaQuizQuestions>) query.execute(startNum, endNum);
     String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
     //System.out.println("base url: " + baseUrl);
     if (!questions.isEmpty()) {
         for (TriviaQuizQuestions question : questions) {
 %> <question number="<%=question.getNumber()%>"
	text="<%=question.getTriviaQuestion()%>"
	imageUrl="<%=baseUrl%><%=question.getQuestionImageUrl()%>" /> <%
     }
     }

     try {
         String updateScore = request.getParameter("updateScore");
         if (updateScore.equals("yes")) {
             String updateIdStr = request.getParameter("updateId");
             String scoreStr = request.getParameter("score");
             Long key = Long.valueOf(updateIdStr);
             Long score = Long.valueOf(scoreStr);
             System.out.println("Key to update score: " + key);
             PlayerPersistentData ppd = pm.getObjectById(PlayerPersistentData.class, key);
             ppd.setScore(score);

         }
     } catch (Exception e) {
         // eat this; noncritical
         System.err.println("Failed to update score");
         e.printStackTrace();
     }

     pm.close();
 %> </questions>