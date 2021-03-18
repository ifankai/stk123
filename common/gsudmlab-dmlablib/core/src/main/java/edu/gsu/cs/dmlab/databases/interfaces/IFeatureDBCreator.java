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
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;

/**
 * Creates database for storing feature values representing the fitness of a
 * parameter for representing an event type.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IFeatureDBCreator {

	/**
	 * Creates the table for holding the scoring of a particular feature for a
	 * particular event type.
	 * 
	 * @param type The event type to create the table for
	 * 
	 * @return True if successful, else false
	 * 
	 * @throws SQLException If an error occurs
	 */
	public boolean createFeatureScoreTable(EventType type) throws SQLException;

	/**
	 * Inserts the statistic value for the feature of a particular wavelength and
	 * event type.
	 * 
	 * @param type    The event type to insert the statistic value for
	 * 
	 * @param id      The feature to insert the statistic value for
	 * 
	 * @param statVal The value to insert
	 * 
	 * @return True when successful
	 * 
	 * @throws SQLException If an error occurs
	 */
	public boolean insertParamFeatureStatVal(EventType type, ImageDBWaveParamPair id, float statVal)
			throws SQLException;

}
