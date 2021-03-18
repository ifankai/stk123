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
package edu.gsu.cs.dmlab.tracking.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.BinomialFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class BinomialFrameSkippModelTests {

	@Test  
	public void testFrameSkipModelThrowsOnNullExitProbCalculator() throws IllegalArgumentException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			BinomialFrameSkipModel model = new BinomialFrameSkipModel(null);
		});
	}  

	@Test
	public void testFrameSkipModelThrowsOnNullLeftTrack() throws IllegalArgumentException {

		

		ISTLocationProbCal exitProbCalculator = mock(ISTLocationProbCal.class);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		BinomialFrameSkipModel model = new BinomialFrameSkipModel(exitProbCalculator);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			model.getSkipProb(null, trk);
		});
	}

	@Test
	public void testFrameSkipModelThrowsOnNullRightTrack() throws IllegalArgumentException {

		

		ISTLocationProbCal exitProbCalculator = mock(ISTLocationProbCal.class);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		BinomialFrameSkipModel model = new BinomialFrameSkipModel(exitProbCalculator);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			model.getSkipProb(trk, null);
		});
	}

	@Test
	public void testFrameSkipModelReturnsCorrectValueWhenGapGreaterThanZero() {

		// Setup left track
		DateTime start1 = new DateTime(2012, 1, 1, 0, 0, 0);
		DateTime end1 = new DateTime(2012, 1, 1, 0, 0, 1);
		Interval timePeriod1 = new Interval(start1, end1);
		ISTTrackingTrajectory leftTrk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent leftEvent = mock(ISTTrackingEvent.class);
		when(leftTrk.getLast()).thenReturn(leftEvent);
		when(leftEvent.getTimePeriod()).thenReturn(timePeriod1);

		// Setup right track
		DateTime start2 = new DateTime(2012, 1, 1, 0, 0, 6);
		DateTime end2 = new DateTime(2012, 1, 2, 0, 0, 7);
		Interval timePeriod2 = new Interval(start2, end2);
		ISTTrackingTrajectory rightTrk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent rightEvent = mock(ISTTrackingEvent.class);
		when(rightTrk.getFirst()).thenReturn(rightEvent);
		when(rightEvent.getTimePeriod()).thenReturn(timePeriod2);

		// Setup prob calculator

		

		ISTLocationProbCal exitProbCalculator = mock(ISTLocationProbCal.class);
		when(exitProbCalculator.calcProb(any(ISTTrackingEvent.class))).thenReturn(0.7);

		BinomialFrameSkipModel model = new BinomialFrameSkipModel(exitProbCalculator);
		double val = model.getSkipProb(leftTrk, rightTrk);
		assertTrue(Math.abs(val - 0.0102) <= 0.0001);
	}

	@Test
	public void testFrameSkipModelReturnsCorrectValueWhenGapZero() {

		// Setup left track
		DateTime start1 = new DateTime(2012, 1, 1, 0, 0, 0);
		DateTime end1 = new DateTime(2012, 1, 1, 0, 0, 1);
		Interval timePeriod1 = new Interval(start1, end1);
		ISTTrackingTrajectory leftTrk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent leftEvent = mock(ISTTrackingEvent.class);
		when(leftTrk.getLast()).thenReturn(leftEvent);
		when(leftEvent.getTimePeriod()).thenReturn(timePeriod1);

		// Setup right track
		DateTime start2 = new DateTime(2012, 1, 1, 0, 0, 1);
		DateTime end2 = new DateTime(2012, 1, 2, 0, 0, 2);
		Interval timePeriod2 = new Interval(start2, end2);
		ISTTrackingTrajectory rightTrk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent rightEvent = mock(ISTTrackingEvent.class);
		when(rightTrk.getFirst()).thenReturn(rightEvent);
		when(rightEvent.getTimePeriod()).thenReturn(timePeriod2);

		// Setup prob calculator

		

		ISTLocationProbCal exitProbCalculator = mock(ISTLocationProbCal.class);

		when(exitProbCalculator.calcProb(any(ISTTrackingEvent.class))).thenReturn(0.7);

		BinomialFrameSkipModel model = new BinomialFrameSkipModel(exitProbCalculator);
		double val = model.getSkipProb(leftTrk, rightTrk);
		assertTrue(val == 1.0);
	}
}
