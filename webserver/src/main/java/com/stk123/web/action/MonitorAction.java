package com.stk123.web.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stk123.model.bo.StkMonitor;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.web.context.StkContext;
import com.stk123.web.monitor.Monitor;


public class MonitorAction {
	
	public void create() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String type = request.getParameter("type");
		String code = request.getParameter("code");
		String json = request.getParameter("json");
		//System.out.println("json==="+json);
		Map map = (Map)JsonUtils.getObject4Json(json, Map.class, null);
		//System.out.println(map);
		Connection conn = sc.getConnection();
		List params = new ArrayList();
		params.add(code);
		params.add(type);
		for(int i=1;i<=5;i++){
			params.add(map.get("param"+i));
		}
		//JdbcUtils.insert(conn, "insert into stk_monitor (id,code,type,status,insert_date,param_1,param_2,param_3,param_4,param_5) select s_monitor_id.nextval,?,?,1,sysdate(),?,?,?,?,? from dual", params);
		JdbcUtils.insert(conn, "insert into stk_monitor (id,code,type,status,insert_date,param_1,param_2,param_3,param_4,param_5) select s_monitor_id.nextval,?,?,1,sysdate,?,?,?,?,? from dual", params);
		list();
	}
	
	public void list() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String code = request.getParameter("code");
		List params = new ArrayList();
		params.add(code);
		List<StkMonitor> list = JdbcUtils.list(conn, "select * from stk_monitor where type=1 and code=? order by insert_date asc", params, StkMonitor.class);
		List results = new ArrayList();
		for(StkMonitor sm : list){
			Map map = new HashMap();
			map.put("id", sm.getId());
			map.put("text", Monitor.getInstance(sm).translate(sm, conn));
			results.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(results);
		//System.out.println(json);
		sc.setResponse(json);
	}
	
	public void delete() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = sc.getConnection();
		HttpServletRequest request = sc.getRequest();
		String id = request.getParameter("id");
		List params = new ArrayList();
		params.add(id);
		JdbcUtils.delete(conn, "delete from stk_monitor where id=?", params);
		list();
	}
	
}
