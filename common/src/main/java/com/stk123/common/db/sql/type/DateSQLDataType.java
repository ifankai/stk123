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
package com.stk123.common.db.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.stk123.common.db.sql.SQLDataType;
import com.stk123.common.db.sql.exception.DataTypeException;
import com.stk123.common.db.util.DateUtil;
import com.stk123.common.db.util.StringUtil;


/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class DateSQLDataType implements SQLDataType, ParameteredType {
	private static final String FMT = "yyyy-MM-dd" ;
	
	private Date nullDate = null ;
	
	private String dateFormat = FMT ;
	private SimpleDateFormat simpleDateFormat = null;

	public void setParameter(String param) {
		if(StringUtil.notEmpty(param)){
			dateFormat = param ;
			simpleDateFormat = new SimpleDateFormat(param);
		}
	}
	
	public void setNullToValue(Object nullValue) {
		this.nullDate= (Date) nullValue ;
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Date d = rs.getDate(colName) ;
		
		/*if(d == null){
			return this.nullDate ;
		}*/
		
		return d ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Date d = rs.getDate(colIndex) ;
		
		if(d == null){
			return this.nullDate ;
		}
		
		return d ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			if(this.nullDate == null){
				pstm.setDate(parameterIndex, null) ;
			}else{
				pstm.setDate(parameterIndex, new java.sql.Date(this.nullDate.getTime())) ;
			}
			
			return ;
		}
		if(value instanceof String){
			value = getValueFromString((String) value) ;
		}
		
		if(value instanceof java.util.Date){
			pstm.setDate(parameterIndex, new java.sql.Date(((Date) value).getTime())) ;
		}else if(value instanceof java.sql.Date){
			pstm.setDate(parameterIndex, (java.sql.Date) value) ;
		}else{
			throw new DataTypeException("unknown date type:" + value.getClass()) ;
		}
	}
	
	public Class getJavaDataType(){
		return java.sql.Date.class ;
	}

	public Object getValueFromString(String value) {
		Date d = DateUtil.stringToDate(value, dateFormat) ;
		if(d == null){
			throw new DataTypeException("unknown date:" + value + ", date format shoule be:" + dateFormat) ;
		}
		
		return new java.sql.Date(d.getTime()) ;
	}
	
	public String getValueAsString(Object value) {
		if(value instanceof java.sql.Date){
			return simpleDateFormat.format(((Calendar) value).getTime());
		}else{
			throw new DataTypeException("unknown date date:" + value + ", date type shoule be Calendar.class") ;
		}
		
	}
	
	public String getValueAsSQLString(Object value){
		if(value == null){
			return SQLDataType.NULL;
		}
		return getValueAsString(value);
	}

}
