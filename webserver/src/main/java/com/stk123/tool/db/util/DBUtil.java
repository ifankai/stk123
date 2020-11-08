package com.stk123.tool.db.util;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.stk123.tool.util.ConfigUtils;
import com.stk123.web.StkConstant;


public class DBUtil {
	
	public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String H2_DRIVER = "org.h2.Driver";
	
	public static final String DB_URL = "db_url";
	public static final String DB_USER = "db_user";
	public static final String DB_PASS = "db_pass";
	
	public static final String DB = "database";
	public static final String DB_ORACLE = "oracle";
	public static final boolean isOracle = ConfigUtils.getProp(DB).equalsIgnoreCase(DB_ORACLE);
	
	public static final String DB_ORACLE_URL = ConfigUtils.getProp(DB_URL);
	public static final String DB_ORACLE_USER = ConfigUtils.getProp(DB_USER);
	public static final String DB_ORACLE_PASS = ConfigUtils.getProp(DB_PASS);
	
	public static final String DB_H2_URL = ConfigUtils.getProp("h2."+DB_URL);
	public static final String DB_H2_USER = ConfigUtils.getProp("h2."+DB_USER);
	public static final String DB_H2_PASS = ConfigUtils.getProp("h2."+DB_PASS);
	
	private static final String MYSQL_URL = ConfigUtils.getProp(DB_URL);

	private static DruidPoolConnection druidPoolConnection = null;

	static {
        if(druidPoolConnection == null){
            druidPoolConnection = DruidPoolConnection.getInstance();
        }
    }
	
	public static Connection getH2Connection() throws ClassNotFoundException,SQLException {
		Class.forName(H2_DRIVER);
		return DriverManager.getConnection(DB_H2_URL, DB_H2_USER, DB_H2_PASS);
	}
	
	public static DBConnection getDBConnection() throws Exception {
		return getDBConnection(null);
	}
	
	public static DBConnection getDBConnection(String db) throws ClassNotFoundException,SQLException {
		if(isOracle){
			return DBUtil.getOracleDBConnection(db);
		}
		return DBUtil.getMySqlDBConnection(db);
	}
	
	public static DBConnection getMySqlDBConnection(String db) throws ClassNotFoundException,SQLException {
		if(MYSQL_URL == null){
			throw new SQLException("Please set database configuration file!");
		}
		Class.forName(MYSQL_DRIVER);//
		/*String userName = System.getProperty("user.name");
		if("fankai".equals(userName)){
			db = "xe";
		}*/
		/*String db_url = "db_url";
		String db_user = "db_user";
		String db_pass = "db_pass";
		if(db != null && !"".equals(db)){
			db_url = db+"."+db_url;
			db_user = db+"."+db_user;
			db_pass = db+"."+db_pass;
		}*/
		
		Connection conn = DriverManager.getConnection(MYSQL_URL);
		return new DBConnection(conn);
	}
	
	public static DBConnection getOracleDBConnection(String db) throws ClassNotFoundException,SQLException {
		Class.forName(ORACLE_DRIVER);//
		if(DB_ORACLE_URL == null){
			throw new SQLException("Please set database configuration file!");
		}
		//System.out.println(DB_ORACLE_URL);
		Connection conn = DriverManager.getConnection(DB_ORACLE_URL,DB_ORACLE_USER,DB_ORACLE_PASS);
		return new DBConnection(conn);
	}
	
	public static Connection getConnection() throws ClassNotFoundException,SQLException {
		//return getDBConnection(null).getConnection();
        return druidPoolConnection.getConnection();
	}
	
	public static Connection getConnection(String db) throws Exception {
		return getDBConnection(db).getConnection();
	}
	
	public static byte[] blobToBytes(Blob pBlob) {
        byte buffer[] = null;
        try {
            if(pBlob != null)
                buffer = pBlob.getBytes(1L, (int)pBlob.length());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }
	
	public static void closeAll(ResultSet rs, Statement pst, DBConnection con){
		closeAll(rs,pst,con.getConnection());
	}
	
	public static void closeAll(ResultSet rs, Statement pst, Connection con){
		if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if(pst != null)
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if(con != null)
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
