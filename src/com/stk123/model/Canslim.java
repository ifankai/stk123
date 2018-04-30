package com.stk123.model;

import java.io.Serializable;

/**
 * CAN SLIM 欧奈尔
 *
 * N:股价强度
 */
public class Canslim implements Serializable {
	//N:股价强度
	private int closeChangeRank;
	private double closeChange;
	private int closeChangeRS;
	
	//jlr
	private int netProfitGrowthRank;
	private double netProfitGrowth;
	
	public int getNetProfitGrowthRank() {
		return netProfitGrowthRank;
	}
	public void setNetProfitGrowthRank(int netProfitGrowthRank) {
		this.netProfitGrowthRank = netProfitGrowthRank;
	}
	public double getNetProfitGrowth() {
		return netProfitGrowth;
	}
	public void setNetProfitGrowth(double netProfitGrowth) {
		this.netProfitGrowth = netProfitGrowth;
	}
	public int getCloseChangeRank() {
		return closeChangeRank;
	}
	public void setCloseChangeRank(int closeChangeRank) {
		this.closeChangeRank = closeChangeRank;
	}
	public double getCloseChange() {
		return closeChange;
	}
	public void setCloseChange(double closeChange) {
		this.closeChange = closeChange;
	}
	public int getCloseChangeRS() {
		return closeChangeRS;
	}
	public void setCloseChangeRS(int closeChangeRS) {
		this.closeChangeRS = closeChangeRS;
	}
}
