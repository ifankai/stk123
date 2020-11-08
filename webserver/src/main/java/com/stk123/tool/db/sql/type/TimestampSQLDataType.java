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
package com.stk123.tool.db.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.stk123.tool.db.sql.SQLDataType;
import com.stk123.tool.db.sql.exception.DataTypeException;
import com.stk123.tool.db.util.DateUtil;
import com.stk123.tool.db.util.StringUtil;


/**
 * 
 * datetime/timestamp
 * 
 * 完成数据库的timestamp字段类型数据和@link java.util.Date 类型的对象的转换
 *
 */
public class TimestampSQLDataType implements SQLDataType, ParameteredType {
	private static final String FMT = "yyyy-MM-dd HH:mm:ss" ;
	
	private Timestamp nullDate = null ;
	
	private String dateFormat = FMT ;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FMT);

	public void setParameter(String param) {
		if(StringUtil.notEmpty(param)){
			dateFormat = param ;
			simpleDateFormat = new SimpleDateFormat(param);
		}
	}
	
	public void setNullToValue(Object nullValue) {
		this.nullDate = (Timestamp) nullValue ;
	}

	public Object getValueFromString(String value) {
		Date d = DateUtil.stringToDate(value, dateFormat) ;
		if(d == null){
			throw new DataTypeException("unknown datetime:" + value + ", date format should be:" + dateFormat) ;
		}
		
		return new Timestamp(d.getTime()) ;
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Timestamp ts =  rs.getTimestamp(colName) ;
		
		if(ts == null){
			return this.nullDate ;
		}
		
		return ts ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Timestamp ts =  rs.getTimestamp(colIndex) ;
		if(ts == null){
			return this.nullDate ;
		}
		
		return ts ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			pstm.setTimestamp(parameterIndex, this.nullDate) ;
			
			return ;
		}
		if(value instanceof String){
			value = getValueFromString((String) value) ;
		}
				
		if(value instanceof java.sql.Timestamp){
			pstm.setTimestamp(parameterIndex, (Timestamp) value) ;
		}else if(value instanceof java.util.Date){
			Timestamp ts = new Timestamp(((Date) value).getTime()) ;
			pstm.setTimestamp(parameterIndex, ts) ;
		}else if(value instanceof java.sql.Date){
			Timestamp ts = new Timestamp(((java.sql.Date) value).getTime()) ;
			pstm.setTimestamp(parameterIndex, ts) ;
		}else{
			throw new DataTypeException("unknown datetime type:" + value.getClass()) ;
		}
	}
	
	public String getValueAsString(Object value) {
		if(value instanceof java.sql.Timestamp){
			return String.valueOf(value);
		}else{
			throw new DataTypeException("unknown timestamp date:" + value + ", date type shoule be Timestamp.class") ;
		}
		
	}
	
	public String getValueAsSQLString(Object value){
		if(value == null){
			return SQLDataType.NULL;
		}
		return new StringBuffer().append("to_date('").append(simpleDateFormat.format(((Timestamp) value).getTime())).append("','yyyy-mm-dd hh24:mi:ss')").toString();
	}
	
	public Class getJavaDataType(){
		return java.sql.Timestamp.class ;
	}

}
