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
package edu.gsu.cs.dmlab.solgrind.base;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

/**
 * Class for modeling spatiotemporal event instances. Instances are moving
 * objects modeled by Trajectory objects
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class Instance implements Comparable<Instance> {

	private static final EventType INVALID_TYPE = new EventType("INVALID_EVENT_TYPE");

	private String id;
	private EventType type;
	private Trajectory trajectory;

	public Instance(String iid, EventType e) {
		this.id = iid;
		setType(new EventType(e.getType()));
		trajectory = new Trajectory();
	}

	public Instance(String iid) {
		setId(iid);
		setType(INVALID_TYPE);
		trajectory = new Trajectory();
	}

	public Instance(String iid, EventType e, Trajectory traj) {
		setId(iid);
		setType(e);
		trajectory = traj;
	}

	public Interval getInterval() {
		return new Interval(getStartTime(), getEndTime());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public DateTime getStartTime() {
		return trajectory.getStartTime();
	}

	public DateTime getEndTime() {
		return trajectory.getEndTime();
	}

	public Trajectory getTrajectory() {
		return this.trajectory;
	}

	public boolean equals(Instance ins) {
		return this.id == ins.id && this.type.equals(ins.type);
	}

	public int hashCode() {
		return this.type.getType().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return "Instance{" + "id='" + id + '\'' + ", type=" + type + ", trajectory=" + trajectory + '}';
	}

	@Override
	public int compareTo(Instance o) {
		return this.getStartTime().compareTo(o.getStartTime());
	}
}
