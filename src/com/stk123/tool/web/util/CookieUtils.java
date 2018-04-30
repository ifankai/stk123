package com.stk123.tool.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stk123.web.StkConstant;

public class CookieUtils {
	
	public static void addCookie(HttpServletResponse response, String name, String value, int age) {
		Cookie cookies = new Cookie(name, value);
		cookies.setPath(String.valueOf(StkConstant.MARK_SLASH));
		//cookies.setMaxAge(-1);//����cookie�����೤���ɾ�������0����˵������ɾ��������Ǹ����ͱ�����������ر�ʱ�Զ�ɾ����
		cookies.setMaxAge(age);
		response.addCookie(cookies);
	}

	public static String getCookieValue(HttpServletRequest request,String cookieName) {
		if (cookieName != null) {
			Cookie cookie = getCookie(request, cookieName);
			if (cookie != null) {
				return cookie.getValue();
			}
		}
		return StkConstant.MARK_BLANK_SPACE;
	}

	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		try {
			if (cookies != null && cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					cookie = cookies[i];
					if (cookie.getName().equals(cookieName)) {
						return cookie;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cookie;
	}

	public static boolean deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		if (cookieName != null) {
			Cookie cookie = getCookie(request, cookieName);
			if (cookie != null) {
				cookie.setMaxAge(0);// ���0����˵������ɾ��
				cookie.setPath(String.valueOf(StkConstant.MARK_SLASH));// ��Ҫ©��
				response.addCookie(cookie);
				return true;
			}
		}
		return false;
	}
}
