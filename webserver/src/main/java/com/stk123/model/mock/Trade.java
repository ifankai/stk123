package com.stk123.model.mock;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.K.MACD;
import com.stk123.model.mock.Condition;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.web.StkConstant;

public class Trade {
	
	private K k;
	private Index index;
	
	public Trade(Index index, K k){
		this.k = k;
		this.index = index;
	}
	
	public Score getSellScore() throws Exception {
		Score score = new Score(0);
		return score;
	}
	
	public Score getBuyScore() throws Exception {
		Score score = new Score(0);
		TradeContext context = new TradeContext(index, k, score);
		
		if(context.have(C1000B)){
			context.run(C1001B).run(C1002B).run(C1003B).run(C1004B).run(C1005B).run(C1006B)
				   .run(C2001B).run(C2002B).run(C2003B).run(C2004B).run(C2005B).run(C2006B);
		}
		return score;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public final static Factor F1000B = new Factor(1, 1000,"二品抄底-红线上穿买");
	public final static Condition C1000B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			double red = k.getEpcd_Hxscm();
			double green = k.getEpcd_Hxxcm();
			
			K yk = k.before(1);
			double yred = yk.getEpcd_Hxscm();
			double ygreen = yk.getEpcd_Hxxcm();
			
			if(red >= green && yred < ygreen){
				return F1000B;
			}
			return null;
		}
	};
	
	public final static Factor F1001B = new Factor(1,1001,"二品抄底-趋势线在下面");
	public final static Condition C1001B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			double green = k.getEpcd_Hxxcm();
			double yellow = k.getEpcd_Qs();
			if(green >= yellow){
				return F1001B;
			}
			return null;
		}
	};

	public final static Factor F1002B = new Factor(1,1002,"量能大于5日均量");
	public final static Condition C1002B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			if(k.getVolumn() > k.getMA(K.Volumn, 5)){
				return F1002B;
			}
			return null;
		}
	};

	public final static Factor F1003B = new Factor(3,1003,"MACD底部背离");
	public final static Condition C1003B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();			
			if(k.getKByLLV(6).getDate().equals(k.getKByLLV(60).getDate())){
				List<K> lows = k.getHistoryLowPoint(60, 6);
				//System.out.println("lows=="+lows);
				for(K lowk : lows){
					if(k.before(6).getDate().compareTo(lowk.getDate()) > 0){
						//System.out.println("kkkkkk="+k.getLow()+(k.getLow() < lowk.getLow())+",lowk.getLow()="+lowk.getLow()+","+(macd.dif > lowk.getMACD().dif));
						//System.out.println("lowk="+lowk.getDate()+",macd.dif="+macd.dif+",="+lowk.getMACD().dif);
						if(k.getKByLLV(6).getLow() < lowk.getLow() && context.getMACD().dif > lowk.getMACD().dif){
							return F1003B;
						}
					}
				}
			}
			return null;
		}
	};

	public final static Factor F1004B = new Factor(1,1004,"MACD在0轴缠绕");
	public final static Condition C1004B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			int cnt = k.getKCountWithCondition(6, new K.Condition() {
				public boolean pass(K k) throws Exception {
					MACD macd = k.getMACD();
					return Math.abs(macd.macd) <= 0.3 && Math.abs(macd.dea) <= 0.3;
				}
			});
			if(cnt >= 6){
				return F1004B;
			}
			return null;
		}
	};

	public final static Factor F1005B = new Factor(2,1005,"MACD 0轴下面2次以上金叉");
	public final static Condition C1005B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			int cnt = k.getKCountWithCondition(20, new K.Condition() {
				public boolean pass(K k) throws Exception {
					MACD macd = k.getMACD();
					MACD ymacd = k.before(1).getMACD();
					return macd.dif < 0 && macd.dif > macd.dea && ymacd.dif < ymacd.dea;
				}
			});
			if(cnt >= 2){
				return F1005B;
			}
			return null;
		}
	};
	
	public final static Condition C1006B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			Index index = context.getIndex();
			Index tmp = new Index(index.getConnection(),index.getCode());
			K k = tmp.getK();
			if(k != null){
				Double d = tmp.getCapitalFlowPercent(k.getDate());
				if(d != null && d > 0){
					d = tmp.getCapitalFlowPercent(k.before(1).getDate());
					if(d > 0){
						return new Factor(2,1006,"资金连续流入"+d);
					}
					return new Factor(1,1006,"资金流入"+d);
				}
			}
			return null;
		}
	};
	
	//--------------------//
	public final static Factor F2001B = new Factor(-1,2001,"量能小于10日均量");
	public final static Condition C2001B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			if(k.getVolumn() < k.getMA(K.Volumn, 10)){
				return F2001B;
			}
			return null;
		}
	};

	public final static Factor F2002B = new Factor(-1,2002,"DIF为负且MACD小于DIF");
	public final static Condition C2002B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			MACD macd = context.getMACD();
			if(macd.dif < 0 && macd.macd <= macd.dif){
				return F2002B;
			}
			return null;
		}
	};

	public final static Factor F2003B = new Factor(-2,2003,"二品抄底-趋势线大于80");
	public final static Condition C2003B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			double qs = k.getEpcd_Qs();
			if(qs >= 80){
				return F2003B;
			}
			return null;
		}
	};

	public final static Factor F2004B = new Factor(-1,2004,"二品抄底-趋势线大于50");
	public final static Condition C2004B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			double qs = k.getEpcd_Qs();
			if(qs >= 50 && qs < 80){
				return F2004B;
			}
			return null;
		}
	};

	public final static Factor F2005B = new Factor(-1,2005,"60,30,20,10日均线空头排列");
	public final static Condition C2005B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			K yk = k.before(1);
			if(k.getMA(K.Close, 60) < yk.getMA(K.Close, 60) && k.getMA(K.Close, 60) > k.getMA(K.Close, 30) 
					&& k.getMA(K.Close, 30) > k.getMA(K.Close, 20) && k.getMA(K.Close, 20) > k.getMA(K.Close, 10)){
				return F2005B;
			}
			return null;
		}
	};

	public final static Factor F2006B = new Factor(-2,2006,"60日均线刚拐头向下");
	public final static Condition C2006B = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			K high20 = k;
			for(int i=1;i<=20;i++){
				if(k.getMA(K.Close, 60) < k.before(i).getMA(K.Close, 60)){
					high20 = k.before(i);
				}
			}
			K k_20 = k.before(20);
			if(k.getMA(K.Close, 60) < high20.getMA(K.Close, 60) && k_20.getMA(K.Close, 60) < high20.getMA(K.Close, 60)){
				return F2006B;
			}
			return null;
		}
	};
	
	
	/**
	 * sell
	 */
	public final static Factor F3000S = new Factor(1, 3000,"二品抄底-红线下穿卖");
	public final static Condition C3000S = new Condition() {
		public Factor execute(TradeContext context) throws Exception {
			K k = context.getK();
			double red = k.getEpcd_Hxscm();
			double green = k.getEpcd_Hxxcm();
			
			K yk = k.before(1);
			double yred = yk.getEpcd_Hxscm();
			double ygreen = yk.getEpcd_Hxxcm();
			
			if(red <= green && yred > ygreen){
				return F3000S;
			}
			return null;
		}
	};

}
