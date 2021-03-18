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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.solgrind.algo.measures.significance.JStar;
import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.operations.STOperations;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;
import edu.gsu.cs.dmlab.solgrind.base.types.event.EventCooccurrence;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceCooccurrence;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceData;

/**
 * Spatiotemporal co-occurrence pattern mining discovers sets of event types
 * whose instances frequently co-occur in both space and time.
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class StcopMiner {

	static final double CCEth = 0.01;
	static final double PIth = 0.001;

	static Map<EventCooccurrence, Collection<InstanceCooccurrence>> patternInstanceMap = null;
	static Map<EventType, Integer> instanceCountMap = null;

	public StcopMiner(Map<EventType, Set<Instance>> instanceMap) {
		patternInstanceMap = new HashMap<>();
		instanceCountMap = countInstances(instanceMap);
		Set<EventType> eventTypes = instanceMap.keySet();

		ArrayList<EventCooccurrence> S2_CP = generateSize2CandidateSTCOPs(eventTypes);
		ArrayList<EventCooccurrence> S2_PP = discoverSize2STCOPs(S2_CP, instanceMap);
		ArrayList<EventCooccurrence> prevPatterns = S2_PP;
		// System.out.println("Size 2 Patterns==>>\n" + S2_PP);
		while (prevPatterns != null && !prevPatterns.isEmpty()) {

			ArrayList<EventCooccurrence> SK_CP = generateSizeKCandidateSTCOPs(prevPatterns);
			ArrayList<EventCooccurrence> SK_PP = discoverSizeKSTCOPs(SK_CP, patternInstanceMap);
			prevPatterns = SK_PP;
		}
	}

	private ArrayList<EventCooccurrence> discoverSizeKSTCOPs(ArrayList<EventCooccurrence> sK_CP,
			Map<EventCooccurrence, Collection<InstanceCooccurrence>> patternInstanceMap2) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList<EventCooccurrence> generateSizeKCandidateSTCOPs(ArrayList<EventCooccurrence> prevPatterns) {
		Collections.sort(prevPatterns);
		System.out.println(prevPatterns);
		// pre-implementation comment:
		// assuming here is that the treeSet sorts the strings too. I hope it
		// works. Fingers crossed
		ArrayList<EventCooccurrence> currentCandidates = new ArrayList<EventCooccurrence>();

		int prevCardinality = prevPatterns.get(0).getCardinality();

		for (int i = 0; i < prevPatterns.size(); i++) {
			EventCooccurrence pattern = new EventCooccurrence(prevPatterns.get(i));
			TreeSet<EventType> patternEventTypes = new TreeSet<EventType>(pattern.getEventTypes());
			EventType firstET = patternEventTypes.first();
			EventType lastET = patternEventTypes.last();

			TreeSet<EventType> query = (TreeSet<EventType>) patternEventTypes.subSet(firstET, false, lastET, true);
			System.out.println("Query" + query);
			for (int j = i + 1; j < prevPatterns.size(); j++) {
				EventCooccurrence p = new EventCooccurrence(prevPatterns.get(j));
				TreeSet<EventType> pFeatures = new TreeSet<>(p.getEventTypes());
				EventType pLastElement = pFeatures.pollLast();

				System.out.println("\tpFeatures" + pFeatures);
				System.out.println("\tLast Element:" + pLastElement);

				if (pFeatures.size() == 0) { // it means the prev cardinality is
												// 1, we should be good
					EventCooccurrence t = new EventCooccurrence();
					t.addEventType(firstET);
					t.addEventType(pLastElement);
					currentCandidates.add(t);
				} else { // it means we need to check if pFeatures and query is
					// the same
					if (query.containsAll(pFeatures)) {
						// then we need to check for other possible subsets
						EventCooccurrence possibleCandidate = pattern.union(prevPatterns.get(j));
						System.out.println("\t\tPossible Cand:" + possibleCandidate);
						ArrayList<EventType> pcEventTypes = new ArrayList<EventType>(possibleCandidate.getEventTypes());
						boolean isRealCandidate = true;
						for (int s = 1; s < pcEventTypes.size() - 1; s++) { // search for other subsets
							ArrayList<EventType> eventTypeList = new ArrayList<EventType>(pcEventTypes);
							eventTypeList.remove(s); // now: eventTypeList is one of the other
							// subsets that must be present in the previous candidate list
							System.out.println("\t\t\tEvent Type List: " + eventTypeList);
							boolean isSubsetsExist = false;

							for (int k = 0; k < prevPatterns.size(); k++) {
								System.out.println(
										"\t\t\t\tPrevPattern Iteration:" + prevPatterns.get(k).getEventTypes());
								if (prevPatterns.get(k).getEventTypes().containsAll(eventTypeList)) {
									isSubsetsExist = true;
									break;
								}
							}
							if (!isSubsetsExist) {// if it doesn't exist, then
								// it is not a candidate
								isRealCandidate = false;
								break;
							}
						}
						if (isRealCandidate) {
							currentCandidates.add(possibleCandidate);
						}
					}
				}
			}
		}
		if (currentCandidates.size() != 0) {
			System.out.println(":::" + (prevCardinality + 1) + "-cardinality candidates:::");
		}

		for (EventCooccurrence t : currentCandidates) {
			System.out.println(t + "\n");
		}

		return currentCandidates;

	}

	/**
	 * This method discovers size-2 STCOPs from given size-2 candidate patterns and
	 * the provided instance map. The event types in instance map and the
	 * participating event types in the candidate pattern list must match.
	 * 
	 * @param s2_CP       -- size-2 candidadate patterns
	 * @param instanceMap -- instance map for each event type.
	 * @return - an array list of STCOPs (in the form of EventCooccurrence objects).
	 */
	private ArrayList<EventCooccurrence> discoverSize2STCOPs(ArrayList<EventCooccurrence> s2_CP,
			Map<EventType, Set<Instance>> instanceMap) {
		ArrayList<EventCooccurrence> s2_pp = new ArrayList<>();
		for (EventCooccurrence size2Candidate : s2_CP) {

			ArrayList<EventType> eventTypes = new ArrayList<EventType>(size2Candidate.getEventTypes());

			TreeSet<Instance> e1Instances = new TreeSet<>(instanceMap.get(eventTypes.get(0)));
			TreeSet<Instance> e2Instances = new TreeSet<>(instanceMap.get(eventTypes.get(1)));

			ArrayList<InstanceCooccurrence> significantCOs = findCooccurences(e1Instances, e2Instances);
			double pi = calculatePi(size2Candidate, significantCOs);
			if (pi > StcopMiner.PIth) {
				System.out.println("Candidate passed the pi threshold " + size2Candidate + " PI:" + pi);
				patternInstanceMap.put(size2Candidate, significantCOs);
				s2_pp.add(size2Candidate);
			}
		}

		return s2_pp;
	}

	private double calculatePi(EventCooccurrence candidate, ArrayList<InstanceCooccurrence> cooccurrences) {
		HashMap<EventType, Set<String>> participatingInstanceMap = new HashMap<>();
		for (EventType et : candidate.getEventTypes()) {
			participatingInstanceMap.put(et, new TreeSet<>());
		}

		for (InstanceCooccurrence cooccurrence : cooccurrences) {
			for (EventType et : candidate.getEventTypes()) {
				String id = cooccurrence.getInstanceIdOfType(et);
				participatingInstanceMap.get(et).add(id);
			}
		}
		double pi = Double.MAX_VALUE;
		for (Entry<EventType, Set<String>> kv : participatingInstanceMap.entrySet()) {
			double total = instanceCountMap.get(kv.getKey());
			double participatorCount = participatingInstanceMap.get(kv.getKey()).size();
			double pr = participatorCount / total; // participation ratio
			if (pr < pi) {
				pi = pr;
			}
		}
		if (pi == Double.MAX_VALUE) {
			System.out.println("Warning invalid pi for" + candidate + " returning pi=-1.0");
			return -1.0;
		} else {
			return pi;
		}
	}

	/**
	 * Find ST co-occurrences between two sets of instances
	 * 
	 * @param e1Instances - instances of event type e1
	 * @param e2Instances - instances of event type e2
	 * @return An ArrayList of pattern instances (in the form of
	 *         InstanceCooccurrence objects)
	 */
	private ArrayList<InstanceCooccurrence> findCooccurences(TreeSet<Instance> e1Instances,
			TreeSet<Instance> e2Instances) {
		ArrayList<InstanceCooccurrence> overlappingCooccurrences = new ArrayList<>();
		for (Instance e1i : e1Instances) {
			for (Instance e2i : e2Instances) {
				InstanceCooccurrence ic = findCooccurrence(e1i, e2i);
				if (ic.isCceValid()) {
					// System.out.println("Overlapping ST CO: " + ic );
					overlappingCooccurrences.add(ic);
				}
			}
		}
		return overlappingCooccurrences;

	}

	/**
	 * Given two instances ins1 and ins2, this method creates an InstanceCoocurrence
	 * object (which may or may not be empty). The empty object corresponds to no
	 * pattern instance, and it is invalid. The non-empty ones are calculated. USE
	 * THIS WITH CAUTION, THIS IS AN EXPENSIVE OPERATION
	 * 
	 * @param ins1 - instance 1 to be checked
	 * @param ins2 - instance 2 to be checked
	 * @return a pattern instance (or an empty cooccurrence)
	 */
	private InstanceCooccurrence findCooccurrence(Instance ins1, Instance ins2) {
		InstanceCooccurrence ic = new InstanceCooccurrence();
		if (!STOperations.stIntersects(ins1.getTrajectory(), ins2.getTrajectory())) {
			return ic; // return empty ic
		} else {
			Trajectory traj1 = ins1.getTrajectory();
			Trajectory traj2 = ins2.getTrajectory();
			double jstar = new JStar().calculateT(traj1, traj2);
			if (jstar > StcopMiner.CCEth) {
				return ic; // return empty ic
			} else {
				Trajectory uTraj = STOperations.union(traj1, traj2);
				Trajectory iTraj = STOperations.intersection(traj1, traj2);
				SortedSet<Interval> xcoIntervals = iTraj.getTimeIntervals();

				ic.append(new InstanceData(ins1));
				ic.append(new InstanceData(ins2));
				ic.setIntersectionTrajectory(iTraj);
				ic.setUnionTrajectory(uTraj);
				ic.setXcoTimeIntervals(xcoIntervals);
				ic.setCce(jstar);
				ic.setcceType("J*");

				return ic;
			}
		}
	}

	/**
	 * This generates size-2 candidate patterns
	 * 
	 * @param eventTypes: set of event types
	 * @return an arraylist of candidates to be examined
	 */
	private ArrayList<EventCooccurrence> generateSize2CandidateSTCOPs(Set<EventType> eventTypes) {
		ArrayList<EventCooccurrence> size2Candidates = new ArrayList<>();
		ArrayList<EventType> etList = new ArrayList<>(eventTypes);
		for (int i = 0; i < etList.size(); i++) {
			for (int j = i + 1; j < etList.size(); j++) {
				EventCooccurrence candidate = new EventCooccurrence();
				candidate.addEventType(etList.get(i));
				candidate.addEventType(etList.get(j));
				size2Candidates.add(candidate);
			}
		}

		return size2Candidates;
	}

	/**
	 * Count the instances in the given InstanceMap for each event type then return
	 * an eventtype count map.
	 * 
	 * @param instanceMap - the EventType -> Set<Instance> map
	 * @return a Map consisting of EventType->(number of instances for the event
	 *         type) kv pairs
	 */
	private Map<EventType, Integer> countInstances(Map<EventType, Set<Instance>> instanceMap) {
		HashMap<EventType, Integer> countMap = new HashMap<>();
		for (Entry<EventType, Set<Instance>> kv : instanceMap.entrySet()) {
			if (kv.getValue() != null) {
				countMap.put(kv.getKey(), kv.getValue().size());
			}
		}
		return countMap;
	}

//	public static void main(String[] args){
//		Map<EventType, Collection<Instance>> instanceMap = new HashMap<>();
//		
//		instanceMap.put(new EventType("AR"), null);
//		instanceMap.put(new EventType("CH"), null);
//		instanceMap.put(new EventType("FL"), null);
//		instanceMap.put(new EventType("EF"), null);
//		System.out.println(instanceMap);
//		new StcopMiner(instanceMap);
//	}
//	
}
