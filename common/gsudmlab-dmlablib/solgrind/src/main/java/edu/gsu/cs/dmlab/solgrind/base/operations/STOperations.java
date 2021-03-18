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
package edu.gsu.cs.dmlab.solgrind.base.operations;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

/**
 * Implements spatiotemporal operations
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class STOperations {

	public static Trajectory union(Trajectory traj1, Trajectory traj2) {
		Trajectory trj = new Trajectory();

		if (!TOperations.tIntersects(traj1, traj2)) { // do the simple union
			trj.getTGPairs().addAll(traj1.getTGPairs());
			trj.getTGPairs().addAll(traj2.getTGPairs());
			return trj;
		}
		Trajectory earlyTrajectory = traj1;
		Trajectory lateTrajectory = traj2;
		if (traj1.getStartTime().isAfter(traj2.getStartTime())) {
			// traj2 starts earlier
			earlyTrajectory = traj2;
			lateTrajectory = traj1;
		}

		for (TGPair earlyPair : earlyTrajectory.getTGPairs()) { // get early
																// trajectory's
																// pair
			Interval earlyTI = earlyPair.getTInterval(); // early traj pair's
															// time interval
			SortedSet<TGPair> lateTGPairs = lateTrajectory.getSegment(earlyTI.getStart(), earlyTI.getEnd())
					.getTGPairs();

			Geometry earlyGeom = earlyPair.getGeometry();
			for (TGPair latePair : lateTGPairs) { // late trajectory's pair
				Interval t2ti = latePair.getTInterval();
				Geometry t2geom = latePair.getGeometry();
				Geometry uGeometry = earlyGeom.union(t2geom);
				trj.addTGPair(t2ti.getStart(), t2ti.getEnd(), uGeometry);
			}
		}

		// check if late trajectory has some more tg pairs that are not covered
		// above
		if (earlyTrajectory.getEndTime().isBefore(lateTrajectory.getEndTime())) {
			Trajectory lateLastParts = lateTrajectory.getSegment(earlyTrajectory.getEndTime(),
					lateTrajectory.getEndTime());
			trj.getTGPairs().addAll(lateLastParts.getTGPairs());
		}

		return trj;
	}

	public static Trajectory intersection(Trajectory traj1, Trajectory traj2) {

		Trajectory trj = new Trajectory();
		if (!TOperations.tIntersects(traj1, traj2)) {
			return trj; // return empty
		}

		if (!SOperations.sIntersectsMBR(traj1, traj2)) {
			return trj; // return empty
		}

		for (TGPair t1Pair : traj1.getTGPairs()) {
			Interval t1ti = t1Pair.getTInterval(); // t1 pair's time interval
			SortedSet<TGPair> tgpairs2 = traj2.getSegment(t1ti.getStart(), t1ti.getEnd()).getTGPairs();

			if (tgpairs2.size() == 0) {
				continue;
			}

			Geometry t1geom = t1Pair.getGeometry();
			for (TGPair t2pair : tgpairs2) {

				Interval t2ti = t2pair.getTInterval();
				Geometry t2geom = t2pair.getGeometry();
				Geometry iGeometry = t1geom.intersection(t2geom);
				trj.addTGPair(t2ti.getStart(), t2ti.getEnd(), iGeometry);
			}
		}
		return trj;
	}

	public static boolean stIntersects(Trajectory traj1, Trajectory traj2) {

		Trajectory trj = new Trajectory();
		if (!TOperations.tIntersects(traj1, traj2)) {
			return false; // return false
		}

		if (!SOperations.sIntersectsMBR(traj1, traj2)) {
			return false; // return false
		}

		for (TGPair t1Pair : traj1.getTGPairs()) {
			Interval t1ti = t1Pair.getTInterval(); // t1 pair's time interval
			SortedSet<TGPair> tgpairs2 = traj2.getSegment(t1ti.getStart(), t1ti.getEnd()).getTGPairs();

			if (tgpairs2.size() == 0) {
				continue;
			}

			Geometry t1geom = t1Pair.getGeometry();
			for (TGPair t2pair : tgpairs2) {
				Interval t2ti = t2pair.getTInterval();
				Geometry t2geom = t2pair.getGeometry();
				if (t1geom.intersects(t2geom)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Trajectory unionAll(Collection<Trajectory> trajCollection) {

		return new Trajectory();
	}

	public static Trajectory intersectionAll(Collection<Trajectory> trajCollection) {

		return new Trajectory();
	}

	public static Geometry interpolate(Geometry geometry, Geometry nextGeometry, double samplingRatio) {
		return geometry;
	}

	public static Geometry interpolate(Geometry geometry, long l) {
		return geometry;
	}

}
