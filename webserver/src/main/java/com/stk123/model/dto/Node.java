package com.stk123.model.dto;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public String data;
	public String state;
	public MetaData attr;
	public List<Node> children = new ArrayList<Node>();
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public MetaData getAttr() {
		return attr;
	}
	public void setAttr(MetaData metadata) {
		this.attr = metadata;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}
	
}
