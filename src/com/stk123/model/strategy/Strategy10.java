package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy10 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(k != null){
				if(condition(k)){
					K yk = k.before(1);
					if(!condition(yk))index.isNew=true;
					results.add(index);
				}
			}
			
		}
		/*for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
		}*/
		if(results.size() > 0){
			super.logStrategy(conn, today, "模型10-三堆巨量后缩量", results);
			EmailUtils.sendAndReport("模型10-三堆巨量后缩量,个数："+results.size()+",日期:"+today,
				StkUtils.createHtmlTable(today, results));
		}

	}
	
	public static boolean condition(K k) throws Exception{
		final K kv = k.getKByHVV(250);
		
		int cnt = k.getKCountWithCondition(120,5, new K.Condition() {
			public boolean pass(K k) throws Exception {
				return k.getClose() >= k.getOpen() && k.getVolumn() >= kv.getVolumn()/2 
						&& k.getVolumn()/k.getMA(K.Volumn, 10) >= 2;
			}
		});
		if(cnt >= 3){
			cnt = k.getKCountWithCondition(80,5, new K.Condition() {
				public boolean pass(K k) throws Exception {
					return k.getClose() >= k.getOpen() && k.getVolumn() >= kv.getVolumn()/2 
							&& k.getVolumn()/k.getMA(K.Volumn, 10) >= 2;
				}
			});
			
			if(cnt >= 1){
				if(k.getVolumn() < k.getMA(K.Volumn, 10)
						&& ((k.getLow() < k.getMA(K.Close, 30) || k.getLow() < k.getMA(K.Close, 20)))){
					//System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
