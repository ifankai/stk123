package com.stk123.web.action;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.StkPe;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.User;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.web.StkConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;

public class MainAction {
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return StkConstant.ACTION_404;
		}
		return StkConstant.ACTION_SUCC;
	}
	
	public void getDailyReportForUS() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = StkContext.getConnection();
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct b.kline_date \"date\",");
		sb.append("                b.kline_date ,");
		sb.append("                b.close dji,");
		sb.append("                a.result_1,a.result_2,a.result_3,a.result_4 ");
		sb.append("from stk_kline_us b ");
		sb.append("left join stk_daily_report_us a on b.kline_date = a.report_date ");
		sb.append("where b.code='.DJI' and b.kline_date>='20130520' order by b.kline_date asc ");
		//System.out.println(sb.toString());
		List<Map> pes = JdbcUtils.list2UpperKeyMap(conn, sb.toString());
		for(int i=0;i<pes.size();i++){
			Map<String,Object> map = pes.get(i);
			if(i > 0){
				for(String key : map.keySet()){
					if(map.get(key) == null){
						map.put(key, pes.get(i-1).get(key));
					}
				}
			}
		}
		String json = JsonUtils.getJsonString4JavaPOJO(pes, StkConstant.DATE_FORMAT_YYYY_MM_DD);
		sc.setResponse(json);
	}
	
	public void growthPE() throws Exception{
		StkContext sc = StkContext.getContext();
		Connection conn = StkContext.getConnection();
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct b.kline_date \"date\",");
		sb.append("                b.kline_date ,");
		sb.append("                a.average_pe \"pe\",");
		sb.append("                b.close \"value\",");
		sb.append("                a.bias,");
		sb.append("                a.ene_upper_cnt eneupper,");
		sb.append("                a.ene_lower_cnt enelower,");
		sb.append("                c.flow flowlarge,");
		sb.append("                d.flow flowsmall,");
		sb.append("                e.close sh180,");
		sb.append("                a.result_3,a.result_4,a.result_5,a.result_6 ");
		sb.append("from stk_kline b ");
		sb.append("left join stk_pe a on b.kline_date = a.report_date ");
		sb.append("left join (select round(avg(main_amount), 2) flow,flow_date from stk_capital_flow a,stk_cn b  ");
		sb.append("        where a.code=b.code and b.TOTAL_CAPITAL>=100000 and main_amount!=0 group by flow_date) c ");
		sb.append("     on b.kline_date=c.flow_date ");
		sb.append("left join (select round(avg(main_amount), 2) flow,flow_date from stk_capital_flow a,stk_cn b  ");
		sb.append("        where a.code=b.code and b.TOTAL_CAPITAL<=50000 and main_amount!=0 group by flow_date) d  ");
		sb.append("     on b.kline_date=d.flow_date ");
		sb.append("left join stk_kline e on b.kline_date=e.kline_date and e.code='01000010' ");
		sb.append("where b.code='01000905' and b.kline_date>='20130520' order by b.kline_date asc ");
		//System.out.println(sb.toString());
		List<Map> pes = JdbcUtils.list2UpperKeyMap(conn, sb.toString()/*"select distinct b.kline_date \"date\","
				+ "b.kline_date ,a.average_pe \"pe\",b.close \"value\","
				+ "a.bias,"
				+ "a.ene_upper_cnt eneupper,a.ene_lower_cnt enelower,c.flow flowlarge,d.flow flowsmall, e.close sh180 "
				+ "from stk_pe a, stk_kline b, stk_kline e,"
				//+ "(select round(avg(main_amount+super_large_amount),2) flow,flow_date from stk_capital_flow where main_amount!=0 and super_large_amount!=0 group by flow_date) c "
				//+ "(select round(avg(middle_amount+small_amount),2) flow,flow_date from stk_capital_flow where middle_amount!=0 and small_amount!=0 group by flow_date) c "
				//+ "(select round(avg(main_amount+super_large_amount),2) flow,flow_date,count(1) from stk_capital_flow a,stk_cn b where main_amount!=0 and super_large_amount!=0 and a.code = b.CODE and b.TOTAL_CAPITAL <= 30000 group by flow_date order by flow_date desc) c"
				+ "(select round(avg(main_amount+super_large_amount),2) flow,flow_date from stk_capital_flow a,stk_cn b where a.code=b.code and b.TOTAL_CAPITAL>=100000 and main_amount!=0 and super_large_amount!=0 group by flow_date) c,"
				+ "(select round(avg(main_amount+super_large_amount),2) flow,flow_date from stk_capital_flow a,stk_cn b where a.code=b.code and b.TOTAL_CAPITAL<=50000 and main_amount!=0 and super_large_amount!=0 group by flow_date) d"
				+ " where b.code='399905' and b.kline_date>='20130520' and a.report_date(+)=b.kline_date "
				+ " and b.kline_date=c.flow_date(+) "
				+ " and b.kline_date=d.flow_date(+) "
				+ " and b.kline_date=e.kline_date(+) and e.code='01000010' "
				+ " order by b.kline_date asc"*/);
		for(int i=0;i<pes.size();i++){
			Map<String,Object> map = pes.get(i);
			if(i > 0){
				for(String key : map.keySet()){
					if(map.get(key) == null){
						map.put(key, pes.get(i-1).get(key));
					}
				}
				/*if(map.get("PE") == null){
					map.put("PE", pes.get(i-1).get("PE"));
				}
				if(map.get("ENEUPPER") == null){
					map.put("ENEUPPER", pes.get(i-1).get("ENEUPPER"));
				}
				if(map.get("ENELOWER") == null){
					map.put("ENELOWER", pes.get(i-1).get("ENELOWER"));
				}
				if(map.get("FLOWLARGE") == null){
					map.put("FLOWLARGE", StkUtils.formatNumber(((BigDecimal)pes.get(i-1).get("FLOWLARGE")).doubleValue(), 2));
				}
				if(map.get("FLOWSMALL") == null){
					map.put("FLOWSMALL", StkUtils.formatNumber(((BigDecimal)pes.get(i-1).get("FLOWSMALL")).doubleValue(), 2));
				}*/
			}
			if("20130829".compareTo((String)map.get("KLINE_DATE")) >= 0){
				map.put("FLOWLARGE", BigDecimal.ZERO);
				map.put("FLOWSMALL", BigDecimal.ZERO);
			}
		}
		/*for(int i=0;i<5;i++){
			Map<String,Object> map = pes.get(pes.size()-1);
			Map<String,Object> newMap = new HashMap<String,Object>();
			for(String key : map.keySet()){
				newMap.put(key, map.get(key));
			}
			newMap.put("DATE", StkUtils.sf_ymd2.format(StkUtils.addDay(StkUtils.now, i+1)));
			pes.add(newMap);
		}*/
		String json = JsonUtils.getJsonString4JavaPOJO(pes, StkConstant.DATE_FORMAT_YYYY_MM_DD);
		//System.out.println(json);
		sc.setResponse(json);
	}
	
	private final static String DATE = "date";
	private final static String VALUE = "value";
	
	public void getStkKline() throws Exception{
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		String code = sc.getRequest().getParameter(StkConstant.PARAMETER_S);
		Index index = new Index(conn, code);
		List<K> ks = index.getKs();
		List<Map> result = new ArrayList<Map>();
		for(int i=ks.size()-1;i>=0;i--){
			K k = ks.get(i);
			Map m = new HashMap();
			m.put(DATE, k.getDate());
			m.put(VALUE, k.getClose());
			result.add(m);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(result,StkConstant.DATE_FORMAT_YYYY_MM_DD);
		//System.out.println(json);
		sc.setResponse(json);
		
	}
	
}
