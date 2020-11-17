package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.tool.TaskUtils;

public class Strategy7 extends Strategy {
	/**
	 * 通策医疗600763，20160530
	 */
	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(k.isIncrementVolume(5) && k.isDecrementLow(5)){
				//System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
				results.add(index);
			}
			
		}
		if(results.size() > 0){
			super.logStrategy(conn, today, "模型7-连续5日下跌量能却放大", results);
			EmailUtils.sendAndReport("模型7-连续5日下跌量能却放大,个数："+results.size()+",日期:"+today,
					TaskUtils.createHtmlTable(today, results));
		}
	}

}
