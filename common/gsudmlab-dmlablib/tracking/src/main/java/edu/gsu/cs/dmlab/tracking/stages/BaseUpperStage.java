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

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ISTEventTrackingFactory;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTAssociationProblem;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTProcessingStage;
import edu.gsu.cs.dmlab.util.MotionUtils;
import edu.gsu.cs.dmlab.util.interfaces.ISTSearchAreaProducer;

import org.joda.time.Interval;
import org.joda.time.Seconds;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * Is the base of the second and third stages in the iterative tracking
 * algorithm of Kempton et. al
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Thaddeus Gholston and Dustin Kempton Data Mining Lab, Georgia State
 *         University
 * 
 */
public class BaseUpperStage implements ISTProcessingStage {

	/**
	 * Constant: number of seconds in a day.
	 */
	private static final double SECONDS_TO_DAYS = 60.0 * 60.0 * 24.0;

	private ISTTrackingTrajectoryIndexer tracksIdxr;
	private ISTTrackingEventIndexer evntIdxr;
	private ISTSearchAreaProducer predictor;
	private ISTEventTrackingFactory factory;
	private ForkJoinPool forkJoinPool = null;

	protected int maxFrameSkip;
	private int stage;

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
	 * @param stage        The stage that this base is the base of. It tells the
	 *                     association problem's edge weight calculator how to
	 *                     calculate the edge weights as stage 2 is different than
	 *                     3+.
	 * 
	 * @param numThreads   The number of threads to use when processing batch. -1
	 *                     for all available &gt; 0 for a specific number.
	 */
	public BaseUpperStage(ISTSearchAreaProducer predictor, ISTEventTrackingFactory factory,
			ISTTrackingTrajectoryIndexer tracksIdxr, ISTTrackingEventIndexer evntIdxr, int maxFrameSkip, int stage,
			int numThreads) {

		if (predictor == null)
			throw new IllegalArgumentException("Search Area Producer cannot be null.");
		if (factory == null)
			throw new IllegalArgumentException("Event Tracking Factory cannot be null.");
		if (tracksIdxr == null)
			throw new IllegalArgumentException("Track Indexer cannot be null.");
		if (evntIdxr == null)
			throw new IllegalArgumentException("Event Indexer cannot be null.");
		if (maxFrameSkip < 0)
			throw new IllegalArgumentException("maxFameSkip cannot be less than zero.");
		if (stage < 1)
			throw new IllegalArgumentException("stage cannot be less than one.");
		if (numThreads < -1 || numThreads == 0)
			throw new IllegalArgumentException("numThreads must be -1 or > 0 in BaseUpperStage constructor.");

		this.predictor = predictor;
		this.tracksIdxr = tracksIdxr;
		this.evntIdxr = evntIdxr;
		this.maxFrameSkip = maxFrameSkip;
		this.factory = factory;
		this.stage = stage;

		if (numThreads == -1) {
			this.forkJoinPool = new ForkJoinPool();
		} else {
			this.forkJoinPool = new ForkJoinPool(numThreads);
		}

	}

	@Override
	public void finalize() throws Throwable {
		this.forkJoinPool.shutdownNow();
		this.forkJoinPool = null;

		this.tracksIdxr = null;
		this.evntIdxr = null;
		this.predictor = null;
		this.factory = null;
	}

	@Override
	public List<ISTTrackingTrajectory> process() {

		List<ISTTrackingTrajectory> vectOfTracks = this.tracksIdxr.getAll();

		if (vectOfTracks.size() > 0) {
			ISTAssociationProblem graph = this.factory.getAssociationProblem(vectOfTracks, evntIdxr, this.stage);

			// for each track that we got back in the list we will find the
			// potential matches for that track and link it to the one with the
			// highest probability of being a match.
			try {
				this.forkJoinPool.submit(() -> {
					IntStream.range(0, vectOfTracks.size()).parallel().forEach(i -> {

						// we get the event associated with the end of our
						// current track as that is the last frame of the track
						// and has position information and image information
						// associated with it.
						ISTTrackingTrajectory currentTrack = vectOfTracks.get(i);
						ISTTrackingEvent currentEvent = currentTrack.getLast();

						double span;
						Geometry searchArea;
						HashMap<UUID, ISTTrackingTrajectory> potentialMap = new HashMap<UUID, ISTTrackingTrajectory>();

						// get the search area to find tracks that may belong
						// linked to the current one being processed
						span = Seconds.secondsIn(currentEvent.getTimePeriod()).getSeconds() / SECONDS_TO_DAYS;
						// set the time span to search in as the end of our
						// current track+the the span of the frame for the last
						// event in our current track being processed.
						Interval currentSearchTime = new Interval(currentEvent.getTimePeriod().getEnd(),
								currentEvent.getTimePeriod().toDuration());

						float[] motionVect = null;
						if (currentTrack.size() < 2) {
							searchArea = this.predictor.getSearchRegion(currentEvent.getEnvelope(), span);

						} else {
							motionVect = MotionUtils.trackMovement(currentTrack);
							searchArea = this.predictor.getSearchRegion(currentEvent.getEnvelope(), motionVect, span);

						}
						List<ISTTrackingTrajectory> potentialTracks = this.tracksIdxr.search(currentSearchTime,
								searchArea);

						for (ISTTrackingTrajectory trk : potentialTracks) {
							if (!potentialMap.containsKey(trk.getUUID()) && trk.getTimePeriod()
									.getStartMillis() >= currentTrack.getTimePeriod().getEndMillis()) {
								potentialMap.put(trk.getUUID(), trk);
							}
						}
						// search locations for potential matches up to the
						// maxFrameSkip away using the previously predicted
						// search area as the starting point for the next search
						// area.
						for (int j = 0; j < maxFrameSkip; j++) {

							// update the currentSearchTime to the next frame
							currentSearchTime = new Interval(currentSearchTime.getEnd(),
									currentSearchTime.toDuration());

							// if we don't have a motion vector then use diff
							// rotation
							if (motionVect == null) {
								// update search area for next frame
								Envelope rect = searchArea.getEnvelopeInternal();
								searchArea = this.predictor.getSearchRegion(rect, span);

								// search next frame
								potentialTracks = this.tracksIdxr.search(currentSearchTime, searchArea);

							} else {
								// update search area for next frame using
								// motion vector
								Envelope rect = searchArea.getEnvelopeInternal();
								searchArea = this.predictor.getSearchRegion(rect, motionVect, span);

								// search next frame
								potentialTracks = this.tracksIdxr.search(currentSearchTime, searchArea);

							}

							// put potential matches not in potentialTracks list
							// into the list
							for (ISTTrackingTrajectory trk : potentialTracks) {

								if (!potentialMap.containsKey(trk.getUUID()) && trk.getTimePeriod()
										.getStartMillis() >= currentTrack.getTimePeriod().getEndMillis()) {
									potentialMap.put(trk.getUUID(), trk);
								}

							}
						}

						// if the map of potential matches has anything in it,
						// then we will process those tracks by adding them to
						// the graph problem
						if (potentialMap.size() >= 1) {
							for (ISTTrackingTrajectory tmpTrk : potentialMap.values()) {
								graph.addAssociationPossibility(currentTrack, tmpTrk);
							}
						}
					});
				}).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			// if the graph problem was solved then return the results
			// System.out.println("Solving Association Problem.");
			if (graph.solve()) {
				// System.out.println("Association Problem Solved, linking.");
				return graph.getTrackLinked();
			}
		}
		// if solve didn't work then just return the original list.
		return this.tracksIdxr.getAll();

	}

}