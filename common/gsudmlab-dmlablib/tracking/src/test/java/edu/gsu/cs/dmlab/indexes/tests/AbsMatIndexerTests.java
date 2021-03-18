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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.UUID;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.indexes.interfaces.AbsMatIndexer;

/**
 *
 * @author Dustin Kempton
 * 
 *
 */

public class AbsMatIndexerTests {

	protected class FakeAbsMatIndexer extends AbsMatIndexer<IBaseTemporalObject> {

		public FakeAbsMatIndexer(ArrayList<IBaseTemporalObject> objectList, int regionDimension, int regionDiv)
				throws IllegalArgumentException {
			super(objectList, regionDimension, regionDiv);

		}

		public void setSearchSpace(Envelope rect, IBaseTemporalObject data) {
			for (int x = (int) rect.getMinX(); x < rect.getMaxX(); x++) {
				for (int y = (int) rect.getMinY(); y < rect.getMaxY(); y++) {
					super.searchSpace[x][y].add(data);
				}
			}
		}

		@Override
		protected void buildIndex() {
			// Auto-generated method stub

		}

	}

	@Test
	public void testConstructorThrowsOnNullList() throws IllegalArgumentException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<IBaseTemporalObject> idxr = new FakeAbsMatIndexer(null, 1, 1);
		});
	}

	@Test
	public void testConstructorThrowsOnDimLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<IBaseTemporalObject> lst = (ArrayList<IBaseTemporalObject>) mock(ArrayList.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<IBaseTemporalObject> idxr = new FakeAbsMatIndexer(lst, 0, 1);
		});
	}

	@Test
	public void testConstructorThrowsOnDivLessThanOne() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		ArrayList<IBaseTemporalObject> lst = (ArrayList<IBaseTemporalObject>) mock(ArrayList.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			AbsMatIndexer<IBaseTemporalObject> idxr = new FakeAbsMatIndexer(lst, 1, 0);
		});
	}

	@Test
	public void testGetFirstTime() throws IllegalArgumentException {

		ArrayList<IBaseTemporalObject> lst = new ArrayList<IBaseTemporalObject>();

		IBaseTemporalObject obj = mock(IBaseTemporalObject.class);
		lst.add(obj);

		AbsMatIndexer<IBaseTemporalObject> idxr = new FakeAbsMatIndexer(lst, 1, 1);

		Interval itvl = new Interval(0, 500);
		when(obj.getTimePeriod()).thenReturn(itvl);

		assertTrue(itvl.contains(idxr.getFirstTime()));

	}

	@Test
	public void testGetLastTime() throws IllegalArgumentException {

		ArrayList<IBaseTemporalObject> lst = new ArrayList<IBaseTemporalObject>();

		IBaseTemporalObject obj = mock(IBaseTemporalObject.class);
		lst.add(obj);

		AbsMatIndexer<IBaseTemporalObject> idxr = new FakeAbsMatIndexer(lst, 1, 1);

		Interval itvl = new Interval(0, 500);
		when(obj.getTimePeriod()).thenReturn(itvl);

		assertTrue(itvl.contains(idxr.getFirstTime()));

	}

	@Test
	public void testGetAll() throws IllegalArgumentException {

		ArrayList<IBaseTemporalObject> lst = new ArrayList<IBaseTemporalObject>();

		AbsMatIndexer<IBaseTemporalObject> idxr = new FakeAbsMatIndexer(lst, 1, 1);
		assertEquals(lst, idxr.getAll());

	}

	@Test
	public void testSearchReturnWhenIntersects() throws IllegalArgumentException {
		ArrayList<IBaseTemporalObject> lst = new ArrayList<IBaseTemporalObject>();

		FakeAbsMatIndexer idxr = new FakeAbsMatIndexer(lst, 4, 1);

		IBaseTemporalObject obj = mock(IBaseTemporalObject.class);
		IBaseTemporalObject obj2 = mock(IBaseTemporalObject.class);
		when(obj.getUUID()).thenReturn(new UUID(4, 2));
		when(obj2.getUUID()).thenReturn(new UUID(4, 2));

		Interval itvl = new Interval(0, 500);
		Interval itvl2 = new Interval(501, 1000);
		when(obj.getTimePeriod()).thenReturn(itvl);
		when(obj2.getTimePeriod()).thenReturn(itvl2);

		Envelope rect = new Envelope(1, 2, 1, 2);

		int[] xArr2 = { 1, 3, 3, 1 };
		int[] yArr2 = { 1, 1, 3, 3 };

		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);

		// adds to index so we can get it back out
		idxr.setSearchSpace(rect, obj);
		idxr.setSearchSpace(rect, obj2);

		ArrayList<IBaseTemporalObject> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 1);

	}

	@Test
	public void testSearchDoesNotReturnWhenNotIntersectingSpatial() throws IllegalArgumentException {
		ArrayList<IBaseTemporalObject> lst = new ArrayList<IBaseTemporalObject>();

		FakeAbsMatIndexer idxr = new FakeAbsMatIndexer(lst, 4, 1);

		IBaseTemporalObject obj = mock(IBaseTemporalObject.class);
		when(obj.getUUID()).thenReturn(new UUID(4, 2));

		Interval itvl = new Interval(0, 500);
		when(obj.getTimePeriod()).thenReturn(itvl);

		Envelope rect = new Envelope(1, 2, 1, 2);

		// adds to index so we can get it back out
		idxr.setSearchSpace(rect, obj);

		int[] xArr2 = { 3, 4, 4, 3 };
		int[] yArr2 = { 3, 3, 4, 4 };

		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);

		ArrayList<IBaseTemporalObject> retList = idxr.search(itvl, poly);
		assertTrue(retList.size() == 0);

	}

	@Test
	public void testSearchDoesNotReturnWhenNotIntersectingTemporal() throws IllegalArgumentException {
		ArrayList<IBaseTemporalObject> lst = new ArrayList<IBaseTemporalObject>();

		FakeAbsMatIndexer idxr = new FakeAbsMatIndexer(lst, 4, 1);

		IBaseTemporalObject obj = mock(IBaseTemporalObject.class);
		when(obj.getUUID()).thenReturn(new UUID(4, 2));

		Interval itvl = new Interval(0, 500);
		when(obj.getTimePeriod()).thenReturn(itvl);

		Envelope rect = new Envelope(1, 2, 1, 2);

		// adds to index so we can get it back out
		idxr.setSearchSpace(rect, obj);

		Interval itvl2 = new Interval(600, 700);
		int[] xArr2 = { 1, 3, 3, 1 };
		int[] yArr2 = { 1, 1, 3, 3 };

		Coordinate[] coords = new Coordinate[5];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(xArr2[i % xArr2.length], yArr2[i % yArr2.length]);
		}

		GeometryFactory gf = new GeometryFactory();
		Geometry poly = gf.createLinearRing(coords);

		ArrayList<IBaseTemporalObject> retList = idxr.search(itvl2, poly);
		assertTrue(retList.size() == 0);

	}
}
