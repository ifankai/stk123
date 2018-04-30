package com.stk123.model.strategy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.task.InitialKLine;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;

public class Strategy13 extends Strategy {

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
			super.logStrategy(conn, today, "ģ��13-30���ڳ���������ص���20�����·��ҳ��ֶ�Ʒ����", results);
			EmailUtils.sendAndReport("ģ��13-30���ڳ���������ص���20�����·��ҳ��ֶ�Ʒ����,������"+results.size()+",����:"+today,
					"���䰸�������ؾ���[300176]-2017/02/07�������е[600984]-2017/03/15<br>"+
					StkUtils.createHtmlTable(today, results));
		}

	}
	
	public static boolean condition(K k) throws Exception{
		K k750 = k.getKByHVV(750);
		K k30 = k.getKByHVV(30);
		
		if(k750.getDate().equals(k30.getDate())
				&& k.getClose() <= k.getMA(K.Close, 20)
				&& k.getMA(K.Close, 60) > k.before(1).getMA(K.Close, 60)){
			
			double d = InitialKLine.maiRuShiJi(k);
			double yd = InitialKLine.maiRuShiJi(k.before(1));;
			if(d < 5 && d - yd >= 3){
				//System.out.println(k.getDate()+"="+d);
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		
	}

}
