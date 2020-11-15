package com.stk123.task.quartz.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.model.app.XqPost;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.httpclient.Header;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.bo.Stk;
import com.stk123.model.Index;
import com.stk123.service.ServiceUtils;
import com.stk123.service.XueqiuService;
import com.stk123.common.db.util.CloseUtil;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.db.util.sequence.SequenceUtils;
import com.stk123.common.util.ChineseUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
/**
 * 雪球个股页面长文
 */
@CommonsLog
public class XueqiuStockArticleJob implements Job {

	private static int codeIndex = 0;
	private static List<Stk> stocks = null;
	public static List<XueqiuArticle> results = new ArrayList();
	
	private static Set<String> ids = new HashSet<String>();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = stocks;
			if(stks == null){
				String sql = "select * from ( "
						+ "select code,name from stk_cn a "
						+ "where hot>3000 and not exists (select 1 from stk_cn b where a.code=b.code and b.name like '%ST%') "
						+ "union all "
						+ "select code,name from stk_hk where hot>1000 ) order by reverse(code)";
				stks = stocks = JdbcUtils.list(conn, sql, Stk.class);
				if(new Date().getDate() % 2 == 0){
					Collections.reverse(stks);
				}
			}
			
			if(codeIndex >= stks.size()){
				return;
			}
			for(int i=0; i<4 && codeIndex <stks.size(); i++){
				Stk stk = stks.get(codeIndex++);
				log.info("XueqiuStockArticleJob="+stk.getCode()+"["+results.size()+"]");
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
					results.clear();
				}
				return;
			}
		}catch(Exception e){
			log.error("XueqiuStockArticleJob", e);
		}finally{
			CloseUtil.close(conn);
    			if(codeIndex >= stocks.size()){
				if(results.size() > 0){
					EmailUtils.send("雪球个股长文end", StringUtils.join(results, "<br><br>"));
				}
			}
		}

	}
	
	//https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol=SH603611&hl=0&source=all&sort=alpha&page=1&_=1507209904103
	public static List<XueqiuArticle> getArticles(Connection conn, String code) throws Exception {
		String scode = ServiceUtils.getStkLocation(code)+code;
		if(StringUtils.length(code) == 5){
			scode = code;
		}
		Map<String, String> requestHeaders = XueqiuService.getCookies();
		//String page = HttpUtils.get("https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol="+scode+"&hl=0&source=all&sort=alpha&page=1&_="+new Date().getTime(),null, requestHeaders, "gb2312");

        String url = "https://xueqiu.com/query/v1/symbol/search/status?count=100&comment=0&symbol="+scode+"&hl=0&source=all&sort=&page=1&q=";
		String page = HttpUtils.get(url,null, requestHeaders, "gb2312");
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
			if(ServiceUtils.addDay(new Date(), -2).getTime() <= createAt && ServiceUtils.addDay(new Date(), 0).getTime() >= createAt){
				boolean flag = false;
				String title = (String)art.get("title");
				String text = (String)art.get("text");
				//System.out.println("title:"+title);
				//Map retweeted_status = (Map)art.get("retweeted_status");
				if(StringUtils.isEmpty(title)){
					title = (String)art.get("text");
					if(ChineseUtils.length(title) < 100){//内容长度控制，太短的排除掉，100=50个中文
						continue;
					}
				}
				String name = index.getName();
				if(StringUtils.length(text) > 300 && (text.contains(name) || text.contains(StringUtils.replace(name, " ", "")))){
					flag = true;
				}else if(!StringUtils.isEmpty(title) && (title.contains(name) || title.contains(StringUtils.replace(name, " ", "")))){
					flag = true;
				}
				
				if(flag){
					if(Integer.parseInt(String.valueOf(art.get("reply_count"))) >= 10){
						XueqiuUser u = new XueqiuUser();
						u.id = String.valueOf(art.get("user_id"));
						u.name = String.valueOf(((Map)art.get("user")).get("screen_name"));
						
						XueqiuArticle a = new XueqiuArticle();
						a.code = code;
						a.user = u;
						a.createAt = createAt;
						a.id = String.valueOf(art.get("id"));
						a.title = String.valueOf(art.get("title"));
						a.description = String.valueOf(art.get("description"));
						a.replyCount = Integer.parseInt(String.valueOf(art.get("reply_count")));
						if(a.title == null || a.title.length() == 0){
							a.title = a.description;
						}
						if(!ids.contains(a.id)){
							results.add(a);
							ids.add(a.id);
							insertText(conn, code, a);

                            XqPost xqPost = new XqPost();
                            xqPost.setId(Long.valueOf((String)art.get("id")));
                            xqPost.setTitle((String) art.get("title"));
                            xqPost.setText((String) art.get("description"));
                            xqPost.setCreatedAt(new Date((Long)art.get("created_at")));
                            xqPost.setReplyCount(Integer.valueOf((String)art.get("reply_count")));
							xqPost.setFollowersCount(Integer.valueOf((String)art.get("followers_count")));

                            xqPost.setUserId(Long.valueOf((String)art.get("user_id")));
                            xqPost.setUserName((String) ((Map)art.get("user")).get("screen_name"));
                            xqPost.setUserAvatar(StringUtils.split((String) ((Map)art.get("user")).get("profile_image_url"), ",")[1]);

                            ObjectMapper objectMapper = new ObjectMapper();
                            String json = objectMapper.writeValueAsString(xqPost);

                            requestHeaders.put("Content-Type", "application/json;charset=UTF-8");
                            List<Header> respHeaders = new ArrayList<Header>();
							HttpUtils.post("http://81.68.255.181:8080/api/xq/post", null, json,requestHeaders,"UTF-8", respHeaders);

						}
					}
				}
			}
		}
		return results;
	}
	
	private static void insertText(Connection conn, String code, XueqiuArticle xa) throws Exception {
		List params = new ArrayList();
		long id = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID);
		params.add(id);
		params.add(code);
		params.add(xa.id);
		params.add(JdbcUtils.createClob(conn, xa.toString()));
		params.add(new Date(xa.createAt));

		params.add(code);
		params.add(xa.id);
		int ret = JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) select ?,3,?,1,?,?,?,null,0 from dual where not exists (select 1 from stk_text where code=? and title=?)", params);
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
			List<XueqiuArticle> results = getArticles(conn,"600600");
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
		String scode = ServiceUtils.getStkLocation(code)+code;
		int cnt400 = 0;
		do{
			//System.out.println(p);
			String page = HttpUtils.get("http://xueqiu.com/statuses/search.json?count=20&comment=0&symbol="+scode+"&hl=0&source=user&sort=time&page="+p+"&_="+new Date().getTime(),null, XueqiuService.getCookies(), "gb2312");
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
				if(ServiceUtils.addDay(new Date(), -days).getTime() >= createAt){
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
					if(!ids.contains(a.id)){
						results.add(a);
						ids.add(a.id);
					}
					//System.out.println(new java.util.Date(Long.valueOf(String.valueOf(art.get("created_at"))))+":["+art.get("title")+"]"+art.get("description"));
				}
			}
			if(bflag) break;
			
		}while(true);
		return results;
	}

}
