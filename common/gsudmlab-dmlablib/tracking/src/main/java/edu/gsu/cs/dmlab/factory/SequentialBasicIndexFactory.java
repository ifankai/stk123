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
package edu.gsu.cs.dmlab.factory;

import java.util.List;

import org.joda.time.Duration;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.indexes.BasicEventIndexer;
import edu.gsu.cs.dmlab.indexes.BasicTrackIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;
import edu.gsu.cs.dmlab.factory.interfaces.ISTIndexFactory; 

/**
 * This class provides a way of constructing objects for event indexing of
 * IEvent objects, or indexing of ITrack objects, and constructing anything that
 * those indexes might need. The indexing is based on a grid of ArrayLists,
 * where the grid represents the space over which the index is valid. The
 * ArrayLists in each location of the grid are used to sort the IEvent or ITrack
 * objects that intersect each spatial coordinate based on time.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SequentialBasicIndexFactory implements ISTIndexFactory {

	private int regionDimension;
	private int regionDiv;
	private Duration frameSpan;

	/**
	 * Constructor, constructs a new BasicIndexFactory object (there should only be
	 * one of these objects in the application).
	 * 
	 * @param regionDimension The length for both the x and y spatial domain. For
	 *                        example x will be valid for [0, regionDimension].
	 * @param regionDiv       The divisor used to down size each of the indexed
	 *                        objects to fit inside the spatial domain specified by
	 *                        the regionDimension parameter.
	 * @param frameSpan       The length of a frame in the index. This is used to
	 *                        compute the expected change in detections per frame in
	 *                        the IEventIndexer objects.
	 */
	public SequentialBasicIndexFactory(int regionDimension, int regionDiv, Duration frameSpan) {
		if (regionDimension < 1)
			throw new IllegalArgumentException("The dimension of the regions must be greater than 0.");
		if (regionDiv < 1)
			throw new IllegalArgumentException("The divisor must be greater than 0.");
		if (frameSpan == null)
			throw new IllegalArgumentException("The frame span cannot be null.");
		if (frameSpan.getStandardSeconds() <= 0)
			throw new IllegalArgumentException("The frame span cannot be zero or negative.");
		this.regionDimension = regionDimension;
		this.regionDiv = regionDiv;
		this.frameSpan = frameSpan;
	}

	/*
	 * @Override public IEventIndexer getEventIndexer(List<IEvent> regionalList) {
	 * IEventIndexer idxer = new BasicEventIndexer(regionalList, regionDimension,
	 * regionDiv, frameSpan, 1); return idxer; }
	 * 
	 * @Override public ITrackIndexer getTrackIndexer(List<ITrack> trackList) {
	 * ITrackIndexer idxer = new BasicTrackIndexer(trackList, regionDimension,
	 * regionDiv, 1); return idxer; }
	 */
	@Override
	public ISTTrackingEventIndexer getEventIndexer(List<ISTTrackingEvent> regionalList) {
		ISTTrackingEventIndexer idxer = new BasicEventIndexer(regionalList, regionDimension, regionDiv, frameSpan, 1); 
		return idxer;
	}

	@Override
	public ISTTrackingTrajectoryIndexer getTrackIndexer(List<ISTTrackingTrajectory> trackList) {
		ISTTrackingTrajectoryIndexer idxer = new BasicTrackIndexer(trackList, regionDimension, regionDiv, 1); 
		return idxer;
	}

}
