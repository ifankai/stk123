package com.stk123.task;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.TableTag;

import com.stk123.bo.StkInternetSearch;
import com.stk123.bo.StkTransAccount;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.connection.ConnectionPool;
import com.stk123.tool.thread.pool.ThreadPoolUtils;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.collection.Name2ListSet;
import com.stk123.tool.util.collection.Name2Value;
import com.stk123.web.StkDict;


public class InternetSearch {
	
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		final ConnectionPool pool = ConnectionPool.getInstance();
		try {
			List<StkInternetSearch> searchs = JdbcUtils.list(pool.getConnection(), "select * from stk_internet_search where status=1 order by search_source", StkInternetSearch.class);
			List<Callable> tasks = new ArrayList<Callable>();
			for(final StkInternetSearch search : searchs){
				Callable task = new Callable(){
					public Object call() throws Exception {
						Connection conn = null; 
						try{
							conn = pool.getConnection();
							switch(search.getSearchSource().intValue()){
								case 1 :
									return parseSinaBlogPage(conn, search);
								case 10 :
									return parseStkAccountInfo(conn, search);
								case 11 :
									return parseGov(conn,search);
								case 20 :
									//return parseGovNdrc(conn, search);
								default :
									return null;
							}
						}finally{
							pool.release(conn);
						}
					}
				};
				tasks.add(task);	
			}
			List<Object> results = ThreadPoolUtils.run(tasks,2);
			
			Name2ListSet<String,String> set = new Name2ListSet<String,String>();
			for(Object result : results){
				if(result != null){
					Map<String,Object> map = (Map<String,Object>)result;
					StkInternetSearch search = (StkInternetSearch)map.get("search");
					String title = StkDict.getDict(StkDict.INTERNET_SEARCH_TYPE, search.getSearchSource().toString());
					set.add(title, (List<String>)map.get("result"));
				}
			}
			
			if(set.size() > 0){
				StringBuffer sb = new StringBuffer();
				for(Name2Value<String, List<String>> pair : set.getList()){
					sb.append("<b>"+pair.getName()+":</b><br>");
					for(String txt : pair.getValue()){
						sb.append("&nbsp;&nbsp;").append(txt).append("<br>");
					}
				}
				//System.out.println(sb.toString());
				String content = sb.toString();
				EmailUtils.send("【新浪博客最新动态,日期:"+StkUtils.getToday()+"】", content);
				if(content.contains("十进宫")){
					//EmailUtils.send("kai.fan@suncorp.com.au", "十进宫", content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionUtils.insertLog(pool.getConnection(), e);
		} finally {
			if (pool != null)
				pool.closePool();
		}
	}
	
	public static Map<String,Object> parseGov(Connection conn, StkInternetSearch search) throws Exception{
		String page = HttpUtils.get(search.getSearchUrl(), "UTF-8");
		List<Node> links = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "a", "class", "l14");
		List<String> results = new ArrayList<String>();
		int i = 0;
		for(Node link : links){
			String text = link.toHtml();
			if(text != null){
				text = StringUtils.replace(text, "..", "http://www.gov.cn");
				if(addResult(search,text,i,results)){
					break;
				}
			}
		}
		if(results.size() > 0){
			updateSearch(conn, results.get(0), search);
			return map(search,results);
		}
		return null;
	}
	
	//发改委 - 新闻发布
	public static Map<String, Object> parseGovNdrc(Connection conn, StkInternetSearch search) throws Exception {
		List<String> results = new ArrayList<String>();
		int i = 0;
		String page = HttpUtils.get(search.getSearchUrl(), "utf-8");
		Node node = HtmlUtils.getNodeByAttribute(page, null, "class", "list_02 clearfix");
		List<Node> lis = HtmlUtils.getNodeListByTagName(node, "li");
		for(Node li : lis){
			Node a = HtmlUtils.getNodeByTagName(li, "a");
			if(a != null){
				String date = StringUtils.substringBetween(li.toHtml(), "<font class=\"date\">", "</font>");
				String text = StringUtils.replace(a.toHtml(), "./", search.getSearchUrl())+"["+date+"]";
				if(addResult(search,text,i,results)){
					break;
				}
			}
		}
		if(results.size() > 0){
			updateSearch(conn, results.get(0), search);
			return map(search,results);
		}
		return null;
	}
	
	public static Map<String,Object> parseSinaBlogPage(Connection conn, StkInternetSearch search) throws Exception {
		String page = HttpUtils.get(search.getSearchUrl(), null, "utf-8");
		List<Node> aNode = HtmlUtils.getNodeListByTagName(page, null, "a");
		int i = 0;
		List<String> results = new ArrayList<String>();
		for(Node a:aNode){
			TagNode node = (TagNode)a;
			String text = a.toHtml();
			if(node.getAttribute("href").startsWith("http://blog.sina.com.cn/s/blog")){
				String title = StringUtils.substringBetween(text, "title=\"", "\"");
				if(StringUtils.isEmpty(title)){
					title = a.toPlainTextString();
				}
				String msg = node.toPlainTextString();
				text = StringUtils.replace(text, msg, title);
				//System.out.println(text+",,,,,,,,,,"+msg);
				if(addResult(search,text,i,results)){
					break;
				}
			}
		}
		List<String> urlsSinaBlog = new ArrayList<String>();
		if(results.size() > 0){
			updateSearch(conn, results.get(0), search);
			for(String s : results){
				urlsSinaBlog.add("[<a target='_blank' href='"+search.getSearchUrl()+"'>"+search.getDesc1()+"</a>]"+s);
			}
			return map(search,urlsSinaBlog);
		}
		return null;
	}
	
	public static String createStkAccountInfoTable(Connection conn, int cnt) throws Exception{
		//List<StkTransAccount> list = JdbcUtils.list(conn, "select * from stk_trans_account order by week_start_date desc limit 0,"+cnt, StkTransAccount.class);
		List<StkTransAccount> list = JdbcUtils.list(conn, "select * from stk_trans_account order by week_start_date desc", StkTransAccount.class);
		List<String> titles = new ArrayList<String>();
		titles.add("日期(天数)");titles.add("有效账户");titles.add("新增股票账户");titles.add("持仓A股账户");titles.add("交易A股账户");
		titles.add("持仓交易活跃度");titles.add("有效交易活跃度");titles.add("新增交易活跃度");titles.add("结果1");titles.add("结果2");
		List<List<String>> datas = new ArrayList<List<String>>();
		for(StkTransAccount data : list){
			List<String> row = new ArrayList<String>();
			row.add(data.getWeekStartDate()+"-"+data.getWeekEndDate()+"["+StkUtils.getDaysBetween(StkUtils.sf_ymd2.parse(data.getWeekStartDate()), StkUtils.sf_ymd2.parse(data.getWeekEndDate()))+"]");
			row.add(StkUtils.number2String(data.getValidAccount(), 2));
			//newAccount > 200000 green
			if(data.getNewAccount() > 200000){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getNewAccount(), 2), "green"));
			}else{
				row.add(StkUtils.number2String(data.getNewAccount(), 2));
			}
			row.add(StkUtils.number2String(data.getHoldAAccount(), 2));
			//transAAccount < 650 red
			if(data.getTransAAccount() < 650){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getTransAAccount(), 2),"red"));
			}else{
				row.add(StkUtils.number2String(data.getTransAAccount(), 2));
			}
			//holdTransActivity > 0.22 green, holdTransActivity < 0.12 red
			if(data.getHoldTransActivity() > 0.70){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getHoldTransActivity()*100,2)+"%","green"));
				if(data.getHoldTransActivity() >= 0.80){
					try{
					//EmailUtils.send("【风险】【股市账户统计】交易活跃度大于45！！！", "交易活跃度:"+data.getHoldTransActivity());
					}catch(Exception e){}
				}
			}else if(data.getHoldTransActivity() < 0.20){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getHoldTransActivity()*100,2)+"%","red"));
			}else{
				row.add(StkUtils.number2String(data.getHoldTransActivity()*100,2)+"%");
			}
			//validTransActivity < 0.05 red
			if(data.getValidTransActivity() < 0.09){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getValidTransActivity()*100,2)+"%","red"));
			}else{
				row.add(StkUtils.number2String(data.getValidTransActivity()*100,2)+"%");
			}
			//newTransActivity > 0.018 green
			if(data.getNewTransActivity() > 0.018){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getNewTransActivity()*100,2)+"%","green"));
			}else{
				row.add(StkUtils.number2String(data.getNewTransActivity()*100,2)+"%");
			}
			//result1 <= 55 green, result1 >= 200 red
			if(data.getResult1() <= 15){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getResult1(),2),"green"));
			}else if(data.getResult1() >= 100){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getResult1(),2),"red"));
			}else{
				row.add(StkUtils.number2String(data.getResult1(),2));
			}
			//result2 <= 50 green, result2 >= 200 red
			if(data.getResult2() <= 10){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getResult2(),2),"green"));
				if(data.getResult2() <= 5){
					try{
					//EmailUtils.send("【风险】【股市账户统计】Result2 < 12 ！！！", "Result2:"+data.getResult2());
					}catch(Exception e){}
				}
			}else if(data.getResult2() >= 100){
				row.add(StkUtils.setHtmlFontColor(StkUtils.number2String(data.getResult2(),2),"red"));
			}else{
				row.add(StkUtils.number2String(data.getResult2(),2));
			}
			datas.add(row);
		}
		return StkUtils.createHtmlTable(titles, datas);
	}
	
	
	public static String parseStkAccountInfo(Connection conn, StkInternetSearch search) throws Exception {
		String page = HttpUtils.get(search.getSearchUrl(), null, "GBK");
		Node node = HtmlUtils.getNodeByAttribute(page, null, "style", "WIDTH: 100%; BORDER-COLLAPSE: collapse");
		if(node == null)return null;
		String date = null;
		String startDate = null;
		String endDate = null;
		List<Node> nodeDate = HtmlUtils.getNodeListByTagName(page, null, "h2");
		if(nodeDate != null && nodeDate.size() > 0){
			date = StringUtils.substringBetween(nodeDate.get(0).toPlainTextString(), "（", "）");
			if(date.equals(search.getLastSearchText())){
				return null;
			}
			String[] ss = date.split("-");
			startDate = StringUtils.replace(ss[0], ".", "");
			endDate = StringUtils.replace(ss[1], ".", "");;
		}
		String validAccount = null;
		//newAccount > 200000 green
		String newAccount = null;
		String holdAAccount = null;
		//transAAccount < 650 red
		String transAAccount = null;
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)node, 0, 0);
		for(Map.Entry<String, Map<String, String>> data : datas.entrySet()){
			if(StringUtils.contains(data.getKey(), "投资者数（万）")){
				for(Map.Entry<String, String> value : data.getValue().entrySet()){
					if(StringUtils.contains(value.getKey(), "二、期末投资者数量")){
						validAccount = StringUtils.replace(StringUtils.replace(StringUtils.trim(value.getValue()), ",", ""), "&nbsp;", "");
					}else if(StringUtils.contains(value.getKey(), "一、新增投资者数量")){
						newAccount = StringUtils.replace(StringUtils.replace(StringUtils.trim(value.getValue()), ",", ""), "&nbsp;", "");
						newAccount = String.valueOf(Double.parseDouble(newAccount)*10000);
					}else if(StringUtils.contains(value.getKey(), "持有A股的投资者")){
						holdAAccount = StringUtils.replace(StringUtils.replace(StringUtils.trim(value.getValue()), ",", ""), "&nbsp;", "");
					}else if(StringUtils.contains(value.getKey(), "交易A股的投资者")){
						transAAccount = StringUtils.replace(StringUtils.replace(StringUtils.trim(value.getValue()), ",", ""), "&nbsp;", "");
					}
				}
			}
		}
		int days = StkUtils.getDaysBetween(StkUtils.sf_ymd2.parse(startDate), StkUtils.sf_ymd2.parse(endDate));
		//holdTransActivity > 0.22 green, holdTransActivity < 0.12 red
		double holdTransActivity = Double.parseDouble(transAAccount)/days*5/Double.parseDouble(holdAAccount);
		//validTransActivity < 0.05 red
		double validTransActivity = Double.parseDouble(transAAccount)/Double.parseDouble(validAccount);
		//newTransActivity > 0.018 green
		double newTransActivity = Double.parseDouble(newAccount)/days*5/10000/Double.parseDouble(transAAccount);
		//result1 <= 55 green, result1 >= 200 red
		double result1 = 100/holdTransActivity/validTransActivity*newTransActivity;
		//result2 <= 50 green, result2 >= 200 red
		double result2 = 100/holdTransActivity/validTransActivity*newTransActivity/newTransActivity/100;
		
		List params = new ArrayList();
		params.add(startDate);
		params.add(endDate);
		params.add(validAccount);
		params.add(newAccount);
		params.add(holdAAccount);
		params.add(transAAccount);
		params.add(holdTransActivity);
		params.add(validTransActivity);
		params.add(newTransActivity);
		params.add(result1);
		params.add(result2);
		params.add(startDate);
		JdbcUtils.insert(conn, "insert into stk_trans_account select (select nvl(max(id)+1,100001) from stk_trans_account),?,?,?,?,?,?,?,?,?,?,? from dual where not exists (select 1 from stk_trans_account where week_start_date=?)", params);
		
		//return date;
		
		if(date != null){
			updateSearch(conn, date, search);
			EmailUtils.send("【股票账户数据,日期:"+date+"】", createStkAccountInfoTable(conn, 50));
		}
		return null;
	}
	
	public static void updateStkAccountResult(Connection conn) throws Exception{
		List<StkTransAccount> list = JdbcUtils.list(conn, "select * from stk_trans_account where id<=100088", StkTransAccount.class);
		for(StkTransAccount tran : list){
			System.out.println(tran.getId());
			String startDate = tran.getWeekStartDate();
			String endDate = tran.getWeekEndDate();
			double transAAccount = tran.getTransAAccount()/1.5;
			double holdAAccount = tran.getHoldAAccount()/1.5;
			double validAccount = tran.getValidAccount()/2;
			double newAccount = tran.getNewAccount()/2.5;
			
			int days = StkUtils.getDaysBetween(StkUtils.sf_ymd2.parse(startDate), StkUtils.sf_ymd2.parse(endDate));
			double holdTransActivity = transAAccount/days*5/holdAAccount;
			//validTransActivity < 0.05 red
			double validTransActivity = transAAccount/validAccount;
			//newTransActivity > 0.018 green
			double newTransActivity = newAccount/days*5/10000/transAAccount;
			//result1 <= 55 green, result1 >= 200 red
			double result1 = 100/holdTransActivity/validTransActivity*newTransActivity;
			//result2 <= 50 green, result2 >= 200 red
			double result2 = 100/holdTransActivity/validTransActivity*newTransActivity/newTransActivity/100;
			
			List params = new ArrayList();
			params.add(holdTransActivity);
			params.add(validTransActivity);
			params.add(newTransActivity);
			params.add(result1);
			params.add(result2);
			params.add(tran.getId());
			JdbcUtils.update(conn, "update stk_trans_account set hold_trans_activity=?,valid_trans_activity=?,new_trans_activity=?,result_1=?,result_2=? where id=?", params);
		}
	}
	
	public static List<String> parseEastMoneyIndustry(StkInternetSearch search) throws Exception {
		String page = HttpUtils.get(search.getSearchUrl(), null, "GBK");
		Node node = HtmlUtils.getNodeByAttribute(page, null, "id", "s1-cont1");
		//System.out.println(node.toHtml());
		List<Node> nodes = HtmlUtils.getNodeListByTagName(node, "ul");
		int i = 0;
		List<String> results = new ArrayList<String>();
		for(Node n : nodes){
			if(i == 0)continue;
			//System.out.println(n.toHtml());
			Node n2 = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "report");
			if(n2 != null){
				Node n3 = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "reportObj");
				Node n4 = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "date");
				String s = n2.getLastChild().toHtml()+"["+n3.getLastChild().toHtml()+"] ("+n4.toPlainTextString()+")";
				String title = StringUtils.substringBetween(n2.getLastChild().toHtml(), "title=\"", "\"");
				String msg = n2.getLastChild().toPlainTextString();
				s = StringUtils.replace(s, msg, title);
				s = StringUtils.replace(s, "href=\"", "href=\"http://data.eastmoney.com");
				//System.out.println(s);
				if(addResult(search,s,i,results)){
					break;
				}
			}
		}
		return results;
	}
	
	public static void updateSearch(Connection conn, String lastText, StkInternetSearch search){
		List params = new ArrayList();
		params.add(lastText);
		params.add(search.getSearchUrl());
		//JdbcUtils.update(conn, "update stk_internet_search set last_search_text=?,update_time=sysdate() where search_url=?", params);
		JdbcUtils.update(conn, "update stk_internet_search set last_search_text=?,update_time=sysdate where search_url=?", params);
	}
	
	private static Map<String, Object> map(StkInternetSearch search, List<String> results){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("search", search);
		map.put("result", results);
		return map;
	}
	
	public static boolean addResult(StkInternetSearch search, String text, int i, List<String> results){
		if(search.getLastSearchText() == null){
			search.setLastSearchText(text);
			results.add(text);
			return true;
		}else{
			if(search.getLastSearchText().equals(text) || i++ >= 10){
				return true; 
			}else{
				results.add(text);
			}
		}
		return false;
	}
	
}

