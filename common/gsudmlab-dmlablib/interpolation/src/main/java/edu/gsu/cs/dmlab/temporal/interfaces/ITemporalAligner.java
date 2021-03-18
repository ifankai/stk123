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
package edu.gsu.cs.dmlab.temporal.interfaces;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;

/**
 * This is the public interface for performing minor adjustments to the
 * timestamp of a spatiotemporal event report to align with an epoch and step
 * size in interpolation methods described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface ITemporalAligner {

	/**
	 * Takes all of the event reports within the trajectory and aligns them to an
	 * integer multiple of some step size starting from some epoch. The original
	 * input data should remain unchanged and new instances of each of the objects
	 * that compose the trajectory should be created.
	 * 
	 * @param input The input trajectory that will be processed.
	 * 
	 * @return A new trajectory with each of the internal event reports aligned so
	 *         that their time stamps are an integer multiple of some step size
	 *         starting from some epoch.
	 */
	public ISTInterpolationTrajectory alignEventsForStepFromEpoch(ISTInterpolationTrajectory input);
	
	public ISTInterpolationEvent alignEventsForInterpolation(ISTInterpolationEvent input);
}
