
package com.stk123.common.db.sql;

import com.stk123.common.db.Column;


/**
 * 
 */
public interface Dialect {
	
	public static final String DELETE_FROM = "DELETE FROM ";
	public static final String INSERT_INTO = "INSERT INTO ";
	public static final String SELECT = "SELECT ";
	public static final String UPDATE = "UPDATE ";
	public static final String FROM = " FROM ";
	public static final String WHERE = " WHERE ";
	public static final String IN = " IN ";
	public static final String AND = " AND ";
	public static final String EQUAL = " = ";
	
	public static final String TABLE_SEPARATOR = ",";
    public static final String COLUMN_SEPARATOR = ",";
    public static final String TABLE_COLUMN_SEPARATOR = ".";
    public static final String PARAMETER = "?";
    public static final String ON = " ON ";
    public static final String LEFT_JOIN = " LEFT OUTER JOIN ";
    public static final String INNER_JOIN = " INNER JOIN ";
    public static final String DISTINCT = " DISTINCT ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";

	public SQLDataType getDataType(Column column) ;
	
	public void registerUserDefinedTypes(String typeName, Class dataType) ;
	
	//public String quoteName(String name);
	
	/**
	 * 
	 * @param sql
	 * @param offset no offset = 0, skip one = 1, ...
	 * @param limit 
	 */
	public String getLimitedString(String sql, int offset, int limit) ;
	
	
	//public String getForUpdateString(String sql) ;
	
	
	//public String getForUpdateNoWaitString(String sql) ;
	
	
	//public boolean supportsSequence() ;
	
	
	//public String getSelectSequenceClause(String sequenceName) ;
	
	/**当主键按照native配置时，实际使用哪种generator*/
	//public String getNativeIDGenerator() ;
	
	/**
	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
	 * {@link java.sql.PreparedStatement#setBinaryStream}).
	 *
	 * @return True if BLOBs and CLOBs should be bound using stream operations.
	 */
	public boolean useStreamToInsertLob() ;
	
	//public String getColumnsAsSQL(LinkedList<Column> columns);
	
	//public String getRowsAsSQL(LinkedList<Row> rows);
	
	//public String getCellsAsSQL(LinkedList<Cell> cells);
	
	//public String getSQLWithParentheses(String sql);
	
	//public String getColumnsAsWhereConditionSQL(LinkedList<Column> columns);
	
	//public String getFKAsWhereConditionSQLByParentTablePK(FK fk,PK parentTablePK);
	
	//public String andWhereCondition(String whereCondition);
	
	//public String andWhereCondition(StringBuffer whereCondition);
}
