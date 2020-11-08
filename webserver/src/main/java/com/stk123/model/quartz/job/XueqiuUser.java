package com.stk123.model.quartz.job;

import com.stk123.task.StkUtils;

public class XueqiuUser {
	public String name;
	public String id;
	
	@Override
	public String toString() {
		return StkUtils.wrapLink(this.name, "http://xueqiu.com/"+this.id);
	}
	
}
