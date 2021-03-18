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
package edu.gsu.cs.dmlab.solgrind.algo.stesminers;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.types.event.EventSequence;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceSequence;
import edu.gsu.cs.dmlab.solgrind.index.InstanceVertex;
import edu.gsu.cs.dmlab.solgrind.index.SequenceGraph;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 
 * @author Ahmet Kucuk, Data Mining Lab, Georgia State University
 * 
 */
public class TopRKESMiner extends ESMiner {

	private int K = 0;
	private PriorityQueue<EventSequence> topKEventSequences;

	/**
	 * Given an event sequence graph (esGraph), and a significance threshold for the
	 * co-occurrences of head and tail window of instances, this finds all the event
	 * sequences in the graph
	 *
	 * @param esGraph
	 */
	public TopRKESMiner(SequenceGraph esGraph, double rPercent, int K) {
		super(esGraph);
		this.esGraph.rPercentFilter(rPercent);

		this.K = K;
		topKEventSequences = new PriorityQueue<>(this.K, EventSequence.piComparator);

		mineGraph();
		System.out.println(
				"Found " + topKEventSequences.size() + " STESs.. Min PI: " + topKEventSequences.peek().getPiValue());
		System.out.println("==END OF MINING (Top-(R%, K) Miner)");
	}

	@Override
	public Set<EventSequence> getResultSet() {
		return topKEventSequences.stream().collect(Collectors.toSet());
	}

	@Override
	public Map<EventSequence, Double> getResultInMap() {
		Map<EventSequence, Double> resultMap = new HashMap<>();
		for (EventSequence es : topKEventSequences) {
			resultMap.put(es, es.getPiValue());
		}

		return resultMap;
	}

	@Override
	protected void mineGraph() {

		for (EventType e : eventCounts.keySet()) {
			double minpr = 1.0;
			Set<InstanceVertex> subsetVertices = findVerticesOfType(e, true);
			Set<InstanceSequence> instanceSequences = createInstanceSequencesFromVertices(subsetVertices);
			EventSequence s = new EventSequence(e);
			s.setPiValue(minpr);

			if (minpr > getCurrentTopKPi()) {
				recursiveMine(s, instanceSequences);
			}
		}
	}

	private double getCurrentTopKPi() {
		EventSequence peakSeq = topKEventSequences.peek();
		double pi_th;
		if (peakSeq == null || topKEventSequences.size() < K) {
			pi_th = 0;
		} else {
			pi_th = peakSeq.getPiValue();
		}
		return pi_th;
	}

	private void recursiveMine(EventSequence s, Set<InstanceSequence> isqSet) {

		Map<EventSequence, Set<InstanceSequence>> esqChildrenMap = createSuccessorMap(s, isqSet);

		for (Entry<EventSequence, Set<InstanceSequence>> entry : esqChildrenMap.entrySet()) {
			EventSequence childESq = entry.getKey();
			Set<InstanceSequence> childrenISq = entry.getValue();
			ArrayList<HashSet<String>> iSqIdList = createIdList(childrenISq, childESq);

			double pi = calculatePI(childESq, iSqIdList);
			if (pi > getCurrentTopKPi()) {
				childESq.setPiValue(pi);

				topKEventSequences.add(childESq);
				if (topKEventSequences.size() > K) {
					topKEventSequences.poll();
				}

				if (childESq.getLength() < MAX_SEQUENCE_LENGTH) {
					recursiveMine(childESq, childrenISq);
				}
			}
		}

	}
}
