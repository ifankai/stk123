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
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.solgrind.base.Instance;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class JaccardPlus {
	public static double jaccardPlus(Instance i1, Instance i2) {
		return JaccardStar.jaccardStar(i1, i2);
	}

	public static double jaccardPlus2(Instance i1, Instance i2) {
		return JaccardStar.jaccardStar2(i1, i2);
	}

	public static double jaccardPlus(List<Instance> instances) throws Exception {

		if (instances.size() == 2)
			return JaccardStar.jaccardStar2(instances.get(0), instances.get(1));

		Set<Long> coexistenceTimeIntervals = findCoexistenceTimeIntervals(instances);

		double totalIntersectionVolume = 0.0;
		double totalUnionVolume = 0.0;

		List<Geometry> geometries = new ArrayList<>();
		for (Long t : coexistenceTimeIntervals) {
			for (Instance instance : instances) {
				Geometry g1 = TGPairMapper.map(instance).get(t);
				if (g1 != null) {
					geometries.add(g1);
				}
			}
			double intersectionAtT = Jaccard.findIntersectionArea(geometries);
			totalIntersectionVolume += intersectionAtT;

			if (intersectionAtT > 0) {
				totalUnionVolume += Jaccard.findUnionArea(geometries);
			}
			geometries.clear();
		}

		if (totalIntersectionVolume == 0) {
			return 0;
		}
		return totalIntersectionVolume / totalUnionVolume;
	}

	public static Set<Long> findCoexistenceTimeIntervals(List<Instance> instances) {

		Set<Long> temporalIntersection = TGPairMapper.map(instances.get(0)).keySet();
		for (int i = 1; i < instances.size(); i++) {
			temporalIntersection = Sets.intersection(TGPairMapper.map(instances.get(i)).keySet(), temporalIntersection);
		}

		return temporalIntersection;
	}
}
