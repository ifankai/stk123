package com.stk123.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stk123.bo.StkFnType;
import com.stk123.bo.StkIndustryType;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.util.CacheUtils;
import com.stk123.tool.util.JdbcUtils;

public class Fn {
	
	//private static Map<String,StkFnType> fnTypes = new HashMap<String,StkFnType>();
	public final static String SQL_SELECT_FN_TYPE_ALL = "select * from stk_fn_type";
	
	public static Map<String,StkFnType> getFnTypes() throws Exception{
		Map<String, StkFnType> fnTypes = (Map<String, StkFnType>)CacheUtils.getForever(CacheUtils.KEY_FN_TYPE);
		if(fnTypes != null){
			return fnTypes;
		}
		fnTypes = new HashMap<String,StkFnType>();
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<StkFnType> fnTypeList = JdbcUtils.list(conn, SQL_SELECT_FN_TYPE_ALL, StkFnType.class);
			for(StkFnType fnType : fnTypeList){
				fnTypes.put(fnType.getType().toString(), fnType);
			}
			CacheUtils.putForever(CacheUtils.KEY_FN_TYPE, fnTypes);
			return fnTypes;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static List<StkFnType> getFnTypesForDisplay(int market)throws Exception{
		List<StkFnType> fnTypes = (List<StkFnType>)CacheUtils.getForever(CacheUtils.KEY_FN_TYPE_DISPLAY + market);
		if(fnTypes != null){
			return fnTypes;
		}
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			fnTypes = JdbcUtils.list(conn, "select * from stk_fn_type where market="+market+" and status=1 and disp_order <> 1000 order by disp_order asc", StkFnType.class);
			CacheUtils.putForever(CacheUtils.KEY_FN_TYPE_DISPLAY + market, fnTypes);
			return fnTypes;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Fn.getFnTypes();
		Fn.getFnTypes();
	}
}
