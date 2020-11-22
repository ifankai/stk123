package com.stk123.web.action;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.stk123.model.bo.StkInvestigation;
import com.stk123.model.Index;
import com.stk123.util.ServiceUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.web.core.util.RequestUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;

public class InvestAction {

	public void listInvest() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String code = request.getParameter(CommonConstant.PARAMETER_CODE);
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		int perPage = 5;//StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		int count = 0;
		List<StkInvestigation> invests = null;
		
		if("undefined".equals(code)){
			perPage = 15;
			String from = request.getParameter("from");
			String keyword = request.getParameter("keyword");
			if(from != null){
				String to = request.getParameter("to");
				List params = new ArrayList();
				params.add(new Timestamp(ServiceUtils.sf_ymd14.parse(from).getTime()));
				params.add(new Timestamp(ServiceUtils.sf_ymd14.parse(to).getTime()));
				invests = JdbcUtils.list(conn, JdbcUtils.DIALECT.getLimitedString("select id,code,title,investigator,investigator_count,text,text_count,invest_date,source_url from stk_investigation where investigator_count > 0 and invest_date between ? and ? order by investigator_count desc", (page-1)*perPage, perPage), params, StkInvestigation.class);
				count = JdbcUtils.load(conn, "select count(1) from stk_investigation where investigator_count > 0 and invest_date between ? and ? order by investigator_count desc", params, Integer.class);
			}else if(keyword!=null){
				List params = new ArrayList();
				params.add("%"+keyword+"%");
				invests = JdbcUtils.list(conn, JdbcUtils.DIALECT.getLimitedString("select id,code,title,investigator,investigator_count,text,text_count,invest_date,source_url from stk_investigation where investigator_count > 0 and text like ? order by investigator_count desc", (page-1)*perPage, perPage), params, StkInvestigation.class);
				count = JdbcUtils.load(conn, "select count(1) from stk_investigation where investigator_count > 0 and text like ? order by investigator_count desc", params, Integer.class);
			}else{
				invests = JdbcUtils.list(conn, JdbcUtils.DIALECT.getLimitedString("select id,code,title,investigator,investigator_count,text,text_count,invest_date,source_url from stk_investigation order by invest_date desc", (page-1)*perPage, perPage), StkInvestigation.class);
				count = JdbcUtils.load(conn, "select count(1) from stk_investigation", Integer.class);
			}
			for(StkInvestigation invest : invests){
				invest.setSourceUrl("/stock/"+invest.getCode()+"/invest/"+StringUtils.substringAfterLast(invest.getSourceUrl(), "/"));
				invest.setCode(ServiceUtils.wrapCodeAndNameAsHtml(new Index(conn, invest.getCode())));
				invest.setText(WebUtils.display(StringUtils.replace(StringUtils.replace(invest.getText()+"<br/><br/>"+invest.getInvestigator(), "\n", "<br>"),"\r","<br>"), 260, false));
			}
		}else{
			List params = new ArrayList();
			params.add(code);
			invests = JdbcUtils.list(conn, JdbcUtils.DIALECT.getLimitedString("select id,code,title,investigator,investigator_count,text,text_count,invest_date,source_url from stk_investigation where code=? order by invest_date desc", (page-1)*perPage, perPage), params, StkInvestigation.class);
			for(StkInvestigation invest : invests){
				invest.setSourceUrl("/stock/"+invest.getCode()+"/invest/"+StringUtils.substringAfterLast(invest.getSourceUrl(), "/"));
				invest.setCode(ServiceUtils.wrapCodeAndNameAsHtml(new Index(conn, code)));
				invest.setText(WebUtils.display(StringUtils.replace(StringUtils.replace(invest.getText()+"<br/><br/>"+invest.getInvestigator(), "\n", "<br>"),"\r","<br>"), 260, false));
			}
			count = JdbcUtils.load(conn, "select count(1) from stk_investigation where code=?", params, Integer.class);
		}
		
		String json = JsonUtils.getJsonString4JavaPOJO(invests, CommonConstant.DATE_FORMAT_YYYY_MM_DD);
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		sc.setResponse(sb);
	}
}
