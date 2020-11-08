<%@ page isErrorPage="true" %>
<html>
<body>
	<h1> The exception <%= exception %> tells me you
	     made a wrong choice. </h1>
	<%
	exception.printStackTrace();
	%>
</body>
</html>