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

import static org.mockito.Mockito.mock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import edu.gsu.cs.dmlab.databases.Postgres_StateDBConnector;
import edu.gsu.cs.dmlab.databases.interfaces.IISDStateDBConnector;
import edu.gsu.cs.dmlab.datatypes.EventType;

class Postgres_StateDBConnectorTests {

	@Test
	void testThrowsOnNullDataSource() {

		DataSource dsourc = null;
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName, logger);
		});

	}
  
	@Test
	void testThrowsOnNullBlockingList() {

		DataSource dsourc = mock(DataSource.class);
		List<String> blockingProcesses = null;// new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "someName";
		Logger logger = mock(Logger.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName, logger);
		});

	}

	@Test
	void testThrowsOnNullDateTime() {

		DataSource dsourc = mock(DataSource.class);
		List<String> blockingProcesses = new ArrayList<String>();
		;// new ArrayList<String>();
		DateTime defaultTime = null;
		String processName = "someName";
		Logger logger = mock(Logger.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName, logger);
		});

	}
  
	@Test
	void testThrowsOnNullProcessName() {

		DataSource dsourc = mock(DataSource.class);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = null;// "someName";

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName, null);
		});

	}

	@Test
	void testThrowsOnNullLogger() {

		DataSource dsourc = mock(DataSource.class);

		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName, logger);
		});

	}

	/*---------------------checkOKToProcess TestCases------------------------*/
	@Test
	void checkOKToProcess_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());  
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
        IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkOKToProcess(EventType.CORONAL_CAVITY);
		});


		
	}
	
	@Test
	void checkOKToProcess_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setIsProcessing(EventType.CORONAL_CAVITY);
		});  
  

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method setIsProcessing", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
	} 
  
	@Test
	void checkOKToProcess_throwsSQLExceptionOnPreparedStatement() throws SQLException {
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);

		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkOKToProcess(EventType.CORONAL_CAVITY);
		});

	}
  
	@Test
	void checkOKToProcess_throwsSQLExceptionOnResultset() throws SQLException {
		PreparedStatement rs = mock(PreparedStatement.class);
		when(rs.executeQuery()).thenThrow(new SQLException());
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(rs);
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkOKToProcess(EventType.CORONAL_CAVITY);
		});

	}

	/*---------------------SETISProcessing TestCases------------------------*/
	@Test
	void SetIsProcessing_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();  
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setIsProcessing(EventType.CORONAL_CAVITY);
		});

	}
	
	@Test
	void SetIsProcessing_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setIsProcessing(EventType.CORONAL_CAVITY);
		});


		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method setIsProcessing", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
	} 
	  
	@Test
	void SetIsProcessing_throwsSQLExceptionOnPreparedStatement() throws SQLException {

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setIsProcessing(EventType.CORONAL_CAVITY);
		});

	}

	@Test
	void SetIsProcessing_throwsSQLExceptionOnSecondPreparedStatement() throws SQLException {
		ResultSet set = mock(ResultSet.class);
		when(set.next()).thenReturn(true);
		when(set.getBoolean(anyInt())).thenReturn(true);

		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(set).thenThrow(new SQLException());

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);

		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		Logger logger = mock(Logger.class);
		String processName = "tracking-processor";
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		er.setIsProcessing(EventType.CORONAL_WAVE);

	}

	@Test
	void SetIsProcessing_throwsSQLExceptionOnResultsetNext() throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenThrow(new SQLException());
		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(rs);
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setIsProcessing(EventType.CORONAL_DIMMING);
		});

	}
	@Test
	void SetIsProcessing_throwsSQLExceptionOnResultsetFalse() throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true);
		when(rs.getBoolean(anyInt())).thenReturn(false);
		
		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(rs);
		when(val.executeUpdate()).thenThrow(new SQLException());
		
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setIsProcessing(EventType.CORONAL_DIMMING);
		});

	}

	/*---------------------UpdateLastProcessedTime TestCases------------------------*/
	@Test
	void updateLastProcessedTime_throwsSQLExceptionOnGetConnection() throws SQLException {

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";

		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.updateLastProcessedTime(EventType.CORONAL_HOLE, new DateTime());
		});

	}
	
	@Test
	void updateLastProcessedTime_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);

		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.updateLastProcessedTime(EventType.CORONAL_CAVITY, defaultTime);
		});


		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method updateLastProcessedTime", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
	}   
	  
	

	@Test
	void updateLastProcessedTime_throwsSQLExceptionOnPreparedStatement() throws SQLException {
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.updateLastProcessedTime(EventType.CORONAL_HOLE, new DateTime());
		});

	}

	/*---------------------getLastProcessedTime TestCases------------------------*/
	@Test
	void getLastProcessedTime_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getLastProcessedTime(EventType.CORONAL_HOLE);
		});

	}
	
	@Test
	void getLastProcessedTime_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);

		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getLastProcessedTime(EventType.CORONAL_CAVITY);
		});
  

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method getLastProcessedTime", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
	} 

	@Test
	void getLastProcessedTime_throwsSQLExceptionOnPreparedStatement() throws SQLException {
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getLastProcessedTime(EventType.CORONAL_HOLE);
		});

	}

	@Test
	void getLastProcessedTime_throwsSQLExceptionOnResultset() throws SQLException {
		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenThrow(new SQLException());

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getLastProcessedTime(EventType.CORONAL_HOLE);
		});

	}

	/*---------------------SetFinishedProcessing TestCases------------------------*/

	@Test
	void setFinishedProcessing_throwsSQLExceptionOnGetConnection() throws SQLException {

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setFinishedProcessing(EventType.ACTIVE_REGION);
		});

	}
	
	@Test
	void setFinishedProcessing_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);  

		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setFinishedProcessing(EventType.CORONAL_CAVITY);
		});
  

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQL Exception while executing method setFinishedProcessing", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());
	} 
	
	

	@Test
	void setFinishedProcessing_throwsSQLExceptionOnPreparedStatement() throws SQLException {

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenThrow(new SQLException());

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setFinishedProcessing(EventType.ACTIVE_REGION);
		});

	}

	@Test
	void setFinishedProcessing_throwsSQLExceptionOnResultset() throws SQLException {
		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenThrow(new SQLException());

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setFinishedProcessing(EventType.ACTIVE_REGION);
		});

	}

	@Test
	void setFinishedProcessing_throwsSQLExceptionOnSecondPreparedStatement() throws SQLException {

		ResultSet set = mock(ResultSet.class);
		when(set.next()).thenReturn(true);
		when(set.getBoolean(anyInt())).thenReturn(true);

		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(set);

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val).thenThrow(new SQLException());

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		List<String> blockingProcesses = new ArrayList<String>();
		DateTime defaultTime = new DateTime();
		String processName = "hek-proces0sor";
		Logger logger = mock(Logger.class);
		IISDStateDBConnector er = new Postgres_StateDBConnector(dsourc, blockingProcesses, defaultTime, processName,
				logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.setFinishedProcessing(EventType.ACTIVE_REGION);
		});

	}

}
