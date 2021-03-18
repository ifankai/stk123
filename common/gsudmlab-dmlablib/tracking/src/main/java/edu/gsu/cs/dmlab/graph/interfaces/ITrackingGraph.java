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
package edu.gsu.cs.dmlab.graph.interfaces;

import org.jgrapht.WeightedGraph;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.graph.Edge;

/**
 * This interface is for classes that are used to store the information that
 * then gets used in the optimal multi-commodity flow problem that we use for
 * track association.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ITrackingGraph extends WeightedGraph<String, Edge> {

	/**
	 * Adds the track to the graph by adding any vertices and edges from the source
	 * or to the sink that are used to represent the given track.
	 * 
	 * @param trajectory The track to insert into the graph.  
	 */  
	public void addTrackToGraph(ISTTrackingTrajectory trajectory);

	/**
	 * Given the vertex name, this method returns the track that the vertex
	 * represents.
	 * 
	 * @param name The vertex to get the track for.
	 * @return The track that the vertex represents.
	 */
	public ISTTrackingTrajectory getTrackForVertex(String name);

	/**
	 * Adds an association edge between the two tracks. This assumes that the tracks
	 * are already in the graph.
	 * 
	 * @param leftTrajectory  The track at an earlier time step.
	 * @param rightTrajectory The track at a later time step.
	 * @return True if successful, false otherwise.
	 */
	public boolean addAssociationPossibility(ISTTrackingTrajectory leftTrajectory,
			ISTTrackingTrajectory rightTrajectory);

	/**
	 * Returns whether this graph contains the vertices that represent the passed in
	 * track.
	 * 
	 * @param trajectory Track to test for graph containment
	 * 
	 * @return True if the graph has the vertices that represent the passed in
	 *         track. False otherwise.
	 */
	public boolean containsTrack(ISTTrackingTrajectory trajectory);

	/**
	 * This method moves the track passed in, and all those that are reachable from
	 * the passed in track, to the passed in graph. It is not safe to call this
	 * method by passing in a pointer to the same graph that you are calling this
	 * method on. Just don't do it.
	 * 
	 * @param trajectory The track to move
	 * 
	 * @param graph      The graph to move to
	 * 
	 * @return True if successful, otherwise you will probably just lock up.
	 */
	public boolean moveTrackToGraph(ISTTrackingTrajectory trajectory, ITrackingGraph graph);

	/**
	 * Returns the name of the sink vertex for this graph.
	 * 
	 * @return The name of the sink vertex.
	 */
	public String getSinkName();

	/**
	 * Returns the name of the source vertex for this graph.
	 * 
	 * @return The name of the source vertex.
	 */
	public String getSourceName();

}
