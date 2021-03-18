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
package edu.gsu.cs.dmlab.indexes;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.geometry.GeometryUtilities;
import edu.gsu.cs.dmlab.indexes.interfaces.AbsMatIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * This class provides event indexing for ISpatialTemporalObj objects. The
 * indexing is based on a grid of Lists, where the grid represents the space
 * over which the index is valid. The List in each location of the grid is used
 * to sort the IEvent objects that intersect each spatial coordinate based on
 * time.
 * 
 * The class also provides a temporal index for the IEvent objects that are put
 * into it. This index is meant to allow for the calculation of the expected
 * change per "frame" or time step in reporting of events.
 * 
 * @author Thaddeus Gholston, updated by Dustin Kempton Data Mining Lab, Georgia
 *         State University
 * 
 */
public class BasicEventIndexer extends AbsMatIndexer<ISTTrackingEvent> implements ISTTrackingEventIndexer {
	private Duration frameSpan;
	private Interval globalTimePeriod;
	private EventType type;
	private HashMap<Integer, List<ISTTrackingEvent>> frames = new HashMap<Integer, List<ISTTrackingEvent>>();
	private Lock loc;

	private ForkJoinPool forkJoinPool = null;

	/**
	 * Constructor, constructs a new BasicEventIndexer.
	 * 
	 * @param regionalList    The list of IEvent objects to add to the index.
	 * @param regionDimension The length for both the x and y spatial domain. For
	 *                        example x will be valid for [0, regionDimension].
	 * @param regionDiv       The divisor used to down size each of the IEvent
	 *                        objects to fit inside the spatial domain specified by
	 *                        the regionDimension parameter.
	 * @param frameSpan       The length of a frame in the index. This is used to
	 *                        compute the expected change in detections per frame.
	 * @param numThreads      The number of threads to use when processing batch. -1
	 *                        for all available &gt; 0 for a specific number. JavaEE
	 *                        doesn't seem to like more than one thread, so only use
	 *                        1 for applications built for web application servers.
	 * @throws IllegalArgumentException When any of the passed in arguments are
	 *                                  null, or if the regionDimension is less than
	 *                                  1 same with divisor, or frame span.
	 */
	public BasicEventIndexer(List<ISTTrackingEvent> regionalList, int regionDimension, int regionDiv,
			Duration frameSpan, int numThreads) throws IllegalArgumentException {

		super(regionalList, regionDimension, regionDiv);

		if (frameSpan == null)
			throw new IllegalArgumentException("FrameSpan cannot be null");

		if (frameSpan.getStandardSeconds() < 1)
			throw new IllegalArgumentException("FrameSpan cannot be a duration less than 1");
		if (numThreads < -1 || numThreads == 0)
			throw new IllegalArgumentException("numThreads must be -1 or > 0 in Indexer constructor.");

		this.loc = new ReentrantLock();
		this.frameSpan = frameSpan;

		// the build index should expand this as events are indexed.
		this.globalTimePeriod = null;

		if (numThreads == -1) {
			this.forkJoinPool = new ForkJoinPool();
		} else {
			this.forkJoinPool = new ForkJoinPool(numThreads);
		}

		this.buildIndex();
		this.type = regionalList.get(0).getType();
	}

	@Override
	protected void buildIndex() {

		// add all the objects to the index
		try {
			this.forkJoinPool.submit(() -> {
				// Add each object to index
				objectList.forEach(event -> indexEvent((ISTTrackingEvent) event));

				// Sort objects in each box in the index by the time of the
				// objects in those regions.
				IntStream.range(0, this.regionDimension * this.regionDimension).parallel().forEach(i -> {
					int x = i / this.regionDimension;
					int y = i % this.regionDimension;
					Collections.sort(this.searchSpace[x][y], IBaseTemporalObject.baseTemporalComparator);
				});
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	private void indexEvent(ISTTrackingEvent event) {
		this.insertEventIntoSearchSpace(event);
		this.buildFrameIndex(event);
	}

	private void insertEventIntoSearchSpace(ISTTrackingEvent event) {

		Geometry shape = event.getGeometry();
		Geometry scaledSahpe = GeometryUtilities.scaleGeometry(shape, this.regionDivisor);
		Envelope boundingBox = scaledSahpe.getEnvelopeInternal();

		GeometryFactory gf = new GeometryFactory();
		IntStream.rangeClosed((int) boundingBox.getMinX(), (int) boundingBox.getMaxX()).parallel().forEach(x -> {
			IntStream.rangeClosed((int) boundingBox.getMinY(), (int) boundingBox.getMaxY()).forEach(y -> {
				if (x > -1 && x < this.regionDimension && y > -1 && y < this.regionDimension) {
					Geometry g = gf.toGeometry(new Envelope(x, x + 1, y, y + 1));
					if (scaledSahpe.intersects(g)) {
						searchSpace[x][y].add(event);
					}
				}
			});
		});

	}

	private void buildFrameIndex(ISTTrackingEvent event) {
		Interval timePeriod = event.getTimePeriod();
		this.resizeGlobalPeriod(timePeriod);
		ArrayList<Integer> indexes = this.getFrameIndex(timePeriod);
		for (int index : indexes) {
			List<ISTTrackingEvent> frame = this.frames.getOrDefault(index, null);
			if (frame != null) {
				frame.add(event);
			} else {
				frame = new LockingArrayList<ISTTrackingEvent>();
				frame.add(event);
				this.frames.put(index, frame);
			}
		}
	}

	@Override
	public void finalize() throws Throwable {
		this.forkJoinPool.shutdownNow();
		this.forkJoinPool = null;

		this.frameSpan = null;
		this.globalTimePeriod = null;
		this.type = null;
		this.frames.clear();
		this.frames = null;
		this.loc = null;
	}

	/**
	 * This method expands the global input interval for the temporal index, and
	 * shuffles the indexed elements around to coincide with the new state of the
	 * index (if needed).
	 * 
	 * @param timePeriod The time period to expand to encompass
	 */
	private void resizeGlobalPeriod(Interval timePeriod) {
		this.loc.lock();
		if (this.globalTimePeriod == null) {
			long length = timePeriod.toDurationMillis() / this.frameSpan.getMillis() + 1;
			DateTime end = timePeriod.getStart().plus(this.frameSpan.getMillis() * length);
			this.globalTimePeriod = new Interval(timePeriod.getStart(), end);
		} else {

			// if the current insertion is before the start time then we need to
			// update all indexes
			if (timePeriod.getStart().isBefore(this.globalTimePeriod.getStartMillis())) {

				// Save the old global time period.
				Interval oldGlobalPeriod = this.globalTimePeriod;
				// Resize the global time period.
				this.globalTimePeriod = this.union(this.globalTimePeriod, timePeriod);

				// Get the frame steps between the new start and the old one.
				long diff = oldGlobalPeriod.getStart().getMillis() - this.globalTimePeriod.getStart().getMillis();
				long diffSteps = diff / this.frameSpan.getMillis();

				// Create the new frame set to hold the new index based on the
				// new period.
				HashMap<Integer, List<ISTTrackingEvent>> tmpFrames = new HashMap<Integer, List<ISTTrackingEvent>>();

				// We now have to re-index everything that was in there.
				for (Entry<Integer, List<ISTTrackingEvent>> frame : this.frames.entrySet()) {
					int index = (int) (frame.getKey() + diffSteps);
					tmpFrames.put(Integer.valueOf(index), frame.getValue());
				}

				this.frames.clear();
				this.frames = tmpFrames;

			} else if (timePeriod.isAfter(this.globalTimePeriod.getEndMillis())) {
				// if it is after, we can just union.
				this.globalTimePeriod = this.union(this.globalTimePeriod, timePeriod);
			}
			// The else we don't have to do anything it is already capable of
			// handling the new input.
		}
		this.loc.unlock();
	}

	/***
	 * This method assumes that the input interval is within the global time period.
	 * Make sure to expand the global interval if this is not the case, or trim the
	 * input timePeriod prior to calling.
	 *
	 * @param timePeriod the time period we wish to get valid index locations for
	 * @return the set of index locations the input period intersects
	 */
	private ArrayList<Integer> getFrameIndex(Interval timePeriod) {

		// find the beginning index location
		long globalStart = this.globalTimePeriod.getStartMillis();
		long localStart = timePeriod.getStartMillis();
		long beginningIndex = (localStart - globalStart) / this.frameSpan.getMillis();

		long numIdPositions = (timePeriod.toDurationMillis() / this.frameSpan.getMillis());

		if (timePeriod.toDurationMillis() % this.frameSpan.getMillis() != 0) {
			numIdPositions += 1;
		}

		// find the ending index location
		long endingIndex = beginningIndex + numIdPositions;

		// construct the array of indexes to return
		ArrayList<Integer> indexes = new ArrayList<>();
		for (long index = beginningIndex; index <= endingIndex; index++) {
			indexes.add((int) index);
		}
		return indexes;
	}

	/**
	 * Produces a union of the two input intervals, including any time between them
	 * that neither occupy.
	 * 
	 * @param firstInterval
	 * @param secondInterval
	 * @return
	 */
	private Interval union(Interval firstInterval, Interval secondInterval) {

		// Take the earliest of both starting date-times.
		DateTime start = firstInterval.getStart().isBefore(secondInterval.getStart()) ? firstInterval.getStart()
				: secondInterval.getStart();
		// Take the latest of both ending date-times.
		DateTime end = firstInterval.getEnd().isAfter(secondInterval.getEnd()) ? firstInterval.getEnd()
				: secondInterval.getEnd();
		// Instantiate a new Interval from the pair of DateTime instances.
		Interval unionInterval = new Interval(start, end);

		return unionInterval;
	}

	@Override
	public int getExpectedChangePerFrame(Interval timePeriod) {
		if (timePeriod.getEnd().isBefore(this.globalTimePeriod.getStart())) {
			return 0;
		} else if (timePeriod.getStart().isAfter(this.globalTimePeriod.getEnd())) {
			return 0;
		}

		Interval intersection = this.globalTimePeriod.overlap(timePeriod);
		ArrayList<Integer> periodIndexes = this.getFrameIndex(intersection);
		double sum = 0.0;
		if (periodIndexes.size() > 1) {
			List<ISTTrackingEvent> events = this.frames.getOrDefault(periodIndexes.get(0), new ArrayList<>());
			double lastValue = events.size();
			for (int i = 1; i < periodIndexes.size(); i++) {
				events = this.frames.getOrDefault(periodIndexes.get(i), new ArrayList<>());
				double currentValue = events.size();
				sum += Math.abs(currentValue - lastValue);
				lastValue = currentValue;
			}
		}
		return (int) (sum / periodIndexes.size());
	}

	@Override
	public EventType getTypeIndexed() {
		return this.type;
	}

}