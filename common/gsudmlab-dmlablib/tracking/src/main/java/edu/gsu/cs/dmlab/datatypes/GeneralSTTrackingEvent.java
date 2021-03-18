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

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;

/**
 * Is an event object used to represent a single detection of a solar event
 * taken from HEK. This object contains the logic needed for tracking. Mainly
 * the ability to link this event to others to form a trajectory.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class GeneralSTTrackingEvent extends GeneralSTObject implements ISTTrackingEvent {

	private int id;
	private EventType type = null;
	private ISTTrackingEvent previous = null;
	private ISTTrackingEvent next = null;

	public GeneralSTTrackingEvent(int id, EventType type, Interval timePeriod, Geometry geometry) {
		super(timePeriod, geometry);
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null in GenaricEvet constructor.");
		this.id = id;
		this.type = type;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void updateTimePeriod(Interval period) {
		this.timePeriod = period;
	}

	@Override
	public EventType getType() {
		return this.type;
	}

	@Override
	public ISTTrackingEvent getPrevious() {
		return this.previous;
	}

	@Override
	public void setPrevious(ISTTrackingEvent event) {
		// If this object doesn't already have a previous event assigned, then we will
		// assign it.
		if (this.previous == null && event != null) {
			this.previous = event;
			event.setNext(this);
		} else if (event == null && this.previous != null) {
			ISTTrackingEvent ev = this.previous;
			this.previous = null;
			ev.setNext(null);
		}
	}

	@Override
	public ISTTrackingEvent getNext() {
		return this.next;
	}

	@Override
	public void setNext(ISTTrackingEvent event) {
		// If this object doesn't already have a next event assigned, then we will
		// assign it.
		if (this.next == null && event != null) {
			this.next = event;
			event.setPrevious(this);
		} else if (event == null && this.next != null) {
			ISTTrackingEvent ev = this.next;
			this.next = null;
			ev.setPrevious(null);
		}

	}

}
