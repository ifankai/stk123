package com.stk123.model;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stk123.bo.StkKeyword;
import com.stk123.bo.StkKeywordLink;
import com.stk123.tool.db.util.sequence.SequenceUtils;
import com.stk123.tool.util.JdbcUtils;


public class Keyword implements Serializable {
	
	public static final int CODETYPE_STK = 1;
	public static final int CODETYPE_INDUSTRY = 2;
	
	public static final int LINKTYPE_MANUAL = 0;
	public static final int LINKTYPE_MAIN_BUSINESS = 1;
	
	private Connection conn;
	private String value;
	private StkKeyword keyword = null;
	private List<StkKeywordLink> links = new ArrayList<StkKeywordLink>();
	
	public final static int DELETED = -1;
	
	public Keyword(Connection conn){
		this.conn = conn;
	}
	
	public Keyword(Connection conn, String value){
		this.conn = conn;
		this.value = value;
	}
	
	public final static String SQL_SELECT_KEYWORD_BY_NAME = "select * from stk_keyword where name=?";
	
	public StkKeyword load(String name){
		List params = new ArrayList();
		params.add(name);
		keyword = JdbcUtils.load(conn, SQL_SELECT_KEYWORD_BY_NAME, params, StkKeyword.class);
		return keyword;
	}
	
	//private final static String SQL_INSERT_KEYWORD = "insert into stk_keyword(id,name,insert_time,status) values(?,?,sysdate(),?)";
	private final static String SQL_INSERT_KEYWORD = "insert into stk_keyword(id,name,insert_time,status) values(?,?,sysdate,?)";
	public void add(String keyword, int status){
		List params = new ArrayList();
		long seq = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_KEYWORD_ID);
		params.add(seq);
		params.add(keyword);
		params.add(status);
		JdbcUtils.insert(conn, SQL_INSERT_KEYWORD, params);
	}
	
	private final static String SQL_DELETE_KEYWORD_BY_ID = "update stk_keyword set status=-1 where id=?";
	public int delete(String id){
		List params = new ArrayList();
		params.add(id);
		return JdbcUtils.update(conn, SQL_DELETE_KEYWORD_BY_ID,params);
	}
	
	public final static String SQL_SELECT_KEYWORD_BY_ID = "select * from stk_keyword where id=?";
	
	public int addLink(String code,int codeType, int linkType){
		if(value == null){
			throw new RuntimeException("keyword must be not null.");
		}
		List params = new ArrayList();
		params.add(value);
		StkKeyword keyword = JdbcUtils.load(conn, SQL_SELECT_KEYWORD_BY_NAME, params, StkKeyword.class);
		if(keyword == null){
			long seq = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_KEYWORD_ID);//JdbcUtils.getSequence(conn, "s_keyword_id");
			params.clear();
			params.add(seq);
			params.add(value);
			params.add(1);
			int n = JdbcUtils.insert(conn, SQL_INSERT_KEYWORD, params);
			if(n == 1){
				params.clear();
				params.add(seq);
				keyword = JdbcUtils.load(conn, SQL_SELECT_KEYWORD_BY_ID, params, StkKeyword.class);
			}
		}
		//status=-1 表示已经删除
		if(keyword.getStatus().intValue() == -1){
			return -1;
		}
		params.clear();
		params.add(linkType);
		params.add(code);
		params.add(codeType);
		params.add(keyword.getId());
		StkKeywordLink skl = JdbcUtils.load(conn, "select * from stk_keyword_link where link_type=? and code=? and code_type=? and keyword_id=?",params, StkKeywordLink.class);
		if(skl == null && keyword != null && code != null){
			long seq = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_KEYWORD_LINK_ID);//JdbcUtils.getSequence(conn, "s_keyword_link_id");
			params.clear();
			params.add(seq);
			params.add(code);
			params.add(codeType);
			params.add(keyword.getId());
			params.add(linkType);
			//JdbcUtils.insert(conn, "insert into stk_keyword_link(id,code,code_type,keyword_id,link_type,insert_time) values(?,?,?,?,?,sysdate())", params);
			JdbcUtils.insert(conn, "insert into stk_keyword_link(id,code,code_type,keyword_id,link_type,insert_time) values(?,?,?,?,?,sysdate)", params);
			return 1;
		}
		return 0;
	}
	
	public static List<String> listKeywords(Connection conn, String code, int codeType){
		return Keyword.listKeywords(conn, code, codeType, -1);
	}
	public static List<String> listKeywords(Connection conn, String code, int codeType, int linkType){
		List params = new ArrayList();
		params.clear();
		params.add(code);
		params.add(codeType);
		List<Map> list = null;
		if(linkType == -1){
			list = JdbcUtils.list2UpperKeyMap(conn, "select a.keyword_id keywordid,a.id linkid,b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.keyword_id=b.id and a.code=? and a.code_type=?", params);
		}else{
			params.add(linkType);
			list = JdbcUtils.list2UpperKeyMap(conn, "select a.keyword_id keywordid,a.id linkid,b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.keyword_id=b.id and a.code=? and a.code_type=? and a.link_type=?", params);
		}
		List<String> kws = new ArrayList<String>();
		for(Map m : list){
			kws.add(String.valueOf(m.get("name")));
		}
		return kws;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

