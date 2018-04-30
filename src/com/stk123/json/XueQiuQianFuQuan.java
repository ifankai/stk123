package com.stk123.json;

import java.util.List;
import java.util.Map;

public class XueQiuQianFuQuan {
	private List<Map<String,String>> chartlist;
	private String success;
	private String stock;

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public List<Map<String,String>> getChartlist() {
		return chartlist;
	}

	public void setChartlist(List<Map<String,String>> chartlist) {
		this.chartlist = chartlist;
	}
	
	public Map<String,String> get(String yyyyMMdd){
		int n = this.indexOf(yyyyMMdd);
		if(n == -1){
			return null;
		}else{
			return this.chartlist.get(n);
		}
	}
	
	public Map<String,String> get(int n){
		return this.chartlist.get(n);
	}
	
	public int indexOf(String yyyyMMdd){
		if(chartlist == null)return -1;
		for(int i=this.chartlist.size()-1;i>=0;i--){
			Map<String,String> data = this.chartlist.get(i);
			if(yyyyMMdd.equals(data.get("time"))){
				return i;
			}
		}
		return -1;
	}
}
