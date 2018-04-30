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
		//System.out.println(BaiduSearch.getKeywordsTotalWeight("��ӱ����"));
		//System.out.println(BaiduSearch.getKeywordsTotalWeight("����Ƽ�"));
		/*Date date = StkUtils.addDay(new Date(), -90);
		System.out.println(getBaiduNewsCount(date, "����Ƽ� �²�Ʒ", false));*/
		
		//(��ҵ | ���� | Ӫ��) ���� (�½� | �µ�)
		List<String> results = getBaiduNews(StkUtils.addDay(new Date(), -10), "(��ҵ | ���� | Ӫ��) ���� (�½� | �µ�)", true);
		System.out.println(results);
		EmailUtils.send("test", StringUtils.join(results, ""));
	}
	
	private static List<SearchKeyword> getKeywords() {
		List<SearchKeyword> BAIDU_NEWS_KEYWORDS = new ArrayList<SearchKeyword>();
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("��Ԥ��/����", "{stk} ��Ԥ�� | {stk} ����", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("������/�߳ɳ�", "{stk} ������ | {stk} �߳ɳ�", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("���", "{stk} ���", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("ǿ���Ƽ�/����", "{stk} ǿ���Ƽ� | {stk} ����", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("��ҵ/��ҵ", "{stk} ��ҵ | {stk} ��ҵ", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("����", "{stk} ����", true));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("��������", "{stk} ��������", false));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("����/����", "{stk} ���� | {stk} ����", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("��ͷ", "{stk} ��ͷ", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�²�Ʒ/��ҵ��", "{stk} �²�Ʒ | {stk} ��ҵ��", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�����ͷ�/����Ӧ��", "{stk} �����ͷ� | {stk} ����Ӧ��", true, 2));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("ת��", "{stk} ת��", true));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�����", "{stk} �����", false));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("������/������", "{stk} ������ | {stk} ������", false, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("����/�б�/��ͬ", "{stk} ���� | {stk} �б� | {stk} ��ͬ", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�г�ռ����", "{stk} �г�ռ����", false, 2));
		//BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("���ļ���", "{stk} ���ļ���", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("����/����/�չ�", "{stk} ���� | {stk} ���� | {stk} �չ�", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("��Ȩ����", "{stk} ��Ȩ����", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�ع�/����", "{stk} �ع� | {stk} ����", true, 2));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("����", "{stk} ����", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�ǹ�������", "{stk} �ǹ�������", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("����", "{stk} ����", true));
		BAIDU_NEWS_KEYWORDS.add(new SearchKeyword("�յ�", "{stk} �յ�", true, 2));
		//¢��
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
	 * @return 0:û���������; 1:���������; 2:date��֮��������
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
        if(div.indexOf("�ҵ��������0ƪ") >= 0){
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
            		if(str == null && StringUtils.contains(str, "Сʱǰ")){
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
        if(div.indexOf("�ҵ��������0ƪ") >= 0){
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
            		if(StringUtils.contains(text, "ǰ")){
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
