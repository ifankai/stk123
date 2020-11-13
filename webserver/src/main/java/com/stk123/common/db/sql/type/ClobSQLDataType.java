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

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.stk123.common.db.sql.Dialect;
import com.stk123.common.db.sql.SQLDataType;
import com.stk123.common.db.sql.exception.DataTypeException;
import com.stk123.common.db.sql.lob.TranClob;


/**
 * 
 * CLOB data type. the returned value is {@link TranClob}.
 * 
 * @see TranClob
 * @see Clob
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class ClobSQLDataType implements SQLDataType, DialectAware {
	
	private Dialect dialect ;

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Clob c = rs.getClob(colName) ;
		
		return c == null ? null : new TranClob(c) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Clob c = rs.getClob(colIndex) ;
		
		return c == null ? null : new TranClob(c) ;
	}

	public void setNullToValue(Object nullValue) {
		if(nullValue != null){
			throw new DataTypeException("null value unsupported. nullValue is:" + nullValue) ;
		}
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value instanceof String){
			value = getValueFromString((String) value) ;
		}
		
		boolean useStream = false ;
		Clob clob = (Clob) value ;
		
		if(clob instanceof TranClob && !((TranClob) clob).isLoadedFromDB() && this.dialect.useStreamToInsertLob()){
			useStream = true ;
		}
		
		if(useStream){
			pstm.setCharacterStream(parameterIndex, clob.getCharacterStream(), (int) clob.length() );
		}else{
			pstm.setClob(parameterIndex, clob) ;
		}
	}
	
	public Class getJavaDataType(){
		return String.class ;
	}

	public Object getValueFromString(String value) {
		return null;
	}
	
	public String getValueAsString(Object value) {
		return ((TranClob)value).getHex();
	}
	
	public String getValueAsSQLString(Object value){
		if(value == null){
			return SQLDataType.NULL;
		}
		return "'"+getValueAsString(value)+"'";
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect ;
	}

}
