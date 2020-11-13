package com.stk123.web.action;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import com.stk123.web.context.StkContext;


public class AjaxAction {
	
	public void urlEncode2Gb2312() throws IOException{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String word = request.getParameter("word");
		sc.setResponse(URLEncoder.encode(word, "gb2312"));
	}
	
}
