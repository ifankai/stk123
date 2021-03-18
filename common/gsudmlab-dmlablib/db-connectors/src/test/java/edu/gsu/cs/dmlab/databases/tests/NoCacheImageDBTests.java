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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import edu.gsu.cs.dmlab.databases.NonCacheImageDBConnection;
import edu.gsu.cs.dmlab.databases.interfaces.ISTImageDBConnection;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgParamNormalizer;

/**
 * Implemented by Dustin Kempton 10/10/15
 */
public class NoCacheImageDBTests {

	@Test
	public void testDefaultConstructorThrowsOnNullConnection() throws IllegalArgumentException {
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = mock(Logger.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTImageDBConnection dbConnect = new NonCacheImageDBConnection(null, normalizer, logger);
		});
	}

	@Test
	public void testConstructorWDownSampleThrowsOnNullConnection() throws IllegalArgumentException {
		IImgParamNormalizer normalizer = mock(IImgParamNormalizer.class);
		Logger logger = mock(Logger.class);
		int paramDim = 1;
		int paramDownSample = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTImageDBConnection dbConnect = new NonCacheImageDBConnection(null, normalizer, paramDim, paramDownSample,
					logger);
		});
	}

}