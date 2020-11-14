package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.schedule.TaskUtils;

public class Strategy9 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(index.getKs().size() > 60){
				int cnt = k.getKCountWithCondition(2, new K.Condition(){
					public boolean pass(K k) throws Exception {
						double times = k.getVolumeMA(5);
						return times >= 5;
					}
					
				});
				if(cnt > 0){
					results.add(index);
				}
			}
			
		}
		/*for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
		}*/
		if(results.size() > 0){
			EmailUtils.sendAndReport("模型9-最近5倍巨量,个数："+results.size()+",日期:"+today,
                    TaskUtils.createHtmlTable(today, results));
		}

	}


}
