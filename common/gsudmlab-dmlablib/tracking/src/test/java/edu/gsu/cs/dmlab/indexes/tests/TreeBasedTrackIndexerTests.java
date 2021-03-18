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

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.indexes.TreeBasedTrackIndexer;

import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by thad on 11/19/15.
 */
public class TreeBasedTrackIndexerTests {

	@Test
	public void testTreeBasedConstructorThrowsOnNullList() throws IllegalArgumentException {

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(null, 1);
		});
	}

	@Test
	public void testTreeBasedConstructorCallsSuperWhichThrowsOnDivLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingTrajectory> lst = (ArrayList<ISTTrackingTrajectory>) mock(ArrayList.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(lst, 0);
		});
	}

	@Test
	public void testTreeBasedTrackSearchDoesNotReturnWhenNotIntersectingSpatialAR() throws IllegalArgumentException {
		// list for input to contructor of track indexer
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<>();

		// Duration dur = new Duration(0, 2000);

		// create track to index
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent evnt = mock(ISTTrackingEvent.class);
		lst.add(trk);
		;
		when(trk.getType()).thenReturn(EventType.ACTIVE_REGION);
		when(trk.getFirst()).thenReturn(evnt);

		Interval interval = new Interval(0, 5000);
		when(evnt.getTimePeriod()).thenReturn(interval);
		when(trk.getTimePeriod()).thenReturn(interval);
		when(evnt.getUUID()).thenReturn(new UUID(4, 2));
		when(trk.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(3, 1, 3, 1);
		when(evnt.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(new Envelope(2, 1, 3, 1));
		when(evnt.getGeometry()).thenReturn(geom);

		int regionDivisor = 1;
		TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(lst, regionDivisor);
		int[] xArr2 = { 4, 5, 5, 4 };
		int[] yArr2 = { 4, 4, 5, 5 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingTrajectory> retList = idxr.search(interval, poly);
		// System.out.println("testTreeBasedTrackSearchDoesNotReturnWhenNotIntersectingSpatialAR:
		// " + retList.size());
		assertTrue(retList.size() == 0);
	}

	public void testTreeBasedTrackSearchDoesReturnWhenIntersectingSpatialTemporalAR() throws IllegalArgumentException {
		// list for input to constructor
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<>();

		// create track to index
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent evnt = mock(ISTTrackingEvent.class);
		lst.add(trk);

		Interval itvl = new Interval(0, 5000);
		when(evnt.getTimePeriod()).thenReturn(itvl);
		when(trk.getTimePeriod()).thenReturn(itvl);
		when(evnt.getUUID()).thenReturn(new UUID(4, 2));
		when(trk.getUUID()).thenReturn(new UUID(4, 2));

		// Envelope envelope = new Envelope(3, 1, 3, 1);
		// when(evnt.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(new Envelope(2, 1, 3, 1));
		when(evnt.getGeometry()).thenReturn(geom);

		int regionDiv = 1;
		TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(lst, regionDiv);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);

		ArrayList<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		// System.out.println("testTreeBasedTrackSearchDoesReturnWhenIntersectingSpatialTemporalAR:
		// " + retList.size());
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testTreeBasedTrackSearchDoesReturnWhenIntersectingSpatialTemporalSG() throws IllegalArgumentException {

		// list for input to constructor of track indexer
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<ISTTrackingTrajectory>();

		// create track to index
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent evnt = mock(ISTTrackingEvent.class);
		lst.add(trk);
		when(trk.getType()).thenReturn(EventType.SIGMOID);
		when(trk.getFirst()).thenReturn(evnt);
		// event for the track

		// add interval to the event
		Interval itvl = new Interval(0, 5000);
		when(evnt.getTimePeriod()).thenReturn(itvl);
		when(trk.getTimePeriod()).thenReturn(itvl);
		when(evnt.getUUID()).thenReturn(new UUID(4, 2));
		when(trk.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(3, 1, 3, 1);
		when(evnt.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(new Envelope(2, 1, 3, 1));
		when(evnt.getGeometry()).thenReturn(geom);

		int regionDiv = 1;
		TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(lst, regionDiv);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		// System.out.println("testTreeBasedTrackSearchDoesReturnWhenIntersectingSpatialTemporalSG:
		// " + retList.size());
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testTreeBasedTrackSearchDoesNotReturnWhenIntersectingSpatialButNotTemporalAR()
			throws IllegalArgumentException {

		// list for input to constructor of track indexer
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<ISTTrackingTrajectory>();

		// create track to index
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent evnt = mock(ISTTrackingEvent.class);
		lst.add(trk);
		when(trk.getType()).thenReturn(EventType.ACTIVE_REGION);
		when(trk.getFirst()).thenReturn(evnt);
		// event for the track

		// add interval to the event
		Interval itvl = new Interval(0, 5000);
		when(evnt.getTimePeriod()).thenReturn(itvl);
		when(trk.getTimePeriod()).thenReturn(itvl);
		when(evnt.getUUID()).thenReturn(new UUID(4, 2));
		when(trk.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(3, 1, 3, 1);
		when(evnt.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(new Envelope(2, 1, 3, 1));
		when(evnt.getGeometry()).thenReturn(geom);

		int regionDiv = 1;
		TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(lst, regionDiv);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingTrajectory> retList = idxr.search(itvl2, poly);
		// System.out.println("testTreeBasedTrackSearchDoesNotReturnWhenIntersectingSpatialButNotTemporalAR:
		// " + retList.size());
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testTreeBasedTrackSearchDoesNotReturnWhenIntersectingSpatialButNotTemporalSG()
			throws IllegalArgumentException {

		// list for input to constructor of track indexer
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<ISTTrackingTrajectory>();

		// create track to index
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent evnt = mock(ISTTrackingEvent.class);
		lst.add(trk);
		when(trk.getType()).thenReturn(EventType.SIGMOID);
		when(trk.getFirst()).thenReturn(evnt);
		// event for the track

		// add interval to the event
		Interval itvl = new Interval(0, 5000);
		when(evnt.getTimePeriod()).thenReturn(itvl);
		when(trk.getTimePeriod()).thenReturn(itvl);
		when(evnt.getUUID()).thenReturn(new UUID(4, 2));
		when(trk.getUUID()).thenReturn(new UUID(4, 2));

		Envelope envelope = new Envelope(3, 1, 3, 1);
		when(evnt.getEnvelope()).thenReturn(envelope);
		GeometryFactory geoFact = new GeometryFactory();
		Geometry geom = geoFact.toGeometry(new Envelope(2, 1, 3, 1));
		when(evnt.getGeometry()).thenReturn(geom);

		int regionDiv = 1;
		TreeBasedTrackIndexer idxr = new TreeBasedTrackIndexer(lst, regionDiv);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		ArrayList<ISTTrackingTrajectory> retList = idxr.search(itvl2, poly);
		// System.out.println("testTreeBasedTrackSearchDoesNotReturnWhenIntersectingSpatialButNotTemporalSG:
		// " + retList.size());
		assertTrue(retList.size() == 0);
	}
}
