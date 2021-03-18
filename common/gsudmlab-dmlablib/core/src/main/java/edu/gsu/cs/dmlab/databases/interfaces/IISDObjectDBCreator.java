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
package edu.gsu.cs.dmlab.databases.interfaces;

import java.sql.SQLException;

import edu.gsu.cs.dmlab.datatypes.EventType;

/**
 * This is the public interface for classes used to build the object detection
 * report database used by the ISD
 * 
 * @author Surabhi Priya, Data Mining Lab, Georgia State University
 *
 */
public interface IISDObjectDBCreator {

	/**
	 * Checks the ISD object storage database to see if the table to store the
	 * passed in event type exists.
	 * 
	 * @param type The event type to check if the table exists for.
	 * 
	 * @return True if the table exists, False otherwise.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean checkTableExists(EventType type) throws SQLException;

	/**
	 * Creates a table to store the passed in event type in the ISD object storage
	 * database.
	 * 
	 * @param type The event type to create the table for.
	 * 
	 * @return True when successful, False if not.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean createTable(EventType type) throws SQLException;

}
