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

import edu.gsu.cs.dmlab.graph.Edge;
import edu.gsu.cs.dmlab.graph.algo.interfaces.ITrackingGraphProblemSolver;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

/**
 * This interface is for classes that are used to solve the underlying minimum
 * cost multi-commodity flow problem for
 * {@link edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory
 * ISTTrackingTrajectory} association.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ITrackingGraphProblemFactory {

	/**
	 * Produces a new IGraphProblemSolver for solving the ITrack association
	 * problem.
	 * 
	 * @return The solver for the graph produced for the ITrack association problem.
	 */
	public ITrackingGraphProblemSolver getGraphSolver();

	/**
	 * Produces an edge for use in the Graph.
	 * 
	 * @param weight Weight for the edge.
	 * @return An edge with the passed in weight.
	 */
	public Edge getEdge(double weight);

	/**
	 * Produces a new WeightedGraph that is thread safe.
	 * 
	 * @param weightCalculator The calculator for the weight of the edges that will
	 *                         be added to the graph.
	 * @return A new WeightedGraph
	 */
	public ITrackingGraph getGraph(ISTEdgeWeightCalculator weightCalculator);
}
