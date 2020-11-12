package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.tool.util.StkUtils;


public class Strategy18 extends Strategy {
	
	public Strategy18(){
		this(null,null,false,true);
	}
	
	public Strategy18(String dataSourceName, List<Index> indexs, boolean sendMail, boolean logToDB){
		super(dataSourceName, indexs, sendMail, logToDB);
	}

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			if(condition(index, today)){
				results.add(index);
			}
		}
		
		if(results.size() > 0){
			super.logStrategy(conn, today,"???18-k???λ?????", results);
			super.sendAndReport("???18-k???λ?????", results.size(), today,
					"????????????????[300299]-2017/07/19<br><br><br>"+
					StkUtils.createHtmlTable(today, results));
		}
	}
	
	public boolean condition(Index index, String today) throws Exception{
		K k = index.getK(today);
		if(k == null) return false;
		double ma5 = k.getMA(K.Close, 5);
		if(k.getClose() > ma5)return false;
		double ma20 = k.getMA(K.Close, 20);
		if(k.getClose() > ma20)return false;
		if(Math.abs(k.getOpen()-k.getClose())/k.getOpen() <= 0.004 
				&& k.getHigh() > k.getOpen() && k.getLow() < k.getOpen()
				&& k.getHigh() > k.getClose() && k.getLow() < k.getClose()){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		/*Strategy18 s = new Strategy18();
		s.testCondition("300299","20170719", true);
		s.testCondition("300299","20170720", false);*/
	}

}
