package com.stk123.model.quartz.job;

import com.stk123.task.StkUtils;

public class XueqiuArticle {
	public XueqiuUser user;
	public String id;
	public long createAt;
	public String title;
	public int replyCount;
	public String description;
	
	public String toString(){
		return "[" + StkUtils.wrapLink(user.name, "http://xueqiu.com/"+user.id) + "]-" + StkUtils.wrapLink(title, "http://xueqiu.com/"+user.id+"/"+id)+" ["+StkUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/"+user.id+"/"+id)+"]";
	}
	
	public String titleToHtml(){
		return StkUtils.wrapLink(title, "http://xueqiu.com/"+user.id+"/"+id);
	}
	
	public String replyToHtml() {
		return StkUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/"+user.id+"/"+id);
	}
}
