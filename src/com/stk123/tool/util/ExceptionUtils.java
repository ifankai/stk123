package com.stk123.tool.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExceptionUtils {
	
	public static void insertLog(Connection conn, Exception e){
		ExceptionUtils.insertLog(conn,"999999", e);
	}
	
	public static void insertLog(Connection conn, String code, Exception e){
		List params = new ArrayList();
		params.add(code);
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		params.add(JdbcUtils.createClob(aWriter.getBuffer().toString()));
		params.add(new Timestamp(new Date().getTime()));
		JdbcUtils.insert(conn, "insert into stk_error_log(code,error,insert_time) values(?,?,?)", params);
	}
	
	public static Exception exception(String msg, Exception e){
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		return new Exception(msg +"\r"+ aWriter.getBuffer().toString());
	}

	public static String getException(Throwable e){
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		return aWriter.getBuffer().toString();
	}
}
