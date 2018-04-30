<html>
<head>
<title>JWhich</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>

<body>
<form name="form1" method="post" action="">
  ClassName:
  <input type="text" name="className">
  <input type="submit" name="Submit" value="Submit">
</form>
<%
	String className = request.getParameter("className");
	if (className != null && className.length() > 0)
	{
        if (!className.startsWith("/"))
        {
            className = "/" + className;
        }
        className = className.replace('.', '/');
        className = className + ".class";

        java.net.URL classUrl = this.getClass().getResource(className);

        if (classUrl != null)
        {
            out.println("\nClass '" + className + "' found in \n'" + classUrl.getFile() + "'");
        }
        else
        {
            out.println("\nClass '" + className + "' not found!");
        }
	}
%>
</body>
</html>
