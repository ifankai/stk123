package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.K.MACD;
import com.stk123.tool.util.StkUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.JdbcUtils;

public class Strategy5 extends Strategy {
	
	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results1 = new ArrayList<Index>();
		List<Index> results2 = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			if(index.getTotalMarketValue() >= 500)continue;
			if(index.getStk()!=null && index.getStk().getTotalCapital() != null){
				if(index.getStk().getTotalCapital() /10000.0 >= 8)continue;
			}
			int existing = JdbcUtils.load(conn, "select count(1) from stk_search_mview where code=? and (revenue_growth_rate>0 or net_profit_growth_rate>0)", index.getCode(), Integer.class);
			if(existing == 0)continue;
			
			List<List<K>> list1 = index.isBreakOutTrendLine3(today, 10, 2, 0);
			if(list1.size() > 0 || index.isBreakOutShortTrendLine(today)){
				K yk = k.before(1);
				MACD macd = k.getMACD();
				MACD yMacd = yk.getMACD();
				if(yMacd.dif < macd.dif || yMacd.dea < macd.dea || yMacd.macd < macd.macd){
					index.changePercent = macd.dif;
					results1.add(index);
				}
			}
			
			List<List<K>> list2 = index.isBreakOutTrendLine3(today, 60, 5, 0);
			if(list2.size() > 0 || index.isBreakOutTrendLine2(today,60,6,0.05)){
				K yk = k.before(1);
				MACD macd = k.getMACD();
				MACD yMacd = yk.getMACD();
				if(yMacd.dif < macd.dif || yMacd.dea < macd.dea || yMacd.macd < macd.macd){
					index.changePercent = macd.dif;
					results2.add(index);
				}
			}
		}
		
		if(results1.size()+results2.size() > 0){
			Collections.sort(results1, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((Math.abs(o1.changePercent) - Math.abs(o2.changePercent))*10000);
					return i;
				}});
			Collections.sort(results2, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o1.changePercent - o2.changePercent)*10000);
					return i;
				}});
			
			List<List> datas1 = new ArrayList<List>();
			for(Index result : results1){
				List data = new ArrayList();
				data.add(result);
				data.add(StkUtils.numberFormat(result.changePercent, 2));
				datas1.add(data);
			}
			List<List> datas2 = new ArrayList<List>();
			for(Index result : results2){
				List data = new ArrayList();
				data.add(result);
				data.add(StkUtils.numberFormat(result.changePercent, 2));
				datas2.add(data);
			}
			
			/*for(Index index : results1){
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}*/
			if(results1.size()+results2.size() > 0){
				//super.logStrategy(conn, today, "模型5-突破长期趋势线", results2);
				//super.logStrategy(conn, today, "模型5-突破短期趋势线", results1);
				List<String> addtitle = new ArrayList<String>();
				addtitle.add("MACD(Diff)");
				EmailUtils.sendAndReport("模型5-突破趋势线,个数："+(results1.size()+results2.size())+",日期:"+today,
						"突破长期趋势线:<br>"+StkUtils.createHtmlTable(today, datas2, addtitle)
						+ "<br><br>" +
						"突破短期趋势线:<br>"+StkUtils.createHtmlTable(today, datas1, addtitle)
						);
			}
		}
		
		
		//---自选股-关注C
		/*results1.clear();
		results2.clear();
		List<Index> list = followIndexs;
		for(Index index : list){
			if(index.isStop(today))continue;			
			List<List<K>> list1 = index.isBreakOutTrendLine3(today, 10, 2, 0);
			if(list1.size() > 0 || index.isBreakOutShortTrendLine(today)){
				results1.add(index);
			}
			
			List<List<K>> list2 = index.isBreakOutTrendLine3(today, 60, 5, 0);
			if(list2.size() > 0 || index.isBreakOutTrendLine2(today,60,6,0.05)){
				results2.add(index);
			}
		}
		
		if(results1.size()+results2.size() > 0){
			super.logStrategy(conn, today, "模型5-关注C-突破长期趋势线", results2);
			super.logStrategy(conn, today, "模型5-关注C-突破短期趋势线", results1);
		}*/
	}
	
}
