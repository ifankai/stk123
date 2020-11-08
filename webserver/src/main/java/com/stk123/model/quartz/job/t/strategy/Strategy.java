package com.stk123.model.quartz.job.t.strategy;

import org.apache.commons.lang.StringUtils;

import com.stk123.model.quartz.job.t.K;
import com.stk123.model.quartz.job.t.Share;
import com.stk123.model.quartz.job.t.TradeUtils;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.ChineseUtils;

public abstract class Strategy<T> {
	
	public String name;
	
	public abstract T run(Share share);
	
	public void sendMessage(Share share, String message){
		K k = share.getK();
		String leftPad = StringUtils.repeat("&nbsp;", 8-ChineseUtils.length(share.getName()));
		TradeUtils.info(leftPad+share.getName()+"["+share.getCode()+"]"+this.getName() + message +" K["+StkUtils.formatDate(k.getTime(), StkUtils.sf_ymd9)+"] 价格:"+k.getClose());
	}
	
	public void sendMessage(Share share){
		this.sendMessage(share, "");
	}
	
	public String getName(){
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	};
	
}
