package com.stk123.tool.util.collection;

public class IntRange {
	private int lower;
	private int upper;
	
	public IntRange(int lower, int upper){
		this.lower = lower;
		this.upper = upper;
	}

	public int getLower() {
		return lower;
	}

	public void setLower(int lower) {
		this.lower = lower;
	}

	public int getUpper() {
		return upper;
	}

	public void setUpper(int upper) {
		this.upper = upper;
	}
	
}
