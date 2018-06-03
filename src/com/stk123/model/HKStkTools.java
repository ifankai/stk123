package com.stk123.model;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.Stk;
import com.stk123.bo.StkFnType;
import com.stk123.model.strategy.Strategy16;
import com.stk123.task.InitialData;
import com.stk123.task.NoticeRobot;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.PDFUtils;
import com.stk123.web.action.SyncAction;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertySetStrategy;


public class HKStkTools {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//EmailUtils.SEND_MAIL = false;
		
		Connection conn = null;
		List<String> errors = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			IndexContext context = new IndexContext();
			
			List<String> result = new ArrayList<String>();
			String codes = "08597";
			String sql = null;
			if(codes != null && codes.length() > 0){
				sql = "select code,name from stk where market=3 and code in ("+codes+") order by code";
			}else{
				sql = "select code,name from stk where market=3 and cate in (1) and code>=01195 order by code";
			}
			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
			List<Index> indexs = new ArrayList<Index>();
			int i = 0;
			
			String today = StkUtils.getToday();
			//today = "20170522";
			Index.KLineWhereClause = Index.KLINE_20140101;
			
			
			for(Stk stk : stks){
				try{
					Index index =  new Index(conn,stk.getCode(),stk.getName());
					context.indexs.add(index);
					System.out.println("code:"+stk.getCode());
					
					//index.initKLine();
					
					
				}catch(Exception e){
					e.printStackTrace();
					errors.add(stk.getCode());
					System.out.println(stk.getCode());
					//throw e;
				}
			}
			
			
			Collections.sort(indexs, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o1.changePercent - o2.changePercent)*10000);
					return i;
				}});
			
			for(Index index : indexs){
				//System.out.println(index.getCode()+","+index.getName());
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}
			
			
		} finally {
			if (conn != null) conn.close();
		}
		if(errors.size() > 0)
			System.out.println("errors:"+errors);

	}
	
}
