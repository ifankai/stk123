package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stk123.bo.StkKeyword;
import com.stk123.model.Index;
import com.stk123.model.Keyword;
import com.stk123.tool.ik.StkSearch;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.StkConstant;
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
		String code = request.getParameter(StkConstant.PARAMETER_KWCODE);
		String type = request.getParameter(StkConstant.PARAMETER_KWTYPE);
		String kw = request.getParameter(StkConstant.PARAMETER_K);
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
			return StkConstant.NUMBER_ONE;
		}
		
	}
	
	public void stopKeyword() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String reqKw = request.getParameter(StkConstant.PARAMETER_K).trim();
		if(reqKw != null && reqKw.length() > 0){
			String[] kws = reqKw.split(StkConstant.MARK_COMMA);
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
		sc.setResponse(StkConstant.NUMBER_ONE);
	}
	
	public void listMainBusiness() throws Exception{
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter(StkConstant.PARAMETER_CODE);
		
		Index index = new Index(conn, code);
		List<Map> list = index.getMainBusiness();
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		sc.setResponse(json);
	}
	
	public void deleteKeyword() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String kid = request.getParameter(StkConstant.PARAMETER_ID);
		Keyword kw  = new Keyword(conn);
		int result = kw.delete(kid);
		sc.setResponse(result);
	}
	
}
