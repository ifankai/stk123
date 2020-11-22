package com.stk123.task.quartz.job;

import com.stk123.util.ServiceUtils;

public class XueqiuUser {
	public String name;
	public String id;
	
	@Override
	public String toString() {
		return ServiceUtils.wrapLink(this.name, "http://xueqiu.com/"+this.id);
	}
	
}
