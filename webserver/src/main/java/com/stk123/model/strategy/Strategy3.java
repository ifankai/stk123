package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stk123.model.Ene;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.tool.util.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy3 extends Strategy {
	
	@Override
	public void run(Connection conn, String today) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(index.isStop(today))continue;			
			K k = index.getK(today);
			if(k==null)continue;
			if(index.getTotalMarketValue() >= 300)continue;
			if(index.getStk()!=null && index.getStk().getTotalCapital() != null){
				if(index.getStk().getTotalCapital() /10000.0 >= 5)continue;
			}
			Ene ene = k.getEne();
			if(k.getLow() <= ene.getLower()){
				int cntUpper = 0;
				int cntLower = 0;
				List<K> ks = index.getKsHistoryHighAndLow(today, 50, 5);
				if(ks.size() >= 5){
					for(K kk : ks){
						ene = kk.getEne();
						if(kk.getLow() < ene.getLower()){
							cntLower ++;
						}
						if(kk.getHigh() > ene.getUpper()){
							cntUpper ++;
						}
					}
					if(cntLower >= 2 && cntUpper >= 2){
						results.add(index);
					}
				}
			}
		}
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			/*for(Index index : results){
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}
			System.out.println(StkUtils.join(results, ","));*/
			super.logStrategy(conn, today, "模型3-ENE活跃个股", results);
			EmailUtils.sendAndReport("模型3-ENE活跃个股,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	 

	}

}
