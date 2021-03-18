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

import edu.gsu.cs.dmlab.datatypes.EventType;

/**
 * The public interface for event tracks processed by tracking algorithms
 * implemented by the Data Mining Lab at Georgia State University
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTTrackingTrajectory extends ISpatialTemporalTrajectory<ISTTrackingEvent> {

	/**
	 * Returns the first event in the track
	 * 
	 * @return the first event in the track
	 */
	public ISTTrackingEvent getFirst();

	/**
	 * Returns the last event in the track
	 * 
	 * @return the last event in the track
	 */
	public ISTTrackingEvent getLast();

	/**
	 * Returns the event type of the track
	 * 
	 * @return the event type of the track
	 */
	public EventType getType();

}
