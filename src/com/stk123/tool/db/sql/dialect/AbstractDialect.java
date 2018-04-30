/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.stk123.tool.db.sql.dialect;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.stk123.tool.db.Cell;
import com.stk123.tool.db.Column;
import com.stk123.tool.db.FK;
import com.stk123.tool.db.PK;
import com.stk123.tool.db.Row;
import com.stk123.tool.db.sql.Dialect;
import com.stk123.tool.db.sql.type.BigDecimalSQLDataType;
import com.stk123.tool.db.sql.type.BlobSQLDataType;
import com.stk123.tool.db.sql.type.BooleanSQLDataType;
import com.stk123.tool.db.sql.type.ByteSQLDataType;
import com.stk123.tool.db.sql.type.BytesSQLDataType;
import com.stk123.tool.db.sql.type.CalendarSQLDataType;
import com.stk123.tool.db.sql.type.ClobSQLDataType;
import com.stk123.tool.db.sql.type.DateSQLDataType;
import com.stk123.tool.db.sql.type.DoubleSQLDataType;
import com.stk123.tool.db.sql.type.FloatSQLDataType;
import com.stk123.tool.db.sql.type.IntegerSQLDataType;
import com.stk123.tool.db.sql.type.LongSQLDataType;
import com.stk123.tool.db.sql.type.ShortSQLDataType;
import com.stk123.tool.db.sql.type.StringSQLDataType;
import com.stk123.tool.db.sql.type.TimeSQLDataType;
import com.stk123.tool.db.sql.type.TimestampSQLDataType;



/**
 * 
 */
public abstract class AbstractDialect implements Dialect {
	protected transient final Log log = LogFactory.getLog(getClass()) ;	
	
	protected final Map sqlTypes = new HashMap() ;
	
	public AbstractDialect(){
		regSystemTypes() ;
	}
	
	protected void regSystemTypes(){
		sqlTypes.put("int", IntegerSQLDataType.class) ;
		sqlTypes.put("Integer", IntegerSQLDataType.class) ;
		sqlTypes.put(Integer.class.getName(), IntegerSQLDataType.class) ;
		
		sqlTypes.put("string", StringSQLDataType.class) ;
		sqlTypes.put("varchar", StringSQLDataType.class) ;
		sqlTypes.put("nvarchar", StringSQLDataType.class) ;
		sqlTypes.put("char", StringSQLDataType.class) ;
		sqlTypes.put("nchar", StringSQLDataType.class) ;
		sqlTypes.put("text", StringSQLDataType.class) ;
		sqlTypes.put("tinytext", StringSQLDataType.class) ;
		sqlTypes.put(String.class.getName(), StringSQLDataType.class) ;
		
		//java.sql.Timestamp
		sqlTypes.put("datetime", TimestampSQLDataType.class) ;
		sqlTypes.put("timestamp", TimestampSQLDataType.class) ;
		sqlTypes.put(java.util.Date.class.getName(), TimestampSQLDataType.class) ;
		sqlTypes.put(java.sql.Timestamp.class.getName(), TimestampSQLDataType.class) ;

		//java.sql.Date
		sqlTypes.put("date", DateSQLDataType.class) ;
		sqlTypes.put(java.sql.Date.class.getName(), DateSQLDataType.class) ;
		
		//java.util.Calendar
		sqlTypes.put(java.util.Calendar.class.getName(), CalendarSQLDataType.class) ;
		
		//java.sql.Time
		sqlTypes.put("time", TimeSQLDataType.class) ;
		sqlTypes.put(java.sql.Time.class.getName(), TimeSQLDataType.class) ;
		
		//boolean
		sqlTypes.put("bool", BooleanSQLDataType.class) ;
		sqlTypes.put("boolean", BooleanSQLDataType.class) ;	
		sqlTypes.put(Boolean.class.getName(), BooleanSQLDataType.class) ;		

		sqlTypes.put("bigint", LongSQLDataType.class) ;
		sqlTypes.put("long", LongSQLDataType.class) ;
		sqlTypes.put(Long.class.getName(), LongSQLDataType.class) ;
		
		sqlTypes.put("double", DoubleSQLDataType.class) ;
		sqlTypes.put(Double.class.getName(), DoubleSQLDataType.class) ;
		
		sqlTypes.put("money", BigDecimalSQLDataType.class) ;	
		sqlTypes.put("decimal", BigDecimalSQLDataType.class) ;
		sqlTypes.put(BigDecimal.class.getName(), BigDecimalSQLDataType.class) ;		

		sqlTypes.put("float", FloatSQLDataType.class) ;
		sqlTypes.put(Float.class.getName(), FloatSQLDataType.class) ;

		sqlTypes.put("short", ShortSQLDataType.class) ;
		sqlTypes.put("smallint", ShortSQLDataType.class) ;
		sqlTypes.put("tinyint", ShortSQLDataType.class) ;
		sqlTypes.put(Short.class.getName(), ShortSQLDataType.class) ;
		
		//bit
		sqlTypes.put("byte", ByteSQLDataType.class) ;
		sqlTypes.put("bit", ByteSQLDataType.class) ;
		sqlTypes.put(java.lang.Byte.class.getName(), ByteSQLDataType.class) ;
		
		//byte[]
		sqlTypes.put("bytes", BytesSQLDataType.class) ;
		sqlTypes.put("[B", BytesSQLDataType.class) ;//byte[]
		sqlTypes.put("[Ljava.lang.Byte;", BytesSQLDataType.class) ;//Byte[]
		sqlTypes.put("binary", BytesSQLDataType.class) ;
		sqlTypes.put("varbinary", BytesSQLDataType.class) ;
		
		//clob
		sqlTypes.put("clob", ClobSQLDataType.class) ;
		sqlTypes.put(java.sql.Clob.class.getName(), ClobSQLDataType.class) ;
		sqlTypes.put(com.stk123.tool.db.sql.lob.TranClob.class.getName(), ClobSQLDataType.class) ;
		
		//blob
		sqlTypes.put("blob", BlobSQLDataType.class) ;
		sqlTypes.put(java.sql.Blob.class.getName(), BlobSQLDataType.class) ;
		sqlTypes.put(com.stk123.tool.db.sql.lob.TranBlob.class.getName(), BlobSQLDataType.class) ;
		
		//enum
		/*sqlTypes.put("enum.ordinal", EnumOrdinalSQLDataType.class) ;
		sqlTypes.put("enum.string", EnumStringSQLDataType.class) ;*/
		
	}
	
	public void registerUserDefinedTypes(String typeName, Class dataType){
		sqlTypes.put(typeName, dataType);
	}

	public String getForUpdateNoWaitString(String sql) {
		return getForUpdateString(sql) ;
	}

	public String getForUpdateString(String sql) {
		return sql + " for update" ;
	}
	
	public boolean useStreamToInsertLob(){
		return true ;
	}
	
	public String getColumnsAsSQL(LinkedList<Column> columns) {
		StringBuffer statement = new StringBuffer();
		if(columns.size()>0){
			statement.append(columns2SQLInNumber(columns));
		}
		return statement.toString();
	}
	
	public String getRowsAsSQL(LinkedList<Row> rows){
		StringBuffer statement = new StringBuffer();
		if(rows.size()>0){
			statement.append(rows2SQLInNumber(rows));
		}
		return statement.toString();
	}
	
	public String getCellsAsSQL(LinkedList<Cell> cells){
		StringBuffer statement = new StringBuffer();
		if(cells.size()>0){
			statement.append(cells2SQLInDataType(cells));
		}
		return statement.toString();
	}
	
	public String getSQLWithParentheses(String sql){
		StringBuffer statement = new StringBuffer();
		statement.append("(").append(sql).append(")");
		return statement.toString();
	}
	
	private String cells2SQLInDataType(LinkedList<Cell> cells) {
		StringBuffer sb = new StringBuffer();
		for (Cell cell:cells){
			sb.append(",").append(cell.getColumn().getSQLDataType().getValueAsSQLString(cell.getValue()));
			/*if(cell.getValue() == null)
				sb.append(",").append("null");
		    else if(cell.getValue() instanceof java.lang.Number)
				sb.append(",").append(cell.getValue());
		    else if(cell.getValue() instanceof java.util.Date || cell.getValue() instanceof java.lang.Number)
		    	sb.append(",").append(cell.getColumn().getSQLDataType().getValueAsString(cell.getValue()));
			else
				sb.append(",").append("'").append(cell.getColumn().getSQLDataType().getValueAsString(cell.getValue())).append("'");
		*/
		}
		return sb.substring(1);
	}
	
	/*public String getColumnsAsWhereConditionSQL(LinkedList<Column> columns){
		StringBuffer whereCondition = new StringBuffer();
		for(int i=0;i<columns.size();i++){
			Column column = columns.get(i);
			String inOrEqual = null;
			String conditionWithParenthesesOrNot = null;
			if(column.getCells().size() == 1){
				inOrEqual = Dialect.EQUAL;
				conditionWithParenthesesOrNot = column.getCellsAsSQL();
			}else{
				inOrEqual = Dialect.IN;
				conditionWithParenthesesOrNot = column.getCellsAsSQLWithParentheses();
			}
			if(i == 0){
				whereCondition.append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}else{
				whereCondition.append(Dialect.AND).append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}
		}
		return whereCondition.toString();
	}*/
	
	
	/*public String getFKAsWhereConditionSQLByParentTablePK(FK fk,PK parentTablePK){
		if(!fk.getParentTable().getName().equals(parentTablePK.getTable().getName())){
			throw new RuntimeException("PK "+parentTablePK+" is not the parent of FK "+fk);
		}
		
		StringBuffer whereCondition = new StringBuffer();
		LinkedList<Column> columns = fk.getColumns();
		for(int i=0;i<columns.size();i++){
			Column column = columns.get(i);
			Column parentPKColumn = parentTablePK.getColumn(i);
			String inOrEqual = null;
			String conditionWithParenthesesOrNot = null;
			if(parentPKColumn.getCells().size() == 1){
				inOrEqual = Dialect.EQUAL;
				//conditionWithParenthesesOrNot = column.getCellsAsSQL();
				conditionWithParenthesesOrNot = parentPKColumn.getCellsAsSQL();
			}else{
				inOrEqual = Dialect.IN;
				conditionWithParenthesesOrNot = parentPKColumn.getCellsAsSQLWithParentheses();
			}
			if(i == 0){
				whereCondition.append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}else{
				whereCondition.append(Dialect.AND).append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}
		}
		return whereCondition.toString();
	}*/
	
	public String andWhereCondition(String whereCondition){
		if(whereCondition!=null && whereCondition.length()>0)
			return Dialect.AND + whereCondition;
		else
			return "";
	}
	
	public String andWhereCondition(StringBuffer whereCondition){
		if(whereCondition!=null && whereCondition.length()>0){
			return whereCondition.insert(0, Dialect.AND).toString();
		}else{
			return "";
		}
	}
	
	private String columns2SQLInNumber(LinkedList<Column> columns) {
		StringBuffer sb = new StringBuffer();
		for (Column col:columns){
			sb.append(",").append(col.getName());
		}
		return sb.substring(1);
	}
	
	private static String rows2SQLInNumber(LinkedList<Row> rows) {
		StringBuffer sb = new StringBuffer();
		for (Row row:rows){
			if(rows.size()==1){
				sb.append(",").append(row.getRowAsSQL());
			}else{
				sb.append(",").append(row.getRowAsSQLWithParentheses());
			}
		}
		return sb.substring(1);
	}
	
}
