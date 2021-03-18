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
package edu.gsu.cs.dmlab.tracking;

import org.locationtech.jts.geom.Point;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTMotionModel;

/**
 * MotionModel does a comparison of two track fragments and gives a 0-1
 * likelihood value of being the same tracked object based on their mean
 * movement vectors. From Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class MotionModel implements ISTMotionModel {

	@Override
	public double calcProbMotion(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {
		if (leftTrack == null)
			throw new IllegalArgumentException("Left Track cannot be null.");
		if (rightTrack == null)
			throw new IllegalArgumentException("Right Track cannot be null.");

		double[] leftMotion = this.trackNormalizedMeanMovement(leftTrack);
		double[] rightMotion = this.trackNormalizedMeanMovement(rightTrack);

		double xdiff = leftMotion[0] - rightMotion[0];
		double ydiff = leftMotion[1] - rightMotion[1];

		double val = (xdiff * xdiff) + (ydiff * ydiff);
		val = Math.sqrt(val);

		double prob = 1.0 - (0.5 * val);
		return prob;
	}

	private double[] trackNormalizedMeanMovement(ISTTrackingTrajectory track) {
		double xMovement = 0;
		double yMovement = 0;
		ISTTrackingEvent tmp = track.getFirst();
		int count = 0;
		while (tmp.getNext() != null) {
			ISTTrackingEvent tmp2 = tmp.getNext();
			Point tmp2loc = tmp2.getCentroid();
			Point tmpLoc = tmp.getCentroid();
			xMovement += tmp2loc.getX() - tmpLoc.getX();
			yMovement += tmp2loc.getY() - tmpLoc.getY();
			tmp = tmp2;
			count++;
		}
		double[] motionNormMean = new double[2];
		if (count > 0) {
			// average the movement
			double xMean = xMovement / count;
			double yMean = yMovement / count;

			// Nor normalize the movement
			double val = (xMean * xMean) + (yMean * yMean);
			val = Math.sqrt(val);

			// Store in the array
			motionNormMean[0] = xMean / val;
			motionNormMean[1] = yMean / val;
		} else {
			motionNormMean[0] = 0;
			motionNormMean[1] = 0;
		}
		return motionNormMean;
	}
}
