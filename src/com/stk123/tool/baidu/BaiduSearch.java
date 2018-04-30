package com.stk123.tool.baidu;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlparser.Node;

import com.stk123.bo.Stk;
import com.stk123.model.Index;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.html.HtmlA;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.collection.Name2Value;
import com.stk123.tool.util.collection.SimilarSet;
import com.stk123.web.StkConstant;
import com.stk123.web.WebUtils;


@SuppressWarnings("unchecked")
public class BaiduSearch {
	
	public static boolean SearchSwitch = true;
	
	public static class SearchKeyword{
		public String keyword;
		public boolean existing = false;
		
		String serchkeyword;
		boolean searchInTitle;
		int weight;
		
		public SearchKeyword(String keyword, String serchkeyword, boolean searchInTitle){
			this(keyword, serchkeyword, searchInTitle, 1);
		}
		
		public SearchKeyword(String keyword, String serchkeyword, boolean searchInTitle, int weight){
			this.keyword = keyword;
			this.serchkeyword = serchkeyword;
			this.searchInTitle = searchInTitle;
			this.weight = weight;
		}
	}
	
	public static void main(String[] args) throws Exception {
		//System.out.println(BaiduSearch.getKeywordsTotalWeight("中颖电子"));
		//System.out.println(BaiduSearch.getKeywordsTotalWeight("丹邦科技"));
		/*Date date = StkUtils.addDay(new Date(), -90);
		System.out.println(getBaiduNewsCount(date, "丹邦科技 新产品", false));*/
		
		//(行业 | 收入 | 营收) 连续 (下降 | 下跌)
		List<String> results = getBaiduNews(StkUtils.addDay(new Date(), -10), "(行业 | 收入 | 营收) 连续 (下降 | 下跌)", true);
		System.out.println(results);
		EmailUtils.send("test", StringUtils.join(results, ""));
	}
	
	private static List<SearchKeyword> getKeywords() {
		List<SearchKeyword> BAIDU_NEWS_KEYWORDS = new ArrayList<SearchKeyword>();
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("超预期/爆发", "{stk} 超预期 | {stk} 爆发", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("高增长/高成长", "{stk} 高增长 | {stk} 高成长", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("提价", "{stk} 提价", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("强烈推荐/利好", "{stk} 强烈推荐 | {stk} 利好", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("行业/产业", "{stk} 行业 | {stk} 产业", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("景气", "{stk} 景气", true));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("竞争对手", "{stk} 竞争对手", false));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("上游/下游", "{stk} 上游 | {stk} 下游", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("龙头", "{stk} 龙头", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("新产品/新业务", "{stk} 新产品 | {stk} 新业务", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("产能释放/供不应求", "{stk} 产能释放 | {stk} 供不应求", true, 2));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("转型", "{stk} 转型", true));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("管理层", "{stk} 管理层", false));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("出货量/开工率", "{stk} 出货量 | {stk} 开工率", false, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("订单/中标/合同", "{stk} 订单 | {stk} 中标 | {stk} 合同", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("市场占有率", "{stk} 市场占有率", false, 2));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("核心技术", "{stk} 核心技术", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("重组/并购/收购", "{stk} 重组 | {stk} 并购 | {stk} 收购", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("股权激励", "{stk} 股权激励", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("回购/增持", "{stk} 回购 | {stk} 增持", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("增发", "{stk} 增发", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("非公开发行", "{stk} 非公开发行", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("举牌", "{stk} 举牌", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("拐点", "{stk} 拐点", true, 2));
		//垄断
		return BAIDU_NEWS_KEYWORDS;
	}
	
	public static List<SearchKeyword> searchKeywords(final String stkName) throws Exception {
		List<SearchKeyword> BAIDU_NEWS_KEYWORDS = getKeywords();
		if(!SearchSwitch){
			return BAIDU_NEWS_KEYWORDS;
		}
		final Date date = StkUtils.addDay(new Date(), -90);
		final CountDownLatch countDownLatch = new CountDownLatch(BAIDU_NEWS_KEYWORDS.size());
		ExecutorService exec = Executors.newFixedThreadPool(5);
		for(final SearchKeyword sk : BAIDU_NEWS_KEYWORDS){
			Runnable run = new Runnable() {
				public void run() {
					try{
						try {
							String s = StringUtils.replace(sk.serchkeyword, "{stk}", stkName);
							int n = BaiduSearch.getBaiduNewsCount(date , s, sk.searchInTitle);
							if(n == 2){
								sk.existing = true;
							}
						} catch (Exception e) {
							if(e instanceof IOException || e instanceof NoHttpResponseException || e instanceof UnknownHostException){
								try {
									Thread.currentThread().sleep(1000 * 300);
								} catch (InterruptedException e1) {	}
							}
							e.printStackTrace();
						}
					}finally{
						countDownLatch.countDown();
					}
				}
			};
			exec.execute(run);
		}
		exec.shutdown();
		countDownLatch.await();
		
		return BAIDU_NEWS_KEYWORDS;
	}
	
	public static int getKeywordsTotalWeight(String stkName) throws Exception{
		List<SearchKeyword> BAIDU_NEWS_KEYWORDS = BaiduSearch.searchKeywords(stkName);
		int total = 0;
		for(SearchKeyword sk : BAIDU_NEWS_KEYWORDS){
			if(sk.existing){
				total += sk.weight;
			}
		}
		return total;
	}
	
	/**
	 * @return 0:没有相关新闻; 1:有相关新闻; 2:date日之后有新闻
	 */
	public static int getBaiduNewsCount(Date date,String searchword,boolean searchInTitle) throws Exception {
		String url = WebUtils.getBaiduNewsUrl(URLEncoder.encode(searchword, StkConstant.ENCODING_UTF_8), searchInTitle);
		//System.out.println(url);
		String page = HttpUtils.get(url,StkConstant.ENCODING_UTF_8);
        Node node = HtmlUtils.getNodeByAttribute(page, null, "id", "header_top_bar");
        if(node == null){
        	return 0;
        }
        String div = node.toPlainTextString();
        //System.out.println(searchword+"="+searchword+"="+StkUtils.getNumberFromString(div));
        if(div.indexOf("找到相关新闻0篇") >= 0){
            return 0;
        }else{
        	Node left = HtmlUtils.getNodeByAttribute(page, null, "id", "content_left");
        	//System.out.println(left.toHtml());
            //List<Node> list = HtmlUtils.getNodeListByTagName(left, "li");
        	List<Node> list = HtmlUtils.getNodeListByTagNameAndAttribute(left, "div", "class", searchInTitle?"result title":"result");
            for(Node li : list){
            	Node span = HtmlUtils.getNodeByAttribute(li.toHtml(), null, "class", searchInTitle?"c-title-author":"c-author");
            	if(span != null){
            		String text = span.toPlainTextString();
            		//System.out.println(text);
            		String str = StkUtils.getMatchString(text, StkUtils.PATTERN_YYYYMMDD_HHMM_CHINESE);
            		if(str == null && StringUtils.contains(str, "小时前")){
            			return 2;
            		}
            		if(str != null && str.length() > 0){
            			Date time = StkUtils.sf_ymd15.parse(str);
	            		if(date.after(time)){
	            			return 2;
	            		}
            		}
            	}
            }
            return 1;
        }
	}
	
	public static List<String> getBaiduNews(Date date,String searchwordorUrl,boolean searchInTitle) throws Exception {
		String url = searchwordorUrl.startsWith("http")?searchwordorUrl:WebUtils.getBaiduNewsUrl(URLEncoder.encode(searchwordorUrl, StkConstant.ENCODING_UTF_8), searchInTitle);
		//System.out.println(url);
		String page = HttpUtils.get(url,StkConstant.ENCODING_UTF_8);
        Node node = HtmlUtils.getNodeByAttribute(page, null, "id", "header_top_bar");
        
        List<String> results = new ArrayList<String>();
        if(node == null){
        	return results;
        }
        String div = node.toPlainTextString();
        //System.out.println(searchword+"="+searchword+"="+StkUtils.getNumberFromString(div));
        if(div.indexOf("找到相关新闻0篇") >= 0){
            return results;
        }else{
        	Node left = HtmlUtils.getNodeByAttribute(page, null, "id", "content_left");
        	//System.out.println(left.toHtml());
            //List<Node> list = HtmlUtils.getNodeListByTagName(left, "li");
        	List<Node> list = HtmlUtils.getNodeListByTagNameAndAttribute(left, "div", "class", searchInTitle?"result title":"result");
            for(Node li : list){
            	//System.out.println(li.toHtml());
            	Node span = HtmlUtils.getNodeByAttribute(li.toHtml(), null, "class", searchInTitle?"c-title-author":"c-author");
            	if(span != null){
            		String text = span.toPlainTextString();
            		if(StringUtils.contains(text, "前")){
            			results.add(li.toHtml());
            		}else{
	            		String str = StkUtils.getMatchString(text, StkUtils.PATTERN_YYYYMMDD_HHMM_CHINESE);
	            		if(str != null && str.length() > 0){
	            			Date time = StkUtils.sf_ymd15.parse(str);
		            		if(date.before(time)){
		            			results.add(li.toHtml());
		            		}
	            		}
            		}
            		//results.add(text);
            	}
            }
            return results;
        }
	}

	private static boolean contain(List<String> list, String str){
		for(String s : list){
			if(StringUtils.contains(str, s)){
				return true;
			}
		}
		return false;
	}
	

}
