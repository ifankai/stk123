package com.stk123.model.mock;

public class Factor {
	
	public int points = 0;
	public int code;
	public String reason;
	
	public Factor(int code, String reason){
		this.code = code;
		this.reason = reason;
	}
	
	public Factor(int points,int code, String reason){
		this.points = points;
		this.code = code;
		this.reason = reason;
	}
	
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String toString(){
		return "["+(points>0?"+"+points:points)+"]"+reason;
	}
}
