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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import static org.mockito.Mockito.mock;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.GeneralSTObject;

/**
 * Created by Michael on 6/6/19
 * 
 */

class GeneralSTObjectTests {

	@Test
	public void testConstructorThrowsOnNullInterval() throws IllegalArgumentException {
		Geometry geometry = mock(Geometry.class); 
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTObject generalSTObject = new GeneralSTObject(null, geometry);
		});
	}
	
	@Test 
	public void testConstructorThrowsOnNullGeometry() throws IllegalArgumentException {
		Interval intvl = new Interval(6000, 8000);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTObject generalSTObject = new GeneralSTObject(intvl, null);
		});
	}
	
	@Test
	public void testGetCentroid() throws Exception{
		Geometry geometry = mock(Geometry.class);
		Interval intvl = new Interval(6000, 8000);
		GeneralSTObject generalSTObject = new GeneralSTObject(intvl, geometry);
		assertEquals(generalSTObject.getCentroid(), geometry.getCentroid()); 
	}
	
	@Test
	public void testGetEnvelope() throws Exception{
		Geometry geometry = mock(Geometry.class);
		Interval intvl = new Interval(6000, 8000);
		GeneralSTObject generalSTObject = new GeneralSTObject(intvl, geometry);
		assertEquals(generalSTObject.getEnvelope(), geometry.getEnvelopeInternal()); 
	}
	
	@Test 
	public void testGetGeometry() throws Exception{
		Geometry geometry = mock(Geometry.class);
		Interval intvl = new Interval(6000, 8000);
		GeneralSTObject generalSTObject = new GeneralSTObject(intvl, geometry);
		assertEquals(generalSTObject.getGeometry(), geometry); 
	}
	
	@Test 
	public void testGetVolume() throws Exception{
		Geometry geometry = mock(Geometry.class);
		Interval intvl = new Interval(6000, 8000); 
		GeneralSTObject generalSTObject = new GeneralSTObject(intvl, geometry); 
		assertEquals(generalSTObject.getVolume(), intvl.toDurationMillis() * geometry.getArea());
	}
}
