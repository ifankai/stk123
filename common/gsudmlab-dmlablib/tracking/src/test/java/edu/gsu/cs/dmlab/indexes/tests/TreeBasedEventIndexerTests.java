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

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.indexes.TreeBasedEventIndexer;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by thad on 11/20/15.
 */
public class TreeBasedEventIndexerTests {

	@Test
	public void testConstructorCallsThrowsOnNullList() throws IllegalArgumentException {
		Duration dur = new Duration(0, 2000);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			TreeBasedEventIndexer idxr = new TreeBasedEventIndexer(null, 1, dur);
		});
	}

	@Test
	public void testConstructorThrowsOnDivLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingEvent> lst = (ArrayList<ISTTrackingEvent>) mock(ArrayList.class);
		Duration duration = new Duration(0, 2000);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			TreeBasedEventIndexer idxr = new TreeBasedEventIndexer(lst, 0, duration);
		});
	}

	@Test
	public void testConstructorThrowsOnNullFrameSpan() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingEvent> lst = (ArrayList<ISTTrackingEvent>) mock(ArrayList.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			TreeBasedEventIndexer idxr = new TreeBasedEventIndexer(lst, 1, null);
		});
	}

	@Test
	public void testTreeBasedEventSearchDoesNotReturnWhenNotIntersectingSpatial() throws IllegalArgumentException {
		ArrayList<ISTTrackingEvent> lst = new ArrayList<ISTTrackingEvent>();

		Duration dur = new Duration(0, 2000);
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
		Geometry geom = geoFact.toGeometry(new Envelope(2, 1, 3, 1));
		when(obj.getGeometry()).thenReturn(geom);

		int regionDiv = 1;
		TreeBasedEventIndexer idxr = new TreeBasedEventIndexer(lst, regionDiv, dur);
		int[] xArr2 = { 13, 14, 14, 13 };
		int[] yArr2 = { 13, 13, 14, 14 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingEvent> retList = idxr.search(itvl, poly);
		// System.out.println("testTreeBasedEventSearchDoesNotReturnWhenNotIntersectingSpatial:
		// " + retList.size());
		assertTrue(retList.size() == 0);
	}

	@Test
	public void testTreeBasedSearchDoesReturnWhenIntersectingSpatialTemporal() throws IllegalArgumentException {
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

		int regionDiv = 1;

		TreeBasedEventIndexer idxr = new TreeBasedEventIndexer(lst, regionDiv, dur);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingEvent> retList = idxr.search(itvl, poly);
		// System.out.println("testTreeBasedSearchDoesReturnWhenIntersectingSpatialTemporal:
		// " + retList.size());
		assertTrue(retList.size() == 1);
	}

	@Test
	public void testTreeBasedEventSearchDoesNotReturnWhenIntersectingSpatialButNotTemporal()
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

		int regionDiv = 1;
		TreeBasedEventIndexer idxr = new TreeBasedEventIndexer(lst, regionDiv, dur);
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
		// System.out.println("testTreeBasedEventSearchDoesNotReturnWhenIntersectingSpatialButNotTemporal:
		// " + retList.size());
		assertTrue(retList.size() == 0);

	}

}
