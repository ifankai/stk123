package com.stk123.util;

import java.sql.Connection;
import java.util.List;

import com.stk123.model.bo.Stk;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.JdbcUtils;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class TaUtils {

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String codes = "002433";
			String sql = null;
			if(codes != null && codes.length() > 0){
				sql = "select code,name from stk_cn where market=1 and code in ("+codes+") order by code";
			}else{
				sql = "select code,name from stk_cn where market=1 and code>=000000 order by code";
			}
			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
			
			Core lib = new Core();
			for(Stk stk : stks){
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				List<K> ks = index.getKs();
				
				
		        MInteger outBegIdx = new MInteger();
		        MInteger outNbElement = new MInteger();
		        outBegIdx.value = -1;
		    	outNbElement.value = -1;
		    	RetCode retCode = null;
		    	int lookback = 0;
		    	
		    	int x = 100;
		        
		        double macd[]   = new double[x];
		    	double signal[] = new double[x];
		    	double hist[]   = new double[x];
		    	
		    	double[] close = new double[x];
		    	//for( int i=99; i >= 0; i-- ){
		    	for( int i=0; i <x; i++ ){
		    		K k = ks.get(i);
		    		close[i] = k.getClose();
		    		System.out.println(close[i]);
		    	}
		    	/*lookback = lib.macdLookback(12,26,9);
		    	retCode = lib.macd(0,close.length-1,close,12,26,9,outBegIdx,outNbElement,macd,signal,hist);
		    	System.out.println(retCode);
		    	System.out.println(lookback);
		    	System.out.println(macd[0]);
				System.out.println(signal[0]);
				System.out.println(hist[0]);
				System.out.println(macd[x - 1 - lookback]);
				System.out.println(signal[x - 1 - lookback]);
				System.out.println(hist[x - 1 - lookback]);*/
				
				double ema15[] = new double[close.length];
		   	    lookback = lib.emaLookback(50);
		        retCode = lib.ema(0,close.length-1,close,50,outBegIdx,outNbElement,ema15);
		        System.out.println(lookback);
		        System.out.println(ema15[0]);
				System.out.println(ema15[11]);
				
				K k = index.getK();
				double e = k.getEMA(K.Close, 50);
				System.out.println(e);
				System.out.println(k.getMACD().macd);
			}
		}finally {
			if (conn != null) conn.close();
		}

	}

}
