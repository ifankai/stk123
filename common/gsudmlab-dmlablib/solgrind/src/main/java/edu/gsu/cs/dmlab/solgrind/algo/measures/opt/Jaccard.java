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
import java.util.TreeSet;

import com.google.common.collect.Sets;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.PolygonExtracter;

import edu.gsu.cs.dmlab.solgrind.base.Instance;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class Jaccard {

	public static double jaccard(Instance i1, Instance i2) {

		double intersectionArea = intersection(i1, i2);

		if (intersectionArea == 0)
			return 0;

		double unionArea = union(i1, i2);

		return intersectionArea / unionArea;

	}

	public static double jaccard(List<Instance> instances) throws Exception {

		if (instances.size() == 2)
			return jaccard(instances.get(0), instances.get(1));

		Set<Long> coTimeIntervals = findCoTimeIntervals(instances);
		if (coTimeIntervals.isEmpty())
			return 0;

		Set<Long> timeIntervalList = new TreeSet<>();

		// For each combination in the list
		for (int i = 0; i < instances.size(); i++) {
			timeIntervalList = Sets.union(timeIntervalList, TGPairMapper.map(instances.get(i)).keySet());
		}

		double totalIntersectionArea = 0;
		double totalUnionArea = 0;

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
				totalIntersectionArea += findIntersectionArea(geometries);
			}
			totalUnionArea += findUnionArea(geometries);
			geometries.clear();
		}

		return totalIntersectionArea / totalUnionArea;
	}

	public static Set<Long> findCoTimeIntervals(List<Instance> instances) {
		Set<Long> coTimeIntervals = TGPairMapper.map(instances.get(0)).keySet();
		for (int i = 1; i < instances.size(); i++) {
			coTimeIntervals = Sets.intersection(coTimeIntervals, TGPairMapper.map(instances.get(i)).keySet());
		}
		return coTimeIntervals;
	}

	public static double findUnionArea(List<Geometry> geometries) throws Exception {
		if (geometries.size() == 0) {
			throw new Exception("Fatal Error, intersection geometry list is empty");
		}

		boolean polyFlag = true;
		for (Geometry g : geometries) {
			if (!(g instanceof Polygon)) {
				polyFlag = false;
				break;
			}
		}

		if (polyFlag) {
			Geometry g1 = geometries.get(0);
			try { // the simplest way
				for (int i = 1; i < geometries.size(); i++) {
					g1 = g1.union(geometries.get(i));
				}
				return g1.getArea();
			} catch (Exception e) {
				e.printStackTrace();

				// put them into collection and buffer
				GeometryCollection gCollection = new GeometryFactory()
						.createGeometryCollection(geometries.toArray(new Geometry[geometries.size()]));

				// get polygons and create a multipolygon
				@SuppressWarnings("unchecked")
				List<Polygon> polys = PolygonExtracter.getPolygons(gCollection);
				Geometry combinedPolys = gCollection.getFactory().buildGeometry(polys);
				// get the area of combined polygons
				return combinedPolys.getArea();
			}

		} else { // buffer approach for multipolygons
			GeometryCollection gCollection = new GeometryFactory()
					.createGeometryCollection(geometries.toArray(new Geometry[geometries.size()]));

			try {
				return gCollection.buffer(0).getArea();
			} catch (Exception e) {
				e.printStackTrace();
				// if it does not work extract polygons from the collection
				@SuppressWarnings("unchecked")
				List<Polygon> polys = PolygonExtracter.getPolygons(gCollection);
				Geometry combinedPolys = gCollection.getFactory().buildGeometry(polys);
				return combinedPolys.getArea();
			}

		}

	}

	public static double findIntersectionArea(List<Geometry> geometries) throws Exception {
		if (geometries.size() == 0) {
			throw new Exception("Fatal Error, intersection geometry list is empty");
		}

		if (geometries.size() == 1) {
			return 0;
		}

		Geometry g1 = geometries.get(0);
		for (int i = 1; i < geometries.size(); i++) {
			g1 = g1.intersection(geometries.get(i));
		}
		return g1.getArea();
	}

	public static double intersection(Instance i1, Instance i2) {

		Map<Long, Geometry> geos1 = TGPairMapper.map(i1);
		Map<Long, Geometry> geos2 = TGPairMapper.map(i2);

		Set<Long> timeIntersection = Sets.intersection(geos1.keySet(), geos2.keySet());

		double area = 0;

		for (Long i : timeIntersection) {
			area += geos1.get(i).intersection(geos2.get(i)).getArea();
		}
		return area;
	}

	public static double union(Instance i1, Instance i2) {

		Map<Long, Geometry> geos1 = TGPairMapper.map(i1);
		Map<Long, Geometry> geos2 = TGPairMapper.map(i2);

		Set<Long> unionSet = Sets.union(geos1.keySet(), geos2.keySet());

		double area = 0;

		for (Long i : unionSet) {
			if (!geos1.containsKey(i)) {
				area += geos2.get(i).getArea();
				continue;
			}
			if (!geos2.containsKey(i)) {
				area += geos1.get(i).getArea();
				continue;
			}
			area += geos1.get(i).union(geos2.get(i)).getArea();
		}
		return area;
	}
}
