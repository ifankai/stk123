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
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.geometry.GeometryUtilities;
import edu.gsu.cs.dmlab.indexes.datastructures.GITree;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public class TreeBasedEventIndexer implements ISTTrackingEventIndexer {
	Duration frameSpan;
	Interval globalTimePeriod;
	// private int regionDimension;
	private int regionDivisor;
	private GITree<ISTTrackingEvent> tree;
	private ArrayList<ISTTrackingEvent> regionalList;
	EventType type;
	HashMap<Integer, ArrayList<ISTTrackingEvent>> frames = new HashMap<Integer, ArrayList<ISTTrackingEvent>>();

	/**
	 * Constructor, constructs a new TreeBasedEventIndexer.
	 * 
	 * @param regionalList    The list of IEvent objects to add to the index.
	 * 
	 * @param regionDivisor   The divisor used to down size each of the IEvent
	 *                        objects to fit inside the spatial domain specified by
	 *                        the regionDimension parameter.
	 * 
	 * @param frameSpan       The length of a frame in the index. This is used to
	 *                        compute the expected change in detections per frame.
	 * 
	 * @throws IllegalArgumentException When any of the passed in arguments are
	 *                                  null, or if the regionDivisor is less than
	 *                                  1 same with frame span.
	 */
	public TreeBasedEventIndexer(ArrayList<ISTTrackingEvent> regionalList, int regionDivisor,
			Duration frameSpan) throws IllegalArgumentException {
		if (regionalList == null) {
			throw new IllegalArgumentException();
		}
		if (regionDivisor < 1) {
			throw new IllegalArgumentException("region divisor cannot be less than 1.");
		}
		if (frameSpan == null)
			throw new IllegalArgumentException("FrameSpan cannot be null");

		if (frameSpan.getStandardSeconds() < 1)
			throw new IllegalArgumentException("FrameSpan cannot be a duration less than 1");
		this.regionalList = regionalList;
		this.frameSpan = frameSpan;
		this.globalTimePeriod = null; // the build index should
		// TODO: should the regionDimension be used for the tree below? If so
		// this needs to be fixed.
		// this.regionDimension = regionDimension;
		this.regionDivisor = regionDivisor;
		this.tree = new GITree<>();
		// expand this as events are
		// indexed.
		this.buildIndex();
		this.type = regionalList.get(0).getType();
	}

	@Override
	public DateTime getFirstTime() {
		return this.globalTimePeriod.getStart();
	}

	@Override
	public DateTime getLastTime() {
		return this.globalTimePeriod.getEnd();
	}

	@Override
	public ArrayList<ISTTrackingEvent> search(Interval timePeriod, Geometry searchArea) {
		Geometry scaledSearchArea = GeometryUtilities.scaleGeometry(searchArea, this.regionDivisor);
		Envelope searchBoundingBox = scaledSearchArea.getEnvelopeInternal();
		ArrayList<ISTTrackingEvent> events = tree.search(timePeriod.getStart(), timePeriod.getEnd(), searchBoundingBox);
		Map<UUID, ISTTrackingEvent> map = new ConcurrentHashMap<>();
		for (ISTTrackingEvent evnt : events) {
			map.put(evnt.getUUID(), evnt);
		}
		ArrayList<ISTTrackingEvent> retList = new ArrayList<>();
		retList.addAll(map.values());
		return retList;
	}

	@Override
	public ArrayList<ISTTrackingEvent> getAll() {
		return this.regionalList;
	}

	private void buildIndex() {
		// add all the objects to the index
		this.regionalList.forEach(event -> indexEvent((ISTTrackingEvent) event));
	}

	private void indexEvent(ISTTrackingEvent event) {
		this.insertEventIntoSearchSpace(event);
		this.resizeGlobalPeriod(event.getTimePeriod());
		this.buildFrameIndex(event);
	}

	private void insertEventIntoSearchSpace(ISTTrackingEvent event) {
		long startTime = event.getTimePeriod().getStartMillis();
		long endTime = event.getTimePeriod().getEndMillis();

		Geometry shape = event.getGeometry();
		Geometry scaledShape = GeometryUtilities.scaleGeometry(shape, regionDivisor);
		Envelope shapeEnvelope = scaledShape.getEnvelopeInternal();
		tree.insert(event, startTime, endTime, shapeEnvelope);
	}

	private void buildFrameIndex(ISTTrackingEvent event) {
		Interval timePeriod = event.getTimePeriod();
		this.resizeGlobalPeriod(timePeriod);
		ArrayList<Integer> indexes = getFrameIndex(timePeriod);
		for (int index : indexes) {
			ArrayList<ISTTrackingEvent> frame = frames.getOrDefault(index, null);
			if (frame != null) {
				frame.add(event);
			} else {
				frame = new ArrayList<ISTTrackingEvent>();
				frame.add(event);
				this.frames.put(index, frame);
			}
		}
	}

	private void resizeGlobalPeriod(Interval timePeriod) {

		if (this.globalTimePeriod == null) {
			long length = timePeriod.toDurationMillis() / this.frameSpan.getMillis() + 1;
			DateTime end = timePeriod.getStart().plus(this.frameSpan.getMillis() * length);
			this.globalTimePeriod = new Interval(timePeriod.getStart(), end);
		} else {
			this.globalTimePeriod = union(this.globalTimePeriod, timePeriod);
		}
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
		long beginningIndex = (this.globalTimePeriod.getStartMillis() - timePeriod.getStartMillis())
				/ this.frameSpan.getMillis();

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

	private Interval union(Interval firstInterval, Interval secondInterval) {
		// Purpose: Produce a new Interval instance from the outer limits of any
		// pair of Intervals.

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
		ArrayList<Integer> periodIndexes = getFrameIndex(intersection);
		double sum = 0.0;
		if (periodIndexes.size() > 1) {
			ArrayList<ISTTrackingEvent> events = this.frames.getOrDefault(0, new ArrayList<>());
			double lastValue = events.size();
			for (int i = 0; i < periodIndexes.size(); i++) {
				events = this.frames.getOrDefault(i, new ArrayList<>());
				double currentValue = events.size();
				sum += Math.abs(currentValue - lastValue);
				lastValue = currentValue;
			}
		}
		return (int) sum / periodIndexes.size();
	}

	@Override
	public EventType getTypeIndexed() {
		return this.type;
	}

}
