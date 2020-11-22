package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.stk123.model.bo.StkImportInfo;
import com.stk123.model.Index;
import com.stk123.model.News;
import com.stk123.util.ServiceUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.web.core.util.RequestUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.context.StkContext;

public class NewsAction {
	
	public final static String SQL_SELECT_IMPORT_INFO = "select * from stk_import_info where type>=100 and type not in (190) order by info_create_time desc,insert_time desc";
	public final static String SQL_COUNT_IMPORT_INFO = "select count(1) from stk_import_info where type>=100 and type not in (190) order by info_create_time desc,insert_time desc";

	public final static String SQL_SELECT_IMPORT_INFO_BY_TYPE = "select * from stk_import_info where type=? order by info_create_time desc,insert_time desc";
	public final static String SQL_COUNT_IMPORT_INFO_BY_TYPE = "select count(1) from stk_import_info where type=? order by info_create_time desc,insert_time desc";
	
	public final static String SQL_SELECT_IMPORT_INFO_BY_CODE_TYPE = "select * from stk_import_info where code=? and type=? order by info_create_time desc,insert_time desc";
	public final static String SQL_COUNT_IMPORT_INFO_BY_CODE_TYPE = "select count(1) from stk_import_info where code=? and type=? order by info_create_time desc,insert_time desc";
	
	public final static String SQL_SELECT_IMPORT_INFO_BY_CODE = "select * from stk_import_info where code=? and type>=100 order by info_create_time desc,insert_time desc";
	public final static String SQL_COUNT_IMPORT_INFO_BY_CODE = "select count(1) from stk_import_info where code=? and type>=100 order by info_create_time desc,insert_time desc";

	public void listNews() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String code = request.getParameter(CommonConstant.PARAMETER_CODE);
		String type = request.getParameter("type");
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		int perPage = 20;//StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		int count = 0;
		List<StkImportInfo> infos = null;
		List params = new ArrayList();
		if(!"all".equals(type) && "undefined".equals(code)){
			params.add(type);
			infos = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_IMPORT_INFO_BY_TYPE, (page-1)*perPage, perPage), params, StkImportInfo.class);
			count = JdbcUtils.load(conn, SQL_COUNT_IMPORT_INFO_BY_TYPE, params, Integer.class);
		}else if(!"all".equals(type) && !"undefined".equals(code)){
			params.add(code);
			params.add(type);
			infos = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_IMPORT_INFO_BY_CODE_TYPE, (page-1)*perPage, perPage), params, StkImportInfo.class);
			count = JdbcUtils.load(conn, SQL_COUNT_IMPORT_INFO_BY_CODE_TYPE, params, Integer.class);
		}else if("all".equals(type) && !"undefined".equals(code)){
			params.add(code);
			infos = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_IMPORT_INFO_BY_CODE, (page-1)*perPage, perPage), params, StkImportInfo.class);
			count = JdbcUtils.load(conn, SQL_COUNT_IMPORT_INFO_BY_CODE, params, Integer.class);
		}else{
			infos = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_IMPORT_INFO, (page-1)*perPage, perPage), StkImportInfo.class);
			count = 500;//JdbcUtils.load(conn, SQL_COUNT_IMPORT_INFO, Integer.class);
		}
		for(StkImportInfo info : infos){
			StkImportInfo prevInfo = this.getPreviousInfo(conn, info);
			int days = 0;
			if(prevInfo != null){
				days = ServiceUtils.getDaysBetween(info.getInfoCreateTime(), prevInfo.getInfoCreateTime());
			}
			Index index = new Index(conn, info.getCode());
			String infoType = News.getType(info.getType()).getName();
			String title = ServiceUtils.wrapCodeAndNameAsHtml(index) +
					" <a target='_blank' class='r-menu-care' code='"+info.getCode()+"' name='"+index.getName()+"' type='"+infoType+"' createtime='"+ServiceUtils.formatDate(info.getInfoCreateTime(),ServiceUtils.sf_ymd)+"' href='"+info.getUrlTarget()+"'>"+info.getTitle()+"</a>";
			info.setTitle(title + (days>90?" <img src='/images/icon_new.png' width='24px' height='12px'>":""));
			info.setInfo(infoType);
			info.setUrlSource(null);
		}
		
		String json = JsonUtils.getJsonString4JavaPOJO(infos, CommonConstant.DATE_FORMAT_YYYY_MM_DD);
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		
		sc.setResponse(sb.toString());
	}
	
	private StkImportInfo getPreviousInfo(Connection conn, StkImportInfo info) {
		List params = new ArrayList();
		params.add(info.getCode());
		params.add(info.getId());
		return JdbcUtils.load(conn, "select * from (select * from stk_import_info where code=? and type>=100 and id<? order by info_create_time desc) where rownum=1", params, StkImportInfo.class);
	}
	
	public final static String SQL_SELECT_IMPORT_INFO_MSG = "select * from stk_import_info where type<100 order by insert_time desc";
	public final static String SQL_COUNT_IMPORT_INFO_MSG = "select count(1) from stk_import_info where type<100 order by insert_time desc";
	public final static String SQL_SELECT_IMPORT_INFO_BY_TYPE2 = "select * from stk_import_info where type=? order by insert_time desc";
	public final static String SQL_COUNT_IMPORT_INFO_BY_TYPE2 = "select count(1) from stk_import_info where type=? order by insert_time desc";

	
	public void listMsg() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = sc.getConnection();
		String type = request.getParameter("type");
		int page = RequestUtils.getInt(request, CommonConstant.PARAMETER_PAGE);
		
		int perPage = 20;//StkConstant.SYS_ARTICLE_LIST_PER_PAGE;
		int count = 0;
		List<StkImportInfo> infos = null;
		List params = new ArrayList();
		if("all".equals(type)){
			infos = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_IMPORT_INFO_MSG, (page-1)*perPage, perPage), StkImportInfo.class);
			count = JdbcUtils.load(conn, SQL_COUNT_IMPORT_INFO_MSG, Integer.class);
		}else{
			params.add(type);
			infos = JdbcUtils.list(conn,JdbcUtils.DIALECT.getLimitedString(SQL_SELECT_IMPORT_INFO_BY_TYPE2, (page-1)*perPage, perPage),params, StkImportInfo.class);
			count = JdbcUtils.load(conn, SQL_COUNT_IMPORT_INFO_BY_TYPE2,params, Integer.class);
		}
		
		for(StkImportInfo info : infos){
			String title = ServiceUtils.wrapCodeAndNameAsHtml(new Index(conn, info.getCode()));
			info.setTitle(title);
		}
		
		String json = JsonUtils.getJsonString4JavaPOJO(infos, CommonConstant.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
		
		StringBuffer sb = new StringBuffer(CommonConstant.MARK_BRACE_LEFT);
		sb.append("\"count\":").append(count).append(CommonConstant.MARK_COMMA);
		sb.append("\"perpage\":").append(perPage).append(CommonConstant.MARK_COMMA);
		sb.append("\"data\":").append(json);
		sb.append(CommonConstant.MARK_BRACE_RIGHT);
		
		sc.setResponse(sb.toString());
	}
}
