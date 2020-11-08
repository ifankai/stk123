package com.stk123.model;

import java.io.Serializable;

public class Ene implements Serializable {
	
	public final static int N = 10;
	public final static int M1 = 11;
	public final static int M2 = 9;
	
	private double ene;
	private double upper;
	private double lower;
	
	public double getEne() {
		return ene;
	}
	public void setEne(double ene) {
		this.ene = ene;
	}
	public double getUpper() {
		return upper;
	}
	public void setUpper(double upper) {
		this.upper = upper;
	}
	public double getLower() {
		return lower;
	}
	public void setLower(double lower) {
		this.lower = lower;
	}
	
	public String toString(){
		return "ene:"+ene+",upper:"+upper+",lower:"+lower;
	}
	
}

