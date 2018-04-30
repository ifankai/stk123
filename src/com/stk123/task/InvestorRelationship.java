package com.stk123.task;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.tags.TableTag;

import com.stk123.bo.Stk;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.web.StkConstant;

public class InvestorRelationship {
	
	public static final List<String> KEYWORDS = new ArrayList<String>();
	static{
		KEYWORDS.add("重组");
		KEYWORDS.add("并购");
		KEYWORDS.add("收购");
	}

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		String today = StkUtils.getToday();
		//today = "20150529";
		List<Index> results = new ArrayList<Index>();
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			for(Stk stk : stks){
				try{
					Index index =  new Index(conn,stk.getCode(),stk.getName());
					K todayK = index.getK(today);
					if(index.isStop(today) || todayK.isUpLimit()){
						continue;
					}
					String url =  "http://ircs.p5w.net/ircs/interaction/bbs.do?stockcode="+stk.getCode()+"&stocktype=S";
					String page = HttpUtils.get(url, "utf-8");
					TableTag tab = (TableTag)HtmlUtils.getNodeByAttribute(page, null, "style", "background:#d7d7d7;width:100%;table-layout:fixed;");
					if(tab != null){
						List<List<String>> datas = HtmlUtils.getListFromTable(tab,0);
						//System.out.println(datas);
						boolean contain = false;
						double cnt = 0;
						for(List<String> data : datas){
							String content = data.get(3);
							if(content != null){
								String date = StringUtils.substringBetween(content, "(", ")");
								if(date != null && date.replaceAll("-", "").contains(today)){
									for(String kw : KEYWORDS){
										if(StringUtils.contains(data.toString(), kw)){
											contain = true;
											break;
										}
									}
									if(contain)break;
								}
							}
						}
						
						if(contain){
							for(List<String> data : datas){
								String content = data.get(3);
								if(content != null){
									for(String kw : KEYWORDS){
										if(StringUtils.contains(data.toString(), kw)){
											cnt++;
										}
									}
								}
							}
							
							index.changePercent = cnt;
							System.out.println(stk.getName()+","+stk.getCode()+","+url);
							results.add(index);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					//throw e;
				}
			}
			
			if(results.size() > 0){
				Collections.sort(results, new Comparator<Index>(){
					public int compare(Index o1, Index o2) {
						int i = (int)((o2.changePercent - o1.changePercent)*10000);
						return i;
					}});
				StringBuffer sb = new StringBuffer();
				for(Index index : results){
					sb.append(index.getName()+"[<a target='_blank' href='http://"+StkConstant.HOST_PORT+"/stk.do?s="+index.getCode()+"'>"+index.getCode()+"</a>]"+"["+StkUtils.number2String(index.getTotalMarketValue(),2)+"亿]"+"<a target='_blank' href='http://ircs.p5w.net/ircs/interaction/bbs.do?stockcode="+index.getCode()+"&stocktype=S'>来源</a>,"+index.changePercent);
					sb.append("<br>");
				}
				EmailUtils.send("互动平台-并购重组，个数："+results.size()+"，日期："+today, sb.toString());
			}
		} finally {
			if (conn != null) conn.close();
		}
		
		
	}

}

