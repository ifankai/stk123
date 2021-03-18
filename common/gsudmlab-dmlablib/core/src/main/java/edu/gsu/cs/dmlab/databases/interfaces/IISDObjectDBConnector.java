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
import edu.gsu.cs.dmlab.datatypes.interfaces.IISDEventReport;
import edu.gsu.cs.dmlab.exceptions.InvalidAttributeException;

/**
 * This is the public interface for classes used to connect to the ISD object
 * storage database within this project.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IISDObjectDBConnector {

	/**
	 * Checks if the object report is already stored in the object storage database
	 * of the ISD project.
	 * 
	 * @param type   The type of event report that is to be checked for.
	 * 
	 * @param sdb_id The identifier that is unique to that event report (for
	 *               sdb_solardb).
	 * 
	 * @return True if the object event report is in the database, False otherwise.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean checkReportInDB(EventType type, String sdb_id) throws SQLException;

	/**
	 * Inserts the event report into the appropriate table based on the "event_type"
	 * that was reported in the HEK.
	 * 
	 * @param eventReport The event report to store into the appropriate table.
	 * 
	 * @return True when successful, False if not.
	 * 
	 * @throws SQLException              If there was an SQLException that occurred
	 *                                   while attempting to execute.
	 * @throws InvalidAttributeException
	 */
	public boolean insertEventReport(IISDEventReport eventReport) throws SQLException, InvalidAttributeException;
}
