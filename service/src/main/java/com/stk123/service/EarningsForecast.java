package com.stk123.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stk123.common.util.*;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.TableTag;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkDictionary;
import com.stk123.model.Index;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.CommonConstant;

public class EarningsForecast {

	public static void main(String[] args) throws Exception {
		System.out.println(EarningsForecast.class.getName());
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		List<Stk> growth = new ArrayList<Stk>();
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			List params = new ArrayList();
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				try{
					String page = HttpUtils.get("http://data.eastmoney.com/bbsj/"+stk.getCode()+".html", "GBK");
					TableTag node = HtmlUtils.getTableNodeByText(page, null, "业绩变动幅度");
					if(node == null)continue;
					List<List<String>> datas = HtmlUtils.getListFromTable(node);
					//System.out.println(datas);
					Node node4 = HtmlUtils.getNodeByAttribute(page, null, "id", "Table4");
					if(node4 == null)continue;
					List<List<String>> datas4 = HtmlUtils.getListFromTable((TableTag)node4, 0);
					//System.out.println(datas4);
					
					for(List<String> data : datas){
						if(data.size() >= 5){
							String er = data.get(3);
							double value = ServiceUtils.percentigeGreatThan(er);
							String content = JsonUtils.getJsonString4JavaPOJO(data);
							params.clear();
							params.add(content);
							params.add(value);
							params.add(stk.getCode());
							params.add(content);
							int n = JdbcUtils.update(conn, "update stk set next_quarter_earning=?,next_earning=? where code=? and (next_quarter_earning is null or next_quarter_earning != ?)", params);
							if(n > 0){
								if(value >= 100){
									stk.setNextQuarterEarning(data.toString());
									stk.setEarningExpect(String.valueOf(value));
									growth.add(stk);
								}
							}
							break;
						}
					}
					
					for(List<String> data : datas){
						if(data.size() >= 5){
							String er = data.get(3);
							Double erLow = null;
							Double erHigh = null;
							if(!"-".equals(er)){
								List<String> ers = ServiceUtils.getMatchAllStrings(er, "-?\\d+(\\.\\d+)?%?");
								if(ers.size() == 1){
									erLow = Double.parseDouble(StringUtils.replace(ers.get(0),"%",""));
									erHigh = erLow;
								}else if(ers.size() == 2){
									erLow = Double.parseDouble(StringUtils.replace(ers.get(0),"%",""));
									erHigh = Double.parseDouble(StringUtils.replace(ers.get(1),"%",""));;
								}
							}
							String fnDate = StringUtils.replace(data.get(0), "-", "");
							
							
							params.clear();
							params.add(stk.getCode());
							params.add(fnDate);
							params.add(data.get(1));
							params.add(erLow);
							params.add(erHigh);
							params.add(data.get(5));
							String amount = "-".equals(data.get(6))?null:data.get(6);
							int n = 1;
							if(StringUtils.contains(amount, "亿")){
								n = 10000;
								amount = StringUtils.replace(amount, "亿", "");
							}
							amount = StringUtils.replace(amount, "万", "");
							params.add(amount==null?null:Double.parseDouble(amount) * n);
							params.add(StringUtils.replace(data.get(7), "-", ""));
							
							params.add(stk.getCode());
							params.add(fnDate);
							JdbcUtils.insert(conn, "insert into stk_earnings_notice select ?,?,?,?,?,?,?,?,null,sysdate from dual where not exists (select 1 from stk_earnings_notice where code=? and fn_date=?)", params);
							
							String realDate = null;
							for(List<String> data4 : datas4){
								if(fnDate.equals(StringUtils.replace(data4.get(0), "-", ""))){
									if(!"-".equals(data4.get(5))){
										realDate = StringUtils.replace(data4.get(5), "-", "");
										break;
									}
									if(!"-".equals(data4.get(4))){
										realDate = StringUtils.replace(data4.get(4), "-", "");
										break;
									}
									if(!"-".equals(data4.get(3))){
										realDate = StringUtils.replace(data4.get(3), "-", "");
										break;
									}
									if(!"-".equals(data4.get(2))){
										realDate = StringUtils.replace(data4.get(2), "-", "");
										break;
									}
									if(!"-".equals(data4.get(1))){
										realDate = StringUtils.replace(data4.get(1), "-", "");
										break;
									}
								}
							}
							if(realDate != null){
								params.clear();
								params.add(erLow);
								params.add(erHigh);
								params.add(realDate);
								params.add(stk.getCode());
								params.add(fnDate);
								
								JdbcUtils.update(conn, "update stk_earnings_notice set er_low=?,er_high=?,real_date=? where code=? and fn_date=?", params);
							}
						}
						
					}
				}catch(Exception e){
					e.printStackTrace();
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					Thread.sleep(1000 * 180);
					//throw e;
				}
			}
			/*if(growth.size() > 0){
				Collections.sort(growth, new Comparator<Stk>(){
					public int compare(Stk arg0, Stk arg1) {
						return (int)(Double.parseDouble(arg1.getEarningExpect()) - Double.parseDouble(arg0.getEarningExpect()));
					}
				});
				
				List<Index> indexs = new ArrayList<Index>();
				StringBuffer sb = new StringBuffer();
				for(Stk stk : growth){
					Index index = new Index(conn, stk.getCode());
					sb.append(stk.getName()+"["+stk.getCode()+"]"+stk.getNextQuarterEarning()).append("<br>");
					List<String> ef = index.getEarningsForecastAsList();
					if(ef != null && ef.size() >=3){
						Text.insert(conn, stk.getCode(), "[盈利预期] ["+
								StkUtils.numberFormat(index.getFnDataLastestByType(StkConstant.FN_TYPE_CN_JLRZZL).getFnValue(), 2) + "] " +
								ef.get(0)+", "+ef.get(2)+", "+ef.get(1), Text.SUB_TYPE_EARNING_FORECAST);
					}
					indexs.add(index);
				}
				EmailUtils.send("下个季度盈利预期数据，大于100%",StkUtils.createHtmlTable(StkUtils.getToday(), indexs));
			}*/
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static List<String> getEarningsForecastAsList(String code) throws Exception {
		int year = ServiceUtils.YEAR;
		String sql = "select distinct ef.code code,"
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year-1)+"),0) \"a_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year-1)+"),0) \"a_pe\","
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year)+"),0) \"b_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year)+"),0) \"b_pe\","
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+1)+"),0) \"c_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+1)+"),0) \"c_pe\","
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+2)+"),0) \"d_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+2)+"),0) \"d_pe\""
				+ " from stk_earnings_forecast ef where forecast_net_profit>0 and ef.code=?";
		//System.out.println(sql);
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List params = new ArrayList();
			params.add(code);
			Map m = JdbcUtils.load2Map(conn, sql, params);
			if(m == null)return null;
			Index index = new Index(conn, String.valueOf(m.get("code")));
			List<String> l = new ArrayList<String>();
			Double anp = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("a_np"))),2);
			Double ape = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("a_pe"))),2);
			Double bnp = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("b_np"))),2);
			Double bpe = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("b_pe"))),2);
			Double cnp = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("c_np"))),2);
			Double cpe = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("c_pe"))),2);
			Double dnp = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("d_np"))),2);
			Double dpe = ServiceUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("d_pe"))),2);
			
			/*//去年
			l.add(String.valueOf(anp));
			l.add(String.valueOf(ape));
			//去年PEG(2年复合增长率)
			double cagr = StkUtils.calcCAGR(anp, cnp, 2);
			if(cagr == 0){
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(format(ape/cagr));
			}
			//去年PEG(3年复合增长率)
			cagr = StkUtils.calcCAGR(anp, dnp, 3);
			if(cagr == 0){
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(format(ape/cagr));
			}
			
			//今年
			l.add(String.valueOf(bnp));
			l.add(String.valueOf(bpe));
			if(anp == 0){
				l.add(StkConstant.MARK_HYPHEN);
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(format((bnp-anp)/anp*100)+"%");
				//今年PEG(2年复合增长率)
				cagr = StkUtils.calcCAGR(bnp, dnp, 2);
				if(cagr == 0){
					l.add(StkConstant.MARK_HYPHEN);
				}else{
					l.add(format(bpe/cagr));
				}
			}
			
			//明年
			l.add(String.valueOf(cnp));
			if(bnp == 0){
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(format((cnp-bnp)/bnp*100)+"%");
			}
			
			//后年
			l.add(String.valueOf(dnp));
			if(cnp == 0){
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(format((dnp-cnp)/cnp*100)+"%");
			}
			
			//明后年增速
			if(cnp == 0 || bnp == 0){
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(format(((cnp-bnp)/bnp*100 + (dnp-cnp)/cnp*100)/2)+"%");
			}*/
			return calculation(l, anp, ape, bnp, bpe, cnp, cpe, dnp, dpe);
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static List<String> calculation(List<String> l, Double anp, Double ape,
			Double bnp, Double bpe, Double cnp, Double cpe, Double dnp, Double dpe){
		//去年
		l.add(String.valueOf(anp));
		l.add(String.valueOf(ape));
		//去年PEG(2年复合增长率)
		double cagr = ServiceUtils.calcCAGR(anp, cnp, 2);
		if(cagr == 0){
			l.add(CommonConstant.MARK_HYPHEN);
		}else{
			l.add(format(ape/cagr));
		}
		//去年PEG(3年复合增长率)
		cagr = ServiceUtils.calcCAGR(anp, dnp, 3);
		if(cagr == 0){
			l.add(CommonConstant.MARK_HYPHEN);
		}else{
			l.add(format(ape/cagr));
		}
		
		//今年
		l.add(String.valueOf(bnp));
		l.add(String.valueOf(bpe));
		if(anp == 0){
			l.add(CommonConstant.MARK_HYPHEN);
			l.add(CommonConstant.MARK_HYPHEN);
		}else{
			l.add(format((bnp-anp)/anp*100)+"%");
			//今年PEG(2年复合增长率)
			cagr = ServiceUtils.calcCAGR(bnp, dnp, 2);
			if(cagr == 0){
				l.add(CommonConstant.MARK_HYPHEN);
			}else{
				l.add(format(bpe/cagr));
			}
		}
		
		//明年
		l.add(String.valueOf(cnp));
		if(bnp == 0){
			l.add(CommonConstant.MARK_HYPHEN);
		}else{
			l.add(format((cnp-bnp)/bnp*100)+"%");
		}
		
		//后年
		l.add(String.valueOf(dnp));
		if(cnp == 0){
			l.add(CommonConstant.MARK_HYPHEN);
		}else{
			l.add(format((dnp-cnp)/cnp*100)+"%");
		}
		
		//明后年增速
		if(cnp == 0 || bnp == 0){
			l.add(CommonConstant.MARK_HYPHEN);
		}else{
			l.add(format(((cnp-bnp)/bnp*100 + (dnp-cnp)/cnp*100)/2)+"%");
		}
		return l;
	}

	public static String getEarningsForecast(String code) throws Exception {
		List<String> list = getEarningsForecastAsList(code);
		if(list == null)return "";
		List<StkDictionary> dicts = StkService.getColumnNames(1003);
		List<String> cn = new ArrayList<String>();
		for(StkDictionary dict : dicts){
			if(dict.getKey().equals("name"))continue;
			cn.add(dict.getText());
		}
		return ServiceUtils.createHtmlTableOneLine(cn, list);
	}
	
	public static String format(Double d){
		if(d.isInfinite()){
			return CommonConstant.MARK_HYPHEN;
		}else{
			return ServiceUtils.number2String(d,2);
		}
	}
}
