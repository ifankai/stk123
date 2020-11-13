package com.stk123.common.weixin;

import java.net.URLEncoder;
import java.util.Date;

public class WeixinSearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void getWeixinNews(Date date,String searchwords) {
		String url = "http://weixin.sogou.com/weixin?type=2&ie=utf8&query="+URLEncoder.encode(searchwords)+"&tsn=3&ft=&et=&interation=&wxid=&usip=";
		//HttpUtils.get(url, enc)
	}

}
