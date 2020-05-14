package com.stk123.model.quartz.job;

import org.apache.commons.lang.StringUtils;

import com.stk123.task.StkUtils;

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
				scode = StkUtils.getStkLocation(code)+code;
			}
			return "[" + StkUtils.wrapLink(user.name, "http://xueqiu.com/S/"+scode) + "]-" + StkUtils.wrapLink(title, "http://xueqiu.com/S/"+scode+"/"+id)+" ["+StkUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/S/"+scode+"/"+id)+"]";
		}
		return "[" + StkUtils.wrapLink(user.name, "http://xueqiu.com/"+user.id) + "]-" + StkUtils.wrapLink(title, "http://xueqiu.com/"+user.id+"/"+id)+" ["+StkUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/"+user.id+"/"+id)+"]";
	}
	
	public String titleToHtml(){
		return StkUtils.wrapLink(title, "http://xueqiu.com/"+user.id+"/"+id);
	}
	
	public String replyToHtml() {
		return StkUtils.wrapLink(String.valueOf(replyCount), "http://xueqiu.com/"+user.id+"/"+id);
	}
}
