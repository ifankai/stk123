package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.stk123.model.bo.StkReportDaily;
import com.stk123.model.Index;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.CommonConstant;
import com.stk123.web.WebUtils;
import com.stk123.web.context.StkContext;


public class DailyReportAction {
	
	public String newHigh() throws Exception {
		StkContext sc = StkContext.getContext();
		//WebUtils.setCareStk(sc);
		HttpServletRequest request = sc.getRequest();
		String date = request.getParameter(CommonConstant.PARAMETER_DATE);
		String type = request.getParameter(CommonConstant.PARAMETER_TYPE);
		String sort = request.getParameter("sort");
		String order = request.getParameter(CommonConstant.PARAMETER_ORDER);
		if(date == null || date.length() == 0){
			date = WebUtils.REPORT_DATE;
		}
		Connection conn = sc.getConnection();
		List<Index> indexs = new ArrayList<Index>();
		List params = new ArrayList();
		params.add(type);
		params.add(date);
		List<StkReportDaily> list = null;
		if(sort != null){
			list = JdbcUtils.list(conn, "select * from stk_report_daily where type = ? and report_date=? order by to_number("+sort+") "+order, params, StkReportDaily.class);
		}else{
			list = JdbcUtils.list(conn, "select * from stk_report_daily where type = ? and report_date=?", params, StkReportDaily.class);
		}
		for(StkReportDaily rp : list){
			Index index = new Index(conn,rp.getCode());
			indexs.add(index);
		}
		sc.put("stk_reports", indexs);
		return CommonConstant.ACTION_SUCC;
	}
}
