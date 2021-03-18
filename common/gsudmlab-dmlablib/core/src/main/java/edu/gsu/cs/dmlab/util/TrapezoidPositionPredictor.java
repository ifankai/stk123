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
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import edu.gsu.cs.dmlab.util.interfaces.ISTSearchAreaProducer;

/**
 * Predicts the position of points and polygons based upon the differential
 * rotation of the sun, or a given normalized movement vector. Similarly, this
 * file also produces a search based upon the polygon representation or the mbr
 * of an object. The search are is a trapezoid that starts as the size of the
 * mbr at the left and opens up to the right.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University Michael
 *         Tinglof, Data Mining Lab, Georgia State University
 * 
 */
public class TrapezoidPositionPredictor implements ISTSearchAreaProducer {
	static final double THETA = -20.5;

	/**
	 * getPredictedPos :returns the predicted position of a point, for the change in
	 * time, based upon the latitude of the point and the solar rotation at that
	 * latitude.
	 *
	 * @param point :the point to calculate the new position of.
	 * @param span  :the time span (in days) used to determine how far the sun has
	 *              rotated
	 * @return :a new point with the new coordinates
	 */

	Coordinate getPredictedPos(Coordinate point, double span) {
		Coordinate HGSCoord = STCoordinateSystemConverter.convertPixXYToHGS(point);
		HGSCoord = this.calcNewLoc(HGSCoord, span);
		return STCoordinateSystemConverter.convertHGSToPixXY(HGSCoord);
	}

	@Override
	public Geometry getSearchRegion(Envelope bBox, double span) {

		Coordinate oldCorner = new Coordinate(bBox.getMinX(), bBox.getMinY());
		Coordinate rotatedCorner = this.getPredictedPos(oldCorner, span);

		Coordinate rotatedLowerLeft = new Coordinate(rotatedCorner.x, rotatedCorner.y + bBox.getHeight());

		Coordinate upperLeft = rotatedCorner;
		Coordinate lowerLeft = rotatedLowerLeft;

		Coordinate upperCenterRight = new Coordinate((rotatedCorner.x + bBox.getWidth()), rotatedCorner.y);
		Coordinate lowerCenterRight = new Coordinate((rotatedLowerLeft.x + bBox.getWidth()), rotatedLowerLeft.y);

		double length = upperCenterRight.x - upperLeft.x;
		double addedHeight = Math.tan(Math.toRadians(THETA)) * length;

		Coordinate lowerRight = new Coordinate(lowerCenterRight.x, lowerCenterRight.y - addedHeight);
		Coordinate upperRight = new Coordinate(upperCenterRight.x, upperCenterRight.y + addedHeight);

		int[] searchXArr = new int[7];
		int[] searchYArr = new int[7];

		// out.add(upperLeft);
		searchXArr[0] = (int) upperLeft.x;
		searchYArr[0] = (int) upperLeft.y;

		// out.add(lowerLeft);
		searchXArr[1] = (int) lowerLeft.x;
		searchYArr[1] = (int) lowerLeft.y;

		// out.add(lowerRight);
		searchXArr[2] = (int) lowerRight.x;
		searchYArr[2] = (int) lowerRight.y;

		// out.add(lowerCenterRight);
		searchXArr[3] = (int) lowerCenterRight.x;
		searchYArr[3] = (int) lowerCenterRight.y;

		// out.add(upperCenterRight);
		searchXArr[4] = (int) upperCenterRight.x;
		searchYArr[4] = (int) upperCenterRight.y;

		// out.add(upperRight);
		searchXArr[5] = (int) upperRight.x;
		searchYArr[5] = (int) upperRight.y;

		// out.add(upperLeft);
		searchXArr[6] = (int) upperLeft.x;
		searchYArr[6] = (int) upperLeft.y;

		Coordinate[] coords = new Coordinate[8];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(searchXArr[i % searchXArr.length], searchYArr[i % searchYArr.length]);
		}
		GeometryFactory gf = new GeometryFactory();
		Geometry out = gf.createLinearRing(coords);

		return out;
	}

	@Override
	public Geometry getSearchRegion(Envelope bBox, float[] movementVect, double span) {

		double xMove = movementVect[0] * span;
		double yMove = movementVect[1] * span;

		Coordinate oldCorner = new Coordinate(bBox.getMinX(), bBox.getMinY());

		Coordinate rotatedUpperCenterLeft = new Coordinate(oldCorner.x + xMove, oldCorner.y + yMove);
		Coordinate rotatedLowerCenterLeft = new Coordinate(rotatedUpperCenterLeft.x,
				rotatedUpperCenterLeft.y + bBox.getHeight());

		Coordinate upperRight = new Coordinate((rotatedUpperCenterLeft.x + bBox.getWidth()), rotatedUpperCenterLeft.y);
		Coordinate lowerRight = new Coordinate((rotatedLowerCenterLeft.x + bBox.getWidth()), rotatedLowerCenterLeft.y);

		double length = upperRight.x - rotatedUpperCenterLeft.x;
		double addedHeight = Math.tan(Math.toRadians(THETA)) * length;

		Coordinate upperLeft = new Coordinate(rotatedUpperCenterLeft.x, rotatedUpperCenterLeft.y + addedHeight);
		Coordinate lowerLeft = new Coordinate(rotatedLowerCenterLeft.x, rotatedLowerCenterLeft.y - addedHeight);

		int[] searchXArr = new int[7];
		int[] searchYArr = new int[7];

		// out.add(upperLeft);
		searchXArr[0] = (int) upperLeft.x;
		searchYArr[0] = (int) upperLeft.y;

		// out.add(rotatedUpperCenterLeft);
		searchXArr[1] = (int) rotatedUpperCenterLeft.x;
		searchYArr[1] = (int) rotatedUpperCenterLeft.y;

		// out.add(rotatedLowerCenterLeft);
		searchXArr[2] = (int) rotatedLowerCenterLeft.x;
		searchYArr[2] = (int) rotatedLowerCenterLeft.y;

		// out.add(lowerLeft);
		searchXArr[3] = (int) lowerLeft.x;
		searchYArr[3] = (int) lowerLeft.y;

		// out.add(lowerRight);
		searchXArr[4] = (int) lowerRight.x;
		searchYArr[4] = (int) lowerRight.y;

		// out.add(upperRight);
		searchXArr[5] = (int) upperRight.x;
		searchYArr[5] = (int) upperRight.y;

		// out.add(upperLeft);
		searchXArr[6] = (int) upperLeft.x;
		searchYArr[6] = (int) upperLeft.y;

		Coordinate[] coords = new Coordinate[8];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(searchXArr[i % searchXArr.length], searchYArr[i % searchYArr.length]);
		}
		GeometryFactory gf = new GeometryFactory();
		Geometry out = gf.createLinearRing(coords);

		return out;
	}

	/**
	 * calcNewLoc :calculates the new location in HGS based on time passed and
	 * latitude
	 *
	 * @param pointIn :point in HGS to calculate the new position of
	 * @param days    :how far in the future, in days, for which to calculate the
	 *                position of the point
	 * @return : point with new HGS coordinates
	 */
	private Coordinate calcNewLoc(Coordinate pointIn, double days) {
		double x = pointIn.x + days * (14.44 - 3.0 * Math.pow(Math.sin(Math.toDegrees(pointIn.y)), 2.0));
		pointIn.x = x;
		return pointIn;
	}

	@Override
	public Geometry getSearchRegionBack(Envelope bBox, double span) {
		Coordinate oldCorner = new Coordinate(bBox.getMinX(), bBox.getMinY());

		Coordinate rotatedCorner = this.getPredictedPos(oldCorner, -span);
		Coordinate rotatedLowerLeft = new Coordinate(rotatedCorner.x, rotatedCorner.y + bBox.getHeight());

		Coordinate upperCenterLeft = rotatedCorner;
		Coordinate lowerCenterLeft = rotatedLowerLeft;

		Coordinate upperRight = new Coordinate((rotatedCorner.x + bBox.getWidth()), rotatedCorner.y);
		Coordinate lowerRight = new Coordinate((rotatedLowerLeft.x + bBox.getWidth()), rotatedLowerLeft.y);

		double length = upperRight.x - upperCenterLeft.x;
		double addedHeight = Math.tan(Math.toRadians(THETA)) * length;

		Coordinate lowerLeft = new Coordinate(lowerCenterLeft.x, lowerCenterLeft.y - addedHeight);
		Coordinate upperLeft = new Coordinate(upperCenterLeft.x, upperCenterLeft.y + addedHeight);

		int[] searchXArr = new int[7];
		int[] searchYArr = new int[7];

		// out.add(upperLeft);
		searchXArr[0] = (int) upperLeft.x;
		searchYArr[0] = (int) upperLeft.y;

		// out.add(lowerLeft);
		searchXArr[1] = (int) upperCenterLeft.x;
		searchYArr[1] = (int) upperCenterLeft.y;

		// out.add(lowerRight);
		searchXArr[2] = (int) lowerCenterLeft.x;
		searchYArr[2] = (int) lowerCenterLeft.y;

		// out.add(lowerCenterRight);
		searchXArr[3] = (int) lowerLeft.x;
		searchYArr[3] = (int) lowerLeft.y;

		// out.add(upperCenterRight);
		searchXArr[4] = (int) lowerRight.x;
		searchYArr[4] = (int) lowerRight.y;

		// out.add(upperRight);
		searchXArr[5] = (int) upperRight.x;
		searchYArr[5] = (int) upperRight.y;

		// out.add(upperLeft);
		searchXArr[6] = (int) upperLeft.x;
		searchYArr[6] = (int) upperLeft.y;

		Coordinate[] coords = new Coordinate[8];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new Coordinate(searchXArr[i % searchXArr.length], searchYArr[i % searchYArr.length]);
		}
		GeometryFactory gf = new GeometryFactory();
		Geometry out = gf.createLinearRing(coords);
		return out;
	}
}