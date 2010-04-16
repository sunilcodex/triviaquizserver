<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<%@ page import="com.androidbook.triviaquizserver.PMF"%>
<%@ page import="com.androidbook.triviaquizserver.TriviaQuizQuestions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Questions Admin Page</title>
</head>
<body>
New question
<form action="questions" method="post" enctype="multipart/form-data">
Number: <input type="text" name="number" /><br />
Question: <input type="text" name="question" /><br />
Image: <input type="file" name="imageUpload" /><br />
<input type="submit" name="submit" value="Gogogo!" /></form>

All questions:
<table>

<thead>
<tr>
<td>Number</td>
<td>Approved?</td>
<td>Question</td>
<td>Image</td>
<td>Action</td>
</tr>
</thead>
<%

PersistenceManager pm = PMF.get().getPersistenceManager();
String query = "select from " + TriviaQuizQuestions.class.getName() + " order by number desc";
List<TriviaQuizQuestions> questions = (List<TriviaQuizQuestions>)pm.newQuery(query).execute();
String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
System.out.println("base url: " + baseUrl);
if (!questions.isEmpty()) {
    for (TriviaQuizQuestions question : questions) {
        String delUrl = "questions?action=delete&key="+question.getId();
        String approveUrl = "questions?action=approve&key="+question.getId();
%><tr>
		<td><%=question.getNumber() %></td>
		<td><%=question.getApproved() %></td>
		<td><%=question.getTriviaQuestion() %></td>
		<td><img src="<%= baseUrl%><%= question.getQuestionImageUrl() %>" /></td>
		<td><a href="<%=delUrl %>">Delete</a> | <a href="<%=approveUrl %>">Approve</a></td>
	</tr>
	<%
    }
}
pm.close();
%>
</table>
</body>
</html>