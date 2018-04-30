package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy7 extends Strategy {
	/**
	 * ͨ��ҽ��600763��20160530
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
			super.logStrategy(conn, today, "ģ��7-����5���µ�����ȴ�Ŵ�", results);
			EmailUtils.sendAndReport("ģ��7-����5���µ�����ȴ�Ŵ�,������"+results.size()+",����:"+today,
				StkUtils.createHtmlTable(today, results));
		}
	}

}
