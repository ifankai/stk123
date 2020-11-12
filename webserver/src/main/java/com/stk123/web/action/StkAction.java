package com.stk123.web.action;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;

import com.stk123.bo.StkCare;
import com.stk123.bo.StkMonitor;
import com.stk123.bo.StkText;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.Keyword;
import com.stk123.task.EarningsForecast;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.baidu.BaiduSearch;
import com.stk123.tool.ik.DocumentField;
import com.stk123.tool.ik.IKUtils;
import com.stk123.tool.search.Search;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JdbcUtils.IngoreCaseHashMap;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.collection.Name2Value;
import com.stk123.tool.web.util.RequestUtils;
import com.stk123.StkConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;


public class StkAction {
	
	private static List<String> PPI_KW;
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_S);
		code = code.toUpperCase();
		Connection conn = sc.getConnection();
		Index index = new Index(conn,code); 
		sc.setIndex(index);
		return StkConstant.ACTION_SUCC;
	}
	
	public void getPPI() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_S);
		code = code.toUpperCase();
		Connection conn = sc.getConnection();
		if(PPI_KW == null) {
			PPI_KW = JdbcUtils.list(conn, "select name from stk_data_ppi_type", String.class);
		}
		Index index = new Index(conn,code); 
		List params = new ArrayList();
		params.add(code);
		List<String> list = JdbcUtils.list(conn, "select b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.keyword_id=b.id and a.code=? and a.code_type="+StkConstant.KEYWORD_TYPE_STK, params, String.class);
		Set<String> ppi = IKUtils.intersection(index.getStock().getCompanyProfile()+list, PPI_KW.toString());
		sc.put("keyword_ppi", ppi);
	}
	
	public void getCare() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		
		List<List> list = new ArrayList<List>();
		List<StkCare> cares = JdbcUtils.list(conn, "select * from stk_care order by insert_time desc", StkCare.class);
		for(StkCare care : cares){
			Index index = new Index(conn, care.getCode());
			List map = new ArrayList();
			map.add(StkUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
			map.add(StkUtils.number2String(index.getTotalMarketValue(),2));
			map.add(care.getType());
			map.add("<a target='_blank' href='"+care.getUrl()+"'>"+care.getInfo()+"</a>");
			map.add(StkUtils.formatDate(care.getInfoCreateTime(), StkUtils.sf_ymd) );
			map.add(WebUtils.display(care.getMemo(), 10, false) );
			map.add(care.getParam1()==null?"":care.getParam1());
			//map.add(care.getParam2()==null?"":care.getParam2());
			map.add(StkUtils.formatDate(care.getInsertTime(), StkUtils.sf_ymd));
			list.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	public void listFund() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String fund = request.getParameter("fund");
		
		List<List> list = new ArrayList<List>();
		List<Map<String,Object>> cares = JdbcUtils.list(conn, "select a.code,a.fn_date,b.name,a.stk_num,a.rate,a.num_change,a.num_change_rate "
				+ "from stk_ownership a, stk_organization b where a.org_id=b.id and b.name like '%"+fund+"%' and  a.num_change_rate > 0 "
						+ "order by a.fn_date desc,a.stk_num desc", IngoreCaseHashMap.class);
		for(Map<String,Object> care : cares){
			Index index = new Index(conn, (String)care.get("code"));
			List map = new ArrayList();
			map.add(StkUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
			map.add(care.get("fn_date"));
			map.add(care.get("name"));
			map.add(care.get("stk_num"));
			map.add(care.get("rate"));
			map.add(care.get("num_change"));
			map.add(care.get("num_change_rate"));
			list.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	public void listNewFund() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		
		List<List> list = new ArrayList<List>();
		List<Map<String,Object>> cares = JdbcUtils.list(conn, "select a.code,a.fn_date,count(a.num_change_rate) cnt,"
				+ "listagg(c.name,'<br>') within group (order by a.num_change_rate) name "
				+ "from stk_ownership a,stk_cn b, stk_organization c "
				+ "where a.code=b.CODE and length(c.name)>3 /*排除个人投资者*/ and a.org_id=c.id and b.listing_date < to_char(add_months(sysdate, -12),'yyyymmdd') "
				+ "and a.fn_date > to_char(add_months(sysdate, -5),'yyyymmdd') and a.num_change_rate>0 "
				+ "group by a.code,a.fn_date having count(a.num_change_rate) >= 8 order by a.fn_date desc", IngoreCaseHashMap.class);
		for(Map<String,Object> care : cares){
			Index index = new Index(conn, (String)care.get("code"));
			List map = new ArrayList();
			map.add(StkUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
			map.add(care.get("fn_date"));
			map.add(care.get("cnt"));
			map.add(care.get("name"));
			list.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	public void getMonitor() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		
		List<List> list = new ArrayList<List>();
		List<StkMonitor> ms = JdbcUtils.list(conn, "select * from stk_monitor where type=2", StkMonitor.class);
		for(StkMonitor care : ms){
			Index index = new Index(conn, care.getCode());
			List map = new ArrayList();
			map.add(StkUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
			//map.put(StkConstant.JSON_MARKET_VALUE, StkUtils.formatNumber(index.getTotalMarketValue(),2));
			map.add("<a target='_blank' href='"+care.getResult1()+"'>"+care.getParam1()+"</a>");
			map.add(StkUtils.formatDate(care.getParam3(),StkUtils.sf_ymd2, StkUtils.sf_ymd) );
			map.add(care.getParam2());
			//map.put("close", index.getK().getClose());
			map.add(StkUtils.formatDate(care.getParam4(),StkUtils.sf_ymd2, StkUtils.sf_ymd));
			map.add(care.getParam5());
			list.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	//加关注
	public void care() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_CODE);
		String type = request.getParameter("type");
		String info = request.getParameter("info");
		String url = request.getParameter("url");
		String memo = request.getParameter("memo");
		String createtime = request.getParameter("createtime");
		String param1 = request.getParameter("param1");
		String param2 = request.getParameter("param2");

		List params = new ArrayList();
		params.add(code);
		params.add(type);
		params.add(info);
		params.add(url);
		params.add(memo);
		params.add(new Timestamp(StkUtils.sf_ymd.parse(createtime).getTime()));
		params.add(param1);
		params.add(param2);
		int n = JdbcUtils.insert(sc.getConnection(), "insert into stk_care(code,type,info,url,memo,insert_time,info_create_time,param1,param2) values (?,?,?,?,?,sysdate,?,?,?)", params);
		if(n == 1){
			sc.setResponse(1);
		}else{
			sc.setResponse(0);
		}
	}
	
	//取消关注
	public void uncare() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_S);
		List params = new ArrayList();
		params.add(code);
		int n = JdbcUtils.update(sc.getConnection(), "update stk set status=0 where code=?", params);
		if(n == 1){
			sc.setResponse(1);
		}else{
			sc.setResponse(0);
		}
	}
	
	private final static String COLOR_RED = "#33AA11";
	private final static String COLOR_GREEN = "#FFB380";
	
	public void chartstk() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_S);
		Index index = new Index(StkContext.getConnection(),code); 
		//Index.KLineWhereClause = " and kline_date>='20060101'";
		List<K> ks = index.getKs();
		List<K> result = new ArrayList<K>();
		int cnt = ks.size()-1<200?ks.size()-1:200;
		for(int i=cnt;i>=0;i--){
			K k = ks.get(i);
			if(k.getOpen() > k.getClose()){
				k.setColor(COLOR_RED);
			}else{
				k.setColor(COLOR_GREEN);
			}
			result.add(k);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(result, K.JSON_INCLUDE_FIELDS, StkConstant.DATE_FORMAT_YYYY_MM_DD);
		//System.out.println(json);
		sc.setResponse(json);
	}
	
	//历史PE
	public void stkValueHistory() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_S);
		Index index = new Index(StkContext.getConnection(),code); 
		List<List> list = index.getValueHistoryList();
		String json = "{\"data\":"+JsonUtils.getJsonString4JavaPOJO(list)+StkConstant.MARK_BRACE_RIGHT;
		sc.setResponse(json);
	}
	
	public void listOnwer() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String date = request.getParameter(StkConstant.PARAMETER_DATE);
		String code = request.getParameter(StkConstant.PARAMETER_S);
		Index index = new Index(StkContext.getConnection(),code); 
		List<List> list = index.getOnwershipByDate(date);
		String json = "{\"data\":"+JsonUtils.getJsonString4JavaPOJO(list)+StkConstant.MARK_BRACE_RIGHT;
		sc.setResponse(json);
	}
	
	public void listRelatedStk() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = StkContext.getConnection();
		String code = request.getParameter(StkConstant.PARAMETER_CODE);
		List<Map> list1 = getRelatedStk(conn, code, false);
		List<Map> list2 = getRelatedStk(conn, code, true);
		String json1 = JsonUtils.getJsonString4JavaPOJO(list1);
		String json2 = JsonUtils.getJsonString4JavaPOJO(list2);
		StringBuffer json = new StringBuffer();
		json.append("{\"stk1\":").append(json1).append(StkConstant.MARK_COMMA).append("\"stk2\":").append(json2).append(StkConstant.MARK_BRACE_RIGHT);
		sc.setResponse(json.toString());
	}
	
	private final static String F9_KEY = "营";
	
	public List<Map> getRelatedStk(Connection conn, String code, boolean includeCompanyInfo) throws Exception{
		Index index = new Index(conn, code);
		List<Name2Value> f9 = index.getF9();
		List<Name2Value> zhuyin = Name2Value.containName(f9, F9_KEY);
		String sZhuyin = StkConstant.MARK_EMPTY;
		for(Name2Value pair : zhuyin){
			sZhuyin += pair.getValue();
		}
		
		//keyword
		List<String> kws = Keyword.listKeywords(conn, code, 1);
		List<Name2Value> searchWordsWeight = new ArrayList<Name2Value>();
		for(String kw : kws){
			Name2Value<String,Float> nv = new Name2Value<String,Float>();
			nv.setName(kw);
			nv.setValue(5.0f);
			searchWordsWeight.add(nv);
		}
		
		StringBuffer keyword = new StringBuffer();
		if(includeCompanyInfo){
			keyword.append(StringUtils.join(kws, StkConstant.MARK_COMMA)).append(StkConstant.MARK_COMMA).append(sZhuyin).append(StkConstant.MARK_COMMA).append(index.getStock().getCompanyProfile());
		}else{
			keyword.append(StringUtils.join(kws, StkConstant.MARK_COMMA)).append(StkConstant.MARK_COMMA).append(sZhuyin);
		}
		List<Document> stks = Search.searchRelatedStk(keyword.toString(), searchWordsWeight, IKUtils.default_excludes,0,14);
		List<Map> jsonList = new ArrayList<Map>();
		for(Document stk : stks){
			String id = stk.get(DocumentField.ID.value());
			if(id.equals(code))continue;
			index = new Index(conn,id);
			Map map = new HashMap();
			map.put(StkConstant.JSON_CODE, index.getCode());
			map.put(StkConstant.JSON_NAME, index.getName());
			jsonList.add(map);
		}
		return jsonList;
	}
	
	private static List<Map> stkCodes = new ArrayList<Map>();
	private static final int maxSearchResult = 50;
	private static final String MARKET = "market";
	private static final String ACTIVITY = "n";
	private static final String VAL = "v";
	
	public void search() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = StkContext.getConnection();
		String k = request.getParameter(StkConstant.PARAMETER_K);
		
		if(stkCodes.size() == 0){
			//stkCodes = JdbcUtils.list2Map(conn, "SELECT case when market = 1 then name||', '||F_TRANS_PINYIN_CAPITAL(name)||', '||code when market = 2 then code||', '||name end activity,code val, market FROM stk order by code asc", null);
			stkCodes = JdbcUtils.list2Map(conn, "SELECT case when market=1 or market=3 then name||', '||F_TRANS_PINYIN_CAPITAL(name)||', '||code when market = 2 then code||', '||name end n,code v from stk where cate=1 order by code asc", null);
		}
		List<Map> results = new ArrayList<Map>();
		int i = 0;
		for(Map<String,String> map : stkCodes){
			//for cn & us
			/*if(String.valueOf(map.get(MARKET)).equals(String.valueOf(Index.CN))){
				if(map.get(ACTIVITY) != null && map.get(ACTIVITY).toLowerCase().contains(k.toLowerCase())){
					results.add(map);
					if(i++ >= maxSearchResult)break;
				}
			}else{
				if(map.get(VAL) != null && map.get(VAL).toLowerCase().contains(k.toLowerCase())){
					results.add(map);
					if(i++ >= maxSearchResult)break;
				}
			}*/
			
			//only for cn
			if(map.get(ACTIVITY) != null && map.get(ACTIVITY).toLowerCase().contains(k.toLowerCase())){
				results.add(map);
				if(i++ >= maxSearchResult)break;
			}
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		sc.setResponse(json);
	}
	
	public void baiduSearch() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String data = request.getParameter("data");
		List<Map> list = (List<Map>)JsonUtils.getList4Json(data, HashMap.class, null);
		
		final CountDownLatch countDownLatch = new CountDownLatch(list.size());
		ExecutorService exec = Executors.newFixedThreadPool(4);
		for(final Map<String,String> map : list){
			
			Runnable run = new Runnable() {
				public void run() {
					try{
						try {
							int n = BaiduSearch.getBaiduNewsCount(StkUtils.addDay(new Date(), -7),map.get("keyword"),new Boolean(map.get("intitle")).booleanValue());
							map.put("count", String.valueOf(n));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}finally{
						countDownLatch.countDown();
					}
				}
			};
			exec.execute(run);
		}
		exec.shutdown();
		countDownLatch.await();
		
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		//System.out.println(json);
		sc.setResponse(json);
	}
	
	public void listNotice() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String code = request.getParameter(StkConstant.PARAMETER_CODE);
		int page = RequestUtils.getInt(request, StkConstant.PARAMETER_PAGE);
		int perPage = 20;//StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		
		Index index = new Index(conn, code);
		List<Map> notices = this.getNoticeFromXueqiu(index);
		List<Map> subNotices = notices.subList((page - 1)*perPage, Math.min(page * perPage, notices.size()-1));
		
		int count = notices.size();
		String json = JsonUtils.getJsonString4JavaPOJO(subNotices, StkConstant.DATE_FORMAT_YYYY_MM_DD);
		StringBuffer sb = new StringBuffer(StkConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(StkConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(StkConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(StkConstant.MARK_BRACE_RIGHT);
		sc.setResponse(sb);
	}
	
	private List<Map> getNoticeFromXueqiu(Index index) throws Exception {
		String scode = index.getLocationAsString()+index.getCode();
		Map<String, String> headerRequests = XueqiuUtils.getCookies();
		List<Map> notices = new ArrayList<Map>();
		Date now = new Date();
		int pageNum = 1;
		boolean clearCookie = false;
		do{
			String page = HttpUtils.get("https://xueqiu.com/statuses/stock_timeline.json?symbol_id="+scode+"&count=50&source=%E5%85%AC%E5%91%8A&page="+pageNum,null,headerRequests, "GBK");
			if("400".equals(page) || "404".equals(page)){
				if(!clearCookie){
					XueqiuUtils.clearCookie();
					clearCookie = true;
					continue;
				}
				break;
			}
			Map m = JsonUtils.testJson(page);
			List<Map> list = (List)m.get("list");
			boolean flag = false;
			for(Map n : list){
				int retweet = Integer.parseInt(String.valueOf(n.get("retweet_count")));
				int reply = Integer.parseInt(String.valueOf(n.get("reply_count")));
				if(retweet > 0 || reply > 0){
					String createdAt = String.valueOf(n.get("created_at"));
					Date date = new Date(Long.parseLong(createdAt));
					//System.out.println(StkUtils.formatDate(date));
					if(date.before(StkUtils.addDay(now, -500))){
						flag = true;
						break;
					}
					Map map = new HashMap();
					map.put("url", "https://xueqiu.com"+n.get("target"));
					map.put("count", retweet+reply);
					map.put("createtime", StkUtils.formatDate(date));
					map.put("description", n.get("description"));
					//System.out.println(n.get("description"));
					notices.add(map);
				}
			}
			if(flag){
				break;
			}
			if(pageNum++ >= 100)break;
		}while(true);
		return notices;
	}
	
	
	public void getEarningsForecast() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		
		List<List> list = new ArrayList<List>();
		
		int year = StkUtils.YEAR;
		
		String sql = "select distinct ef.code code,"
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year-1)+"),0) \"a_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year-1)+"),0) \"a_pe\","
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year)+"),0) \"b_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year)+"),0) \"b_pe\","
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+1)+"),0) \"c_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+1)+"),0) \"c_pe\","
				+ "nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+2)+"),0) \"d_np\","
				+ "nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year="+(year+2)+"),0) \"d_pe\""
				+ " from stk_earnings_forecast ef where forecast_net_profit>0";
		
		List<Map> ms = JdbcUtils.list2Map(conn, sql);
		for(Map m : ms){
			Index index = new Index(conn, String.valueOf(m.get("code")));
			List l = new ArrayList();
			l.add(StkUtils.wrapCodeAndNameAsHtml(index.getCode(), index.getName()));
			Double anp = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("a_np"))),2);
			Double ape = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("a_pe"))),2);
			Double bnp = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("b_np"))),2);
			if(anp <=0 || bnp <=0)continue;
			if(bnp <= anp * 1.2)continue;
			Double bpe = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("b_pe"))),2);
			Double cnp = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("c_np"))),2);
			if(cnp <= bnp * 1.2)continue;
			Double cpe = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("c_pe"))),2);
			Double dnp = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("d_np"))),2);
			if(dnp <= cnp * 1.2)continue;
			Double dpe = StkUtils.numberFormat(Double.parseDouble(String.valueOf(m.get("d_pe"))),2);
			
			/*//去年
			l.add(anp);
			l.add(ape);
			
			//今年
			l.add(bnp);
			if(anp == 0){
				l.add(StkConstant.MARK_HYPHEN);
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(EarningsForecast.format((bnp-anp)/anp*100)+"%");
				if(bnp == anp){
					l.add("持平");
				}else{
					Double d = bpe/((bnp-anp)/anp)/100;
					l.add(EarningsForecast.format(d));
				}
			}
			
			//明年
			l.add(cnp);
			if(bnp == 0){
				l.add(StkConstant.MARK_HYPHEN);
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(EarningsForecast.format((cnp-bnp)/bnp*100)+"%");
				if(cnp == bnp){
					l.add("持平");
				}else{
					Double d = cpe/((cnp-bnp)/bnp)/100;
					l.add(EarningsForecast.format(d));
				}
			}
			
			//后年
			l.add(dnp);
			if(cnp == 0){
				l.add(StkConstant.MARK_HYPHEN);
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(EarningsForecast.format((dnp-cnp)/cnp*100)+"%");
				if(cnp == dnp){
					l.add("持平");
				}else{
					Double d = cpe/((dnp-cnp)/cnp)/100;
					l.add(EarningsForecast.format(d));
				}
			}
			if(cnp == 0 || bnp == 0){
				l.add(StkConstant.MARK_HYPHEN);
			}else{
				l.add(EarningsForecast.format(((cnp-bnp)/bnp*100 + (dnp-cnp)/cnp*100)/2)+"%");
			}*/
			list.add(EarningsForecast.calculation(l, anp, ape, bnp, bpe, cnp, cpe, dnp, dpe));
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	public void getXueqiuArticle() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_CODE);
		Connection conn = sc.getConnection();
		
		List<List> list = new ArrayList<List>();
		//List<XueqiuArticle> articles = XueqiuStockArticleJob.getArticles(code, 250, 5);
		List params = new ArrayList();
		params.add(code);
		List<StkText> texts = JdbcUtils.list(conn,"select * from stk_text where code=? and type=3 order by insert_time desc" ,params, StkText.class);
		for(StkText article : texts){
			List l = new ArrayList();
			String text = article.getText();
			String title = StringUtils.substring(text, 0, StringUtils.indexOf(text,"]")+1);
            String reply = StringUtils.substring(text, StringUtils.lastIndexOf(text,"[")-1, text.length());
            text = StringUtils.substring(text, StringUtils.indexOf(text,"]")+2, text.length());
			l.add(title);
			l.add(text);
			l.add(reply);
			l.add(StkUtils.sf_ymd9.format(article.getInsertTime()));
			list.add(l);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse("{\"data\":" + json + "}");
	}
	
	public static void main(String[] args){
		
	}
}
