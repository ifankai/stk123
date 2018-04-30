package com.stk123.model.quartz.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.bo.Stk;
import com.stk123.bo.cust.StkFnDataCust;
import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.baidu.BaiduSearch;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.html.HtmlTd;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.JdbcUtils;

public class BreakoutTrendLineJob implements Job {
	
	public static void run() throws Exception {
		String today = StkUtils.getToday();
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			Map<String,K> map = IndexUtils.getKsRealTime(conn);
			System.out.println(map.size());
			
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			Index.KLineWhereClause = "and kline_date>='" + StkUtils.formatDate(StkUtils.addDay(StkUtils.now, -120),StkUtils.sf_ymd2)+"'";
			
			Index sh = new Index(conn, "999999");
			K ksh = sh.getK(0);
			//System.out.println(ksh);
			
			List<Index> results = new ArrayList<Index>();
			for(Stk stk : stks){
				Index index = new Index(conn, stk.getCode());
				K k = index.getK(0);
				if(k == null){
					continue;
				}
				K kRealTime = map.get(index.getCode());
				
				if(kRealTime != null){
					if(kRealTime.getClose() == 0.0){
						continue;//停牌的
					}
					if(!kRealTime.dateEquals(k)){
						index.addK(kRealTime);
					}else{
						K kTmp = (K)k.clone();
						kTmp.setDate(StkUtils.formatDate(StkUtils.addDay(k.getDate(), 1), StkUtils.sf_ymd2));
						index.addK(kTmp);
					}
					
					K k0 = k.after(1);
					K k1 = k;
					
					
					if(k0.getClose() < k1.getClose()*1.01){
						k0.setClose(k1.getClose()*1.01);
					}
					
					if(index.isBreakOutTrendLine2(today,60,6,0.05)
							|| index.isBreakOutShortTrendLine(today)
							//|| index.isBreakOutTrendLine3(today, 10, 2, 0).size() > 0
							|| index.isBreakOutTrendLine3(today, 30, 3, 0).size() > 0
							//|| index.isBreakOutTrendLine3(today, 60, 5, 0).size() > 0
							){
						
						if(existingXueqiuFollow(index.getCode())){
							results.add(index);
						}else{
							StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
							Double gm = fn.getFnValue();
							if(gm != null && gm.doubleValue() >= 50){//毛利率>=50%
								results.add(index);
							}else{
								List<String> nextEF = null;
								try{
									nextEF = index.getEarningsForecastAsList();
								}catch(Exception e){}
								if(nextEF != null && nextEF.size() > 3){
									fn = index.getFnDataLastestByType(index.FN_JLRZZL);
									if(fn!=null && fn.getFnDate().compareTo(StringUtils.replace(nextEF.get(0), "-", "")) > 0){
										Double jlr = fn.getFnValue();
										if(jlr != null && jlr.doubleValue() >= 50){
											results.add(index);
										}
									}else{
										double ef = StkUtils.percentigeGreatThan(nextEF.get(2));
										if(ef >= 40){
											results.add(index);
										}
									}
								}
							}
						}
					}
				}
				
			}
			
			do{
				
				List<String> codes = new ArrayList<String>();
				for(Index index : results){
					codes.add(index.getCode());
				}
				List<Index> breakouts = new ArrayList<Index>();
				
				Map<String,K> maps = IndexUtils.getKsRealTime(conn, codes);
				for(Map.Entry<String, K> m : maps.entrySet()){
					K k = m.getValue();
					if((k.getClose()-k.getLastClose())/k.getLastClose() >= 0.011){
						breakouts.add(IndexUtils.remove(results, m.getKey()));
					}
				}
				
				/*for(Index index : results){
					System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
				}*/
				
				if(breakouts.size() > 0){
					BaiduSearch.SearchSwitch = false;
					EmailUtils.sendImport("K线突破下降趋势个数："+breakouts.size()+",时间:"+ StkUtils.sf_ymd15.format(new Date()), 
							StkUtils.createHtmlTable(today, breakouts));
				}
				
				if(new Date().getHours() >= 15){
					break;
				}
				
				Thread.currentThread().sleep(1000 * 300);
			
			}while(true);
			
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	private static Set<String> FollowC = new HashSet<String>();
	
	private static boolean existingXueqiuFollow(String code){
		if(FollowC.size() == 0){
			try {
				FollowC = XueqiuUtils.getFollowStks("关注C");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return FollowC.contains(code);
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			run();
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			EmailUtils.send("BreakoutTrendLineJob Error", aWriter.getBuffer().toString());
		}
	}
	
	public static void main(String[] args) throws Exception {
		run();
	}

}
