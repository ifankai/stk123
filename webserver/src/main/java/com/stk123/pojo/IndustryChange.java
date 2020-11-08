package com.stk123.pojo;

import java.util.ArrayList;
import java.util.List;

import com.stk123.bo.StkIndustryType;


public class IndustryChange {
	
	private StkIndustryType industryType;
	private int period;
	private double closeChange;
	private List<StkChange> stks = new ArrayList<StkChange>();
	
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public double getCloseChange() {
		return closeChange;
	}
	public void setCloseChange(double closeChange) {
		this.closeChange = closeChange;
	}
	public List<StkChange> getStkChanges() {
		return stks;
	}
	public void addStkChange(StkChange stk){
		this.stks.add(stk);
	}
	/*public void setStks(List<Stk> stks) {
		this.stks = stks;
	}*/
	public StkIndustryType getIndustryType() {
		return industryType;
	}
	public void setIndustryType(StkIndustryType industryType) {
		this.industryType = industryType;
	}
}


