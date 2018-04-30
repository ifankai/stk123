package com.stk123.web.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.collection.Name2Value;
import com.stk123.tool.web.ActionContext;

public class SyncAction {
	
	private static List<Name2Value<Long, String>> messages = new ArrayList<Name2Value<Long, String>>();
	
	public void perform() throws Exception {
		ActionContext ac = ActionContext.getContext();
		HttpServletRequest request = ac.getRequest();
		HttpSession session = request.getSession();
		
		Long lastTimestamp = (Long)session.getAttribute("lastTimestamp");
		List<Name2Value<Long, String>> results = null;
		if(lastTimestamp != null){
			long t = lastTimestamp;
			results = this.getMessageAfter(t);
		}else{
			results = messages;
		}
		if(results.size() > 0){
			session.setAttribute("lastTimestamp", results.get(results.size()-1).getName());
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		//System.out.println(json);
		ac.setResponse("var messages ="+json);
	}
	
	public void add() throws Exception {
		ActionContext ac = ActionContext.getContext();
		HttpServletRequest request = ac.getRequest();
		String smessages = request.getParameter("m");
		List<Name2Value<Long, String>> messages = JsonUtils.getList4Json(smessages, Name2Value.class);
		this.messages.addAll(messages);
		//System.out.println(this.messages);
	}
	
	public void clearMessage(){
		this.messages.clear();
	}
	
	private List<Name2Value<Long, String>> getMessageAfter(long l) {
		List<Name2Value<Long, String>> results = new ArrayList<Name2Value<Long, String>>();
		for(Name2Value<Long, String> message : messages){
			if(message.getName().longValue() > l){
				results.add(message);
			}
		}
		return results;
	}
	
	
	/**
	 *************  Database Operation  
	 * @throws IOException *************
	 */
	public void sync() throws IOException {
		ActionContext ac = ActionContext.getContext();
		try{
			HttpServletRequest request = ac.getRequest();
			//Connection conn = ActionContext.getConnection();
			Connection conn = DBUtil.getH2Connection();
			String type = request.getParameter("t");
			String sql = request.getParameter("sql");
			if("ddl".equalsIgnoreCase(type)){
				JdbcUtils.execute(conn, sql);
				ac.setResponse("{code:1, message:'success'}");
				return;
			}else if("dml".equalsIgnoreCase(type)){
				String p = request.getParameter("p");
				List params = null;
				if(p != null){
					params = JsonUtils.parseJsonToList(p);
				}
				int cnt = JdbcUtils.update(conn, sql, params);
				ac.setResponse("{code:1, message:'success', update:"+cnt+"}");
				return;
			}
		}catch(Exception e){
			ac.setResponse("{code:0, message:'"+ExceptionUtils.getException(e)+"'}");
		}
	}
	
	
}
