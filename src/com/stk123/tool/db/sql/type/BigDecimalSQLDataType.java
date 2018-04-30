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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.stk123.tool.db.sql.SQLDataType;


public class BigDecimalSQLDataType implements SQLDataType {

	private BigDecimal nullValue = null;
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		BigDecimal bd = rs.getBigDecimal(colName) ;
		//bd.setScale(0, BigDecimal.ROUND_UNNECESSARY).unscaledValue();
		//System.out.println(colName+"="+bd);
		return bd == null ? this.nullValue : bd ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		BigDecimal bd = rs.getBigDecimal(colIndex) ;
		return bd == null ? this.nullValue : bd ;
	}

	public void setNullToValue(Object nullValue) {
		this.nullValue = (BigDecimal) nullValue ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value instanceof String){
			value = getValueFromString((String) value) ;
		}
		pstm.setBigDecimal(parameterIndex, value == null ? this.nullValue : (BigDecimal) value) ;
	}
	
	public Class getJavaDataType(){
		return BigDecimal.class ;
	}

	public Object getValueFromString(String value) {
		return new BigDecimal(value);
	}
	
	public String getValueAsString(Object value) {
		return ((BigDecimal)value).toString();
	}
	
	public String getValueAsSQLString(Object value){
		if(value == null){
			return SQLDataType.NULL;
		}
		return getValueAsString(value);
	}
}
