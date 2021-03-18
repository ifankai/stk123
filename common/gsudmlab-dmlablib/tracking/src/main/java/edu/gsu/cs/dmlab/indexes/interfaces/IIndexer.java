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
package edu.gsu.cs.dmlab.indexes.interfaces;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;

import java.util.List;

/**
 * The base interface for indexes that store objects of IBaseDatType or derived
 * from objects that implement that data type.
 * 
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public interface IIndexer<T extends IBaseTemporalObject> {

	/**
	 * Gets the earliest start time in the index. That way you can check before you
	 * waste time querying the index for objects that don't exist.
	 * 
	 * @return The earliest start time in the index.
	 */
	public DateTime getFirstTime();

	/**
	 * Gets the latest end time in the index. That way you can check before you
	 * waste time querying the index for objects that don't exist.
	 * 
	 * @return The latest end time in the index.
	 */
	public DateTime getLastTime();

	/**
	 * Searches the index for any objects that intersect the query time and the
	 * query search area. This method is intended to look forward in time for these
	 * objects as is done to find the next possible detection for a given detection.
	 * 
	 * @param timePeriod
	 *            The time period to query the index with.
	 * @param searchArea
	 *            The search area to search for intersections with.
	 * @return A list of the objects in the index that intersect the query time and
	 *         the query search area.
	 */
	public List<T> search(Interval timePeriod, Geometry searchArea);

	/**
	 * Returns the list of all objects in the index.
	 * 
	 * @return A list of all the objects in the index.
	 */
	public List<T> getAll();
}