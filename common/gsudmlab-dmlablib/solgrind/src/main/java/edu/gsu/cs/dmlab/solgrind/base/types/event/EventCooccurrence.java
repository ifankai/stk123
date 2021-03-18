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
package edu.gsu.cs.dmlab.solgrind.base.types.event;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.gsu.cs.dmlab.solgrind.base.EventType;

/**
 * This class is will be used for ST co-occurrence pattern. (Naming is
 * EventCooccurrence because not every set of event co-occurrences are
 * necessarily frequent patterns)
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class EventCooccurrence implements Comparable<EventCooccurrence> {

	private Set<EventType> eventTypes;

	public EventCooccurrence(Collection<EventType> eventTypes) {
		this.eventTypes = new TreeSet<EventType>(eventTypes);
	}

	public EventCooccurrence() {
		eventTypes = new TreeSet<>();
	}

	/**
	 * copy constructor
	 * 
	 * @param eventCooccurrence
	 */
	public EventCooccurrence(EventCooccurrence eventCooccurrence) {
		eventTypes = new TreeSet<>();
		for (EventType otherEvent : eventCooccurrence.getEventTypes()) {
			eventTypes.add(new EventType(otherEvent.getType()));
		}
	}

	public void addEventType(EventType eventType) {
		this.eventTypes.add(eventType);
	}

	public Set<EventType> getEventTypes() {
		return eventTypes;
	}

	public String toString() {
		return "EventCO:" + this.eventTypes.toString();
	}

	public int getCardinality() {
		return this.eventTypes.size();
	}

	public boolean equals(Object et) {
		if (et instanceof EventCooccurrence) {
			if (this != null && et != null) {
				return this.eventTypes.containsAll((Collection<?>) et);
			} else { // at least one of the ECs is null
				return false;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = 13;
		if (this != null) {
			for (EventType e : this.eventTypes) {
				i += e.hashCode();
			}
		}
		return i;
	}

	public EventCooccurrence union(EventCooccurrence eventCooccurrence) {
		SortedSet<EventType> es1 = new TreeSet<EventType>(eventTypes);
		SortedSet<EventType> es2 = new TreeSet<EventType>(eventCooccurrence.getEventTypes());
		es1.addAll(es2);
		return new EventCooccurrence(es1);
	}

	@Override
	public int compareTo(EventCooccurrence o) {
		if (this.getCardinality() > o.getCardinality()) {
			return -1;
		} else if (this.getCardinality() < o.getCardinality()) {
			return 1;
		} else {
			return this.eventTypes.toString().compareTo(o.eventTypes.toString());
		}
	}

}
