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
package edu.gsu.cs.dmlab.graph.algo.interfaces;

import java.util.List;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;

/**
 * This interface is for classes that find the optimal multi-commodity flow
 * through the passed in SimpleDirectedWeightedGraph where some of the edges are
 * negative and the graph is a DAG.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ITrackingGraphProblemSolver {

	/**
	 * Solves the optimal multi-commodity flow problem and returns a list of keys
	 * for the edges that are used in the solution.
	 * 
	 * @param graph The graph to solve the multi-commodity flow problem on.
	 * 
	 * @return A list of two key pairs that represent the edges that are used in the
	 *         solution. Where the first key is the head vertex of the edge and the
	 *         second key is the tail vertex of the edge.
	 */
	public List<ISTTrackingTrajectory[]> solve(ITrackingGraph graph);
}
