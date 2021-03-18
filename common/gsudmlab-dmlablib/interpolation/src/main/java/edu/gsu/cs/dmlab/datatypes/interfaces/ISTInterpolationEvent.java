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

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.EventType;

/**
 * Is the public interface for events processed by the interpolation algorithms
 * implemented by the Data Mining Lab at Georgia State University.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface ISTInterpolationEvent extends ISpatialTemporalObj {

	/**
	 * Returns the primary key Id in a database, of the event that this object
	 * represents
	 * 
	 * @return id of the event that this object represents if the event was already
	 *         stored in the database. This will be -1 for objects created by
	 *         interpolation because they have yet to be stored in the database.
	 */
	public int getId();

	/**
	 * Returns an indicator of whether the trajectory is interpolated or not. This
	 * is just so we can have an indicator of this.
	 * 
	 * @return True if the trajectory is interpolated, false if it is not.
	 */
	public boolean isInterpolated();

	/**
	 * Updates the time period that this object is valid
	 * 
	 * @param period the new period for this object to be considered valid over
	 */
	public void updateTimePeriod(Interval period);

	/**
	 * Returns the type of event that this object represents. It is usually a two
	 * letter designation such as AR for Active IRegion, SS for Sun Spot etc.
	 * 
	 * @return the type of event that this object represents.
	 */
	public EventType getType();
}
