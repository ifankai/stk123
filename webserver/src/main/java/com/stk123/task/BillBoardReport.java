package com.stk123.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.stk123.common.util.HtmlUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.service.ServiceUtils;
import com.stk123.common.util.*;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.TableTag;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkDeptType;
import com.stk123.model.bo.StkText;
import com.stk123.model.Index;
import com.stk123.model.Text;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class BillBoardReport {

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			//init(conn);
			run(conn);
		}catch(Exception e){
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			//EmailUtils.send("BillBoard Data Error", aWriter.getBuffer().toString());
			e.printStackTrace();
		}finally{
			if(conn != null)conn.close();
		}

	}
	
	public static void init(Connection conn) throws Exception {
		List params = new ArrayList();
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn where code>='600011' order by code", Stk.class);
		for(Stk stk : stks){
			System.out.println(stk.getCode());
			String page = HttpUtils.get("http://data.eastmoney.com/soft/stock/StockDetail.aspx?code="+stk.getCode(), "utf8");
			Node dnode = HtmlUtils.getNodeByAttribute(page, null, "class", "tipsdate");
			if(dnode == null)continue;
			String transDate = dnode.toPlainTextString();
			transDate = ServiceUtils.getMatchString(transDate, "\\d{4}-\\d{2}-\\d{2}");
			Node ul = HtmlUtils.getNodeByAttribute(page, null, "id", "datelist");
			List<String> dates = new ArrayList<String>();
			List<Node> as = HtmlUtils.getNodeListByTagName(ul, "a");
			for(Node a : as){
				LinkTag link = (LinkTag)a;
				String href = link.getAttribute("href");
				//System.out.println(href);
				String date = HttpUtils.getParameter(href, null, "date");
				dates.add(date);
			}
			ul = HtmlUtils.getNodeByAttribute(page, null, "id", "datesele");
			if(ul != null){
				List<Node> options = HtmlUtils.getNodeListByTagName(ul, "option");
				for(Node option : options){
					OptionTag o = (OptionTag)option;
					String date = o.getAttribute("value");
					if(!"-1".equals(date)){
						dates.add(date);
					}
				}
			}
			//System.out.println(dates);
			int i=0;
			do{
				if(i == dates.size()){
					break;
				}
				//System.out.println(transDate);
				List<Node> tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "tab2");
				if(tables.size() >= 2)
				for(int j=0;j<2;j++){
					Node table = tables.get(j);
					List<List<String>> tableList = HtmlUtils.getListFromTable((TableTag)table, 1);
					for(List<String> row : tableList){
						String trs = row.toString();
						if(trs.contains("总合计:")){
							break;
						}
						//System.out.println(trs);
						String dept = row.get(1);
						if(dept == null || dept.length() == 0)continue;
						int deptId = getDeptId(conn,dept);
						String date = StringUtils.replace(transDate, "-", "");
						params.clear();
						params.add(stk.getCode());
						params.add(date);
						params.add(deptId);
						params.add(row.get(2));
						params.add(StringUtils.replace(row.get(3), "%", ""));
						params.add(row.get(4));
						params.add(StringUtils.replace(row.get(5), "%", ""));
						params.add(row.get(6));
						params.add(j);
						params.add(row.get(0));
						
						params.add(stk.getCode());
						params.add(date);
						params.add(deptId);
						params.add(j);
						params.add(row.get(0));
						JdbcUtils.update(conn, "insert into stk_billboard select ?,?,?,?,?,?,?,?,?,? from dual where not exists (select 1 from stk_billboard where code=? and trans_date=? and dept_id=? and buy_sell=? and seq=?)", params);
					}
				}
				
				transDate = dates.get(i++);
				page = HttpUtils.get("http://data.eastmoney.com/soft/stock/StockDetail.aspx?code="+stk.getCode()+"&date="+transDate, "utf8");
			}while(true);
			Thread.currentThread().sleep(2000);
		}
	}
		
	public static int getDeptId(Connection conn,String deptName){
		List params = new ArrayList();
		params.add(deptName);
		StkDeptType dept = JdbcUtils.load(conn, "select * from stk_dept_type where dept_name=?", params,StkDeptType.class);
		if(dept != null){
			return dept.getDeptId().intValue();
		}else{
			params.clear();
			params.add(deptName);
			JdbcUtils.update(conn, "insert into stk_dept_type values ((select nvl(max(dept_id)+1,101) from stk_dept_type),?)", params);
			dept = JdbcUtils.load(conn, "select * from stk_dept_type where dept_name=?", params,StkDeptType.class);
			return dept.getDeptId().intValue();
		}
	}
	
	public static void insert(Connection conn, String code, String transDate, List<Node> tables){
		if(tables.size() >= 2){
			for(int j=0;j<2;j++){
				Node table = tables.get(j);
				List<List<String>> tableList = HtmlUtils.getListFromTable((TableTag)table, 1);
				for(List<String> row : tableList){
					String trs = row.toString();
					if(trs.contains("总合计:")){
						break;
					}
					//System.out.println(trs);
					String dept = row.get(1);
					if(dept == null || dept.length() == 0)continue;
					int deptId = getDeptId(conn,dept);
					String date = StringUtils.replace(transDate, "-", "");
					List params = new ArrayList();
					params.add(code);
					params.add(date);
					params.add(deptId);
					params.add(row.get(2));
					params.add(StringUtils.replace(row.get(3), "%", ""));
					params.add(row.get(4));
					params.add(StringUtils.replace(row.get(5), "%", ""));
					params.add(row.get(6));
					params.add(j);
					params.add(row.get(0));
					
					params.add(code);
					params.add(date);
					params.add(deptId);
					params.add(j);
					params.add(row.get(0));
					JdbcUtils.update(conn, "insert into stk_billboard select ?,?,?,?,?,?,?,?,?,? from dual where not exists (select 1 from stk_billboard where code=? and trans_date=? and dept_id=? and buy_sell=? and seq=?)", params);
				}
			}
		}
	}
	
	public static void run(Connection conn)throws Exception{
		String page = HttpUtils.get("http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=LHB&sty=JGXWMX&p=1&ps=50&rt=47199888", "utf8");
		//System.out.println(page);
		List<String> list = JsonUtils.testJsonArray(StringUtils.substringBetween(page, "(", ")"));
		int i = 0;
		String theDay = null;
		for(String row : list){
			String[] s = row.split(",");
			if(i++ == 0){
				theDay = s[5];
			}
			if(theDay.equals(s[5])){
				String code = s[2].substring(0, 6);
				System.out.println(code);
				if(StringUtils.startsWith(code, "900"))continue;
				page = HttpUtils.get("http://data.eastmoney.com/soft/stock/StockDetail.aspx?code="+code+"&date="+theDay, "utf8");
				List<Node> tabs = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "tab2");
				insert(conn, code, theDay, tabs);
			}
		}
	}
	
	public void jigou(Connection conn)throws Exception{
		Set<String> result = new HashSet<String>();
		String page = HttpUtils.get("http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=LHB&sty=JGXWMX&p=1&ps=50&rt=47199888", "utf8");
		//System.out.println(page);
		List<String> list = JsonUtils.testJsonArray(StringUtils.substringBetween(page, "(", ")"));
		int i = 0;
		String theDay = null;
		for(String row : list){
			String[] s = row.split(",");
			if(i++ == 0){
				theDay = s[5];
			}
			if(theDay.equals(s[5])){
				String code = s[2].substring(0, 6);
				//System.out.println(code);
				page = HttpUtils.get("http://data.eastmoney.com/soft/stock/StockDetail.aspx?code="+code+"&date="+theDay, "utf8");
				List<Node> tabs = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "tab2");
				//System.out.println(tabs.get(0).toHtml());
				List<List<String>> tabList = HtmlUtils.getListFromTable((TableTag)tabs.get(0), 1);
				boolean isbuy = false;
				for(List<String> tr : tabList){
					String trs = tr.toString();
					if(trs.contains("机构专用")){
						isbuy = true;
						break;
					}
				}
				if(isbuy){
					Node ul = HtmlUtils.getNodeByAttribute(page, null, "id", "datelist");
					List<Node> as = HtmlUtils.getNodeListByTagName(ul, "a");
					for(Node a : as){
						LinkTag link = (LinkTag)a;
						String href = link.getAttribute("href");
						//System.out.println(href);
						String date = HttpUtils.getParameter(href, null, "date");
						//System.out.println(date);
						Date d = ServiceUtils.sf_ymd.parse(date);
						if(d.after(ServiceUtils.addDay(new Date(), -60))){
							String subPage = HttpUtils.get("http://data.eastmoney.com/soft/stock/StockDetail.aspx?code="+code+"&date="+date, "utf8");
							List<Node> tables = HtmlUtils.getNodeListByTagNameAndAttribute(subPage, null, "table", "class", "tab2");
							List<List<String>> tableList = HtmlUtils.getListFromTable((TableTag)tables.get(0), 1);
							for(List<String> tr : tableList){
								String trs = tr.toString();
								if(trs.contains("机构专用")){//也就是说60天内不是首次买入了
									isbuy = false;
									break;
								}
							}
							if(!isbuy){
								break;
							}
						}
					}
					if(isbuy){
						System.out.println("首次买入:"+code);
						result.add("<a target='_blank' href='http://data.eastmoney.com/soft/stock/StockDetail.aspx?code="+code+"'>"+s[4]+"["+code+"]</a>");
						
						List params = new ArrayList();
						params.add(code);
						params.add(Text.SUB_TYPE_ORG_BUY_WITHIN_60);
						params.add(new Timestamp(ServiceUtils.addDay(new Date(),-7).getTime()));
						params.add(new Timestamp(ServiceUtils.addDay(new Date(),1).getTime()));
						List<StkText> infos = JdbcUtils.list(conn, "select * from stk_text where code=? and sub_type=? and insert_time between ? and ?", params, StkText.class);
						if(infos.size() == 0){
							Index in = new Index(conn, code);
							if(in.getStock() != null){
								Text.insert(conn, code, "[60日内机构首次买入]",Text.SUB_TYPE_ORG_BUY_WITHIN_60);
							}
						}
					}
				}
			}
			//System.out.println(row);
		}
		//EmailUtils.send("机构60日内首次买入", StringUtils.join(result, "<br>"));
	}

}
