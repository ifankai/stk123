package com.stk123.task.thread;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.common.db.connection.ConnectionPool;
import com.stk123.service.ExceptionUtils;


public class IndexRequest implements Request {
	
	private List<RequestMethod> methods = new ArrayList<RequestMethod>();
	private List<Object> results = new ArrayList<Object>();
	private ConnectionPool pool = null;
	private Index index = null;
	private Class clazz = null;
	
	public IndexRequest(Index index){
		this(null,index);
	}
	
	public IndexRequest(ConnectionPool pool, Index index){
		this.pool = pool;
		this.index = index;
		this.clazz = index.getClass();
	}
	
	public void addExecuteMethod(String methodName){
		addExecuteMethod(methodName, (Class[])null, (Object[])null);
	}
	public void addExecuteMethod(String methodName,Class[] clazz,Object[] objs){
		methods.add(new RequestMethod(methodName, clazz, objs));
	}
	public void addExecuteMethod(String methodName,Class clazz,Object obj){
		addExecuteMethod(methodName, new Class[]{clazz}, new Object[]{obj});
	}

	public int execute() {
		Connection conn = null;
		try{
			if(pool != null){
				conn = pool.getConnection();
				index.setConnection(conn);
			}
			for(RequestMethod method : methods){
				Method m = clazz.getMethod(method.getName(), method.getClazz());
				Object o = m.invoke(index, method.getObjs());
				results.add(o);
			}
			return 0;
		}catch(Exception e){
			ExceptionUtils.insertLog(conn, index.getCode(), e);
			e.printStackTrace();
			return 0;
		}finally{
			if(pool != null)pool.release(conn);
		}
	}
	
	public List<Object> getResult(){
		return this.results;
	}

}

class RequestMethod{
	private String name;
	private Class[] clazz;
	private Object[] objs;
	
	public RequestMethod(String name,Class[] clazz,Object[] objs){
		this.name = name;
		this.clazz = clazz;
		this.objs = objs;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class[] getClazz() {
		return clazz;
	}
	public void setClazz(Class[] clazz) {
		this.clazz = clazz;
	}
	public Object[] getObjs() {
		return objs;
	}
	public void setObjs(Object[] objs) {
		this.objs = objs;
	}
	
}
