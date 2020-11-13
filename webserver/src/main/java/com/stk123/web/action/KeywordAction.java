package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stk123.model.bo.StkKeyword;
import com.stk123.model.Index;
import com.stk123.model.Keyword;
import com.stk123.web.ik.StkSearch;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.context.StkContext;


public class KeywordAction {
	
	public String perform() throws Exception{
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		
		String json = addOrListToStk(sc);
		sc.setResponse(json);
		return null;
	}
	
	private final static String SQL_SELECT_KEYWORD_BY_CODE = "select a.keyword_id keywordid,a.id linkid,b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.link_type=0 and a.keyword_id=b.id and a.code=? and a.code_type=?";
	public String addOrListToStk(StkContext sc) throws Exception{
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(CommonConstant.PARAMETER_KWCODE);
		String type = request.getParameter(CommonConstant.PARAMETER_KWTYPE);
		String kw = request.getParameter(CommonConstant.PARAMETER_K);
		List params = new ArrayList();
		if(kw != null && kw.length() > 0){
			Keyword keyword = new Keyword(conn,kw); 
			keyword.addLink(code, Integer.parseInt(type), 0);
			StkSearch.clear();
		}
		if(code != null){
			params.clear();
			params.add(code);
			params.add(type);
			List<Map> list = JdbcUtils.list2UpperKeyMap(conn, SQL_SELECT_KEYWORD_BY_CODE, params);
			String json = JsonUtils.getJsonString4JavaPOJO(list);
			return json;
		}else{
			return CommonConstant.NUMBER_ONE;
		}
		
	}
	
	public void stopKeyword() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String reqKw = request.getParameter(CommonConstant.PARAMETER_K).trim();
		if(reqKw != null && reqKw.length() > 0){
			String[] kws = reqKw.split(CommonConstant.MARK_COMMA);
			for(String kw : kws){
				kw = kw.trim();
				if(kw != null && kw.length() > 0){
					Keyword keyword = new Keyword(conn);
					StkKeyword sk = keyword.load(kw);
					if(sk == null){
						keyword.add(kw,Keyword.DELETED);
					}else{
						keyword.delete(String.valueOf(sk.getId()));
					}
				}
			}
			StkSearch.clear();
		}
		sc.setResponse(CommonConstant.NUMBER_ONE);
	}
	
	public void listMainBusiness() throws Exception{
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(CommonConstant.PARAMETER_CODE);
		
		Index index = new Index(conn, code);
		List<Map> list = index.getMainBusiness();
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse(json);
	}
	
	public void deleteKeyword() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String kid = request.getParameter(CommonConstant.PARAMETER_ID);
		Keyword kw  = new Keyword(conn);
		int result = kw.delete(kid);
		sc.setResponse(result);
	}
	
}
