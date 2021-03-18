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

/**
 * This is the public interface for classes used to build tables in the ISD
 * database that are used to store the processing pipeline's state of operation.
 * 
 * @author Dustin Kempton, Surabhi Priya, Data Mining Lab, Georgia State
 *         University
 *
 */
public interface IISDStateDBCreator {

	/**
	 * Checks if the table used for storing processing state is in the database.
	 * 
	 * @return True if the table used for storing processing state exists, False
	 *         otherwise.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean checkStateTableExists() throws SQLException;

	/**
	 * Builds the table used for storing processing state of the ISD processing
	 * pipeline.
	 * 
	 * @return True if successful, False otherwise.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean createStateTable() throws SQLException;

}
