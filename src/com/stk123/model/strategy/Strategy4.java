package com.stk123.model.strategy;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy4 extends Strategy {
	
	
	/**
	 * TODO 5日均量放大量后缩量，放的大量必须是前面80天内的最大5日均量
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
			double ma10 = k.getMA(K.Close, 10);
			if(k.getClose() < ma10){
				int cnt = k.getKCountWithCondition(5, new K.Condition() {
								public boolean pass(K k) throws Exception {
									boolean flag = k.isDecrementVolume(5);
									K k5 = k.before(5);
									if(flag && k.getClose() < k5.getClose() 
											&& k.getSlopeOfMA(10, 10) < 0.08
											&& k.getDayOnMaxOfVolumeMA(5, 120) >= 60
											){
										//System.out.println(k.getDate() + "," +k.getSlopeOfMA(10, 10) );
										return true;
									}
									return false;
								}
							});
				if(cnt > 0){
					K kv = k.getKByHVV(10);
					cnt = kv.getKCountWithCondition(3, new K.Condition() {
									public boolean pass(K k) throws Exception {
										double d = (k.getClose() - k.before(1).getClose())/k.before(1).getClose();
										return d > 0.06;
									}
								});
					if(cnt > 0){
						results.add(index);
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
			}
			System.out.println(StkUtils.join(results, ","));*/
			EmailUtils.sendAndReport("模型4-连续5日缩量,个数："+results.size()+",日期:"+today,
					getImage("Strategy4_1") + "<br><br>" + getImage("Strategy4_2")
					+ "<br><br>" +
					StkUtils.createHtmlTable(today, results));
		}
	 

	}

}
