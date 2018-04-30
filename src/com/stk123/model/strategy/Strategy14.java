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
			//super.logStrategy(conn, today, "ģ��14-����-Сʮ����������", results);
			EmailUtils.sendAndReport("ģ��14-����-Сʮ����������,������"+results.size()+",����:"+today,
				"������Դ���ΰĻ���[603822]-2016/09/30,2017/03/03<br>" +
				"���Թؼ��㣺<br>" +
				"x����20�վ����Ϸ���k����������������С���������룻<br>"+
				"x����20�վ����·�����۲�ù��Ƿ��ȥ60��������ϴ�ͻ�ƹ�ene�Ϲ죬������x�������Ƿ����5�վ����ҹ�ȥ5����ֹ������С��������k�ߣ��������룻����󣬺���Ҫ������ò�Ҫ����x�����ܣ������д��µͿ��ܡ�" +
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
