package com.stk123.task.sub;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.StkDictionary;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.baidu.BaiDuHi;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.web.StkDict;


/**
 * ѩ������
 * http://xueqiu.com/statuses/user_timeline.json?user_id=5964068708&page=1&type=&access_token=ZAKjlaAg7GQSInYrP2cnSl&_=1396073863568
 */
public class XueqiuZhuTie {
	
	static int noOfError = 0;
	private static Set sendMsg = new HashSet();

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		//Connection conn = null;
		try{
			//conn = DBUtil.getConnection();
			Date lastStartTime = null;
			while(true){
				try{
					Date startTime = new Date();
					if(lastStartTime == null){
						lastStartTime = StkUtils.addMinute(startTime, -180);
					}
					System.out.println("[XueqiuZhuTie]"+lastStartTime);
					run(lastStartTime);
					lastStartTime = startTime;
					Thread.sleep(1000*60*10);
					Date now = new Date();
					if(now.getHours() >= 17){
						break;
					}
				}catch(Exception e){
					EmailUtils.send("ʵʱ���ѩ����������", e);
					e.printStackTrace();
					//ExceptionUtils.insertLog(conn, null, e);
					//if(noOfError > 5)break;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			//if (conn != null) conn.close();
		}
	}
	
	private static void run(Date time) {
		try{
			List<String> results = new ArrayList<String>();
			Map<String, StkDictionary> articles = StkDict.getDict(StkDict.XUEQIU_ZHUTIE);
			for(Map.Entry<String, StkDictionary> dict : articles.entrySet()){
				if(dict.getValue().getParam() != null)continue;
				String page = HttpUtils.get("http://xueqiu.com/statuses/user_timeline.json?user_id="+dict.getKey()+"&page=1&type=", null, XueqiuUtils.getCookies(), "gb2312");
				Map<String, Class> m = new HashMap<String, Class>();
		        m.put("statuses", Map.class);
				Map map = (Map)JsonUtils.getObject4Json(page, Map.class, m);
				//System.out.println(map.get("stocks"));
				Iterator it = ((List)map.get("statuses")).iterator();
				while(it.hasNext()){
					Map mp = (Map)it.next();
					String article = String.valueOf(mp.get("text"));
					//Date createTime = new Date(String.valueOf(mp.get("created_at")));
					String createAt = String.valueOf(mp.get("created_at"));
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(Long.parseLong(createAt));
					Date createTime = cal.getTime();
					if(createTime.after(time) && !sendMsg.contains(createAt)){
						sendMsg.add(createAt);
						results.add(dict.getValue().getText()+"["+StkUtils.formatDate(createTime,StkUtils.sf_ymd9)+"]"+":"+article);
					}
				}
				Thread.currentThread().sleep(1000);
			}
			//System.out.println(results);
			if(results.size() > 0){
				//System.out.println(StringUtils.join(results, "<br/>"));
				String content = StringUtils.join(results, "<br/><br/>");
				EmailUtils.send("ʵʱ���ѩ������", content);
				Date now = new Date();
				if(now.getHours() <= 15 && content != null && (content.contains("СС����")||content.contains("�É�"))){
					//EmailUtils.send("kai.fan@suncorp.com.au", "����", content);
				}
				results.add("--------------------");
				Collections.reverse(results);
				BaiDuHi.sendSMS(results);
			}
		}catch(Exception e){
			noOfError ++;
			if(noOfError < 2){
				try {
					EmailUtils.send("ʵʱ���ѩ����������", e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

}
