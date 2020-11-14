package com.stk123.model.mock;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.K.MACD;

public class TradeContext {
	
	private Index index;
	private K k;
	private Score score;
	
	private MACD macd = null;
	
	public TradeContext(Index index, K k, Score score){
		this.index = index;
		this.k = k;
		this.score = score;
	}
	
	public TradeContext run(Condition c) throws Exception {
		/*if(f.getCondition().execute(this)){
			score.add(f);
		}*/
		Factor f = c.execute(this);
		if(f != null){
			this.score.add(f);
		}
		return this;
	}
	
	public boolean have(Condition c) throws Exception {
		/*if(f.getCondition().execute(this)){
			score.add(f);
		}*/
		Factor f = c.execute(this);
		if(f != null){
			this.score.points += f.points;
			return true;
		}
		return false;
	}
	
	public MACD getMACD() throws Exception{
		if(macd == null){
			macd = k.getMACD();
		}
		return macd;
	}
	
	public Index getIndex() {
		return index;
	}
	public void setIndex(Index index) {
		this.index = index;
	}
	public K getK() {
		return k;
	}
	public void setK(K k) {
		this.k = k;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}
	
	
}
