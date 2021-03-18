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
package edu.gsu.cs.dmlab.graph.algo.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.graph.Edge;
import edu.gsu.cs.dmlab.graph.algo.SuccessiveShortestPaths;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class SuccessiveShortestPathsTests {

	@SuppressWarnings("serial")
	private class MockGraph extends SimpleDirectedWeightedGraph<String, Edge> implements ITrackingGraph {

		public MockGraph() {
			super(Edge.class);
		}

		@Override
		public void addTrackToGraph(ISTTrackingTrajectory track) {
			// TODO Auto-generated method stub

		}

		@Override
		public ISTTrackingTrajectory getTrackForVertex(String name) {
			return this.tracks.get(name);
		}

		@Override
		public String getSinkName() {
			return "Sink";
		}

		@Override
		public String getSourceName() {
			return "Source";
		}

		HashMap<String, ISTTrackingTrajectory> tracks = new HashMap<String, ISTTrackingTrajectory>();

		public void insertTrack(String id, ISTTrackingTrajectory trk1) {
			this.tracks.put(id, trk1);
		}

		@Override
		public boolean addAssociationPossibility(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsTrack(ISTTrackingTrajectory track) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean moveTrackToGraph(ISTTrackingTrajectory track, ITrackingGraph graph) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Test
	public void testSolveThrowsOnNullGraph() throws IllegalArgumentException {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
		pathsSolver.solve(null);
		});
	}

	@Test
	public void testSolveFirstReturn() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();

		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-3);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(3);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-2);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertArrayEquals(new ISTTrackingTrajectory[] { trk1, trk1 }, solution.get(0));
	}

	@Test
	public void testSolveSecondReturn() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-3);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(3);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-2);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertArrayEquals(new ISTTrackingTrajectory[] { trk1, trk2 }, solution.get(1));
	}

	@Test
	public void testSolveThirdReturn() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-3);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(3);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-2);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertArrayEquals(new ISTTrackingTrajectory[] { trk2, trk2 }, solution.get(2));
	}

	@Test
	public void testSolveReturnCorrectNumberOfEdges() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-3);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(3);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-2);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertTrue(solution.size() == 3);
	}

	@Test
	public void testSolveFirstReturn2Path() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-4);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(10);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-2);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertArrayEquals(new ISTTrackingTrajectory[] { trk1, trk1 }, solution.get(0));
	}

	@Test
	public void testSolveSecondReturn2Path() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-4);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(10);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-4);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertArrayEquals(new ISTTrackingTrajectory[] { trk2, trk2 }, solution.get(1));
	}

	@Test
	public void testSolveReturnCorrectNumberOfEdges2Path() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-4);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(10);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-4);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertTrue(solution.size() == 2);
	}

	@Test
	public void testSolveReturnCorrectNumberOfEdges2PathNoNegative() {
		SuccessiveShortestPaths pathsSolver = new SuccessiveShortestPaths();
		String SOURCE = "Source";
		String SINK = "Sink";
		String V1 = "V1";
		String V1_1 = "V1_1";
		String V2 = "V2";
		String V2_1 = "V2_1";

		MockGraph graph = new MockGraph();

		// Add all the vertices
		graph.addVertex(SOURCE);
		graph.addVertex(SINK);
		graph.addVertex(V1);
		graph.addVertex(V1_1);
		graph.addVertex(V2);
		graph.addVertex(V2_1);

		// Add edge from source to v1
		Edge edgeSV1 = new Edge(1);
		graph.addEdge(SOURCE, V1, edgeSV1);
		graph.setEdgeWeight(edgeSV1, edgeSV1.getWeight());

		// Add edge from v1 to v1_1
		Edge edgeV1V11 = new Edge(-3);
		graph.addEdge(V1, V1_1, edgeV1V11);
		graph.setEdgeWeight(edgeV1V11, edgeV1V11.getWeight());

		// Add edge from v1_1 to sink
		Edge edgeV11Sn = new Edge(3);
		graph.addEdge(V1_1, SINK, edgeV11Sn);
		graph.setEdgeWeight(edgeV11Sn, edgeV11Sn.getWeight());

		// Add edge from v1_1 to v2
		Edge edgeV11V2 = new Edge(10);
		graph.addEdge(V1_1, V2, edgeV11V2);
		graph.setEdgeWeight(edgeV11V2, edgeV11V2.getWeight());

		// Add edge from Source to v2
		Edge edgeSV2 = new Edge(3);
		graph.addEdge(SOURCE, V2, edgeSV2);
		graph.setEdgeWeight(edgeSV2, edgeSV2.getWeight());

		// Add edge from V2 to V2_1
		Edge edgeV2V21 = new Edge(-2);
		graph.addEdge(V2, V2_1, edgeV2V21);
		graph.setEdgeWeight(edgeV2V21, edgeV2V21.getWeight());

		// Add edge from v2_1 to sink
		Edge edgeV21Sn = new Edge(1);
		graph.addEdge(V2_1, SINK, edgeV21Sn);
		graph.setEdgeWeight(edgeV21Sn, edgeV21Sn.getWeight());

		ISTTrackingTrajectory trk1 = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		graph.insertTrack(V1, trk1);
		graph.insertTrack(V1_1, trk1);
		graph.insertTrack(V2, trk2);
		graph.insertTrack(V2_1, trk2);
		List<ISTTrackingTrajectory[]> solution = pathsSolver.solve(graph);
		assertTrue(solution.size() == 0);
	}
}
