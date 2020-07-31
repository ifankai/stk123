package com.stk123.tool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.HtmlEmail;

import com.stk123.model.quartz.job.IndexRealTimeJob;
import com.stk123.task.InitialKLine;
import com.stk123.task.StkUtils;
import com.stk123.web.StkConstant;


public class EmailUtils {

	public static final String IMPORTANT = "★【重要】";
	public static boolean SEND_MAIL = true;

	public static final String ADDRESS_126 = "ifankai@126.com";
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		/*HtmlEmail email = new HtmlEmail();
        email.setHostName("mail.ebaotech.com");
        email.addTo("kevin.fan@ebaotech.com");
        email.setFrom("robot@ebaotech.com","Robot");
        email.setSubject("test-test");
        email.setCharset("gb2312");
        email.setHtmlMsg("<table width=\"825\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"nr\"><a href=\"http://www.hexun.com/\" target=\"_blank\">首页</a>-<a href=\"http://news.hexun.com/\" target=\"_blank\">新闻</a>-<a href=\"http://stock.hexun.com/\" target=\"_blank\">股票</a>-<a href=\"http://funds.hexun.com/\" target=\"_blank\">基金</a>-<a href=\"http://futures.hexun.com/\" target=\"_blank\">期货</a>-<a href=\"http://qizhi.hexun.com/\" target=\"_blank\">股指期货</a>-<a href=\"http://gold.hexun.com/\" target=\"_blank\">黄金</a>-<a href=\"http://forex.hexun.com/\" target=\"_blank\">外汇</a>-<a href=\"http://bond.hexun.com/\" target=\"_blank\">债券</a>-<a href=\"http://money.hexun.com/\" target=\"_blank\">理财</a>-<a href=\"http://bank.hexun.com/\" target=\"_blank\">银行</a>-<a href=\"http://insurance.hexun.com/\" target=\"_blank\">保险</a>-<a href=\"http://house.hexun.com/\" target=\"_blank\">房产</a>-<a href=\"http://auto.hexun.com/\" target=\"_blank\">汽车</a>-<a href=\"http://tech.hexun.com/\" target=\"_blank\">科技</a>-<a href=\"http://guba.hexun.com/\" target=\"_blank\">股吧</a>-<a href=\"http://salon.hexun.com/\" target=\"_blank\">沙龙</a>-<a href=\"http://bbs.wayup.hexun.com/\" target=\"_blank\">论坛</a>-<a href=\"http://blog.hexun.com/\" target=\"_blank\">博客</a>-<a href=\"http://tv.hexun.com/\" target=\"_blank\">和讯视点</a>-<a href=\"http://lc.hexun.com/\" target=\"_blank\">软件服务</a></td></tr></table>");
        email.send();*/

		//EmailUtils.send( "①test<span class=\"red\">red</span>", "我是随机数<table width=\"825\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"nr\"><a href=\"http://www.hexun.com/\" target=\"_blank\">首页</a>-<a href=\"http://news.hexun.com/\" target=\"_blank\">新闻</a>-<a href=\"http://stock.hexun.com/\" target=\"_blank\">股票</a>-<a href=\"http://funds.hexun.com/\" target=\"_blank\">基金</a>-<a href=\"http://futures.hexun.com/\" target=\"_blank\">期货</a>-<a href=\"http://qizhi.hexun.com/\" target=\"_blank\">股指期货</a>-<a href=\"http://gold.hexun.com/\" target=\"_blank\">黄金</a>-<a href=\"http://forex.hexun.com/\" target=\"_blank\">外汇</a>-<a href=\"http://bond.hexun.com/\" target=\"_blank\">债券</a>-<a href=\"http://money.hexun.com/\" target=\"_blank\">理财</a>-<a href=\"http://bank.hexun.com/\" target=\"_blank\">银行</a>-<a href=\"http://insurance.hexun.com/\" target=\"_blank\">保险</a>-<a href=\"http://house.hexun.com/\" target=\"_blank\">房产</a>-<a href=\"http://auto.hexun.com/\" target=\"_blank\">汽车</a>-<a href=\"http://tech.hexun.com/\" target=\"_blank\">科技</a>-<a href=\"http://guba.hexun.com/\" target=\"_blank\">股吧</a>-<a href=\"http://salon.hexun.com/\" target=\"_blank\">沙龙</a>-<a href=\"http://bbs.wayup.hexun.com/\" target=\"_blank\">论坛</a>-<a href=\"http://blog.hexun.com/\" target=\"_blank\">博客</a>-<a href=\"http://tv.hexun.com/\" target=\"_blank\">和讯视点</a>-<a href=\"http://lc.hexun.com/\" target=\"_blank\">软件服务</a></td></tr></table>");
		//writeFile("测试", "我是随机数<table width=\"825\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"nr\"><a href=\"http://www.hexun.com/\" target=\"_blank\">首页</a>-<a href=\"http://news.hexun.com/\" target=\"_blank\">新闻</a>-<a href=\"http://stock.hexun.com/\" target=\"_blank\">股票</a>-<a href=\"http://funds.hexun.com/\" target=\"_blank\">基金</a>-<a href=\"http://futures.hexun.com/\" target=\"_blank\">期货</a>-<a href=\"http://qizhi.hexun.com/\" target=\"_blank\">股指期货</a>-<a href=\"http://gold.hexun.com/\" target=\"_blank\">黄金</a>-<a href=\"http://forex.hexun.com/\" target=\"_blank\">外汇</a>-<a href=\"http://bond.hexun.com/\" target=\"_blank\">债券</a>-<a href=\"http://money.hexun.com/\" target=\"_blank\">理财</a>-<a href=\"http://bank.hexun.com/\" target=\"_blank\">银行</a>-<a href=\"http://insurance.hexun.com/\" target=\"_blank\">保险</a>-<a href=\"http://house.hexun.com/\" target=\"_blank\">房产</a>-<a href=\"http://auto.hexun.com/\" target=\"_blank\">汽车</a>-<a href=\"http://tech.hexun.com/\" target=\"_blank\">科技</a>-<a href=\"http://guba.hexun.com/\" target=\"_blank\">股吧</a>-<a href=\"http://salon.hexun.com/\" target=\"_blank\">沙龙</a>-<a href=\"http://bbs.wayup.hexun.com/\" target=\"_blank\">论坛</a>-<a href=\"http://blog.hexun.com/\" target=\"_blank\">博客</a>-<a href=\"http://tv.hexun.com/\" target=\"_blank\">和讯视点</a>-<a href=\"http://lc.hexun.com/\" target=\"_blank\">软件服务</a></td></tr></table>");
		//send("重要3113","我是随机数<table width=\"825\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"nr\"><a href=\"http://www.hexun.com/\" target=\"_blank\">首页</a>-<a href=\"http://news.hexun.com/\" target=\"_blank\">新闻</a>-<a href=\"http://stock.hexun.com/\" target=\"_blank\">股票</a>-<a href=\"http://funds.hexun.com/\" target=\"_blank\">基金</a>-<a href=\"http://futures.hexun.com/\" target=\"_blank\">期货</a>-<a href=\"http://qizhi.hexun.com/\" target=\"_blank\">股指期货</a>-<a href=\"http://gold.hexun.com/\" target=\"_blank\">黄金</a>-<a href=\"http://forex.hexun.com/\" target=\"_blank\">外汇</a>-<a href=\"http://bond.hexun.com/\" target=\"_blank\">债券</a>-<a href=\"http://money.hexun.com/\" target=\"_blank\">理财</a>-<a href=\"http://bank.hexun.com/\" target=\"_blank\">银行</a>-<a href=\"http://insurance.hexun.com/\" target=\"_blank\">保险</a>-<a href=\"http://house.hexun.com/\" target=\"_blank\">房产</a>-<a href=\"http://auto.hexun.com/\" target=\"_blank\">汽车</a>-<a href=\"http://tech.hexun.com/\" target=\"_blank\">科技</a>-<a href=\"http://guba.hexun.com/\" target=\"_blank\">股吧</a>-<a href=\"http://salon.hexun.com/\" target=\"_blank\">沙龙</a>-<a href=\"http://bbs.wayup.hexun.com/\" target=\"_blank\">论坛</a>-<a href=\"http://blog.hexun.com/\" target=\"_blank\">博客</a>-<a href=\"http://tv.hexun.com/\" target=\"_blank\">和讯视点</a>-<a href=\"http://lc.hexun.com/\" target=\"_blank\">软件服务</a></td></tr></table>");
		sendMailByQQ("ifankai@126.com,184032887@qq.com","重要3113","我是随机数<table width=\"825\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"nr\"><a href=\"http://www.hexun.com/\" target=\"_blank\">首页</a>-<a href=\"http://news.hexun.com/\" target=\"_blank\">新闻</a>-<a href=\"http://stock.hexun.com/\" target=\"_blank\">股票</a>-<a href=\"http://funds.hexun.com/\" target=\"_blank\">基金</a>-<a href=\"http://futures.hexun.com/\" target=\"_blank\">期货</a>-<a href=\"http://qizhi.hexun.com/\" target=\"_blank\">股指期货</a>-<a href=\"http://gold.hexun.com/\" target=\"_blank\">黄金</a>-<a href=\"http://forex.hexun.com/\" target=\"_blank\">外汇</a>-<a href=\"http://bond.hexun.com/\" target=\"_blank\">债券</a>-<a href=\"http://money.hexun.com/\" target=\"_blank\">理财</a>-<a href=\"http://bank.hexun.com/\" target=\"_blank\">银行</a>-<a href=\"http://insurance.hexun.com/\" target=\"_blank\">保险</a>-<a href=\"http://house.hexun.com/\" target=\"_blank\">房产</a>-<a href=\"http://auto.hexun.com/\" target=\"_blank\">汽车</a>-<a href=\"http://tech.hexun.com/\" target=\"_blank\">科技</a>-<a href=\"http://guba.hexun.com/\" target=\"_blank\">股吧</a>-<a href=\"http://salon.hexun.com/\" target=\"_blank\">沙龙</a>-<a href=\"http://bbs.wayup.hexun.com/\" target=\"_blank\">论坛</a>-<a href=\"http://blog.hexun.com/\" target=\"_blank\">博客</a>-<a href=\"http://tv.hexun.com/\" target=\"_blank\">和讯视点</a>-<a href=\"http://lc.hexun.com/\" target=\"_blank\">软件服务</a></td></tr></table>");
		/*sendMailByQQ("ifankai@126.com,184032887@qq.com","重要3","我是随机数<table width=\"825\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"nr\"><a href=\"http://www.hexun.com/\" target=\"_blank\">首页</a>-<a href=\"http://news.hexun.com/\" target=\"_blank\">新闻</a>-<a href=\"http://stock.hexun.com/\" target=\"_blank\">股票</a>-<a href=\"http://funds.hexun.com/\" target=\"_blank\">基金</a>-<a href=\"http://futures.hexun.com/\" target=\"_blank\">期货</a>-<a href=\"http://qizhi.hexun.com/\" target=\"_blank\">股指期货</a>-<a href=\"http://gold.hexun.com/\" target=\"_blank\">黄金</a>-<a href=\"http://forex.hexun.com/\" target=\"_blank\">外汇</a>-<a href=\"http://bond.hexun.com/\" target=\"_blank\">债券</a>-<a href=\"http://money.hexun.com/\" target=\"_blank\">理财</a>-<a href=\"http://bank.hexun.com/\" target=\"_blank\">银行</a>-<a href=\"http://insurance.hexun.com/\" target=\"_blank\">保险</a>-<a href=\"http://house.hexun.com/\" target=\"_blank\">房产</a>-<a href=\"http://auto.hexun.com/\" target=\"_blank\">汽车</a>-<a href=\"http://tech.hexun.com/\" target=\"_blank\">科技</a>-<a href=\"http://guba.hexun.com/\" target=\"_blank\">股吧</a>-<a href=\"http://salon.hexun.com/\" target=\"_blank\">沙龙</a>-<a href=\"http://bbs.wayup.hexun.com/\" target=\"_blank\">论坛</a>-<a href=\"http://blog.hexun.com/\" target=\"_blank\">博客</a>-<a href=\"http://tv.hexun.com/\" target=\"_blank\">和讯视点</a>-<a href=\"http://lc.hexun.com/\" target=\"_blank\">软件服务</a></td></tr></table>"
				+ "<img src=\"data:image/png;base64,"
				+ "iVBORw0KGgoAAAANSUhEUgAAAG8AAAA6CAYAAAC+hgckAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA2ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDowNDgwMTE3NDA3MjA2ODExOTdBNUJDQUY2RkQ0Q0Y0RiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo0NzUyMTYxMzY3ODYxMUUyQjAwNzlGMEJBNzEwQjgwQiIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo0NzUyMTYxMjY3ODYxMUUyQjAwNzlGMEJBNzEwQjgwQiIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ1M1IE1hY2ludG9zaCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjAyODAxMTc0MDcyMDY4MTE4OEM2ODE0NDFEOTQzMkNBIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjA0ODAxMTc0MDcyMDY4MTE5N0E1QkNBRjZGRDRDRjRGIi8+IDwvcmRmOkRlc2NyaXB0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\"/>");
	*/}

	public static void send(String host,List<String> to,String fromEmail,String fromName,String subject,String msg) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setCharset("gb2312");
        email.setHostName(host);
        for(String t:to){
        	email.addTo(t);
        }
        email.setFrom(fromEmail,fromName);
        email.setSubject(subject);
        email.setHtmlMsg(msg);
        email.send();
	}

	public static void send(String title,String msg) {
		//writeFile(title,msg);
		if(SEND_MAIL){
			if(StringUtils.contains(msg, "http get try n times error")){
				return;
			}
			if(title.startsWith(EmailUtils.IMPORTANT)){
				sendMailBy126("184032887@qq.com,"+ADDRESS_126,title,msg);
			}else{
				sendMailBy126(ADDRESS_126,title,msg);
			}
		}
	}

	public static void sendAndReport(String title,String msg) {
		if(!title.startsWith(EmailUtils.IMPORTANT)){
			writeFile(title,msg);
		}
		send(title,msg);
	}

	public static void sendImport(String title,String msg) {
		EmailUtils.send(EmailUtils.IMPORTANT + title, msg);
	}

	public static void sendImport2(String title,String msg) {

	}

	public static void send(String title,Exception e) {
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		send(title,aWriter.getBuffer().toString());
	}

	public static void sendException(String title, Exception e){
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		EmailUtils.send(title, aWriter.getBuffer().toString());
	}

	/*public static void sendAndHtml(String title,String msg) throws Exception {
		sendMail("ifankai@gmail.com",title,msg);
		writeFile(title,msg);
	}*/

	private static void writeFile(String title, String msg) {
		try {
			/*"d:\\share\\workspace\\stk123\\dailyreport\\"*/
			String path = StkConstant.PATH_DAILYREPORT_EMAIL_BACKUP +StkUtils.getToday()+"\\";
			String indexFile = path + "index.html";
			String linkFile = path + "link.html";
			File file = new File(path);
			if(!file.exists()){
				file.mkdir();
				File index = new File(StkConstant.PATH_DAILYREPORT_EMAIL_BACKUP + "index.html");
				FileUtils.copyFileToDirectory(index, file);
				File link = new File(StkConstant.PATH_DAILYREPORT_EMAIL_BACKUP + "link.html");
				FileUtils.copyFileToDirectory(link, file);
			}
			title = title.replaceAll("[\\?/:*|<>\"]", "_");
			String t = title + ".html";
			file = new File(file, t);
			OutputStream os;

			os = new FileOutputStream(file, false);
			IOUtils.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head>\n", os);
			IOUtils.write("<h3>"+title+"</h3>", os, "UTF-8");
			IOUtils.write(msg, os, "UTF-8");
			IOUtils.write("\n</html>",os);

			String linkPage = FileUtils.readFileToString(new File(linkFile));
			if(!StringUtils.contains(linkPage, title)){
				linkPage = StringUtils.replace(linkPage, "${pageUrl}", "<li><a href=\""+t+"\" target=\"a2\">"+title+"</a></li>${pageUrl}");
				FileUtils.writeStringToFile(new File(linkFile), linkPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void writeReport(String title, String msg){

	}

	public static void send(String to,String subject,String text) {
		sendMailByAliyun(to,subject,text);
	}

	public static void sendByGmail(String[] tos,String subject,String text) {
        Properties props = new Properties();
        //props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.host", "173.194.193.108");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        Session ssn = Session.getInstance(props, null);
        MimeMessage message = new MimeMessage(ssn);
        try{
	        InternetAddress fromAddress = new InternetAddress("ifankai@gmail.com");
	        message.setFrom(fromAddress);
	        for(String to : tos){
	        	InternetAddress toAddress = new InternetAddress(to);
	        	 message.addRecipient(Message.RecipientType.TO, toAddress);
	        }
	        message.setSubject(subject);
	        message.setText(text);
	        message.setContent(text, "text/html;charset=utf-8");
	        Transport transport = ssn.getTransport("smtp");
	        //transport.connect("smtp.gmail.com", "ifankai", "181302kevin");
	        transport.connect("209.85.201.111", "ifankai", "181302kevin");
	        transport.sendMessage(message, message
	                .getRecipients(Message.RecipientType.TO));
	        transport.close();
        }catch(Exception e){
        	e.printStackTrace();
        	//ExceptionUtils.insertLog(conn, e);
        }
    }

	public static void sendMailByQQ(String to,String subject,String content){
	      // 获取系统属性
	      Properties properties = System.getProperties();
	      // 设置邮件服务器
	      properties.setProperty("mail.smtp.host", "smtp.qq.com");
	      properties.put("mail.smtp.port", "465");
	      properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	      properties.setProperty("mail.smtp.socketFactory.fallback", "false");
	      properties.setProperty("mail.smtp.socketFactory.port", "465");
	      properties.put("mail.smtp.auth", "true");
	      // 获取默认session对象
	      Session session = Session.getDefaultInstance(properties,new Authenticator(){
		    public PasswordAuthentication getPasswordAuthentication()
		    {
		     return new PasswordAuthentication("184032887@qq.com", "xynxpeaokntmbiih"); //发件人邮件用户名、密码
		    }
		   });
	      MimeMessage mailMessage = new MimeMessage(session);
	        try{
		        mailMessage.setFrom(new InternetAddress("184032887@qq.com"));
		        // Message.RecipientType.TO属性表示接收者的类型为TO
		        mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		        mailMessage.setSubject(subject, "UTF-8");
		        mailMessage.setSentDate(new Date());
		        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		        Multipart mainPart = new MimeMultipart();
		        // 创建一个包含HTML内容的MimeBodyPart
		        BodyPart html = new MimeBodyPart();
		        html.setContent(content, "text/html; charset=utf-8");
		        mainPart.addBodyPart(html);
		        mailMessage.setContent(mainPart);
		        Transport.send(mailMessage);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	}

	public static void sendMailByAliyun(String to,String subject,String content) {
		//String to = "ifankai@aliyun.com";
        //String subject = "subject";//邮件主题
        //String content = "content";//邮件内容
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.aliyun.com");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator(){
		    public PasswordAuthentication getPasswordAuthentication()
		    {
		     return new PasswordAuthentication("ifankai@aliyun.com", "181302kevin"); //发件人邮件用户名、密码
		    }
		   };
        javax.mail.Session sendMailSession = javax.mail.Session.getDefaultInstance(properties, authenticator);
        MimeMessage mailMessage = new MimeMessage(sendMailSession);
        try{
	        mailMessage.setFrom(new InternetAddress("ifankai@aliyun.com"));
	        // Message.RecipientType.TO属性表示接收者的类型为TO
	        mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	        mailMessage.setSubject(subject, "UTF-8");
	        mailMessage.setSentDate(new Date());
	        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
	        Multipart mainPart = new MimeMultipart();
	        // 创建一个包含HTML内容的MimeBodyPart
	        BodyPart html = new MimeBodyPart();
	        html.setContent(content, "text/html; charset=utf-8");
	        mainPart.addBodyPart(html);
	        mailMessage.setContent(mainPart);
	        Transport.send(mailMessage);
        }catch(Exception e){
        	e.printStackTrace();
        }
	}

	public static void sendMailBy126(String to,String subject,String content) {
		//String to = "ifankai@aliyun.com";
        //String subject = "subject";//邮件主题
        //String content = "content";//邮件内容
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.126.com");
        properties.put("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    properties.setProperty("mail.smtp.socketFactory.fallback", "false");
	    properties.setProperty("mail.smtp.socketFactory.port", "465");
	    properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator(){
		    public PasswordAuthentication getPasswordAuthentication()
		    {
		     return new PasswordAuthentication(ADDRESS_126, "181302fankai"); //密码:181302kevin
		    }
		   };
        javax.mail.Session sendMailSession = javax.mail.Session.getDefaultInstance(properties, authenticator);
        MimeMessage mailMessage = new MimeMessage(sendMailSession);
        try{
	        mailMessage.setFrom(new InternetAddress(ADDRESS_126));
	        // Message.RecipientType.TO属性表示接收者的类型为TO
	        mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	        mailMessage.setSubject(subject, "UTF-8");
	        mailMessage.setSentDate(new Date());
	        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
	        Multipart mainPart = new MimeMultipart();
	        // 创建一个包含HTML内容的MimeBodyPart
	        BodyPart html = new MimeBodyPart();
	        html.setContent(content, "text/html; charset=utf-8");
	        mainPart.addBodyPart(html);
	        mailMessage.setContent(mainPart);
	        Transport.send(mailMessage);
        }catch(Exception e){
        	e.printStackTrace();
        	/**
        	 * re-try by sendMailByAliyun
        	 */
        	sendMailBy139(to, subject, content);
        }
	}

	public static void sendMailBy139(String to,String subject,String content) {
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.139.com");
        properties.put("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    properties.setProperty("mail.smtp.socketFactory.fallback", "false");
	    properties.setProperty("mail.smtp.socketFactory.port", "465");
	    properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator(){
		    public PasswordAuthentication getPasswordAuthentication()
		    {
		     return new PasswordAuthentication("13816923931@139.com", "181302kevin"); //发件人邮件用户名、密码
		    }
		   };
        javax.mail.Session sendMailSession = javax.mail.Session.getDefaultInstance(properties, authenticator);
        MimeMessage mailMessage = new MimeMessage(sendMailSession);
        try{
	        mailMessage.setFrom(new InternetAddress("13816923931@139.com"));
	        // Message.RecipientType.TO属性表示接收者的类型为TO
	        mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	        mailMessage.setSubject(subject, "UTF-8");
	        mailMessage.setSentDate(new Date());
	        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
	        Multipart mainPart = new MimeMultipart();
	        // 创建一个包含HTML内容的MimeBodyPart
	        BodyPart html = new MimeBodyPart();
	        html.setContent(content, "text/html; charset=utf-8");
	        mainPart.addBodyPart(html);
	        mailMessage.setContent(mainPart);
	        Transport.send(mailMessage);
        }catch(Exception e){
        	e.printStackTrace();
        }
	}

}
