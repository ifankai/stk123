package com.stk123.web.action;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.stk123.tool.web.ActionContext;

public class WeixinAction {
	
	public String perform() throws Exception {
		ActionContext ac = ActionContext.getContext();
		HttpServletRequest request = ac.getRequest();
		
		Enumeration<String> en =  request.getParameterNames();
		while(en.hasMoreElements()){
			String name = en.nextElement();
			String value = request.getParameter(name);
			System.out.println(name + "=" +value);
		}
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		List list = new ArrayList();
		list.add("stk123");
		list.add(timestamp);
		list.add(nonce);
		
		Collections.sort(list);
		
		String s = StringUtils.join(list,"");
		String hashCode = hash(s, "SHA1");
		System.out.println("hashCode="+hashCode);
		
		ac.setResponse(echostr);
		return null;//"/wx/test.html";
	}
	
	public static String hash(String string, String algorithm) {
        if (string.isEmpty()) {
            return "";
        }
        MessageDigest hash = null;
        try {
            hash = MessageDigest.getInstance(algorithm);
            byte[] bytes = hash.digest(string.getBytes("UTF-8"));
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
