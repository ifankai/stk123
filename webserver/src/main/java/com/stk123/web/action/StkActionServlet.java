package com.stk123.web.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stk123.util.ServiceUtils;
import com.stk123.web.ik.WebIKUtils;
import com.stk123.web.core.ActionServlet;


public class StkActionServlet extends ActionServlet {
	
	public void init() throws ServletException {
    	try {
    		super.init();
			WebIKUtils.init();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("stock init error.");
		}
    }
	
	protected void process(HttpServletRequest request, HttpServletResponse response)
	        throws IOException, ServletException {
		super.process(request, response);
	}

}
