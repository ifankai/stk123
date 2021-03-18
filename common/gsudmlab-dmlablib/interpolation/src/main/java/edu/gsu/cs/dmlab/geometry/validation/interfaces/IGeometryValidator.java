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
package edu.gsu.cs.dmlab.geometry.validation.interfaces;

import org.locationtech.jts.geom.Geometry;

/**
 * This is the public interface for geometry validator objects used to clean
 * various geometry objects so that they are valid geometry objects. One use of
 * such validation is in polygon interpolation methods described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IGeometryValidator {

	/**
	 * Performs some validation function on the input geometry and returns a new
	 * geometry that conforms to that validation method. An example would be to
	 * ensure that the coordinate list is in a counterclockwise direction.
	 * 
	 * @param input The input geometry to force to conform to a set of validity
	 *              rules.
	 * 
	 * @return A valid copy of the input geometry.
	 */
	public Geometry produceValidGeometry(Geometry input);
}
