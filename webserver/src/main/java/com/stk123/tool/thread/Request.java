package com.stk123.tool.thread;


public interface Request {
	
	public int execute();
	
	public void addExecuteMethod(String methodName);
	
	public void addExecuteMethod(String methodName,Class clazz,Object obj);
	
	public void addExecuteMethod(String methodName,Class[] clazz,Object[] objs);

}
