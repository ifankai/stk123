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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.GeneralSTTrackingEvent;

/**
 * Created by Michael on 6/7/19
 * 
 */

class GeneralSTTrackingEventTests {
	
	@Test
	public void testConstructorThrowsOnNullEvent() throws IllegalArgumentException {
		Interval intvl = new Interval(6000, 8000);
		Geometry geometry = mock(Geometry.class); 
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(0, null, intvl, geometry);
		});
	}
	
	@Test
	public void testConstructorThrowsOnNullInterval() throws IllegalArgumentException {
		Geometry geometry = mock(Geometry.class); 
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(0, EventType.ACTIVE_REGION, null, geometry);
		});
	}
	
	@Test
	public void testConstructorThrowsOnNullGeometry() throws IllegalArgumentException {
		Interval intvl = new Interval(6000, 8000);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(0, EventType.ACTIVE_REGION, intvl, null);
		});
	}
	
	@Test
	public void testGetID() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		Geometry geometry = mock(Geometry.class); 
		int id = 1; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry); 
		assertEquals(generalSTTrackingEvent.getId(), id);
	}
	
	@Test
	public void testUpdateTimePeriod() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		Interval intvl2 = new Interval(5000, 7000); 
		Geometry geometry = mock(Geometry.class); 
		int id = 1; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry);
		generalSTTrackingEvent.updateTimePeriod(intvl2); 
		assertEquals(intvl2, generalSTTrackingEvent.getTimePeriod());
	}

	@Test
	public void testGetType() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		Geometry geometry = mock(Geometry.class); 
		int id = 1; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry); 
		assertEquals(generalSTTrackingEvent.getType(), EventType.ACTIVE_REGION);
	}
	
	@Test
	public void testGetType2() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		Geometry geometry = mock(Geometry.class); 
		int id = 1; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.CORONAL_HOLE, intvl, geometry); 
		assertEquals(generalSTTrackingEvent.getType(), EventType.CORONAL_HOLE);
	}
	
	@Test
	public void testGetPreviousIsNull() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		Geometry geometry = mock(Geometry.class); 
		int id = 1; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry); 
		assertEquals(generalSTTrackingEvent.getPrevious(), null);
	}
	
	@Test
	public void testGetNextIsNull() throws Exception {
		Interval intvl = new Interval(6000, 8000);
		Geometry geometry = mock(Geometry.class); 
		int id = 1; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry); 
		assertEquals(generalSTTrackingEvent.getNext(), null);
	}
	
	@Test public void testSetPrevious() throws Exception{
		Interval intvl = new Interval(6000, 8000); 
		Interval intvl2 = new Interval(5000, 7000); 
		Geometry geometry = mock(Geometry.class);
		int id = 1; 
		int id2 = 2; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry); 
		GeneralSTTrackingEvent generalSTTrackingEvent2 = new GeneralSTTrackingEvent(id2, EventType.ACTIVE_REGION, intvl2, geometry); 
		generalSTTrackingEvent.setPrevious(generalSTTrackingEvent2);
		assertEquals(generalSTTrackingEvent.getPrevious().getId(), id2); 
		
		generalSTTrackingEvent.setPrevious(null); 
		assertEquals(generalSTTrackingEvent.getPrevious(), null); 
		assertEquals(generalSTTrackingEvent.getNext(), null); 
	}
	
	@Test public void testSetNext() throws Exception{
		Interval intvl = new Interval(6000, 8000); 
		Interval intvl2 = new Interval(5000, 7000); 
		Geometry geometry = mock(Geometry.class);
		int id = 1; 
		int id2 = 2; 
		GeneralSTTrackingEvent generalSTTrackingEvent = new GeneralSTTrackingEvent(id, EventType.ACTIVE_REGION, intvl, geometry); 
		GeneralSTTrackingEvent generalSTTrackingEvent2 = new GeneralSTTrackingEvent(id2, EventType.ACTIVE_REGION, intvl2, geometry); 
		generalSTTrackingEvent.setNext(generalSTTrackingEvent2);
		assertEquals(generalSTTrackingEvent.getNext().getId(), id2); 
		
		generalSTTrackingEvent.setNext(null); 
		assertEquals(generalSTTrackingEvent.getNext(), null); 
		assertEquals(generalSTTrackingEvent.getPrevious(), null); 
	}
}