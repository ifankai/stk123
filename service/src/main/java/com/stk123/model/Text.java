package com.stk123.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.stk123.service.ServiceUtils;
import com.stk123.common.db.util.sequence.SequenceUtils;
import com.stk123.common.util.JdbcUtils;

public class Text {
	
	public final static int SUB_TYPE_EARNING_FORECAST = 10;
	public final static int SUB_TYPE_ORG_BUY_WITHIN_60 = 20;
	public final static int SUB_TYPE_NIU_FUND_ONE_YEAR = 30;
	public final static int SUB_TYPE_NIU_FUND_ALL_TIME = 31;
	public final static int SUB_TYPE_FIND_REVERSION = 40;
	public final static int SUB_TYPE_FIND_GROWTH = 45;
	public final static int SUB_TYPE_STK_HOLDER_REDUCE = 50;
	public final static int SUB_TYPE_COMPANY_RESEARCH = 100; //公司调研
	public final static int SUB_TYPE_INDUSTRY_RESEARCH = 110; //行业分析
	public final static int SUB_TYPE_STK_REPORT = 200; //年报季报

	public static void insert(Connection conn, String code,String content) throws Exception{
		List params = new ArrayList();
		long id = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID);
		params.add(id);
		params.add(code);
		params.add(JdbcUtils.createClob(content));
		JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time) values (?,1,?,1,null,?,sysdate,null)", params);
	}
	
	public static void insert(Connection conn, String code,String content, int subType) throws Exception{
		Text.insert(conn, 1, code,null, content, subType);
	}
	
	public static void insert(Connection conn,int type, String code,String title, String content, int subType) throws Exception{
		List params = new ArrayList();
		long id = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID);
		params.add(id);
		params.add(type);
		params.add(code);
		params.add(title);
		params.add(JdbcUtils.createClob(content));
		params.add(subType);
		JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) values (?,?,?,1,?,?,sysdate,null,?)", params);
	}
	
	public static long insert(Connection conn,int type, String code,String title, String content, int subType, Date insertTime) throws Exception{
		List params = new ArrayList();
		long id = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID);
		params.add(id);
		params.add(type);
		params.add(code);
		params.add(title);
		params.add(JdbcUtils.createClob(conn, content));
		params.add(insertTime);
		params.add(subType);
		params.add(code);
		params.add(subType);
		params.add(title);
		int ret = JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) select ?,?,?,1,?,?,?,null,? from dual where not exists (select 1 from stk_text where code=? and sub_type=? and title=?)", params);
		if(ret >= 1){
			/*params.clear();
			params.add(id);
			params.add(StkConstant.DEFAULT_USER_ID);
			StkText stext = JdbcUtils.load(conn, StkConstant.SQL_SELECT_TEXT_BY_ID, params, StkText.class);
			Search search = new Search(StkConstant.DEFAULT_USER_ID);
			search.addDocument(stext);
			search.close();*/
			return id;
		}
		return ret;
	}
	
	public static long insert(Connection conn,int type, String code,String title, String content, int subType, String insertTime) throws Exception{
		return Text.insert(conn, type, code, title, content, subType, ServiceUtils.sf_ymd9.parse(insertTime));
	}
	
	public static long insert(Connection conn,int type, String title, String content, int subType, String insertTime) throws Exception{
		List params = new ArrayList();
		long id = SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID);
		params.add(id);
		params.add(type);
		params.add(title);
		params.add(JdbcUtils.createClob(conn, content));
		params.add(ServiceUtils.sf_ymd9.parse(insertTime));
		params.add(subType);
		params.add(subType);
		params.add(title);
		int ret = JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) select ?,?,null,1,?,?,?,null,? from dual where not exists (select 1 from stk_text where sub_type=? and title=?)", params);
		if(ret >= 1){
			/*params.clear();
			params.add(id);
			params.add(StkConstant.DEFAULT_USER_ID);
			StkText stext = JdbcUtils.load(conn, StkConstant.SQL_SELECT_TEXT_BY_ID, params, StkText.class);
			Search search = new Search(StkConstant.DEFAULT_USER_ID);
			search.addDocument(stext);
			search.close();*/
			return id;
		}
		return ret;
	}

	public static Integer countByTitle(Connection conn, String title, int days){
		return JdbcUtils.load(conn, "select count(1) from stk_text where title=? and insert_time>(sysdate-?)", Integer.class, title, days);
	}
	
}
