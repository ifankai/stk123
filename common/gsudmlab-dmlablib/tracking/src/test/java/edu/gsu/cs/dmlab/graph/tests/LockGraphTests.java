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
package edu.gsu.cs.dmlab.graph.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ITrackingGraphProblemFactory;
import edu.gsu.cs.dmlab.graph.Edge;
import edu.gsu.cs.dmlab.graph.LockGraph;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.UUID;

public class LockGraphTests {

	@Test
	public void testLockGraphConstructorThrowsOnNullFactory() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ITrackingGraph graph = new LockGraph(Edge.class, null, weightCalculator);
		});
	}

	@Test
	public void testLockGraphConstructorThrowsOnNullCalculator() throws IllegalArgumentException {
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ITrackingGraph graph = new LockGraph(Edge.class, factory, null);
		});
	}  

	@Test
	public void testLockGraphSourceVertexInGraphAfterConstructor() {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		String source = graph.getSourceName();
		assertTrue(graph.containsVertex(source));
	}

	@Test
	public void testLockGraphSinkVertexInGraphAfterConstructor() {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		String sink = graph.getSinkName();
		assertTrue(graph.containsVertex(sink));
	}

	@Test
	public void testLockGraphThrowsOnNullSourceOnAddEdge() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		String targetVertex = "target";
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			graph.addEdge(null, targetVertex, edge);
		});
	}

	@Test
	public void testLockGraphThrowsOnNullTargetOnAddEdge() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		String sourceVertex = "target";
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			graph.addEdge(sourceVertex, null, edge);
		});
	}

	@Test
	public void testLockGraphThrowsOnNullEdgeOnAddEdge() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);

		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName();
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			graph.addEdge(sourceVertex, targetVertex, null);
		});
	}

	@Test
	public void testLockGraphAddEdgeAddsEdge() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName();
		graph.addEdge(sourceVertex, targetVertex, edge);
		assertTrue(graph.containsEdge(edge));
	}

	@Test
	public void testLockGraphAddEdgeReturnsTrueOnAdd() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName();
		assertTrue(graph.addEdge(sourceVertex, targetVertex, edge));
	}

	@Test
	public void testLockGraphAddEdgeReturnsFalseOnAddEdgeTwice() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName();
		graph.addEdge(sourceVertex, targetVertex, edge);
		assertTrue(!graph.addEdge(sourceVertex, targetVertex, edge));
	}

	@Test
	public void testLockGraphAddEdgeThrowsWhenVertexNotInGraph() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName() + 12321;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			graph.addEdge(sourceVertex, targetVertex, edge);
		});
	}

	@Test
	public void testLockGraphGetEdgeWeightReturnsCorrectWeigth() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		Edge edge = mock(Edge.class);
		double weight = 10.0;
		when(edge.getWeight()).thenReturn(weight);
		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName();
		graph.addEdge(sourceVertex, targetVertex, edge);
		assertTrue(graph.getEdgeWeight(edge) == weight);
	}

	@Test
	public void testLockGraphSetEdgeWeightSetsWeightWeigth() throws IllegalArgumentException {
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);
		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		double weight = 10.0;
		Edge edge = mock(Edge.class);
		when(edge.getWeight()).thenReturn(weight);
		String sourceVertex = graph.getSourceName();
		String targetVertex = graph.getSinkName();
		graph.addEdge(sourceVertex, targetVertex, edge);
		double weight2 = 12.0;
		ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
		graph.setEdgeWeight(edge, weight2);
		verify(edge, times(1)).setWeight(captor.capture());
		assertTrue(captor.getValue() == weight2);
	}

	@Test
	public void testLockGraphAddTrackToGraphUsesFirstEvent() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		verify(trk, atLeast(1)).getFirst();

	}

	@Test
	public void testLockGraphAddTrackToGraphUsesLastEvent() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		verify(trk, atLeast(1)).getLast();

	}

	@Test
	public void testLockGraphAddTrackToGraphUsesFirstToGetSourceWeight() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		verify(weightCalculator, times(1)).sourceEdgeWeight(firstEvent);

	}

	@Test
	public void testLockGraphAddTrackToGraphUsesLastToGetSinkWeight() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		verify(weightCalculator, times(1)).sinkEdgeWeight(lastEvent);

	}

	@Test
	public void testLockGraphAddTrackToGraphUsesFirstToGetObservationWeight() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		verify(weightCalculator, times(1)).observationEdgeWeight(firstEvent);

	}

	@Test
	public void testLockGraphAddTrackToGraphThenContainsTrack() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		assertTrue(graph.containsTrack(trk));

	}

	@Test
	public void testLockGraphContainsTrackReturnsFalseWhenNotInGraph() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);
		assertTrue(!graph.containsTrack(trk2));

	}

	@Test
	public void testLockGraphGetTrackForVertexWorksForFirstVertex() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		String name = firstEventUUID.toString() + 1;
		assertTrue(graph.getTrackForVertex(name) == trk);

	}

	@Test
	public void testLockGraphGetTrackForVertexWorksForSecondVertex() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		String name = lastEventUUID.toString() + 2;
		assertTrue(graph.getTrackForVertex(name) == trk);

	}

	@Test
	public void testLockGraphReturnsTrueOnAddAssociationPossibility() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);

		assertTrue(graph.addAssociationPossibility(trk, trk2));

	}

	@Test
	public void testLockGraphReturnsFalseOnAddAssociationPossibilityWhenLeftTrackNotInGraph()
			throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk2);

		assertTrue(!graph.addAssociationPossibility(trk, trk2));

	}

	@Test
	public void testLockGraphReturnsFalseOnAddAssociationPossibilityWhenRightTrackNotInGraph()
			throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);

		assertTrue(!graph.addAssociationPossibility(trk, trk2));

	}

	@Test
	public void testLockGraphContainsEdgeAfterAddAssociationPossibility() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);

		graph.addAssociationPossibility(trk, trk2);
		String fromUUID2 = lastEventUUID.toString() + 2;
		String toUUID1 = firstEventUUID2.toString() + 1;
		assertTrue(graph.containsEdge(fromUUID2, toUUID1));

	}

	@Test
	public void testLockGraphMovesWhenNoAssociation() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk));

	}

	@Test
	public void testLockGraphMovesFirstTrackWhenAssociation() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk, trk2);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk));

	}

	@Test
	public void testLockGraphMovesFirstTrackWhenAssociationFromLeft() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk2, trk)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk2, trk);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk));

	}

	@Test
	public void testLockGraphMovesFirstTrackWhenAssociationAndRemovesFromFirst() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk, trk2);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(!graph.containsTrack(trk));

	}

	@Test
	public void testLockGraphMovesFirstTrackWhenAssociationFromLeftAndRemovesFromFirst()
			throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk2, trk)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk2, trk);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(!graph.containsTrack(trk));

	}

	@Test
	public void testLockGraphMovesSecondTrackWhenAssociation() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk, trk2);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk2));

	}

	@Test
	public void testLockGraphMovesSecondTrackWhenAssociationFromLeft() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk2, trk)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk2, trk);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk2));

	}

	@Test
	public void testLockGraphMovesSecondTrackWhenAssociationAndRemovesFromFirst() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk, trk2);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(!graph.containsTrack(trk2));

	}

	@Test
	public void testLockGraphMovesSecondTrackWhenAssociationFromLeftAndRemovesFromFirst()
			throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);
		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk2, trk)).thenReturn(assocWeight);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);
		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);
		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);
		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addAssociationPossibility(trk2, trk);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(!graph.containsTrack(trk2));

	}

	@Test
	public void testLockGraphMovesThirdTrackWhenAssociationToSecond() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		ISTTrackingEvent firstEvent3 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID3 = UUID.randomUUID();
		when(firstEvent3.getUUID()).thenReturn(firstEventUUID3);

		ISTTrackingEvent lastEvent3 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID3 = UUID.randomUUID();
		when(lastEvent3.getUUID()).thenReturn(lastEventUUID3);

		ISTTrackingTrajectory trk3 = mock(ISTTrackingTrajectory.class);
		when(trk3.getFirst()).thenReturn(firstEvent3);
		when(trk3.getLast()).thenReturn(lastEvent3);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		double sourceWeight3 = 13.0;
		double sinkWeight3 = 23.0;
		double obsWeight3 = 33.0;
		double assocWeight2 = 200;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent3)).thenReturn(sourceWeight3);
		when(weightCalculator.sinkEdgeWeight(lastEvent3)).thenReturn(sinkWeight3);
		when(weightCalculator.observationEdgeWeight(firstEvent3)).thenReturn(obsWeight3);
		when(weightCalculator.associationEdgeWeight(trk2, trk3)).thenReturn(assocWeight2);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);

		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);

		Edge sourceEdge3 = mock(Edge.class);
		Edge sinkEdge3 = mock(Edge.class);
		Edge obsEdge3 = mock(Edge.class);
		Edge assocEdge2 = mock(Edge.class);

		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		when(factory.getEdge(sourceWeight3)).thenReturn(sourceEdge3);
		when(factory.getEdge(sinkWeight3)).thenReturn(sinkEdge3);
		when(factory.getEdge(obsWeight3)).thenReturn(obsEdge3);
		when(factory.getEdge(assocWeight2)).thenReturn(assocEdge2);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addTrackToGraph(trk3);
		graph.addAssociationPossibility(trk, trk2);
		graph.addAssociationPossibility(trk2, trk3);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk3));

	}

	@Test
	public void testLockGraphMovesThirdTrackWhenAssociationFromLeftToSecond() throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		ISTTrackingEvent firstEvent3 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID3 = UUID.randomUUID();
		when(firstEvent3.getUUID()).thenReturn(firstEventUUID3);

		ISTTrackingEvent lastEvent3 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID3 = UUID.randomUUID();
		when(lastEvent3.getUUID()).thenReturn(lastEventUUID3);

		ISTTrackingTrajectory trk3 = mock(ISTTrackingTrajectory.class);
		when(trk3.getFirst()).thenReturn(firstEvent3);
		when(trk3.getLast()).thenReturn(lastEvent3);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		double sourceWeight3 = 13.0;
		double sinkWeight3 = 23.0;
		double obsWeight3 = 33.0;
		double assocWeight2 = 200;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk2, trk)).thenReturn(assocWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent3)).thenReturn(sourceWeight3);
		when(weightCalculator.sinkEdgeWeight(lastEvent3)).thenReturn(sinkWeight3);
		when(weightCalculator.observationEdgeWeight(firstEvent3)).thenReturn(obsWeight3);
		when(weightCalculator.associationEdgeWeight(trk2, trk3)).thenReturn(assocWeight2);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);

		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);

		Edge sourceEdge3 = mock(Edge.class);
		Edge sinkEdge3 = mock(Edge.class);
		Edge obsEdge3 = mock(Edge.class);
		Edge assocEdge2 = mock(Edge.class);

		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		when(factory.getEdge(sourceWeight3)).thenReturn(sourceEdge3);
		when(factory.getEdge(sinkWeight3)).thenReturn(sinkEdge3);
		when(factory.getEdge(obsWeight3)).thenReturn(obsEdge3);
		when(factory.getEdge(assocWeight2)).thenReturn(assocEdge2);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addTrackToGraph(trk3);
		graph.addAssociationPossibility(trk2, trk);
		graph.addAssociationPossibility(trk2, trk3);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(graph2.containsTrack(trk3));

	}

	@Test
	public void testLockGraphMovesThirdTrackWhenAssociationToSecondAndRemovesFromFirstGraph()
			throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		ISTTrackingEvent firstEvent3 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID3 = UUID.randomUUID();
		when(firstEvent3.getUUID()).thenReturn(firstEventUUID3);

		ISTTrackingEvent lastEvent3 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID3 = UUID.randomUUID();
		when(lastEvent3.getUUID()).thenReturn(lastEventUUID3);

		ISTTrackingTrajectory trk3 = mock(ISTTrackingTrajectory.class);
		when(trk3.getFirst()).thenReturn(firstEvent3);
		when(trk3.getLast()).thenReturn(lastEvent3);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		double sourceWeight3 = 13.0;
		double sinkWeight3 = 23.0;
		double obsWeight3 = 33.0;
		double assocWeight2 = 200;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk, trk2)).thenReturn(assocWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent3)).thenReturn(sourceWeight3);
		when(weightCalculator.sinkEdgeWeight(lastEvent3)).thenReturn(sinkWeight3);
		when(weightCalculator.observationEdgeWeight(firstEvent3)).thenReturn(obsWeight3);
		when(weightCalculator.associationEdgeWeight(trk2, trk3)).thenReturn(assocWeight2);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);

		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);

		Edge sourceEdge3 = mock(Edge.class);
		Edge sinkEdge3 = mock(Edge.class);
		Edge obsEdge3 = mock(Edge.class);
		Edge assocEdge2 = mock(Edge.class);

		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		when(factory.getEdge(sourceWeight3)).thenReturn(sourceEdge3);
		when(factory.getEdge(sinkWeight3)).thenReturn(sinkEdge3);
		when(factory.getEdge(obsWeight3)).thenReturn(obsEdge3);
		when(factory.getEdge(assocWeight2)).thenReturn(assocEdge2);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addTrackToGraph(trk3);
		graph.addAssociationPossibility(trk, trk2);
		graph.addAssociationPossibility(trk2, trk3);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(!graph.containsTrack(trk3));

	}

	@Test
	public void testLockGraphMovesThirdTrackWhenAssociationFromLeftToSecondAndRemovesFromFirstGraph()
			throws IllegalArgumentException {
		ISTTrackingEvent firstEvent = mock(ISTTrackingEvent.class);
		UUID firstEventUUID = UUID.randomUUID();
		when(firstEvent.getUUID()).thenReturn(firstEventUUID);

		ISTTrackingEvent lastEvent = mock(ISTTrackingEvent.class);
		UUID lastEventUUID = UUID.randomUUID();
		when(lastEvent.getUUID()).thenReturn(lastEventUUID);

		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		when(trk.getFirst()).thenReturn(firstEvent);
		when(trk.getLast()).thenReturn(lastEvent);

		ISTTrackingEvent firstEvent2 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID2 = UUID.randomUUID();
		when(firstEvent2.getUUID()).thenReturn(firstEventUUID2);

		ISTTrackingEvent lastEvent2 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID2 = UUID.randomUUID();
		when(lastEvent2.getUUID()).thenReturn(lastEventUUID2);

		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		when(trk2.getFirst()).thenReturn(firstEvent2);
		when(trk2.getLast()).thenReturn(lastEvent2);

		ISTTrackingEvent firstEvent3 = mock(ISTTrackingEvent.class);
		UUID firstEventUUID3 = UUID.randomUUID();
		when(firstEvent3.getUUID()).thenReturn(firstEventUUID3);

		ISTTrackingEvent lastEvent3 = mock(ISTTrackingEvent.class);
		UUID lastEventUUID3 = UUID.randomUUID();
		when(lastEvent3.getUUID()).thenReturn(lastEventUUID3);

		ISTTrackingTrajectory trk3 = mock(ISTTrackingTrajectory.class);
		when(trk3.getFirst()).thenReturn(firstEvent3);
		when(trk3.getLast()).thenReturn(lastEvent3);

		double sourceWeight = 1.0;
		double sinkWeight = 2.0;
		double obsWeight = 3.0;
		double sourceWeight2 = 12.0;
		double sinkWeight2 = 22.0;
		double obsWeight2 = 32.0;
		double assocWeight = 100;
		double sourceWeight3 = 13.0;
		double sinkWeight3 = 23.0;
		double obsWeight3 = 33.0;
		double assocWeight2 = 200;
		ISTEdgeWeightCalculator weightCalculator = mock(ISTEdgeWeightCalculator.class);
		when(weightCalculator.sourceEdgeWeight(firstEvent)).thenReturn(sourceWeight);
		when(weightCalculator.sinkEdgeWeight(lastEvent)).thenReturn(sinkWeight);
		when(weightCalculator.observationEdgeWeight(firstEvent)).thenReturn(obsWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent2)).thenReturn(sourceWeight2);
		when(weightCalculator.sinkEdgeWeight(lastEvent2)).thenReturn(sinkWeight2);
		when(weightCalculator.observationEdgeWeight(firstEvent2)).thenReturn(obsWeight2);
		when(weightCalculator.associationEdgeWeight(trk2, trk)).thenReturn(assocWeight);

		when(weightCalculator.sourceEdgeWeight(firstEvent3)).thenReturn(sourceWeight3);
		when(weightCalculator.sinkEdgeWeight(lastEvent3)).thenReturn(sinkWeight3);
		when(weightCalculator.observationEdgeWeight(firstEvent3)).thenReturn(obsWeight3);
		when(weightCalculator.associationEdgeWeight(trk2, trk3)).thenReturn(assocWeight2);

		ITrackingGraphProblemFactory factory = mock(ITrackingGraphProblemFactory.class);

		Edge sourceEdge = mock(Edge.class);
		Edge sinkEdge = mock(Edge.class);
		Edge obsEdge = mock(Edge.class);

		Edge sourceEdge2 = mock(Edge.class);
		Edge sinkEdge2 = mock(Edge.class);
		Edge obsEdge2 = mock(Edge.class);
		Edge assocEdge = mock(Edge.class);

		Edge sourceEdge3 = mock(Edge.class);
		Edge sinkEdge3 = mock(Edge.class);
		Edge obsEdge3 = mock(Edge.class);
		Edge assocEdge2 = mock(Edge.class);

		when(factory.getEdge(sourceWeight)).thenReturn(sourceEdge);
		when(factory.getEdge(sinkWeight)).thenReturn(sinkEdge);
		when(factory.getEdge(obsWeight)).thenReturn(obsEdge);

		when(factory.getEdge(sourceWeight2)).thenReturn(sourceEdge2);
		when(factory.getEdge(sinkWeight2)).thenReturn(sinkEdge2);
		when(factory.getEdge(obsWeight2)).thenReturn(obsEdge2);
		when(factory.getEdge(assocWeight)).thenReturn(assocEdge);

		when(factory.getEdge(sourceWeight3)).thenReturn(sourceEdge3);
		when(factory.getEdge(sinkWeight3)).thenReturn(sinkEdge3);
		when(factory.getEdge(obsWeight3)).thenReturn(obsEdge3);
		when(factory.getEdge(assocWeight2)).thenReturn(assocEdge2);

		ITrackingGraph graph = new LockGraph(Edge.class, factory, weightCalculator);
		ITrackingGraph graph2 = new LockGraph(Edge.class, factory, weightCalculator);
		graph.addTrackToGraph(trk);
		graph.addTrackToGraph(trk2);
		graph.addTrackToGraph(trk3);
		graph.addAssociationPossibility(trk2, trk);
		graph.addAssociationPossibility(trk2, trk3);

		graph.moveTrackToGraph(trk, graph2);
		assertTrue(!graph.containsTrack(trk3));

	}
}
