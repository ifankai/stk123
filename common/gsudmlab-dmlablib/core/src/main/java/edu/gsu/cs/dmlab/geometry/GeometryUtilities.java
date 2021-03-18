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
package edu.gsu.cs.dmlab.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Set of geometry utilities used throughout the project.
 * 
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public class GeometryUtilities {

	/**
	 * Scales an Envelope by the divisor value
	 * 
	 * @param env     The envelope geometry to scale
	 * 
	 * @param divisor The divisor used to scale
	 * 
	 * @return A envelope scaled to the appropriate size based upon the input
	 */
	public static Envelope scaleEnvelope(Envelope env, int divisor) {

		double xmin, xmax, ymin, ymax;
		xmin = env.getMinX() / divisor;
		xmax = env.getMaxX() / divisor;
		ymin = env.getMinY() / divisor;
		ymax = env.getMaxY() / divisor;
		Envelope scaledEnvelope = new Envelope(xmin, xmax, ymin, ymax);
		return scaledEnvelope;
	}

	/**
	 * Scales a Geometry by the divisor value
	 * 
	 * @param geom    The geometry to scale
	 * 
	 * @param divisor The divisor used to scale
	 * 
	 * @return A scaled geometry
	 */
	public static Geometry scaleGeometry(Geometry geom, int divisor) {
		Coordinate[] coords = geom.getCoordinates();
		for (int i = 0; i < coords.length; i++) {
			Coordinate coord = coords[i];
			double x = coord.x / divisor;
			double y = coord.y / divisor;
			coords[i] = new Coordinate(x, y);
		}

		GeometryFactory geoFactory = new GeometryFactory();
		Geometry scaledGeometry = geoFactory.createLinearRing(coords);

		return scaledGeometry;
	}

}
