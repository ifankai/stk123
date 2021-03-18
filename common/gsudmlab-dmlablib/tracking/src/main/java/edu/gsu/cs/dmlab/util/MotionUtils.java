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

import org.joda.time.DateTime;
import org.locationtech.jts.geom.Point;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class MotionUtils {

	/**
	 * Constant: number of seconds in a day.
	 */
	private static final double SECONDS_TO_DAYS = 60.0 * 60.0 * 24.0;

	/**
	 * Utility function to calculate the mean movement of a track.
	 * 
	 * @param track The track to find the mean movement of
	 * 
	 * @return The mean movement vector of the input track.
	 */
	public static float[] trackMovement(ISTTrackingTrajectory track) {
		double xMovement = 0.0;
		double yMovement = 0.0;
		double totalTime = 0.0;
		int count = 0;

		ISTTrackingEvent event = track.getFirst();

		float[] motionNormMean = new float[2];

		for (ISTTrackingEvent currentEvent : track.getSTObjects()) {
			Point locationTwo = currentEvent.getCentroid();
			Point locationOne = event.getCentroid();
			xMovement += locationOne.getX() - locationTwo.getX();
			yMovement += locationOne.getY() - locationTwo.getY();

			double span;
			DateTime startSearch;
			DateTime endSearch;

			// Interval timePeriod = event.getTimePeriod();
			startSearch = currentEvent.getTimePeriod().getEnd();
			endSearch = startSearch
					.plus(currentEvent.getTimePeriod().getStartMillis() - event.getTimePeriod().getStartMillis());
			span = ((endSearch.minus(startSearch.getMillis())).getMillis() / 1000) / SECONDS_TO_DAYS;

			totalTime += span;
			event = currentEvent;
		}

		if (track.size() > 0) {
			double xMean = xMovement / count;
			double yMean = yMovement / count;
			double tMean = totalTime / count;
			float xMeanPerTime = (float) (xMean / tMean);
			float yMeanPerTime = (float) (yMean / tMean);

			motionNormMean[0] = xMeanPerTime;
			motionNormMean[1] = yMeanPerTime;
		} else {
			motionNormMean[0] = 0;
			motionNormMean[1] = 0;
		}
		return motionNormMean;
	}
}
