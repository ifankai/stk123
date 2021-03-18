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
package edu.gsu.cs.dmlab.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

/**
 * Estimates the position of points and polygons based upon the differential
 * rotation of the sun.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class PositionEstimator {

	/**
	 * Method estimates the location of a given point using the approximate
	 * differential rotation of the sun and the amount of time passed.
	 * 
	 * @param point     The point to project the location of
	 * 
	 * @param millisecs The milliseconds offset from the original time the point was
	 *                  recorded, used to estimate how much the point should have
	 *                  moved.
	 * 
	 * @return The estimated location of the point after the elapsed input time
	 */
	public static Coordinate getPredictedPos(Coordinate point, double millisecs) {
		Coordinate XY = STCoordinateSystemConverter.convertHPCToPixXY(point);
		Coordinate HGSCoord = STCoordinateSystemConverter.convertPixXYToHGS(XY);

		HGSCoord = calcNewHGSLoc(HGSCoord, millisecs);
		Coordinate XY2 = STCoordinateSystemConverter.convertHGSToPixXY(HGSCoord);

		return STCoordinateSystemConverter.convertPixXYToHPC(XY2);
	}

	/**
	 * Method estimates the location of a given polygon using the approximate
	 * differential rotation of the sun and the amount of time passed.
	 * 
	 * @param poly      The polygon to project the location of
	 * 
	 * @param millisecs The milliseconds offset from the original time the point was
	 *                  recorded, used to estimate how much the point should have
	 *                  moved.
	 * 
	 * @return The estimated location of the input polygon after the elapsed input
	 *         time
	 */
	public static Polygon getPredictedPos(Polygon poly, double millisecs) {
		Coordinate[] s_coords = new Coordinate[poly.getCoordinates().length];

		for (int i = 0; i < poly.getCoordinates().length; i++) {
			Coordinate shiftedPoint = getPredictedPos(poly.getCoordinates()[i], millisecs);
			s_coords[i] = new Coordinate(shiftedPoint.x, shiftedPoint.y);
		}

		Polygon outPoly = new GeometryFactory().createPolygon(s_coords);
		return outPoly;
	}

	private static Coordinate calcNewHGSLoc(Coordinate pointIn, double millisecs) {
		double x = pointIn.x
				+ millisecs * (14.44 - 3.0 * Math.pow(Math.sin(Math.toDegrees(pointIn.y)), 2.0)) / (24 * 60 * 60000);
		pointIn.x = x;
		return pointIn;
	}
}
