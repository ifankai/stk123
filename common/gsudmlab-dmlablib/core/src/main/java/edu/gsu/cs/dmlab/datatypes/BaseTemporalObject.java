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

import java.util.UUID;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;

/**
 * Is a generic temporal object type. One should derive a class by extending
 * this class if a specific implementation for your specific project is needed.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class BaseTemporalObject implements IBaseTemporalObject {

	private UUID uniqueId = null;
	protected Interval timePeriod = null;

	public BaseTemporalObject() {
		this.uniqueId = UUID.randomUUID();
	}

	public BaseTemporalObject(Interval timePeriod) {
		if (timePeriod == null)
			throw new IllegalArgumentException("Interval cannot be null in BaseTemporalObject constructor.");
		this.timePeriod = timePeriod;
		this.uniqueId = UUID.randomUUID();
	}

	@Override
	public Interval getTimePeriod() {
		return this.timePeriod;
	}

	@Override
	public int compareTime(IBaseTemporalObject baseDataType) {
		return this.timePeriod.getStart().compareTo(baseDataType.getTimePeriod().getStart());
	}

	@Override
	public UUID getUUID() {
		return this.uniqueId;
	}

}
