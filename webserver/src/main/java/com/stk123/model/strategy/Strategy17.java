package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.tool.util.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy17 extends Strategy {
	
	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		List<Index> results2 = new ArrayList<Index>();
		List<Index> results3 = new ArrayList<Index>();
		List<Index> results4 = new ArrayList<Index>();
		List<Index> results5 = new ArrayList<Index>();
		for(Index index : indexs){
			K todayK = index.getK(today);
			if(!index.isStop(today) && todayK != null && !todayK.isUpLimit()){
				K yk = todayK.before(1);
				
				int cnt = todayK.getKCountWithCondition(20, new K.Condition() {
					@Override
					public boolean pass(K k) throws Exception {
						K highk = k.getKByHVV(30);
						if(k.getDate().equals(highk.getDate()) && k.getOpen() <= k.getClose() ){
							return true;
						}
						return false;
					}
				});
				if(cnt > 0){
					if(index.isKIntersect1(today,0)){
						results.add(index);
					}
					if(index.isKIntersect2(today)){
						results2.add(index);
					}
					if(index.isKIntersect3(today)){
						results3.add(index);
					}
					if(index.isKIntersect4(today)){
						results4.add(index);
					}
					if(index.isKIntersect5(today)){
						results5.add(index);
					}
				}
			}
		}
		Collections.sort(results, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results2, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results3, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results4, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results5, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		int cnt = results.size()+results2.size()+results3.size()+results4.size()+results5.size();
		if(cnt > 0){
			/*super.logStrategy(conn, today, "模型17-K线缠绕-10,20,30,60,120日K线缠绕", results2);
			super.logStrategy(conn, today, "模型17-K线缠绕-10,20,30,60,250日K线缠绕", results3);
			super.logStrategy(conn, today, "模型17-K线缠绕-10,20,30,60日K线缠绕", results4);
			super.logStrategy(conn, today, "模型17-K线缠绕-10,20,30日K线缠绕", results5);
			super.logStrategy(conn, today, "模型17-K线缠绕", results);*/
			EmailUtils.sendAndReport("模型17-K线缠绕,个数：" + cnt
					+ ",日期:" + today, "<b>切记最好等均线走出多头排列后再入，短期均线金叉长期均线</b>"
					+ "<br>10,20,30,60,120日K线缠绕<br>"	+ StkUtils.createHtmlTable(today, results2)  
					+ "<br><br>10,20,30,60,250日K线缠绕<br>" + StkUtils.createHtmlTable(today, results3) 
					+ "<br><br>10,20,30,60日K线缠绕<br>" + StkUtils.createHtmlTable(today, results4) 
					+ "<br><br>" + StkUtils.createHtmlTable(today, results) 
					+ "<br><br>10,20,30日K线缠绕<br>" + StkUtils.createHtmlTable(today, results5) 
					);
		}

	}
	

	public static void main(String[] args) {

	}

}
