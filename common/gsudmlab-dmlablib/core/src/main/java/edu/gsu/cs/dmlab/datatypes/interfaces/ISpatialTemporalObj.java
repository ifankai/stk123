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
package edu.gsu.cs.dmlab.datatypes.interfaces;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * Is the base interface for a number of spatio-temporal objects. It provides
 * the minimum definitions need for an object of spatio-temporal type in this
 * library.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISpatialTemporalObj extends IBaseTemporalObject {

	/**
	 * Computes the centroid of the envelope of this Geometry.
	 * 
	 * @return The centroid of the envelope of this Geometry.
	 */
	public Point getCentroid();

	/**
	 * Gets a Geometry representing the envelope (bounding box) of this Geometry.
	 * 
	 * @return a Geometry representing the envelope of this Geometry
	 */
	public Envelope getEnvelope();

	/**
	 * Gets the Geometry of this object, which is a representation of a planar,
	 * linear vector geometry.
	 * 
	 * @return the Geometry of this object.
	 */
	public Geometry getGeometry();

	/**
	 * Gets the area of this Geometry multiplied by the duration of this object.
	 * Areal Geometries have a non-zero area.
	 * 
	 * @return The computed volume of this spatio-temporal object.
	 */
	public double getVolume();

}
