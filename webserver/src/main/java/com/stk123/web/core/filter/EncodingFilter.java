package com.stk123.web.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.stk123.StkConstant;

public class EncodingFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(StkConstant.ENCODING_UTF_8); 
		chain.doFilter(request, response);  
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}
