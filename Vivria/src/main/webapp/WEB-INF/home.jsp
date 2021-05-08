<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%
	String requestUrl = (request.getRequestURL() != null) ? request.getRequestURL().toString() : "";

	if (requestUrl != null && requestUrl.indexOf("localhost") > -1) {
		response.sendRedirect("/game/vivria/");
	} else {
		response.sendRedirect("http://ddoc.kr");
	}
%>
<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="width=device-width, user-scalable=no" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>홈페이지</title>
	<style>
		body {
			font-size: 1em;
		}
	</style>
</head>
<body>
	<h1>홈페이지</h1>
	<a href="/game/vivria/">비브리아 게임</a>
</body>
</html>
