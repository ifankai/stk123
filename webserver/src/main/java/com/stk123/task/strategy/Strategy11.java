package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.service.ServiceUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.TaskUtils;

public class Strategy11 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(k != null){
				if(condition(k)){
					/*K yk = k.before(1);
					if(!condition(yk))index.isNew=true;*/
					results.add(index);
				}
			}
			
		}
		/*for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
		}*/
		if(results.size() > 0){
			super.logStrategy(conn, today, "模型11-二堆巨量后缩量", results);
			EmailUtils.sendAndReport("模型11-二堆巨量后缩量,个数："+results.size()+",日期:"+today,
					TaskUtils.createHtmlTable(today, results));
		}

	}
	
	public static boolean condition(K k) throws Exception{
		final K kv = k.getKByHVV(180);
		//System.out.println(kv.getDate());
		int cnt = k.getKCountWithCondition(100,5, new K.Condition() {
			public boolean pass(K k) throws Exception {
				/*if(k.getDate().equals("20170306")){
					System.out.println((k.getVolumn() >= kv.getVolumn()/2)+","+(k.getVolumn()/k.getMA(K.Volumn, 20) >= 2));
				}*/
				return k.getClose() >= k.getOpen() && k.getVolumn() >= kv.getVolumn()/2 
						&& k.getVolumn()/k.getMA(K.Volumn, 20) >= 2;
			}
		});
		
		//System.out.println(k.getDate()+","+cnt);
		if(cnt >= 2){
			
			cnt = k.getKCountWithCondition(80,5, new K.Condition() {
				public boolean pass(K k) throws Exception {
					/*if(k.getDate().equals("20151216")){
						System.out.println("kkkk="+(k.getVolumn() >= kv.getVolumn()/2)+","+(k.getVolumn()/k.getMA(K.Volumn, 20) >= 1.5));
					}*/
					return k.getClose() >= k.getOpen() && k.getVolumn() >= kv.getVolumn()/2 
							&& k.getVolumn()/k.getMA(K.Volumn, 20) >= 1.5;
				}
			});
			
			//System.out.println(k.getDate()+",=="+cnt);
			if(cnt >= 1){
			
				if(k.getVolumn() < k.getMA(K.Volumn, 10)
						&& (k.getLow() < k.getMA(K.Close, 30) || k.getLow() < k.getMA(K.Close, 20))){
					K ky = k.before(1);
					K ky30 = k.before(30);
					K ky31 = k.before(31);
					if(k.getMA(K.Close, 60) >= ky.getMA(K.Close, 60) 
							|| ky30.getMA(K.Close, 60) >= ky31.getMA(K.Close, 60)){
						K kh = k.getKByHHV(100);
						K kl = k.getKByLLV(100);
						/*if(k.getDate().equals("20160201")){
							System.out.println("kkkk="+kh.dateBefore(kl)+","+((kh.getClose() - k.getClose())/k.getClose()));
						}*/
						if(kh.dateBefore(kl) || (kh.dateAfter(kl) && (kh.getClose() - kl.getClose())/kl.getClose() < 0.5)
								|| (kh.dateAfter(kl) && (kh.getClose() - k.getClose())/k.getClose() > 0.2) ){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
