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
package edu.gsu.cs.dmlab.solgrind.algo.measures.significance;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.solgrind.algo.measures.SignificanceMeasure;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class JStar implements SignificanceMeasure {

	@Override
	public double calculate(Instance ins1, Instance ins2) {
		return calculateT(ins1.getTrajectory(), ins2.getTrajectory());
	}

	@Override
	public double calculate(Collection<Instance> instances) {
		// TODO implement that
		return 0;
	}

	@Override
	public double calculateT(Trajectory traj1, Trajectory traj2) {
		Set<TGPair> tgpSet1 = traj1.getTGPairs();
		Set<TGPair> tgpSet2 = traj2.getTGPairs();

		Set<Interval> timeIntersection = Sets.intersection(
				tgpSet1.stream().map(g -> g.getTInterval()).collect(Collectors.toSet()),
				tgpSet2.stream().map(g -> g.getTInterval()).collect(Collectors.toSet()));

		double intersectionArea = 0;
		double unionArea = 0;
		double tempArea;

		for (Interval i : timeIntersection) {
			Geometry g1 = getGeomFromSet(tgpSet1, i);
			Geometry g2 = getGeomFromSet(tgpSet2, i);
			tempArea = g1.intersection(g2).getArea();
			if (tempArea > 0.0) {
				unionArea += g1.union(g2).getArea();
				intersectionArea += tempArea;
			}
		}

		if (intersectionArea == 0)
			return 0;

		return intersectionArea / unionArea;
	}

	@Override
	public double calculateT(Collection<Trajectory> trajectories) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static Geometry getGeomFromSet(Set<TGPair> set, Interval interval) {
		for (TGPair tgPair : set) {
			if (tgPair.getTInterval().equals(interval))
				return tgPair.getGeometry();
		}
		return null;
	}

}
