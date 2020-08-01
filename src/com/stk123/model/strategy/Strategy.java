package com.stk123.model.strategy;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.db.util.CloseUtil;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.collection.Name2Value;
import org.junit.Assert;

public abstract class Strategy {
	
	public static boolean log  = false;
	
	public static List<Index> allIndexs = null;	
	public static List<Index> followIndexs = null;
	
	public String dataSourceName = null;
	public boolean sendMail;
	public boolean logToDB;
	public List<Index> indexs = new ArrayList<Index>();
	public List<Index> result = new ArrayList<Index>();
	
	public Strategy(){
		this.indexs = allIndexs;
		this.sendMail = true;
		this.logToDB = true;
	}
	
	public Strategy(String dataSourceName, List<Index> indexs, boolean sendMail, boolean logToDB){
		this.dataSourceName = dataSourceName;
		if(indexs != null){
			this.indexs = indexs;
		}
		this.sendMail = sendMail;
		this.logToDB = logToDB;
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public void setIndexs(List<Index> indexs) {
		this.indexs = indexs;
	}

	public abstract void run(Connection conn, String date) throws Exception;
	
	public String getImage(String fileName) throws Exception {
		InputStream is = Strategy.class.getResourceAsStream("images/"+fileName);
		return "<img src=\"data:image/png;base64," + IOUtils.toString(is) + "\"/>";
	}
	
	public void testCondition(String code, String date, boolean pass) {
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			Index index = new Index(conn, code);
			Assert.assertEquals(pass, condition(index, date));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			CloseUtil.close(conn);
		}
	}
	
	public boolean condition(Index index, String today) throws Exception{return false;}
	
	public static void logToDB(Connection conn,String date, String title, List<Index> indexs){
		if(indexs != null && indexs.size() == 0)return;
		List params = new ArrayList();
		params.add(date);
		params.add(title+"["+indexs.size()+"]");
		List<String> codes = IndexUtils.indexToCode(indexs);
		params.add(StringUtils.join(codes, ","));
		params.add(date);
		params.add(title+"["+indexs.size()+"]");
		try{
			JdbcUtils.insert(conn, "insert into stk_strategy select s_strategy_id.nextval,?,?,?,sysdate from dual where not exists (select 1 from stk_strategy where strategy_date=? and name=?)", params);
		}catch(Exception e){
			ExceptionUtils.insertLog(conn, e);
		}
	}
	
	private void logToDB2(Connection conn,String date, String title, List<Index> indexs){
		if(indexs != null && indexs.size() == 0)return;
		List params = new ArrayList();
		params.add(date);
		String t = title+"["+indexs.size()+"] ["+(StkUtils.numberFormat0Digits((double)indexs.size()/this.indexs.size()*100))+"%]";
		params.add(t);
		List<String> codes = IndexUtils.indexToCode(indexs);
		params.add(StringUtils.join(codes, ","));
		params.add(date);
		params.add(t);
		JdbcUtils.insert(conn, "insert into stk_strategy select s_strategy_id.nextval,?,?,?,sysdate from dual where not exists (select 1 from stk_strategy where strategy_date=? and name=?)", params);
	}
	
	public void logStrategy(Connection conn,String date,String title, List<Index> indexs){
		result.addAll(indexs);
		if(!this.logToDB)return;
		logToDB2(conn, date, (dataSourceName==null?"":"["+dataSourceName+"] ") + title, indexs);
	}
	
	public void sendAndReport(String title, int cnt, String date, String body) {
		if(!this.sendMail)return;
		EmailUtils.sendAndReport(title + (dataSourceName==null?"":" ["+dataSourceName+"]")+",个数:"+cnt+",日期:"+date, body);
	}
	
	public List<String> getResultCode(){
		return IndexUtils.indexToCode(this.result);
	}
	
}
