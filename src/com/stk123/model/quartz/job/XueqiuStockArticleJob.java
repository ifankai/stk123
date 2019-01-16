package com.stk123.model.quartz.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.bo.Stk;
import com.stk123.model.Index;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.db.util.CloseUtil;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;

public class XueqiuStockArticleJob implements Job {
	
	private static int codeIndex = 0;
	private static List<Stk> stocks = null;
	public static List<XueqiuArticle> results = new ArrayList();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = stocks;
			if(stks == null){
				stks = stocks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			}
			if(codeIndex >= stks.size()){
				return;
			}
			for(int i=0; i<3 && codeIndex <stks.size(); i++){
				Stk stk = stks.get(codeIndex++);
				System.out.println("XueqiuStockArticleJob="+stk.getCode()+"["+results.size()+"]");
				List<XueqiuArticle> list = getArticles(conn, stk.getCode());
				if(list.size() > 0){
					results.addAll(list);
					if(results.size() >= 20){
						EmailUtils.send("雪球个股长文", StringUtils.join(results, "<br><br>"));
						results.clear();
					}
				}
				//System.out.println(results);
				Thread.sleep(1000*15);
			}
			if(codeIndex >= stks.size()){
				if(results.size() > 0){
					EmailUtils.send("雪球个股长文2", StringUtils.join(results, "<br><br>"));
				}
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			CloseUtil.close(conn);
		}

	}
	
	//https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol=SH603611&hl=0&source=all&sort=alpha&page=1&_=1507209904103
	public static List<XueqiuArticle> getArticles(Connection conn, String code) throws Exception {
		String scode = StkUtils.getStkLocation(code)+code;
		Map<String, String> requestHeaders = XueqiuUtils.getCookies();
		String page = HttpUtils.get("https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol="+scode+"&hl=0&source=all&sort=alpha&page=1&_="+new Date().getTime(),null, requestHeaders, "gb2312");
		//System.out.println(page);
		List<XueqiuArticle> results = new ArrayList<XueqiuArticle>();
		if("400".equals(page)){
			return results;
		}
		Index index = new Index(conn, code);
		
		Map map = JsonUtils.testJson(page);
		List<Map> arts = (List<Map>)map.get("list");
		for(Map art : arts){
			long createAt = Long.valueOf(String.valueOf(art.get("created_at")));
			if(StkUtils.addDay(new Date(), -2).getTime() <= createAt && StkUtils.addDay(new Date(), 0).getTime() >= createAt){
				boolean flag = false;
				String title = (String)art.get("title");
				String text = (String)art.get("text");
				//System.out.println("title:"+title);
				Map retweeted_status = (Map)art.get("retweeted_status");
				if(retweeted_status != null && StringUtils.isEmpty(title)){
					title = (String)retweeted_status.get("title");
				}
				String name = index.getName();
				if(StringUtils.length(text) > 300 && (text.contains(name) || text.contains(StringUtils.replace(name, " ", "")))){
					flag = true;
				}else
				if(!StringUtils.isEmpty(title) && (title.contains(name) || title.contains(StringUtils.replace(name, " ", "")))){
					flag = true;
				}
				
				if(flag){
					if(Integer.parseInt(String.valueOf(art.get("reply_count"))) >= 10 || (retweeted_status != null && Integer.parseInt(String.valueOf(retweeted_status.get("reply_count"))) >= 10)){
						XueqiuUser u = new XueqiuUser();
						u.id = String.valueOf(art.get("user_id"));
						u.name = String.valueOf(((Map)art.get("user")).get("screen_name"));
						
						XueqiuArticle a = new XueqiuArticle();
						a.user = u;
						a.createAt = createAt;
						a.id = String.valueOf(art.get("id"));
						a.title = String.valueOf(art.get("title"));
						a.description = String.valueOf(art.get("description"));
						a.replyCount = Integer.parseInt(String.valueOf(art.get("reply_count")));
						if(a.title == null || a.title.length() == 0){
							a.title = a.description;
						}
						results.add(a);
					}
				}
			}
		}
		return results;
	}

	public static void main(String[] args) throws Exception {
		/*String code = "300185";
		List<XueqiuArticle> results = getArticles(code, 120, 10);
		
		if(results.size() > 0){
			EmailUtils.sendAndReport(code+" - 雪球长文", StringUtils.join(results, "<br><br>"));
		}*/
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<XueqiuArticle> results = getArticles(conn,"000671");
			System.out.println(results);
		}finally{
			CloseUtil.close(conn);
		}
	}
	
	//雪球个股页面（按时间排序）
	public static List<XueqiuArticle> getArticles(String code, int days, int replyCnt) throws Exception {
		int p = 1;
		List<XueqiuArticle> results = new ArrayList<XueqiuArticle>();
		boolean bflag = false;
		String scode = StkUtils.getStkLocation(code)+code;
		int cnt400 = 0;
		do{
			//System.out.println(p);
			String page = HttpUtils.get("http://xueqiu.com/statuses/search.json?count=20&comment=0&symbol="+scode+"&hl=0&source=user&sort=time&page="+p+"&_="+new Date().getTime(),null, XueqiuUtils.getCookies(), "gb2312");
			//System.out.println(page);
			p++;
			if("400".equals(page)){
				if( ++cnt400 >= 3)break;
				continue;
			}
			Map map = JsonUtils.testJson(page);
			List<Map> arts = (List<Map>)map.get("list");
			for(Map art : arts){
				long createAt = Long.valueOf(String.valueOf(art.get("created_at")));
				if(StkUtils.addDay(new Date(), -days).getTime() >= createAt){
					bflag = true;
					break;
				}
				if(Integer.parseInt(String.valueOf(art.get("reply_count"))) >= replyCnt){
					XueqiuUser u = new XueqiuUser();
					u.id = String.valueOf(art.get("user_id"));
					u.name = String.valueOf(((Map)art.get("user")).get("screen_name"));
					
					XueqiuArticle a = new XueqiuArticle();
					a.user = u;
					a.createAt = createAt;
					a.id = String.valueOf(art.get("id"));
					a.title = String.valueOf(art.get("title"));
					a.description = String.valueOf(art.get("description"));
					a.replyCount = Integer.parseInt(String.valueOf(art.get("reply_count")));
					if(a.title == null || a.title.length() == 0){
						a.title = a.description;
					}
					results.add(a);
					//System.out.println(new java.util.Date(Long.valueOf(String.valueOf(art.get("created_at"))))+":["+art.get("title")+"]"+art.get("description"));
				}
			}
			if(bflag) break;
			
		}while(true);
		return results;
	}

}
