package com.stk123.task.sub;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;

import com.stk123.model.bo.Stk;
import com.stk123.service.ServiceUtils;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.service.ExceptionUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;


public class Guba {

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			for(Stk stk : stks){
				try{
					System.out.println(stk.getCode());
					task(conn, stk.getCode(), ServiceUtils.getToday());
					//break;
				}catch(Exception e){
					e.printStackTrace();
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
				}
			}
			
		} finally {
			if (conn != null) conn.close();
		}
		
	}
	
	public static void task(Connection conn, String code, String date) throws Exception {
		int numTotal = 0;
		int numClick = 0;
		int numReply = 0;
		int i = 0;
		int pageNum = 1;
		while(true){
			String page = HttpUtils.get("http://guba.eastmoney.com/list,"+code+"_"+pageNum+".html", "utf-8");
			//System.out.println(page);
			if(pageNum == 1){
				Node total = HtmlUtils.getNodeByText(page, null, "共有帖子数");
				//System.out.println("total==="+total.toPlainTextString());
				numTotal = Integer.parseInt(StringUtils.trim(StringUtils.substringBetween(total.toPlainTextString(), "共有帖子数", "篇")));
			}
			Node node = HtmlUtils.getNodeByAttribute(page, null, "id", "articlelistnew");
			//System.out.println(node.toHtml());
			
			List<Node> list = HtmlUtils.getNodeListByTagNameAndAttribute(node, "div", "class", "articleh");
			for(Node n : list){
				if(n.toHtml().contains("em"))continue;
				//System.out.println(n.toHtml());
				Node click = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "l1");
				Node reply = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "class", "l2");
				//System.out.println(click.toPlainTextString()+","+reply.toPlainTextString());
				numClick += Integer.parseInt(click.toPlainTextString());
				numReply += Integer.parseInt(reply.toPlainTextString());
				i ++;
				if(i >= 100)break;
			}
			pageNum ++;
			if(i >= 100)break;
			if(pageNum >= 3)break;
		}
		List params = new ArrayList();
		params.add(code);
		params.add(date);
		params.add(numClick);
		params.add(numReply);
		params.add(numTotal);
		params.add(code);
		params.add(date);
		//JdbcUtils.insert(conn, "insert into stk_data_eastmoney_guba select ?,?,?,?,?,sysdate() from dual where not exists (select 1 from stk_data_eastmoney_guba where code=? and insert_date=?)", params);
		JdbcUtils.insert(conn, "insert into stk_data_eastmoney_guba select ?,?,?,?,?,sysdate from dual where not exists (select 1 from stk_data_eastmoney_guba where code=? and insert_date=?)", params);
		//System.out.println(numTotal+","+numClick+","+numReply);
	}

}
