package com.stk123.web.pojo;

import com.stk123.common.CommonConstant;

public class MetaData {
	public String id;
	//public String title;
	public String isLeaf = CommonConstant.YES_N;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/*public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}*/
	public String getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	
}
