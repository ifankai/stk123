//package com.stk123.task.quartz.job;
//
//import java.sql.Connection;
//import java.util.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.stk123.entity.StkTextEntity;
//import com.stk123.model.constant.TextConstant;
//import com.stk123.model.xueqiu.XueqiuPost;
//import com.stk123.model.xueqiu.XueqiuPostRoot;
//import com.stk123.repository.StkTextRepository;
//import com.stk123.service.core.TextService;
//import lombok.extern.apachecommons.CommonsLog;
//import org.apache.commons.lang.StringUtils;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//
//import com.stk123.model.bo.Stk;
//import com.stk123.model.Index;
//import com.stk123.util.ServiceUtils;
//import com.stk123.service.XueqiuService;
//import com.stk123.common.db.util.CloseUtil;
//import com.stk123.common.db.util.DBUtil;
//import com.stk123.common.util.ChineseUtils;
//import com.stk123.common.util.EmailUtils;
//import com.stk123.util.HttpUtils;
//import com.stk123.common.util.JdbcUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * 雪球个股页面长文
// */
//@CommonsLog
//@Service
//public class XueqiuStockArticleJob implements Job {
//
//	private static int codeIndex = 0;
//	private static List<Stk> stocks = null;
//	public static List<StkTextEntity> results = new ArrayList();
//
//	private static Set<Long> ids = new HashSet<Long>();
//
//	@Autowired
//	private TextService textService;
//	@Autowired
//    private StkTextRepository stkTextRepository;
//
//	@Override
//	public void execute(JobExecutionContext arg0) {
//		Connection conn = null;
//		try {
//			conn = DBUtil.getConnection();
//			List<Stk> stks = stocks;
//			if(stks == null){
//				String sql = "select * from ( "
//						+ "select code,name from stk_cn a "
//						+ "where hot>3000 and not exists (select 1 from stk_cn b where a.code=b.code and b.name like '%ST%') "
//						+ "union all "
//						+ "select code,name from stk_hk where hot>1000 ) order by reverse(code)";
//				stks = stocks = JdbcUtils.list(conn, sql, Stk.class);
//				if(new Date().getDate() % 2 == 0){
//					Collections.reverse(stks);
//				}
//			}
//
//			if(codeIndex >= stks.size()){
//				return;
//			}
//			for(int i=0; i<4 && codeIndex <stks.size(); i++){
//				Stk stk = stks.get(codeIndex++);
//				log.info("XueqiuStockArticleJob="+stk.getCode()+"["+results.size()+"]");
//				List<StkTextEntity> list = getArticles(conn, stk.getCode());
//				if(list.size() > 0){
//					results.addAll(list);
//					if(results.size() >= 20){
//						EmailUtils.send("雪球个股长文", StringUtils.join(results, "<br><br>"));
//						results.clear();
//					}
//				}
//				//System.out.println(results);
//				Thread.sleep(1000*15);
//			}
//			if(codeIndex >= stks.size()){
//				if(results.size() > 0){
//					EmailUtils.send("雪球个股长文2", StringUtils.join(results, "<br><br>"));
//					results.clear();
//				}
//				return;
//			}
//		}catch(Exception e){
//			log.error("XueqiuStockArticleJob", e);
//		}finally{
//			CloseUtil.close(conn);
//    			if(codeIndex >= stocks.size()){
//				if(results.size() > 0){
//					EmailUtils.send("雪球个股长文end", StringUtils.join(results, "<br><br>"));
//				}
//			}
//		}
//
//	}
//
//	//https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol=SH603611&hl=0&source=all&sort=alpha&page=1&_=1507209904103
//	public List<StkTextEntity> getArticles(Connection conn, String code) throws Exception {
//		String scode = ServiceUtils.getStkLocation(code)+code;
//		if(StringUtils.length(code) == 5){
//			scode = code;
//		}
//		Map<String, String> requestHeaders = XueqiuService.getCookies();
//		//String page = HttpUtils.get("https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol="+scode+"&hl=0&source=all&sort=alpha&page=1&_="+new Date().getTime(),null, requestHeaders, "gb2312");
//
//        String url = "https://xueqiu.com/query/v1/symbol/search/status?count=100&comment=0&symbol="+scode+"&hl=0&source=all&sort=&page=1&q=";
//		String page = HttpUtils.get(url,null, requestHeaders, "gb2312");
////		System.out.println(page);
//		List<StkTextEntity> results = new ArrayList<StkTextEntity>();
//		if("400".equals(page)){
//			return results;
//		}
//		Index index = new Index(conn, code);
//
////		Map map = JsonUtils.testJson(page);
//		ObjectMapper objectMapper = new ObjectMapper();
//		XueqiuPostRoot xueqiuPostRoot = objectMapper.readValue(page, XueqiuPostRoot.class);
//		//log.info(xueqiuPostRoot);
//
////		List<Map> arts = (List<Map>)map.get("list");
//		List<XueqiuPost> list = xueqiuPostRoot.getList();
//		for(XueqiuPost post : list){
//			long createdAt = post.getCreated_at();
//			if(ServiceUtils.addDay(new Date(), -2).getTime() <= createdAt && ServiceUtils.addDay(new Date(), 0).getTime() >= createdAt){
//				boolean flag = false;
//				String title = post.getTitle();
//				String text = post.getText();
//				//Map retweeted_status = (Map)art.get("retweeted_status");
//
//				if(ChineseUtils.length(text) < 100){//内容长度控制，太短的排除掉，100=50个中文
//					continue;
//				}
//
//				String name = index.getName();
//				if(StringUtils.length(text) > 300 && (text.contains(name) || text.contains(StringUtils.replace(name, " ", "")))){
//					flag = true;
//				}else if(!StringUtils.isEmpty(title) && (title.contains(name) || title.contains(StringUtils.replace(name, " ", "")))){
//					flag = true;
//				}
//				if(post.getUser().getId() != 0 && post.getUser().getFollowers_count() < 100){ //排除粉丝数小于100
//					continue;
//				}
//
//				if(flag){
//					int replyCount = post.getReply_count();
//					if(replyCount >= 10){
//
//						if(!ids.contains(post.getId())) {
//                            if(stkTextRepository.existingByPostId(post.getId()) > 0) continue;
//
//							StkTextEntity stkTextEntity = new StkTextEntity();
//							stkTextEntity.setUserId(post.getUser_id());
//							stkTextEntity.setUserName(post.getUser().getScreen_name());
//							stkTextEntity.setCode(code);
//							stkTextEntity.setCreatedAt(new Date(post.getCreated_at()));
//							stkTextEntity.setPostId(post.getId());
//							stkTextEntity.setTitle(title);
//							stkTextEntity.setText(text);
//							stkTextEntity.setTextDesc(post.getDescription());
//							stkTextEntity.setType(TextConstant.TYPE_XUEQIU);
//							stkTextEntity.setSubType(TextConstant.SUB_TYPE_XUEQIU_DEFAUTL);
//							stkTextEntity.setInsertTime(new Date());
//							stkTextEntity.setCodeType(TextConstant.CODE_TYPE_STK);
//							stkTextEntity.setFollowersCount(post.getUser().getFollowers_count());
//							stkTextEntity.setReplyCount(post.getReply_count());
//							stkTextEntity.setUserAvatar(StringUtils.split(post.getUser().getProfile_image_url(), ",")[1]);
//
//							results.add(stkTextEntity);
//							ids.add(post.getId());
//							textService.save(stkTextEntity);
//						}
//
//					}
//				}
//			}
//		}
//
//
//		/*for(Map art : list){
//			long createAt = Long.valueOf(String.valueOf(art.get("created_at")));
//			if(ServiceUtils.addDay(new Date(), -2).getTime() <= createAt && ServiceUtils.addDay(new Date(), 0).getTime() >= createAt){
//				boolean flag = false;
//				String title = (String)art.get("title");
//				String text = (String)art.get("text");
//				//System.out.println("title:"+title);
//				//Map retweeted_status = (Map)art.get("retweeted_status");
//				if(StringUtils.isEmpty(title)){
//					title = (String)art.get("text");
//					if(ChineseUtils.length(title) < 100){//内容长度控制，太短的排除掉，100=50个中文
//						continue;
//					}
//				}
//				String name = index.getName();
//				if(StringUtils.length(text) > 300 && (text.contains(name) || text.contains(StringUtils.replace(name, " ", "")))){
//					flag = true;
//				}else if(!StringUtils.isEmpty(title) && (title.contains(name) || title.contains(StringUtils.replace(name, " ", "")))){
//					flag = true;
//				}
//
//				if(flag){
//					if(Integer.parseInt(String.valueOf(art.get("reply_count"))) >= 10){
//						XueqiuUser u = new XueqiuUser();
//						u.id = String.valueOf(art.get("user_id"));
//						u.name = String.valueOf(((Map)art.get("user")).get("screen_name"));
//
//						XueqiuArticle a = new XueqiuArticle();
//						a.code = code;
//						a.user = u;
//						a.createAt = createAt;
//						a.id = String.valueOf(art.get("id"));
//						a.title = String.valueOf(art.get("title"));
//						a.description = String.valueOf(art.get("description"));
//						a.replyCount = Integer.parseInt(String.valueOf(art.get("reply_count")));
//						if(a.title == null || a.title.length() == 0){
//							a.title = a.description;
//						}
//						if(!ids.contains(a.id)){
//							results.add(a);
//							ids.add(a.id);
//							insertText(conn, code, a);
//
//							String followersCount = (String)((Map)art.get("user")).get("followers_count");
//							if(StringUtils.isNotEmpty(followersCount)){
//								int fc = Integer.valueOf(followersCount);
//								if(fc < 100){
//									continue;
//								}
//							}
//
//							int cnt = Text.countByTitle(conn, a.title, 30);
//							if(cnt > 1){
//								continue;
//							}
//
//                            StkXqPostEntity xqPost = new StkXqPostEntity();
//                            xqPost.setId(Long.valueOf((String)art.get("id")));
//                            xqPost.setTitle((String) art.get("title"));
//                            xqPost.setText((String) art.get("description"));
//
//                            String createDate = (String)art.get("created_at");
//                            if(createDate != null) xqPost.setCreatedAt(new Date(Long.parseLong(createDate)));
//                            xqPost.setReplyCount(Integer.valueOf((String)art.get("reply_count")));
//
//							if(followersCount != null) xqPost.setFollowersCount(Integer.valueOf(followersCount));
//                            xqPost.setUserId(Long.valueOf((String)art.get("user_id")));
//                            xqPost.setUserName((String) ((Map)art.get("user")).get("screen_name"));
//                            xqPost.setUserAvatar(StringUtils.split((String) ((Map)art.get("user")).get("profile_image_url"), ",")[1]);
//
////                            ObjectMapper objectMapper = new ObjectMapper();
//                            String json = objectMapper.writeValueAsString(xqPost);
//
//                            requestHeaders.put("Content-Type", "application/json;charset=UTF-8");
//                            List<Header> respHeaders = new ArrayList<Header>();
//							HttpUtils.post("http://81.68.255.181:8080/api/xq/post", null, json,requestHeaders,"UTF-8", respHeaders);
//
//						}
//					}
//				}
//			}
//		}*/
//
//		return results;
//	}
//
//
//	public static void main(String[] args) throws Exception {
//		/*String code = "300185";
//		List<XueqiuArticle> results = getArticles(code, 120, 10);
//
//		if(results.size() > 0){
//			EmailUtils.sendAndReport(code+" - 雪球长文", StringUtils.join(results, "<br><br>"));
//		}*/
//
//		Connection conn = null;
//		try {
//			conn = DBUtil.getConnection();
//			XueqiuStockArticleJob xueqiuStockArticleJob = new XueqiuStockArticleJob();
//			List<StkTextEntity> results = xueqiuStockArticleJob.getArticles(conn,"002460");
//			System.out.println(results);
//		}finally{
//			CloseUtil.close(conn);
//		}
//	}
//
//
//}
