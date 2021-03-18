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

import com.google.common.collect.Sets;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Ahmet Kucuk, Data Mining Lab, Georgia State University
 * 
 */
public class JaccardStar {

	public static double jaccardStar2(Instance i1, Instance i2) {

		Set<TGPair> geos1 = i1.getTrajectory().getTGPairs();
		Set<TGPair> geos2 = i2.getTrajectory().getTGPairs();

		Set<Interval> timeIntersection = Sets.intersection(
				geos1.stream().map(g -> g.getTInterval()).collect(Collectors.toSet()),
				geos2.stream().map(g -> g.getTInterval()).collect(Collectors.toSet()));

		double intersectionArea = 0;
		double unionArea = 0;
		double tempArea;

		for (Interval i : timeIntersection) {
			Geometry g1 = getGeomFromSet(geos1, i);
			Geometry g2 = getGeomFromSet(geos2, i);
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

	public static Geometry getGeomFromSet(Set<TGPair> set, Interval interval) {
		for (TGPair tgPair : set) {
			if (tgPair.getTInterval().equals(interval))
				return tgPair.getGeometry();
		}
		return null;
	}

}
