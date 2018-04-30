package com.stk123.tool.db.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLDataType {
	
	public final static String NULL = "null";
	
	/**
	 * @param nullValue 默认
	 */
	public void setNullToValue(Object nullValue) ;
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException ;
	
	/**
	 * @param rs
	 * @param colIndex the first column is 1, the second column is 2, ...
	 */
	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException ;
	
	/**
	 * set the java paramter to PreparedStatement params.
	 * 
	 * @param pstm
	 * @param parameterIndex the first column is 1, the second column is 2, ...
	 * @param value could be null.
	 */
	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException ;
	
	/**
	 * get java data type.
	 */
	public Class getJavaDataType() ;

	/**
	 * convert the string to the data.
	 * @param value data in string format
	 */
	public Object getValueFromString(String value) ;
	
	public String getValueAsString(Object value) ;

	public String getValueAsSQLString(Object value) ;

}
