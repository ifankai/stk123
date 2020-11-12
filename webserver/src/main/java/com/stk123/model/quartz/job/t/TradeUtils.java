package com.stk123.model.quartz.job.t;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;

import com.stk123.tool.util.StkUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.HttpUtils;

@CommonsLog
public class TradeUtils {
	
	public static void info(Object o){
		try {
			sendMessage(o.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(o);
	}
	
	public static void log(Object o){
		System.out.println(o);
	}
	
	public static String getLocation(String code){
		if(!StkUtils.isAllNumeric(code))return "";
		if(code.charAt(0) == '6'){
			return "sh";
		}else{
			return "sz";
		}
	}
	
	public static String getKsFromSina(List<Share> shares) throws Exception {
		List<String> codes = new ArrayList<String>();
		for(Share share : shares){
			codes.add(share.getLocationCode());
		}
		return HttpUtils.get("http://hq.sinajs.cn/list="+StringUtils.join(codes, ","), null);
	}
	
	public static String getKsFromSina(Share share) throws Exception {
		List<Share> shares = new ArrayList<Share>();
		shares.add(share);
		return getKsFromSina(shares);
	}
	
	public static void updateSinaData(List<Share> shares) throws Exception {
		String js = TradeUtils.getKsFromSina(shares);
		String[] data = js.split(";");
		
		Map<String,String> map = new HashMap();
		for(String sdata : data){
			String[] arr = sdata.split("=");
			if(arr.length >= 2){
				map.put(arr[0], arr[1]);
			}
		}
		
		for(Share share : shares){
			String key = "var hq_str_"+share.getLocationCode();
			String sdata = map.get(key);
			if(sdata != null){
				String s = StringUtils.substringBetween(sdata, "\"", "\"");
				//System.out.println(s);
				String[] ss = s.split(",");
				share.setName(ss[0]);
				
				K latestk = share.getK();
				Date d = StkUtils.sf_ymd9.parse(ss[30]+' '+ss[31]);
				double a = Double.parseDouble(ss[9]);
				int v = Integer.parseInt(ss[8]);
				if(latestk != null && latestk.getId() == -1){
					a -= latestk.getA();
					v -= latestk.getV();
					if(a == 0) continue;
				}else{
					a = 0;
					v = 0;
				}
				K k = new K();
				k.setClose(Double.parseDouble(ss[3]));
				k.setLastClose(Double.parseDouble(ss[2]));
				k.setVolume(v);
				k.setV(Integer.parseInt(ss[8]));
				k.setAmount(a);
				k.setA(Double.parseDouble(ss[9]));
				k.setTime(d);
				
				share.addK(k);
				
				ShortTrade.logK(share, k);
				//log("Update ["+share.getCode()+"] from Sinajs Finished.");
			}
		}
	}
	
	private static String URL_TEST = "localhost";
	private static String URL_PROD = "60.205.210.238";
	
	private static String getUrl(){
		if(ShortTrade.isTest){
			return URL_TEST;
		}else{
			return URL_PROD;
		}
	}
	
	public static void sendMessage(String message) throws UnsupportedEncodingException {
		String m = "[{name:"+ new Date().getTime() +",value:\""+ message +"\"}]";
		System.out.println("底部放量 - " + message);
		if(!ShortTrade.isTest){
			EmailUtils.send("底部放量 - "+message, message);
		}
		//HttpUtils.post("http://"+getUrl()+"/sync?method=add", "m="+URLEncoder.encode(m, "utf-8"), "utf-8");
	}
	
	public static void clearMessage() throws Exception {
		//HttpUtils.get("http://"+getUrl()+"/sync?method=clearMessage", null);
	}
	
	
	public static void main(String[] args) throws Exception {
		/*List<Share> shares = new ArrayList<Share>();
		shares.add(new Share("sh000001"));
		Utils.updateSinaData(shares);*/
		//Utils.sendMessage("隆华节能[300263]平台突破-Input [id=7, hous=2.5, timeAmountMultiple=name:180,value:name:1.5,value:2.5, rank=4, k= \\r K [id=3914, close=7.66, change=-0.06, time=2017-07-28 14:57:04, amount=1382912.0, a=0.0, volume=1790, v=0, flag=M, before=1]]");
		TradeUtils.clearMessage();
	}
}
