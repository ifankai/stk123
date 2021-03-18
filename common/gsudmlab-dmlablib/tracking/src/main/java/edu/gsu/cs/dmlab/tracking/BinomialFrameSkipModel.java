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

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;


/**
 * Model of frame skipping that return the probability of two track fragments
 * being from the same object, given the number of skipped frames between them.
 * This model uses the exit probability as a means to calculate this value by
 * modeling the probability as a binomial distribution.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 			Michael Tinglof, Data Mining Lab, Georgia State University 
 * 
 */
public class BinomialFrameSkipModel implements ISTFrameSkipModel {

	private ISTLocationProbCal exitProbCalculator;

	/**
	 * Constructor with the exit probability calculator passed in.
	 * 
	 * @param exitProbCalculator
	 *            The exit probability calculator used in this object.
	 */
	public BinomialFrameSkipModel(ISTLocationProbCal exitProbCalculator) {
		if (exitProbCalculator == null)
			throw new IllegalArgumentException("Exit Prob Calculator cannot be null.");

		this.exitProbCalculator = exitProbCalculator;
	}

	@Override
	public void finalize() throws Throwable {
		try {
			this.exitProbCalculator = null;
		} finally {
			super.finalize();
		}
	}

	@Override

	

	public double getSkipProb(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {

		if (leftTrack == null)
			throw new IllegalArgumentException("Left Trck cannot be null.");
		if (rightTrack == null)
			throw new IllegalArgumentException("Right Trck cannot be null.");

		DateTime leftTime = leftTrack.getLast().getTimePeriod().getEnd();
		DateTime rightTime = rightTrack.getFirst().getTimePeriod().getStart();

		Interval timePeriod = new Interval(leftTime, rightTime);
		int span = (int) (leftTrack.getLast().getTimePeriod().toDurationMillis() / 1000);
		int frameSkip = (int) (timePeriod.toDurationMillis() / 1000) / span;

		if (frameSkip > 0) {
			// Use exit probability to come up with the probability of this not
			// being the last detection.
			double exitProb = this.exitProbCalculator.calcProb(leftTrack.getLast());
			double notExitProb = 1.0 - exitProb;

			// Now use that probability to calculate what the probability of
			// getting n skipped frames, when considering n+1 frames,
			// when the probability of not exiting is the previously calculated
			// value.
			double binomialCoeff = CombinatoricsUtils.binomialCoefficientDouble(frameSkip + 1, frameSkip);
			return binomialCoeff * Math.pow(notExitProb, frameSkip) * exitProb;
		} else {

			return 1.0;
		}
	}
}
