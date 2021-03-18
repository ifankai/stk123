/**
 * dmLabLib, a Library created for use in various projects at the Data Mining Lab 
 * (http://dmlab.cs.gsu.edu/) of Georgia State University (http://www.gsu.edu/).  
 *  
 * Copyright (C) 2019 Georgia State University
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 3.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package edu.gsu.cs.dmlab.databases.tests;
  
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import edu.gsu.cs.dmlab.databases.Postgres_StateDBCreator;
import edu.gsu.cs.dmlab.databases.interfaces.IISDStateDBCreator;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.sql.SQLException;
import org.slf4j.*;

class Postgres_StateDBCreatorTests {

	@Test
	void test_NullDataSource() {

		DataSource dsourc = null;
		Logger logger = mock(Logger.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBCreator(dsourc, logger);
		});

	} 

	@Test
	void test_NullLogger() {

		DataSource dsourc = mock(DataSource.class);
		Logger logger = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBCreator(dsourc, logger);
		});

	}
    
	/*-------------------------------------checkStateTableExists---------------------------------*/

	@Test
	void checkStateTableExists_throwsSQLExceptionOnGetConnection() throws SQLException {

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		IISDStateDBCreator sb = new Postgres_StateDBCreator(dsourc, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			sb.checkStateTableExists();
		});

	}
	
	@Test
	void checkStateTableExists_checkLogging() throws SQLException {
		
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		
		Logger logger = mock(Logger.class);
		IISDStateDBCreator er = new Postgres_StateDBCreator(dsourc,logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkStateTableExists();
		});


		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method checkStateTableExists", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
		

	}
  
  
	@Test
	void checkStateTableExists_throwsSQLExceptionOnPreparedStatement() throws SQLException {
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		Logger logger = mock(Logger.class);

		IISDStateDBCreator sb = new Postgres_StateDBCreator(dsourc, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			sb.checkStateTableExists();
		});
	}
	
	@Test
	void checkStateTableExists_throwsSQLExceptionOnResultSet() throws SQLException {
		PreparedStatement rs = mock(PreparedStatement.class);
		when(rs.executeQuery()).thenThrow(new SQLException());
		
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(rs);
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		Logger logger = mock(Logger.class);

		IISDStateDBCreator sb = new Postgres_StateDBCreator(dsourc, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			sb.checkStateTableExists();
		});
	}
	/*  -------------------------------------createStateTable---------------------------------*/
	@Test
	void createStateTable_throwsSQLExceptionOnGetConnection() throws SQLException {
		ResultSet res = mock(ResultSet.class);  
		when(res.next()).thenReturn(true);
		
		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(res);
		when(val.executeUpdate()).thenThrow(new SQLException());
		
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);
		
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);

		Logger logger = mock(Logger.class);

		IISDStateDBCreator sb = new Postgres_StateDBCreator(dsourc, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			sb.createStateTable();
		});

	}    
	@Test
	void createStateTable_checkLogging() throws SQLException {
		
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		
		Logger logger = mock(Logger.class);
		IISDStateDBCreator er = new Postgres_StateDBCreator(dsourc,logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.createStateTable();
		});  


		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method createStateTable", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
		

	}
	
	

	@Test
	void createStateTable_throwsSQLExceptionOnPreparedStatement() throws SQLException {
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		Logger logger = mock(Logger.class);

		IISDStateDBCreator sb = new Postgres_StateDBCreator(dsourc, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			sb.createStateTable();
		});

	}
	    
	@Test
	void createStateTable_throwsSQLExceptionOnExecuteUpdate() throws SQLException {
		  
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(false); // to check else condition; 
		
		
		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(rs);
		when(val.executeUpdate()).thenThrow(new SQLException());
		
		Connection con = mock(Connection.class);
		when(con.getSchema()).thenReturn("");
		when(con.prepareStatement(any(String.class))).thenReturn(val);
		
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		Logger logger = mock(Logger.class);
  
		IISDStateDBCreator sb = new Postgres_StateDBCreator(dsourc, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			sb.createStateTable();
		});

	}
	
}
