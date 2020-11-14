package com.stk123.task.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;

public class Strategy19 extends Strategy {
	
	public Strategy19(){
		this(null,null,false,true);
	}
	
	public Strategy19(String dataSourceName, List<Index> indexs, boolean sendMail, boolean logToDB){
		super(dataSourceName, indexs, sendMail, logToDB);
	}
	
	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			List<List<K>> list1 = index.isBreakOutTrendLine3(today, 10, 2, 0);
			if(list1.size() > 0 || index.isBreakOutShortTrendLine(today)){
				results.add(index);
			}
			
			List<List<K>> list2 = index.isBreakOutTrendLine3(today, 60, 5, 0);
			if(list2.size() > 0 || index.isBreakOutTrendLine2(today,60,6,0.05)){
				results.add(index);
			}
		}
		
		if(results.size() > 0){
			super.logStrategy(conn, today, "丁垂19-往它宅找阿", results);
		}
	}
	
}
