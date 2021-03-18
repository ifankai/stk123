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

import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;

/**
 * A geometry validator that takes an input geometry and attempts to ensure that
 * it is valid by performing a set of functions to the geometry. If the geometry
 * is a polygon or multi polygon, self intersections / inconsistencies are
 * fixed. Otherwise the geometry is returned. This functionality was produced
 * for the work described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Original Soukaina Filali Boubrahimi, refactored by Dustin Kempton,
 *         Surabhi Priya, Data Mining Lab, Georgia State University
 * 
 */
public class PolygonizerGeometryValidator implements IGeometryValidator {

	@Override
	public Geometry produceValidGeometry(Geometry geom) {
		if (geom instanceof Polygon) {

			if (geom.isValid()) {
				// validate does not pick up rings in the wrong order - this will fix that
				geom.normalize();

				// If the polygon is valid just return it
				return geom;
			}

			Polygonizer polygonizer = new Polygonizer();
			this.addPolygon((Polygon) geom, polygonizer);

			@SuppressWarnings("unchecked")
			Geometry retVal = this.toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
			return retVal;
		} else if (geom instanceof MultiPolygon) {

			if (geom.isValid()) {
				// validate does not pick up rings in the wrong order - this will fix that
				geom.normalize();

				// If the multipolygon is valid just return it
				return geom;
			}

			Polygonizer polygonizer = new Polygonizer();
			for (int n = geom.getNumGeometries(); n-- > 0;) {
				this.addPolygon((Polygon) geom.getGeometryN(n), polygonizer);
			}

			@SuppressWarnings("unchecked")
			Geometry retVal = this.toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
			return retVal;
		} else {
			// We only care about polygon / multipolygon geometries
			return geom;
		}
	}

	/**
	 * Add all line strings from the polygon given to the polygonizer given
	 * 
	 * @param polygon     polygon from which to extract line strings
	 * @param polygonizer polygonizer
	 */
	private void addPolygon(Polygon polygon, Polygonizer polygonizer) {
		this.addLineString(polygon.getExteriorRing(), polygonizer);
		for (int n = polygon.getNumInteriorRing(); n-- > 0;) {
			this.addLineString(polygon.getInteriorRingN(n), polygonizer);
		}
	}

	/**
	 * Add the linestring given to the polygonizer
	 * 
	 * @param linestring  line string
	 * @param polygonizer polygonizer
	 */
	private void addLineString(LineString lineString, Polygonizer polygonizer) {
		if (lineString instanceof LinearRing) {
			// LinearRings are treated differently to line strings : we need a
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
	private Geometry toPolygonGeometry(Collection<Polygon> polygons, GeometryFactory factory) {
		switch (polygons.size()) {
		case 0:
			// No valid polygons!
			return null;
		case 1:
			// single polygon - no need to wrap
			return polygons.iterator().next();
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
