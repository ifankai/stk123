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
import static org.mockito.Mockito.mock;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.GeneralSTInterpolationEvent;

/**
 * 
 * @author Michael Tinglof, 6/13/19
 *
 */

class GeneralSTInterpolationEventTests {
	
	@Test
	public void testConstructorThrowsOnNullInterval1() throws IllegalArgumentException{
		int id = 1; 
		Geometry geometry = mock(Geometry.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, null, EventType.ACTIVE_REGION,
					geometry); 
		}); 
	}
	
	@Test
	public void testConstructorThrowsOnNullType() throws IllegalArgumentException{
		int id = 1; 
		Interval interval = new Interval(6000, 8000); 
		Geometry geometry = mock(Geometry.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, interval, null,
					geometry); 
		}); 
	}
	
	@Test
	public void testConstructorThrowsOnNullGeometry() throws IllegalArgumentException{
		int id = 1; 
		Interval interval = new Interval(6000, 8000); 
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, interval, EventType.ACTIVE_REGION,
					null); 
		}); 
	}
	
	@Test
	public void testGetId() throws Exception{
		int id = 1; 
		Interval interval = new Interval(6000, 8000); 
		Geometry geometry = mock(Geometry.class); 
		GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, interval, EventType.ACTIVE_REGION, 
				geometry); 
		assertEquals(id, interEvent.getId()); 
	}
	
	@Test
	public void testisInterpolated() throws Exception{
		boolean isInterpolated = true; 
		int id = 1; 
		Interval interval = new Interval(6000, 8000); 
		Geometry geometry = mock(Geometry.class); 
		GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, interval, EventType.ACTIVE_REGION, 
				geometry); 
		assertEquals(interEvent.isInterpolated(), isInterpolated); 
	}
	
	@Test
	public void testUpdateTimePeriod() throws Exception{
		int id = 1; 
		Interval interval = new Interval(6000, 8000); 
		Interval interval2 = new Interval(7000, 9000); 
		Geometry geometry = mock(Geometry.class); 
		GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, interval, EventType.ACTIVE_REGION, 
				geometry); 
		interEvent.updateTimePeriod(interval2);
		assertEquals(interval2, interEvent.getTimePeriod()); 
	}
	
	@Test
	public void testGetType() throws Exception{
		int id = 1; 
		Interval interval = new Interval(6000, 8000); 
		Geometry geometry = mock(Geometry.class); 
		GeneralSTInterpolationEvent interEvent = new GeneralSTInterpolationEvent(id, interval, EventType.ACTIVE_REGION, 
				geometry); 
		assertEquals(EventType.ACTIVE_REGION, interEvent.getType()); 
	}
}
