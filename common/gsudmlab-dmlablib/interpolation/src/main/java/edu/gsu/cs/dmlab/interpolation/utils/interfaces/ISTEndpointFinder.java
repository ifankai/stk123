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
package edu.gsu.cs.dmlab.interpolation.utils.interfaces;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

/**
 * Interface used for class implementations that are used to find a single end
 * point on a polygon based on some criteria
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface ISTEndpointFinder {

	/**
	 * Returns the best end point based on some criteria
	 * 
	 * @param densifiedGeometry The geometry object to find the best end point on
	 * 
	 * @return A coordinate that is considered to be the best based on some criteria
	 */
	public Coordinate findBestEndpoint(Polygon densifiedGeometry);
}
