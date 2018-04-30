package com.stk123.web.monitor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.StkMonitor;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.JdbcUtils;


public abstract class Monitor {

	public final static int TYPE_KLINE = 1;
	
	private static Monitor klineMonitor = new KlineMonitor();
	
	public static void run(Connection conn, int type) throws Exception{
		List params = new ArrayList();
		params.add(type);
		List<StkMonitor> sms = JdbcUtils.list(conn, "select * from stk_monitor where type=? and trigger_date is null order by insert_date asc",params, StkMonitor.class);
		List<String> results = new ArrayList<String>();
		for(StkMonitor sm : sms){
			String result = getInstance(sm).execute(sm,conn,true);
			if(result != null){
				results.add(result);
			}
		}
		if(results.size() > 0){
			EmailUtils.send("Êý¾Ý¼à¿Ø", StringUtils.join(results, "<br/>"));
		}
	}
	
	public static Monitor getInstance(StkMonitor sm){
		if(sm.getType() == 1){
			return klineMonitor;
		}
		return null;
	}
	
	public abstract String execute(StkMonitor sm,Connection conn,boolean updateResult) throws Exception;
	
	public abstract String translate(StkMonitor sm,Connection conn) throws Exception;
}
