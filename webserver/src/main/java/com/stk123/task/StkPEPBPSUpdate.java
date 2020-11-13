package com.stk123.task;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stk123.model.bo.Stk;
import com.stk123.model.Index;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.EmailUtils;
import com.stk123.service.ExceptionUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;

public class StkPEPBPSUpdate {

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			//List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn where code='300208' order by code", Stk.class);
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				try{
					Map<String, String> requestHeaders = new HashMap<String, String>();
					//requestHeaders.put("Cookie", "__cfduid=d163814fe168b4b389c1fd15a4c94de401497098007; PHPSESSID=62eodgkj5517n12b6cd2hnbq82; cGS5_2132_saltkey=uk8MsT0h; cGS5_2132_lastvisit=1497094393; cGS5_2132_sendmail=1; cGS5_2132_seccode=499.b8a6639db5df8a6a93; cGS5_2132_ulastactivity=ad7dyz3l7awX%2FcYYF5UTNvG0Boa9Q%2BN5f%2Blht3Go2h0YQ%2B00g8Fe; cGS5_2132_auth=7994SgqS%2BzlDSEcVopLmTCP0n4R5j1NfhLqNI6RprUpEs4iDzfgZ9Bx%2FZ%2BZhIFAwgKKInJ%2FVAVeWbXc8mOl5cwx1; cGS5_2132_security_cookiereport=d650XxxdgUDa4dO7rug1bJxWnZdQ3phToTrBpT%2BGef8ouFxXl8mg; cGS5_2132_home_readfeed=1497098081; cGS5_2132_sid=IZ55pS; Hm_lvt_210e7fd46c913658d1ca5581797c34e3=1497097996; Hm_lpvt_210e7fd46c913658d1ca5581797c34e3=1497098113; cGS5_2132_lastact=1497098123%09historical_valuation_data.php%09");
					requestHeaders.put("X-Requested-With", "XMLHttpRequest");
					//果仁网
					String page = HttpUtils.post("https://guorn.com/stock/query", null,
							"{\"ticker\":[[\""+index.getCode()+"\",\"0.M.股票每日指标_市盈率.0\"]],\"index\":[],\"sector\":[],\"pool\":[],\"strategy\":[]}"
							, requestHeaders, "UTF-8", null);
					Map map = JsonUtils.testJson(page);
					Map data = (Map)((Map)map.get("data")).get("sheet_data");
					List meas_data = (List)data.get("meas_data");
					//System.out.println(meas_data.get(0));
					List date = (List)((Map)((List)data.get("row")).get(0)).get("data");
					List<String> list = (List)date.get(0);
					int k = 0;
					List values = (List)meas_data.get(0);
					Collections.reverse(values);
					Collections.reverse(list);
					for(String d : list){
						if(k >= 100)break;
						List params = new ArrayList();
						params.add(values.get(k++));
						params.add(index.getCode());
						params.add(d);
						JdbcUtils.update(conn,"update stk_kline set pe_ttm=? where code=? and kline_date=?",params);
					}
					
					page = HttpUtils.post("https://guorn.com/stock/query", null,
							"{\"ticker\":[[\""+index.getCode()+"\",\"0.M.股票每日指标_市净率B.0\"]],\"index\":[],\"sector\":[],\"pool\":[],\"strategy\":[]}"
							, requestHeaders, "UTF-8", null);
					map = JsonUtils.testJson(page);
					data = (Map)((Map)map.get("data")).get("sheet_data");
					meas_data = (List)data.get("meas_data");
					//System.out.println(meas_data.get(0));
					date = (List)((Map)((List)data.get("row")).get(0)).get("data");
					list = (List)date.get(0);
					k = 0;
					values = (List)meas_data.get(0);
					Collections.reverse(values);
					Collections.reverse(list);
					for(String d : list){
						if(k >= 100)break;
						List params = new ArrayList();
						params.add(values.get(k++));
						params.add(index.getCode());
						params.add(d);
						JdbcUtils.update(conn,"update stk_kline set pb_ttm=? where code=? and kline_date=?",params);
					}
					
					
					page = HttpUtils.post("https://guorn.com/stock/query", null,
							"{\"ticker\":[[\""+index.getCode()+"\",\"0.M.股票每日指标_市销率.0\"]],\"index\":[],\"sector\":[],\"pool\":[],\"strategy\":[]}"
							, requestHeaders, "UTF-8", null);
					map = JsonUtils.testJson(page);
					data = (Map)((Map)map.get("data")).get("sheet_data");
					meas_data = (List)data.get("meas_data");
					//System.out.println(meas_data.get(0));
					date = (List)((Map)((List)data.get("row")).get(0)).get("data");
					list = (List)date.get(0);
					k = 0;
					values = (List)meas_data.get(0);
					Collections.reverse(values);
					Collections.reverse(list);
					for(String d : list){
						if(k >= 100)break;
						List params = new ArrayList();
						params.add(values.get(k++));
						params.add(index.getCode());
						params.add(d);
						JdbcUtils.update(conn,"update stk_kline set ps_ttm=? where code=? and kline_date=?",params);
					}
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, e);
				}
			}
		}catch(Exception e){
			EmailUtils.send("StkPEPBPSUpdate Error", e);
		}finally{
			if (conn != null) conn.close();
		}

	}

}
