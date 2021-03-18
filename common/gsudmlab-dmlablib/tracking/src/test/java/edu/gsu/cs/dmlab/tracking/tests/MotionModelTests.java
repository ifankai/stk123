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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.MotionModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTMotionModel;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class MotionModelTests {
	@Test
	public void testMotionModelThrowsOnNullLeftTrack() throws IllegalArgumentException {
		ISTMotionModel model = new MotionModel();
		ISTTrackingTrajectory nonNullTrk = mock(ISTTrackingTrajectory.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			model.calcProbMotion(null, nonNullTrk);
		});
	}

	@Test
	public void testMotionModelThrowsOnNullRightTrack() throws IllegalArgumentException {
		ISTMotionModel model = new MotionModel();
		ISTTrackingTrajectory nonNullTrk = mock(ISTTrackingTrajectory.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			model.calcProbMotion(nonNullTrk, null);
		});
	}

	@Test
	public void testMotionModelReturnsOneOnPerfectMatchLengthTwo() {
		ISTMotionModel model = new MotionModel();
		GeometryFactory gf = new GeometryFactory();
		// point1 = new Point();
		double x = 1.0;
		double y = 1.0;

		Point p = gf.createPoint(new Coordinate(x, y));
		ISTTrackingEvent ev1 = mock(ISTTrackingEvent.class);
		when(ev1.getCentroid()).thenReturn(p);

		double a = 2.0;
		double b = 2.0;
		Point point2 = gf.createPoint(new Coordinate(a, b));
		ISTTrackingEvent ev2 = mock(ISTTrackingEvent.class);
		when(ev2.getCentroid()).thenReturn(point2);
		when(ev1.getNext()).thenReturn(ev2);

		ISTTrackingTrajectory track = mock(ISTTrackingTrajectory.class);
		when(track.getFirst()).thenReturn(ev1);
		double val = model.calcProbMotion(track, track);
		assertTrue(val == 1.0);
	}

	@Test
	public void testMotionModelReturnsOneOnBothLengthOne() {
		ISTMotionModel model = new MotionModel();
		GeometryFactory gf = new GeometryFactory();

		double x = 1.0;
		double y = 1.0;
		Point point1 = gf.createPoint(new Coordinate(x, y));
		ISTTrackingEvent ev1 = mock(ISTTrackingEvent.class);
		when(ev1.getCentroid()).thenReturn(point1);

		ISTTrackingTrajectory track = mock(ISTTrackingTrajectory.class);
		when(track.getFirst()).thenReturn(ev1);
		double val = model.calcProbMotion(track, track);
		assertTrue(val == 1.0);
	}

	@Test
	public void testMotionModelReturnsOneOnPerfectMatchMismatchLength() {
		ISTMotionModel model = new MotionModel();
		GeometryFactory gf = new GeometryFactory();

		double x = 1.0;
		double y = 2.0;

		Point point1 = gf.createPoint(new Coordinate(x, y));
		ISTTrackingEvent ev1 = mock(ISTTrackingEvent.class);
		when(ev1.getCentroid()).thenReturn(point1);

		double a = 2.0;
		double b = 3.0;
		Point point2 = gf.createPoint(new Coordinate(a, b));
		ISTTrackingEvent ev2 = mock(ISTTrackingEvent.class);
		when(ev2.getCentroid()).thenReturn(point2);
		when(ev1.getNext()).thenReturn(ev2);

		double m = 3.0;
		double n = 4.0;
		Point point3 = gf.createPoint(new Coordinate(m, n));
		ISTTrackingEvent ev3 = mock(ISTTrackingEvent.class);
		when(ev3.getCentroid()).thenReturn(point3);
		when(ev2.getNext()).thenReturn(ev3);

		double p = 1.0;
		double q = 2.0;
		Point point4 = gf.createPoint(new Coordinate(p, q));
		ISTTrackingEvent ev4 = mock(ISTTrackingEvent.class);
		when(ev4.getCentroid()).thenReturn(point4);

		double u = 2.0;
		double v = 3.0;
		Point point5 = gf.createPoint(new Coordinate(u, v));
		ISTTrackingEvent ev5 = mock(ISTTrackingEvent.class);
		when(ev5.getCentroid()).thenReturn(point5);
		when(ev4.getNext()).thenReturn(ev5);

		ISTTrackingTrajectory track1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory track2 = mock(ISTTrackingTrajectory.class);
		when(track1.getFirst()).thenReturn(ev1);
		when(track2.getFirst()).thenReturn(ev4);

		double val = model.calcProbMotion(track1, track2);
		assertTrue(val == 1.0);
	}
}
