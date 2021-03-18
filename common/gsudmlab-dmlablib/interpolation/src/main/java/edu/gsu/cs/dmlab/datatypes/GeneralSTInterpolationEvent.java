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
package edu.gsu.cs.dmlab.datatypes;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;

/**
 * Is an event object used to represent a single detection of a solar event
 * taken from HEK. This object contains the logic needed for interpolation.
 * Mainly the ability to update the interval of this event and an indicator of
 * it being interpolated or not.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 			Michael Tinglof, Data Mining Lab, Georgia State University
 * 
 */
public class GeneralSTInterpolationEvent extends GeneralSTObject implements ISTInterpolationEvent {

	int id = -1;
	boolean isInterpolated = true;
	EventType type = null; 

	/**
	 * Default constructor that assumes that the object is representing an
	 * interpolated event object.
	 * 
	 * @param timePeriod The valid time for this event representation.
	 * 
	 * @param type       The type of event that this object represents.
	 * 
	 * @param geometry   The geometry of this object.
	 */
	public GeneralSTInterpolationEvent(Interval timePeriod, EventType type, Geometry geometry) {
		super(timePeriod, geometry);
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null in GenaricEvet constructor.");
		this.type = type; 
	}

	/**
	 * Constructor that assumes the object is representing an object from the
	 * database and not one that has been interpolated.
	 * 
	 * @param id         The id of the event in the database so we can reference
	 *                   back to the database to store the interpolated objects
	 *                   between this and another one from the database.
	 * 
	 * @param timePeriod The valid time for this event representation.
	 * 
	 * @param type       The type of event that this object represents.
	 * 
	 * @param geometry   The geometry of this object.
	 */
	public GeneralSTInterpolationEvent(int id, Interval timePeriod, EventType type, Geometry geometry) {
		super(timePeriod, geometry);
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null in GenaricEvet constructor.");
		this.type = type; 
		this.id = id; 
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean isInterpolated() {
		return this.isInterpolated;
	}

	@Override
	public void updateTimePeriod(Interval period) {
		this.timePeriod = period; 

	}

	@Override
	public EventType getType() {
		return this.type;
	}

}
