<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/xml; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.TreeSet"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="javax.jdo.Query"%>
<%@ page import="com.androidbook.triviaquizserver.PMF"%>
<%@ page import="com.androidbook.triviaquizserver.PlayerPersistentData"%>
<scores> <%
     String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
     PersistenceManager pm = PMF.get().getPersistenceManager();

     String friendsOf = request.getParameter("playerId");
     String followers = request.getParameter("followers");
     Collection<PlayerPersistentData> players;
     if (friendsOf == null) {

         Query query = pm.newQuery(PlayerPersistentData.class);
         query.setOrdering("score desc");
         query.setRange(0, 25);

         players = (List<PlayerPersistentData>) query.execute();
     } else {

         players = new TreeSet<PlayerPersistentData>(new Comparator<PlayerPersistentData>() {

             public int compare(PlayerPersistentData first, PlayerPersistentData second) {
                 int result = 0;
                 Long firstScore = first.getScore();
                 Long secondScore = second.getScore();
                 if (firstScore.equals(secondScore)) {
                     result = first.getId().compareTo(second.getId());
                 } else {
                     result = secondScore.compareTo(firstScore);
                 }
                 return result;
             }
         });

         Long key = Long.valueOf(friendsOf);

         PlayerPersistentData ppd = pm.getObjectById(PlayerPersistentData.class, key);

         if (ppd != null) {
             Set<Long> friendKeys; 
             
             if (followers != null && followers.equals("true")) {
                 friendKeys = ppd.getFollowers();
             } else {
                 friendKeys = ppd.getFriends();
             }

             if (friendKeys != null) {
                 for (Long friendKey : friendKeys) {
                     PlayerPersistentData friend = pm.getObjectById(PlayerPersistentData.class, friendKey);
                     players.add(friend);
                 }
             }
         }
     }

     int rank = 1;
     for (PlayerPersistentData player : players) {
 %> <score username="<%=player.getNickname()%>"
	score="<%=player.getScore()%>" rank="<%=rank%>"
	avatarUrl="<%=baseUrl%><%=player.getAvatarUrl()%>" /> <%
     rank++;
     }
     pm.close();
 %> </scores>