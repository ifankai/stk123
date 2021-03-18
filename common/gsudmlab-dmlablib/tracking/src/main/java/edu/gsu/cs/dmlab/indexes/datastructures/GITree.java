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
package edu.gsu.cs.dmlab.indexes.datastructures;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.strtree.SIRtree;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;

import org.joda.time.DateTime;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public class GITree<T extends IBaseTemporalObject> {
	Map<Coordinate, SIRtree> grid;
	private int deltaX;
	private int deltaY;

	public GITree() {
		this.deltaX = 4;
		this.deltaY = 4;
		grid = new ConcurrentHashMap<Coordinate, SIRtree>();
	}

	public void insert(T element, long startTime, long endTime, Envelope mbr) {
		HashSet<Coordinate> cells = this.getCells(mbr);
		if (cells.size() == 0) {
			System.out.println("There is a problem when getting cells (GRt Index insertion)");
		} else {
			for (Coordinate c : cells) {
				if (grid.containsKey(c)) {
					grid.get(c).insert(startTime, endTime, element);
				} else {
					SIRtree cellIntervalRtree = new SIRtree();
					cellIntervalRtree.insert(startTime, endTime, element);
					grid.put(c, cellIntervalRtree);
				}
			}
		}
	}

	public ArrayList<T> search(DateTime startTime, DateTime endTime) {
		long startTimeInMilli = startTime.getMillis();
		long endTimeInMilli = endTime.getMillis();
		return temporalSearch(grid.keySet(), startTimeInMilli, endTimeInMilli);
	}

	public ArrayList<T> search(DateTime startTime, DateTime endTime, Envelope mbr) {
		long startTimeInMilli = startTime.getMillis();
		long endTimeInMilli = endTime.getMillis();
		ArrayList<T> results = new ArrayList<>();
		HashSet<Coordinate> cells = spatialSearch(mbr);
		for (Coordinate c : cells) {
			SIRtree tree = grid.get(c);
			if (tree != null) {
				@SuppressWarnings("unchecked")
				List<T> l = tree.query(startTimeInMilli, endTimeInMilli);
				results.addAll(l);
			}
		}
		return results;
	}

	private ArrayList<T> temporalSearch(Set<Coordinate> coordinates, long startTime, long endTime) {
		ArrayList<T> resultingTrajIDs = new ArrayList<>();
		for (Coordinate c : coordinates) {
			@SuppressWarnings("unchecked")
			List<T> l = grid.get(c).query(startTime, endTime);
			resultingTrajIDs.addAll(l);
		}
		return resultingTrajIDs;
	}

	private HashSet<Coordinate> spatialSearch(Envelope mbr) {
		return getCells(mbr);
	}

	private HashSet<Coordinate> getCells(Envelope mbr) {
		HashSet<Coordinate> mbrCells = new HashSet<Coordinate>();
		double min_x = mbr.getMinX();
		double max_x = mbr.getMaxX();
		double min_y = mbr.getMinY();
		double max_y = mbr.getMaxY();

		for (Integer xCell = (int) (min_x / deltaX); xCell <= (int) (max_x / deltaX); xCell++) {
			for (Integer yCell = (int) (min_y / deltaY); yCell <= (int) (max_y / deltaY); yCell++) {
				Coordinate cell = new Coordinate(xCell.doubleValue(), yCell.doubleValue());
				mbrCells.add(cell);
			}
		}
		return mbrCells;
	}
}
