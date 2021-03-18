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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.GeneralSTInterpolationTrajectory;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;

/**
 * 
 * @author Michael Tinglof 6/17/19
 *
 */

class GeneralSTInterpolationTrajectoryTests {

	@Test
	public void testConstructorThrowsOnNullList() throws IllegalArgumentException{
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GeneralSTInterpolationTrajectory trajectory = new GeneralSTInterpolationTrajectory(null); 
		}); 
	}
	
	@Test
	public void testIsInterpolated() throws Exception{
		ISTInterpolationEvent event1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent event2 = mock(ISTInterpolationEvent.class); 
		List<ISTInterpolationEvent> events = new ArrayList<ISTInterpolationEvent>(2); 
		events.add(event1); 
		events.add(event2); 
		when(event1.isInterpolated()).thenReturn(true); 
		when(event2.isInterpolated()).thenReturn(false); 
		GeneralSTInterpolationTrajectory trajectory = new GeneralSTInterpolationTrajectory(events); 
		assertEquals(trajectory.isInterpolated(), true); 
	}
	
	@Test
	public void testIsInterpolated2() throws Exception{
		ISTInterpolationEvent event1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent event2 = mock(ISTInterpolationEvent.class); 
		List<ISTInterpolationEvent> events = new ArrayList<ISTInterpolationEvent>(2); 
		events.add(event1); 
		events.add(event2); 
		when(event1.isInterpolated()).thenReturn(false); 
		when(event2.isInterpolated()).thenReturn(false); 
		GeneralSTInterpolationTrajectory trajectory = new GeneralSTInterpolationTrajectory(events); 
		assertEquals(trajectory.isInterpolated(), false);
	}
	
	@Test
	public void testGetType() throws Exception{
		ISTInterpolationEvent event1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent event2 = mock(ISTInterpolationEvent.class); 
		List<ISTInterpolationEvent> events = new ArrayList<ISTInterpolationEvent>(2); 
		events.add(event1); 
		events.add(event2); 
		when(event1.getType()).thenReturn(EventType.ACTIVE_REGION); 
		GeneralSTInterpolationTrajectory trajectory = new GeneralSTInterpolationTrajectory(events); 
		assertEquals(trajectory.getType(), EventType.ACTIVE_REGION);
	}
}
