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
 * Connection to the database of feature feature values representing the fitness
 * of a parameter for representing an event type.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IFeatureDBConnection {

	/**
	 * Selects the top features in the database and returns the array of their
	 * descriptors
	 * 
	 * @param type The event type to select features for
	 * 
	 * @param num  The number of features to return
	 * 
	 * @return The array of the top features in the database
	 * 
	 * @throws SQLException If an error occurs
	 */
	public ImageDBWaveParamPair[] getBestFeatures(EventType type, int num) throws SQLException;

	/**
	 * Checks to see if the feature table for the particular event type exists.
	 * 
	 * @param type The event type to check for.
	 * 
	 * @return True if exists, else false.
	 * 
	 * @throws SQLException If an error occurs
	 */
	public boolean featureTableExists(EventType type) throws SQLException;
}
