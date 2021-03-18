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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;

/**
 * A geometry validator that takes an input geometry and attempts to ensure that
 * it is valid by performing a set of functions to the geometry. If the geometry
 * is a polygon or multi polygon, self intersections / inconsistencies are
 * fixed. It also performs gradual incremental additions to the allowed
 * deviation of the original points for merging of the multipolygons into a
 * single polygon. If this fails to get a single polygon, it will simply return
 * the largest area polygon after the iterations of simplification are complete.
 * This functionality was produced for the work described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Original Soukaina Filali Boubrahimi, refactored by Dustin Kempton,
 *         Surabhi Priya, Data Mining Lab, Georgia State University
 *
 */
public class MulitPolyConvexHullGeometryValidator implements IGeometryValidator {
	double SIMPLIFIER_DISTANCE_TOLERANCE = 1.0;

	private IGeometryValidator bufferValidator;
	private IGeometryValidator simplifyValidator;
	private IGeometryValidator polyValidator;

	/**
	 * Constructor that takes in a geometry validator that ensures that points are
	 * listed in the clockwise order. Another validator that performs a simplifying
	 * function. And finally a validator that processes the input polygon or multi
	 * polygon, and ensures self intersections / inconsistencies are fixed.
	 * 
	 * @param bufferValidator   The bufferValidator that verifies points are listed
	 *                          in correct order
	 * 
	 * @param simplifyValidator The simplifying validator that attempts to reduce
	 *                          the complexity of the polygon.
	 * 
	 * @param polyValidator     The validator that removes self
	 *                          intersections/inconsistencies
	 */
	public MulitPolyConvexHullGeometryValidator(IGeometryValidator bufferValidator,
			IGeometryValidator simplifyValidator, IGeometryValidator polyValidator) {
		if (bufferValidator == null)
			throw new IllegalArgumentException(
					"BufferValidator cannot be null in MultiPoly Geometry Validator constructor.");
		if (simplifyValidator == null)
			throw new IllegalArgumentException(
					"SimplifyValidator cannot be null in MultiPoly Geometry Validator constructor.");
		if (polyValidator == null)
			throw new IllegalArgumentException(
					"PolyValidator cannot be null in MultiPoly Geometry Validator constructor.");

		this.bufferValidator = bufferValidator;
		this.polyValidator = polyValidator;
		this.simplifyValidator = simplifyValidator;

	}

	@Override
	public Geometry produceValidGeometry(Geometry geom) {
		if (!geom.isValid()) {

			Geometry geom_bv = this.bufferValidator.produceValidGeometry(geom);
			if (!geom_bv.isValid()) {
				Geometry geom_sv = this.simplifyValidator.produceValidGeometry(geom);
				if (!geom_sv.isValid()) {
					Geometry geom_pv = this.polyValidator.produceValidGeometry(geom);
					if (!geom_pv.isValid()) {
						// System.out.println("WARNING! Geometry validation has failed!!!");
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
			geom = geom.convexHull().buffer(0.1);
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
			Geometry locMaxGeom = geom;
			for (i = 0; i < geom.getNumGeometries(); i++) {
				if (maxArea < geom.getGeometryN(i).getArea()) {
					// get the polygon with maximum area
					locMaxGeom = geom.getGeometryN(i);
				}
			}
			geom = locMaxGeom;
		}

		if (((Polygon) geom).getNumInteriorRing() > 0)
			geom = geom.convexHull();
		return geom;

	}

	private Geometry simplifyGeometry(Geometry geom, double tolerance) {
		DouglasPeuckerSimplifier dps = new DouglasPeuckerSimplifier(geom);
		dps.setEnsureValid(true);
		dps.setDistanceTolerance(tolerance);
		return dps.getResultGeometry();
	}

}
