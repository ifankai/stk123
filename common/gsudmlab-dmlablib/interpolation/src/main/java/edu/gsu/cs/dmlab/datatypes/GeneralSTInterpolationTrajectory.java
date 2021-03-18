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

import java.util.List;
import java.util.SortedSet;

import org.locationtech.jts.geom.Envelope;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;

/**
 * Is an trajectory object used to represent track of a solar event taken from
 * HEK. It is composed of a number of individual reports that together represent
 * the trajectory.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class GeneralSTInterpolationTrajectory extends BaseTemporalObject implements ISTInterpolationTrajectory {

	private List<ISTInterpolationEvent> events = null; 
	boolean interpolated = false; 
	/**
	 * Constructor that takes in the list of ISTInterpolationEvents that compose the
	 * trajectory.
	 * 
	 * @param events The set of event detections that compose the trajectory.
	 */
	public GeneralSTInterpolationTrajectory(List<ISTInterpolationEvent> events) {
		if(events == null) {
			throw new IllegalArgumentException("Event List cannot be null in Interpolation Trajectory constructor.");
		}
		for(ISTInterpolationEvent ist : events) {
			if(ist.isInterpolated()) {
				this.interpolated = true; 
			}
		}
		this.events = events; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public SortedSet<ISTInterpolationEvent> getSTObjects() {
		return (SortedSet<ISTInterpolationEvent>) this.events;
	}

	@Override
	public Envelope getMBR() {
		Envelope env = new Envelope(); 
		for (ISTInterpolationEvent ist : events) {
			env.expandToInclude(ist.getEnvelope()); 
		}
		return env;
	}

	@Override
	public int size() {
		return this.events.size();
	}

	@Override
	public double getVolume() {
		double volume = 0.0;
		for (ISTInterpolationEvent ist : events) {
			volume += ist.getVolume(); 
		}
		return volume;
	}

	@Override
	public EventType getType() {
		return this.events.get(0).getType();
	}

	@Override
	public boolean isInterpolated() {
		return this.interpolated;
	}
}
