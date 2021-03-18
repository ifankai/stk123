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

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;

import edu.gsu.cs.dmlab.datatypes.BaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;

/**
 * Is an trajectory object used to represent track of a solar event taken from
 * HEK. It is composed of a number of individual reports linked together to
 * represent the trajectory. This object contains the logic needed for tracking.
 * Mainly the ability to traverse a doubly linked list of event detections to
 * update the list of detections that are in the trajectory.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class GeneralSTTrackingTrajectory extends BaseTemporalObject implements ISTTrackingTrajectory {

	private ISTTrackingEvent headEvent = null;
	private ISTTrackingEvent tailEvent = null;
	private boolean outDated = true;
	private TreeSet<ISTTrackingEvent> eventsList = null;
	private Lock loc;

	/**
	 * Constructor that uses a head and tail event to point to the head of the
	 * doubly linked list of detections and the tail of the linked list of
	 * detections. This is to save having to traverse the list
	 * 
	 * @param headEvent The head of the linked list of event detections.
	 * 
	 * @param tailEvent The tail of the linked list of event detections.
	 */
	public GeneralSTTrackingTrajectory(ISTTrackingEvent headEvent, ISTTrackingEvent tailEvent) {
		if (headEvent == null)
			throw new IllegalArgumentException("Head Event cannot be null in TrackingSTTrajectory constructor.");
		if (tailEvent == null)
			throw new IllegalArgumentException("Tail Event cannot be null in TrackingSTTrajectory constructor.");
		this.timePeriod = new Interval(headEvent.getTimePeriod().getStart(), tailEvent.getTimePeriod().getEnd());

		this.headEvent = headEvent;
		this.tailEvent = tailEvent;
		this.loc = new ReentrantLock();
	}

	/**
	 * Constructor that assumes that the head and tail event of the linked list of
	 * detections is the same object.
	 * 
	 * @param event The event detection to be set as both the head and tail of the
	 *              linked list of event detections that represent this trajectory.
	 */
	public GeneralSTTrackingTrajectory(ISTTrackingEvent event) {
		if (event == null)
			throw new IllegalArgumentException("Event cannot be null in TrackingSTTrajectory constructor.");
		this.timePeriod = event.getTimePeriod();
		this.headEvent = event;
		this.tailEvent = event;
		this.loc = new ReentrantLock();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SortedSet<ISTTrackingEvent> getSTObjects() {
		this.getFirst();
		this.getLast();
		if (this.outDated) {
			this.update();
		}
		return (SortedSet<ISTTrackingEvent>) this.eventsList.clone();
	}

	@Override
	public Envelope getMBR() {
		this.getFirst();
		this.getLast();
		if (this.outDated) {
			this.update();
		}
		Envelope env = new Envelope();
		for (ISTTrackingEvent ist : eventsList) {
			env.expandToInclude(ist.getEnvelope());
		}
		return env;
	}

	@Override
	public int size() {
		this.getFirst();
		this.getLast();
		if (this.outDated) {
			this.update();
		}
		return this.eventsList.size();
	}

	@Override
	public double getVolume() {
		this.getFirst();
		this.getLast();
		double volume = 0.0;
		if (this.outDated) {
			this.update();
		}
		for (ISTTrackingEvent ist : eventsList) {
			volume += ist.getVolume();
		}
		return volume;
	}

	@Override
	public Interval getTimePeriod() {
		this.getFirst();
		this.getLast();
		if (this.outDated) {
			this.update();
		}
		return this.timePeriod;
	}

	@Override
	public ISTTrackingEvent getFirst() {
		this.loc.lock();
		while (this.headEvent.getPrevious() != null) {
			this.headEvent = this.headEvent.getPrevious();
			this.outDated = true;
		}
		this.loc.unlock();
		return this.headEvent;
	}

	@Override
	public ISTTrackingEvent getLast() {
		this.loc.lock();
		while (this.tailEvent.getNext() != null) {
			this.tailEvent = this.tailEvent.getNext();
			this.outDated = true;
		}
		this.loc.unlock();
		return this.tailEvent;
	}

	@Override
	public EventType getType() {
		return this.getFirst().getType();
	}

	@Override
	public int compareTime(IBaseTemporalObject baseDataType) {
		return this.getFirst().getTimePeriod().getStart()
				.compareTo(((ISTTrackingTrajectory) baseDataType).getFirst().getTimePeriod().getStart());
	}

	private void update() {
		ISTTrackingEvent ev = this.getFirst();

		TreeSet<ISTTrackingEvent> evList = new TreeSet<ISTTrackingEvent>(IBaseTemporalObject.baseTemporalComparator);
		evList.add(ev);

		while (ev.getNext() != null) {
			ev = ev.getNext();
			evList.add(ev);
		}

		this.timePeriod = new Interval(this.getFirst().getTimePeriod().getStart(),
				this.getLast().getTimePeriod().getEnd());
		this.eventsList = evList;
		this.outDated = false;
	}

}
