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
package edu.gsu.cs.dmlab.indexes.interfaces;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.geometry.GeometryUtilities;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Abstract class that implements some of the shared functionality of all the
 * indexes that implement the IIndexer interface.
 * 
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public abstract class AbsMatIndexer<T extends IBaseTemporalObject> implements IIndexer<T> {
	protected ArrayList<IBaseTemporalObject>[][] searchSpace;
	protected List<T> objectList;
	protected int regionDivisor;
	protected int regionDimension;

	protected class LockingArrayList<B> extends ArrayList<B> {

		private static final long serialVersionUID = 8161514146315487078L;

		Lock loc;

		public LockingArrayList() {
			super();
			this.loc = new ReentrantLock();
		}

		@Override
		public boolean add(B item) {
			boolean result = false;
			try {
				while (!this.loc.tryLock(100, TimeUnit.MILLISECONDS))
					;
				result = super.add(item);
				this.loc.unlock();
			} catch (InterruptedException e) {
				result = false;
			}
			return result;
		}

	}

	/**
	 * Constructor that constructs the basic matrix used to index the objects in the
	 * indexes that are subclasses of this class.
	 * 
	 * @param objectList      The list of objects that are to be indexed in this
	 *                        index.
	 * @param regionDimension The number of rows and columns that the underlying
	 *                        matrix used to index the list of objects shall have.
	 * @param regionDivisor   The value that is used to down size the objects that
	 *                        are to be indexed, so that they fit inside the scaled
	 *                        down space covered by the underlying matrix.
	 * @throws IllegalArgumentException Thrown when the object list is null, the
	 *                                  regionDimension is &lt; 1, or the
	 *                                  regionDivisor is &lt; 1.
	 */
	@SuppressWarnings("unchecked")
	public AbsMatIndexer(List<T> objectList, int regionDimension, int regionDivisor) throws IllegalArgumentException {
		if (objectList == null)
			throw new IllegalArgumentException("Object List cannot be null");
		if (regionDimension < 1)
			throw new IllegalArgumentException("Region Dimension cannot be less than 1");
		if (regionDivisor < 1)
			throw new IllegalArgumentException("Region Divisor cannot be less than 1");

		this.regionDimension = regionDimension;
		this.regionDivisor = regionDivisor;
		this.searchSpace = new ArrayList[regionDimension][regionDimension];
		for (int x = 0; x < this.regionDimension; x++) {
			for (int y = 0; y < this.regionDimension; y++) {
				this.searchSpace[x][y] = new LockingArrayList<IBaseTemporalObject>();
			}
		}
		this.objectList = new ArrayList<T>(objectList);
		this.sortList(this.objectList);
	}

	@Override
	public void finalize() throws Throwable {
		this.objectList.clear();
		this.objectList = null;

		IntStream.range(0, this.regionDimension * this.regionDimension).forEach(i -> {
			int x = i / this.regionDimension;
			int y = i % this.regionDimension;
			this.searchSpace[x][y].clear();
			this.searchSpace[x][y] = null;
		});

		this.searchSpace = null;
	}

	private void sortList(List<T> list) {
		list.sort((T o1, T o2) -> o1.compareTime(o2));
	}

	/**
	 * The function that indexes the passed in objects. Since each object is
	 * different in how it is indexed, this is implemented by each index
	 * implementation. This must be called before any spatial query will return the
	 * correct list of objects.
	 */
	protected abstract void buildIndex();

	@Override
	public List<T> getAll() {
		return new ArrayList<T>(this.objectList);
	}

	@Override
	public DateTime getFirstTime() {
		return objectList.get(0).getTimePeriod().getStart();
	}

	@Override
	public DateTime getLastTime() {
		return objectList.get(objectList.size() - 1).getTimePeriod().getEnd();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<T> search(Interval timePeriod, Geometry searchArea) {
		ConcurrentHashMap<UUID, IBaseTemporalObject> results = new ConcurrentHashMap<>();
		Geometry scaledSearchArea = GeometryUtilities.scaleGeometry(searchArea, this.regionDivisor);
		Envelope searchBoundingBox = scaledSearchArea.getEnvelopeInternal();

		GeometryFactory gf = new GeometryFactory();
		for (int x = (int) searchBoundingBox.getMinX(); x <= (int) searchBoundingBox.getMaxX(); x++) {
			for (int y = (int) searchBoundingBox.getMinY(); y <= (int) searchBoundingBox.getMaxY(); y++) {
				Point p = gf.createPoint(new Coordinate(x, y));
				if (scaledSearchArea.intersects(p) && (x < searchSpace.length && x > -1)
						&& (y < searchSpace[0].length && y > -1)) {
					for (IBaseTemporalObject object : searchSpace[x][y]) {
						if (object.getTimePeriod().overlaps(timePeriod)) {
							results.put(object.getUUID(), object);
						}
					}
				}
			}
		}

		ArrayList<IBaseTemporalObject> list = new ArrayList<IBaseTemporalObject>();
		results.forEach((id, ev) -> {
			list.add(ev);
		});
		return (ArrayList<T>) list;
	}

}