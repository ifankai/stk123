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

import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;

/**
 * A geometry validator that takes an input geometry and attempts to ensure that
 * it is valid by performing a set of functions to the geometry, ensuring that
 * points are listed in the clockwise order. This functionality was produced for
 * the work described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Original Soukaina Filali Boubrahimi, refactored by Dustin Kempton,
 *         Surabhi Priya, Data Mining Lab, Georgia State University
 *
 */
public class BufferGeometryValidator implements IGeometryValidator {

	@Override
	public Geometry produceValidGeometry(Geometry geom) {
		if (geom instanceof Polygon) {
			if (geom.isValid()) {
				// validate does not pick up rings in the wrong order - this will fix that
				geom.normalize();
				// If the polygon is valid just return it
				return geom;
			}
			// put buffer
			return geom.buffer(0.0);
		} else if (geom instanceof MultiPolygon) {
			if (geom.isValid()) {
				// validate does not pick up rings in the wrong order - this will fix that
				geom.normalize();
				// If the multipolygon is valid just return it
				return geom;
			}
			// put buffer
			return geom.buffer(0.0);
		} else {
			// We only care about polygon / multipolygon geometries
			return geom;
		}
	}

}
