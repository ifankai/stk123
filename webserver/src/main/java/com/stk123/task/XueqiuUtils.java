package com.stk123.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JsonUtils;

public class XueqiuUtils {
	
	private static String cookies = null; //ConfigUtils.getProp("xueqiu.cookie"); 
	
	static{
		Thread my = new Thread() {
			@Override
			public void run() {
				while(true){
					if(StringUtils.isNotEmpty(cookies)){
						cookies = null;
					}
					try {
						Thread.sleep(1000 * 60 * 30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		my.setDaemon(true);
		my.start();
	}
	
	public static Map<String, String> getCookies(){
		Map<String, String> requestHeaders = new HashMap<String, String>();
		if(cookies == null){
			//cookies = login();
		}
		if(cookies == null || cookies.length() == 0){
			try {
				ConfigUtils.setConfigFile("D:/share/workspace/stk123/xueqiu.cookie.properties");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cookies = ConfigUtils.getProp("xueqiu.cookie");
			System.out.println("cookie="+cookies);
		}
    	requestHeaders.put("Cookie", cookies);
    	return requestHeaders;
	}
	
	/**
	 * @return Cookies
	 */
	private static String login(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "ifankai@126.com");
		params.put("areacode", "86");
		params.put("remember_me", "on");
		params.put("password", "FAEE62F3236431487F98D5DC9FCC578A");
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("X-Requested-With", "XMLHttpRequest");
		List<Header> respHeaders = new ArrayList<Header>();
		HttpUtils.post("https://xueqiu.com/user/login", params, requestHeaders, "UTF-8", respHeaders);
		Set<String> cookies = new HashSet<String>();
		for(Header hd : respHeaders){
			if("Set-Cookie".equals(hd.getName())){
				cookies.addAll(Arrays.asList(StringUtils.split(hd.getValue(), ";")));
			}
		}
		return StringUtils.replace(StringUtils.join(cookies, "; "), ".xueqiu.com;", "xueqiu.com;");
	}
	
	//https://xueqiu.com/v4/stock/portfolio/stocks.json?size=1000&pid=10&tuid=6237744859&pname=%E5%85%B3%E6%B3%A8C&uid=6237744859&category=2&type=2&_=1505130889888
	//https://xueqiu.com/v4/stock/portfolio/stocks.json?size=1000&pid=17&tuid=6237744859&pname=%E5%85%B3%E6%B3%A8H&uid=6237744859&category=2&type=2&_=1505131015277
	public static Set<String> getFollowStks(String tabName) throws Exception {
		if(Follows.get(tabName) != null){
			return Follows.get(tabName);
		}
        Set<String> careA = new LinkedHashSet<String>();
		Map<String, String> headerRequests = XueqiuUtils.getCookies();
		//System.out.println(headerRequests);
		//headerRequests.put("Content-Type", "application/json;charset=UTF-8");
    	String page = HttpUtils.get("https://xueqiu.com/stock/portfolio/stocks.json?size=1000&pid=10&tuid=6237744859", null, headerRequests, "GBK");
    	//System.out.println(page);
    	if("400".equals(page)){
    	    return Follows.put(tabName, careA);
        }

        Map<String, List> map = (Map) JsonUtils.testJson(page);
        //System.out.println(map.get("portfolios"));
        for(Object obj : map.get("portfolios")){
            Map care = (Map)obj;
            if(tabName.equals(care.get("name"))){
                for(String s : StringUtils.split((String)care.get("stocks"),",")){
                	careA.add(StringUtils.startsWithAny(s,new String[]{"SH","SZ"})?s.substring(2):s);
                };
            }
        }
        if(careA.size() > 0){
        	Follows.put(tabName, careA);
        }
        return careA;
    }
	
	//https://xueqiu.com/u/road_of_faith#/stock
	//https://stock.xueqiu.com/v5/stock/portfolio/stock/list.json?pid=-5&category=1&size=1000&uid=6237744859
	//{"data":{"pid":-5,"category":1,"stocks":[{"symbol":"SZ000526","name":"紫光学大","type":11,"remark":"","exchange":"SZ","created":1590697960528},{"symbol":"SH601288","name":"农业银行","type":11,"remark":"","exchange":"SH","created":1590462857107},{"symbol":"SH000902","name":"中证流通","type":12,"remark":"","exchange":"SH","created":1590060296825},{"symbol":"SH603589","name":"口子窖","type":11,"remark":"","exchange":"SH","created":1589462380128},{"symbol":"SZ002460","name":"赣锋锂业","type":11,"remark":"","exchange":"SZ","created":1384056375471},{"symbol":"SZ300124","name":"汇川技术","type":11,"remark":"","exchange":"SZ","created":1375090999580},{"symbol":"SH000001","name":"上证指数","type":12,"remark":"","exchange":"SH","created":1341237149686},...]},"error_code":0,"error_description":""}
	public static List<String> getFollowStks() throws Exception {
		Map<String, String> headerRequests = XueqiuUtils.getCookies();
		String page = HttpUtils.get("https://stock.xueqiu.com/v5/stock/portfolio/stock/list.json?pid=-5&category=1&size=1000&uid=6237744859", null, headerRequests, "UTF-8");
		
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("data", Map.class);
        m.put("stocks", Map.class);
        Map<String, Map> map = (Map) JsonUtils.getObject4Json(page, Map.class, m);
        Map<String, List> data = map.get("data");
		List<Map> stocks = data.get("stocks");
		//System.out.println(stocks);
		
		List<String> stks = new ArrayList<String>();
		for(Map<String,String> stock : stocks){
			String symbol = stock.get("symbol");
			String exchange = stock.get("exchange");
			stks.add(StringUtils.replace(symbol, exchange, ""));
		}
		System.out.println(StringUtils.join(stks, ","));
		FileUtils.writeStringToFile(new File("D:\\share\\workspace\\stk123\\mystocks.txt"), StringUtils.join(stks, ","));
		return stks;
	}
	
	private static Map<String,Set<String>> Follows = new HashMap<String, Set<String>>();
	
	public static boolean existingXueqiuFollowStk(String tabName, String code) {
		try {
			return getFollowStks(tabName).contains(code);
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void clearCookie(){
		cookies = null;
	}
	
	public static Set<String> getFollow() throws Exception {
		String page = HttpUtils.get("https://xueqiu.com/v4/stock/portfolio/stocks.json?size=1000&pid=10&tuid=6237744859&pname=%E5%85%B3%E6%B3%A8C&uid=6237744859&category=2&type=2&_=1505130889888", null, "GBK");
    	//System.out.println(page);
		Set<String> careA = new HashSet<String>();
        Map<String, List> map = (Map) JsonUtils.testJson(page);
        List<Map> stks = (List)map.get("stocks");
        for(Map s : stks){
        	careA.add((String)s.get("code"));
        };
        return careA;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*System.out.println("login="+login());
		Set<String> followStks = XueqiuUtils.getFollowStks("关注C");
		System.out.println(followStks);*/
		/*followStks.addAll(XueqiuUtils.getFollowStks("备选"));
		IOUtils.writeLines(followStks, null, new FileOutputStream(new File("d:\\care.txt")));*/
		//System.out.println(ConfigUtils.getProp("xueqiu.cookie"));
		System.out.println(XueqiuUtils.getFollowStks("全部"));

        System.out.println(XueqiuUtils.existingXueqiuFollowStk("全部","002191"));
		//XueqiuUtils.getCookies();
		//XueqiuUtils.getFollowStks();
	}

}
