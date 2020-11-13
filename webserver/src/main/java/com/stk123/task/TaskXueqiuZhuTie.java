package com.stk123.task;

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

import com.stk123.model.bo.StkDictionary;
import com.stk123.service.ServiceUtils;
import com.stk123.service.XueqiuService;
import com.stk123.service.baidu.BaiDuHi;
import com.stk123.common.db.TableTools;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.service.DictService;


/**
 * 雪球主贴
 * http://xueqiu.com/statuses/user_timeline.json?user_id=5964068708&page=1&type=&access_token=ZAKjlaAg7GQSInYrP2cnSl&_=1396073863568
 */
public class TaskXueqiuZhuTie {
	
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
						lastStartTime = ServiceUtils.addMinute(startTime, -180);
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
					EmailUtils.send("实时监控雪球主贴出错", e);
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
			Map<String, StkDictionary> articles = DictService.getDict(DictService.XUEQIU_ZHUTIE);
			for(Map.Entry<String, StkDictionary> dict : articles.entrySet()){
				if(dict.getValue().getParam() != null)continue;
				String page = HttpUtils.get("http://xueqiu.com/statuses/user_timeline.json?user_id="+dict.getKey()+"&page=1&type=", null, XueqiuService.getCookies(), "gb2312");
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
						results.add(dict.getValue().getText()+"["+ServiceUtils.formatDate(createTime,ServiceUtils.sf_ymd9)+"]"+":"+article);
					}
				}
				Thread.currentThread().sleep(1000);
			}
			//System.out.println(results);
			if(results.size() > 0){
				//System.out.println(StringUtils.join(results, "<br/>"));
				String content = StringUtils.join(results, "<br/><br/>");
				EmailUtils.send("实时监控雪球主贴", content);
				Date now = new Date();
				if(now.getHours() <= 15 && content != null && (content.contains("小小辛巴")||content.contains("婷??"))){
					//EmailUtils.send("kai.fan@suncorp.com.au", "主贴", content);
				}
				results.add("--------------------");
				Collections.reverse(results);
				BaiDuHi.sendSMS(results);
			}
		}catch(Exception e){
			noOfError ++;
			if(noOfError < 2){
				try {
					EmailUtils.send("实时监控雪球主贴出错", e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

}
