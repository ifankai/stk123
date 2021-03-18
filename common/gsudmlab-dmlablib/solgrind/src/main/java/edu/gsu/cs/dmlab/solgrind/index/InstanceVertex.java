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
package edu.gsu.cs.dmlab.solgrind.index;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;

public class InstanceVertex {

	private String id;
	private EventType type;

	public InstanceVertex(String id, EventType type) {
		this.setId(id);
		this.setType(new EventType(type.getType()));
	}

	public InstanceVertex(InstanceVertex v) {
		this.id = v.getId();
		this.type = new EventType(v.getType().getType());
	}

	public InstanceVertex getInstanceVertex(Instance ins) {
		return new InstanceVertex(ins.getId(), ins.getType());
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

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (!(o instanceof InstanceVertex)) {
			return false;
		}

		InstanceVertex iV = (InstanceVertex) o;

		return this.id.equalsIgnoreCase(iV.getId()) && this.type.equals(iV.getType());
	}

	@Override
	public int hashCode() {
		return this.type.getType().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return "InstanceVertex{" + "id='" + id + '\'' + ", type=" + type + '}';
	}
}
