package com.stk123.tool.thread;

public class Log {
	public static void log(String o){
		System.out.println(o);
	}
	
	public static void error(Exception e,String o){
		System.err.println(o);
	}
	
	public static void error(String o){
		System.err.println(o);
	}
}
