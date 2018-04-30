package com.stk123.tool.web.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

import com.stk123.web.StkConstant;

public class RequestUtils {
	
	public static void populate(Object bean, HttpServletRequest request)
			throws ServletException {
		// Build a list of relevant request parameters from this request
		HashMap properties = new HashMap();
		// Iterator of parameter names
		Enumeration names = null;
		// Map for multipart parameters
		Map multipartParameters = null;

		String contentType = request.getContentType();
		String method = request.getMethod();
		boolean isMultipart = false;

		if ((contentType != null)
				&& (contentType.startsWith("multipart/form-data"))
				&& (method.equalsIgnoreCase("POST"))) {

			throw new ServletException("not support multipart/form-data yet.");
		}

		if (!isMultipart) {
			names = request.getParameterNames();			
		}

		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			//System.out.println("parameter="+name+",v="+request.getParameter(name));
			Object parameterValue = null;
			if (isMultipart) {
				parameterValue = multipartParameters.get(name);
			} else {
				parameterValue = request.getParameterValues(name);
			}
			
			properties.put(name, parameterValue);
		}

		// Set the corresponding properties of our bean
		try {					
			BeanUtils.populate(bean, properties);
		} catch (Exception e) {
			throw new ServletException("BeanUtils.populate", e);
		}

	}
	
	public static <T> T requst2Bean(HttpServletRequest request, Class<T> bean) {  
        T t = null;  
        try {  
            t = bean.newInstance();  
            Enumeration parameterNames = request.getParameterNames();               
            while (parameterNames.hasMoreElements()) {  
                String name = (String) parameterNames.nextElement();  
                String value = request.getParameter(name);  
  
                BeanUtils.setProperty(t, name, value);//使用BeanUtils来设置对象属性的值  
  
            }  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return t;  
  
    } 

	public static Object applicationInstance(String className)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {

		return (applicationClass(className).newInstance());

	}

	public static Class applicationClass(String className)
			throws ClassNotFoundException {

		// Look up the class loader to be used
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = RequestUtils.class.getClassLoader();
		}

		// Attempt to load the specified class
		return (classLoader.loadClass(className));

	}

	/**
	 * @param request
	 *            request
	 * @throws UnsupportedEncodingException
	 */
	public static void setCharacterEncoding(HttpServletRequest request)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding(StkConstant.ENCODING_UTF_8);
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应字符串值
	 */
	public static String getString(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		return value != null ? value : StkConstant.MARK_EMPTY;
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取属性对应字符串值
	 */
	public static String getStrAttribute(HttpServletRequest request, String paramName) {
		String value = (String) request.getAttribute(paramName);
		return value != null ? value : StkConstant.MARK_EMPTY;
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应数组值
	 */
	public static String[] getArray(HttpServletRequest request, String paramName) {
		return request.getParameterValues(paramName);
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应Byte值
	 */
	public static byte getByte(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0)
			return 0;
		else
			return Byte.parseByte(value);
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应整型值
	 */
	public static int getInt(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0)
			return 0;
		else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应整型值
	 */
	public static int getIntAttribute(HttpServletRequest request, String paramName) {
		String value = request.getAttribute(paramName).toString();
		if (value == null || value.length() == 0)
			return 0;
		else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应长整型值
	 */
	public static long getLong(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0)
			return 0L;
		else {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
		}

	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应短整型值
	 */
	public static short getShort(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0)
			return 0;
		else
			return Short.parseShort(value);
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应布尔值
	 */
	public static boolean getBoolean(HttpServletRequest request,
			String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0)
			return false;
		else
			return Boolean.valueOf(value).booleanValue();
	}

}
