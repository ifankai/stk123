package com.stk123.web.core.config;

import java.util.HashMap;

public class ActionConfig {
	
	protected String path = null;
	protected String type = null;
	protected String form = null;
	protected String method = null;
	protected String forward = null;
	protected HashMap<String,ForwardConfig> forwards = new HashMap<String,ForwardConfig>();
	
	public void addForwardConfig(ForwardConfig forward){
		forwards.put(forward.getName(), forward);
	}
	
	public HashMap<String,ForwardConfig> getForwards(){
		return forwards;
	}
	
	public ForwardConfig getForwardConfig(String name){
		return forwards.get(name);
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	
}
