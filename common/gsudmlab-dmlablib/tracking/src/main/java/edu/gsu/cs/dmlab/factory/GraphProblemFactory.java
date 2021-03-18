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


import edu.gsu.cs.dmlab.factory.interfaces.ITrackingGraphProblemFactory;
import edu.gsu.cs.dmlab.graph.Edge;
import edu.gsu.cs.dmlab.graph.LockGraph;
import edu.gsu.cs.dmlab.graph.algo.SuccessiveShortestPaths;
import edu.gsu.cs.dmlab.graph.algo.interfaces.ITrackingGraphProblemSolver;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

/**
 * This class is used create objects for solving the underlying minimum cost
 * multi-commodity flow problem for ITrack association.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 * 
 */
public class GraphProblemFactory implements ITrackingGraphProblemFactory {

	@Override
	public ITrackingGraphProblemSolver getGraphSolver() {
		ITrackingGraphProblemSolver solver = new SuccessiveShortestPaths();
		return solver;
	}

	@Override
	public Edge getEdge(double weight) {
		Edge e = new Edge(weight);
		return e;
	}

	@Override
	public ITrackingGraph getGraph(ISTEdgeWeightCalculator weightCalculator) {
		LockGraph graph = new LockGraph(Edge.class, this, weightCalculator);
		return graph;
	}

}
