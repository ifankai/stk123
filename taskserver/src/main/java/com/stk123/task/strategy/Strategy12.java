package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.common.util.EmailUtils;
import com.stk123.task.schedule.TaskUtils;

public class Strategy12 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(k != null && index.getKs().size() > 750){
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
			super.logStrategy(conn, today, "模型12-60日内出现天量后回调到60均线下方", results);
			EmailUtils.sendAndReport("模型12-60日内出现天量后回调到60均线下方,个数："+results.size()+",日期:"+today,
					"经典案例：鸿特精密[300176] 10日与20日均线粘合金叉,k线振幅较小,为最佳买入点<br>"+
							TaskUtils.createHtmlTable(today, results));
		}

	}
	
	public static boolean condition(K k) throws Exception{
		K k750 = k.getKByHVV(750);
		K k60 = k.getKByHVV(60);
		
		if(k750.getDate().equals(k60.getDate())
				&& k.getClose() <= k.getMA(K.Close, 60)
				&& k.getMA(K.Close, 60) > k.before(1).getMA(K.Close, 60)){
			//System.out.println(k);
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		
	}

}
