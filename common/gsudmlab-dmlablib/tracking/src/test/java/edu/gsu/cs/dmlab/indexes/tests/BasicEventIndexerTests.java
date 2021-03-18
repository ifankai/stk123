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
package edu.gsu.cs.dmlab.indexes.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.UUID;

import org.joda.time.Duration;
import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.indexes.BasicEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.AbsMatIndexer;

/**
 * Created by Dustin on 10/28/15.
 */

public class BasicEventIndexerTests {

	@Test
	public void testConstructorCallsSuperWhichThrowsOnNullList() throws IllegalArgumentException {
		Duration dur = new Duration(0, 2000);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(null, 1, 1, dur, -1);
		});
	}

	@Test
	public void testConstructorCallsSuperWhichThrowsOnDimLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingEvent> lst = (ArrayList<ISTTrackingEvent>) mock(ArrayList.class);
		Duration dur = new Duration(0, 2000);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, 0, 1, dur, -1);
		});
	}

	@Test
	public void testConstructorCallsSuperWhichThrowsOnDivLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingEvent> lst = (ArrayList<ISTTrackingEvent>) mock(ArrayList.class);
		Duration dur = new Duration(0, 2000);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, 1, 0, dur, -1);
		});
	}

	@Test
	public void testConstructorThrowsOnNullFrameSpan() throws IllegalArgumentException {

		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, 1, 1, null, -1);
		});
	}

	@Test
	public void testConstructorThrowsOnNumThreadsLessThanNegOne() throws IllegalArgumentException {

		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();
		Duration dur = new Duration(0, 2000);
		int numThreads = -2;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, 1, 1, dur, numThreads);
		});
	}

	@Test
	public void testConstructorThrowsOnNumThreadsZero() throws IllegalArgumentException {

		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();
		Duration dur = new Duration(0, 2000);
		int numThreads = -0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, 1, 1, dur, numThreads);
		});
	}

	@Test
	public void testConstructorThrowsOnShortFrameSpan() throws IllegalArgumentException {

		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();
		Duration dur = new Duration(0, 0);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, 1, 1, dur, -1);
		});
	}

	@Test
	public void testBasicEventSearchDoesNotReturnWhenNotIntersectingSpatial() throws IllegalArgumentException {
		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();

		Duration dur = new Duration(0, 2000);

		// add event to index
		ISTTrackingEvent obj = mock(ISTTrackingEvent.class);
		ISTTrackingEvent obj2 = mock(ISTTrackingEvent.class);
		lst.add(obj);
		lst.add(obj2);

		// add interval to the object
		Interval itvl = new Interval(0, 5000);
		when(obj.getTimePeriod()).thenReturn(itvl);
		when(obj.getUUID()).thenReturn(new UUID(4, 2));

		Interval itvl2 = new Interval(5001, 10000);
		when(obj2.getTimePeriod()).thenReturn(itvl2);
		when(obj2.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(1, 3, 1, 3);
		when(obj.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(envelope);
		when(obj.getGeometry()).thenReturn(geom);
		when(obj2.getGeometry()).thenReturn(geom);

		int regionDim = 4;
		int regionDiv = 1;
		AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, regionDim, regionDiv, dur, -1);

		int[] xArr2 = { 4, 5, 5, 4 };
		int[] yArr2 = { 4, 4, 5, 5 };

		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingEvent> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testBasicEventSearchDoesReturnWhenIntersectingSpatialTemporal() throws IllegalArgumentException {
		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();

		Duration dur = new Duration(2000);

		// add event to index
		ISTTrackingEvent obj = mock(ISTTrackingEvent.class);
		ISTTrackingEvent obj2 = mock(ISTTrackingEvent.class);
		lst.add(obj);
		lst.add(obj2);

		// add interval to the object
		Interval itvl = new Interval(0, 5000);
		when(obj.getTimePeriod()).thenReturn(itvl);
		when(obj.getUUID()).thenReturn(new UUID(4, 2));

		Interval itvl2 = new Interval(5001, 10000);
		when(obj2.getTimePeriod()).thenReturn(itvl2);
		when(obj2.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(3, 1, 3, 1);
		when(obj.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(envelope);
		when(obj.getGeometry()).thenReturn(geom);
		when(obj2.getGeometry()).thenReturn(geom);

		int regionDim = 4;
		int regionDiv = 1;
		AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, regionDim, regionDiv, dur, -1);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };

		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingEvent> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 1);
		assertTrue(retList.get(0) == obj);

	}

	@Test
	public void testBasicEventSearchDoesNotReturnWhenIntersectingSpatialButNotTemporal()
			throws IllegalArgumentException {
		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();

		Duration dur = new Duration(2000);

		// add event to index
		ISTTrackingEvent obj = mock(ISTTrackingEvent.class);
		lst.add(obj);

		// add interval to the object
		Interval itvl = new Interval(0, 5000);
		when(obj.getTimePeriod()).thenReturn(itvl);
		when(obj.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(3, 1, 3, 1);
		when(obj.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(new Envelope(3, 1, 3, 1));
		when(obj.getGeometry()).thenReturn(geom);

		int regionDim = 4;
		int regionDiv = 1;
		AbsMatIndexer<ISTTrackingEvent> idxr = new BasicEventIndexer(lst, regionDim, regionDiv, dur, -1);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };

		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingEvent> retList = idxr.search(itvl2, poly);
		assertTrue(retList.size() == 0);

	}

}
