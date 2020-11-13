package com.stk123.task.quartz.job;

import org.apache.commons.lang.StringUtils;

import com.stk123.service.ServiceUtils;

public class XueqiuArticle {
	public XueqiuUser user;
	public String id;
	public long createAt;
	public String title;
	public int replyCount;
	public String description;
	public String code;
	
	public String toString(){
		if("-1".equals(user.id)){
			String scode = code;
			if(StringUtils.length(code) > 5){
				scode = ServiceUtils.getStkLocation(code)+code;
			}
			return "[" + ServiceUtils.wrapLink(user.name, "http://xueqiu.com/S/"+scode) + "]-" + ServiceUtils.wrapLink(title, "http://xueqiu.com/S/"+scode+"/"+id)+" ["+ServiceUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/S/"+scode+"/"+id)+"]";
		}
		return "[" + ServiceUtils.wrapLink(user.name, "http://xueqiu.com/"+user.id) + "]-" + ServiceUtils.wrapLink(title, "http://xueqiu.com/"+user.id+"/"+id)+" ["+ServiceUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/"+user.id+"/"+id)+"]";
	}
	
	public String titleToHtml(){
		return ServiceUtils.wrapLink(title, "http://xueqiu.com/"+user.id+"/"+id);
	}
	
	public String replyToHtml() {
		return ServiceUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/"+user.id+"/"+id);
	}
}
