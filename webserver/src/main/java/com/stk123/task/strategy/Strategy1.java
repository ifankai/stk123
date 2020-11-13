package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.service.ServiceUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.TaskUtils;

public class Strategy1 extends Strategy {
	
	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			if(index.getTotalMarketValue() >= 500)continue;
			if(index.getStk()!=null && index.getStk().getTotalCapital() != null){
				if(index.getStk().getTotalCapital() /10000.0 >= 10)continue;
			}
			K yk = k.before(1);
			double ma60 = k.getMA(K.Close, 60);
			double yma60 = yk.getMA(K.Close, 60);
			if(ma60 >= yma60){
				//if(k.getClose() <= k.getOpen()){
					//if(k.getClose() >= ma60 * 0.94 && k.getLow() <= ma60 * 1.05){
						//if(k.getVolumn() <= yk.getVolumn()){
							K kh = k.getKByHVV(20);
							K kh2 = kh.getKByHVV(60);
							if(kh.getDate().equals(kh2.getDate())){								
								int cnt = k.getKCountWithCondition(20, new K.Condition() {
									public boolean pass(K k) throws Exception {
										return k.getClose() <= k.getMA(K.Close, 20);
									}
								});
								if(log){
									System.out.println("ccc="+(cnt<=6));
								}
								if(cnt <= 6){
									cnt = k.getKCountWithCondition(60, new K.Condition() {
										public boolean pass(K k) throws Exception {
											Ene ene = k.getEne();
											return k.getHigh() > ene.getUpper() || k.getLow() < ene.getLower();
										}
									});
									if(log){
										System.out.println("bbb="+(cnt>=12));
									}
									if(cnt >= 12){
										index.changePercent = cnt;
										cnt = k.getKCountWithCondition(20, new K.Condition() {
											public boolean pass(K k) throws Exception {
												return k.getVolumn() > k.getMA(K.Volumn, 10) * 1.3;
											}
										});
										//System.out.println(cnt);
										if(log){
											System.out.println("aaa="+(cnt>=5));
										}
										if(cnt >= 5){
											//连续波段性
											if(k.hasHighVolumn(1.4)){
												cnt = k.getKCountWithCondition(5, new K.Condition() {
													public boolean pass(K k) throws Exception {
														Ene ene = k.getEne();
														return k.getLow()*0.97 <= ene.getLower();
													}
												});
												if(cnt > 0){
													cnt = k.getKCountWithCondition(3, new K.Condition() {
														public boolean pass(K k) throws Exception {
															Ene ene = k.getEne();
															return k.getHigh() >= ene.getUpper();
														}
													});
													if(cnt == 0){
														results.add(index);
													}
												}
											}
										}
									}
								}
							}
						//}
					//}
				//}
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
			EmailUtils.sendAndReport("模型1-60日均线上升且活跃放量,个数："+results.size()+",日期:"+today,TaskUtils.createHtmlTable(today, results));
		}
	 

	}

}
