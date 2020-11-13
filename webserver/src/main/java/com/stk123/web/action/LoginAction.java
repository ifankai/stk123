package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.stk123.model.User;
import com.stk123.common.util.JsonUtils;
import com.stk123.web.core.util.CookieUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.context.StkContext;

public class LoginAction {
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		HttpServletResponse response = sc.getResponse();
		Connection conn = StkContext.getConnection();
		String username = request.getParameter(CommonConstant.PARAMETER_USERNAME);
		String password = request.getParameter(CommonConstant.PARAMETER_PASSWORD);
		String autologin = request.getParameter(CommonConstant.PARAMETER_AUTOLOGIN);
		User user = login(conn, request,response, username, password);
		if(user != null){
			setCookieAge(response, username, password);
			if(CommonConstant.NUMBER_ONE.equals(autologin)){
				CookieUtils.addCookie(response, CommonConstant.PARAMETER_AUTOLOGIN, autologin, Integer.MAX_VALUE);
			}else{
				CookieUtils.deleteCookie(request, response, CommonConstant.PARAMETER_AUTOLOGIN);
			}
            return CommonConstant.ACTION_SUCC;
		}else{
			//用户名密码错误
			sc.put(CommonConstant.ATTRIBUTE_LOGIN_ERROR, true);
			return CommonConstant.ACTION_FAIL;
		}
	}
	
	public static User login(Connection conn, HttpServletRequest request, HttpServletResponse response, String userName, String password) throws Exception {
		User user = User.loadByEmail(conn, userName);
		if(user != null && user.getStkUser().getPassword().equals(password)){
			HttpSession session = request.getSession();
			session.setAttribute(CommonConstant.SESSION_CURRENT_USER, user);
            return user;
		}
		return null;
	}
	
	public String logout() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		request.getSession().invalidate();
		return CommonConstant.ACTION_SUCC;
	}
	
	private final static int age = 30*24*60*60;
	
	public static void setCookieAge(HttpServletResponse response,String email,String password){
		CookieUtils.addCookie(response, CommonConstant.PARAMETER_USERNAME, email, age);
		CookieUtils.addCookie(response, CommonConstant.PARAMETER_PASSWORD, password, age);
	}
	
	public void register() throws Exception {
		System.out.println("register................");
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = StkContext.getConnection();
		String nickname = request.getParameter(CommonConstant.PARAMETER_NICKNAME);
		String email = request.getParameter(CommonConstant.PARAMETER_EMAIL);
		String pw1 = request.getParameter(CommonConstant.PARAMETER_PW1);
		String pw2 = request.getParameter(CommonConstant.PARAMETER_PW2);
		User user = User.loadByNameOrEmail(conn, nickname, email);
		List errors = new ArrayList();
		if(user != null){
			if(user.getStkUser().getNickname().equals(nickname)){
				errors.add("101");
			}
			if(user.getStkUser().getEmail().equalsIgnoreCase(email)){
				errors.add("102");
			}
			String json = JsonUtils.getJsonString4JavaPOJO(errors);
			sc.setResponse(json);
		}else{
			user = User.create(conn, nickname, email.toLowerCase(), pw1);
			if(user != null){
				sc.setResponse(CommonConstant.NUMBER_ONE);
			}else{
				sc.setResponse("-1");
			}
		}
	}
}
