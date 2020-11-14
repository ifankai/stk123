package com.stk123.common.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.stk123.common.db.util.DBUtil;


public class Database {
	
	private String dbConnectionName = null;
	private String databaseName = null;
	private String schema = null;
	
	private Map<String,Table> tables = new HashMap<String,Table>();
	
	private Database(String dbConnectionName){
		this.dbConnectionName = dbConnectionName;
	}
	
	public static Database getInstance(){
		return Database.getInstance(null);
	}
	
	public static Database getInstance(String dbConnection) {
		return new Database(dbConnection);
	}
	
	public Table getTable(String tableName){
		Table instance = null;
		String tableNameUpper = tableName.trim().toUpperCase();
		if((instance = tables.get(tableNameUpper)) != null){
			return instance;
		}
		instance = new Table(tableNameUpper,this);
		tables.put(tableNameUpper, instance);
		return instance;
		
	}
	
	/*public void setTable(Table table){
		tables.put(table.getName(), table);
	}*/
	
	public String getDBConnectionName(){
		return this.dbConnectionName;
	}
	
	public String getDatabaseName(){
		if(databaseName == null)
			databaseName = getCurrentDatabaseName();
		return databaseName;
	}
	
	public String getSchema(){
		if(schema == null)
			schema = getCurrentSchema();
		return schema;
	}
	
	public Connection getDBConnection() throws Exception {
		//System.out.println("table="+this.getName()+",getDBConnectionName()=========="+getDBConnectionName());
		return DBUtil.getConnection(getDBConnectionName());
	}
	
	private String getCurrentSchema() {
		DatabaseMetaData dm;
		try {
			dm = this.getDBConnection().getMetaData();
			return dm.getUserName();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getCurrentDatabaseName() {
		DatabaseMetaData dm;
		try {
			dm = this.getDBConnection().getMetaData();
			return dm.getDatabaseProductName();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
