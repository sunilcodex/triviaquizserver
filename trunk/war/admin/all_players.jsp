<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.androidbook.triviaquizserver.PlayerPersistentData"%>
<%@ page import="com.androidbook.triviaquizserver.PMF"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>List of all Been There, Done That! Players</title>
</head>
<body>
<h2>New Player</h2>
<form action="/receive" method="get">Id: <input type="text"
	name="updateId" /><br />
Nickname: <input type="text" name="nickname" /><br />
Email: <input type="text" name="email" /> <br />
Password: <input type="text" name="password" /> <br />
Score: <input type="text" name="score" /><br />
<input type="submit" value="Do it!" /><br />
</form>
<h2>All Players</h2>
<table>
	<thead>
		<tr>
			<td>Id</td>
			<td>Pic</td>
			<td>Nickname</td>
			<td>Email</td>
			<td>Score</td>
			<td>Actions</td>
		</tr>
	</thead>

	<%
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    String query = "select from " + PlayerPersistentData.class.getName() + " order by nickname";
	    List<PlayerPersistentData> players = (List<PlayerPersistentData>) pm.newQuery(query).execute();
	    String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
	    if (players.isEmpty()) {
	%>No players<%
	    } else {
	        for (PlayerPersistentData player : players) {
	%>
	<tr>
		<td>Id:<%=player.getId()%></td>
		<td><img src="<%= baseUrl%><%= player.getAvatarUrl() %>" /></td>
		<td>Nickname: <%=player.getNickname()%></td>
		<td>Email: <%=player.getEmail() %>
		<td>Score: <%=player.getScore()%></td>
		<td>
		<form action="/receive" method="post" enctype="multipart/form-data"><input
			type="hidden" name="updateId" value="<%=player.getId()%>" /> <input
			type="file" name="avatar" /><input type="submit" value="Upload" /></form>
		</td>
	</tr>
	<%
	    }
	    }
	    pm.close();
	%>
</table>
</body>
</html>