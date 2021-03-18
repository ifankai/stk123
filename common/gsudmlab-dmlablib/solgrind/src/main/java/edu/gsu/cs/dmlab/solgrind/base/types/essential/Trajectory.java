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
package edu.gsu.cs.dmlab.solgrind.base.types.essential;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class Trajectory {

	private SortedSet<TGPair> tgpairs; // time geometry pairs of trajectory

	public Trajectory() {
		tgpairs = new TreeSet<>();
	}

	public Trajectory(Collection<TGPair> timeGeometryPairs) {

		tgpairs = new TreeSet<>();
		tgpairs.addAll(timeGeometryPairs);

	}

	public Envelope getMBR() {
		Envelope env = new Envelope();
		for (TGPair tgp : tgpairs) {
			env.expandToInclude(tgp.getEnvelope());
		}
		return env;
	}

	public Trajectory getSegment(DateTime start, DateTime end) {
		Trajectory segment = new Trajectory();
		Interval desiredTInterval = new Interval(start, end);

		Iterator<TGPair> pairs = tgpairs.iterator();
		while (pairs.hasNext()) {
			TGPair tgp = pairs.next();
			// get intersection between an individual time interval and desired interval
			Interval intersectionInterval = tgp.getTInterval().overlap(desiredTInterval);
			if (intersectionInterval != null) {
				segment.addTGPair(intersectionInterval.getStart(), intersectionInterval.getEnd(), tgp.getGeometry());
			}
		}
		return segment;
	}

	public void addTGPair(long startTime, long endTime, Geometry geometry) {
		TGPair tgp = new TGPair(startTime, endTime, geometry);
		this.tgpairs.add(tgp);
	}

	public void addTGPair(DateTime startTime, DateTime endTime, Geometry geometry) {
		TGPair tgp = new TGPair(startTime, endTime, geometry);
		this.tgpairs.add(tgp);
	}

	public void addTGPair(TGPair tgp) {
		this.tgpairs.add(tgp);
	}

	public DateTime getStartTime() {
		return tgpairs.first().getTInterval().getStart();
	}

	public DateTime getEndTime() {
		return tgpairs.last().getTInterval().getEnd();
	}

	public SortedSet<Interval> getTimeIntervals() {
		TreeSet<Interval> intervalSet = new TreeSet<>();
		for (TGPair tgp : tgpairs) {

			intervalSet.add(tgp.getTInterval());
		}
		return intervalSet;
	}

	public SortedSet<TGPair> getTGPairs() {
		return (TreeSet<TGPair>) tgpairs;
	}

	/**
	 * Retuns the number of TGPairs in the trajectory
	 * 
	 * @return
	 */
	public int getTGPairSize() {
		return tgpairs.size();
	}

	public double getVolume() {

		double volume = 0.0;
		for (TGPair tgp : tgpairs) {
			volume += tgp.getVolume();
		}

		return volume;
	}

}
