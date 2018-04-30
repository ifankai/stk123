package com.stk123.model;

public class Gap {
	
	private K startK;
	private K endK;
	
	private double low;
	private double high;
	
	private boolean isUp;
	
	public String toString(){
		return "isUp:"+isUp+",start date:"+startK.getDate()+",end date:"+endK.getDate()+",low:"+low+",high:"+high;
	}
	
	public K getStartK() {
		return startK;
	}
	public void setStartK(K startK) {
		this.startK = startK;
	}
	public K getEndK() {
		return endK;
	}
	public void setEndK(K endK) {
		this.endK = endK;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public boolean isUp() {
		return isUp;
	}
	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	
	
}
