package com.stk123.tool.sync;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.bo.StkSearchCondition;
import com.stk123.bo.StkSearchMview;
import com.stk123.bo.StkStrategy;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;

public class Sync implements Job {
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			run();
		}catch(Exception e){
			EmailUtils.send("Sync Job error:", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		run();
	}
	
	public static void run() throws Exception {
		String result = deleteTableAllRows("stk_search_condition");
		System.out.println(result);
		insertTableAllRows(StkSearchCondition.class);
		
		//--------
		result = deleteTableAllRows("stk_search_mview");
		System.out.println(result);
		insertTableAllRows(StkSearchMview.class);
		
		//--------
		result = deleteTableAllRows("stk_strategy");
		System.out.println(result);
		insertTableAllRows(StkStrategy.class);
	}
	
	public static String deleteTableAllRows(String tableName) throws Exception {
		String sql = "delete from "+tableName;
		String body = "sql="+URLEncoder.encode(sql, "utf-8");
		return HttpUtils.post("http://60.205.210.238/sync?method=sync&t=dml", body, null);
	}
	
	public static String insertTableOneRow(Object obj) throws Exception {
		JdbcUtils.SqlAndParams sp = JdbcUtils.getInsertSQL(obj);
		String body = "sql="+URLEncoder.encode(sp.sql, "utf-8")+"&p=" + URLEncoder.encode(JsonUtils.parseListToJson(sp.params), "utf-8");
		return HttpUtils.post("http://60.205.210.238/sync?method=sync&t=dml", body, null);
	}
	
	public static void insertTableAllRows(Class clazz) throws Exception{
		System.out.println(clazz.getClass());
        String tableName = JdbcUtils.getTableName(clazz);
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			List list = JdbcUtils.list(conn, "select * from "+tableName, clazz);
			for(Object sc : list){
				String result = insertTableOneRow(sc);
				System.out.println(result);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			conn.close();
		}
	}

	

}
