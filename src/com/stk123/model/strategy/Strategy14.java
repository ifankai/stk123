package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy14 extends Strategy {

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			if(index.getKs().size() > 300)continue;
			
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
			//super.logStrategy(conn, today, "模型14-次新-小十字星且缩量", results);
			EmailUtils.sendAndReport("模型14-次新-小十字星且缩量,个数："+results.size()+",日期:"+today,
				"策略来源：嘉澳环保[603822]-2016/09/30,2017/03/03<br>" +
				"策略关键点：<br>" +
				"x日在20日均线上方，k线如果缩量且振幅变小，则考虑买入；<br>"+
				"x日在20日均线下方，则观察该股是否过去60日内振幅较大，突破过ene上轨，如有则看x日量能是否大于5日均量且过去5天出现过振幅变小且缩量的k线，则考虑买入；买入后，后面要放量最好不要低于x日量能，否则有创新低可能。" +
				"<br><br>"+
				StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static boolean condition(K k) throws Exception{
		if(k.getClose() >= k.getMA(K.Close, 20)){
			if(run(k)){
				return true;
			}
		}else{
			if(k.getVolumn() >= k.getMA(K.Volumn, 5)){
				int cnt = k.getKCountWithCondition(5, new K.Condition(){
					public boolean pass(K k) throws Exception {
						return run(k);
					}
				});
				/*if(k.getDate().equals("20161010")){
					System.out.println("kkkkkk="+cnt);
				}*/
				if(cnt >=1){
					cnt = k.getKCountWithCondition(60, new K.Condition(){
						public boolean pass(K k) throws Exception {
							Ene ene = k.getEne();
							if(ene.getUpper() <= k.getHigh()){
								return true;
							}
							return false;
						}
					});
					
					if(cnt >=1){
						K kl = k.getKByLLV(30);
						if(kl.dateEquals(k))return false;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean run(K k) throws Exception {
		double amplitude = (k.getHigh()-k.getLow())/k.getLow();
		double amplitude2 = (k.getOpen()-k.getClose())/Math.min(k.getClose(), k.getOpen());
		/*if(k.getDate().equals("201600930")){
			System.out.println("amplitude="+amplitude+",amplitude2="+amplitude2);
		}*/
		if(amplitude > 0 && amplitude <= 0.015 && Math.abs(amplitude2) <= 0.003 && k.getChangeOfClose() <= 0.02){
			K kv = k.getKByLVV(5);
			if(k.getDate().equals(kv.getDate())){
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
