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

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import com.stk123.tool.db.Column;
import com.stk123.tool.db.sql.SQLDataType;
import com.stk123.tool.db.sql.exception.DataTypeException;
import com.stk123.tool.db.sql.type.BigDecimalSQLDataType;
import com.stk123.tool.db.sql.type.BlobSQLDataType;
import com.stk123.tool.db.sql.type.BytesSQLDataType;
import com.stk123.tool.db.sql.type.ClobSQLDataType;
import com.stk123.tool.db.sql.type.DateSQLDataType;
import com.stk123.tool.db.sql.type.DialectAware;
import com.stk123.tool.db.sql.type.FloatSQLDataType;
import com.stk123.tool.db.sql.type.ParameteredType;
import com.stk123.tool.db.sql.type.StringSQLDataType;
import com.stk123.tool.db.sql.type.TimestampSQLDataType;



/**
 * 
 * A dialect for Oracle databases.
 *
 */
public class OracleDialect extends AbstractDialect {
	
	public OracleDialect(){
		super() ;
		
		this.registerUserDefinedTypes("Oracle.Long", StringSQLDataType.class) ;
		this.registerUserDefinedTypes("varchar2", StringSQLDataType.class) ;
		this.registerUserDefinedTypes("nclob", ClobSQLDataType.class) ;
		this.registerUserDefinedTypes("raw", BytesSQLDataType.class) ;
		
		
		this.registerUserDefinedTypes("BLOB", BlobSQLDataType.class) ;
		this.registerUserDefinedTypes("CHAR", StringSQLDataType.class) ;
		this.registerUserDefinedTypes("CLOB", ClobSQLDataType.class) ;
		this.registerUserDefinedTypes("DATE", TimestampSQLDataType.class) ;
		this.registerUserDefinedTypes("FLOAT", FloatSQLDataType.class) ;
		this.registerUserDefinedTypes("NVARCHAR2", StringSQLDataType.class) ;
		this.registerUserDefinedTypes("RAW", BytesSQLDataType.class) ;
		this.registerUserDefinedTypes("ROWID", StringSQLDataType.class) ;
		this.registerUserDefinedTypes("TIMESTAMP(6)", TimestampSQLDataType.class) ;
		this.registerUserDefinedTypes("VARCHAR2", StringSQLDataType.class) ;
		this.registerUserDefinedTypes("XMLTYPE", ClobSQLDataType.class) ;
		
		this.registerUserDefinedTypes("NUMBER", BigDecimalSQLDataType.class) ;
	}
	
	public SQLDataType getDataType(Column column){
		String colType = column.getSQLDataTypeAsString();
		String param = null ;
		int pos = colType.indexOf('|') ;
		
		if(pos != -1){
			param = colType.substring(pos + 1) ;
			colType = colType.substring(0, pos) ;
		}
		
		/*if(colType.indexOf('(') > 0){ //handle varchar(255), number(10, 4)...
			colType = colType.substring(0, colType.indexOf('(')) ;
		}*/
		if("NUMBER".equalsIgnoreCase(colType)){
			if(column.getDataScale()==0){
				if(column.getDataPrecision() <= 9){
					colType = "int";
				}else if(column.getDataPrecision() <= 18){
					colType = "long";
				}else{
					colType = "decimal";
				}
			}else{
				if(column.getDataScale() <= 6){
					colType = "double";
				}else{
					colType = "decimal";
				}
			}
		}
		
		Class type = (Class) sqlTypes.get(colType) ;
		
		if(type == null){
			Iterator i = this.sqlTypes.entrySet().iterator() ;
			while(i.hasNext()){
				Map.Entry e = (Entry) i.next() ;
				if(colType.equalsIgnoreCase((String) e.getKey())){
					type = (Class) e.getValue() ;
				}
			}
		}
		
		if(type != null){
			SQLDataType typeInstance ;
			try {
				typeInstance = (SQLDataType) type.newInstance() ;
			} catch (InstantiationException e) {
				throw new DataTypeException("unable to instance type class[" + type.getName() + "] for type:[" + colType + "].") ;
			} catch (IllegalAccessException e) {
				throw new DataTypeException("unable to instance type class[" + type.getName() + "] for type:[" + colType + "].") ;
			}
			
			if(typeInstance instanceof DialectAware){
				((DialectAware) typeInstance).setDialect(this) ;
			}
			
			if(typeInstance instanceof ParameteredType){
				((ParameteredType) typeInstance).setParameter(param) ;
			}else if(param != null){
				log.warn("data type class[" + type.getName() + "] for type:[" + colType + "] doesn't support parameterization. parameter:[" + param + "] is ignored.") ;
			}
			
			return typeInstance ;
		}else{
			throw new DataTypeException("column type[" + colType + "] is not supported.") ;
		}
	}

	public String getLimitedString(String sql, int offset, int limit) {
		sql = sql.trim() ;
		String sql2 = sql.toLowerCase() ;
		
		boolean isForUpdate = false ;
		boolean isForUpdateNoWait = false ;
		
		if( sql2.endsWith(" for update") ){
			sql = sql.substring(0, sql.length() - 11) ;
			isForUpdate = true ;
		}else if( sql2.endsWith(" for update nowait") ){
			sql = sql.substring( 0, sql.length() - 18) ;
			isForUpdateNoWait = true ;
		}

		StringBuffer sb = new StringBuffer(sql.length() + 128) ;
		
		if(offset > 0){
			sb.append("select * from ( select row_.*, rownum rownum_ from ( ") ;
		}
		else{
			sb.append("select * from ( ") ;
		}
		
		sb.append(sql) ;
		
		if(offset > 0){
			sb.append(" ) row_ ) where rownum_ <= ").append(limit + offset).append(" and rownum_ > ").append(offset) ;
		}
		else{
			sb.append(" ) where rownum <= ").append(limit) ;
		}

		if(isForUpdate ) {
			sb.append( " for update" ) ;
		}else if(isForUpdateNoWait){
			sb.append( " for update nowait" ) ;
		}

		return sb.toString() ;
	}

	public String getSelectSequenceClause(String sequenceName) {
		if(sequenceName == null){
			throw new IllegalArgumentException("sequence name cann't be null.") ;
		}
		
		StringBuffer sb = new StringBuffer(32) ;
		sb.append("select ").append(sequenceName).append(".nextval from dual") ;		
		
		return sb.toString() ;
	}

	public boolean supportsSequence() {
		return true;
	}

	public String getNativeIDGenerator() {
		return "sequence";
	}

	public String getForUpdateNoWaitString(String sql) {
		return sql + " for update nowait";
	}

	public String getForUpdateString(String sql) {
		return sql + " for update";
	}
	
	public String getEscapedColunmName(String columnName) {
		return '"' + columnName + '"' ;
	}
	
	public String quoteName(String name) {
        return doubleQuoteName(name).toUpperCase();
    }
	final String doubleQuoteName(String name) {
        StringBuffer buffer = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(name, ".");
        buffer.append('"');
        buffer.append(tokens.nextToken());
        for(; tokens.hasMoreTokens(); buffer.append(tokens.nextToken()))
            buffer.append("\".\"");

        buffer.append('"');
        return buffer.toString();
    }

	
}
