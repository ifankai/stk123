<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

String s = request.getParameter("class_name");
out.println(new JWhich().which(s));
%><%!
class JWhich {
	public String which(String resourceName)  throws java.io.IOException {
		if(resourceName==null)return "";
		resourceName = resourceName.replace('.', '/');
		resourceName = resourceName + ".class";

		
	    ClassLoader cld = new JWhich().getClass().getClassLoader();
	    java.util.Enumeration en = cld.getResources(resourceName);
	    StringBuffer buf = new StringBuffer();
	    buf.append(resourceName);
	    buf.append(": ");
	    
	    if (en == null || (!en.hasMoreElements()))
	    {
	       buf.append("not found");
	    }
	    else
	    {
	       boolean firstLoc = true;
	       while (en.hasMoreElements())
	       {
	          if (!firstLoc)
	          {
	             buf.append(", ");
	          }
	          
	          java.net.URL url = (java.net.URL) (en.nextElement());
	          buf.append(url.toString());
	          firstLoc = false;
	       }
	    }
	    
	    return buf.toString();

	}
}

%>
<html>
<script>
	function doSubmit(){	    
		document.form1.submit();
	}	
</script>


<body bgcolor="#808080" leftmargin="0" topmargin="10">
<form action="" method="post" name="form1">
<input type="text" name="class_name" value="Submit" onclick="doSubmit()" />
</form>
</body>


</html>