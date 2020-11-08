package com.stk123.model.quartz.job.t.strategy;

import java.util.ArrayList;
import java.util.List;

public class Output {

	private List<Input> inputs = new ArrayList<Input>();
	
	private Input current = null;
	private int maxRank = 0;
	
	public int countBigBuy = 0;
	
	public int strategy2 = 0;
	
	
	public boolean contain(Input input){
		return this.inputs.contains(input);
	}

	public Input getCurrentHappen() {
		return current;
	}

	public void setCurrentHappen(Input current) {
		if(maxRank == 0) {
			this.maxRank = current.getRank();
		}else{
			this.maxRank = this.maxRank > current.getRank()?this.maxRank:current.getRank();
		}
		this.current = current;
	}
	
	public void add(Input input){
		this.inputs.add(input);
	}

	public int getMaxRank() {
		return maxRank;
	}

	@Override
	public String toString() {
		return "Output [inputs=" + inputs + ", current=" + current + ", maxRank=" + maxRank + "]";
	}
	
	
}
