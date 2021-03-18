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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
public class JaccardStar {

	public static double jaccardStar(Instance i1, Instance i2) {

		double intersectionArea = Jaccard.intersection(i1, i2);

		if (intersectionArea == 0)
			return 0;

		double unionArea = jaccardStarUnion(i1, i2);

		return intersectionArea / unionArea;

	}

	public static double jaccardStar2(Instance i1, Instance i2) {

		Map<Long, Geometry> geos1 = TGPairMapper.map(i1);
		Map<Long, Geometry> geos2 = TGPairMapper.map(i2);

		Set<Long> timeIntersection = Sets.intersection(geos1.keySet(), geos2.keySet());

		double intersectionArea = 0;
		double unionArea = 0;
		double tempArea;

		for (Long i : timeIntersection) {
			tempArea = geos1.get(i).intersection(geos2.get(i)).getArea();
			if (tempArea > 0.0) {
				unionArea += geos1.get(i).union(geos2.get(i)).getArea();
				intersectionArea += tempArea;
			}
		}

		if (intersectionArea == 0)
			return 0;

		return intersectionArea / unionArea;

	}

	public static double jaccardStar(List<Instance> instances) throws Exception {

		if (instances.size() == 2)
			return jaccardStar2(instances.get(0), instances.get(1));

		Set<Long> xCoTimeIntervalList = new TreeSet<>();

		// For each combination in the list
		for (int i = 0; i < instances.size(); i++) {

			for (int j = i + 1; j < instances.size(); j++) {
				Instance instance1 = instances.get(i);
				Instance instance2 = instances.get(j);

				// Find temporal intersection of two instances
				Set<Long> tempSet = findSpatioTempralIntersection(instance1, instance2);

				if (tempSet.isEmpty()) {
					return 0.0;
				}
				xCoTimeIntervalList.addAll(tempSet);
			}
		}

		double totalIntersectionArea = 0.0;

		List<Geometry> geometries = new ArrayList<>();
		for (Long t : xCoTimeIntervalList) {
			boolean isAllIntersecting = true;
			for (Instance instance : instances) {
				Geometry g1 = TGPairMapper.map(instance).get(t);
				if (g1 != null) {
					geometries.add(g1);
				} else {
					isAllIntersecting = false;
					break;
				}
			}
			if (isAllIntersecting)
				totalIntersectionArea += Jaccard.findIntersectionArea(geometries);
			geometries.clear();
		}

		if (totalIntersectionArea == 0) {
			return 0;
		}
		geometries.clear();

		double totalUnionArea = 0.0;
		for (Long t : xCoTimeIntervalList) {
			for (Instance instance : instances) {
				Geometry g1 = TGPairMapper.map(instance).get(t);
				if (g1 != null) {
					geometries.add(g1);
				}
			}
			totalUnionArea += findUnionArea(geometries);
			geometries.clear();
		}
		return totalIntersectionArea / totalUnionArea;
	}

	public static double findUnionArea(List<Geometry> geometries) throws Exception {
		if (geometries.size() == 0) {
			throw new Exception("Fatal Error, intersection geometry list is empty");
		}

		Geometry g1 = geometries.get(0);
		for (int i = 1; i < geometries.size(); i++) {
			g1 = g1.union(geometries.get(i));
		}
		return g1.getArea();
	}

	public static Set<Long> findSpatioTempralIntersection(Instance i1, Instance i2) {
		Set<Long> temporalIntersection = Sets.intersection(TGPairMapper.map(i1).keySet(),
				TGPairMapper.map(i2).keySet());
		Iterator<Long> iterator = temporalIntersection.iterator();
		Set<Long> stIntersection = new TreeSet<>();
		while (iterator.hasNext()) {
			Long l = iterator.next();
			if (TGPairMapper.map(i1).get(l).intersects(TGPairMapper.map(i2).get(l))) {
				stIntersection.add(l);
			}
		}
		return stIntersection;
	}

	public static double jaccardStarUnion(Instance i1, Instance i2) {

		Map<Long, Geometry> geos1 = TGPairMapper.map(i1);
		Map<Long, Geometry> geos2 = TGPairMapper.map(i2);

		Set<Long> timeIntersection = Sets.intersection(geos1.keySet(), geos2.keySet());

		double area = 0;

		for (Long i : timeIntersection) {
			if (!geos1.get(i).intersects(geos2.get(i))) {
				continue;
			}
			area += geos1.get(i).union(geos2.get(i)).getArea();
		}
		return area;
	}

}
