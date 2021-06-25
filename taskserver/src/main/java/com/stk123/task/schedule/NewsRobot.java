package com.stk123.task.schedule;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.model.bo.*;
import com.stk123.service.DictService;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import com.stk123.common.util.*;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.Span;

import com.stk123.model.bo.cust.StkFnDataCust;
import com.stk123.model.Index;
import com.stk123.model.Industry;
import com.stk123.model.News;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.CommonConstant;
import org.springframework.stereotype.Component;

@Component
@CommonsLog
public class NewsRobot {
	
	private static Date InitialDate = null;
	static{
		try {
			InitialDate = ServiceUtils.addDay(new Date(), -7);
			//InitialDate = StkUtils.sf_ymd2.parse("20110101");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
        NewsRobot newsRobot = new NewsRobot();
		newsRobot.run();
	}

    public void run() {
        try{
            runCN(InitialDate);
        }catch(Exception e){
            StringWriter aWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(aWriter));
            EmailUtils.send("NewsRobot CN Error", aWriter.getBuffer().toString());
            log.error("NewsRobot CN Error", e);
        }

        try{
            runHK(InitialDate);
        }catch(Exception e){
            StringWriter aWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(aWriter));
            EmailUtils.send("NewsRobot HK Error", aWriter.getBuffer().toString());
            log.error("NewsRobot HK Error", e);
        }
    }
	
	public static void runHK(Date date) throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_hk order by code", Stk.class);
			//List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_hk where code='00853' order by code", Stk.class);
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				List<Map> news = parseNewsFromSinaHK(index, date);
				insert(conn, index, news);
			}
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void runCN(Date date) throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			//List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn where code='000009' order by code", Stk.class);
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				List<Map> news = parseNewsFromSina(index, date);
				insert(conn, index, news);
			}
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void insert(Connection conn, Index index, List<Map> news) throws Exception {
        List<StkDictionary> types = DictService.getDictionary(2000);
		Collections.reverse(news);
		for(Map map : news){
			String title = (String)map.get("title");
			
			for(StkDictionary type : types){
				boolean match = false;
				String[] patterns = type.getParam().split(CommonConstant.MARK_SEMICOLON);
				for(String pattern : patterns){
					if(ServiceUtils.getMatchString(title, pattern) != null){
						if(type.getParam2() != null && ServiceUtils.getMatchString(title, type.getParam2().split(CommonConstant.MARK_SEMICOLON)) != null){
							continue;
						}
						match = true;
						break;
					}
				}
				if(match){
					List params = new ArrayList();
					params.add(index.getCode());
					params.add(type.getType());
					params.add(new Timestamp(ServiceUtils.addDay(((Date)map.get("date")),-5).getTime()));
					params.add(new Timestamp(((Date)map.get("date")).getTime()));
					List<StkNews> infos = JdbcUtils.list(conn, "select * from stk_news where code=? and type=? and info_create_time between ? and ?", params, StkNews.class);
					if(infos.size() == 0){
						params.clear();
						params.add(index.getCode());
						params.add(type.getType());
						params.add((String)map.get("title"));
						params.add((String)map.get("url"));
						params.add((String)map.get("target"));
						params.add(new Timestamp(((Date)map.get("date")).getTime()));
						JdbcUtils.insert(conn, "insert into stk_news(id,code,type,insert_time,info,title,url_source,url_target,info_create_time) values (s_news_id.nextval,?,?,sysdate,null,?,?,?,?)", params);
						
						//对合同、订单处理
						if(type.getType() == 200){
							boolean flag = checkContactSumGreaterThanMainIncome(conn, index, title, (Date)map.get("date"));
							Industry ind = index.getIndustryDefault();
							if(ind != null){
								if(ind.getType().getName().contains("房地产")){
									flag = false;
								}
							}
							if(flag){
								params.clear();
								params.add(index.getCode());
								params.add(News.TYPE_1);
								double percent = ServiceUtils.numberFormat(index.changePercent * 100, 2);
								params.add("["+type.getText()+"] 半年来总额是主营收入(TTM)的 "+ percent +"%");
								JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,info) values (s_import_info_id.nextval,?,?,sysdate,?)", params);
								
								if(percent >= 300){
									//EmailUtils.send("【订单总额】半年来订单总额是主营收入3倍以上: "+ index.getName() + " - "+ percent +"%","经典案例：东方园林(SZ:002310)<br><br>"+ StkUtils.wrapCodeAndNameAsHtml(index)+ " - "+ percent +"%");
								}
							}
						}
						//对定增、非公处理
						if(type.getType() == 150){
							boolean flag = checkContactGreaterThanTotalMarketValue(index, title, 0.1);
							if(flag){
								params.clear();
								params.add(index.getCode());
								params.add(News.TYPE_4);
								params.add("["+type.getText()+"] 金额是总市值["+ServiceUtils.number2String(index.getTotalMarketValue(),2)+"亿]的 "+ServiceUtils.numberFormat(index.changePercent * 100, 2)+"%. - " + title);
								JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,info) values (s_import_info_id.nextval,?,?,sysdate,?)", params);
							}
						}
						
						//非公开发行、员工持股,股权激励 监控
						if(type.getType() == 120 || type.getType() == 150 || type.getType() == 130){
							//NoticeRobot.updateNotice(conn, index);
						}
						
					}
					break;
				}
			}
		}
	}
	
	public static boolean checkContactGreaterThanTotalMarketValue(Index index, String title, double percent) throws Exception{
		String amount = ServiceUtils.getMatchString(title, ServiceUtils.PATTERN_1);
		if(amount != null){
			double total = 0;
			if(amount.contains("万")){
				total += Double.parseDouble(StringUtils.replace(amount, "万", "")) / 10000;
			}
			if(amount.contains("亿")){
				total += Double.parseDouble(StringUtils.replace(amount, "亿", ""));
			}
			double mv = index.getTotalMarketValue();
			if(mv > 0 && total >= mv * percent){
				index.changePercent = total/mv;
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkContactSumGreaterThanMainIncome(Connection conn, Index index, String title, Date newsCreateDate) throws Exception{
		String amount = ServiceUtils.getMatchString(title, ServiceUtils.PATTERN_1);
		if(amount != null){
			List params = new ArrayList();
			params.add(index.getCode());
			params.add(new Timestamp(ServiceUtils.addDay(newsCreateDate,-180).getTime()));
			params.add(new Timestamp(newsCreateDate.getTime()));
			List<StkNews> infos = JdbcUtils.list(conn, "select * from stk_news where code=? and type=200 and info_create_time between ? and ?", params, StkNews.class);
			double total = 0.0;
			for(StkNews info : infos){
				String t = info.getTitle();
				if(t.contains("同比") || t.contains("环比"))continue;
				amount = ServiceUtils.getMatchString(t, ServiceUtils.PATTERN_1);
				if(amount != null){
					if(amount.contains("万")){
						total += Double.parseDouble(StringUtils.replace(amount, "万", "")) / 10000;
					}
					if(amount.contains("亿")){
						total += Double.parseDouble(StringUtils.replace(amount, "亿", ""));
					}
				}
			}
			StkFnDataCust fnData = index.getFnDataLastestByType(index.FN_ZYSR);
			if(fnData != null){
				Double d = fnData.getFnDataByTTM();
				if(d != null){
					double zysr = d.doubleValue();
					if(zysr > 0 && total >= zysr * 0.4){
						index.changePercent = total/zysr;
						System.out.print("total="+total+",zysr="+zysr+",");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//https://vip.stock.finance.sina.com.cn/corp/view/vCB_AllNewsStock.php?symbol=sh600600&Page=1
	public static List<Map> parseNewsFromSina(Index index, Date dateBefore) throws Exception {
		String loc = index.getLocationAsString().toLowerCase();
		int pageId = 1;
		List<Map> news = new ArrayList<Map>();
		
		while(true){
			//https://vip.stock.finance.sina.com.cn/corp/view/vCB_AllNewsStock.php?symbol=sh600600&Page=1
			String url = "https://vip.stock.finance.sina.com.cn/corp/view/vCB_AllNewsStock.php?symbol="+loc+index.getCode()+"&Page="+pageId;
			String page = HttpUtils.get(url,null,"GBK");
			Node node = HtmlUtils.getNodeByAttribute(page,"","class","datelist");
			boolean stop = false;
			
			if(node != null){
				List<Node> as = HtmlUtils.getNodeListByTagName(node, "a");
				for(Node a : as){
					String href = HtmlUtils.getAttribute(a, "href");
					String title = a.toPlainTextString();
					Node dateNode = a.getPreviousSibling();
					String date = ServiceUtils.getMatchString(dateNode.toPlainTextString(), ServiceUtils.PATTERN_YYYY_MM_DD);
					//System.out.println(href+","+title+","+date);
					
					Date d = ServiceUtils.sf_ymd.parse(date);
					if(d.before(dateBefore)){
						stop = true;
						break;
					}

					if(index.getName() == null){
						continue;
					}
					
					if(!title.contains(index.getName()) && !title.contains(StringUtils.replace(index.getName(), " ", ""))
							&& !title.contains(StringUtils.replace(index.getName(), " ", "").replace("ST", ""))
							&& !title.contains(StringUtils.replace(index.getName(), " ", "").replace("*ST", ""))
							){
						continue;
					}
					
					Map map = new HashMap();
					map.put("date", d);
					map.put("title", title);
					map.put("target", href);
					map.put("url", url);
					news.add(map);
				}
			}else{
				break;
			}
			if(stop)break;
			pageId ++;
		}
		return news;
	}
	
	//http://stock.finance.sina.com.cn/hkstock/go.php/CompanyNews/page/1/code/01610/.phtml
	public static List<Map> parseNewsFromSinaHK(Index index, Date dateBefore) throws Exception {
		String loc = index.getLocationAsString().toLowerCase();
		int pageId = 1;
		List<Map> news = new ArrayList<Map>();
		
		while(true){
			String url = "http://stock.finance.sina.com.cn/hkstock/go.php/CompanyNews/page/"+pageId+"/code/"+index.getCode()+"/.phtml";
			String page = HttpUtils.get(url,null,"GBK");
			Node node = HtmlUtils.getNodeByAttribute(page,"","id","js_ggzx");
			boolean stop = false;
			
			if(node != null){
				List<Node> lis = HtmlUtils.getNodeListByTagName(node, "li");
				for(Node li : lis){
					Node a = HtmlUtils.getNodeByTagName(li, "a");
					if(a == null)continue;
					String href = HtmlUtils.getAttribute(a, "href");
					String title = a.toPlainTextString();
					Node dateNode = HtmlUtils.getNodeByTagName(li, "span");
					String date = ServiceUtils.getMatchString(dateNode.toPlainTextString(), ServiceUtils.PATTERN_YYYY_MM_DD);
					//System.out.println(href+","+title+","+date);
					
					Date d = ServiceUtils.sf_ymd.parse(date);
					if(d.before(dateBefore)){
						stop = true;
						break;
					}
					
					if(!title.contains(index.getName()) && !title.contains(StringUtils.replace(index.getName(), " ", "")) 
							&& !title.contains(StringUtils.replace(index.getName(), " ", "").replace("ST", ""))
							&& !title.contains(StringUtils.replace(index.getName(), " ", "").replace("*ST", ""))
							){
						continue;
					}
					
					Map map = new HashMap();
					map.put("date", d);
					map.put("title", title);
					map.put("target", href);
					map.put("url", url);
					news.add(map);
				}
			}else{
				break;
			}
			if(stop)break;
			pageId ++;
		}
		return news;
	}
	
	
	public static List<Map> parseNews(Index index,Date DateBefore) throws Exception {
		String loc = index.getLocationAsString();
		int pid = 1;
		List<Map> news = new ArrayList<Map>();
		
		while(true){
			//wind公司新闻
			String url = "http://www.windin.com/Tools/NewsDetail.aspx?windcode="+index.getCode()+"."+loc+"&start=&end=&pid="+pid+"&ajax=";
			String page = HttpUtils.get(url,null,"GBK");
			Node node = HtmlUtils.getNodeByAttribute(HtmlUtils.unescape(page),"","id","lblData");
			boolean stop = false;
			
			if(node != null){
				Span span = (Span)node;
				String data = StringUtils.substringBetween(span.getStringText(), ":[", "],");
				if(data != null){
					if(data.length() == 0){
						stop = true;
						break;
					}
					String[] infos = data.split(",\\u007B");
					for(String info:infos){
						if(info == null || info.length() == 0){
							continue;
						}
						
						String date = StringUtils.substringBetween(info, "\"newsdate\":\"", "\",\"caption");
						Date d = ServiceUtils.sf_ymd.parse(date);
						if(d.before(DateBefore)){
							stop = true;
							break;
						}
						
						String title = StringUtils.substringBetween(info, "\"caption\":\"", "\",\"source\"");
						if(!title.contains(index.getName()) && !title.contains(StringUtils.replace(index.getName(), " ", "")) 
								&& !title.contains(StringUtils.replace(index.getName(), " ", "").replace("ST", ""))
								&& !title.contains(StringUtils.replace(index.getName(), " ", "").replace("*ST", ""))
								){
							continue;
						}
						
						String target = StringUtils.substringBetween(info, "\"target\":\"", "\"");
						
						//System.out.println("date="+date+",title="+title);
						Map map = new HashMap();
						map.put("date", d);
						map.put("title", title);
						map.put("target", target);
						map.put("url", url);
						news.add(map);
					}
				}
			}
			if(stop)break;
			pid ++;
		}
		return news;
	}
	
	public static void remove() throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				//Index index =  new Index(conn,stk.getCode(),stk.getName());
				
				List params = new ArrayList();
				params.add(stk.getCode());
				List<StkImportInfo> infos = JdbcUtils.list(conn, "select * from stk_import_info where type>100 and code=?", params, StkImportInfo.class);
				for(StkImportInfo info : infos){
					StkImportInfoType type = News.getType(info.getType());
					if(type.getNotMatchPattern() != null && ServiceUtils.getMatchString(info.getTitle(), type.getNotMatchPattern().split(",")) != null){
						System.out.println(info.getTitle());
						params.clear();
						params.add(info.getId());
						JdbcUtils.delete(conn, "delete from stk_import_info where id=?", params);
					}
				}
				//break;
			}
		} finally {
			if (conn != null) conn.close();
		}
			
	}

}
