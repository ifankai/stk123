package com.stk123.model.quartz.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.model.K;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;

public class OneMinuteJob implements Job {
	
	public static List<String> CODES = new ArrayList<String>();
	static{
		CODES.add("01000852");//��֤1000
		CODES.add("01000905");//��֤500
		CODES.add("01000016");//��֤50
		CODES.add("399006");
		
	}
	
	private static Map SendMail = new HashMap();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			run();
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	
	public static void run() throws Exception {
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			for(String code : CODES){
				System.out.println("[OneMinuteJob]"+code);
				K k = IndexUtils.getKsRealTime(conn, code);
				Index index =  new Index(conn, code);
				index.addK(k);
				StringBuffer sb = new StringBuffer();
				
				if("399006".equals(code)){
					double warn = 1500;
					if(SendMail.get(code+"warning") == null && k.getClose() <= warn){
						SendMail.put(code+"warning", "warning");
						sb.append("[����]"+index.getName()+"["+code+"]"+"����ǰ�ڵ͵�"+warn+"<br>");
						//EmailUtils.send(EmailUtils.IMPORTANT + "[����]"+index.getName()+"["+code+"]"+"����ǰ������ȱ��"+warn+"","");
					}
					warn = 2280;
					if(SendMail.get(code+"warning2") == null && k.getClose() >= warn){
						SendMail.put(code+"warning2", "warning");
						sb.append("[����]"+index.getName()+"["+code+"]"+"�ϴ�"+warn+"Ҫ���ַ�<br>");
						//EmailUtils.send(EmailUtils.IMPORTANT + "[����]"+index.getName()+"["+code+"]"+"�ϴ�"+warn+"Ҫ���ַ�MACD 30�����Ƿ��룿","");
					}
				}
				if(sb.length() > 0){
					EmailUtils.send(EmailUtils.IMPORTANT, sb.toString());
				}
			}
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void main(String[] arg) throws Exception{
		run();
	}

}
