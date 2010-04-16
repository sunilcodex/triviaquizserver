<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Enter Suggestion</title>
</head>
<body>

Suggest a Question
<form action="/suggest" method="post" enctype="multipart/form-data">
PlayerId: <input type="text" name="playerId" /><br />
Question: <input type="text" name="question" /><br />
Image: <input type="file" name="questionImage" /><br />
<input type="submit" name="submit" value="Suggest" /></form>
Tip: If you don't know your playerId, you probably shouldn't be using this form. :)
</body>
</html>