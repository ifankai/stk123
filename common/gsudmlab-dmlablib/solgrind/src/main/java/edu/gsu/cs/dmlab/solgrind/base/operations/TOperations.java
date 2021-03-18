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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

/**
 * Implementation of some temporal operations for Trajectory objects
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class TOperations {

	/**
	 * Method for temporal intersection between two trajectory. Returns the set of
	 * time interval objects where there exists a temporal co-existence relation
	 * between two trajectories given as parameters
	 * 
	 * @param traj1
	 * @param traj2
	 * @return SortedSet of TInterval objects
	 */
	public static SortedSet<Interval> tIntersection(Trajectory traj1, Trajectory traj2) { // TODO
																						// test
																						// it
		TreeSet<Interval> intersectionIntervals = new TreeSet<>();
		SortedSet<Interval> t1Intervals = traj1.getTimeIntervals();
		SortedSet<Interval> t2Intervals = traj2.getTimeIntervals();

		Iterator<Interval> iter1 = t1Intervals.iterator();
		Iterator<Interval> iter2 = t2Intervals.iterator();
		Interval cIntv1 = iter1.next();
		Interval cIntv2 = iter2.next();

		while (iter1.hasNext()) {

			if (cIntv1.getEnd().isBefore(cIntv2.getStart())) {
				cIntv1 = iter1.next();
			} else if (cIntv2.getEnd().isBefore(cIntv1.getStart())) {
				cIntv2 = iter2.next();
			} else { // they must be overlapping
				Interval intersectionInterval = cIntv1.overlap(cIntv2);
				intersectionIntervals.add(intersectionInterval);
				if (cIntv1.getEnd().isBefore(cIntv2.getStart())) {
					cIntv1 = iter1.next();
				} else if (cIntv2.getEnd().isBefore(cIntv1.getStart())) {
					cIntv2 = iter2.next();
				} else { // they end at the same time
					cIntv1 = iter1.next();
					cIntv2 = iter2.next();
				}
			}
		}
		return intersectionIntervals;
	}

	public static boolean tIntersects(Trajectory traj1, Trajectory traj2) { // TODO
																			// test
																			// it
		Interval t1ti = new Interval(traj1.getStartTime(), traj1.getEndTime());
		Interval t2ti = new Interval(traj2.getStartTime(), traj2.getEndTime());

		return t1ti.overlaps(t2ti);

	}

}
