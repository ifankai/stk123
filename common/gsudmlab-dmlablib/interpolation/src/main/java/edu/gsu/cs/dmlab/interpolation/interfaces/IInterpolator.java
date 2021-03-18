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
package edu.gsu.cs.dmlab.interpolation.interfaces;

import java.util.List;

import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;

/**
 * This is the public interface for trajectory interpolation objects used to
 * interpolate between geometry objects at some cadence rate. The implementing
 * classes are from the polygon interpolation methods described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IInterpolator {

	/**
	 * Interpolates new event reports between the reports contained within the input
	 * trajectory and returns a new trajectory object with the new data. The
	 * original event reports are copied and temporally aligned with a epoch and a
	 * set cadence rate. The original trajectory shall remain unedited.
	 * 
	 * @param inTrajectory The input trajectory that is to be interpolated.
	 * 
	 * @return An interpolated trajectory that is a representation of the input if
	 *         it had reports at the desired cadence rate.
	 */
	public ISTInterpolationTrajectory interpolateTrajectory(ISTInterpolationTrajectory inTrajectory);

	/**
	 * Interpolates new event reports between the two reports that are passed in and
	 * returns a list of the interpolated polygon representations. The original
	 * event reports are copied to be the first and last elements in the returned
	 * list. They are also temporally aligned with an epoch and a set cadence rate.
	 * The original objects shall remain unedited.
	 * 
	 * @param first  The first element to interpolate between
	 * 
	 * @param second The end element to interpolate between
	 * 
	 * @return A list of interpolated polygon objects between the two input objects
	 */
	public List<ISTInterpolationEvent> interpolateBetween(ISTInterpolationEvent first, ISTInterpolationEvent second);

	/**
	 * Interpolates a new event report at a specified time. The original event
	 * reports are assumed to be at the time period they are supposed to be, and
	 * therefore there is not any adjustment made to their time stamp prior to
	 * interpolation. The original objects remain unmodified and only a new event is
	 * returned at the interpolated time.
	 * 
	 * @param first    The first element to interpolate between
	 * 
	 * @param second   The second element to interpolate between
	 * 
	 * @param dateTime The date and time of the object report to be interpolated
	 * 
	 * @return An object that is at an interpolated point between the inputs
	 */
	public ISTInterpolationEvent interpolateAtTime(ISTInterpolationEvent first, ISTInterpolationEvent second,
			DateTime dateTime);

	/**
	 * Interpolates a new event report at a specified time using a default movement
	 * rate as the method to determine where the input event would be at the given
	 * time. The original event is assumed to be at the time period it is supposed
	 * to be, and therefore does not have its time stamp adjusted prior to
	 * interpolation. The original object remains unmodified and only a new event is
	 * returned at the interpolated time.
	 * 
	 * @param ev       The event to apply an interpolation method to
	 * 
	 * @param dateTime The date and time of the object report to be interpolated.
	 *                 This time must be prior to the date and time of the input
	 *                 event. Otherwise an exception is thrown.
	 * 
	 * @return An object that is at an interpolated point prior to the input event.
	 */
	public ISTInterpolationEvent interpolateBeforeAtTime(ISTInterpolationEvent ev, DateTime dateTime);

	/**
	 * Interpolates a new event report at a specified time using a default movement
	 * rate as the method to determine where the input event would be at the given
	 * time. The original event is assumed to be at the time period it is supposed
	 * to be, and therefore does not have its time stamp adjusted prior to
	 * interpolation. The original object remains unmodified and only a new event is
	 * returned at the interpolated time.
	 * 
	 * @param ev       The event to apply an interpolation method to
	 * 
	 * @param dateTime The date and time of the object report to be interpolated.
	 *                 This time must be after the date and time of the input event.
	 *                 Otherwise an exception is thrown.
	 * 
	 * @return An object that is at an interpolated point after the input event.
	 */
	public ISTInterpolationEvent interpolateAfterAtTime(ISTInterpolationEvent ev, DateTime dateTime);
}
