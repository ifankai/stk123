package com.stk123.json;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.stk123.task.StkUtils;
import com.stk123.tool.util.JdbcUtils;


public class SQL {
	
	private String sql;
	private List<Column> params;
	private String clazz;
	
	public int updateOrInsert(Connection conn) throws Exception{
		return JdbcUtils.insertOrUpdate(conn, sql, convert(params));
	}
	
	public List<Object> select(Connection conn) throws Exception {
		return JdbcUtils.list(conn, sql, convert(params), Class.forName(clazz));
	}
	
	private List<Object> convert(List<Column> cols) throws ParseException{
		List<Object> params = new ArrayList<Object>();
		for(Column col : cols){
			if(col.getType() == null){
				params.add(col.getValue());
			}else if("date".equals(col.getType())){
				params.add(StkUtils.sf_ymd2.parse(col.getValue()));
			}else if("clob".equals(col.getType())){
				params.add(JdbcUtils.createClob(col.getValue()));
			}else{
				params.add(col.getValue());
			}
		}
		return params;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public List<Column> getParams() {
		return params;
	}
	public void setParams(List<Column> params) {
		this.params = params;
	}
	
	
	
}
