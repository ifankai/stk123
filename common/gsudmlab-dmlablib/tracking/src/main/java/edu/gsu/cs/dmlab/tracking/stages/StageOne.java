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
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTProcessingStage;
import edu.gsu.cs.dmlab.util.Utility;
import edu.gsu.cs.dmlab.util.interfaces.ISTSearchAreaProducer;

import org.joda.time.Interval;
import org.joda.time.Seconds;
import org.locationtech.jts.geom.Geometry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * The first stage in the iterative tracking algorithm of Kempton et. al
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>. It uses the
 * search area to link events into tracks iff there is one and only one
 * available event as a possibility.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class StageOne implements ISTProcessingStage {

	private ISTSearchAreaProducer searchAreaProducer;
	private ISTTrackingEventIndexer eventIndexer;
	private ISTEventTrackingFactory factory;
	private ForkJoinPool forkJoinPool = null;

	/**
	 * Constructor for stage one of tracking.
	 * 
	 * @param searchAreaProducer The search area producer used to produce locations
	 *                           to search for the next detection to match to.
	 * @param eventIndexer       An index object that contains the spatiotemporal
	 *                           location of each object in the dataset.
	 * @param factory            The factory object used to create new track
	 *                           objects.
	 * @param numThreads         The number of threads to use when processing batch.
	 *                           -1 for all available &gt; 0 for a specific number.
	 */
	public StageOne(ISTSearchAreaProducer searchAreaProducer, ISTTrackingEventIndexer eventIndexer,
			ISTEventTrackingFactory factory, int numThreads) {
		if (eventIndexer == null)
			throw new InvalidParameterException("IEventIndexer cannot be null");
		if (searchAreaProducer == null)
			throw new InvalidParameterException("IPositionPredictor cannot be null");
		if (factory == null)
			throw new InvalidParameterException("Object Factory cannot be null");
		if (numThreads < -1 || numThreads == 0)
			throw new IllegalArgumentException("numThreads must be -1 or > 0 in BaseUpperStage constructor.");

		this.searchAreaProducer = searchAreaProducer;
		this.eventIndexer = eventIndexer;
		this.factory = factory;

		if (numThreads == -1) {
			this.forkJoinPool = new ForkJoinPool();
		} else {
			this.forkJoinPool = new ForkJoinPool(numThreads);
		}

	}

	@Override
	public void finalize() throws Throwable {
		this.searchAreaProducer = null;
		this.eventIndexer = null;
		this.factory = null;

		if (this.forkJoinPool != null) {
			this.forkJoinPool.shutdownNow();
			this.forkJoinPool = null;
		}
	}

	@Override
	public List<ISTTrackingTrajectory> process() {
		/*
		 * for each event between start and end, find events that are in the next frame
		 * after the current one we are looking at. Then link an event and the current
		 * one together iff it is the only one in the search box determined by our
		 * position predictor and the length of the current event.
		 */

		List<ISTTrackingEvent> events = this.eventIndexer.getAll();

		try {
			this.forkJoinPool.submit(() -> {
				events.parallelStream().forEach(e -> linkToNext(e));
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		// Now we determine how many tracks we have.
		HashMap<UUID, ISTTrackingTrajectory> tracksMap = new HashMap<>();
		for (int i = 0; i < events.size(); i++) {
			ISTTrackingEvent currEvent = events.get(i);
			while (currEvent.getPrevious() != null) {
				currEvent = currEvent.getPrevious();
			}

			// if it is already in the map then move along.
			UUID key = currEvent.getUUID();
			if (!tracksMap.containsKey(key)) {
				// create a track and add it to the map
				ISTTrackingTrajectory track = this.factory.getTrack(currEvent);
				tracksMap.put(key, track);
			}
		}

		ArrayList<ISTTrackingTrajectory> results = new ArrayList<ISTTrackingTrajectory>();

		tracksMap.forEach((id, track) -> {
			results.add(track);
		});
		return results;
	}

	private void linkToNext(ISTTrackingEvent event) {
		// only if the event isn't already linked to another event do we do
		// anything with it.
		if (event.getNext() == null) {
			// get the search are for the neighborhood
			double span = Seconds.secondsIn(event.getTimePeriod()).getSeconds() / Utility.SECONDS_TO_DAYS;
			Geometry nextSearchArea = this.searchAreaProducer.getSearchRegion(event.getEnvelope(), span);
			Geometry prevSearchArea = this.searchAreaProducer.getSearchRegionBack(event.getEnvelope(), span);
			// get the time interval for the neighborhood
			Interval time = event.getTimePeriod();
			Interval nextTime = new Interval(time.getEndMillis() + 1, time.getEndMillis() + time.toDurationMillis());
			Interval prevTime = new Interval(time.getStartMillis() - time.toDurationMillis(),
					time.getStartMillis() - 1);

			// Get the events in the neighborhood
			List<ISTTrackingEvent> eventsInArea = this.eventIndexer.search(nextTime, nextSearchArea);
			// System.out.println("Events in area: "+eventsInArea.size());
			List<ISTTrackingEvent> eventsInPrevArea = this.eventIndexer.search(prevTime, prevSearchArea);
			// System.out.println("Events in prev area: "+eventsInPrevArea.size());

			// Only if there is one do we process
			if (eventsInArea.size() == 1 && eventsInPrevArea.size() <= 1) {
				ISTTrackingEvent nextEvent = eventsInArea.get(0);

				// Only if the next event is not linked
				// do we link it to this event.
				if (nextEvent.getPrevious() == null) {
					event.setNext(nextEvent);
					nextEvent.setPrevious(event);
				}
			}
		}
	}
}
