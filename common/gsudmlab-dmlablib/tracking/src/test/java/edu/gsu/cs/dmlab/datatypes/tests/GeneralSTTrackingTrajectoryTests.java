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

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.GeneralSTTrackingTrajectory; 

/**
 * 
 * @author Michael Tinglof 6/10/19
 *
 */

class GeneralSTTrackingTrajectoryTests {

	@Test
	public void testConstructorThrowsOnNullTail() throws IllegalArgumentException{
		ISTTrackingEvent head = mock(ISTTrackingEvent.class); 
		Assertions.assertThrows(IllegalArgumentException.class,  () -> {
			@SuppressWarnings("unused")
			GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(head, null); 
		}); 
	}
	
	@Test
	public void testConstructorThrowsOnNullHead() throws IllegalArgumentException{
		ISTTrackingEvent tail = mock(ISTTrackingEvent.class);
		Assertions.assertThrows(IllegalArgumentException.class,  () -> {
			@SuppressWarnings("unused")
			GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(null, tail); 
		}); 
	}
	
	@Test
	public void testConstructorThrowsOnNull() throws IllegalArgumentException{
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(null); 
		}); 
	}
	
	@Test 
	public void testGetFirst() throws Exception{
		ISTTrackingEvent head = mock(ISTTrackingEvent.class); 
		ISTTrackingEvent tail = mock(ISTTrackingEvent.class);
		Interval headPeriod = new Interval(6000, 8000); 
		Interval tailPeriod = new Interval(7000, 9000); 
		when(head.getTimePeriod()).thenReturn(headPeriod);
		when(tail.getTimePeriod()).thenReturn(tailPeriod); 
		GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(head, tail); 
		assertEquals(trajectory.getFirst(), head); 
	}
	
	@Test
	public void testGetFirst2() throws Exception{
		ISTTrackingEvent head = mock(ISTTrackingEvent.class); 
		ISTTrackingEvent head2 = mock(ISTTrackingEvent.class);
		ISTTrackingEvent tail = mock(ISTTrackingEvent.class);
		Interval headPeriod = new Interval(6000, 8000); 
		Interval tailPeriod = new Interval(7000, 9000); 
		when(head.getTimePeriod()).thenReturn(headPeriod);
		when(tail.getTimePeriod()).thenReturn(tailPeriod); 
		GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(head, tail); 
		when(head.getPrevious()).thenReturn(head2); 
		assertEquals(trajectory.getFirst(), head2); 
	}
	
	@Test 
	public void testGetLast() throws Exception{
		ISTTrackingEvent head = mock(ISTTrackingEvent.class); 
		ISTTrackingEvent tail = mock(ISTTrackingEvent.class);
		Interval headPeriod = new Interval(6000, 8000); 
		Interval tailPeriod = new Interval(7000, 9000); 
		when(head.getTimePeriod()).thenReturn(headPeriod);
		when(tail.getTimePeriod()).thenReturn(tailPeriod); 
		GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(head, tail); 
		assertEquals(trajectory.getLast(), tail); 
	}
	
	@Test
	public void testGetLast2(){
		ISTTrackingEvent head = mock(ISTTrackingEvent.class); 
		ISTTrackingEvent tail2 = mock(ISTTrackingEvent.class);
		ISTTrackingEvent tail = mock(ISTTrackingEvent.class);
		Interval headPeriod = new Interval(6000, 8000); 
		Interval tailPeriod = new Interval(7000, 9000); 
		when(head.getTimePeriod()).thenReturn(headPeriod);
		when(tail.getTimePeriod()).thenReturn(tailPeriod); 
		GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(head, tail); 
		when(tail.getNext()).thenReturn(tail2); 
		assertEquals(trajectory.getLast(), tail2); 
	}
	
	@Test
	public void testSizeCorrect() {
		ISTTrackingEvent head = mock(ISTTrackingEvent.class); 
		ISTTrackingEvent tail = mock(ISTTrackingEvent.class);
		when(head.getNext()).thenReturn(tail); 
		when(tail.getPrevious()).thenReturn(head); 
		
		Interval headPeriod = new Interval(6000, 8000); 
		Interval tailPeriod = new Interval(8000, 10000); 
		when(head.getTimePeriod()).thenReturn(headPeriod);
		when(tail.getTimePeriod()).thenReturn(tailPeriod); 
		
		when(head.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
		when(tail.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);
		
		GeneralSTTrackingTrajectory trajectory = new GeneralSTTrackingTrajectory(head, tail); 
		assertEquals(2, trajectory.size());
	}
}