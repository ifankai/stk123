package com.stk123.web.core.config;

import java.io.Serializable;
import java.util.HashMap;

public class MvcConfig implements Serializable {
	
	protected String prefix = null;
	protected HashMap<String,ForwardConfig> forwards = new HashMap<String,ForwardConfig>();
	protected HashMap<String,ActionConfig> actions = new HashMap<String,ActionConfig>();
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public HashMap<String,ActionConfig> getActions() {
		return actions;
	}
	public void addActionConfig(ActionConfig action) {
		this.actions.put(action.getPath(), action);
	}
	public HashMap<String,ForwardConfig> getForwards() {
		return forwards;
	}
	public void addForward(ForwardConfig forward){
		this.forwards.put(forward.getName(), forward);
	}
}
