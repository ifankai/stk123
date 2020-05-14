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

import com.stk123.task.XueqiuUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;

public class XueqiuUserJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("XueqiuUserJob executing...");
		try {
			initUser("1287305957");//雪球访谈
			initUser("1955602780");//不明真相的群众
			initUser("7607677791");//秃鹫投资
			initUser("5819606767");//DAVID自由之路
			initUser("3386153330");//刘志超
		} catch (Exception e) {
			EmailUtils.send("XueqiuUser Error", ExceptionUtils.getException(e));
		}

	}
	
	private static void initUser(String userId) throws Exception {
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			int cnt = 1;
			int maxCnt = 0;
			do{
				try{
					System.out.println(cnt);
					String page = HttpUtils.get("https://xueqiu.com/friendships/followers.json?pageNo="+cnt+"&uid="+userId,null,XueqiuUtils.getCookies(), "gb2312");
					//System.out.println(page);
					Map m = JsonUtils.testJson(page);
					maxCnt = Integer.parseInt(String.valueOf(m.get("maxPage")));
					
					List<Map> users = (List<Map>)m.get("followers");
					for(Map user : users){
						String id = String.valueOf(user.get("id"));
						String name = String.valueOf(user.get("screen_name"));
						int followers_count = Integer.parseInt(String.valueOf(user.get("followers_count")));
						if(followers_count >= 1000){
							List params = new ArrayList();
							params.add(id);
							params.add(name);
							params.add(id);
							JdbcUtils.insert(conn, "insert into stk_xueqiu_user select s_xueqiu_user_id.nextval,?,? from dual where not exists (select 1 from stk_xueqiu_user where user_id=?)", params);
						}
					}
					
					if(cnt%200 == 0){
						Thread.currentThread().sleep(1000*60*5);
					}
					
					if(++cnt > maxCnt){
						break;
					}
				}catch(Exception e){
					e.printStackTrace();
					//EmailUtils.send("XueqiuUser Error", ExceptionUtils.getException(e));
					Thread.currentThread().sleep(1000*60);
				}
			}while(true);
		}finally{
			if (conn != null) conn.close();
		}
	}

	public static void main(String[] args) throws Exception {
		XueqiuUserJob job = new XueqiuUserJob();
		job.execute(null);
	}

}
