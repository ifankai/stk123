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

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;

import java.util.List;

/**
 * This is the public interface for track/event database connections for any
 * project that depends on a database for tracks and events created for the Data
 * Mining Lab at Georgia State University
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ITrackDBConnection {

	/**
	 * Retrieves all of the IEvents of a particular type from the database.
	 * 
	 * @param type The type of IEvent to retrieve from the database. Like Active
	 *             Region or Coronal Hole etc.
	 * 
	 * @return All of the IEvents from the database based on whatever internal
	 *         configuration the implementing class has.
	 */
	public List<ISTTrackingEvent> getAllEvents(EventType type);

	/**
	 * Inserts the links between the IEvents in each of the ITracks. This assumes
	 * that the IEvents in each ITrack are contained in the database already.
	 * 
	 * @param tracks The list of tracks to insert into the database.
	 * 
	 * @param expId  The id of the experiment, since we will most likely be doing
	 *               this on more than one occasion and want to keep results
	 *               separated, this is used to denote which experiment these
	 *               results came from.
	 */
	public void insertTracks(List<ISTTrackingTrajectory> tracks, int expId);

	/**
	 * Retrieves the tracks from the database from a particular experiment.
	 * 
	 * @param type  The type of IEvents to retrieve.
	 * 
	 * @param expId The id of the experimental results to retrieve.
	 * 
	 * @return All of the tracks from the experiment indicated by the id.
	 */
	public List<ISTTrackingTrajectory> getAllTracks(EventType type, int expId);

}
