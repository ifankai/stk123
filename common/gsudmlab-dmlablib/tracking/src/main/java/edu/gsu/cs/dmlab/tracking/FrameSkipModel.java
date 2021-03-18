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

import org.joda.time.DateTime;
import org.joda.time.Interval;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;

/**
 * Model of frame skipping that return the probability of two track fragments
 * being from the same object, given the number of skipped frames between them.
 * This model uses the exit probability as a means to calculate this value.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */ 
public class FrameSkipModel implements ISTFrameSkipModel {

	ISTLocationProbCal exitProbCalculator;  
  
	public FrameSkipModel(ISTLocationProbCal exitProbCalculator) {
		if (exitProbCalculator == null)
			throw new IllegalArgumentException("Exit Prob Calculator cannot be null.");

		this.exitProbCalculator = exitProbCalculator;
	}

	@Override
	public void finalize() throws Throwable {
		this.exitProbCalculator = null;
	}

	@Override
	public double getSkipProb(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {

		DateTime leftTime = leftTrack.getLast().getTimePeriod().getEnd();
		DateTime rightTime = rightTrack.getFirst().getTimePeriod().getStart();

		Interval timePeriod = new Interval(leftTime, rightTime);
		int span = (int) (leftTrack.getLast().getTimePeriod().toDurationMillis() / 1000);
		int frameSkip = (int) (timePeriod.toDurationMillis() / 1000) / span;

		if (frameSkip > 0) {
			double pExitVal = exitProbCalculator.calcProb(leftTrack.getLast());
			double pFalseNeg = 1 - (pExitVal);
			for (int i = 0; i < frameSkip; i++) {
				pFalseNeg *= pFalseNeg;
			}
			System.out.println("pSkip: " + this.sigSkip(pFalseNeg));
			return pFalseNeg;
		} else {

			return 1.0;
		}
	}

	private double sigSkip(double val) {
		double retVal = 1.0 / (1 + Math.exp(-3 * (val - 0.3)));
		return retVal;
	}
}
