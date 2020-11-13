package com.stk123.model.dto;

import com.stk123.model.bo.Stk;

public class StkChange {
	
	private Stk stk;
	private int period;
	private double closeChange;
	
	public Stk getStk() {
		return stk;
	}
	public void setStk(Stk stk) {
		this.stk = stk;
	}
	public double getCloseChange() {
		return closeChange;
	}
	public void setCloseChange(double closeChange) {
		this.closeChange = closeChange;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public String toString(){
		return "period:"+period+",stk:"+stk.getCode()+",closeChange:"+closeChange;
	}
}
