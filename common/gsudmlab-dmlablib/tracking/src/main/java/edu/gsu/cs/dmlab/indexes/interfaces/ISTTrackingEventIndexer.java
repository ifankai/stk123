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

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;

/**
 * This interface is for indexes that will index ISTTrackingEvent based on the
 * {@link edu.gsu.cs.dmlab.indexes.interfaces.IIndexer IIndexer} generic
 * interface. It has the added function of requiring the ability to get the
 * expected change in the number of event detections per frame for a period in
 * the index.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTTrackingEventIndexer extends IIndexer<ISTTrackingEvent> {

	/**
	 * Computes the expected change in the number of detections in each frame. The
	 * length of the frame is set internally but the time that this expected change
	 * is to be computed over is passed in as a parameter.
	 * 
	 * @param timePeriod The period over which the expected change is to be
	 *                   computed.
	 * @return The expected change in the number of detections per frame based on
	 *         the passed in period of time.
	 */
	int getExpectedChangePerFrame(Interval timePeriod);

	/**
	 * Returns the event type that is indexed in this event indexer.
	 * 
	 * @return Event type of the events in the indexer.
	 */
	public EventType getTypeIndexed();
}
