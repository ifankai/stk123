package com.stk123.task;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkFnType;
import com.stk123.service.ServiceUtils;
import com.stk123.model.Index;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;


public class UStkTools {
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		try {
			//HttpUtils.NO_OF_RETRY = 10;
			conn = DBUtil.getConnection();
			//InitialData.initUStkFromFinviz(conn);
			//InitialData.initUStkFromEasymoney(conn);
			//if(true)return;
			//InitialKLine.initUStkPE(conn);
			List<StkFnType> fnTypes = JdbcUtils.list(conn, "select * from stk_fn_type where market=2 and status=1", StkFnType.class);
			List<String> errors = new ArrayList<String>();
			String sql = null;
			String codes = "CWEI";
			if(codes != null && codes.length() > 0){
				sql = "select code,name from stk_us where market=2 and code in ('"+codes+"') order by code";
			}else{
				sql = "select code,name from stk_us where market=2 and code>='AAXJ' order by code";
			}
			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
			List<Index> indexs = new ArrayList<Index>();
			//String today = StkUtils.getToday();
			
			String today = JdbcUtils.load("select kline_date from (select kline_date from stk_kline_us where code='.DJI' order by kline_date desc) where rownum=1", String.class);
			System.out.println("today="+today);
			
			Index.KLineWhereClause = Index.KLINE_20130101;
			for(Stk stk : stks){
				try{
					System.out.println(stk.getCode()+","+stk.getName());
					String code = stk.getCode();
					Index index = new Index(conn,stk.getCode(),stk.getName());
					/*
					if(index.getStk().getHot() < 500 && !StringUtils.containsIgnoreCase(stk.getName(), "etf")){
						continue;
					}*/
					
					indexs.add(index);
					InitialData.initFnDataTTM(conn,ServiceUtils.now,index,fnTypes);
					
					//index.initKLineToday();
					//index.initKLines();

					/*if(index.isStop("20160715"))continue;
					if(index.getTotalMarketValue() <= 20)continue;
					
					K k = index.getK("20160715");
					if(k != null){
					double change = k.getChangeOfClose(20);
						if(change > 0.2){
							index.changePercent = change;
							indexs.add(index);
						}
					}*/
					
					//K k = index.getK(today);
					
					/*K todayK = index.getK(today);
					if(todayK != null && !index.isStop(today) && todayK.getClose() > 3 
							&& !todayK.isNoVolumeOrNoChange()){
						index.getKsMonthly(true);
			
						today="20160229";
						List<List<K>> list2 = index.isBreakOutTrendLine3(today, 30, 5, 1);
						System.out.println(list2.size());
						if(list2.size() > 0 || index.isBreakOutTrendLine2(today,30,4,0.05)){
							index.changePercent = todayK.getSlopeOfMA(30, 5);
							System.out.println("kkkkkkkkkkkkkkkkk");
						}
					}*/
					
//updateUSPEPBPS(conn, index);
					//index.initKLine();
					//InitialData.initFnDataTTM(conn,StkUtils.now,index,fnTypes);
					//InitialData.initialUStkStatus(conn, index.getCode());
					//index.initKLines(10000);
					
				}catch(Exception e){
					e.printStackTrace();
					errors.add(stk.getCode());
					//throw e;
				}
			}
			
			
			/*Collections.sort(indexs, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o1.changePercent - o2.changePercent)*10000);
					return i;
				}});
			
			for(Index index : indexs){
				//System.out.println(index.getCode()+","+index.getName());
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}*/
			
			
			//InitialData.initUStkFromSina(conn, false);
			
			//InitialKLine.checkErPinChaoDi(conn, today, indexs, 2);
			//InitialKLine.checkUSKIntersect(today, indexs);
			//InitialKLine.checkMACDWeekly(today, indexs);
			//InitialKLine.checkMonthlyTrandLine("20170105", indexs);
			//InitialKLine.checkErPinChaoDi(conn, "20161004", indexs, 2, true, true);
			
			/*List<Index> newHighs = IndexUtils.getIndexsByPercentRangeOnEarlyHighPoint(indexs, StkUtils.getToday(), 250, 0.85, 0.9);
			for(Index index : newHighs){
				System.out.println(index.getCode()+","+index.getName());
			}*/
			
			/*List<Map> avgPE = JdbcUtils.list2Map(conn, "select avg(pe_ttm) pe,kline_date from stk_kline_us where pe_ttm is not null and pe_ttm>3 and pe_ttm<200 group by kline_date order by kline_date desc");
			for(Map m : avgPE){
				System.out.println(m.get("kline_date")+":"+m.get("pe"));
				
				List params = new ArrayList();
				params.add(StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("pe"))), 2));
				params.add(String.valueOf(m.get("kline_date")));
				JdbcUtils.update(conn, "update stk_daily_report_us set result_2=? where report_date=?", params);
			}
			*/
			System.out.println(errors);
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void updateUSPEPBPS(Connection conn, Index index) throws Exception{
		String page = HttpUtils.get("https://ycharts.com/charts/fund_data.json?securities=include%3Atrue%2Cid%3A"+index.getCode()+"%2C%2C&calcs=include%3Atrue%2Cid%3Ape_ratio%2C%2C&correlations=&format=real&recessions=false&zoom=5&startDate=&endDate=&chartView=&splitType=&scaleType=&note=&title=&source=&units=&quoteLegend=&partner=&quotes=&legendOnChart=&securitylistSecurityId=&clientGroupLogoUrl=&displayTicker=&maxPoints=3650", null, "utf-8");

		//PE
		Map map = JsonUtils.testJson(page);
		List data = (List)map.get("chart_data");
		Map m = (Map)((List)data.get(0)).get(0);
		//System.out.println(m.get("raw_data"));
		List<List> raw = (List)m.get("raw_data");
		for(List p : raw){
			String time = (String)p.get(0);
			if(p.get(1).getClass() != String.class){
				continue;
			}
			String pe = (String)p.get(1);
			
			if(pe == null || pe.length()==0 || Double.parseDouble(pe)==0){
				continue;
			}
			
			Date d = new Date(Long.parseLong(time));			
			//System.out.println(StkUtils.formatDate(d, StkUtils.sf_ymd2)+","+pe);
			
			List params = new ArrayList();
			params.add(pe);
			params.add(index.getCode());
			params.add(ServiceUtils.formatDate(d, ServiceUtils.sf_ymd2));
			JdbcUtils.update(conn,"update stk_kline_us set pe_ttm=? where code=? and kline_date=?",params);

		}
		
		//PB
		//https://ycharts.com/charts/fund_data.json?securities=id%3AIRBT%2Cinclude%3Atrue%2C%2C&calcs=id%3Aprice_to_book_value%2Cinclude%3Atrue%2C%2C&correlations=&format=real&recessions=false&zoom=5&startDate=&endDate=&chartView=&splitType=&scaleType=&note=&title=&source=&units=&quoteLegend=&partner=&quotes=&legendOnChart=&securitylistSecurityId=&clientGroupLogoUrl=&displayTicker=&maxPoints=720
		page = HttpUtils.get("https://ycharts.com/charts/fund_data.json?securities=id%3A"+index.getCode()+"%2Cinclude%3Atrue%2C%2C&calcs=id%3Aprice_to_book_value%2Cinclude%3Atrue%2C%2C&correlations=&format=real&recessions=false&zoom=5&startDate=&endDate=&chartView=&splitType=&scaleType=&note=&title=&source=&units=&quoteLegend=&partner=&quotes=&legendOnChart=&securitylistSecurityId=&clientGroupLogoUrl=&displayTicker=&maxPoints=3650", null, "utf-8");
		map = JsonUtils.testJson(page);
		data = (List)map.get("chart_data");
		m = (Map)((List)data.get(0)).get(0);
		//System.out.println(m.get("raw_data"));
		raw = (List)m.get("raw_data");
		for(List p : raw){
			String time = (String)p.get(0);
			if(p.get(1).getClass() != String.class){
				continue;
			}
			String pe = (String)p.get(1);
			if(pe == null || pe.length()==0 || Double.parseDouble(pe)==0){
				continue;
			}
			Date d = new Date(Long.parseLong(time));
			//System.out.println(StkUtils.formatDate(d, StkUtils.sf_ymd2)+","+pe);
			
			List params = new ArrayList();
			params.add(pe);
			params.add(index.getCode());
			params.add(ServiceUtils.formatDate(d, ServiceUtils.sf_ymd2));
			JdbcUtils.update(conn,"update stk_kline_us set pb_ttm=? where code=? and kline_date=?",params);

		}
		
		//ps
		//https://ycharts.com/charts/fund_data.json?securities=include%3Atrue%2Cid%3AIRBT%2C%2C&calcs=include%3Atrue%2Cid%3Aps_ratio%2C%2C&correlations=&format=real&recessions=false&zoom=5&startDate=&endDate=&chartView=&splitType=&scaleType=&note=&title=&source=&units=&quoteLegend=&partner=&quotes=&legendOnChart=&securitylistSecurityId=&clientGroupLogoUrl=&displayTicker=&maxPoints=720
		page = HttpUtils.get("https://ycharts.com/charts/fund_data.json?securities=include%3Atrue%2Cid%3A"+index.getCode()+"%2C%2C&calcs=include%3Atrue%2Cid%3Aps_ratio%2C%2C&correlations=&format=real&recessions=false&zoom=5&startDate=&endDate=&chartView=&splitType=&scaleType=&note=&title=&source=&units=&quoteLegend=&partner=&quotes=&legendOnChart=&securitylistSecurityId=&clientGroupLogoUrl=&displayTicker=&maxPoints=3650", null, "utf-8");
		map = JsonUtils.testJson(page);
		data = (List)map.get("chart_data");
		m = (Map)((List)data.get(0)).get(0);
		//System.out.println(m.get("raw_data"));
		raw = (List)m.get("raw_data");
		for(List p : raw){
			String time = (String)p.get(0);
			if(p.get(1).getClass() != String.class){
				continue;
			}
			String pe = (String)p.get(1);
			if(pe == null || pe.length()==0 || Double.parseDouble(pe)==0){
				continue;
			}
			Date d = new Date(Long.parseLong(time));
			//System.out.println(StkUtils.formatDate(d, StkUtils.sf_ymd2)+","+pe);
			
			List params = new ArrayList();
			params.add(pe);
			params.add(index.getCode());
			params.add(ServiceUtils.formatDate(d, ServiceUtils.sf_ymd2));
			JdbcUtils.update(conn,"update stk_kline_us set ps_ttm=? where code=? and kline_date=?",params);

		}
		
	}
	
}
