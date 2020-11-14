package com.stk123.common.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.stk123.common.util.ConfigUtils;



public class DBConnection {

	private Connection conn;
	
	public DBConnection(Connection conn){
		this.conn = conn;
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException{
		if("Y".equals(ConfigUtils.getProp("sql_show"))){
			System.out.println(sql);
		}
		return conn.prepareStatement(sql);
	}
	
	public Connection getConnection(){
		return this.conn;
	}
	
	public void close() throws SQLException{
		conn.close();
	}

}
