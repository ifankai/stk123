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
package edu.gsu.cs.dmlab.imageproc.interfaces;

import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;

/**
 * Interface for methods of extracting histogram from a data source that
 * represents the passed in IEvent in the requested dimensions.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTHistogramProducer {

	/**
	 * Computes the histogram of the passed in IEvent using the requested dimensions
	 * of the underlying data souce.
	 * 
	 * @param event  The event to extract a histogram of.
	 * 
	 * @param params The dimensions to use of the underlying data source.
	 * 
	 * @param left   Indicates if the event is on the left side of a gap in
	 *               detections. If true the histogram is extracted from the frame
	 *               at the end of the event's valid time. Otherwise it is extracted
	 *               from the frame at the beginning of the passed in event's valid
	 *               time.
	 * 
	 * @return The histogram that represents the passed in event.
	 */
	int[][] getHist(ISpatialTemporalObj event, ImageDBWaveParamPair[] params, boolean left);
}