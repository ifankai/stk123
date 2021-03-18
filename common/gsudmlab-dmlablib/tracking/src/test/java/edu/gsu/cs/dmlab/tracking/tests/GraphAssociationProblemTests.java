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
package edu.gsu.cs.dmlab.tracking.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ITrackingGraphProblemFactory;
import edu.gsu.cs.dmlab.graph.Edge;
import edu.gsu.cs.dmlab.graph.algo.interfaces.ITrackingGraphProblemSolver;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.GraphAssociationProblem;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GraphAssociationProblemTests {

	@Test
	public void testGraphAssociationProblemThrowsOnNullFactory() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		List<ISTTrackingTrajectory> tracks = mock(List.class);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		//ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GraphAssociationProblem problem = new GraphAssociationProblem(tracks, null, weightCalculator);
		});
	}

	@Test
	public void testGraphAssociationProblemThrowsOnNullTrack() throws IllegalArgumentException {
		// @SuppressWarnings("unchecked")
		// List<ISTTrackingTrajectory> tracks = mock(ArrayList.class);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GraphAssociationProblem problem = new GraphAssociationProblem(null, factory, weightCalculator);
		});
	}

	@Test
	public void testGraphAssociationProblemThrowsOnNullWeightCalculator() throws IllegalArgumentException {
		@SuppressWarnings("unchecked")
		List<ISTTrackingTrajectory> tracks = mock(List.class);
		ITrackingGraphProblemFactory  factory = mock(ITrackingGraphProblemFactory .class);
		ISTEdgeWeightCalculator weightCalculator2 =null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator2);
		});
	}

	@Test
	public void testGraphAssociationProblemCallsAddOnGraphForTrackOnConstruction() {

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		tracks.add(trk);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		ITrackingGraph grph = mock(ITrackingGraph.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);

		@SuppressWarnings("unused")
		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		verify(grph, times(1)).addTrackToGraph(trk);
	}

	@Test
	public void testGraphAssociationProblemCallsAddAssociationOnGraphWhenAssociationAdded() {

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		tracks.add(trk);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		ITrackingGraph grph = mock(ITrackingGraph.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);

		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		problem.addAssociationPossibility(trk, trk2);
		verify(grph, times(1)).addAssociationPossibility(trk, trk2);
	}

	@Test
	public void testGraphAssociationProblemCallsSolveOnGraphWhenSolveCalled() {

		// ITrack trk = mock(ITrack.class);
		// ITrack trk2 = mock(ITrack.class);
		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		// tracks.add(trk);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		@SuppressWarnings("unchecked")
		Set<Edge> edges = mock(Set.class);
		when(edges.size()).thenReturn(2);

		String source = "Source";
		ITrackingGraph grph = mock(ITrackingGraph.class);
		when(grph.getSourceName()).thenReturn(source);
		when(grph.edgesOf(source)).thenReturn(edges);  

		List<ISTTrackingTrajectory[]> trackSolution = new ArrayList<ISTTrackingTrajectory[]>();
		ITrackingGraphProblemSolver solver = mock(ITrackingGraphProblemSolver.class);
		when(solver.solve(grph)).thenReturn(trackSolution);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);
		when(factory.getGraphSolver()).thenReturn(solver);

		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		problem.solve();
		verify(solver, times(1)).solve(grph);
	}

	@Test
	public void testGraphAssociationProblemLinksLeftNextToRighInTracksSolution() {

		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		// tracks.add(trk);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		@SuppressWarnings("unchecked")
		Set<Edge> edges = mock(Set.class);
		when(edges.size()).thenReturn(2);

		String source = "Source";
		ITrackingGraph grph = mock(ITrackingGraph.class);
		when(grph.getSourceName()).thenReturn(source);
		when(grph.edgesOf(source)).thenReturn(edges);

		UUID trkUUID = UUID.randomUUID();
		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getUUID()).thenReturn(trkUUID);
		when(trk.getLast()).thenReturn(lastEvent);

		UUID trk2UUID = UUID.randomUUID();
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getUUID()).thenReturn(trk2UUID);
		when(trk2.getFirst()).thenReturn(firstEvent);

		List<ISTTrackingTrajectory[]> trackSolution = new ArrayList<ISTTrackingTrajectory[]>();
		trackSolution.add(new ISTTrackingTrajectory[] { trk, trk2 });

		ITrackingGraphProblemSolver solver = mock(ITrackingGraphProblemSolver.class);
		when(solver.solve(grph)).thenReturn(trackSolution);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);
		when(factory.getGraphSolver()).thenReturn(solver);

		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		problem.solve();
		verify(lastEvent, times(1)).setNext(firstEvent);
	}

	@Test
	public void testGraphAssociationProblemLinksRightPreviousToLeftInTracksSolution() {

		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		// tracks.add(trk);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		@SuppressWarnings("unchecked")
		Set<Edge> edges = mock(Set.class);
		when(edges.size()).thenReturn(2);

		String source = "Source";
		ITrackingGraph grph = mock(ITrackingGraph.class);
		when(grph.getSourceName()).thenReturn(source);
		when(grph.edgesOf(source)).thenReturn(edges);

		UUID trkUUID = UUID.randomUUID();
		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getUUID()).thenReturn(trkUUID);
		when(trk.getLast()).thenReturn(lastEvent);

		UUID trk2UUID = UUID.randomUUID();
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getUUID()).thenReturn(trk2UUID);
		when(trk2.getFirst()).thenReturn(firstEvent);

		List<ISTTrackingTrajectory[]> trackSolution = new ArrayList<ISTTrackingTrajectory[]>();
		trackSolution.add(new ISTTrackingTrajectory[] { trk, trk2 });

		ITrackingGraphProblemSolver solver = mock(ITrackingGraphProblemSolver.class);
		when(solver.solve(grph)).thenReturn(trackSolution);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);
		when(factory.getGraphSolver()).thenReturn(solver);

		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		problem.solve();
		verify(firstEvent, times(1)).setPrevious(lastEvent);
	}

	@Test
	public void testGraphAssociationProblemDoesNotLinkTracksSolutionWhenObsEdge() {

		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		// tracks.add(trk);
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		@SuppressWarnings("unchecked")
		Set<Edge> edges = mock(Set.class);
		when(edges.size()).thenReturn(2);

		String source = "Source";
		ITrackingGraph grph = mock(ITrackingGraph.class);
		when(grph.getSourceName()).thenReturn(source);
		when(grph.edgesOf(source)).thenReturn(edges);

		UUID trkUUID = UUID.randomUUID();
		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getUUID()).thenReturn(trkUUID);
		when(trk.getLast()).thenReturn(lastEvent);

		ArrayList<ISTTrackingTrajectory[]> trackSolution = new ArrayList<ISTTrackingTrajectory[]>();
		trackSolution.add(new ISTTrackingTrajectory[] { trk, trk });

		ITrackingGraphProblemSolver solver = mock(ITrackingGraphProblemSolver.class);
		when(solver.solve(grph)).thenReturn(trackSolution);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);
		when(factory.getGraphSolver()).thenReturn(solver);

		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		problem.solve();
		verify(lastEvent, times(0)).setPrevious(lastEvent);
	}

	@Test
	public void testGraphAssociationProblemReturnsOneTrackWhenTwoHaveTheSameStartAfterSolve() {

		UUID firstEventUUID = UUID.randomUUID();
		ISTTrackingEvent resultEvent = mock(ISTTrackingEvent.class);
		when(resultEvent.getUUID()).thenReturn(firstEventUUID);
		ISTTrackingTrajectory resultTrack = mock(ISTTrackingTrajectory.class);
		when(resultTrack.getFirst()).thenReturn(resultEvent);
		ISTTrackingTrajectory resultTrack2 = mock(ISTTrackingTrajectory.class);
		when(resultTrack2.getFirst()).thenReturn(resultEvent);
		List<ISTTrackingTrajectory> tracks = new ArrayList<ISTTrackingTrajectory>();
		tracks.add(resultTrack);
		tracks.add(resultTrack2);

		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);

		@SuppressWarnings("unchecked")
		Set<Edge> edges = mock(Set.class);
		when(edges.size()).thenReturn(0);

		String source = "Source";
		ITrackingGraph grph = mock(ITrackingGraph.class);
		when(grph.getSourceName()).thenReturn(source);
		when(grph.edgesOf(source)).thenReturn(edges);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		when(factory.getGraph(weightCalculator)).thenReturn(grph);

		GraphAssociationProblem problem = new GraphAssociationProblem(tracks, factory, weightCalculator);
		List<ISTTrackingTrajectory> resultsFromProb = problem.getTrackLinked();
		assertTrue(resultsFromProb.size() == 1);
	}
}
