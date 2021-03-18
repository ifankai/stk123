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
package edu.gsu.cs.dmlab.factory.interfaces;

import java.util.List;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;

/**
 * This interface is for classes that produce Indexes of both
 * {@link edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer
 * ISTTrackingEventIndexer} and
 * {@link edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer
 * ISTTrackingTrajectoryIndexer} type. As well as some of the objects that are
 * needed in such indexes.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTIndexFactory {

	/**
	 * Produces a new IEventIndexer that indexes the IEvent objects passed in.
	 * 
	 * @param regionalList The IEvent objects to be indexed.
	 * @return A new IEventIndexer containing the passed in IEvnet objects.
	 */
	public ISTTrackingEventIndexer getEventIndexer(List<ISTTrackingEvent> regionalList);

	/**
	 * Produces a new ITrackIndexer that indexes the ITrack objects passed in.
	 * 
	 * @param trackList The ISTTrackingTrajectory objects to be indexed.
	 * @return A new ITrackIndexer containing the passed in ITrack objects.
	 */
	public ISTTrackingTrajectoryIndexer getTrackIndexer(List<ISTTrackingTrajectory> trackList);

}
