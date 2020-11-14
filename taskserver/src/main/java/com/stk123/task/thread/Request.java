package com.stk123.task.thread;


public interface Request {
	
	public int execute();
	
	public void addExecuteMethod(String methodName);
	
	public void addExecuteMethod(String methodName,Class clazz,Object obj);
	
	public void addExecuteMethod(String methodName,Class[] clazz,Object[] objs);

}
