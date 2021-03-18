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
package edu.gsu.cs.dmlab.tracking.appearance.interfaces;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;

/**
 * The public interface for various appearance models used in the tracking
 * module of this library.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTAppearanceModel {

	/**
	 * Produces a likelihood value for the two input tracks being the same object
	 * based upon their appearance at the join point.
	 * 
	 * @param leftTrack  The track that is earlier in time.
	 * 
	 * @param rightTrack The track that is later in time.
	 * 
	 * @return The likelihood value for their similarity.
	 */
	public double calcProbAppearance(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack);
}
