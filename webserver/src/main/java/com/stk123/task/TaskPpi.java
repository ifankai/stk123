package com.stk123.task;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.tags.TableTag;

import com.stk123.model.bo.StkDataPpiType;
import com.stk123.service.ServiceUtils;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;


/**
 * 生意社 http://www.100ppi.com/
 */
public class TaskPpi {

	public static void main(String[] args) throws Exception {
		System.out.println(TaskPpi.class.getName());
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		HttpUtils.NO_OF_RETRY = 5;
		Connection conn = null;
		List params = new ArrayList();
		try{
			conn = DBUtil.getConnection();
			String page = HttpUtils.get("http://www.100ppi.com/cindex/", "utf-8");
			//System.out.println(page);
			Node node = HtmlUtils.getNodeByAttribute(page, null, "class", "block clearfix ovh");
			//System.out.println(node.toHtml());
			List<Node> list = HtmlUtils.getNodeListByTagNameAndAttribute(node, "dl", "id", "m-list1");
			for(Node n : list){
				//System.out.println(n.toHtml());
				Node parent = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "rel", "1");
				if(parent == null){
					parent = HtmlUtils.getNodeByAttribute(n.toHtml(), null, "rel", "0");
				}
				String name = StringUtils.trim(parent.toPlainTextString());
				params.clear();
				params.add(name);
				StkDataPpiType type = JdbcUtils.load(conn, "select * from stk_data_ppi_type where name=?", params, StkDataPpiType.class);
				if(type == null){
					long seq = JdbcUtils.getSequence(conn, "s_data_ppi_type_id");
					params.clear();
					params.add(seq);
					params.add(name);
					JdbcUtils.insert(conn, "insert into stk_data_ppi_type (id,name,parent_id,url) select ?,?,null,null from dual", params);
					params.clear();
					params.add(seq);
					type = JdbcUtils.load(conn, "select * from stk_data_ppi_type where id=?", params, StkDataPpiType.class);
				}
				List<Node> children = HtmlUtils.getNodeListByTagNameAndAttribute(n, "dd", "class", "d-list");
				for(Node child : children){
					System.out.println(child.toPlainTextString()+","+((Tag)child).getAttribute("id"));
					name = StringUtils.trim(child.toPlainTextString());
					String url = ((Tag)HtmlUtils.getNodeListByTagName(child, "a").get(0)).getAttribute("href");
					//System.out.println("url=="+url);
					params.clear();
					params.add(name);
					StkDataPpiType childType = JdbcUtils.load(conn, "select * from stk_data_ppi_type where name=?", params, StkDataPpiType.class);
					if(childType == null){
						long seq = JdbcUtils.getSequence(conn, "s_data_ppi_type_id");
						params.clear();
						params.add(seq);
						params.add(name);
						params.add(type.getId());
						params.add(url);
						JdbcUtils.insert(conn, "insert into stk_data_ppi_type (id,name,parent_id,url) select ?,?,?,? from dual", params);
						params.clear();
						params.add(seq);
						childType = JdbcUtils.load(conn, "select * from stk_data_ppi_type where id=?", params, StkDataPpiType.class);
					}
					
					page = HttpUtils.get("http://www.100ppi.com"+childType.getUrl(), "utf-8");
					//System.out.println(page);
					List<Node> tabs = HtmlUtils.getNodeListByTagName(page, null, "table");
					if(tabs != null && tabs.size() > 0){
						TableTag table = (TableTag)tabs.get(0);
						//System.out.println(table.toHtml());
						Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable(table, 0, 0);
						System.out.println(datas);
						for(Map.Entry<String, Map<String, String>> data : datas.entrySet()){
							String mmdd = StringUtils.replace(data.getKey(), "-", "");
							String date = mmdd;
							if(mmdd.length() == 4){
								int year = ServiceUtils.YEAR;
								date = year + mmdd;
								if(ServiceUtils.getToday().compareTo(date) < 0){
									date = (--year) + mmdd;
								}
							}
							params.clear();
							params.add(childType.getId());
							params.add(date);
							String key = data.getValue().keySet().iterator().next();
							String value = data.getValue().get(key);
							if("-".equals(value))continue;
							params.add(value);
							params.add(childType.getId());
							params.add(date);
							//JdbcUtils.insert(conn, "insert into stk_data_ppi (type_id,ppi_date,value,insert_time) select ?,?,?,sysdate() from dual where not exists (select 1 from stk_data_ppi where type_id=? and ppi_date=?)", params);
							JdbcUtils.insert(conn, "insert into stk_data_ppi (type_id,ppi_date,value,insert_time) select ?,?,?,sysdate from dual where not exists (select 1 from stk_data_ppi where type_id=? and ppi_date=?)", params);
						}
					}
					
					//初始化用，一次性的
					if(false){
						Node kNode = HtmlUtils.getNodeByText(page, null, "查看周K线");
						//System.out.println(kNode.toHtml());
						if(kNode != null){
							page = HttpUtils.get("http://www.100ppi.com/cindex/?welcome=no&f=graph&func="+((Tag)child).getAttribute("id"), "utf-8");
							String json = StringUtils.substringBetween(page, "eval(", ");");
							//System.out.println(json);
							List<Map> ks = JsonUtils.getList4Json(json, Map.class);
							for(Map k : ks){
								//System.out.println(k);
								params.clear();
								params.add(childType.getId());
								String pdate = ServiceUtils.formatDate(StringUtils.replace(String.valueOf(k.get("pdate")), "Mon ", ""), ServiceUtils.sf_ymd7, ServiceUtils.sf_ymd2);
								params.add(pdate);
								params.add(k.get("close"));
								params.add(childType.getId());
								params.add(pdate);
								JdbcUtils.insert(conn, "insert into stk_data_ppi (type_id,ppi_date,value,insert_time) select ?,?,?,sysdate from dual where not exists (select 1 from stk_data_ppi where type_id=? and ppi_date=?)", params);
							}
						}
					}
					
				}
				//break;
			}
		}catch(Exception e){
			EmailUtils.send("生意社出错", e);
		} finally {
			if (conn != null) conn.close();
		}
	}

}
