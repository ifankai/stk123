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

import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.features.interfaces.IStatProducer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTAssociationProblem;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTMotionModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTProcessingStage;

/**
 * The public interface for classes that shall be used to create the objects
 * used in event tracking.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTEventTrackingFactory {

	/**
	 * Creates a new track containing the passed in IEvent object.
	 * 
	 * @param event The IEvent for the track to contain.
	 * @return The track containing the passed in IEvent.
	 */
	public ISTTrackingTrajectory getTrack(ISTTrackingEvent event);

	/**
	 * Produces a new Graph that depicts the possible paths tracks can take through
	 * multiple shorter track fragments.
	 * 
	 * @param tracks    List of track fragments to build the association graph from.
	 * 
	 * @param evntsIdxr The indexer of the events that are contained in all the
	 *                  tracks passed in. Which is used to calculate the expected
	 *                  change in the number of detections over a period of time.
	 * 
	 * @param stage     The stage which the association problem shall be used. This
	 *                  matters because we calculate the weights differently based
	 *                  on what stage we are in.
	 * 
	 * @return The graph of possible association paths.
	 */
	public ISTAssociationProblem getAssociationProblem(List<ISTTrackingTrajectory> tracks,
			ISTTrackingEventIndexer evntsIdxr, int stage);

	/**
	 * Gets a model for producing probability values based on the number of skipped
	 * frames after a particular track another track starts.
	 * 
	 * @return The fame skip model.
	 */
	public ISTFrameSkipModel getSkipModel();

	/**
	 * Gets a model for producing probability values based on the input objects
	 * location and size to represent how likely the input object is to be the
	 * beginning of a track.
	 * 
	 * @return The enter model object.
	 */
	public ISTLocationProbCal getEnterModel();

	/**
	 * Gets a model for producing probability values based on the input objects
	 * location and size to represent how likely the input object is to be the end
	 * of a track.
	 * 
	 * @return The exit model object.
	 */
	public ISTLocationProbCal getExitModel();

	/**
	 * Gets a model for producing probability values based on how similar the
	 * movement of two input tracks is.
	 * 
	 * @return The motion model object.
	 */
	public ISTMotionModel getMotionModel();

	/**
	 * Gets a model for producing probability values to indicate how likely an
	 * object is a true detection of an event or is a false detection.
	 * 
	 * @param evntsIdxr An indexer object that contains event count per frame data
	 *                  for event reports in the temporal vicinity of an input
	 *                  object
	 * 
	 * @return The observation model
	 */
	public ISTObsModel getObservationModel(ISTTrackingEventIndexer evntsIdxr);

	/**
	 * Gets a model for producing probability values to indicate how visually
	 * similar two tracks are at their point of possible joining.
	 * 
	 * @param params
	 * 
	 * @return
	 */
	public ISTAppearanceModel getAppearanceModel(ImageDBWaveParamPair[] params);

	public IStatProducer getStatProducer(List<ISTTrackingTrajectory> tracks);

	public ISTProcessingStage getStage1(ISTTrackingEventIndexer eventIndexer);

	public ISTProcessingStage getStage2(ISTTrackingEventIndexer eventIndexer, ISTTrackingTrajectoryIndexer tracksIdxr,
			int maxFrameSkip);

	public ISTProcessingStage getStage3(ISTTrackingEventIndexer eventIndexer, ISTTrackingTrajectoryIndexer tracksIdxr,
			int maxFrameSkip);
}
