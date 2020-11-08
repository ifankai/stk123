package com.stk123.tool.weixin;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import com.stk123.tool.util.HttpUtils;

public class WeixinSearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void getWeixinNews(Date date,String searchwords) {
		String url = "http://weixin.sogou.com/weixin?type=2&ie=utf8&query="+URLEncoder.encode(searchwords)+"&tsn=3&ft=&et=&interation=&wxid=&usip=";
		//HttpUtils.get(url, enc)
	}

}
