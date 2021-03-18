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
package edu.gsu.cs.dmlab.solgrind.index;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import edu.gsu.cs.dmlab.solgrind.base.EventType;

import java.util.*;
import java.util.stream.Collectors;

public class SequenceGraph extends DirectedAcyclicGraph<InstanceVertex, RelationEdge> implements WeightedGraph<InstanceVertex, RelationEdge> {

	/**
	 * Generated serial version uid
	 */
	private static final long serialVersionUID = -4702851463757320142L;

	public SequenceGraph() {
		super(RelationEdge.class);
	}

	public SequenceGraph(SequenceGraph graph) {
		super(RelationEdge.class);
		DirectedNeighborIndex<InstanceVertex, RelationEdge> neighbors = new DirectedNeighborIndex<>(graph);
		for(InstanceVertex v : graph.vertexSet() ) {
			InstanceVertex newVertex1 = new InstanceVertex(v);
			addVertex(newVertex1);
			for(InstanceVertex v2 :neighbors.successorsOf(v)){
				RelationEdge edge = graph.getEdge(v, v2);

				InstanceVertex newVertex2 = new InstanceVertex(v2);
				addVertex(newVertex1);
				addVertex(newVertex2);
				RelationEdge newEdge = addEdge(newVertex1, newVertex2);
				setEdgeWeight(newEdge, edge.getWeight());
			}
		}
	}

	public void ciFilter(double ciThreshold) {

		Iterator<RelationEdge> iterator = edgeSet().iterator();
		Set<RelationEdge> toBeRemoved = new HashSet<>();
		while (iterator.hasNext()){
			RelationEdge edge = iterator.next();
			if(ciThreshold >= edge.getWeight()) {
				toBeRemoved.add(edge);
			}
		}
		this.removeAllEdges(toBeRemoved);
	}

	public void rPercentFilter(double rPercent) {

		double ciThreshold = this.findKPercentThreshold(rPercent);
		System.out.println(ciThreshold);
		this.ciFilter(ciThreshold);
	}

	public SequenceGraph randomSampleEdges(double ratio) {
		SequenceGraph newGraph = new SequenceGraph(this);
		Set<RelationEdge> allEdges = newGraph.edgeSet();
		if(allEdges.size()  <= 0) {
			throw new UnsupportedOperationException("There is not edge to be selected");
		}
		List<RelationEdge> shuffledEdges = new ArrayList<>(allEdges);
		Collections.shuffle(shuffledEdges, new Random(System.currentTimeMillis()));
		List<RelationEdge> tobeDeleted = shuffledEdges.subList((int)Math.floor(ratio * (shuffledEdges.size() - 1)), shuffledEdges.size()-1);
		newGraph.removeAllEdges(tobeDeleted);
		return newGraph;
	}

	/**
	 *
	 * @param quantile
	 * @return threshold cutoff point at highest quantile * 100% (e.g. 0.7 -&gt; highest 70% of weights)
	 */
	public double findKPercentThreshold(double quantile) {
		if(quantile <= 0.0 || quantile > 1.0) {
			throw new IllegalArgumentException("quantile must be between (0, 1]");
		}
		List<Double> sortedWeights = edgeSet().stream().map(e -> e.getWeight()).sorted().collect(Collectors.toList());
		int numberOfWeights = sortedWeights.size();
		int thresholdIndex = (int)Math.floor(numberOfWeights * (1.0 - quantile));
		return sortedWeights.get(thresholdIndex);
	}

	@Override
	public boolean containsVertex(InstanceVertex instanceVertex) {
		for(InstanceVertex i: super.vertexSet()) {
			if(i.getId().equalsIgnoreCase(instanceVertex.getId()) && i.getType().equals(instanceVertex.getType()))
				return true;
		}
		return false;
	}

	public Map<EventType, Integer> countVerticesByEventType() {

		Map<EventType, Integer> countMap = new HashMap<>();

		for(InstanceVertex v: vertexSet()) {
			countMap.putIfAbsent(v.getType(), 0);
			countMap.put(v.getType(), countMap.get(v.getType()) + 1);
		}

		return countMap;
	}
}
