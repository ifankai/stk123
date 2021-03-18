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

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.*;

import edu.gsu.cs.dmlab.databases.ImageDBCreator;

import edu.gsu.cs.dmlab.databases.interfaces.IImageDBCreator;
import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import smile.math.matrix.SparseMatrix;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class ImageDBCreatorTests {
	
	/*------------On First Constructor------------------*/

	@Test
	void testThrowsOnNullDataSource() {
		DataSource dsourc = null;

		Logger logger = mock(Logger.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new ImageDBCreator(dsourc, logger);
		});

	}  

	@Test
	void testThrowsOnNullLogger() {
		DataSource dsourc = mock(DataSource.class);

		Logger logger = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new ImageDBCreator(dsourc, logger);
		});

	}
	/*------------On Second Constructor------------------*/

	@Test
	void testThrowsOnNullDataSource2() {
		DataSource dsourc = null;
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		});

	}

	@Test
	void testThrowsOnNullLogger2() {
		DataSource dsourc = mock(DataSource.class);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		});

	}

	@Test
	void testThrowsOnNullcorrectParamRowCount() {
		DataSource dsourc = mock(DataSource.class);
		int correctParamRowCount = 0;
		int numParams = 10;
		Logger logger = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		});

	}

	@Test
	void testThrowsOnNullnumParams() {
		DataSource dsourc = mock(DataSource.class);
		int correctParamRowCount = 64 * 64;
		int numParams = 0;
		Logger logger = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		});

	}

	/*---------------insertFileDescriptTables-------------------*/

	@Test
	void insertFileDescriptTables_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);

		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertFileDescriptTables(null);
		});
	}

	@Test
	void insertFileDescriptTables_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);

		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertFileDescriptTables(null);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method insertFileDescriptTables", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*---------------insertFileDescript-------------------*/
	@Test
	void insertFileDescript_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertFileDescript(Waveband.AIA131, period);
		});
	}

	@Test
	void insertFileDescript_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);

		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertFileDescript(Waveband.AIA131, null);
		});  

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while exeuting method insertFileDescript", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*---------------------insertImage--------------*/

	@Test
	void insertImage_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);

		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertImage(null, 0, null);
		});
	}

	@Test
	void insertImage_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();  
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);

		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertImage(null, 0, null);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("Exception occurred while executing method insertImage ", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}


	
	/*---------------------checkParamsExist--------------*/
	@Test
	void checkParamsExist_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		
		
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkParamsExist(1, period);
		});
	}
	@Test
	void checkParamsExist_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		
 		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkParamsExist(0, period);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method checkParamsExist", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}
	
	/*---------------------insertParams--------------*/
	@Test    
	void insertParams_throwsSQLExceptionOnGetConnection() throws SQLException {
		ResultSet res = mock(ResultSet.class);
		when(res.next()).thenReturn(false);
		
		PreparedStatement val = mock(PreparedStatement.class);
		
		when(val.executeQuery()).thenReturn(res);
		
		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val).thenThrow(new SQLException());
		
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		double[][][] params = new double[1][1][1];
		
		
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertParams(params, 1, period);
		});
	}
	/*---------------------checkImagesExist-----------------*/
	@Test
	void checkImagesExist_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		  
		
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkImagesExist(1, period);
		});
	}
	
	@Test
	void checkImagesExist_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.checkImagesExist(1, period);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method checkImagesExist", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}
	
	/*-----------------insertHeader--------------------*/
	@Test
	void insertHeader_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		ImageDBFitsHeaderData header = mock(ImageDBFitsHeaderData.class);
		
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertHeader(header, 1, period);
		});
	}
	
	@Test
	void insertHeader_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		
		ImageDBFitsHeaderData header = mock(ImageDBFitsHeaderData.class);
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertHeader(header, 1, period);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method insertHeader", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}
	/*-------------------------insertImageSparseVect-----------------------*/
	
	@Test
	void insertImageSparseVect_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		
		SparseMatrix[] vectors = new SparseMatrix[12];
		
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertImageSparseVect(vectors, 1, period);
		});
	}
	
	@Test
	void insertImageSparseVect_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		int correctParamRowCount = 64 * 64;
		int numParams = 10;
		Logger logger = mock(Logger.class);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(),now.getMillis());
		
		ImageDBFitsHeaderData header = mock(ImageDBFitsHeaderData.class);
		IImageDBCreator er = new ImageDBCreator(dsourc, correctParamRowCount, numParams, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.insertHeader(header, 1, period);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method insertHeader", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

}
