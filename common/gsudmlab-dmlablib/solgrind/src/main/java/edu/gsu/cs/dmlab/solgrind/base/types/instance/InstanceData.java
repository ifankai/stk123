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
package edu.gsu.cs.dmlab.solgrind.base.types.instance;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.index.InstanceVertex;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class InstanceData {
	public final String id;
	public final EventType type;

	public InstanceData(String id, EventType type) {
		this.id = id;
		this.type = type;
	}

	public InstanceData(Instance ins) {
		this.id = ins.getId();
		this.type = ins.getType();
	}

	public static InstanceData createInstanceData(Instance inst) {
		return new InstanceData(inst.getId(), inst.getType());
	}

	public boolean equals(InstanceData ins) {
		return this.id.equalsIgnoreCase(ins.id) && this.type.equals(ins.type);
	}

	public int hashCode() {
		return this.type.getType().hashCode() + id.hashCode();
	}

	public String toString() {
		return type.toString() + ":" + id;
	}

	public static InstanceData createFromInstanceVertex(InstanceVertex iv) {
		return new InstanceData(iv.getId(), iv.getType());
	}

	public static InstanceVertex convertToInstanceVertex(InstanceData idata) {
		return new InstanceVertex(idata.id, idata.type);
	}

}