package com.stk123.tool.util;


import java.util.Properties;

import javax.mail.Message;     
import javax.mail.Session;     
import javax.mail.Transport;     
import javax.mail.internet.InternetAddress;     
import javax.mail.internet.MimeMessage;     
     
public class SendMail {     
     
    private static final String MAIL_USER = "ifankai";   //�ʼ���������¼�û���     
    private static final String MAIL_PASSWORD = "181302kevin";   //�ʼ���������¼����     
    private static final String MAIL_FROM = "ifankai@sina.com";  //�����ʼ���ַ     
         
         
    /**   
     * @param mail      �ռ���   
     * @param subject   ����   
     * @param text      ����   
     */     
    public static void sendMail(String mail,String subject,String text) {     
     
        try {     
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
            InternetAddress fromAddress = new InternetAddress(MAIL_FROM);     
            message.setFrom(fromAddress);     
            InternetAddress toAddress = new InternetAddress(mail);     
            message.addRecipient(Message.RecipientType.TO, toAddress);     
            message.setSubject(subject);     
            message.setText(text);     
            Transport transport = ssn.getTransport("smtp");     
            transport.connect("smtp.gmail.com", MAIL_USER, MAIL_PASSWORD);     
            transport.sendMessage(message, message     
                    .getRecipients(Message.RecipientType.TO));     
            transport.close();     
            System.out.println("����ʼ��ѷ���");     
        } catch (Exception m) {     
            System.out.println(m.toString());     
        }     
    }     
         
    /**   
     * @param args   
     */     
    public static void main(String[] args) {     
    	sendMail("kevin.fan@ebaotech.com", "����", "���������");     
    }     
     
} 
