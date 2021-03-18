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
package edu.gsu.cs.dmlab.indexes;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.geometry.GeometryUtilities;
import edu.gsu.cs.dmlab.indexes.datastructures.GITree;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.*;

/**
 * This class provides event indexing for ISTTrackingTrajectory objects.
 * 
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public class TreeBasedTrackIndexer implements ISTTrackingTrajectoryIndexer {

	private DateTime firstTime;
	private DateTime lastTime;
	private int regionDivisor;
	private GITree<ISTTrackingTrajectory> tree;
	private ArrayList<ISTTrackingTrajectory> regionalList;

	/**
	 * Constructor, constructs a new TreeBasedTrackIndexer.
	 * 
	 * @param regionalList  The list of tracks to index.
	 * 
	 * @param regionDivisor The divisor used to down size each of the
	 *                      ISTTrackingTrajectory objects to fit inside the spatial
	 *                      domain specified by the regionDimension parameter.
	 * 
	 * @throws IllegalArgumentException When any of the passed in arguments are
	 *                                  null, or if the regionDivisor is less than
	 *                                  1.
	 */
	public TreeBasedTrackIndexer(ArrayList<ISTTrackingTrajectory> regionalList, int regionDivisor)
			throws IllegalArgumentException {
		if (regionalList == null) {
			throw new IllegalArgumentException();
		}
		if (regionDivisor < 1) {
			throw new IllegalArgumentException("region divisor cannot be less than one.");
		}

		// this.regionDimension = regionDimension;
		this.regionDivisor = regionDivisor;
		this.regionalList = regionalList;
		this.tree = new GITree<>();

		for (ISTTrackingTrajectory track : regionalList) {
			if (track.getType().equals(EventType.SIGMOID)) {
				pushSGTrackIntoMatrix(track);
			} else {
				pushTrackIntoMatrix(track);
			}
		}
	}

	private void pushSGTrackIntoMatrix(ISTTrackingTrajectory track) {
		Geometry trackGeom = track.getFirst().getGeometry();

		Geometry scaledTrackGeom = GeometryUtilities.scaleGeometry(trackGeom, this.regionDivisor);
		tree.insert(track, track.getTimePeriod().getStartMillis(), track.getTimePeriod().getEndMillis(),
				scaledTrackGeom.getEnvelopeInternal());
	}

	private void pushTrackIntoMatrix(ISTTrackingTrajectory track) {
		Geometry trackGeom = track.getFirst().getGeometry();
		Geometry scaledTrackGeom = GeometryUtilities.scaleGeometry(trackGeom, this.regionDivisor);

		tree.insert(track, track.getTimePeriod().getStartMillis(), track.getTimePeriod().getEndMillis(),
				scaledTrackGeom.getEnvelopeInternal());
	}

	@Override
	public DateTime getFirstTime() {
		return firstTime;
	}

	@Override
	public DateTime getLastTime() {
		return lastTime;
	}

	@Override
	public ArrayList<ISTTrackingTrajectory> search(Interval timePeriod, Geometry searchArea) {
		Geometry scaledSearchArea = GeometryUtilities.scaleGeometry(searchArea, this.regionDivisor);
		Envelope searchBoundingBox = scaledSearchArea.getEnvelopeInternal();
		ArrayList<ISTTrackingTrajectory> tracks = tree.search(timePeriod.getStart(), timePeriod.getEnd(),
				searchBoundingBox);
		Map<UUID, ISTTrackingTrajectory> map = new HashMap<>();
		for (ISTTrackingTrajectory trk : tracks) {
			map.put(trk.getUUID(), trk);
		}
		ArrayList<ISTTrackingTrajectory> retList = new ArrayList<>();
		retList.addAll(map.values());
		return retList;
	}

	@Override
	public ArrayList<ISTTrackingTrajectory> getAll() {
		return regionalList;
	}

}
