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
package edu.gsu.cs.dmlab.solgrind.algo.measures.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.solgrind.base.Instance;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class OMAX {

	public static double omax(Instance i1, Instance i2) {

		double intersectionVolume = Jaccard.intersection(i1, i2);

		if (intersectionVolume == 0)
			return 0;

		double maxArea = Math.max(volume(i1), volume(i2));

		return intersectionVolume / maxArea;
	}

	public static double omax(List<Instance> instances) throws Exception {

		if (instances.size() == 2)
			return omax(instances.get(0), instances.get(1));

		Set<Long> coTimeIntervals = Jaccard.findCoTimeIntervals(instances);
		if (coTimeIntervals.isEmpty())
			return 0;

		Set<Long> timeIntervalList = new TreeSet<>();

		// For each combination in the list
		for (int i = 0; i < instances.size(); i++) {
			timeIntervalList = Sets.union(timeIntervalList, TGPairMapper.map(instances.get(i)).keySet());
		}

		double totalIntersectionArea = 0;
		double maxVolume = 0;

		List<Geometry> geometries = new ArrayList<>();
		for (Long t : timeIntervalList) {
			boolean isAllIntersecting = true;
			for (Instance instance : instances) {
				Geometry g1 = TGPairMapper.map(instance).get(t);
				if (g1 != null) {
					geometries.add(g1);
				} else {
					isAllIntersecting = false;
				}
			}
			if (isAllIntersecting) {
				totalIntersectionArea += Jaccard.findIntersectionArea(geometries);
			}
			geometries.clear();
		}

		for (Instance i : instances) {
			maxVolume = Math.max(maxVolume, volume(i));
		}

		return totalIntersectionArea / maxVolume;
	}

	public static double volume(Instance i) {

		double v = 0;
		for (Geometry g : TGPairMapper.map(i).values()) {
			v += g.getArea();
		}
		return v;
	}

}
