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
package edu.gsu.cs.dmlab.factory.interfaces;

import java.util.List;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;

/**
 * The public interface for classes that will be used to create objects used in
 * trajectory interpolation.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IInterpolationFactory {

	/**
	 * Gets a new event report object that assumes that the object is representing
	 * an interpolated event object.
	 * 
	 * @param timePeriod The valid time for the event representation.
	 * 
	 * @param type       The type of event that the object represents.
	 * 
	 * @param geometry   The geometry of the object.
	 * 
	 * @return A new ISTInterpolationEvent object with the passed in information
	 */
	public ISTInterpolationEvent getSTEvent(Interval timePeriod, EventType type, Geometry geometry);

	/**
	 * Gets an event report object is representing an object from the database and
	 * not one that has been interpolated.
	 * 
	 * @param id         The id of the event in the database so we can reference
	 *                   back to the database to store the interpolated objects
	 *                   between the object and another one from the database.
	 * 
	 * @param timePeriod The valid time for the event representation.
	 * 
	 * @param type       The type of event that the object represents.
	 * 
	 * @param geometry   The geometry of the object.
	 * 
	 * @return A new ISTInterpolationEvent object with the passed in information
	 */
	public ISTInterpolationEvent getSTEvent(int id, Interval timePeriod, EventType type, Geometry geometry);

	/**
	 * Gets a trajectory object that contains the list of ISTInterpolationEvents
	 * that are passed in trajectory.
	 * 
	 * @param events The set of event detections that compose the trajectory
	 * 
	 * @return A new ISTInterpolationTrajectory object that contains the passed in
	 *         detection reports.
	 */
	public ISTInterpolationTrajectory getSTTrajectory(List<ISTInterpolationEvent> events);
}
