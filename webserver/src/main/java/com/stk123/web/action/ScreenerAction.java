package com.stk123.web.action;

import java.sql.Connection;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.stk123.model.Index;
import org.apache.commons.lang.StringUtils;

import com.stk123.model.bo.StkIndustryType;
import com.stk123.model.bo.StkSearchCondition;
import com.stk123.model.bo.StkSearchMview;
import com.stk123.model.bo.StkStrategy;
import com.stk123.model.bo.StkUsSearchView;
import com.stk123.model.IndexUtils;
import com.stk123.model.User;
import com.stk123.task.strategy.Strategy;
import com.stk123.task.strategy.StrategyManager;
import com.stk123.util.ServiceUtils;
import com.stk123.util.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.util.collection.Name2IntegerMap;
import com.stk123.common.util.collection.Name2Value;
import com.stk123.common.CommonConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;
import com.stk123.web.form.ScreenerForm;
import com.stk123.common.util.Arrays;

public class ScreenerAction implements CommonConstant {
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return CommonConstant.ACTION_404;
		}
		//Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String from = request.getParameter("from");	
		
		return from;
	}
	
	public String formToSql(Connection conn, ScreenerForm form, String cols) {
		StringBuffer sql = new StringBuffer();		
		sql.append("select "+(cols==null?"*":cols)+" from stk_search_mview v ");
		sql.append("where 1=1 ");
		//sql.append(" and v.code='IRBT' ");	
		addWhereClauseCodes(sql, form);
		addWhereClausePE(sql, form);
		addWhereClausePB(sql, form);
		addWhereClausePS(sql, form);
		addWhereClauseRevenueGrowthRate(sql, form);
		addWhereClauseNetProfitGrowthRate(sql, form);
		addWhereClauseGrossProfitMargin(sql, form);
		addWhereClauseListingDays(sql, form);
		addWhereClauseErLowAndHigh(sql, form);
		addWhereClauseErPe(sql, form);
		addWhereClauseMarketCap(sql, form);
		addWhereClauseDebtRate(sql, form);
		addWhereClauseIndustry(sql, form);
		addWhereClauseStrategy(conn, sql, form);
		addWhereClauseComment(sql, form);
		
		return sql.toString();
	}
	
	public void searchForCN() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		ScreenerForm form = (ScreenerForm)sc.getForm();
		
		String sql = formToSql(conn, form, null);
		
		System.out.println(sql);
		List<StkSearchMview> list = JdbcUtils.list(conn, sql.toString(), StkSearchMview.class);
		
		List<List> result = new ArrayList<List>();
		Name2IntegerMap<String,Integer> indAnaylse = new Name2IntegerMap<String,Integer>();
		Name2IntegerMap<String,Integer> indAnaylse2 = new Name2IntegerMap<String,Integer>();
		List<String> codes = new ArrayList<String>();
		for(StkSearchMview view : list){
			try{
				List row = new ArrayList();
				codes.add(view.getCode());
				row.add(ServiceUtils.wrapCodeAndNameAsHtml2(view.getCode(),view.getName()));
				if(view.getMarket() == 3){
					result.add(row);
					continue;
				}
				row.add(ServiceUtils.nvl(StringUtils.substring(view.getMyIndustry(), 0, 1), MARK_HYPHEN));
				//row.add(view.getListingDays());
				row.add(display(view.getIndustry()));
				
				//industry 分析，看集中在哪个行业
				if(view.getIndustry() != null){
					String[] inds = view.getIndustry().split(",");
					for(String ind : inds){
						indAnaylse.add(ind);
					}
				}
				if(view.getMainIndustry() != null){
					String[] inds = view.getMainIndustry().split(",");
					for(String ind : inds){
						indAnaylse2.add(ind);
					}
				}
				
				row.add(ServiceUtils.numberFormat0Digits(view.getMarketCap()));
				row.add(ServiceUtils.numberFormat2Digits(view.getRoe()));
				
				row.add(formatDate(view.getErDate()));
				//业绩预告
				if(view.getErLow() == null){
					row.add(MARK_HYPHEN);
					row.add(MARK_HYPHEN);
				}else if(view.getErLow().equals(view.getErHigh())){
					row.add(createLink(ServiceUtils.numberFormat0Digits(view.getErLow())+MARK_PERCENTAGE, view.getCode()));
					row.add(ServiceUtils.numberFormat0Digits(view.getErLow()));
				}else{
					row.add(createLink(ServiceUtils.numberFormat0Digits(view.getErLow())+MARK_PERCENTAGE+MARK_TILDE+ServiceUtils.numberFormat0Digits(view.getErHigh())+MARK_PERCENTAGE, view.getCode()));
					row.add(ServiceUtils.numberFormat0Digits((view.getErLow() + view.getErHigh())/2));
				}
				row.add(ServiceUtils.numberFormat2Digits(view.getPeg()));
				row.add(view.getErNetProfitMaxFlag().intValue()==1?"高":MARK_EMPTY);
				//PE
				row.add(ServiceUtils.numberFormat2Digits(view.getPe()));
				//静PE/动PE
				if(view.getPeYoy() != null && view.getPe()!=null&&view.getPe()!=0) {
					row.add(ServiceUtils.numberFormat2Digits(view.getPeYoy()/view.getPe()));
				}else{
					row.add(MARK_HYPHEN);
				}
				//预告PE
				row.add(ServiceUtils.numberFormat2Digits(view.getErPe()));
				if(view.getPe() != null && view.getErPe() != null && view.getErPe() != 0){					
					row.add(ServiceUtils.numberFormat2Digits(view.getPe()/view.getErPe()));
				}else{
					row.add(MARK_HYPHEN);
				}
				row.add(ServiceUtils.numberFormat2Digits(view.getForecastPeThisYear()));
				row.add(ServiceUtils.numberFormat2Digits(view.getForecastPeNextYear()));
				//pb
				row.add(ServiceUtils.numberFormat2Digits(view.getPb()));
				row.add(ServiceUtils.numberFormat2Digits(view.getPs()));
				row.add(view.getPeNtile());	
				row.add(view.getPbNtile());
				row.add(view.getPsNtile());
				row.add(view.getNtile());
				row.add(ServiceUtils.nvl(view.getGrossProfitMargin(), MARK_HYPHEN, ServiceUtils.numberFormat2Digits(view.getGrossProfitMargin())+MARK_PERCENTAGE));
				row.add(ServiceUtils.nvl(view.getSaleProfitMargin(), MARK_HYPHEN, ServiceUtils.numberFormat2Digits(view.getSaleProfitMargin())+MARK_PERCENTAGE));
				
				row.add(ServiceUtils.nvl(view.getRevenueGrowthRate(), MARK_HYPHEN, ServiceUtils.numberFormat2Digits(view.getRevenueGrowthRate())+MARK_PERCENTAGE));
				row.add(view.getRevenueMaxFlag().intValue()==1?"高":MARK_EMPTY);
				
				row.add(rateStatus(view.getNetProfitGrowthRate()));				
				row.add(view.getNetProfitMaxFlag().intValue()==1?"高":MARK_EMPTY);
				
				//经营现金净流量与净利润的比率
				row.add(ServiceUtils.nvl(view.getCashNetProfitRate(), MARK_HYPHEN, view.getCashNetProfitRate()+MARK_PERCENTAGE));
				//负债率
				row.add(ServiceUtils.nvl(view.getDebtRate(), MARK_HYPHEN, ServiceUtils.numberFormat2Digits(view.getDebtRate())+MARK_PERCENTAGE));
				row.add(ServiceUtils.nvl(view.getResearchRate(), MARK_HYPHEN, ServiceUtils.numberFormat2Digits(view.getResearchRate())+MARK_PERCENTAGE));
				
				row.add(formatDate(view.getFnDate()));
				row.add(view.getCloseChange10());
				row.add(view.getCloseChange20());
				row.add(view.getCloseChange30());
				row.add(view.getCloseChange60());
				row.add(view.getCloseChange120());
				result.add(row);
			}catch(Exception e){
				System.err.println(view.getCode());
				e.printStackTrace();
			}
		}
		addIndustryExcludes();
		List<Map.Entry<String,Integer>> inds = indAnaylse.filterGreaterThan(indAnaylse.sort(), 5, Industry_Excludes);
		List<Map.Entry<String,Integer>> inds2 = indAnaylse2.filterGreaterThan(indAnaylse2.sort(), 5, Industry_Excludes);
		//System.out.println(inds);
		sc.put("industry_anaylse", inds.toString());
		String json = JsonUtils.getJsonString4JavaPOJO(result);
		//System.out.println(json);
		sc.setResponse("{\"data\":" + json + ", \"industryAnaylse\":\""+inds.toString()+"\", \"industryAnaylse2\":\""+inds2.toString()+"\", \"codes\":\""+StringUtils.join(codes, "','")+"\"}");
	}
	
	public void doStrategy() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		ScreenerForm form = (ScreenerForm)sc.getForm();
		List<Strategy> ss = getStrategyFromSreenerForm(conn, form);
		List<String> codes = new ArrayList();
		for(Strategy s : ss){
			s.run(conn, ServiceUtils.getToday());
			codes.addAll(s.getResultCode());
		}
		form = new ScreenerForm();
		if(codes.size() > 0){
			form.setCodes(StringUtils.join(codes, ","));
		}else{
			form.setCodes("-1");
		}
		sc.setForm(form);
		searchForCN();
	}

    public static List<Strategy> getSearchConditionWithStrategy(Connection conn) throws Exception {
        List<StkSearchCondition> scs = JdbcUtils.list(conn, "select * from stk_search_condition order by nvl(update_time, insert_time)", StkSearchCondition.class);
        List<Strategy> list = new ArrayList();
        for(StkSearchCondition sc : scs){
            ScreenerForm form = (ScreenerForm)JsonUtils.getObject4Json(sc.getText(), ScreenerForm.class);
            if(form.getAstrategy() != null && form.getAstrategy().length > 0){
                for(String sid : form.getAstrategy()){
                    Strategy s = StrategyManager.getStrategyById(sid);
                    if(s != null){
                        s.setDataSourceName(sc.getName());
                        s.setIndexs(getIndexBySreenerForm(conn, form));
                        list.add(s);
                    }
                }
            }
        }
        return list;
    }

    public static List<Strategy> getStrategyFromSreenerForm(Connection conn, ScreenerForm form) throws Exception {
        List<Strategy> list = new ArrayList();
        if(form.getAstrategy() != null && form.getAstrategy().length > 0){
            for(String sid : form.getAstrategy()){
                Strategy s = StrategyManager.getStrategyById(sid);
                if(s != null){
                    s.setDataSourceName("test");
                    s.setIndexs(getIndexBySreenerForm(conn, form));
                    s.logToDB = false;
                    list.add(s);
                }
            }
        }
        return list;
    }

    public static List<Index> getIndexFromSearchCondition(Connection conn, int id) {
        StkSearchCondition sc = JdbcUtils.load(conn, "select * from stk_search_condition where id=?", id, StkSearchCondition.class);
        ScreenerForm form = (ScreenerForm)JsonUtils.getObject4Json(sc.getText(), ScreenerForm.class);
        return getIndexBySreenerForm(conn, form);
    }

    public static List<Index> getIndexBySreenerForm(Connection conn, ScreenerForm form) {
        List<String> codes = getCodeBySreenerForm(conn, form);
        return IndexUtils.codeToIndex(conn, codes);
    }

    public static List<String> getCodeBySreenerForm(Connection conn, ScreenerForm form) {
        ScreenerAction action = new ScreenerAction();
        String sql = action.formToSql(conn, form, "code");
        return JdbcUtils.list(conn, sql, String.class);
    }




    public void searchForUS() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		ScreenerForm form = (ScreenerForm)sc.getForm();		
		
		StringBuffer sql = new StringBuffer();		
		sql.append("select * from stk_us_search_view v ");
		sql.append("where 1=1 ");
		//sql.append(" and v.code='IRBT' ");
		addWhereClauseCodes(sql, form);
		addWhereClausePE(sql, form);
		addWhereClausePB(sql, form);
		addWhereClausePS(sql, form);
		addWhereClauseRevenueGrowthRate(sql, form);
		addWhereClauseNetProfitGrowthRate(sql, form);
		addWhereClauseGrossProfitMargin(sql, form);
		
		System.out.println(sql.toString());
		List<StkUsSearchView> list = JdbcUtils.list(conn, sql.toString(), StkUsSearchView.class);
		
		List<List> result = new ArrayList<List>();
		for(StkUsSearchView view : list){
			try{
				List row = new ArrayList();
				row.add(ServiceUtils.wrapCodeAndNameAsHtml(view.getCode(),view.getName()));
				row.add(display(view.getIndustry()));
				row.add(ServiceUtils.formatDate(view.getFnDate()));
				row.add(view.getMarketCap());
				row.add(view.getRoe());
				row.add(view.getPe());
				row.add(view.getPb());
				row.add(view.getPs());
				row.add(view.getPeg());
				row.add(ServiceUtils.nvl(view.getGrossProfitMargin(), CommonConstant.MARK_HYPHEN, view.getGrossProfitMargin()+CommonConstant.MARK_PERCENTAGE));
				row.add(rateStatus(view.getNetProfitGrowthRate()));
				row.add(ServiceUtils.nvl(view.getDebtRate(), CommonConstant.MARK_HYPHEN, view.getDebtRate()+CommonConstant.MARK_PERCENTAGE));
				row.add(ServiceUtils.nvl(view.getResearchRate(), CommonConstant.MARK_HYPHEN, view.getResearchRate()+CommonConstant.MARK_PERCENTAGE));
				result.add(row);
			}catch(Exception e){
				System.err.println(view.getCode());
				e.printStackTrace();
			}
		}
		String json = JsonUtils.getJsonString4JavaPOJO(result);
		//System.out.println(json);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	public void save() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		ScreenerForm form = (ScreenerForm)sc.getForm();
		String name = request.getParameter("n");
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		
		String codes = form.getCodes();
		if(codes != null){
			codes = StringUtils.replace(codes, " ", "");
			codes = StringUtils.replace(codes, "，", ",");
			String[] ss = codes.split(",");
			Set<String> set = new LinkedHashSet<String>();
			for(String s : ss){
				set.add(s);
			}
			form.setCodes(StringUtils.join(set,','));
		}
		
		List params = new ArrayList();		
		String json = JsonUtils.getJsonString4JavaPOJO(form);

		if(id == null || id.length() == 0){
			Integer i = JdbcUtils.load(conn, "select nvl(max(id),0)+1 from stk_search_condition", int.class);
			params.add(i);
			params.add(type);
			params.add(name);
			params.add(JdbcUtils.createClob(conn, json));
			JdbcUtils.insert(conn, "insert into stk_search_condition select ?,?,?,?,sysdate,null from dual", params);
			id = String.valueOf(i);
		}else{
			params.add(type);
			params.add(name);
			params.add(JdbcUtils.createClob(json));
			params.add(id);
			JdbcUtils.insert(conn, "update stk_search_condition set type=?,name=?,text=?,update_time=sysdate where id=?", params);
		}
		String result = "{\"id\":"+id+", \"name\":\""+name+"\"}";
		sc.setResponse(result);
	}
	
	public void select() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String id = request.getParameter("id");
		List params = new ArrayList();
		params.add(id);
		StkSearchCondition condition = JdbcUtils.load(conn, "select * from stk_search_condition where id=?", params, StkSearchCondition.class);
		String json = JsonUtils.getJsonString4JavaPOJO(condition);
		sc.setResponseAsJson(json);
	}
	
	public void list() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String type = request.getParameter("type");
		List params = new ArrayList();
		params.add(type);
		List<StkSearchCondition> list = JdbcUtils.list(conn, "select * from stk_search_condition where type=? order by nvl(update_time,insert_time) desc", params, StkSearchCondition.class);
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponseAsJson(json);
	}
	
	public void extract() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String codes = request.getParameter("codes");
		Set<String> result = null;
		if(StringUtils.startsWithIgnoreCase(codes, "http")){
			codes = HttpUtils.get(codes, "utf-8");
			//System.out.println(codes);
		}
		result = IndexUtils.extractCodeFromText(codes);
		String json = JsonUtils.getJsonString4JavaPOJO(result);
		sc.setResponseAsJson(json);
	}
	
	public void getIndustry() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String from = request.getParameter("from");
		List<StkIndustryType> list = JdbcUtils.list("select * from stk_industry_type where source in ('10jqka_gn','10jqka_thshy','hexun_conception','my_industry_fntype','qq_conception','cnindex') order by source,name", StkIndustryType.class);
		
		List<Map> results = new ArrayList();
		for(StkIndustryType type : list){
			Map map = new HashMap();
			map.put("value", type.getId().toString());
			map.put("text", type.getName());
			map.put("group", type.getSource());
			results.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		sc.setResponseAsJson(json);
	}
	
	public void getMVRefreshTime()  throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		String time = JdbcUtils.load(conn, "select to_char(last_refresh_date,'yyyy-mm-dd hh24:mi:ss') from user_mviews where mview_name='STK_SEARCH_MVIEW'", String.class);
		sc.setResponse(time);
	}
	
	public void refreshMV() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		//JdbcUtils.call(conn, "{call dbms_refresh.refresh('STK_SEARCH_MVIEW')}");
		refreshStkSearchMview(conn);
		sc.setResponse(1);
	}
	
	public static void refreshStkSearchMview(Connection conn){
		JdbcUtils.call(conn, "{call dbms_refresh.refresh('STK_SEARCH_MVIEW')}");
	}
	
	public void deleteCondition() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String id = request.getParameter("id");
		List params = new ArrayList();
		params.add(id);
		int n = JdbcUtils.delete(conn, "delete from stk_search_condition where id=?", params);
		sc.setResponse(n);
	}
	
	public void getMoneyFlow() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		List<List<Map>> results = new ArrayList<List<Map>>();
		int days = 11;
		int cnt = 0;
		String date = null;
		while(true){
			List<Map> list = JdbcUtils.list2Map(conn, "select flow_date,name,hot from(select f.code,k.name,round(avg(f.main_amount),2) hot,max(f.flow_date) flow_date from stk_capital_flow f, stk k where f.code=k.code and k.cate=4 and f.flow_date between to_char(sysdate-"+(30+cnt)+",'yyyymmdd') and to_char(sysdate-"+cnt+",'yyyymmdd') group by f.code,k.name) order by hot desc");
			cnt ++;
			String s = (String)list.get(0).get("flow_date");
			if(s.equals(date)){
				continue;
			}
			results.add(list);
			date = s;
			if( --days <= 0)break;
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		sc.setResponse(json);
	}
	
	public void getStrategy()throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		List<StkStrategy> list = JdbcUtils.list(conn, "select * from stk_strategy order by id desc", StkStrategy.class);

		List<Map> results = new ArrayList();
		for(StkStrategy type : list){
			Map map = new HashMap();
			map.put("value", type.getId().toString());
			map.put("text", type.getName());
			map.put("group", type.getStrategyDate());
			results.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		sc.setResponseAsJson(json);
	}
	
	public void getAvailableStrategy() throws Exception {
		StkContext sc = StkContext.getContext();
		List<Map> results = new ArrayList();
		List<Name2Value<String,Name2Value<Integer, Class>>> list = StrategyManager.getApplyStrategy();
		for(Name2Value<String,Name2Value<Integer, Class>> type : list){
			Map map = new HashMap();
			map.put("value", type.getValue().getName());
			map.put("text", type.getName());
			results.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		sc.setResponseAsJson(json);
	}
	
	
	/*
	 * ---------- private -------------- 
	 */
	
	private static String rateStatus(Double rate) {
		if(rate != null){
			if(rate == -999)return "续亏";
			else if(rate == 999)return "扭亏";
			else if(rate == -9999)return "转亏";
			else{
				return rate+CommonConstant.MARK_PERCENTAGE;
			}
		}
		return CommonConstant.MARK_HYPHEN;
	}
	
	private String createLink(String s, String code){
		return "<a target=\"_blank\" href=\"http://finance.sina.com.cn/realstock/stock_predict/predict_notice/detail_"+code+".html\">"+s+"</a>";
	}
	
	private String display(String html) {
		return WebUtils.display(html,6,false,false,false);
	}
	
	private String formatDate(String d) throws ParseException{
		return ServiceUtils.formatDate(d,ServiceUtils.sf_ymd2, ServiceUtils.sf_ymd16);
	}
	
	private void addWhereClauseCodes(StringBuffer sql, ScreenerForm form){
		if(StringUtils.isNotEmpty(form.getCodes())){
			String codes = StringUtils.replace(form.getCodes(), " ", "");			
			sql.append(" and v.code in ('" + StringUtils.join(StringUtils.split(codes, ','),"','") + "')");
		}
	}
	
	private void addWhereClausePE(StringBuffer sql, ScreenerForm form) {
		if(form.getPeFrom() != null){
			sql.append(" and v.pe >= "+form.getPeFrom());
		}
		if(form.getPeTo() != null){
			sql.append(" and v.pe <= "+form.getPeTo());
		}
	}
	
	private void addWhereClausePB(StringBuffer sql, ScreenerForm form) {
		if(form.getPbFrom() != null){
			sql.append(" and v.pb >= "+form.getPbFrom());
		}
		if(form.getPbTo() != null){
			sql.append(" and v.pb <= "+form.getPbTo());
		}
	}
	
	private void addWhereClausePS(StringBuffer sql, ScreenerForm form) {
		if(form.getPsFrom() != null){
			sql.append(" and v.ps >= "+form.getPsFrom());
		}
		if(form.getPsTo() != null){
			sql.append(" and v.ps <= "+form.getPsTo());
		}
	}
	
	private void addWhereClauseRevenueGrowthRate(StringBuffer sql, ScreenerForm form) {
		if(form.getRevenueGrowthRateFrom() != null){
			sql.append(" and v.REVENUE_GROWTH_RATE >= "+form.getRevenueGrowthRateFrom());
		}
		if(form.getRevenueGrowthRateTo() != null){
			sql.append(" and v.REVENUE_GROWTH_RATE <= "+form.getRevenueGrowthRateTo());
		}
	}
	
	private void addWhereClauseNetProfitGrowthRate(StringBuffer sql, ScreenerForm form) {
		if(form.getNetProfitGrowthRateFrom() != null){
			sql.append(" and v.net_profit_Growth_rate >= "+form.getNetProfitGrowthRateFrom());
		}
		if(form.getNetProfitGrowthRateTo() != null){
			sql.append(" and v.net_profit_Growth_rate <= "+form.getNetProfitGrowthRateTo());
		}
	}
	
	private void addWhereClauseGrossProfitMargin(StringBuffer sql, ScreenerForm form) {
		if(form.getGrossProfitMarginFrom() != null){
			sql.append(" and v.Gross_Profit_Margin >= "+form.getGrossProfitMarginFrom());
		}
		if(form.getGrossProfitMarginTo() != null){
			sql.append(" and v.Gross_Profit_Margin <= "+form.getGrossProfitMarginTo());
		}
	}
	
	private void addWhereClauseListingDays(StringBuffer sql, ScreenerForm form) {
		if(form.getListingDaysFrom() != null){
			sql.append(" and v.Listing_days >= "+form.getListingDaysFrom());
		}
		if(form.getListingDaysTo() != null){
			sql.append(" and v.Listing_days <= "+form.getListingDaysTo());
		}
	}
	
	private void addWhereClauseErLowAndHigh(StringBuffer sql, ScreenerForm form) {
		if(form.getErLowFrom() != null){
			sql.append(" and v.er_low >= "+form.getErLowFrom());
		}
		if(form.getErLowTo() != null){
			sql.append(" and v.er_low <= "+form.getErLowTo());
		}
		if(form.getErHighFrom() != null){
			sql.append(" and v.er_high >= "+form.getErHighFrom());
		}
		if(form.getErHighTo() != null){
			sql.append(" and v.er_high <= "+form.getErHighTo());
		}
	}
	
	private void addWhereClauseErPe(StringBuffer sql, ScreenerForm form) {
		if(form.getErPeFrom() != null){
			sql.append(" and v.er_pe >= "+form.getErPeFrom());
		}
		if(form.getErPeTo() != null){
			sql.append(" and v.er_pe <= "+form.getErPeTo());
		}
	}
	
	private void addWhereClauseMarketCap(StringBuffer sql, ScreenerForm form) {
		if(form.getMarketCapFrom() != null){
			sql.append(" and v.market_cap >= "+form.getMarketCapFrom());
		}
		if(form.getMarketCapTo() != null){
			sql.append(" and v.market_cap <= "+form.getMarketCapTo());
		}
	}
	
	private void addWhereClauseDebtRate(StringBuffer sql, ScreenerForm form) {
		if(form.getDebtRateFrom() != null){
			sql.append(" and v.DEBT_RATE >= "+form.getDebtRateFrom());
		}
		if(form.getDebtRateTo() != null){
			sql.append(" and v.DEBT_RATE <= "+form.getDebtRateTo());
		}
	}
	
	private void addWhereClauseIndustry(StringBuffer sql, ScreenerForm form) {
		if(form.getIndustry() != null && form.getIndustry().length > 0){
			sql.append(" and code in (select code from stk_industry where industry in ("+StringUtils.join(form.getIndustry(),",")+"))");
		}
	}
	
	private void addWhereClauseStrategy(Connection conn, StringBuffer sql, ScreenerForm form) {
		if(form.getStrategy() != null && form.getStrategy().length > 0){
			List<StkStrategy> list = JdbcUtils.list(conn, "select * from stk_strategy where id in ("+StringUtils.join(form.getStrategy(),",")+")", StkStrategy.class);
			List<String> sb = new ArrayList();
			for(StkStrategy s : list){
				String codes = s.getText();
				sb.addAll(Arrays.toCollection(codes.split(",")));
			}
			sql.append(" and code in ('"+StringUtils.join(sb,"','")+"')");
		}
	}
	
	private void addWhereClauseComment(StringBuffer sql, ScreenerForm form) {
		if(form.getComment() != null && form.getComment().length() > 0){
			String comment = form.getComment();
			String appendSql = ServiceUtils.getMatchString(comment, "\\{[^}]*\\}");
			if(appendSql != null){
				sql.append(" "+StringUtils.substringBetween(appendSql, "{", "}"));
			}
		}
	}
	
	public static String Industry_Excludes = null;
	public static void addIndustryExcludes(){
		Industry_Excludes = null;
		Industry_Excludes = "深股通,预盈预增,汇金持股,深成500,基金增仓,融资融券,资本品,中证500,转融券,沪深300,中价,沪股通,明星基金,高价,广东,原材料,增持回购,";
		Industry_Excludes+= "重组并购,江苏=18, 浙江=17, 参股金融,山东=15, 参股新三板,北京,定向增发=11, QFII持股,中盘=8, 深证100=8,大盘,福建,安徽,";
		Industry_Excludes+= "上证380=12,债转股,上证180,低价,操盘手80,小盘,次新已开板,四川,上海,预亏预减,深港通";
	}
}
