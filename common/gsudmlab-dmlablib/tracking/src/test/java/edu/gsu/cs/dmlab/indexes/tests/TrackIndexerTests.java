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

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.indexes.BasicTrackIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;

import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 */

public class TrackIndexerTests {

	@Test
	public void testFilterOnIntervalAndLocationDoesNotReturnWhenNotIntersectingSpatialAR()
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

		int regionDim = 5;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 4, 5, 5, 4 };
		int[] yArr2 = { 4, 4, 5, 5 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testFilterOnIntervalAndLocationDoesNotReturnWhenNotIntersectingSpatialSG()
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

		int regionDim = 5;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 4, 5, 5, 4 };
		int[] yArr2 = { 4, 4, 5, 5 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testFilterOnIntervalAndLocationDoesReturnWhenIntersectingSpatialTemporalAR()
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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testFilterOnIntervalAndLocationDoesReturnWhenIntersectingSpatialTemporalSG()
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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testFilterOnIntervalAndLocationDoesNotReturnWhenIntersectingSpatialButNotTemporalAR()
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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl2, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testFilterOnIntervalAndLocationDoesNotReturnWhenIntersectingSpatialButNotTemporalSG()
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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl2, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testConstructorCallsSuperWhichThrowsOnNullList() throws IllegalArgumentException {

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(null, 1, 1, -1);
		});
	}

	@Test
	public void testConstructorCallsSuperWhichThrowsOnDimLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingTrajectory> lst = (ArrayList<ISTTrackingTrajectory>) mock(ArrayList.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, 0, 1, -1);
		});
	}

	@Test
	public void testConstructorCallsSuperWhichThrowsOnDivLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<ISTTrackingTrajectory> lst = (ArrayList<ISTTrackingTrajectory>) mock(ArrayList.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, 1, 0, -1);
		});
	}

	@Test
	public void testBasicTrackSearchDoesNotReturnWhenNotIntersectingSpatialAR() throws IllegalArgumentException {

		// list for input to constructor of track indexer
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<ISTTrackingTrajectory>();

		// Duration dur = new Duration(0, 2000);

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

		int regionDim = 5;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 4, 5, 5, 4 };
		int[] yArr2 = { 4, 4, 5, 5 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testBasicTrackSearchDoesNotReturnWhenNotIntersectingSpatialSG() throws IllegalArgumentException {

		// list for input to constructor of track indexer
		ArrayList<ISTTrackingTrajectory> lst = new ArrayList<ISTTrackingTrajectory>();

		// Duration dur = new Duration(0, 2000);

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

		int regionDim = 5;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 4, 5, 5, 4 };
		int[] yArr2 = { 4, 4, 5, 5 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testBasicTrackSearchDoesReturnWhenIntersectingSpatialTemporalAR() throws IllegalArgumentException {

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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testBasicTrackSearchDoesReturnWhenIntersectingSpatialTemporalSG() throws IllegalArgumentException {

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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testBasicTrackSearchDoesNotReturnWhenIntersectingSpatialButNotTemporalAR()
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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl2, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testBasicTrackSearchDoesNotReturnWhenIntersectingSpatialButNotTemporalSG()
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

		int regionDim = 4;
		int regionDiv = 1;
		ISTTrackingTrajectoryIndexer idxr = new BasicTrackIndexer(lst, regionDim, regionDiv, -1);

		Interval itvl2 = new Interval(6000, 7000);
		int[] xArr2 = { 1, 2, 2, 1 };
		int[] yArr2 = { 1, 1, 2, 2 };
		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);
		List<ISTTrackingTrajectory> retList = idxr.search(itvl2, poly);
		assertTrue(retList.size() == 0);

	}
}
