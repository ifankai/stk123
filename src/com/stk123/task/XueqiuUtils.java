package com.stk123.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JsonUtils;


public class XueqiuUtils {
	
	private static String cookies = null; 
	
	public static Map<String, String> getCookies(){
		Map<String, String> requestHeaders = new HashMap<String, String>();
		if(cookies == null){
			cookies = login();
		}
		if(cookies == null || cookies.length() == 0){
			cookies = "aliyungf_tc=AQAAAChdej+MRAcAyEzEKlewsV8BhWiO; xq_a_token.sig=F2iHnlcpCSXgutP8euxdQqDfqq4; xq_r_token.sig=ZcCuq7XTdkGNIafT5ot8irXZzCU; device_id=9e72888b69a1db66c7db8e62c4be5508; s=g012xghag5; bid=26948a7b701285b58366203fbc172ea6_j7fn1q7b; webp=0; snbim_minify=true; __utmt=1; __utma=1.785228432.1505102053.1505126378.1505130297.3; __utmb=1.10.8.1505130359323; __utmc=1; __utmz=1.1505102053.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); captcha_id=WTvEoVxjJGDv6LYrpLfskEMPrAoAvK; xq_a_token=ef5544a2ab4f023740a43b9c7536f2c3869b4838; xqat=ef5544a2ab4f023740a43b9c7536f2c3869b4838; xq_r_token=dbcdb8706fd556ed2172ac5f89dcec581cf1c893; xq_is_login=1; u=6237744859; xq_token_expire=Fri%20Oct%2006%202017%2019%3A49%3A51%20GMT%2B0800%20(CST); Hm_lvt_1db88642e346389874251b5a1eded6e3=1504942936,1505028862,1505051594,1505102153; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1505130466";
			cookies = "device_id=7e03b129e635a880b2f9c41bf47b6694; s=fx121la7of; bid=26948a7b701285b58366203fbc172ea6_j9krmr16; webp=0; remember=1; remember.sig=K4F3faYzmVuqC0iXIERCQf55g2Y; xq_a_token=2f5a9521c01d0578d5dc96b1b038fbe25fe5395b; xq_a_token.sig=MgfmjCDlb1-oCwr-4lgD53wGH10; xq_r_token=4d680444ea729a36dcce2cbc7674e759dca02eee; xq_r_token.sig=1wKFZlyZ9Mvl0tIl0Mp6c_bqxRI; xq_is_login=1; xq_is_login.sig=J3LxgPVPUzbBg3Kee_PquUfih7Q; u=6237744859; u.sig=jFt0sGgsXMOKMFnjEVjqQCy_ido; aliyungf_tc=AQAAAJbZk0Ir+ggAEWJw3KHMlTTT9njE; Hm_lvt_1db88642e346389874251b5a1eded6e3=1521465684,1523248547; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1523248547; __utmt=1; __utma=1.1695750544.1509767735.1523086188.1523248548.104; __utmb=1.1.10.1523248548; __utmc=1; __utmz=1.1516714240.65.3.utmcsr=localhost|utmccn=(referral)|utmcmd=referral|utmcct=/stk; snbim_minify=true";
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
		Map<String, String> headerRequests = XueqiuUtils.getCookies();
		//System.out.println(headerRequests);
		//headerRequests.put("Content-Type", "application/json;charset=UTF-8");
    	String page = HttpUtils.get("https://xueqiu.com/stock/portfolio/stocks.json?size=1000&pid=10&tuid=6237744859", null, headerRequests, "GBK");
    	//System.out.println(page);
    	Set<String> careA = new HashSet<String>();
        Map<String, List> map = (Map) JsonUtils.testJson(page);
        //System.out.println(map.get("portfolios"));
        for(Object obj : map.get("portfolios")){
            Map care = (Map)obj;
            if(tabName.equals(care.get("name"))){
                for(String s : StringUtils.split((String)care.get("stocks"),",")){
                	careA.add(s.substring(2));
                };
            }
        }
        if(careA.size() > 0){
        	Follows.put(tabName, careA);
        }
        return careA;
    }
	
	private static Map<String,Set<String>> Follows = new HashMap<String, Set<String>>();
	
	public static boolean existingXueqiuFollowStk(String tabName, String code) {
		Set<String> set = null;
		try {
			if(set == null){
				set = getFollowStks(tabName);
			}
		} catch (Exception e) {
			set = new HashSet();
			return false;
		}
		return set.contains(code);
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
		//System.out.println(login());
		Set<String> followStks = XueqiuUtils.getFollowStks("¹Ø×¢C");
		System.out.println(followStks);
		followStks.addAll(XueqiuUtils.getFollowStks("±¸Ñ¡"));
		IOUtils.writeLines(followStks, null, new FileOutputStream(new File("d:\\care.txt")));
	}

}
