package com.stk123.web.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkLabel;
import com.stk123.model.bo.StkText;
import com.stk123.model.Index;
import com.stk123.model.Keyword;
import com.stk123.model.Label;
import com.stk123.model.User;
import com.stk123.service.ServiceUtils;
import com.stk123.common.db.util.sequence.SequenceUtils;
import com.stk123.common.ik.DocumentField;
import com.stk123.web.ik.StkIKUtils;
import com.stk123.web.ik.Search;
import com.stk123.common.util.HtmlUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.util.collection.Name2Value;
import com.stk123.web.core.util.RequestUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.StkDict;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;

public class TextAction implements CommonConstant {
	
	public void perform() throws Exception {
		addOrUpdate();
		listText();
	}
	
	public static String downloadImg(String text){
		Set<String> imgs = ServiceUtils.getMatchStrings(text, HtmlUtils.RegxpForImgTag);
		for(String img : imgs){
			Set<String> srcs = ServiceUtils.getMatchStrings(img, HtmlUtils.RegxpForImaTagSrcAttr);
			if(srcs.size() > 0){
				String src = srcs.iterator().next();
				String url = StringUtils.substringBetween(src, MARK_DOUBLE_QUOTATION, MARK_DOUBLE_QUOTATION);
				if(url.contains(HOST) || !url.startsWith("http"))continue;
				try {
					String replace = WebUtils.download(url);
					text = StringUtils.replace(text, url, replace);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return text;
	}
	
	public static String addOrUpdate() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return null;
		}
		int userId = user.getStkUser().getId().intValue();
		HttpServletRequest request = sc.getRequest();
		String id = request.getParameter(PARAMETER_ID);
		String type = request.getParameter(PARAMETER_TYPE);
		String code = request.getParameter(PARAMETER_CODE);
		String ctype = request.getParameter(PARAMETER_CTYPE);
		String title = request.getParameter(PARAMETER_TITLE);
		String text = request.getParameter(PARAMETER_TEXT);
		String order = request.getParameter(PARAMETER_ORDER);
		Connection conn = sc.getConnection();
		List params = new ArrayList();
		if(text != null && text.length() > 0){
			//img download
			if(user.getStkUser().getId() == 1){
				text = downloadImg(text);
			}
			text = getStkReplace(text, conn);
			if(id == null || id.length() == 0 || NULL.equals(id)){
				long seq = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID);// JdbcUtils.getSequence(conn, "s_text_id");
				params.clear();
				params.add(seq);
				params.add(type);
				params.add(code);
				params.add(ctype);
				params.add(HtmlUtils.removeHTML(title));
				params.add(JdbcUtils.createClob(text));
				params.add(order);
				params.add(userId);
				int ret = JdbcUtils.insert(conn, SQL_INSERT_TEXT, params);
				id = String.valueOf(seq);
				if(ret == 1){
					params.clear();
					params.add(seq);
					params.add(userId);
					StkText stext = JdbcUtils.load(conn, CommonConstant.SQL_SELECT_TEXT_BY_ID, params, StkText.class);
					//IKUtils.addDocument(stext);
					Search search = new Search(userId);
					search.addDocument(stext);
					search.close();
				}
			}else{
				params.clear();
				params.add(title);
				params.add(text);
				params.add(order);
				params.add(id);
				params.add(userId);
				int ret = JdbcUtils.update(conn, "update stk_text set title=?, text=?, disp_order=?, update_time=sysdate where id=? and user_id=?", params);
				if(ret == 1){
					params.clear();
					params.add(id);
					params.add(userId);
					StkText stext = JdbcUtils.load(conn, SQL_SELECT_TEXT_BY_ID, params, StkText.class);
					Search search = new Search(userId);
					search.updateDocument(stext);
					search.close();
					//IKUtils.updateDocument(stext);
				}
			}
			if(NUMBER_ZERO.equals(type)){//只对收藏文章
				String slabels = request.getParameter(PARAMETER_LABEL);
				/*Set<String> labels = StkUtils.getLabels(title);
				labels.addAll(StkUtils.getLabels(text));*/
				List<StkLabel> labels = JsonUtils.getList4Json(slabels, StkLabel.class);
				Label label = new Label(user.getStkUser().getId());
				if(labels.size() > 0){
					label.addLink(labels, Long.parseLong(id));
				}else{
					label.deleteLink(id);
				}
			}
			return id;
		}
		return null;
	}
	
	private static String PATTERN = null;
	private static Map<String,String> PATTERN_MAP = new HashMap<String,String>();
	private static String NOT_MATCH_A = "(?!</a>)";
	private static String A_START_1 = "<a class=\"stk\" target=\"_blank\" href=\"/stk?s=";
	private static String A_START_2 = "\">";
	private static String A_END = "</a>";
	
	public static String getStkReplace(String text, Connection conn){
		if(PATTERN == null){
			List<String> result = new ArrayList<String>();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk where market=1 and cate=1 order by code", Stk.class);
			for(Stk stk : stks){
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				String name = StringUtils.replace(index.getName(), MARK_STAR, MARK_EMPTY);
				result.add(name);
				PATTERN_MAP.put(name, stk.getCode());
				String name2 = StringUtils.replace(name, MARK_BLANK_SPACE, MARK_EMPTY);
				if(!name.equals(name2)) {
					result.add(name2);
					PATTERN_MAP.put(name2, stk.getCode());
				}
			}
			PATTERN = StringUtils.join(result, MARK_VERTICAL);
		}
		if(text == null || text.trim().length()==0)return text;
		StringBuilder sb = new StringBuilder(text);
		Set<String> listName = ServiceUtils.getMatchStrings(text, CommonConstant.MARK_PARENTHESIS_LEFT + PATTERN + CommonConstant.MARK_PARENTHESIS_RIGHT);
		for(String name : listName){
			//String pattern = "(<([^>]*)>|<([^>]*)/>)([^<]*)"+name+"([^((\\[|\\()["+index.getCode()+"]{6}(\\)|\\]))]{8})([^<]*)((<([^>]*)>)|(<([^>]*)/>)|(</([^>]*)>))";
			String pattern = name + NOT_MATCH_A;
			Pattern pat = Pattern.compile(pattern);  
	        Matcher matcher = pat.matcher(sb); 
	        List<Name2Value<String, Name2Value<Integer,Integer>>> pos = new ArrayList<Name2Value<String, Name2Value<Integer,Integer>>>();
	        while (matcher.find())//查找符合pattern的字符串  
	        {  
	        	pos.add(new Name2Value(matcher.group(),new Name2Value(matcher.start(),matcher.end())));
	        }
	        if(pos.size() > 0){
	        	int offset = name.length();
	        	Collections.reverse(pos);
	        	for(Name2Value<String, Name2Value<Integer,Integer>> pair : pos){
	        		String s = pair.getName();
	            	String replace = ServiceUtils.getMatchStringAndReplace(s, name, offset, A_START_1 + PATTERN_MAP.get(name) + A_START_2 + name + A_END);
	            	sb.delete(pair.getValue().getName(), pair.getValue().getValue());
	            	sb.insert(pair.getValue().getName(), replace);
	        	}
	        }
		}
		return sb.toString();
	}
	
	private final static String SQL_DELETE_TEXT_BY_USER_ID = "delete from stk_text where id=? and user_id=?";
	
	public static int delete() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		HttpServletRequest request = sc.getRequest();
		String id = request.getParameter(PARAMETER_ID);
		Connection conn = sc.getConnection();
		List params = new ArrayList();
		if(id != null){
			params.clear();
			params.add(id);
			params.add(user.getStkUser().getId());
			int n = JdbcUtils.delete(conn, SQL_DELETE_TEXT_BY_USER_ID, params);
			if(n == 1){
				StkIKUtils.deleteDocument(id);
				//delete link with label
				Label label = new Label(user.getStkUser().getId());
				label.deleteLink(id);
			}
			return n;
		}
		return 0;
	}
	
	private final static String SQL_SELECT_TEXT_BY_USER_ID = "select id,type,code,code_type,title,text,nvl(update_time,insert_time) update_time,disp_order,sub_type from stk_text where user_id=? and code=? and code_type=? and type!=3 order by nvl(update_time,insert_time) desc";
	private final static String SQL_COUNT_TEXT_BY_USER_ID = "select count(1) from stk_text where user_id=? and code=? and code_type=? and type!=3";
	
	public void listStkText() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return;
		}
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String code = request.getParameter(PARAMETER_CODE);
		String ctype = request.getParameter(PARAMETER_CTYPE);
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		int perPage = 10;//StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		int count = 0;
		List params = new ArrayList();
		params.add(user.getStkUser().getId());
		params.add(code);
		params.add(ctype);
		List<StkText> texts = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_TEXT_BY_USER_ID, (page-1)*perPage, perPage) ,params, StkText.class);
		for(StkText text : texts){//TODO
			if(text.getType()==2){
				text.setTitle((text.getSubType() != 0?"["+StkDict.getDict(StkDict.TEXT_SUB_TYPE, text.getSubType())+"]":"") + text.getTitle());
				text.setText(WebUtils.display(text.getText(), 330));
			}else{
				text.setText(WebUtils.display((text.getSubType() != 0?"["+StkDict.getDict(StkDict.TEXT_SUB_TYPE, text.getSubType())+"]":"") + text.getText(), 330));
			}
		}
		count = JdbcUtils.load(conn, SQL_COUNT_TEXT_BY_USER_ID, params, Integer.class);
		String json = JsonUtils.getJsonString4JavaPOJO(texts, DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		
		sc.setResponse(sb.toString());
	}
	
	public void listText() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return;
		}
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String code = request.getParameter(PARAMETER_CODE);
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		int perPage = 10;//StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		
		List<StkText> texts = new ArrayList<StkText>();
		//查询文章
		Index index = new Index(conn, code);
		List<String> kws = Keyword.listKeywords(conn, code, Keyword.CODETYPE_STK,Keyword.LINKTYPE_MANUAL);
		String keyword = code+MARK_COMMA+index.getNameByTrim()+MARK_COMMA+StringUtils.join(kws, MARK_COMMA);
		
		int start = (page-1)*perPage;
		int end = page * perPage;
		Search search = new Search(user.getStkUser().getId());
		List<Document> longTexts = search.searchRelatedText(keyword,code,true,start,end);
		search.close();
		int count = longTexts.size();
		List<Document> subList = longTexts.subList(start, end > count?count:end);
		for(Document doc : subList){
			Integer id = new Integer(doc.get(DocumentField.ID.value()));
			StkText t = new StkText();
			t.setId(id);
			t.setTitle(doc.get(DocumentField.TITLE.value()));
			t.setText(doc.get(DocumentField.CONTENT.value()));
			t.setUpdateTime(new Timestamp(DateTools.stringToDate(doc.get(DocumentField.TIME.value())).getTime()));
			t.setDispOrder(new Integer(doc.get(DocumentField.ORDER.value())));
			texts.add(t);
		}
		
		for(StkText text : texts){//TODO
			text.setText(WebUtils.display(text.getText(), 330));
		}
		String json = JsonUtils.getJsonString4JavaPOJO(texts, DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		
		sc.setResponse(sb.toString());
	}
	
	public void deleteAndList() throws Exception {
		delete();
		listText();
	}
	
	public final static String SQL_SELECT_TEXT_ALL = "select * from stk_text order by insert_time desc";
	public final static String SQL_COUNT_TEXT_ALL = "select count(1) from stk_text";
	
	public final static String SQL_SELECT_TEXT_BY_SUBTYPE = "select * from stk_text where sub_type=? order by insert_time desc";
	public final static String SQL_COUNT_TEXT_BY_SUBTYPE = "select count(1) from stk_text where sub_type=?";
	
	public void listDocument() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String subtype = request.getParameter("subtype");
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		int perPage = 20;
		int count = 0;
		
		List<StkText> texts = null;
		if("all".equals(subtype)){
			texts = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_TEXT_ALL, (page-1)*perPage, perPage), StkText.class);
			count = JdbcUtils.load(conn, SQL_COUNT_TEXT_ALL, Integer.class);
		}else{
			List params = new ArrayList();
			params.add(subtype);
			texts = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_TEXT_BY_SUBTYPE, (page-1)*perPage, perPage),params, StkText.class);
			count = JdbcUtils.load(conn, SQL_COUNT_TEXT_BY_SUBTYPE,params, Integer.class);
		}
		
		for(StkText text : texts){
			if(text.getCode() != null){
				text.setCode(ServiceUtils.wrapCodeAndNameAsHtml(new Index(conn, text.getCode())));
			}else{
				text.setCode(StkDict.getDict(StkDict.TEXT_SUB_TYPE, text.getSubType()));
			}
			text.setText(WebUtils.display(text.getText(),80,false,false));
		}
		
		String json = JsonUtils.getJsonString4JavaPOJO(texts, CommonConstant.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		
		sc.setResponse(sb.toString());
	}
	
	public final static String SQL_SELECT_TEXT_ALL_BY_KEYWORD = "select * from stk_text where insert_time between ? and ? and (title like ? or text like ?) order by insert_time desc";
	public final static String SQL_COUNT_TEXT_ALL_BY_KEYWORD = "select count(1) from stk_text where insert_time between ? and ? and (title like ? or text like ?)";
	
	public final static String SQL_SELECT_TEXT_BY_SUBTYPE_BY_KEYWORD = "select * from stk_text where sub_type=? and insert_time between ? and ? and (title like ? or text like ?) order by insert_time desc";
	public final static String SQL_COUNT_TEXT_BY_SUBTYPE_BY_KEYWORD = "select count(1) from stk_text where sub_type=? and insert_time between ? and ? and (title like ? or text like ?)";
	
	public void listDocumentByKeyword() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String subtype = request.getParameter("subtype");
		String keyword = request.getParameter("keyword");
		String fromTime = request.getParameter("from");
		String toTime = request.getParameter("to");
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		int perPage = 20;
		int count = 0;
		
		List<StkText> texts = null;
		if("all".equals(subtype)){
			List params = new ArrayList();
			params.add(new Timestamp(ServiceUtils.sf_ymd14.parse(fromTime).getTime()));
			params.add(new Timestamp(ServiceUtils.sf_ymd14.parse(toTime).getTime()));
			params.add("%"+keyword+"%");
			params.add("%"+keyword+"%");
			texts = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_TEXT_ALL_BY_KEYWORD, (page-1)*perPage, perPage),params, StkText.class);
			count = JdbcUtils.load(conn, SQL_COUNT_TEXT_ALL_BY_KEYWORD,params, Integer.class);
		}else{
			List params = new ArrayList();
			params.add(subtype);
			params.add(new Timestamp(ServiceUtils.sf_ymd14.parse(fromTime).getTime()));
			params.add(new Timestamp(ServiceUtils.sf_ymd14.parse(toTime).getTime()));
			params.add("%"+keyword+"%");
			params.add("%"+keyword+"%");
			texts = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_TEXT_BY_SUBTYPE_BY_KEYWORD, (page-1)*perPage, perPage),params, StkText.class);
			count = JdbcUtils.load(conn, SQL_COUNT_TEXT_BY_SUBTYPE_BY_KEYWORD,params, Integer.class);
		}
		
		for(StkText text : texts){
			if(text.getCode() != null){
				text.setCode(ServiceUtils.wrapCodeAndNameAsHtml(new Index(conn, text.getCode())));
			}else{
				text.setCode(StkDict.getDict(StkDict.TEXT_SUB_TYPE, text.getSubType()));
			}
			text.setText(WebUtils.display(text.getText(),80,false,false));
		}
		
		String json = JsonUtils.getJsonString4JavaPOJO(texts, CommonConstant.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		
		sc.setResponse(sb.toString());
	}
}
