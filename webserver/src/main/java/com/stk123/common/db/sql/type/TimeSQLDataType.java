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
import java.sql.Time;
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
public class TimeSQLDataType implements SQLDataType, ParameteredType {
	private static final String FMT = "HH:mm:ss" ;
	
	private Time nullTime = null ;
	
	private String dateFormat = FMT ;

	public void setParameter(String param) {
		if(StringUtil.notEmpty(param)){
			dateFormat = param ;
		}
	}
	
	public void setNullToValue(Object nullValue) {
		this.nullTime = (Time) nullValue ;
	}

	public Object getValueFromString(String value) {
		Date d = DateUtil.stringToDate(value, dateFormat) ;
		if(d == null){
			throw new DataTypeException("unknown time:" + value + ", time format should be:" + dateFormat) ;
		}
		
		return new Time(d.getTime()) ;
	}
	public String getValueAsString(Object value) {
		return String.valueOf(value);//todo
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Time d = rs.getTime(colName) ;
		
		if(d == null){
			return this.nullTime ;
		}
		
		return d ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Date d = rs.getTime(colIndex) ;
		
		if(d == null){
			return this.nullTime ;
		}
		
		return d ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			if(this.nullTime == null){
				pstm.setTime(parameterIndex, null) ;
			}else{
				pstm.setTime(parameterIndex, this.nullTime) ;
			}
			
			return ;
		}
		if(value instanceof String){
			value = getValueFromString((String) value) ;
		}
		
		if(value instanceof java.util.Date){
			pstm.setTime(parameterIndex, new java.sql.Time(((Date) value).getTime())) ;
		}else if(value instanceof java.sql.Time){
			pstm.setTime(parameterIndex, (java.sql.Time) value) ;
		}else{
			throw new DataTypeException("unknown time type:" + value.getClass()) ;
		}
	}
	
	public Class getJavaDataType(){
		return java.sql.Time.class ;
	}
	
	public String getValueAsSQLString(Object value){
		if(value == null){
			return SQLDataType.NULL;
		}
		return getValueAsString(value);
	}

}
