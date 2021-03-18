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
package edu.gsu.cs.dmlab.graph.algo;

import java.util.ArrayList;
import java.util.List;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.graph.Edge;
import edu.gsu.cs.dmlab.graph.algo.interfaces.ITrackingGraphProblemSolver;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import org.jgrapht.alg.BellmanFordShortestPath;

/**
 * This class is used to find the optimal flow through the passed in
 * SimpleDirectedWeightedGraph where some of the edges are negative and the
 * graph is a DAG.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SuccessiveShortestPaths implements ITrackingGraphProblemSolver {

	@Override
	public List<ISTTrackingTrajectory[]> solve(ITrackingGraph graph) {
		if (graph == null)
			throw new IllegalArgumentException("Graph Cannot be null.");

		boolean done = false;
		List<ISTTrackingTrajectory[]> edgesInSolution = new ArrayList<ISTTrackingTrajectory[]>();
		while (!done) {

			// find the path from source to sink
			BellmanFordShortestPath<String, Edge> bellman = new BellmanFordShortestPath<String, Edge>(graph,
					graph.getSourceName());
			List<Edge> edges = bellman.getPathEdgeList(graph.getSinkName());
			// if path exists and costs is negative process otherwise we are
			// done
			if (edges != null) {
				double cost = 0.0;
				for (Edge edge : edges)
					cost += edge.getWeight();
				if (cost <= 0) {
					for (Edge edge : edges) {
						String edgSource = graph.getEdgeSource(edge);
						String edgTarget = graph.getEdgeTarget(edge);

						// if the edge is not from source or to the sink add to
						// returned set
						if (!(edgSource.equalsIgnoreCase(graph.getSourceName())
								|| edgTarget.equalsIgnoreCase(graph.getSinkName()))) {
							ISTTrackingTrajectory[] tmpVerts = { graph.getTrackForVertex(edgSource),
									graph.getTrackForVertex(edgTarget) };
							edgesInSolution.add(tmpVerts);
						}
						graph.removeEdge(edge);
					}
				} else {
					done = true;
				}
			} else {
				done = true;
			}
		}

		return edgesInSolution;
	}

}
