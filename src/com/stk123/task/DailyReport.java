package com.stk123.task;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;

import com.stk123.bo.Stk;
import com.stk123.bo.StkFnData;
import com.stk123.bo.StkFnType;
import com.stk123.bo.StkImportInfo;
import com.stk123.bo.StkIndustryRank;
import com.stk123.bo.StkIndustryType;
import com.stk123.bo.StkInfoLog;
import com.stk123.bo.StkKline;
import com.stk123.pojo.IndustryChange;
import com.stk123.pojo.StkChange;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.db.util.DateUtil;
import com.stk123.tool.html.HtmlA;
import com.stk123.tool.html.HtmlTable;
import com.stk123.tool.html.HtmlTd;
import com.stk123.tool.html.HtmlTr;
import com.stk123.tool.util.MyCacheUtils;
import com.stk123.tool.util.ChineseUtils;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;


/**
 * wind url
 * A股除权除息预告:
 * http://wwtnews.windin.com/home/stocks/default.aspx?columnid=30900000&subcolumnid=2
 * 
 * 机构荐股
 * http://wwtnews.windin.com/home/stocks/default.aspx?columnid=30500000
 * 
 * 新股
 * http://wwtnews.windin.com/home/stocks/default.aspx?columnid=31200000
 * 
 * 行业涨跌幅排名
 * http://www.windin.com/home/top/marketfocus/BoardTop20desc.shtml
 * 
 * 业绩预警
 * http://www.windin.com/home/top/hotcount/FinacialCountWarnningquarter.shtml
 * 
 * 个股投资评级
 * http://wwtnews.windin.com/WUDS/web/F9/Stock/WEST/rating/DealerRating.aspx?WindCode=002275.SZ
 * 个股盈利预测基本信息
 * http://wwtnews.windin.com/WUDS/web/F9/Stock/WEST/EPS/EarningsForecast.aspx?WindCode=002275.sz
 * 
 * http://m.wind.com.cn/WBBWebService/DataGetor.aspx
 * 
 * SERVER#m.wind.com.cn
 * http://www.soupingguo.com/app/433136674/
 * 
 * TODO
 * 1.每条新闻后面加【关注】【取消】
 * 2.最新季度，行业业绩（净利润）总体排名。  行业股数，统计股数，增长占比，平均同比增长率排行，增长股平均排行，增长股前5名平均排行
 *   [大事提醒] select * from stk_import_info where info like '%净利润%增%';
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DailyReport {
	
	public static String[] keywordsInclude = {};
	private static String[] keywordsExclude = {};
	
	private static String[] filterStkName = {};
	private static double   filterLastYearROE = 0;
	private static double   filterPercentige = 0;
	
	private static int fnReportYears = 0;
	private static int fnReportQuarts = 0;
	
	private static String[] industryRankPeriods = {};
	private static int industryRankStkSampleMinNumber = 0;
	private static double industryRankStkSamplePercentige = 0.0;
	private static int industryRankStkShowNumber = 0;
	private static int industryRankStkShowMaxNumber = 0;
	private static int industryRankStkLineNumber = 0;
	
	private static final String separator = "<br><br>---------------------<br><br>";
	private static final String html_empty = "&nbsp;";
	
	static{
		Properties props = new Properties();
		ConfigUtils.loadProperties(props, new File("./config.properties"));
		
		keywordsInclude = ((String)props.get("keywords_include")).split(",");
		keywordsExclude = ((String)props.get("keywords_exclude")).split(",");
		filterStkName = ((String)props.get("filter_stk_name")).split(",");
		filterLastYearROE = Double.parseDouble((String)props.get("filter_lastyear_roe"));
		filterPercentige = Double.parseDouble((String)props.get("filter_percentige"));
		
		fnReportYears = Integer.parseInt((String)props.getProperty("fn_report_years"));
		fnReportQuarts = Integer.parseInt((String)props.getProperty("fn_report_quarts"));
		
		industryRankPeriods = ((String)props.get("industry_rank_periods")).split(",");
		industryRankStkSampleMinNumber = Integer.parseInt((String)props.getProperty("industry_rank_stk_sample_min_number"));
		industryRankStkSamplePercentige = Double.parseDouble((String)props.get("industry_rank_stk_sample_percentige"));
		industryRankStkShowNumber = Integer.parseInt((String)props.getProperty("industry_rank_stk_show_number"));
		industryRankStkShowMaxNumber = Integer.parseInt((String)props.getProperty("industry_rank_stk_show_max_number"));
		industryRankStkLineNumber = Integer.parseInt((String)props.getProperty("industry_rank_stk_line_number"));
		
	}
	

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		Connection conn = null;
		StringBuffer html = new StringBuffer(1024);
		//html.append(StkUtils.writeHtmlHead());
		html.append("<div><img src='http://www.yz21.org/stock/szsec/sh-zj.png' alt='沪市大盘资金指标' /></div>");
		try{
			conn = DBUtil.getConnection();
			String mailTitle = "Daily Report ("+StkUtils.formatDate(new Date())+")";
			
			//--------------------import info
			html.append(createImportInfoTable(conn).toHtml());
			//--------------------industry care
			html.append(createIndustryCareTable());
			//--------------------industry rank
			if(Integer.parseInt(StkUtils.formatDate(new Date(), StkUtils.sf_hh)) >= 15){
				List<HtmlTable> industryTables = createIndustryRankTable(conn);
				for(HtmlTable industryTable:industryTables){
					html.append(industryTable.toHtml()).append("<br>");
				}
			}
			//--------------------stk care
			HtmlTable careTable = createStkCareTable(conn);
			html.append(careTable.toHtml());
			//--------------------daily stk news
			HtmlTable reportTable = createStkDailyNewsTable(conn);
			html.append(separator).append(reportTable.toHtml());
			
			//send e-mail begin -----------------
			if(reportTable.rows.size() == 0){
				mailTitle = mailTitle+" -- Null";
			}
			
			Integer reportId =  JdbcUtils.load(conn, "select nvl(max(id)+1,100001) from stk_pe", Integer.class);
			List params = new ArrayList();
			params.add(reportId);
			params.add(StkUtils.formatDate(new Date(),StkUtils.sf_ymd2));
			params.add(JdbcUtils.createClob(html.toString()));
			JdbcUtils.insert(conn, "insert into stk_pe values (?,?,?)", params);
			
			String url = "http://210.13.77.68:8180/insurance/loginPage.do?p=report&c="+reportId.intValue();
			String msg = "<a href='"+url+"'>"+url+"</a><br>" + html.toString();
			EmailUtils.send(mailTitle, msg);
		}catch(Exception e){
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			EmailUtils.send("Daily Report Error", aWriter.getBuffer().toString()+separator+html);
			e.printStackTrace();
		}finally{
			if(conn != null)conn.close();
		}
	}
	
	private static HtmlTable createImportInfoTable(Connection conn) throws Exception {
		HtmlTable importTable = new HtmlTable();
		importTable.attributes.put("border", "1");
		importTable.attributes.put("cellspacing", "0");
		importTable.attributes.put("cellpadding", "2");
		importTable.attributes.put("style", "font: 13px");
		
		HtmlTr headTr = new HtmlTr();
		headTr.attributes.put("onclick", "show(this)");
		HtmlTd headTd = new HtmlTd();
		headTd.text = "个股";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "行业";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "要闻";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "日期";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "操作";
		headTr.columns.add(headTd);
		importTable.rows.add(headTr);
		
		List<StkImportInfo> importInfos = JdbcUtils.list(conn, "select * from stk_import_info where care_flag=1 order by code asc,id desc", StkImportInfo.class);
		List params = new ArrayList();
		for(StkImportInfo importInfo:importInfos){
			HtmlTr tr = new HtmlTr();
			HtmlTd td = new HtmlTd();
			params.clear();
			params.add(importInfo.getCode());
			Stk stk = JdbcUtils.load(conn, "select code,name from stk where code=?", params, Stk.class);
			td.text = StkUtils.wrapName(stk.getName(), stk.getCode())+"["+StkUtils.wrapCode(stk.getCode())+"]";
			tr.columns.add(td);
			
			tr.columns.add(createIndustryColumn(conn,importInfo.getCode()));
			
			td = new HtmlTd();
			td.text = importInfo.getInfo();
			tr.columns.add(td);
			
			td = new HtmlTd();
			td.text = StkUtils.formatDate(importInfo.getInsertTime());
			tr.columns.add(td);
			
			tr.columns.add(createImportInfoCareColumn(String.valueOf(importInfo.getId())));
			
			importTable.rows.add(tr);
		}
		return importTable;
	}
 	
	private static String createIndustryCareTable() throws Exception {
		return null;
	}
	
	private static List<HtmlTable> createIndustryRankTable(Connection conn) throws Exception{
		List<HtmlTable> rankTables = new ArrayList<HtmlTable>();
		
		List params = new ArrayList();
		List<IndustryChange> ranks = new ArrayList<IndustryChange>();
		
		List<StkKline> shKline = JdbcUtils.list(conn, "select * from stk_kline where code='999999' order by kline_date desc", StkKline.class);
		String today = shKline.get(0).getKlineDate();
		List<StkIndustryType> inds = JdbcUtils.list(conn, "select * from stk_industry_type", StkIndustryType.class);
		for(StkIndustryType ind:inds){
			params.clear();
			params.add(ind.getId());
			List<Stk> stks = JdbcUtils.list(conn, "select a.code,a.name from stk a,stk_industry b where a.code=b.code and b.industry=?", params, Stk.class);
			for(String sPeriod:industryRankPeriods){
				int period = Integer.parseInt(StringUtils.trim(sPeriod));
				IndustryChange rank = new IndustryChange();
				rank.setIndustryType(ind);
				rank.setPeriod(period);
				
				for(Stk stk:stks){
					List<StkKline> stkKlines = (List<StkKline>)MyCacheUtils.get("kline_"+stk.getCode());
					if(stkKlines == null){
						params.clear();
						params.add(stk.getCode());
						params.add(61);
						stkKlines = JdbcUtils.list(conn, "select kline_date,close_change from (select * from stk_kline where code=? order by kline_date desc) where rownum<=?",params, StkKline.class);
						MyCacheUtils.put("kline_"+stk.getCode(), stkKlines);
					}
					StkKline startKline = shKline.get(period-1);
					int days = 0;
					for(StkKline stkKline:stkKlines){
						if(Integer.parseInt(stkKline.getKlineDate()) < Integer.parseInt(startKline.getKlineDate())){
							break;
						}
						days ++;
					}
					if(days >= stkKlines.size()){
						continue;
					}
					
					if(days >= Math.ceil(period * 0.8) && stkKlines.get(0).getCloseChange() != null && stkKlines.get(days).getCloseChange() != null){
						if(stkKlines.get(days).getCloseChange() != 0.0){
							StkChange stkChange = new StkChange();
							stkChange.setPeriod(period);
							stkChange.setStk(stk);
							double closeChange = (stkKlines.get(0).getCloseChange()-stkKlines.get(days).getCloseChange())/stkKlines.get(days).getCloseChange() * 100;
							//System.out.println(stk.getCode()+","+days+","+closeChange+","+stkKlines.get(0).getKlineDate()+","+stkKlines.get(days-1).getKlineDate());
							stkChange.setCloseChange(Double.parseDouble(StkUtils.number2String(closeChange, 2)));
							rank.addStkChange(stkChange);
						}
					}
					
				}
				Collections.sort(rank.getStkChanges(), new Comparator<StkChange>(){
					public int compare(StkChange arg0, StkChange arg1) {
						if(arg1.getCloseChange() >= arg0.getCloseChange()){
							return 1;
						}
						return -1;
					}});
				
				ranks.add(rank);
			}
				
		}
		
		for(IndustryChange a:ranks){
			List<StkChange> stkChanges = a.getStkChanges();
			int num = (int)(Math.ceil(stkChanges.size()*industryRankStkSamplePercentige) >= industryRankStkSampleMinNumber ?
					Math.ceil(stkChanges.size()*industryRankStkSamplePercentige) : industryRankStkSampleMinNumber);
			int realNum = 0;
			double totalChange = 0.0;
			for(int i=0;i<num && i<stkChanges.size();i++){
				totalChange += stkChanges.get(i).getCloseChange();
				realNum ++;
			}
			if(realNum != 0){
				a.setCloseChange(Double.parseDouble(StkUtils.number2String(totalChange/realNum,2)));
			}
		}
		Collections.sort(ranks,new Comparator<IndustryChange>(){
			public int compare(IndustryChange arg0, IndustryChange arg1) {
				if(arg0.getPeriod() > arg1.getPeriod()){
					return 1;
				}else if(arg0.getPeriod() == arg1.getPeriod()){
					if(arg1.getCloseChange() > arg0.getCloseChange()){
						return 1;
					}
				}
				return -1;
			}});
		
		int rank = 0;
		int periodFlag = 0;
		for(IndustryChange a:ranks){
			//System.out.println(a.getPeriod()+","+a.getIndustryType().getId()+","+a.getCloseChange()+","+a.getStkChanges());
			params.clear();
			params.add(a.getIndustryType().getId());
			params.add(a.getPeriod());
			params.add(today);
			int count = JdbcUtils.load(conn, "select count(1) from stk_industry_rank where industry_id=? and period=? and rank_date=?", params, int.class);
			if(count == 0){
				params.clear();
				params.add(a.getIndustryType().getId());
				params.add(a.getPeriod());
				params.add(today);
				if(periodFlag == 0 || periodFlag != a.getPeriod()){
					periodFlag = a.getPeriod();
					rank = 0;
				}
				rank ++;
				params.add(rank);
				params.add(a.getCloseChange());
				params.add(JdbcUtils.createClob(JsonUtils.getJsonString4JavaPOJO(a.getStkChanges())));
				JdbcUtils.update(conn, "insert into stk_industry_rank values (?,?,?,?,?,?)", params);
			}
		}
		
		//create html
		periodFlag = 0;
		int showIndustryNum = 0;
		for(IndustryChange a:ranks){
			HtmlTable rankTable = null;
			if(periodFlag == 0 || periodFlag != a.getPeriod()){
				periodFlag = a.getPeriod();
				showIndustryNum = 0;
				rankTable = new HtmlTable();
				rankTable.attributes.put("border", "1");
				rankTable.attributes.put("cellspacing", "0");
				rankTable.attributes.put("cellpadding", "2");
				rankTable.attributes.put("style", "font: 12px");
				HtmlTr headTr = new HtmlTr();
				headTr.attributes.put("onclick", "show(this)");
				HtmlTd headTd = new HtmlTd();
				headTd.text = "行业["+a.getPeriod()+"日]";
				headTr.columns.add(headTd);
				headTd = new HtmlTd();
				headTd.text = "历史排行";
				headTr.columns.add(headTd);
				headTd = new HtmlTd();
				headTd.text = "个股";
				headTr.columns.add(headTd);
				rankTable.rows.add(headTr);
				rankTables.add(rankTable);
			}else{
				if(showIndustryNum > 20)continue;
				rankTable = rankTables.get(rankTables.size()-1);
			}
			showIndustryNum ++;
			
			HtmlTr tr = new HtmlTr();
			tr.attributes.put("style", "display:none");
			HtmlTd td = new HtmlTd();
			td.text = a.getIndustryType().getName()+"["+a.getStkChanges().size()+"]"+createIndustryCare(String.valueOf(a.getIndustryType().getId()));
			tr.columns.add(td);
			td = new HtmlTd();
			params.clear();
			params.add(a.getIndustryType().getId());
			params.add(a.getPeriod());
			List<StkIndustryRank> listRanks = JdbcUtils.list(conn, "select rank from stk_industry_rank where industry_id=? and period=? order by rank_date desc", params, StkIndustryRank.class);
			List<String> tmp = new ArrayList<String>();
			for(int i=0;i<listRanks.size();i++){
				StkIndustryRank indRank = listRanks.get(i);
				tmp.add(indRank.getRank().toString());
				if(i == industryRankStkLineNumber-1)break;
			}
			td.text = StringUtils.join(tmp, ",");
			tr.columns.add(td);
			td = new HtmlTd();
			
			tmp.clear();
			for(int i=0;i<a.getStkChanges().size();i++){
				StkChange stk = a.getStkChanges().get(i);
				String str = stk.getStk().getName()+"["+StkUtils.wrapCode(stk.getStk().getCode())+"]["+StkUtils.wrapColorForChange(stk.getCloseChange())+"%]";
				String s = null;
				if(i==0){
					s = "<div style='float:left'>" + str;
				}else{
					if(i == industryRankStkShowNumber-1 && a.getStkChanges().size() > industryRankStkShowNumber-1){
						s = str + "</div><div onclick='show(this)'>>>></div><div style='display:none'>";
					}else if(i == industryRankStkShowNumber){
						s = str;
					}else{
						s = "," + str;
					}
				}
				td.text += s;
				if(i == industryRankStkShowMaxNumber-1)break;
			}
			td.text += "</div>";
			tr.columns.add(td);
			
			rankTable.rows.add(tr);
		}
		return rankTables;
	}
	
	private static HtmlTable createStkCareTable(Connection conn) throws Exception{
		HtmlTable careTable = new HtmlTable();
		careTable.attributes.put("border", "1");
		careTable.attributes.put("cellspacing", "0");
		careTable.attributes.put("cellpadding", "2");
		careTable.attributes.put("style", "font: 13px");
		
		HtmlTr headTr = new HtmlTr();
		headTr.attributes.put("onclick", "show(this)");
		HtmlTd headTd = new HtmlTd();
		headTd.text = "个股";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "行业";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "关注日期";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "个股新闻";
		headTr.columns.add(headTd);
		headTd = new HtmlTd();
		headTd.text = "操作";
		headTr.columns.add(headTd);
		careTable.rows.add(headTr);
		
		List<Stk> careStks = JdbcUtils.list(conn, "select * from stk where status=1 order by status_date", Stk.class);
		for(Stk stk:careStks){
			HtmlTr row = new HtmlTr();
			row.attributes.put("style", "display:none");
			//第一列
			HtmlTd column = DailyReport.createStkColumn(stk.getCode(), stk.getName());
			row.columns.add(column);
			
			//第二列 - 行业
			column = createIndustryColumn(conn,stk.getCode());
			row.columns.add(column);
			
			//第三列 开始关注日期
			column = new HtmlTd();
			int workingDays = StkUtils.getWorkingDays(stk.getStatusDate(), new Date());
			String strWDays = "";
			if( workingDays >= 5 && workingDays <= 10){
				strWDays = "<font color='#FF0000'>("+workingDays+")</font>";
			}else{
				strWDays = "("+workingDays+")";
			}
			column.text = StkUtils.sf_ymd.format(new Date(stk.getStatusDate().getTime())).substring(2, 10)+strWDays;
			row.columns.add(column);	
			
			//第四列 公司新闻
			column = new HtmlTd();
			String text = getInfoFromWindForCareStk(stk.getCode());
			
			if(stk.getEarningExpect() != null){
				if(text.length() > 0) text += "<br>";
				text += "[盈利预测]"+stk.getEarningExpect();
			}
			if(stk.getSaleLimit() != null){
				if(text.length() > 0) text += "<br>";
				text += "[解禁日]"+stk.getSaleLimit();
			}
			column.text = text.length()>0?text:html_empty;
			row.columns.add(column);
			
			//第五列 关注
			column = createCareColumn(stk.getCode());
			row.columns.add(column);
			
			careTable.rows.add(row);
			
			HtmlTr row2 = new HtmlTr();	
			row2.attributes.put("style", "display:none");
			getFnData(conn,stk,row2,row.columns.size(),1);
			
			careTable.rows.add(row2);
		}
		return careTable;
	}
	
	private static HtmlTable createStkDailyNewsTable(Connection conn){
		Date now = new Date();
		HtmlTable table = new HtmlTable();
		table.attributes.put("border", "1");
		table.attributes.put("cellspacing", "0");
		table.attributes.put("cellpadding", "2");
		table.attributes.put("style", "font: 13px");
		
		// a.code='002450' and
		List<Stk> stks = JdbcUtils.list(conn, "select a.* from stk a,stk_industry b where a.code=b.code and b.industry in (select id from stk_industry_type where source='wind') order by b.industry", Stk.class);
		for(Stk stk:stks){
			try{
				if(contains(stk.getName(), filterStkName)){
					continue;
				}
				double lastYearROE = getLastYearROE(conn,stk.getCode());
				if(lastYearROE < filterLastYearROE){
					continue;
				}
				
				HtmlTr row = new HtmlTr();
				//from hexun
				List<String> titleContainKeywords = new ArrayList<String>();
				getInfoFromHexun(conn,now,stk.getCode(),stk.getName(),titleContainKeywords);
				//from wind
				getInfoFromWind(conn,now,stk.getCode(),stk.getName(),titleContainKeywords);
				
				if(titleContainKeywords.size() > 0){
					if(stk.getEarningExpect() != null){
						titleContainKeywords.add("[盈利预测]"+stk.getEarningExpect());
					}
					if(stk.getSaleLimit() != null){
						titleContainKeywords.add("[解禁日]"+stk.getSaleLimit());
					}
					createReportTableRow(conn,row,stk.getCode(),stk.getName(),titleContainKeywords);
					table.rows.add(row);
					
					HtmlTr row2 = new HtmlTr();				
					getFnData(conn,stk,row2,row.columns.size(),2);
					table.rows.add(row2);
				}
				
			}catch(Exception e){
				List params = new ArrayList();
				params.add(stk.getCode());
				StringWriter aWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(aWriter));
				params.add(aWriter.getBuffer().toString());
				params.add(new Timestamp(new Date().getTime()));
				JdbcUtils.insert(conn, "insert into stk_error_log(code,error,insert_time) values(?,?,?)", params);
				e.printStackTrace();
			}
		}
		return table;
	}
	
	
	
	private static String getInfoFromWindForCareStk(String code) throws Exception {
		String loc = StkUtils.getStkLocation(code);
		StringBuilder careInfos = new StringBuilder();
		
		//wind公司新闻
		String url = "http://www.windin.com/Tools/NewsDetail.aspx?windcode="+code+"."+loc;
		String page = HttpUtils.get(url,null,"GBK");
		Node node = HtmlUtils.getNodeByAttribute(HtmlUtils.unescape(page),"","id","lblData");
		
		if(node != null){
			Span span = (Span)node;
			String data = StringUtils.substringBetween(span.getStringText(), ":[", "],");
			if(data != null){
				String[] infos = data.split(",\\u007B");
				for(String info:infos){
					if(info == null || info.length() == 0){
						continue;
					}
					String date = StringUtils.substringBetween(info, "\"newsdate\":\"", "\",\"caption");
					Date d = StkUtils.sf_ymd.parse(date);
					if(d.before(DateUtil.addDay(new Date(), -7))){
						continue;
					}
					String title = StringUtils.substringBetween(info, "\"caption\":\"", "\",\"source\"");
					String target = StringUtils.substringBetween(info, "\"target\":\"", "\"");
					
					HtmlA a = new HtmlA();
					a.text = "Wind";
					a.attributes.put("href", url);
						
					HtmlA a2 = new HtmlA();
					a2.text = title;
					a2.attributes.put("href", target);
					
					if(careInfos.length() > 0){
						careInfos.append("<br>");
					}
					careInfos.append("[").append(a.toHtml()).append("]").append(a2.toHtml()).append("(").append(date).append(")");
				}
			}
		}
		return careInfos.toString();
	}
	
	private static void getInfoFromHexun(Connection conn,Date now,String code,String name,List<String> titleContainKeywords) throws Exception {
		List params = new ArrayList();
		String page = HttpUtils.get("http://ggzx.stock.hexun.com/more.jsp?t=0&s=0&k="+code,null,"GBK");
		Node node = HtmlUtils.getNodeByAttribute(page,"","class","temp01");
		List<Node> li = HtmlUtils.getNodeListByTagName(node,"li");
		if(li == null || li.size() == 0){
			return;
		}

		String sql = "select * from stk_info_log where code=? and source='hexun'";
		params.add(code);
		StkInfoLog infoLog = JdbcUtils.load(conn, sql,params, StkInfoLog.class);
		params.clear();
		Node first = null;
		if(infoLog == null){
			first = li.get(0);
			List<Node> span = HtmlUtils.getNodeListByTagName(li.get(0), "span");
			if(span != null && span.get(0).toPlainTextString().length() > 0){
				String time = StringUtils.substringBetween(StringUtils.trim(span.get(0).toPlainTextString()), "(", " ");
				Date date = StkUtils.sf_ymd.parse(StkUtils.YEAR+"-"+time);
				if(date.after(DateUtil.addDay(new Date(), -2))){
					List<Node> link = HtmlUtils.getNodeListByTagName(li.get(0), "a");
					sql = "insert into stk_info_log(code,source,description,url,insert_time) values(?,?,?,?,?)";
					params.add(code);
					params.add("hexun");
					String title = link.get(0).toPlainTextString();
					params.add(title);
					String url = ((LinkTag)link.get(0)).getAttribute("href");
					params.add(url);
					if(StringUtils.contains(title, name) && contains(title, keywordsInclude) && !contains(title, keywordsExclude) && StkUtils.percentigeContainAndGreatThan(title,filterPercentige)){
						HtmlA a = new HtmlA();
						a.text = title;
						a.attributes.put("href", url);
						titleContainKeywords.add("[Hexun]"+a.toHtml()+StringUtils.trim(span.get(0).toPlainTextString()));
					}
					params.add(new Timestamp(now.getTime()));
					JdbcUtils.update(conn, sql, params);
				}
			}
		}else{
			for(Node n:li){
				if(n == null){
					continue;
				}
				List<Node> span = HtmlUtils.getNodeListByTagName(n, "span");
				String time = StringUtils.substringBetween(StringUtils.trim(span.get(0).toPlainTextString()), "(", " ");
				Date date = StkUtils.sf_ymd.parse(StkUtils.YEAR+"-"+time);
				if(date.before(DateUtil.addDay(new Date(), -2))){
					continue;
				}
				List<Node> link = HtmlUtils.getNodeListByTagName(n, "a");
				if(link == null)
					continue;
				LinkTag a = (LinkTag)link.get(0);
				String title = a.toPlainTextString();
				if(infoLog.getDescription().equalsIgnoreCase(title)){
					break;
				}
				if(first == null){
					first = n;
					sql = "update stk_info_log set description=?,url=?,insert_time=? where code=? and source='hexun'";
					params.add(title);
					params.add(((LinkTag)link.get(0)).getAttribute("href"));
					params.add(new Timestamp(now.getTime()));
					params.add(code);
					JdbcUtils.update(conn, sql, params);
				}
				
				if(StringUtils.contains(title, name) && contains(title, keywordsInclude) && !contains(title, keywordsExclude) && StkUtils.percentigeContainAndGreatThan(title,filterPercentige)){
					HtmlA a1 = new HtmlA();
					a1.text = title;
					a1.attributes.put("href", a.getAttribute("href"));
					titleContainKeywords.add("[Hexun]"+a1.toHtml()+StringUtils.trim(span.get(0).toPlainTextString()));
				}
			}
		}
		
	}
	
	private static void getInfoFromWind(Connection conn,Date now,String code,String name,List<String> titleContainKeywords) throws Exception {
		List params = new ArrayList();
		String loc = StkUtils.getStkLocation(code);;
		
		//wind大事提醒
		String url = "http://114.80.159.18/CorpEventsWeb/NewsEventAlert.aspx?windcode="+code+"."+loc+"&t=1";
		String page = HttpUtils.get(url,null,"GBK");
		Node node = HtmlUtils.getNodeByAttribute(page,"","id","TABLE2");
		
		if(node != null){
			String sql = "select * from stk_info_log where code=? and source='wind_ds'";
			params.add(code);
			StkInfoLog infoLog = JdbcUtils.load(conn, sql,params, StkInfoLog.class);
			params.clear();
			
			StringBuffer content = new StringBuffer();
			List<Node> list = HtmlUtils.getNodeListByTagNameAndAttribute(node, "td", "class", "NewsDetail");
			for(Node n:list){
				if(content.toString().contains(n.toPlainTextString()))
					continue;
				content.append(n.toPlainTextString()).append("<br>");
			}
			String title = content.toString();

			if(infoLog == null){
				sql = "insert into stk_info_log(code,source,description,url,insert_time) values(?,?,?,?,?)";
				params.add(code);
				params.add("wind_ds");
				params.add(title);
				params.add(url);
				if(contains(title, keywordsInclude) && !contains(title, keywordsExclude)){
					HtmlA a = new HtmlA();
					a.text = name+":大事提醒";
					a.attributes.put("href", url);
					titleContainKeywords.add("[Wind]"+a.toHtml());
				}
				params.add(new Timestamp(now.getTime()));
				JdbcUtils.update(conn, sql, params);
			}else{
				if(infoLog.getDescription() == null || !infoLog.getDescription().equalsIgnoreCase(title)){
					String title2 = null;
					if(infoLog.getDescription() == null){
						title2 = title;
					}else{
						String[] description = infoLog.getDescription().split("<br>");
						String[] replaces = new String[description.length];
						for(int k=0;k<replaces.length;k++){
							replaces[k]=StringUtils.EMPTY;
						}
						title2 = StringUtils.replaceEach(title, description, replaces);
					}
					if(contains(title2, keywordsInclude) && !contains(title2, keywordsExclude)){
						HtmlA a = new HtmlA();
						a.text = "大事提醒";
						a.attributes.put("href", url);
						titleContainKeywords.add("["+a.toHtml()+"]"+StkUtils.wrapColorForKeyword(title2, keywordsInclude));
						
						if(StkUtils.percentigeContainAndGreatThan(title2,50.0)){
							params.clear();
							params.add(code);
							params.add("[大事提醒]"+title2);
							JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,care_flag,info) select (select nvl(max(id)+1,100001) from stk_import_info),?,1,sysdate,1,? from dual", params);
						}
					}
					sql = "update stk_info_log set description=?,url=?,insert_time=? where code=? and source='wind_ds'";
					params.clear();
					params.add(ChineseUtils.lengthForOracle(title)>4000?StringUtils.EMPTY:title);
					params.add(url);
					params.add(new Timestamp(now.getTime()));
					params.add(code);
					JdbcUtils.update(conn, sql, params);
					
				}
				
			}
		}

		//wind公司新闻
		url = "http://www.windin.com/Tools/NewsDetail.aspx?windcode="+code+"."+loc;
		page = HttpUtils.get(url,null,"GBK");
		node = HtmlUtils.getNodeByAttribute(HtmlUtils.unescape(page),"","id","lblData");
		Span span = (Span)node;
		String data = StringUtils.substringBetween(span.getStringText(), ":[", "],");
		
		if(node != null && data != null){
			String[] infos = data.split(",\\u007B");
			
			params.clear();
			String sql = "select * from stk_info_log where code=? and source='wind_news'";
			params.add(code);
			StkInfoLog infoLog = JdbcUtils.load(conn, sql,params, StkInfoLog.class);
			params.clear();
			
			String first = null;
			if(infoLog == null){
				first = infos[0];
				
				sql = "insert into stk_info_log(code,source,description,url,insert_time) values(?,?,?,?,?)";
				params.add(code);
				params.add("wind_news");
				String title = StringUtils.substringBetween(first, "\"caption\":\"", "\",\"source\"");
				params.add(title);
				String target = StringUtils.substringBetween(first, "\"target\":\"", "\"");
				params.add(target);
				if(contains(title, keywordsInclude) && !contains(title, keywordsExclude) && StkUtils.percentigeContainAndGreatThan(title,filterPercentige)){
					HtmlA a = new HtmlA();
					a.text = "Wind";
					a.attributes.put("href", url);
					
					HtmlA a2 = new HtmlA();
					a2.text = title;
					a2.attributes.put("href", target);
					
					String date = StringUtils.substringBetween(first, "\"newsdate\":\"", "\",\"caption");
					Date d = StkUtils.sf_ymd.parse(date);
					if(d.after(DateUtil.addDay(new Date(), -2))){
						titleContainKeywords.add("["+a.toHtml()+"]"+a2.toHtml()+"("+date+")");
					}
					
				}
				params.add(new Timestamp(now.getTime()));
				JdbcUtils.update(conn, sql, params);
			}else{ 
				for(String info:infos){
					if(info == null || info.length() == 0){
						continue;
					}
					String date = StringUtils.substringBetween(info, "\"newsdate\":\"", "\",\"caption");
					Date d = StkUtils.sf_ymd.parse(date);
					if(d.before(DateUtil.addDay(new Date(), -2))){
						continue;
					}
					
					String title = StringUtils.substringBetween(info, "\"caption\":\"", "\",\"source\"");
					if(infoLog.getDescription().equalsIgnoreCase(title)){
						break;
					}
					String target = StringUtils.substringBetween(first, "\"target\":\"", "\"}");
					if(first == null){
						first = info;
						sql = "update stk_info_log set description=?,url=?,insert_time=? where code=? and source='wind_news'";
						params.add(title);
						params.add(target);
						params.add(new Timestamp(now.getTime()));
						params.add(code);
						JdbcUtils.update(conn, sql, params);
					}
					if(/*StringUtils.contains(title, name) && */contains(title, keywordsInclude)&& !contains(title, keywordsExclude) && StkUtils.percentigeContainAndGreatThan(title,filterPercentige)){
						HtmlA a = new HtmlA();
						a.text = "Wind";
						a.attributes.put("href", url);
						
						HtmlA a2 = new HtmlA();
						a2.text = title;
						a2.attributes.put("href", target);
						
						titleContainKeywords.add("["+a.toHtml()+"]"+a2.toHtml()+"("+date+")");
					}
				}
			}
		}
		
	}
	
	private static void getFnData(Connection conn,Stk stk,HtmlTr row,int colspan,int from) {
		HtmlTd td1 = new HtmlTd();
		td1.attributes.put("colspan", String.valueOf(colspan)); 
		td1.text = html_empty;
		
		HtmlTable table = new HtmlTable();
		table.attributes.put("style", "font: 12px");
		table.attributes.put("width", "100%");
		
		boolean hasThead = false;
		
		List params = new ArrayList();
		List<StkFnType> fnTypes = JdbcUtils.list(conn, "select * from stk_fn_type order by type", StkFnType.class);
		for(StkFnType fnType:fnTypes){
			params.clear();
			params.add(stk.getCode());
			params.add(fnType.getType());
			List<StkFnData> fnDatas = JdbcUtils.list(conn,"select * from stk_fn_data where code=? and type=? and fn_value is not null order by fn_date desc",params,StkFnData.class);
			
			if(fnDatas.size() < fnReportQuarts){
				continue;
			}
			
			if(!hasThead){//table head
				HtmlTr tr = new HtmlTr(); 
				tr.attributes.put("onclick", "show(this)");
				HtmlTd td = new HtmlTd();
				td.text = html_empty;
				tr.columns.add(td);
				for(int i = 0; i <= (fnReportQuarts - 1) && i < fnDatas.size(); i++){//最近5个季度
					td = new HtmlTd();
					td.text = fnDatas.get(i).getFnDate().substring(2,8);
					if(fnDatas.get(i).getFnDate().compareTo(stk.getListingDate()) < 0){
						td.text += "*"; //上市前
					}
					tr.columns.add(td);
				}
				td = new HtmlTd();
				td.text = html_empty;
				tr.columns.add(td);
				int n = 0;
				for(StkFnData fnData:fnDatas){//最近3年
					if(fnData.getFnDate().contains("1231")){
						td = new HtmlTd();
						td.text = fnData.getFnDate().substring(2,8);
						if(fnData.getFnDate().compareTo(stk.getListingDate()) < 0){
							td.text += "*"; //上市前
						}
						tr.columns.add(td);
						n ++;
						if(n >= fnReportYears)break;
					}
				}
				table.rows.add(tr);
				hasThead = true;
			}
			
			List<StkFnData> filterDatas = filter(fnDatas,StkUtils.YEAR - fnReportYears,"1231");
			List<Double> fnValues = ma(filterDatas,2);
			
			boolean hasLessValue = false;
			/*for(Double value:fnValues){
				if(fnType.getNotLessValue() != null){
					if(value.doubleValue() < fnType.getNotLessValue().doubleValue()){
						hasLessValue = true;
						break;
					}
				}else{
					if(value.doubleValue() > fnType.getNotGreatValue().doubleValue()){
						hasLessValue = true;
						break;
					}
				}
			}*/
			
			if(hasLessValue){//财务指标不好
				String color = "color:#999999";
				table.rows.add(createFnDataRow(table.rows.get(0),fnType,fnDatas,color,from));
			}else{//财务指标较好or没有数据
				table.rows.add(createFnDataRow(table.rows.get(0),fnType,fnDatas,null,from));
			}
		}
		
		td1.text = table.toHtml();
		row.columns.add(td1);
	}
	
	private static HtmlTr createFnDataRow(HtmlTr th,StkFnType fnType,List<StkFnData> fnDatas,String color,int from){
		HtmlTr row2 = new HtmlTr();
		if(from == 1){
			row2.attributes.put("style", "display:none");
		}
		if(color != null){
			row2.attributes.put("style", color);
		}
		HtmlTd td = new HtmlTd();
		//td.text = fnType.getName()+"("+StkUtils.formatNumber(fnType.getNotLessValue()==null?fnType.getNotGreatValue():fnType.getNotLessValue(),2)+")";
		row2.columns.add(td);
		for(int i=1;i<th.columns.size();i++){
			HtmlTd thead = th.columns.get(i);
			td = new HtmlTd();
			StkFnData fnData = get(fnDatas,StringUtils.replace("20"+thead.text, "*", ""));
			if(fnData == null){
				td.text = html_empty;
			}else{
				td.text = String.valueOf(fnData.getFnValue()==null?"":fnData.getFnValue());
			}
			
			if((i == 1 || i == 2) && fnData != null){//第1，2季同比增长是否>30%
				StkFnData lastYearFnData = get(fnDatas,String.valueOf(Integer.parseInt(fnData.getFnDate())-10000));
				if(fnData.getFnValue() > 0 && lastYearFnData != null && lastYearFnData.getFnValue() < 0){//一正一负
					if(i==1){//第一季
						td.attributes.put("style","color:#FF0000");
					}
					if(i==2){//第二季
						td.attributes.put("style","color:#FF6600");
					}
					td.text += "(+)";
				}else if(fnData.getFnValue() < 0 && lastYearFnData != null && lastYearFnData.getFnValue() > 0){//一负一正
					if(i==1){//第一季
						td.attributes.put("style","color:#336600");
					}
					if(i==2){//第二季
						td.attributes.put("style","color:#339900");
					}
					td.text += "(-)";
				}else if(fnData.getFnValue() < 0 && lastYearFnData != null && lastYearFnData.getFnValue() < 0){//都是负的
					double upByYearGreaterThan30 = upByYear(fnDatas,fnData.getFnDate());
					if(upByYearGreaterThan30 <= -0.2){
						if(i==1){//第一季
							td.attributes.put("style","color:#FF0000");
						}
						if(i==2){//第二季
							td.attributes.put("style","color:#FF6600");
						}
						td.text += "("+StkUtils.number2String(upByYearGreaterThan30*100,0)+"%)";
					}else if(upByYearGreaterThan30 >= 0.2){
						if(i==1){//第一季
							td.attributes.put("style","color:#336600");
						}
						if(i==2){//第二季
							td.attributes.put("style","color:#339900");
						}
						td.text += "("+StkUtils.number2String(upByYearGreaterThan30*100,0)+"%)";
					}
				}else{//都是正的
					double upByYearGreaterThan30 = upByYear(fnDatas,fnData.getFnDate());
					if(upByYearGreaterThan30 >= 0.2){
						if(i==1){//第一季
							td.attributes.put("style","color:#FF0000");
						}
						if(i==2){//第二季
							td.attributes.put("style","color:#FF6600");
						}
						td.text += "("+StkUtils.number2String(upByYearGreaterThan30*100,0)+"%)";
					}else if(upByYearGreaterThan30 <= -0.2){
						if(i==1){//第一季
							td.attributes.put("style","color:#336600");
						}
						if(i==2){//第二季
							td.attributes.put("style","color:#339900");
						}
						td.text += "("+StkUtils.number2String(upByYearGreaterThan30*100,0)+"%)";
					}
				}
			}
			row2.columns.add(td);
		}
		return row2;
	}
	
	private static double upByYear(List<StkFnData> fnDatas,String fnDate){
		StkFnData curFnData = get(fnDatas,fnDate);
		if(curFnData != null && curFnData.getFnValue() != null){
			StkFnData lastYearFnData = get(fnDatas,String.valueOf(Integer.parseInt(fnDate)-10000));
			if(lastYearFnData != null && lastYearFnData.getFnValue() != null){
				double per = (curFnData.getFnValue().doubleValue()-lastYearFnData.getFnValue().doubleValue())/lastYearFnData.getFnValue().doubleValue();
				return per;
			}
		}
		return 0.0;
	}
	
	private static List<Double> ma(List<StkFnData> fnDatas,int n){
		List<Double> result = new ArrayList<Double>();
		for(int i=0;i<=fnDatas.size()-n;i++){
			double value = 0.0;
			for(int j=0;j<n;j++){
				value += fnDatas.get(i+j).getFnValue();
			}
			result.add(value/n);
		}
		return result;
	}
	
	private static List<StkFnData> filter(List<StkFnData> fnDatas, int year,String filterDate){
		List<StkFnData> result = new ArrayList<StkFnData>();
		for(StkFnData fnData:fnDatas){
			if(fnData.getFnDate().contains(filterDate) && fnData.getFnDate().compareTo(year+filterDate) >= 0){
				result.add(fnData);
			}
		}
		return result;
	}
	
	private static StkFnData get(List<StkFnData> fnDatas,String fnDate){
		for(StkFnData fnData:fnDatas){
			if(fnData.getFnDate().equals(fnDate)){
				return fnData;
			}
		}
		return null;
	}
	
	private static void createReportTableRow(Connection conn,HtmlTr tr,String code,String name,List<String> titleContainKeywords) throws Exception{
		HtmlTd td = null;
		//第一列 - 股票
		td = createStkColumn(code,name);
		tr.columns.add(td);
		
		//第二列 - 行业
		td = createIndustryColumn(conn,code);
		tr.columns.add(td);
		
		//第三列 - 资讯
		td = new HtmlTd();
		td.attributes.put("width", "50%");//360px
		for(String s:titleContainKeywords){
			String br = StringUtils.EMPTY;
			if(td.text.length() > 0){
				br = "<br>";
			}
			td.text = td.text+br+s;
		}
		tr.columns.add(td);
		
		//第四列 - Action
		td = createCareColumn(code);
		tr.columns.add(td);
		
	}
	
	private static HtmlTd createStkColumn(String code,String name){
		HtmlTd td = new HtmlTd();
		
		HtmlA link = new HtmlA();
		link.text = name;
		link.attributes.put("href", "http://ggzx.stock.hexun.com/more.jsp?t=0&s=0&k="+code);
		td.text = link.toHtml();
		
		link = new HtmlA();
		link.text = "F9";
		String loc = StkUtils.getStkLocation(code);
		link.attributes.put("href", "http://www.windin.com/home/stock/html/"+code+"."+loc+".shtml");
		td.text = td.text+"["+StkUtils.wrapCode(code)+"]"+"["+link.toHtml()+"]";
		
		link = new HtmlA();
		link.text = "情报";
		link.attributes.put("href", "http://www.windin.com/Tools/IntelligenceDetail.aspx?windcode="+code+"."+loc);
		td.text = td.text+"["+link.toHtml()+"]";
		
		return td;
	}
	
	private static HtmlTd createIndustryColumn(Connection conn,String code){
		HtmlTd td = new HtmlTd();
		List params = new ArrayList();
		params.add(code);
		List<String> inds = JdbcUtils.list(conn, "select t.name from stk_industry s,stk_industry_type t where s.industry=t.id and s.code=?", params, String.class);
		td.text = StringUtils.join(inds, ",");
		return td;
	}
	
	private static HtmlTd createCareColumn(String code){
		HtmlTd td = new HtmlTd();
		HtmlA link = new HtmlA();
		link.text = "关注";
		link.attributes.put("href", "http://210.13.77.68:8180/insurance/loginPage.do?p=care&c="+code);
		HtmlA a = new HtmlA();
		a.text = "取消";
		a.attributes.put("href", "http://210.13.77.68:8180/insurance/loginPage.do?p=uncare&c="+code);
		td.text = "["+link.toHtml()+"]["+a.toHtml()+"]";
		return td;
	}
	
	private static String createIndustryCare(String industryId){
		HtmlA link = new HtmlA();
		link.text = "关注";
		link.attributes.put("href", "http://210.13.77.68:8180/insurance/loginPage.do?p=ind_care&c="+industryId);
		HtmlA a = new HtmlA();
		a.text = "取消";
		a.attributes.put("href", "http://210.13.77.68:8180/insurance/loginPage.do?p=ind_uncare&c="+industryId);
		return "["+link.toHtml()+"]["+a.toHtml()+"]";
	}
	
	private static HtmlTd createImportInfoCareColumn(String id){
		HtmlTd td = new HtmlTd();
		HtmlA link = new HtmlA();
		link.text = "关注";
		link.attributes.put("href", "http://210.13.77.68:8180/insurance/loginPage.do?p=imp_care&c="+id);
		HtmlA a = new HtmlA();
		a.text = "取消";
		a.attributes.put("href", "http://210.13.77.68:8180/insurance/loginPage.do?p=imp_uncare&c="+id);
		td.text = "["+link.toHtml()+"]["+a.toHtml()+"]";
		return td;
	}
	
	private static boolean contains(String str,String[] array) throws Exception{
		for(String s:array){
			if(StringUtils.contains(new String(str.getBytes("GBK")), s)){
				return true;
			}
		}
		return false;
	}
	
	private static double getLastYearROE(Connection conn,String code){
		List params = new ArrayList();
		params.add(code);
		List<StkFnData> fnDatas = JdbcUtils.list(conn,"select * from stk_fn_data where code=? and type=1 and fn_date like '%1231%' and fn_value is not null order by fn_date desc",params,StkFnData.class);
		for(StkFnData fn:fnDatas){
			return fn.getFnValue();
		}
		return 10.0;
	}
	
	
}
