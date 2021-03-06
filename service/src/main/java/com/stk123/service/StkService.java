package com.stk123.service;

import com.stk123.common.CommonConstant;
import com.stk123.common.db.connection.Pool;
import com.stk123.common.util.CacheUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.bo.StkDictionary;
import com.stk123.model.bo.StkIndustryType;
import com.stk123.model.bo.StkMonitor;
import com.stk123.model.bo.cust.StkFnDataCust;
import com.stk123.util.ServiceUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StkService {
	
	public static List<StkDictionary> getColumnNames(int type){
		List<StkDictionary> list = null;
		list = (List<StkDictionary>)CacheUtils.getForever(CacheUtils.KEY_STKS_COLUMN_NAMES+type);
		//if(list == null){
			list = DictService.getDictionaryOrderByParam(type);
			CacheUtils.putForever(CacheUtils.KEY_STKS_COLUMN_NAMES+type, list);
		//}
		return list;
	}
	
	private final static String SQL_GET_UPDOWN_RANK = "select a.code from stk a, stk_kline b where a.code=b.code and b.kline_date = ? and b.percentage is not null order by b.percentage ";
	
	//stocks
	public List<String> getUpdownCodes(String asc) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			Index sh = new Index(conn, Index.SH999999);
			String date = sh.getK().getDate();
			List params = new ArrayList();
			params.add(date);
			String sql = JdbcUtils.DIALECT.getLimitedString(SQL_GET_UPDOWN_RANK + asc, 0, 30);
			return JdbcUtils.list(conn, sql, params, String.class);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public List<Map> getStksAsList(List<String> codes) throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<Map> list = new ArrayList<Map>();
			for(String code : codes){
				Index index = new Index(conn, code);
				//index.getFnTable(Fn.getFnTypesForDisplay(index.getMarket()), 2);
				Map map = new HashMap();
				/*map.put(StkConstant.JSON_CODE, index.getCode());
				map.put(StkConstant.JSON_NAME, index.getName());
				map.put(StkConstant.JSON_PERCENTAGE_UPDOWN, index.getK().getKline().getPercentage());
				map.put(StkConstant.JSON_MARKET_VALUE, StkUtils.formatNumber(index.getTotalMarketValue(),2));
				map.put(StkConstant.JSON_INDUSTRY, index.getIndustryDefault().getType().getName());
				map.put(StkConstant.JSON_PE_TTM, index.getPETTM());
				map.put(StkConstant.JSON_PB, StkUtils.formatNumber(index.getPB(),2));
				map.put(StkConstant.JSON_PS, StkUtils.formatNumber(index.getPS(),2));
				map.put(StkConstant.JSON_ROE, index.getROE());
				map.put(StkConstant.JSON_LAST_QUARTER_NET_PROFIT_MARGIN, StkUtils.formatNumber(index.getNetProfitGrowthLastestQuarter(),2));
				map.put(StkConstant.JSON_GROSS_MARGIN, index.getFnDataLastestByType(index.FN_GROSS_MARGIN).getFnValue());*/
				map.put(CommonConstant.JSON_NAME, ServiceUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
				map.put(CommonConstant.JSON_PERCENTAGE_UPDOWN, ServiceUtils.numberFormat2Digits(index.getK().getKline().getPercentage()));
				map.put(CommonConstant.JSON_MARKET_VALUE, ServiceUtils.number2String(index.getTotalMarketValue(),2));
				String industry = CommonConstant.NA;
				if(index.getIndustryDefault() != null){
					StkIndustryType indType = index.getIndustryDefault().getType();
					industry = ServiceUtils.wrapIndustryAsHtml(indType.getId(), indType.getName());
				}
				map.put(CommonConstant.JSON_INDUSTRY, industry);
				map.put(CommonConstant.JSON_PE_TTM, index.getK().getKline().getPeTtm());
				map.put(CommonConstant.JSON_PB, ServiceUtils.number2String(index.getPB(),2));
				map.put(CommonConstant.JSON_PS, index.getPSAsString());
				map.put(CommonConstant.JSON_ROE, ServiceUtils.numberFormat2Digits(index.getROE()));
				map.put(CommonConstant.JSON_LAST_QUARTER_NET_PROFIT_MARGIN, index.getNetProfitGrowthLastestQuarterAsString());
				StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
				map.put(CommonConstant.JSON_GROSS_MARGIN, fn==null?"--":ServiceUtils.numberFormat2Digits(fn.getFnValue()));
				list.add(map);
			}
			return list;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public List<Map> getStksMonitorAsList() throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<Map> list = new ArrayList<Map>();
			List<StkMonitor> ms = JdbcUtils.list(conn, "select * from stk_monitor where type=2", StkMonitor.class);
			for(StkMonitor care : ms){
				Index index = new Index(conn, care.getCode());
				Map map = new HashMap();
				map.put(CommonConstant.JSON_NAME, ServiceUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
				//map.put(StkConstant.JSON_MARKET_VALUE, StkUtils.formatNumber(index.getTotalMarketValue(),2));
				map.put("type", "<a target='_blank' href='"+care.getResult1()+"'>"+care.getParam1()+"</a>");
				map.put("date", ServiceUtils.formatDate(care.getParam3(),ServiceUtils.sf_ymd2, ServiceUtils.sf_ymd) );
				map.put("price", care.getParam2());
				//map.put("close", index.getK().getClose());
				map.put("refdate", ServiceUtils.formatDate(care.getParam4(),ServiceUtils.sf_ymd2, ServiceUtils.sf_ymd));
				map.put("rate", care.getParam5());
				list.add(map);
			}
			return list;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public List<List> getStkList2(List<Index> codes) throws Exception {
		List<List> list = new ArrayList<List>();
		for(Index index : codes){
			List row = new ArrayList();
			row.add(ServiceUtils.wrapCodeAsHtml(index.getCode()));
			row.add(index.getName());
			K k = index.getK();
			if(k != null && k.getKline() != null){
				row.add(k.getKline().getPercentage()==null?CommonConstant.MARK_DOUBLE_HYPHEN:ServiceUtils.numberFormat2Digits(k.getKline().getPercentage()));
			}else{
				row.add(CommonConstant.MARK_DOUBLE_HYPHEN);
			}
			row.add(ServiceUtils.number2String(index.getTotalMarketValue(),2));
			String industry = CommonConstant.NA;
			if(index.getIndustryDefault() != null){
				StkIndustryType indType = index.getIndustryDefault().getType();
				industry = ServiceUtils.wrapIndustryAsHtml(indType.getId(), indType.getName());
			}
			row.add(industry);
			if(k != null && k.getKline() != null){
				row.add(k.getKline().getPeTtm());
			}else{
				row.add(CommonConstant.MARK_DOUBLE_HYPHEN);
			}
			row.add(ServiceUtils.number2String(index.getPB(),2));
			row.add(index.getPSAsString());
			row.add(ServiceUtils.numberFormat2Digits(index.getROE()));
			row.add(index.getNetProfitGrowthLastestQuarterAsString());
			StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
			row.add(fn==null?CommonConstant.MARK_DOUBLE_HYPHEN:ServiceUtils.numberFormat2Digits(fn.getFnValue()));
			list.add(row);
		}
		return list;
	}
	
	/**
	 * @param from 1:来自涨跌幅，2:来自行业
	 */
	public List<List> getStkList(List<Index> codes, int from) throws Exception {
		List<List> list = new ArrayList<List>();
		for(Index index : codes){
			List row = new ArrayList();
			row.add(ServiceUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
			K k = index.getK();
			if(from == 1){
				if(k != null && k.getKline() != null){
					row.add(k.getKline().getPercentage()==null?CommonConstant.MARK_DOUBLE_HYPHEN:ServiceUtils.numberFormat2Digits(k.getKline().getPercentage()));
				}else{
					row.add(CommonConstant.MARK_DOUBLE_HYPHEN);
				}
			}
			row.add(ServiceUtils.number2String(index.getTotalMarketValue(),2));
			String industry = CommonConstant.NA;
			if(index.getIndustryDefault() != null){
				StkIndustryType indType = index.getIndustryDefault().getType();
				industry = ServiceUtils.wrapIndustryAsHtml(indType.getId(), indType.getName());
			}
			row.add(industry);
			if(k != null && k.getKline() != null){
				row.add(k.getKline().getPeTtm());
			}else{
				row.add(CommonConstant.MARK_DOUBLE_HYPHEN);
			}
			row.add(ServiceUtils.number2String(index.getPB(),2));
			row.add(index.getPSAsString());
			row.add(ServiceUtils.numberFormat2Digits(index.getROE()));
			row.add(index.getOperatingIncomeGrowthRateAsString());
			row.add(index.getNetProfitGrowthLastestQuarterAsString());
			StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
			row.add(fn==null?CommonConstant.MARK_DOUBLE_HYPHEN:fn.getFnValue());
			if(from == 2){
				List<String> ef = EarningsForecast.getEarningsForecastAsList(index.getCode());
				if(ef != null){
					row.add(ef.get(ef.size()-1));
				}else{
					row.add("0");
				}
			}
			list.add(row);
		}
		return list;
	}
	
	public String getStksAsJson(List<String> codes) throws Exception {
		return JsonUtils.getJsonString4JavaPOJO(this.getStksAsList(codes));
	}
	
}

