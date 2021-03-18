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
import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.datatypes.EventType;

/**
 * This is the public interface for classes used to access the processing
 * pipeline's state of operation tables.
 * 
 * @author Dustin Kempton, Surabhi Priya, Data Mining Lab, Georgia State
 *         University
 *
 */
public interface IISDStateDBConnector {

	/**
	 * Checks if this part of the processing pipeline is cleared to run its
	 * operations again.
	 * 
	 * @param type The ISD Solar Event type that the processing flag is to be
	 *             checked for.
	 * 
	 * @return True if this part of the processing pipeline is cleared to run.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean checkOKToProcess(EventType type) throws SQLException;

	/**
	 * Sets a processing flag to keep other parts of the processing pipeline from
	 * executing while this part is still performing its operations.
	 * 
	 * @param type The ISD Solar Event type that the is processing flag is to be set
	 *             for.
	 * 
	 * @return True if successfully set the processing flag, False otherwise.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean setIsProcessing(EventType type) throws SQLException;

	/**
	 * Sets a processing flag to allow other parts of the processing pipeline to
	 * execute since this process has finished its operations.
	 * 
	 * @param type The ISD Solar Event type that the is processing flag is to be set
	 *             for.
	 * 
	 * @return True if successfully set the processing flag, False otherwise.
	 * 
	 * @throws SQLException           If there was an SQLException that occurred
	 *                                while attempting to execute.
	 * 
	 * @throws IllegalAccessException If this is called for a process that has not
	 *                                been working on the given table. So, the state
	 *                                is not being set to 'FALSE' to avoid some
	 *                                possible mistakes.
	 */
	public boolean setFinishedProcessing(EventType type) throws SQLException, IllegalAccessException;

	/**
	 * Updates the state table with the passed in datetime to indicate the last
	 * report time this part of the processing pipeline successfully executed.
	 * 
	 * @param type          The ISD Solar Event type that the processed time is to
	 *                      be updated for.
	 * 
	 * @param processedTime The time to update the database with.
	 * 
	 * @return True if update was successful, False otherwise.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public boolean updateLastProcessedTime(EventType type, DateTime processedTime) throws SQLException;

	/**
	 * Gets the last report time this part of the processing pipeline successfully
	 * executed its operations on. This is to know what data to process in HEK and
	 * update in the ISD database.
	 * 
	 * @param type The ISD Solar Event type that the processed time is to be checked
	 *             for.
	 * 
	 * @return The date and time of the last time this part of the processing
	 *         pipeline executed its operations.
	 * 
	 * @throws SQLException If there was an SQLException that occurred while
	 *                      attempting to execute.
	 */
	public DateTime getLastProcessedTime(EventType type) throws SQLException;

}
