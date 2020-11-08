package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy16 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			if(condition(index, today)){
				results.add(index);
			}
		}
		/*for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
		}*/
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			//System.out.println(results);
			super.logStrategy(conn, today, "模型16-阳线放量阴线缩量", results);
			EmailUtils.sendAndReport("模型16-阳线放量阴线缩量,个数："+results.size()+",日期:"+today,
					"策略来源：方大碳素[600516]-2017/05/22之前<br>" +
							"<br><br>"+
					StkUtils.createHtmlTable(today, results));
		}

	}
	
	public boolean condition(Index index, String today) throws Exception{
		K k = index.getK(today);
		if(k == null) return false;
		double c20 = k.getMA(K.Close, 20);
		if(k.getClose() < c20*0.98)return false;
		
		double d = 0.0;
		int count = 0;
		int cnt = 0;
		int x = 1;
		if(k.getClose() <= k.getOpen()){
			double maxVolumn = k.getVolumn();
			do{
				k = k.before(1);
				if(k.isNoVolumeOrNoChange())break;
				if(k.getClose() <= k.getOpen()){
					maxVolumn = Math.max(maxVolumn, k.getVolumn());
				}else{
					if(count++ >= 20)break;
					if(maxVolumn == 0)continue;
					if(k.getVolumn() > maxVolumn){
						d += k.getVolumn()/maxVolumn;
						maxVolumn = 0;
					}else{
						if(x-- == 0)
						break;
					}
				}
				if(cnt++ >= 60)break;
			}while(true);
		}
		
		if(count != 0 && d != 0 && count * d >= 100){
			k = index.getK(today);
			int n = k.getKCountWithCondition(10, new K.Condition() {
				
				@Override
				public boolean pass(K k) throws Exception {
					K k30 = k.before(30);
					double kv10 = k.getMA(K.Volumn, 10);
					double k30v10 = k30.getMA(K.Volumn, 10);
					if(kv10/k30v10 >= 2){
						return true;
					}
					return false;
				}
			});
			//System.out.println(n);
			if(n >= 5){
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {

	}

}
