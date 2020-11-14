package com.stk123.task.thread.pool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkInternetSearch;
import com.stk123.model.Index;
import com.stk123.common.db.connection.ConnectionPool;
import com.stk123.service.ExceptionUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.service.HttpUtils;


public class ThreadPoolUtils {
	
	public static void run() throws InterruptedException {
		// only two threads
		ExecutorService exec = Executors.newFixedThreadPool(2);
		for (int index = 0; index < 100; index++) {
			Runnable run = new Runnable() {
				public void run() {
					long time = (long) (Math.random() * 1000);
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
					}
				}
			};
			exec.execute(run);
		}
		// must shutdown
		exec.shutdown();
	}
	
	public static List<Object> run(List<Callable> tasks, int poolSize) throws InterruptedException, ExecutionException {
		// 创建一个线程池
		ExecutorService exec = Executors.newFixedThreadPool(poolSize);
		// 调用CompletionService的take方法是，会返回按完成顺序放回任务的结果
		CompletionService pool = new ExecutorCompletionService(exec);
		// 创建多个有返回值的任务
		//List<Future> list = new ArrayList<Future>();
		for (int i = 0; i < tasks.size(); i++) {
			Callable c = tasks.get(i);
			// 执行任务并获取Future对象
			Future f = pool.submit(c);
			//list.add(f);
		}
		List<Object> results = new ArrayList<Object>();
		// 获取所有并发任务的运行结果
		for (int i = 0; i < tasks.size(); i++) {
			//从Future对象上获取任务的返回值，并输出到控制台
			//System.out.println(">>>" + pool.take().get().toString());
			Future f = pool.take();
			results.add(f.get());
		}
		System.out.println("..................");
		// 关闭线程池
		exec.shutdown();
		return results;
	}
	
	
	public static void initKLines(List<Stk> stks, final boolean flag,int numberOfWorker) throws Exception {
		ConnectionPool pool = null;
		try {
			pool = ConnectionPool.getInstance();
			
			final CountDownLatch countDownLatch = new CountDownLatch(stks.size());
			ExecutorService exec = Executors.newFixedThreadPool(numberOfWorker);
			for(final Stk stk : stks){
				Runnable run = new Runnable() {
					public void run() {
						Connection conn = null;
						Index index = null;
						try{
							conn = ConnectionPool.getInstance().getConnection();
							index = new Index(conn, stk.getCode());
							System.out.println(index.getCode());
							if(flag){
								index.initKLine();
							}else{
								index.initKLines(30);
							}
						}catch(Exception e){
							//e.printStackTrace();
							ExceptionUtils.insertLog(conn, index.getCode(), e);
						}finally{
							ConnectionPool.getInstance().release(conn);
							countDownLatch.countDown();
						}
						
					}
				};
				exec.execute(run);
			}
			exec.shutdown();
			countDownLatch.await();
			
		} finally {
			if (pool != null) pool.closePool();
		}
	}
	

	public static void main(String[] args) throws Exception {
//		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
//		final ConnectionPool pool = ConnectionPool.getInstance();
//		try {
//			List<StkInternetSearch> searchs = JdbcUtils.list(pool.getConnection(), "select * from stk_internet_search where status=1 order by search_source", StkInternetSearch.class);
//			List<Callable> tasks = new ArrayList<Callable>();
//			for(final StkInternetSearch search : searchs){
//				Callable task = new Callable(){
//					public Object call() throws Exception {
//						Connection conn = null;
//						try{
//							conn = pool.getConnection();
//							switch(search.getSearchSource().intValue()){
//								case 1 :
//									return parseSinaBlogPage(conn, search);
//								case 10 :
//									/*String date = parseStkAccountInfo(conn, search);
//									if(date != null){
//										updateSearch(conn, date, search);
//										EmailUtils.send("【股票账户数据,日期:"+date+"】", createStkAccountInfoTable(conn, 50));
//									}*/
//									return null;
//								case 11 :
//									return parseGov(conn,search);
//								default :
//									return null;
//							}
//						}finally{
//							pool.release(conn);
//						}
//					}
//				};
//				tasks.add(task);
//			}
//			List<Object> results = run(tasks,4);
//
//			Name2ListSet<String,String> set = new Name2ListSet<String,String>();
//			for(Object result : results){
//				if(result != null){
//					Map<String,Object> map = (Map<String,Object>)result;
//					StkInternetSearch search = (StkInternetSearch)map.get("search");
//					String title = StkDict.getDict(StkDict.INTERNET_SEARCH_TYPE, search.getSearchSource().toString());
//					set.add(title, (List<String>)map.get("result"));
//				}
//			}
//
//			if(set.size() > 0){
//				StringBuffer sb = new StringBuffer();
//				for(Name2Value<String, List<String>> pair : set.getList()){
//					sb.append("<b>"+pair.getName()+":<b><br>");
//					for(String txt : pair.getValue()){
//						sb.append("&nbsp;&nbsp;").append(txt).append("<br>");
//					}
//				}
//				System.out.println(sb.toString());
//				//EmailUtils.send("【关注对象最新动态,日期:"+StkUtils.getToday()+"】", sb.toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			ExceptionUtils.insertLog(pool.getConnection(), e);
//		} finally {
//			if (pool != null)
//				pool.closePool();
//		}
	}
	
	public static void updateSearch(Connection conn, String lastText, StkInternetSearch search){
		List params = new ArrayList();
		params.add(lastText);
		params.add(search.getSearchUrl());
		//JdbcUtils.update(conn, "update stk_internet_search set last_search_text=?,update_time=sysdate where search_url=?", params);
	}
	
	public static Map<String,Object> parseSinaBlogPage(Connection conn, StkInternetSearch search) throws Exception {
		String page = HttpUtils.get(search.getSearchUrl(), null, "utf-8");
		List<Node> aNode = HtmlUtils.getNodeListByTagName(page, null, "a");
		int i = 0;
		List<String> results = new ArrayList<String>();
		for(Node a:aNode){
			TagNode node = (TagNode)a;
			String text = a.toHtml();
			if(node.getAttribute("href").startsWith("http://blog.sina.com.cn/s/blog")){
				String title = StringUtils.substringBetween(text, "title=\"", "\"");
				String msg = node.toPlainTextString();
				text = StringUtils.replace(text, msg, title);
				//System.out.println(text+",,,,,,,,,,"+msg);
				if(addResult(search,text,i,results)){
					break;
				}
			}
		}
		List<String> urlsSinaBlog = new ArrayList<String>();
		if(results.size() > 0){
			updateSearch(conn, results.get(0), search);
			for(String s : results){
				System.out.println(s);
				urlsSinaBlog.add("[<a target='_blank' href='"+search.getSearchUrl()+"'>"+search.getDesc1()+"</a>]"+s);
			}
			return map(search,urlsSinaBlog);
		}
		return null;
	}
	
	public static Map<String,Object> parseGov(Connection conn, StkInternetSearch search) throws Exception{
		String page = HttpUtils.get("http://www.gov.cn/zfwj/gwyfw.htm", "UTF-8");
		//System.out.println(page);
		List<Node> links = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "a", "class", "l14");
		List<String> results = new ArrayList<String>();
		int i = 0;
		for(Node link : links){
			String text = link.toHtml();
			if(addResult(search,text,i,results)){
				break;
			}
		}
		if(results.size() > 0){
			updateSearch(conn, results.get(0), search);
			return map(search,results);
		}
		return null;
	}
	
	private static Map<String, Object> map(StkInternetSearch search, List<String> results){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("search", search);
		map.put("result", results);
		return map;
	}
	
	public static boolean addResult(StkInternetSearch search, String text, int i, List<String> results){
		if(search.getLastSearchText() == null){
			search.setLastSearchText(text);
			results.add(text);
			return true;
		}else{
			if(search.getLastSearchText().equals(text) || i++ >= 10){
				return true; 
			}else{
				results.add(text);
			}
		}
		return false;
	}
	
}

