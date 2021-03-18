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
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceData;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceSequence;
import edu.gsu.cs.dmlab.solgrind.index.InstanceVertex;
import edu.gsu.cs.dmlab.solgrind.index.RelationEdge;
import edu.gsu.cs.dmlab.solgrind.index.SequenceGraph;

import java.util.*;

/**
 * 
 * Algorithm can be found in:
 * 
 * Berkay Aydin, Rafal Angryk. "Spatiotemporal event sequence mining from
 * evolving regions". ICPR 2016.
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class SequenceConnectMiner extends ESMiner {

	private final double pi_th;
	private Map<EventSequence, Double> resultMap;

	/**
	 * Given the follow relationships (in the form of event sequence graph
	 * (esGraph)), and a prevalence threshold STESs, this finds all the event
	 * sequences using Apriori based SequenceConnect algorithm.
	 * 
	 * 
	 * @param esGraph
	 *            - Event Sequence Graph
	 * @param threshold
	 *            - prevalence index threshold
	 */
	public SequenceConnectMiner(SequenceGraph esGraph, double threshold) {
		super(esGraph);
		this.pi_th = threshold;
		resultMap = new HashMap<>();

		mineGraph();
	}

	@Override
	protected void mineGraph() {
		int k = 2;
		// initialization
		HashSet<EventType> eventTypes = new HashSet<EventType>(eventCounts.keySet());
		Set<EventSequence> candidateEventSequences = generateL2CandidateSTESs(eventTypes);
		Map<EventSequence, HashSet<InstanceSequence>> instanceSequences = generateL2InstanceSequences(
				candidateEventSequences);
		Set<EventSequence> prevalentEventSequences = filterCandidateSTESs(instanceSequences);
		addToResultMap(prevalentEventSequences);

		// iterative steps
		while (prevalentEventSequences.size() > 0 && k <= MAX_SEQUENCE_LENGTH) {
			k++;
			candidateEventSequences = generateCandidateSTESs(prevalentEventSequences);
			instanceSequences = generateLkInstanceSequences(instanceSequences, candidateEventSequences);
			prevalentEventSequences = filterCandidateSTESs(instanceSequences);

			if (prevalentEventSequences.size() == 0) {
				System.out.println("==END OF MINING (Sequence Connect)");
			} else {
				addToResultMap(prevalentEventSequences);
			}
		}

	}

	/**
	 * This method generates length-k [candidate] instance sequences and length-k
	 * candidate event sequences. The instance sequences of length-(k-1) are given
	 * as the instanceSequences (Map). The candidateEventSequences are given as a
	 * set.
	 * 
	 * @param instanceSequences
	 *            - map of EventSequence->Set<InstanceSequence> of length-(k-1)
	 * @param candidateEventSequences
	 *            - candidate EventSequences of length-k
	 * @return Map of length-k instance sequences (mapped by their EventSequence ->
	 *         Set<InstanceSequence>
	 */
	private Map<EventSequence, HashSet<InstanceSequence>> generateLkInstanceSequences(
			Map<EventSequence, HashSet<InstanceSequence>> instanceSequences,
			Set<EventSequence> candidateEventSequences) {
		Map<EventSequence, HashSet<InstanceSequence>> isqMap = new HashMap<>();

		for (EventSequence candidate : candidateEventSequences) {
			// System.out.println("Checking for " + candidate);
			isqMap.put(candidate, new HashSet<InstanceSequence>());

			EventSequence cFollowee = candidate.getHeadSubsequence(candidate.getLength() - 1);
			EventSequence cFollower = candidate.getTailSubsequence(candidate.getLength() - 1);

			HashSet<InstanceSequence> followees = instanceSequences.get(cFollowee);
			HashSet<InstanceSequence> followers = instanceSequences.get(cFollower);

			for (InstanceSequence followee : followees) {
				int size = followee.getLength();
				InstanceSequence subFE = followee.getSubsequence(1, size);
				for (InstanceSequence follower : followers) {
					InstanceSequence subFR = follower.getSubsequence(0, size - 1);

					// System.out.println("\tChecking " + subFE + " -- " + subFR);
					if (subFE.equals(subFR)) {
						// System.out.println("\t\tMatches! " + followee + " -- " + follower);
						InstanceSequence newIsq = followee.appendAndCreate(follower.getLast());
						isqMap.get(candidate).add(newIsq);
						// System.out.println("\t\t==> Adding " + newIsq);
					}
				}
			}
		}
		return isqMap;
	}

	/**
	 * This method generates length-2 candidate spatiotemporal event sequences from
	 * a set of event types. The operation is a simple permutation.
	 * 
	 * @param eventTypes
	 *            - set of event types
	 * @return length-2 candidate STESs
	 */
	private HashSet<EventSequence> generateL2CandidateSTESs(HashSet<EventType> eventTypes) {
		if (eventTypes == null || eventTypes.size() == 0) {
			return null;
		}
		// else we can continue generating the candidates
		HashSet<EventSequence> L2Candidates = new HashSet<>();
		for (EventType e_i : eventTypes) {
			for (EventType e_j : eventTypes) {
				EventSequence es = new EventSequence(e_i);
				es.insert(e_j);
				L2Candidates.add(es);
			}
		}
		return L2Candidates;
	}

	/**
	 * Given a map (that maps EventSequence -> Set of InstanceSequence's), this
	 * method initially determines the unique participating event instances for each
	 * event sequence. Using the counts of the unique event instances, it filters
	 * the event sequences based on the pi threshold value (pi_th)
	 * 
	 * @param instanceSequences
	 *            - map of event sequence to (set of instance sequences)
	 * @return - filtered candidate event sequences
	 */
	private Set<EventSequence> filterCandidateSTESs(Map<EventSequence, HashSet<InstanceSequence>> instanceSequences) {

		Iterator<EventSequence> iter = instanceSequences.keySet().iterator();

		while (iter.hasNext()) {
			EventSequence eSq = iter.next();
			HashSet<InstanceSequence> isqSet = instanceSequences.get(eSq);

			// initialize instance sequence id list (list of hash set for finding pi)
			ArrayList<HashSet<String>> iSqIdList = createIdList(isqSet, eSq);
			double pi = calculatePI(eSq, iSqIdList);
			if (pi_th >= pi) {
				iter.remove();
			} else {
				eSq.setPiValue(pi);
			}
		}
		return instanceSequences.keySet();
	}

	/**
	 * This method generates candidate event sequences (EventSequence) using the
	 * length-(k-1) event sequences found in a previous iteration. This method only
	 * takes event sequences of length-(k-1), where (k-1) is at least 2, and results
	 * in length-k candidate sequences where k is at least 3.
	 * 
	 * @param eventSequences
	 *            - prevalent event sequences of length-(k-1)
	 * @return candidate event sequences of length-k
	 */
	private Set<EventSequence> generateCandidateSTESs(Set<EventSequence> eventSequences) {

		Set<EventSequence> lengthKCandidates = new HashSet<EventSequence>();

		for (EventSequence esq_i : eventSequences) {

			for (EventSequence esq_j : eventSequences) {
				// if the last k-1 event types of esq_i and first k-1 event types of esq_j
				// matches
				if (EventSequence.matches(esq_i, esq_j)) {
					// then create a new event sequence and add it to the results
					EventSequence candidate = EventSequence.connect(esq_i, esq_j);
					lengthKCandidates.add(candidate);
				}
			}
		}

		return lengthKCandidates;
	}

	/**
	 * generate length-2 candidate instance sequences using the edges in the esGraph
	 * (class' instance) This method simply transforms the edges into instance
	 * sequences of candidate event sequences (given as a parameter).
	 * 
	 * @param candidateEventSequences
	 *            - set of candidate event sequences whose instance sequences are to
	 *            be probed
	 * @return map of EventSequence->Set<InstanceSequence> for each candidate event
	 *         sequence given as a parameter
	 */
	private Map<EventSequence, HashSet<InstanceSequence>> generateL2InstanceSequences(
			Set<EventSequence> candidateEventSequences) {
		Set<RelationEdge> followEdges = esGraph.edgeSet(); // follow relations in the form of edge set

		Map<EventSequence, HashSet<InstanceSequence>> L2ISqs = new HashMap<>();

		for (EventSequence ces : candidateEventSequences) {
			if (ces.getLength() != 2) {
				System.err.println("Candidate STES:" + ces.toString() + " is not length-2");
				return null;
			} else {
				L2ISqs.put(ces, new HashSet<InstanceSequence>());
			}
		}
		for (RelationEdge edge : followEdges) {

			InstanceVertex followeeIns = esGraph.getEdgeSource(edge);
			InstanceVertex followerIns = esGraph.getEdgeTarget(edge);
			EventType followeeType = followeeIns.getType();
			EventType followerType = followerIns.getType();
			EventSequence es = new EventSequence(followeeType);
			es.insert(followerType);
			InstanceSequence iSq = new InstanceSequence(InstanceData.createFromInstanceVertex(followeeIns));
			iSq.append(InstanceData.createFromInstanceVertex(followerIns));
			// System.out.println("Added: " + iSq.getSequenceData());

			L2ISqs.get(es).add(iSq);
		}

		return L2ISqs;
	}

	/**
	 * adds the resulting prevalent event sequences to prevalent event sequence map
	 * 
	 * @param prevalentEventSequences
	 */
	private void addToResultMap(Set<EventSequence> prevalentEventSequences) {
		if (prevalentEventSequences.size() != 0) {
			int k = prevalentEventSequences.iterator().next().getLength();
			System.out
					.println("Found " + prevalentEventSequences.size() + " length " + k + " Prevalent Event Sequences");

		}

		for (EventSequence esq : prevalentEventSequences) {
			resultMap.put(esq, esq.getPiValue());
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
