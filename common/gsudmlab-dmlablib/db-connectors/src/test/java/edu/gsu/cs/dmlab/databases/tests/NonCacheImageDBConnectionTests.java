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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import edu.gsu.cs.dmlab.databases.NonCacheImageDBConnection;

import edu.gsu.cs.dmlab.databases.interfaces.ISTImageDBConnection;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgParamNormalizer;

class NonCacheImageDBConnectionTests {

	/*------------------First Constructor--------------------*/
	@Test
	void testThrowsOnNullDataSource() {

		DataSource dsourc = null;
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = mock(Logger.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new NonCacheImageDBConnection(dsourc, normalizer, logger);
		});

	}

	

	@Test
	void testThrowsOnNullLogger() {

		DataSource dsourc = mock(DataSource.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new NonCacheImageDBConnection(dsourc, normalizer, logger);
		});

	}

	/*------------------Second Constructor--------------------*/

	@Test
	void testThrowsOnNullDataSource2() {

		DataSource dsourc = null;
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = mock(Logger.class);
		int paramDim = 10;
		int paramDownSample = 64;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new NonCacheImageDBConnection(dsourc, normalizer, paramDim, paramDownSample, logger);
		});

	}

	

	@Test
	void testThrowsOnNullparamDim() {

		DataSource dsourc = mock(DataSource.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = mock(Logger.class);
		int paramDim = 0;
		int paramDownSample = 64;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new NonCacheImageDBConnection(dsourc, normalizer, paramDim, paramDownSample, logger);
		});

	}

	@Test
	void testThrowsOnNullparamDownSample() {

		DataSource dsourc = mock(DataSource.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = mock(Logger.class);
		int paramDim = 10;
		int paramDownSample = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new NonCacheImageDBConnection(dsourc, normalizer, paramDim, paramDownSample, logger);
		});

	}

	@Test
	void testThrowsOnNulllogger() {

		DataSource dsourc = mock(DataSource.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = null;
		int paramDim = 10;
		int paramDownSample = 65;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new NonCacheImageDBConnection(dsourc, normalizer, paramDim, paramDownSample, logger);
		});

	}

	/*------------------getFirstImage--------------------------*/

	@Test
	void getFirstImage_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFirstImage(period, Waveband.AIA131);
		});
	}

	@Test
	void getFirstImage_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFirstImage(period, Waveband.AIA131);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method getFirstImage", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*------------------getFirstFullImage--------------------------*/

	@Test
	void getFirstFullImage_throwsSQLExceptionOnGetConnection() throws SQLException {
		ResultSet res = mock(ResultSet.class);
		when(res.next()).thenReturn(false);

		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(res);

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFirstFullImage(period, Waveband.AIA131);
		});
	}

	@Test
	void getFirstFullImage_checklogging() throws SQLException {

		Timestamp ts = new Timestamp(1);
		ResultSet res = mock(ResultSet.class);
		when(res.next()).thenReturn(true).thenReturn(false);

		when(res.getInt(any(String.class))).thenReturn(1);
		when(res.getTimestamp(2)).thenReturn(ts);

		PreparedStatement val = mock(PreparedStatement.class);
		when(val.executeQuery()).thenReturn(res);

		Connection con = mock(Connection.class);
		when(con.prepareStatement(any(String.class))).thenReturn(val);

		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenReturn(con).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Waveband wavelength = Waveband.AIA131;

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFirstFullImage(period, wavelength);
		});
		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("Exception occurred while executing method getFirstFullImage", stringCaptor.getValue());
		assertEquals(SQLException.class, exCaptor.getValue().getClass());

	}

	/*---------------------getImageIdsForInterval--------------------*/

	@Test
	void getImageIdsForInterval_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);
		Waveband wavelength = Waveband.AIA131;

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImageIdsForInterval(period, wavelength);

		});
	}

	@Test
	void getImageIdsForInterval_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		Waveband wavelength = Waveband.AIA131;
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImageIdsForInterval(period, wavelength);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method getImageIdsForInterval", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*-------------------getImageParamForId-----------------------*/

	@Test
	void getImageParamForId_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImageParamForId(period, 1);

		});
	}

	@Test
	void getImageParamForId_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImageParamForId(period, 1);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("Exception occurred while executing method getImageParamForId", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*-------------------getImageSparseVectForId-----------------------*/

	@Test
	void getImageSparseVectForId_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImageSparseVectForId(period, 1);

		});
	}

	@Test
	void getImageSparseVectForId_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImageSparseVectForId(period, 1);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("Exception occurred while executing method getImageSparseVectForId", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*-------------------getFirstImageSparseVectForId-----------------------*/

	@Test
	void getFirstImageSparseVectForId_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFirstImageSparseVectForId(period, 1);

		});
	}

	@Test
	void getFirstImageSparseVectForId_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFirstImageSparseVectForId(period, 1);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("Exception occurred while executing method getFirstImageSparseVectForId", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*-------------------getImgForId-----------------------*/

	@Test
	void getImgForId_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImgForId(period, 1);

		});
	}

	@Test
	void getImgForId_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getImgForId(period, 1);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method getImgForId", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*-------------------getFullImgForId-----------------------*/

	@Test
	void getFullImgForId_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFullImgForId(period, 1);

		});
	}

	@Test
	void getFullImgForId_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getFullImgForId(period, 1);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("Exception occurred while executing method getFullImgForId", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

	/*-------------------getHeaderForId-----------------------*/

	@Test
	void getHeaderForId_throwsSQLExceptionOnGetConnection() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		when(dsourc.getConnection()).thenThrow(new SQLException());

		Logger logger = mock(Logger.class);

		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());

		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getHeaderForId(period, 1);

		});
	}

	@Test
	void getHeaderForId_checkLogging() throws SQLException {
		DataSource dsourc = mock(DataSource.class);
		SQLException ex = new SQLException();
		when(dsourc.getConnection()).thenThrow(ex);
		DateTime now = new DateTime();
		Interval period = new Interval(now.getMillisOfDay(), now.getMillis());
		Logger logger = mock(Logger.class);
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		ISTImageDBConnection er = new NonCacheImageDBConnection(dsourc, normalizer, logger);
		Assertions.assertThrows(SQLException.class, () -> {
			er.getHeaderForId(period, 1);
		});

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SQLException> exCaptor = ArgumentCaptor.forClass(SQLException.class);
		Mockito.verify(logger).error(stringCaptor.capture(), exCaptor.capture());
		assertEquals("SQLException occurred while executing method getHeaderForId", stringCaptor.getValue());
		assertEquals(ex, exCaptor.getValue());

	}

}
