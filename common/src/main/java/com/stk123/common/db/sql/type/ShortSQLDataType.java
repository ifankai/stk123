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

import com.stk123.common.db.sql.SQLDataType;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ShortSQLDataType implements SQLDataType {

	private Short nullValue = null;
	
	public void setNullToValue(Object nullValue) {
		this.nullValue = (Short) nullValue ;
	}
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		short value = rs.getShort(colName) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return new Short(value) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		short value = rs.getShort(colIndex) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return new Short(value) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value)  throws SQLException {
		if(value == null){
			pstm.setShort(parameterIndex, this.nullValue) ;
			return ;
		}
		if(value instanceof String){
			value = getValueFromString((String) value) ;
		}
		
		short v = ((Number) value).shortValue() ;
		
		pstm.setShort(parameterIndex, v) ;
	}
	
	public Class getJavaDataType(){
		return Short.class ;
	}

	public Object getValueFromString(String value) {
		return Short.valueOf(value) ;
	}
	
	public String getValueAsString(Object value) {
		return String.valueOf(value);//todo
	}
	
	public String getValueAsSQLString(Object value){
		if(value == null){
			return SQLDataType.NULL;
		}
		return getValueAsString(value);
	}

}
