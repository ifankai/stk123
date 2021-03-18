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
package edu.gsu.cs.dmlab.tracking.stages.interfaces;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;

/**
 * The public interface for classes that return the weights of edges in the
 * graph used to solve the minimum cost multi-commodity flow problem used for
 * associating tracks into longer tracks. From Kempton et. al
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTEdgeWeightCalculator {

	/**
	 * Returns the weight for an edge from the source to the vertex representing the
	 * passed in event.
	 * 
	 * @param event The event to calculate the edge weight for.
	 * 
	 * @return The edge weight for the edge from the source
	 */
	public double sourceEdgeWeight(ISTTrackingEvent event);

	/**
	 * Returns the weight for an edge from the vertex representing the passed in
	 * event to the sink.
	 * 
	 * @param event The event to calculate the edge weight for.
	 * 
	 * @return The edge weight for the edge to the sink
	 */
	public double sinkEdgeWeight(ISTTrackingEvent event);

	/**
	 * Returns the weight for an edge that goes between the two vertices that
	 * represent the passed in event. The weight represents the probability of this
	 * event being a true detection as opposed to a false detection.
	 * 
	 * @param event The event to calculate the edge weight for.
	 * 
	 * @return A value representing how likely it is that the given event is a true
	 *         detection.
	 */
	public double observationEdgeWeight(ISTTrackingEvent event);

	/**
	 * Returns the weight for an edge that goes between the second vertex of the
	 * first track to the first vertex of the second track. This weight represents
	 * how likely it is that these two tracks represent the same object at different
	 * times.
	 * 
	 * @param leftTrack  The earlier track
	 * 
	 * @param rightTrack The later track
	 * 
	 * @return A value representing how likely it is that the two tracks represent
	 *         the same object.
	 */
	public double associationEdgeWeight(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack);
}
