package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.schedule.TaskUtils;

public class Strategy8 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			List<K> listh = index.getKsHistoryHighPoint(today, 60, 6);
			if(listh.size() >= 3){
				K h1 = listh.get(listh.size()-1);
				K h2 = listh.get(listh.size()-2);
				K h3 = listh.get(listh.size()-3);
				if(h1.getHigh() < h2.getHigh() && h2.getHigh() < h3.getHigh()){
					List<K> listl = index.getKsHistoryLowPoint(today, 60, 6);
					if(listl.size() >= 2){
						K l1 = listl.get(listl.size()-1);
						K l2 = listl.get(listl.size()-2);
						if(k.getLow() < l1.getLow() && l1.getLow() < l2.getLow()){
							results.add(index);
						}
					}
				}
			}
			
		}
		if(results.size() > 0){
			EmailUtils.sendAndReport("模型8-5浪下跌,个数："+results.size()+",日期:"+today,
					TaskUtils.createHtmlTable(today, results));
		}

	}

}
