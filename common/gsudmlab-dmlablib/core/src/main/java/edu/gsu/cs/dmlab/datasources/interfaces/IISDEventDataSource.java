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
package edu.gsu.cs.dmlab.datasources.interfaces;

import java.util.List;

import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.datatypes.EventType;

import edu.gsu.cs.dmlab.datatypes.interfaces.IISDEventReport;

/**
 * This is the public interface for classes used to retrieve event reports
 * coming from the source location that are intended to be inserted into our
 * database.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IISDEventDataSource {

	/**
	 * Retrieves all of the event reports of a particular type from the datasource
	 * that meet the startTime endTime requirements of having a start time between
	 * the two.
	 * 
	 * @param startTime
	 *            The start time of the event search in the datasource. The start
	 *            time of the event will be on or after this time.
	 * 
	 * @param endTime
	 *            The end time of the event search in the datasource. The start time
	 *            of the event will be before this time, but after startTime.
	 * 
	 * @param type
	 *            The event type to search in the datasource for.
	 * 
	 * @return A list of the events that have a start time between startTime and
	 *         endTime and are of the type specified in the search input.
	 */
	public List<IISDEventReport> getReports(DateTime startTime, DateTime endTime, EventType type);

}
