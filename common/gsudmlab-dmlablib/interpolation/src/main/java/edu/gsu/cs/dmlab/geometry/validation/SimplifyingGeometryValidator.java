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
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;

/**
 * A geometry validator that takes an input geometry and attempts to ensure that
 * it is valid by performing a simplification action on the input geometry. This
 * functionality was produced for the work described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Original Soukaina Filali Boubrahimi, refactored by Dustin Kempton, Data Mining
 *         Lab, Georgia State University Michael Tinglof, Data Mining Lab,
 *         Georgia State University
 *
 */
public class SimplifyingGeometryValidator implements IGeometryValidator {

	private IGeometryValidator polygonValidator;
	private double simplifierDistance;

	/**
	 * Constructor that takes in a geometry validator that removes self
	 * intersections / inconsistencies of passed in Geometry objects. It also takes
	 * in the variable for the simplifier distance tolerance is taken in.
	 * 
	 * @param polygonValidator   The geometry validator that removes self
	 *                           intersections and the like.
	 * 
	 * @param simplifierDistance The distance tolerance for the simplifier. All
	 *                           vertices in the simplified geometry will be within
	 *                           this distance of the original geometry. The
	 *                           tolerance value must be non-negative.
	 */
	public SimplifyingGeometryValidator(IGeometryValidator polygonValidator, double simplifierDistance) {
		if (polygonValidator == null)
			throw new IllegalArgumentException(
					"Polygon Validator cannot be null in SimplifyingGeometryValidator constructor.");
		if (simplifierDistance < 0)
			throw new IllegalArgumentException(
					"Simplifier Distance cannot be less than 0 in SimplifyingGeometryValidator constructor.");
		this.simplifierDistance = simplifierDistance;
		this.polygonValidator = polygonValidator;
	}

	@Override
	public Geometry produceValidGeometry(Geometry input) {

		// First make a simplifier object using the input geometry and the predefined
		// simplifierDistance.
		DouglasPeuckerSimplifier dps = new DouglasPeuckerSimplifier(input);
		dps.setEnsureValid(true);
		dps.setDistanceTolerance(this.simplifierDistance);

		// Get the original geometry area and the simplified geometry area so we can
		// check to see how different they are.
		double originalArea = input.getArea();
		Geometry result = dps.getResultGeometry();
		double simplifiedArea = result.getArea();

		// If the new area is less than 80% of the original, then we need to try
		// something else.
		if (simplifiedArea / originalArea < 0.8) {
			// We start by forcing it to be a polygon
			Geometry geom_v = null;
			try {
				geom_v = this.polygonValidator.produceValidGeometry(input);
			} catch (TopologyException | IllegalArgumentException te) {
				geom_v = input.convexHull();
			}

			// If the resultant polygon is a multipolygon, then just get the largest one
			if (geom_v instanceof MultiPolygon) {
				double maxMPArea = geom_v.getGeometryN(0).getArea();
				Geometry geom_result = geom_v.getGeometryN(0);
				for (int i = 1; i < geom_v.getNumGeometries(); i++) {
					Geometry geom_v_i = geom_v.getGeometryN(i);
					if (geom_v_i.getArea() >= maxMPArea) {
						maxMPArea = geom_v_i.getArea();
						geom_result = geom_v_i;
					}
				}
				return geom_result;
			} else {
				return geom_v;
			}
		}

		// if the original simplifying task was good enough, then we just return it
		return result;
	}

}
