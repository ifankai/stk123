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

import java.util.Comparator;
import java.util.UUID;

/**
 * Is the base data type for Events and Tracks in the tracking module, as well
 * as any spatio-temporal objects used in the solgrind module or interpolation
 * module. This simply so we can perform temporal indexing with the same index
 * object.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IBaseTemporalObject {

	/**
	 * Returns the time period that the object is valid for
	 * 
	 * @return The interval over which the data type is valid.
	 */
	public Interval getTimePeriod();

	/**
	 * Compares the valid time of another IBaseDataType to this object's valid time.
	 * 
	 * @param baseDataType The object to compare against this object.
	 * @return a negative integer, zero, or a positive integer as the passed in
	 *         object is less than, equal to, or greater than this object.
	 */
	public int compareTime(IBaseTemporalObject baseDataType);

	/**
	 * Returns a unique identifier for this object inside this program. This was
	 * added for use in caching because different event types may contain
	 * identifiers in the ID that overlap from one event type to the next. This is
	 * so we can uniquely identify this particular object when caching such things
	 * as image parameters.
	 * 
	 * @return A unique identifier for this object inside this program.
	 */
	public UUID getUUID();

	/**
	 * The temporal comparator for the IBaseDataType
	 */
	public Comparator<IBaseTemporalObject> baseTemporalComparator = IBaseTemporalObject::compareTime;

}
