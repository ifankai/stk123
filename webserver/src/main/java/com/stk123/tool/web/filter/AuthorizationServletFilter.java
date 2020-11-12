package com.stk123.tool.web.filter;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.stk123.model.User;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.web.util.CookieUtils;
import com.stk123.StkConstant;
import com.stk123.web.action.LoginAction;



public class AuthorizationServletFilter implements Filter {
	
	private final static String PATH_INDEX = "/index";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
        	if(!StkUtils.isDev()){
        		filterChain.doFilter(servletRequest, servletResponse);
        		return;
        	}
            //ActionHelper.initializeOasisUser(request);
        	String requestURI = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/", request.getRequestURI().length()));   
            //登陆页就不需要进行验证了   
            if(!PATH_INDEX.equals(requestURI)){   
                HttpSession session = request.getSession(false);   
                if(session == null || session.getAttribute(StkConstant.SESSION_CURRENT_USER) == null ){
                	String autologin = CookieUtils.getCookieValue(request, StkConstant.PARAMETER_AUTOLOGIN);
                	if("1".equals(autologin) && autoLogin(request,response)){
                		/*String username = CookieUtils.getCookieValue(request, "username");  
                        String password = CookieUtils.getCookieValue(request, "password");  
                		LoginAction.setCookieAge(response, username, password); */ 
	                }else{
	                	response.sendRedirect(request.getContextPath());      
	                    return;
	                }
                }
            }
        }catch (Exception e) {
        	e.printStackTrace();
        	response.sendError(500);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);

	}
	
	public boolean autoLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = CookieUtils.getCookieValue(request, StkConstant.PARAMETER_USERNAME);  
        String password = CookieUtils.getCookieValue(request, StkConstant.PARAMETER_PASSWORD);;  
        Connection conn = Pool.getPool().getConnection();
        User user = LoginAction.login(conn, request, response, username, password);
        Pool.getPool().free(conn);
        if(user == null)return false;
        return true;
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
