package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.stk123.bo.StkLabel;
import com.stk123.bo.StkLabelText;
import com.stk123.bo.StkText;
import com.stk123.model.Label;
import com.stk123.model.User;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.web.core.util.RequestUtils;
import com.stk123.StkConstant;
import com.stk123.web.context.StkContext;

public class ArticleAction {
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		HttpServletRequest request = sc.getRequest();
		String id = request.getParameter(StkConstant.PARAMETER_ID);
		if(StringUtils.isNotEmpty(id)){
			StkText st = selectArticle(id, user.getStkUser().getId());
			if(st != null){
				sc.put(StkConstant.PARAMETER_ID, st.getId());
			}
		}
		Label label = new Label(user.getStkUser().getId());
		sc.put(StkConstant.ATTRIBUTE_LABELS, label.getLabels());
		return StkConstant.ACTION_SUCC;
	}
	
	public void selectLabel() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		HttpServletRequest request = sc.getRequest();
		Connection conn = StkContext.getConnection();
		String labelId = request.getParameter(StkConstant.PARAMETER_LABEL_ID);
		int page = RequestUtils.getInt(request, StkConstant.PARAMETER_PAGE);
		int perPage = StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		int count = 0;
		List<Map> list = null;
		List params = new ArrayList();
		if(StkConstant.NUMBER_ZERO.equals(labelId)){
			params.add(0);
			params.add(user.getStkUser().getId());
			String sql = JdbcUtils.DIALECT.getLimitedString(StkConstant.SQL_SELECT_TITLE_TEXT_BY_TYPE_ORDERBY_ORDER, (page-1)*perPage, perPage);
			list = JdbcUtils.list2UpperKeyMap(conn, sql, params);
			count = JdbcUtils.load(conn, StkConstant.SQL_COUNT_TITLE_TEXT_BY_TYPE_ORDERBY_ORDER, params, Integer.class);
		}else{
			params.add(labelId);
			params.add(user.getStkUser().getId());
			String sql = JdbcUtils.DIALECT.getLimitedString(StkConstant.SQL_SELECT_TITLE_TEXT_BY_LABEL_ID_ORDERBY_ORDER, (page-1)*perPage, perPage);
			list = JdbcUtils.list2UpperKeyMap(conn, sql, params);
			count = JdbcUtils.load(conn, StkConstant.SQL_COUNT_TITLE_TEXT_BY_LABEL_ID_ORDERBY_ORDER, params, Integer.class);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		StringBuffer sb = new StringBuffer(StkConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(StkConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(StkConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(StkConstant.MARK_BRACE_RIGHT);
		sc.setResponse(sb.toString());
	}
	
	public void addOrUpdate() throws Exception {
		StkContext sc = StkContext.getContext();
		String id = TextAction.addOrUpdate();
		sc.setResponse(id);
	}
	
	public void select() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		int userId = user.getStkUser().getId();
		HttpServletRequest request = sc.getRequest();
		String id = request.getParameter(StkConstant.PARAMETER_ID);
		StkText st =  this.selectArticle(id, userId);
		List<StkLabelText> lts = this.listArticleLabels(Integer.parseInt(id), userId);
		List<Map> labels = new ArrayList<Map>();
		for(StkLabelText lt : lts){
			Map label = new HashMap();
			label.put(StkConstant.JSON_ID, lt.getLabelId());
			label.put(StkConstant.JSON_VALUE, lt.getStkLabel().getName());
			labels.add(label);
		}
		StringBuffer sb = new StringBuffer(StkConstant.MARK_BRACE_LEFT);
		sb.append("\"label\":");
		sb.append(JsonUtils.getJsonString4JavaPOJO(labels));
		sb.append(",\"text\":");
		sb.append(JsonUtils.getJsonString4JavaPOJO(st, StkConstant.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS ));
		sb.append(StkConstant.MARK_BRACE_RIGHT);
		sc.setResponse(sb.toString());
	}
	
	public void delete() throws Exception {
		StkContext sc = StkContext.getContext();
		int n = TextAction.delete();
		sc.setResponse(n);
	}
	
	public StkText selectArticle(String id, int userId) throws Exception {
		Connection conn = StkContext.getConnection();
		if(id != null && id.length() > 0){
			List params = new ArrayList();
			params.add(id);
			params.add(userId);
			StkText st = JdbcUtils.load(conn, StkConstant.SQL_SELECT_TEXT_BY_ID, params, StkText.class);
			return st;
		}
		return null;
	}
	
	public List<StkLabelText> listArticleLabels(int textId, int userId) throws Exception {
		Label label = new Label(userId);
		List<StkLabelText> list = label.getLabelTexts(textId);
		for(StkLabelText lt : list){
			StkLabel stkLabel = label.load(lt.getLabelId());
			lt.setStkLabel(stkLabel);
		}
		return list;
	}
	
	public void deleteLabel() throws Exception {
		StkContext sc = StkContext.getContext();
		User user = sc.getUser();
		if(user == null){
			return;
		}
		HttpServletRequest request = sc.getRequest();
		int labelId = RequestUtils.getInt(request,StkConstant.PARAMETER_LABEL_ID);
		Label label = new Label(user.getStkUser().getId(), labelId);
		int n = label.delete();
		sc.setResponse(n);
	}
}
