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
package edu.gsu.cs.dmlab.datatypes.tests;

import edu.gsu.cs.dmlab.datatypes.BaseTemporalObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Michael on 6/6/19
 * 
 */

class BaseTemporalObjectTests {

	@Test
	public void testConstructorThrowsOnNullInterval() throws IllegalArgumentException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			BaseTemporalObject temporalObject = new BaseTemporalObject(null);
		});
	}
	
	@Test
	public void testGetTimePeriod() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		BaseTemporalObject temporalObject = new BaseTemporalObject(intvl);
		assertEquals(intvl, temporalObject.getTimePeriod());
	}
	
	@Test
	public void testGetUUID() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		BaseTemporalObject temporalObject = new BaseTemporalObject(intvl);
		assertNotNull(temporalObject.getUUID());
	}

}

/*
 * That the constructor throws when illegal arguments are passed in 
 * That the
 * time period passed in is the same as the time period that is returned by the
 * getTimePeriod 
 * That the getUUID actually returns a UUID
 */
