package com.stk123.model;

import java.io.Serializable;

public class PE implements Serializable {
	
	private double pe;
	private double jlr;
	private String startQuarter;
	private String endQuarter;
	
	public double getJlr() {
		return jlr;
	}
	public void setJlr(double jlr) {
		this.jlr = jlr;
	}
	public String getStartQuarter() {
		return startQuarter;
	}
	public void setStartQuarter(String startQuarter) {
		this.startQuarter = startQuarter;
	}
	public String getEndQuarter() {
		return endQuarter;
	}
	public void setEndQuarter(String endQuarter) {
		this.endQuarter = endQuarter;
	}
	public double getPe() {
		return pe;
	}
	public void setPe(double pe) {
		this.pe = pe;
	}
}
