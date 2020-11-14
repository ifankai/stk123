package com.stk123.model.bo.po;

import java.util.ArrayList;
import java.util.List;

import com.stk123.model.bo.StkKline;


public class StkKlinePO extends StkKline {
	
	private List<String> pks = new ArrayList<String>();
	
	public StkKlinePO(){
		pks.add("code");
		pks.add("kline_date");
	}
	
	public List<String> getPks() {
		return pks;
	}
	public void setPks(List<String> pks) {
		this.pks = pks;
	}
	
	
}
