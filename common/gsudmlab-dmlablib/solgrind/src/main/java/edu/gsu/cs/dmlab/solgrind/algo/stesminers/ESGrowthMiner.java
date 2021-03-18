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

/**
 * Algorithm can be found in:
 * 
 * Berkay Aydin, and Rafal Angryk. "Discovering spatiotemporal event sequences."
 * ACM SIGSPATIAL - MobiGIS, 2016.
 * 
 * or
 * 
 * Berkay Aydin, and Rafal A. Angryk. "A graph-based approach to spatiotemporal
 * event sequence mining." ICDMW, 2016.
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class ESGrowthMiner extends ESMiner {

	private final double pi_th;
	private Map<EventSequence, Double> resultMap;

	/**
	 * Given an event sequence graph (esGraph), and a prevalence threshold for the
	 * frequency of the STESs, this finds all the STESs using the instance sequences
	 * from the event sequence graph.
	 * 
	 * 
	 * @param esGraph
	 *            - Event Sequence Graph
	 * @param threshold
	 *            - prevalence index threshold
	 */
	public ESGrowthMiner(SequenceGraph esGraph, double threshold) {
		super(esGraph);
		this.pi_th = threshold;
		resultMap = new HashMap<>();

		mineGraph();
		System.out.println("Found " + resultMap.size() + " STESs..");
		System.out.println("==END OF MINING (EsGrowth) pi_th:" + threshold);

	}

	@Override
	protected void mineGraph() {

		for (EventType e : eventCounts.keySet()) {
			double minpr = 1.0;
			Set<InstanceVertex> subsetVertices = findVerticesOfType(e, true);
			Set<InstanceSequence> instanceSequences = createInstanceSequencesFromVertices(subsetVertices);

			EventSequence s = new EventSequence(e);
			ArrayList<HashSet<String>> idList = createIdList(instanceSequences, s);
			minpr = calculatePI(s, idList); // min. of participation ratio is pi
			s.setPiValue(minpr);

			if (minpr > pi_th) {
				recursiveMine(s, instanceSequences);
			}
			// System.out.println("Found " + (resultMap.size()-counter) + " STESs starting
			// with " + e.getType());

		}
	}

	private void recursiveMine(EventSequence s, Set<InstanceSequence> isqSet) {

		Map<EventSequence, Set<InstanceSequence>> esqChildrenMap = createSuccessorMap(s, isqSet);

		for (Entry<EventSequence, Set<InstanceSequence>> entry : esqChildrenMap.entrySet()) {
			EventSequence childESq = entry.getKey();
			Set<InstanceSequence> childrenISq = entry.getValue();
			ArrayList<HashSet<String>> iSqIdList = createIdList(childrenISq, childESq);

			double pi = calculatePI(childESq, iSqIdList);
			if (pi > pi_th) {
				childESq.setPiValue(pi);
				resultMap.put(childESq, pi);
				if (childESq.getLength() < MAX_SEQUENCE_LENGTH) {
					recursiveMine(childESq, childrenISq);
				}

			}
		}
	}

	@Override
	public Set<EventSequence> getResultSet() {
		return resultMap.keySet();
	}

	@Override
	public Map<EventSequence, Double> getResultInMap() {
		return resultMap;
	}

	public Map<EventSequence, Map<InstanceSequence, Double>> getComprehensiveInMap() {

		Map<EventSequence, Map<InstanceSequence, Double>> comprehensiveResults = new HashMap<>();
		for (EventSequence esq : resultMap.keySet()) {
			HashMap<InstanceSequence, Double> isqMap = searchInstanceSequences(esq);
			comprehensiveResults.put(esq, isqMap);
		}
		return comprehensiveResults;
	}

}
