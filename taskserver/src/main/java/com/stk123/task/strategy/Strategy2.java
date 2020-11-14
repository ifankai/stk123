package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.schedule.TaskUtils;

public class Strategy2 extends Strategy {
	
	/**
	 * 20160308
	 * 002407,300327
	 */

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			if(index.getTotalMarketValue() >= 500)continue;
			if(index.getStk()!=null && index.getStk().getTotalCapital() != null){
				if(index.getStk().getTotalCapital() /10000.0 >= 8)continue;
			}
			K yk = k.before(1);
			double ma60 = k.getMA(K.Close, 60);
			double yma60 = yk.getMA(K.Close, 60);
			if(ma60 <= yma60 && k.getLow() <= ma60){
				double ma120 = k.getMA(K.Close, 120);
				double yma120 = yk.getMA(K.Close, 120);
				if(ma120 >= yma120){
					if(k.hasHighVolumn(1.4)){
						int cnt = k.getKCountWithCondition(60, new K.Condition() {
							public boolean pass(K k) throws Exception {
								Ene ene = k.getEne();
								return k.getHigh() > ene.getUpper() || k.getLow() < ene.getLower();
							}
						});
						if(log){
							System.out.println("bbb="+(cnt>=12));
						}
						if(cnt >= 30){
							index.changePercent = cnt;
							cnt = k.getKCountWithCondition(5, new K.Condition() {
								public boolean pass(K k) throws Exception {
									Ene ene = k.getEne();
									return k.getLow()*0.97 <= ene.getLower();
								}
							});
							if(cnt > 0){
								int cntUpper = 0;
								int cntLower = 0;
								List<K> ks = index.getKsHistoryHighAndLow(today, 60, 6);
								for(K kk : ks){
									Ene ene = kk.getEne();
									if(kk.getLow() < ene.getLower()){
										cntLower ++;
									}
									if(kk.getHigh() > ene.getUpper()){
										cntUpper ++;
									}
								}
								if(cntLower >= 2 && cntUpper >= 2){
									results.add(index);
								}
							}
						}
						
					}
				}
			}
			
		}
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			/*for(Index index : results){
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}*/
			EmailUtils.sendAndReport("模型2-120日均线上升60日线下降,个数："+results.size()+",日期:"+today,TaskUtils.createHtmlTable(today, results));
		}
	 

	}

}
