package com.stk123.bo.cust;

import com.stk123.bo.StkEarningsNotice;

public class StkEarningsNoticeCust extends StkEarningsNotice {
	
	private Double roe;
	private Double grossMargin;

	public Double getRoe() {
		return roe;
	}

	public void setRoe(Double roe) {
		this.roe = roe;
	}

	public Double getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(Double grossMargin) {
		this.grossMargin = grossMargin;
	}
	
	
}
