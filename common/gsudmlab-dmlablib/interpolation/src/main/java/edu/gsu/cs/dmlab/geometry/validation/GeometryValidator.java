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
package edu.gsu.cs.dmlab.geometry.validation;

import java.util.Collection;
import java.util.Iterator;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;



public class GeometryValidator {
	
	double SIMPLIFIER_DISTANCE_TOLERANCE = 1.0;

	/**
	 * Validates the given polygon-based geometry. Tries to simplify the geometry.
	 * Simplification is used for multipolygon elimination. This is much
	 * experimental, use it at your own risk.
	 * 
	 * @param geom -- polygon-based geometry
	 * @return
	 */
	public Geometry validateGeometry(Geometry geom) {
		if (!geom.isValid()) {
			Geometry geom_bv = GeometryValidator.bufferValidate(geom);
			if (!geom_bv.isValid()) {
				Geometry geom_sv = this.simplifierValidate(geom);
				if (!geom_sv.isValid()) {
					Geometry geom_pv = GeometryValidator.polygonizerValidate(geom);
					if (!geom_pv.isValid()) {
						System.out.println("WARNING! Geometry validation has failed!!!");
					} else {
						geom = geom_pv;
					}
				} else {
					geom = geom_sv;
				}
			} else {
				geom = geom_bv;
			}
		}

		// simplify the geometry if it is a multipolygon
		// (for 30 times)
		// with increasing tolerance for simplifier
		int i = 0;
		while (geom instanceof MultiPolygon) {
			geom = simplifyGeometry(geom, this.SIMPLIFIER_DISTANCE_TOLERANCE + i * 0.1);
			i++;
			if (i > 30) {
				break;
			}
		}
		// if 30 iterative simplification steps doesn't work
		// get the polygon with the largest area
		if (geom instanceof MultiPolygon) {
			double maxArea = -1.0;
			for (i = 0; i < geom.getNumGeometries(); i++) {
				if (maxArea < geom.getGeometryN(i).getArea()) {
					geom = geom.getGeometryN(i); // get the polygon with maximum area
				}
			}
		}
		return geom;
	}

	private static Geometry simplifyGeometry(Geometry geom, double tolerance) {
		DouglasPeuckerSimplifier dps = new DouglasPeuckerSimplifier(geom);
		dps.setEnsureValid(true);
		dps.setDistanceTolerance(tolerance);
		return dps.getResultGeometry();
	}

	/**
	 * Validate given geometry using buffer(0.0) trick
	 * 
	 * @param geom
	 * @return
	 */
	public static Geometry bufferValidate(Geometry geom) {

		if (geom instanceof Polygon) {
			if (geom.isValid()) {
				geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
				return geom; // If the polygon is valid just return it
			}
			return geom.buffer(0.0); // put buffer
		} else if (geom instanceof MultiPolygon) {
			if (geom.isValid()) {
				geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
				return geom; // If the multipolygon is valid just return it
			}
			return geom.buffer(0.0); // put buffer
		} else {
			return geom; // In my case, I only care about polygon / multipolygon geometries
		}
	}

	/**
	 * Validate the given polygon-based geometry using D-P simplifier setEnsureValid
	 * is set to true
	 * 
	 * @param geom
	 * @return
	 */
	public Geometry simplifierValidate(Geometry geom) {
		DouglasPeuckerSimplifier dps = null;
		if (geom instanceof Polygon) {
			if (geom.isValid()) {
				geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
				return geom; // If the polygon is valid just return it
			}
			dps = new DouglasPeuckerSimplifier(geom);
			dps.setEnsureValid(true);
			dps.setDistanceTolerance(this.SIMPLIFIER_DISTANCE_TOLERANCE);
			return dps.getResultGeometry();
		} else if (geom instanceof MultiPolygon) {
			if (geom.isValid()) {
				geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
				return geom; // If the multipolygon is valid just return it
			}
			dps = new DouglasPeuckerSimplifier(geom);
			dps.setEnsureValid(true);
			dps.setDistanceTolerance(this.SIMPLIFIER_DISTANCE_TOLERANCE);
			return dps.getResultGeometry();
		} else {
			return geom; // In my case, I only care about polygon / multipolygon geometries
		}
	}

	/**
	 * Get / create a valid version of the geometry given. If the geometry is a
	 * polygon or multi polygon, self intersections / inconsistencies are fixed.
	 * Otherwise the geometry is returned.
	 * 
	 * @param geom
	 * @return a geometry
	 */
	public static Geometry polygonizerValidate(Geometry geom) {
		if (geom instanceof Polygon) {
			if (geom.isValid()) {
				geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
				return geom; // If the polygon is valid just return it
			}
			Polygonizer polygonizer = new Polygonizer();
			addPolygon((Polygon) geom, polygonizer);
			return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
		} else if (geom instanceof MultiPolygon) {
			if (geom.isValid()) {
				geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
				return geom; // If the multipolygon is valid just return it
			}
			Polygonizer polygonizer = new Polygonizer();
			for (int n = geom.getNumGeometries(); n-- > 0;) {
				addPolygon((Polygon) geom.getGeometryN(n), polygonizer);
			}
			return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
		} else {
			return geom; // In my case, I only care about polygon / multipolygon geometries
		}
	}

	/**
	 * Add all line strings from the polygon given to the polygonizer given
	 * 
	 * @param polygon     polygon from which to extract line strings
	 * @param polygonizer polygonizer
	 */
	static void addPolygon(Polygon polygon, Polygonizer polygonizer) {
		addLineString(polygon.getExteriorRing(), polygonizer);
		for (int n = polygon.getNumInteriorRing(); n-- > 0;) {
			addLineString(polygon.getInteriorRingN(n), polygonizer);
		}
	}

	/**
	 * Add the linestring given to the polygonizer
	 * 
	 * @param linestring  line string
	 * @param polygonizer polygonizer
	 */
	static void addLineString(LineString lineString, Polygonizer polygonizer) {

		if (lineString instanceof LinearRing) { // LinearRings are treated differently to line strings : we need a
												// LineString NOT a LinearRing
			lineString = lineString.getFactory().createLineString(lineString.getCoordinateSequence());
		}

		// unioning the linestring with the point makes any self intersections explicit.
		Point point = lineString.getFactory().createPoint(lineString.getCoordinateN(0));
		Geometry toAdd = lineString.union(point);

		// Add result to polygonizer
		polygonizer.add(toAdd);
	}

	/**
	 * Get a geometry from a collection of polygons.
	 * 
	 * @param polygons collection
	 * @param factory  factory to generate MultiPolygon if required
	 * @return null if there were no polygons, the polygon if there was only one, or
	 *         a MultiPolygon containing all polygons otherwise
	 */
	static Geometry toPolygonGeometry(Collection<Polygon> polygons, GeometryFactory factory) {
		switch (polygons.size()) {
		case 0:
			return null; // No valid polygons!
		case 1:
			return polygons.iterator().next(); // single polygon - no need to wrap
		default:
			// polygons may still overlap! Need to sym difference them
			Iterator<Polygon> iter = polygons.iterator();
			Geometry ret = iter.next();
			while (iter.hasNext()) {
				ret = ret.symDifference(iter.next());
			}
			return ret;
		}
	}

}
