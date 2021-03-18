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
package edu.gsu.cs.dmlab.tracking.stages;

import edu.gsu.cs.dmlab.factory.interfaces.ISTEventTrackingFactory;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;
import edu.gsu.cs.dmlab.util.interfaces.ISTSearchAreaProducer;

/**
 * The first iteration of second stage in the iterative tracking algorithm of
 * Kempton et. al
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class StageTwo extends BaseUpperStage {
	private static int stage = 2;

	/**
	 * Constructor
	 * 
	 * @param predictor    Used for predicting the location of an event given its
	 *                     current location and an elapsed time period.
	 * 
	 * @param factory      Factory used to create new objects needed to process the
	 *                     tracks at this stage.
	 * 
	 * @param tracksIdxr   Indexer of the current tracks to process in this stage.
	 * 
	 * @param evntIdxr     Indexer of all events taken as input into the program at
	 *                     the beginning. This is mostly used to determine the
	 *                     observation probability in the association problem.
	 * 
	 * @param maxFrameSkip The maximum number of skipped frames allowed between a
	 *                     detection and its possible association match.
	 * 
	 * @param numThreads   The number of threads to use when processing batch. -1
	 *                     for all available &gt; 0 for a specific number.
	 */
	public StageTwo(ISTSearchAreaProducer predictor, ISTEventTrackingFactory factory,
			ISTTrackingTrajectoryIndexer tracksIdxr, ISTTrackingEventIndexer evntIdxr, int maxFrameSkip,
			int numThreads) {
		super(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip, stage, numThreads);
	}

}
