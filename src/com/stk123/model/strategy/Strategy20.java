package com.stk123.model.strategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;

public class Strategy20 extends Strategy {
	
	public Strategy20(){
		this(null,null,false,true);
	}
	
	public Strategy20(String dataSourceName, List<Index> indexs, boolean sendMail, boolean logToDB){
		super(dataSourceName, indexs, sendMail, logToDB);
	}

	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			
			if(k != null){
				if(condition(k) || condition(index, today)){
					//K yk = k.before(1);
					//if(!condition(yk))index.isNew=true;
					results.add(index);
				}
				
			}
			
		}
		/*for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
		}*/
		if(results.size() > 0){
			super.logStrategy(conn, today, "Ä£ÐÍ20-Ê®×ÖÐÇ", results);
		}
	}
	
	public static boolean condition(K k) throws Exception{
		if(k.getClose() >= k.getMA(K.Close, 20)){
			if(run(k)){
				return true;
			}
		}else{
			if(k.getVolumn() >= k.getMA(K.Volumn, 5)) return false;
			
			int cnt = k.getKCountWithCondition(2, new K.Condition(){
				public boolean pass(K k) throws Exception {
					return run(k);
				}
			});
			/*if(k.getDate().equals("20170726")){
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
				
				if(cnt <= 0){
					cnt = k.getKCountWithCondition(60, new K.Condition(){
						public boolean pass(K k) throws Exception {
							double ma60 = k.getMA(K.Close, 60);
							if(ma60 <= k.getClose()){
								return true;
							}
							return false;
						}
					});
				}
				
				if(cnt >= 1){
					K kl = k.getKByLLV(30);
					if(kl.dateEquals(k))return false;
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean run(K k) throws Exception {
		double amplitude = (k.getHigh()-k.getLow())/k.getLow();
		double amplitude2 = (k.getOpen()-k.getClose())/Math.min(k.getClose(), k.getOpen());
		/*if(k.getDate().equals("20170726")){
			System.out.println(k.getOpen()+","+k.getClose());
			System.out.println("amplitude="+amplitude+",amplitude2="+amplitude2+",changeOfClose="+k.getChangeOfClose());
		}*/
		if(amplitude > 0 && amplitude <= 0.025 && Math.abs(amplitude2) <= 0.0035 && k.getChangeOfClose() <= 0.02){
			K kv = k.getKByLVV(5);
			if(k.getDate().equals(kv.getDate())){
				return true;
			}
		}
		return false;
	}
	
	public boolean condition(Index index, String today) throws Exception{
		K k = index.getK(today);
		if(k == null) return false;
		
		int cnt = k.getKCountWithCondition(2, new K.Condition(){
			public boolean pass(K k) throws Exception {
				double ma5 = k.getMA(K.Close, 5);
				if(k.getClose() > ma5)return false;
				double ma20 = k.getMA(K.Close, 20)*1.03;
				if(k.getClose() > ma20)return false;
				if(Math.abs(k.getOpen()-k.getClose())/k.getOpen() <= 0.004 
						&& k.getHigh() >= k.getOpen() && k.getLow() <= k.getOpen()
						&& k.getHigh() >= k.getClose() && k.getLow() <= k.getClose()
						&& k.getHigh() != k.getLow()){
					return true;
				}
				return false;
			}
		});
		return cnt>=1?true:false;
	}

	public static void main(String[] args) throws Exception {
		Connection conn = Pool.getConn();
		Strategy20 s = new Strategy20();
		s.logToDB = false;
		Index index = new Index(conn, "002460");
		s.indexs.add(index);
		int cnt = 0;
		K k = index.getK(StkUtils.getToday());
		do{
			s.result.clear();
			s.run(conn, k.getDate());
			if(s.result.size()>0){
				System.out.println(k.getDate()+"-"+s.result);
			}
			k = k.before(1);
			if(cnt++ >= 200)break;
		}while(true);
		Pool.release(conn);
	}

}
