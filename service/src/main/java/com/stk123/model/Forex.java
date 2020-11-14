package com.stk123.model;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.tags.TableTag;

import com.stk123.model.bo.po.StkKlinePO;
import com.stk123.service.ServiceUtils;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;


public class Forex {

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			initForex(conn,30);
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void initForex(Connection conn, int flag) throws Exception {
		Date today = ServiceUtils.now;
		Date stDate = ServiceUtils.addDay(today, -60);
		int cnt = 0;
		while(true){
			String body = "action=historical_data&curr_id=155&st_date="+URLEncoder.encode(ServiceUtils.formatDate(stDate, ServiceUtils.sf_ymd11), "utf-8")+"&end_date="+URLEncoder.encode(ServiceUtils.formatDate(today, ServiceUtils.sf_ymd11), "utf-8")+"&interval_sec=Daily";
			Map<String, String> requestHeaders = new HashMap<String, String>();
	    	requestHeaders.put("X-Requested-With","XMLHttpRequest");
			String page = HttpUtils.post("http://cn.investing.com/instruments/HistoricalDataAjax", null,body,requestHeaders, "utf-8", null);
			TableTag tab = (TableTag)HtmlUtils.getNodeByAttribute(page, null, "id", "curr_table");
			List<List<String>> datas = HtmlUtils.getListFromTable(tab);
			if(datas.size() < 5)break;
			for(List<String> data : datas){
				//System.out.println(data);
				if(data.size() < 5)continue;
				StkKlinePO kline = new StkKlinePO();
				kline.setCode("USDHKD");
				kline.setKlineDate(ServiceUtils.formatDate(data.get(0), ServiceUtils.sf_ymd10,ServiceUtils.sf_ymd2));
				kline.setOpen(Double.valueOf(data.get(2))*100);
				kline.setClose(Double.valueOf(data.get(1))*100);
				kline.setHigh(Double.valueOf(data.get(3))*100);
				kline.setLow(Double.valueOf(data.get(4))*100);
				kline.setCloseChange(Double.valueOf(StringUtils.replace(data.get(5), "%", "")));
				JdbcUtils.insertOrUpdate(conn, kline);
				cnt ++;
			}
			if(flag > 0 && cnt >= flag)break;
			today = ServiceUtils.addDay(today, -60);
			stDate = ServiceUtils.addDay(today, -60);
		}
	}

}
