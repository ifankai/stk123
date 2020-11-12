package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.tool.util.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy15 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(k != null){
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
			super.logStrategy(conn, today, "模型15-巨量后k线振幅缩小且没有调整太深", results);
			EmailUtils.sendAndReport("模型15-巨量后k线振幅缩小且没有调整太深,个数："+results.size()+",日期:"+today,
					"策略来源：长春一东[600148]-2017/01/03<br>" +
							"策略关键点：<br>" +
							"k线如果缩量且振幅变小，且在放量日最低价位之上，则考虑买入" +
							"<br><br>"+
					StkUtils.createHtmlTable(today, results));
		}

	}
	
	public static boolean condition(K k) throws Exception{
		if(k.getClose() >= k.getMA(K.Close, 5)){
			K kv1 = k.getKByHVV(80);
			K kv2 = k.getKByHVV(200);
			/*if(k.getDate().equals("20161103")){
				System.out.println("kv1="+kv1.getDate()+",k.getHigh()="+k.getHigh()+",kv1.getLow()="+kv1.getLow());
			}*/
			if(!kv1.getDate().equals(k.getDate())
					&& kv1.getDate().equals(kv2.getDate())
					&& k.getHigh() >= kv1.getLow()
					&& kv1.getVolumn()/kv1.getMA(K.Volumn, 30) >= 3
					){				
				int cnt = k.getKCountWithCondition(5,new K.Condition() {
					public boolean pass(K k) throws Exception {
						double amplitude = (k.getHigh()-k.getLow())/k.getLow();
						double amplitude2 = (k.getOpen()-k.getClose())/Math.min(k.getClose(), k.getOpen());
						/*if(k.getDate().equals("20160930")){
							System.out.println("amplitude="+amplitude+",amplitude2="+amplitude2);
						}*/
						if(amplitude > 0 && amplitude <= 0.018 && Math.abs(amplitude2) <= 0.003 && k.getChangeOfClose() <= 0.02){
							return true;
						}
						return false;
					}
				});
				
				if(cnt >= 1){
					return true;
				}
			}
		}
		
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
