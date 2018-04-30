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

import com.stk123.bo.StkXueqiuUser;
import com.stk123.model.IndexUtils;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;

public class XueqiuLongArticleJob implements Job {
	
	private static List<XueqiuUser> MEMBERS = new ArrayList<XueqiuUser>();
	
	private static List<String> ID_EXCLUDED = new ArrayList<String>();
	static{
		ID_EXCLUDED.add("5124430882");//要闻直播
		ID_EXCLUDED.add("3966435964");//慧博资讯-迈博
		ID_EXCLUDED.add("2724224241");//私募排排网
		ID_EXCLUDED.add("2228854973");//云财经
		ID_EXCLUDED.add("6093797154");//华宝油气
		ID_EXCLUDED.add("9485866208");//蛋卷基金
				
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("XueqiuLongArticleJob executing...");
		List<XueqiuArticle> arts = new ArrayList<XueqiuArticle>();
		try {
			if(MEMBERS.size() == 0){
				try{
					initUsers();
				}catch(Exception e){
					EmailUtils.send("XueqiuLongArticleJob initUsers Error", ExceptionUtils.getException(e));
				}
			}
			int i = 0;
			for(XueqiuUser user : MEMBERS){
				//System.out.println("1 user name:"+user.name+",id:"+user.id);
				this.getLongArticle(user, arts);
				
				if(i++ % 100 == 0){
					Thread.sleep(1000*15);
				}
			}
			
			/*if(arts.size() > 0){
				EmailUtils.send("雪球长文", StringUtils.join(arts, "<br><br>"));
			}*/
			
			Connection conn = null;
			List<StkXueqiuUser> users = null;
			try {
				conn = DBUtil.getConnection();
				users = JdbcUtils.list(conn, "select * from stk_xueqiu_user order by id", StkXueqiuUser.class);
			}finally{
				if (conn != null) conn.close();
			}
			i = 0;
			for(StkXueqiuUser user : users){
				//System.out.println("2 user name:"+user.getName()+",id:"+user.getUserId());
				boolean existing = false;
				for(XueqiuUser member : MEMBERS){
					if(member.id.equals(user.getUserId())){
						existing = true;
						break;
					}
				}
				if(!existing){
					XueqiuUser u = new XueqiuUser();
					u.id = user.getUserId();
					u.name = user.getName();
					this.getLongArticle(u, arts);
				}
				if(i++ % 100 == 0){
					Thread.sleep(1000*15);
				}
			}
			/*if(arts.size() > 0){
				EmailUtils.send("非关注大V雪球长文", StringUtils.join(arts, "<br><br>"));
			}*/
			
		} catch (Exception e) {
			EmailUtils.send("XueqiuLongArticleJob Error", ExceptionUtils.getException(e));
		}
	}
	
	private void initUsers() throws Exception {
		int cnt = 1;
		int maxCnt = 0;
		do{
			String page = HttpUtils.get("https://xueqiu.com/friendships/groups/members.json?uid=6237744859&gid=0&page="+cnt+"&_="+new Date().getTime(),null,XueqiuUtils.getCookies(), "gb2312");
			Map m = JsonUtils.testJson(page);
			maxCnt = Integer.parseInt(String.valueOf(m.get("maxPage")));
			List<Map> users = (List<Map>)m.get("users");
			for(Map user : users){
				if(ID_EXCLUDED.contains(String.valueOf(user.get("id")))){
					continue;
				}
				if(Integer.parseInt(String.valueOf(user.get("verified_type"))) == 4){//雪球官方账号
					continue;
				}
				XueqiuUser u = new XueqiuUser();
				u.id = String.valueOf(user.get("id"));
				u.name = String.valueOf(user.get("screen_name"));
				MEMBERS.add(u);
			}
			if(++cnt > maxCnt){
				break;
			}
		}while(true);
	}
	
	private List<XueqiuArticle> getLongArticle(XueqiuUser user, List<XueqiuArticle> results) throws Exception {
		int pageCnt = 1;
		while(true){
			String page = HttpUtils.get("https://xueqiu.com/v4/statuses/user_timeline.json?user_id="+user.id+"&page="+(pageCnt++)+"&type=2&_="+new Date().getTime(),null,XueqiuUtils.getCookies(), "gb2312");
			if(JsonUtils.isString(page)){
				return results;
			}
			boolean flag = false;
			Map m = JsonUtils.testJson(page);
			List<Map> arts = (List<Map>)m.get("statuses");
			if(arts.size() == 0)break;
			for(Map art : arts){
				Long time = Long.valueOf(String.valueOf((art.get("created_at"))));
				long l = new Date().getTime();
				if(time.longValue() < l - 60*60*1000*24*2){
					flag = true;
					break;
				}
				if(time.longValue() >= l - 60*60*1000*24)continue;
				if(time.longValue() < l - 60*60*1000*24 && time.longValue() >= l - 60*60*1000*24*2){
					XueqiuArticle a = new XueqiuArticle();
					a.user = user;
					a.id = String.valueOf(art.get("id"));
					a.createAt = time.longValue();
					a.title = String.valueOf(art.get("title"));
					a.replyCount = Integer.parseInt(String.valueOf(art.get("reply_count")));
					if(a.replyCount < 10)continue;
					a.description = String.valueOf(art.get("description"));
					if(a.title == null || a.title.length() == 0){
						a.title = a.description;
					}
					
					a.title = IndexUtils.containAnyStksNameWrapByString(a.title, "<font color='blue'>","</font>");
					a.description = IndexUtils.containAnyStksNameWrapByString(a.description, "<font color='blue'>","</font>");
					results.add(a);
					if(results.size() >= 20){
						//System.out.println("send xueqiu long article mail ......");
						EmailUtils.send("雪球长文", StringUtils.join(results, "<br><br>"));
						results.clear();
					}
				}
			}
			if(flag)break;
		}
		return results;
	}
	
	public static void main(String[] args) throws Exception {
		XueqiuLongArticleJob job = new XueqiuLongArticleJob();
		job.execute(null);
		//job.initUsers();
	}

}




