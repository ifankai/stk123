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
 * 
 * This code is based on code in the javaml library https://github.com/AbeelLab/javaml
 */
package edu.gsu.cs.dmlab.distance.dtw.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;

/**
 * Class that holds the warping information for matching points in one series to
 * points in another series. Note that there is no guarantee that all points in
 * either series will have a match in the other.
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SeriesWarpPath implements IAlignmentPath {

	private final List<Integer> tsIindexes;
	private final List<Integer> tsJindexes;
	private final Map<Integer, List<Integer>> tsIMap;
	private final Map<Integer, List<Integer>> tsJMap;
	private boolean upToDateI = false;
	private boolean upToDateJ = false;
	private Lock loc;

	public SeriesWarpPath() {
		this.tsIindexes = new LinkedList<Integer>();
		this.tsJindexes = new LinkedList<Integer>();
		this.tsIMap = new HashMap<Integer, List<Integer>>();
		this.tsJMap = new HashMap<Integer, List<Integer>>();
		this.loc = new ReentrantLock();
	}

	@Override
	public int size() {
		return this.tsIindexes.size();
	}

	@Override
	public int minI() {
		return this.tsIindexes.get(0).intValue();
	}

	@Override
	public int minJ() {
		return this.tsJindexes.get(0).intValue();
	}

	@Override
	public int maxI() {
		return this.tsIindexes.get(this.tsIindexes.size() - 1).intValue();
	}

	@Override
	public int maxJ() {
		return this.tsJindexes.get(this.tsJindexes.size() - 1).intValue();
	}

	@Override
	public void addFirst(int i, int j) {
		this.loc.lock();
		try {
			if (this.tsIindexes.isEmpty() || (i <= this.tsIindexes.get(0).intValue() && j <= this.tsJindexes.get(0))) {
				this.tsIindexes.add(0, Integer.valueOf(i));
				this.tsJindexes.add(0, Integer.valueOf(j));
				this.upToDateJ = false;
				this.upToDateI = false;
			} else {
				throw new IllegalArgumentException("Arguments i,j must not cross previous entry.");
			}
		} finally {
			this.loc.unlock();
		}
	}

	@Override
	public void addLast(int i, int j) {
		this.loc.lock();
		try {
			if (this.tsIindexes.isEmpty() || (i >= this.tsIindexes.get(this.tsIindexes.size() - 1).intValue()
					&& j >= this.tsJindexes.get(this.size() - 1))) {
				this.tsIindexes.add(Integer.valueOf(i));
				this.tsJindexes.add(Integer.valueOf(j));
				this.upToDateJ = false;
				this.upToDateI = false;
			} else {
				throw new IllegalArgumentException("Arguments i,j must not cross last entry.");
			}
		} finally {
			this.loc.unlock();
		}
	}

	@Override
	public List<Integer> getMatchingIndexesForI(int i) {
		ArrayList<Integer> matchingJs;
		this.loc.lock();
		try {
			if (!this.upToDateI) {
				this.tsIMap.clear();
				// Use iterator because using get(idx) on a linked list will be a n^2 operation
				// when processing all elements in the list
				Iterator<Integer> iIterator = this.tsIindexes.iterator();
				Iterator<Integer> jIterator = this.tsJindexes.iterator();
				while (iIterator.hasNext() && jIterator.hasNext()) {
					Integer val = iIterator.next();
					Integer mVal = jIterator.next();
					if (this.tsIMap.containsKey(val)) {
						this.tsIMap.get(val).add(mVal);
					} else {
						List<Integer> lst = new ArrayList<Integer>();
						lst.add(mVal);
						this.tsIMap.put(val, lst);
					}
				}
				this.upToDateI = true;
			}

			List<Integer> mapVals = this.tsIMap.get(Integer.valueOf(i));
			if (mapVals == null)
				throw new InternalError("ERROR:  index '" + i + " is not in the " + "warp path.");
			matchingJs = new ArrayList<Integer>(mapVals);
		} finally {
			this.loc.unlock();
		}
		return matchingJs;
	}

	@Override
	public List<Integer> getMatchingIndexesForJ(int j) {
		ArrayList<Integer> matchingIs;
		this.loc.lock();
		try {
			if (!this.upToDateJ) {
				this.tsJMap.clear();
				// Use iterator because using get(idx) on a linked list will be a n^2 operation
				// when processing all elements in the list
				Iterator<Integer> iIterator = this.tsIindexes.iterator();
				Iterator<Integer> jIterator = this.tsJindexes.iterator();
				while (iIterator.hasNext() && jIterator.hasNext()) {
					Integer val = jIterator.next();
					Integer mVal = iIterator.next();
					if (this.tsJMap.containsKey(val)) {
						this.tsJMap.get(val).add(mVal);
					} else {
						List<Integer> lst = new ArrayList<Integer>();
						lst.add(mVal);
						this.tsJMap.put(val, lst);
					}
				}
				this.upToDateJ = true;
			}

			List<Integer> mapVals = this.tsJMap.get(Integer.valueOf(j));
			if (mapVals == null)
				throw new InternalError("ERROR:  index '" + j + " is not in the " + "warp path.");
			matchingIs = new ArrayList<Integer>(mapVals);
		} finally {
			this.loc.unlock();
		}
		return matchingIs;
	}

	@Override
	public ColMajorCell get(int index) {
		if (index > this.size() || index < 0)
			throw new NoSuchElementException();
		else
			return new ColMajorCell(this.tsIindexes.get(index).intValue(), this.tsJindexes.get(index).intValue());
	}

	@Override
	public Iterator<ColMajorCell> getMapping() {
		return new PathIterator(this.tsIindexes, this.tsJindexes);
	}

	private final class PathIterator implements Iterator<ColMajorCell> {

		private final Iterator<Integer> iIterator;
		private final Iterator<Integer> jIterator;

		PathIterator(List<Integer> tsIIndexes, List<Integer> tsJIndexes) {
			this.iIterator = tsIIndexes.iterator();
			this.jIterator = tsJIndexes.iterator();
		}

		@Override
		public boolean hasNext() {
			return (this.iIterator.hasNext() && this.jIterator.hasNext());
		}

		@Override
		public ColMajorCell next() {
			return new ColMajorCell(this.iIterator.next().intValue(), this.jIterator.next().intValue());
		}
	}

}