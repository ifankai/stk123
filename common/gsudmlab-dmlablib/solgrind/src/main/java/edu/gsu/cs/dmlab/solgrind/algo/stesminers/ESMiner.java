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

import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.traverse.TopologicalOrderIterator;


import java.util.*;


/**
 * 
 * @author Ahmet Kucuk, Data Mining Lab, Georgia State University
 * 
 */
public abstract class ESMiner {

	protected final DirectedNeighborIndex<InstanceVertex, RelationEdge> neighbors;
	protected final SequenceGraph esGraph;
	protected Map<EventType, Integer> eventCounts;
	protected Map<EventSequence, Double> resultEventSequenceDoubleMap;

	protected final int MAX_SEQUENCE_LENGTH = 10;//Integer.MAX_VALUE;
	
	public ESMiner(SequenceGraph esGraph) {
		this.esGraph = esGraph;
		neighbors = new DirectedNeighborIndex<>(esGraph);

		eventCounts = esGraph.countVerticesByEventType();
		resultEventSequenceDoubleMap = new HashMap<>();

	}

	protected abstract void mineGraph();
	public abstract Set<EventSequence> getResultSet();
	public abstract Map<EventSequence, Double> getResultInMap();

	/**
	 * Find vertices (instances) of a particular event type in the event sequence graph
	 * Also available is the non-leaf flag, where it is set to true, it finds non-leaf
	 * vertices of type eventType
	 * @param eventType - event type whose instances are to be found
	 * @param isNonLeaf - non-leaf flag, when true instances are non-leaf, when false
	 * 					all instances are returned
	 * @return - a set of instance vertices of type eventType
	 */
	protected HashSet<InstanceVertex> findVerticesOfType(EventType eventType, 
			boolean isNonLeaf) {
		HashSet<InstanceVertex> vertices = new HashSet<>();
		TopologicalOrderIterator<InstanceVertex, RelationEdge> iterator
									= new TopologicalOrderIterator<>(esGraph);
		while (iterator.hasNext()) {
			InstanceVertex v = iterator.next();
			int outDegree = esGraph.outDegreeOf(v);
			//if we are searching for non-leaf nodes
			//do not consider the edges whose outDegree is 0
			if( !(isNonLeaf && outDegree==0) ){ 
				EventType et = v.getType();
				if (et.equals(eventType)) {
					vertices.add(v);
				}
			}
		}
		return vertices;
	}

	/**
	 * This method calculates the prevalence index (PI) of a given EventSequence object using
	 * the @eventCounts (from this class) and a list (of set) of instance sequence identifiers 
	 * (passed as parameter using iSqIdList). iSqIdList has the list of sets of unique instance 
	 * identifiers per each EventType in the given EventSequence object (eSq). 
	 * 
	 * @param eSq - given event sequence object (to calculate the pi)
	 * @param iSqIdList - list of set of unique id's, indexed on the location of event type in eSq
	 * @return - prevalence index value (min of all participation ratios) of eSq
	 */
	protected double calculatePI(EventSequence eSq, ArrayList<HashSet<String>> iSqIdList) {
		double pi = Double.MAX_VALUE;
		for(int i = 0; i < eSq.getEventsList().size(); i++){
			EventType e = eSq.getEventsList().get(i);
			if(eventCounts.get(e) == 0) return 0;

			HashSet<String> vertices = iSqIdList.get(i);
			double pr = ((double) vertices.size()) / ((double)eventCounts.get(e));
			pi = Math.min(pi, pr);
		}
		if(pi == Double.MAX_VALUE) {return 0.0;}
		else {return pi;}
	}

	/**
	 * Given an event sequence and its instance sequences, this method creates a complex
	 * identifier list of unique identifier sets. The participating instances of each
	 * event type in the event sequence is returned as a set of strings (that stores
	 * unique ids for each event type) 
	 * @param instanceSequences - set of instance sequences of type event sequence
	 * @param eventSequence - the event sequence whose instances are to be inspected
	 * @return - arraylist of set of unique identifiers (of participating 
	 * 			 instances) in order
	 */
	protected ArrayList<HashSet<String>> createIdList(
			Set<InstanceSequence> instanceSequences, EventSequence eventSequence) {
		//initialize instance sequence id list (list of hash set for finding pi)
		ArrayList< HashSet<String> > iSqIdList = new ArrayList<>();
		for(int i = 0; i < eventSequence.getEventsList().size(); i++){ // i is the index for the event type in STES
			iSqIdList.add(new HashSet<String>());
			for(InstanceSequence isq : instanceSequences){
				String instanceId = isq.getSequenceData().get(i).id;
				iSqIdList.get(i).add(instanceId);
			}
		}
		return iSqIdList;
	}

	/**
	 * Using the event sequence graph, create a complex successors map that stores 
	 * EventSequence-&gt;Set<InstanceSequence> mappings, which are identified using the
	 * parentEventSequence, and the instance sequences of parentEventSequence (that is
	 * parentInstanceSequences). In other words, from the instance sequences of a 
	 * particular event sequence, this method finds the instance sequences of child
	 * event sequences (found by extending the parent event sequence)
	 * 
	 * @param parentEventSequence - the event sequence to be extended
	 * @param parentInstanceSequences - the instance sequences of the given 
	 * 									parent event sequence
	 * @return the map of child event sequences to its instance sequences
	 */
	public Map<EventSequence, Set<InstanceSequence>> createSuccessorMap(
			EventSequence parentEventSequence,
			Set<InstanceSequence> parentInstanceSequences) {
		Map<EventSequence, Set<InstanceSequence> > childrenMap = new HashMap<>();
		for(EventType e : eventCounts.keySet()){ // initialize the sets
			EventSequence newESq = parentEventSequence.appendEventType(e);
			childrenMap.put(newESq, new HashSet<>());
		}

		for(InstanceSequence parentISq : parentInstanceSequences){ //loop through the parent ISq's
			InstanceVertex v = InstanceData.convertToInstanceVertex(parentISq.getLast());
			List<InstanceVertex> childrenOfV = neighbors.successorListOf(v);

			for(InstanceVertex child : childrenOfV){ //push each children to its particular set in childrenMap
				InstanceData iData = InstanceData.createFromInstanceVertex(child);
				InstanceSequence childISQ = parentISq.appendAndCreate(iData); //create child instance sequence
				EventSequence childESq = childISQ.getEventSequenceType(); // get child's event sequence type
				childrenMap.get(childESq).add(childISQ);
			}
		}
		return childrenMap;
	}

	/**
	 * This method converts a set of instance vertices (stored in instanceVertices)
	 * to a set of length-1 instance sequences. For each instance vertex, a new instance
	 * sequence of length-1 is created.
	 * @param instanceVertices - to be converted instance sequences
	 * @return a set of instance sequences (of length-1)
	 */
	protected Set<InstanceSequence> createInstanceSequencesFromVertices(
			Set<InstanceVertex> instanceVertices) {
		HashSet<InstanceSequence> instanceSequences = new HashSet<InstanceSequence>();
		for(InstanceVertex vertex : instanceVertices){
			InstanceData iData = InstanceData.createFromInstanceVertex(vertex);
			InstanceSequence isq = new InstanceSequence(iData);
			instanceSequences.add(isq);
		}
		return instanceSequences;
	}


	protected HashMap<InstanceSequence,Double> searchInstanceSequences(EventSequence esq){

		ArrayList<EventType> eventList = esq.getEventsList();
		HashSet<InstanceVertex> vertices = new HashSet<InstanceVertex>();

		HashSet<InstanceSequence> instanceSequences = new HashSet<>();
		Set<InstanceSequence> prevInstanceSequences = null;
		for( int i=0; i < eventList.size(); i++ ){
			EventType et = eventList.get(i);

			if(i == 0){
				vertices = this.findVerticesOfType(et, true);
				for(InstanceVertex vertex : vertices){
					InstanceSequence isq = new InstanceSequence(InstanceData.createFromInstanceVertex(vertex));
					instanceSequences.add(isq);
				}
			} else{
				instanceSequences = new HashSet<InstanceSequence>(this.findChildrenMap(prevInstanceSequences, et) );
			}
			prevInstanceSequences = instanceSequences;
		}
		HashMap<InstanceSequence, Double> ciMap = new HashMap<>();
		for(InstanceSequence isq : prevInstanceSequences){
			ciMap.put(isq, isq.getChainIndex());
		}
		return ciMap;
	}

	protected Set<InstanceSequence> findChildrenMap(Set<InstanceSequence> instanceSequences, EventType eChild) {
		Set<InstanceSequence> newInstanceSequences = new HashSet<InstanceSequence>();
		for(InstanceSequence isq : instanceSequences){
			InstanceVertex iv = InstanceData.convertToInstanceVertex( isq.getLast() );
			List<InstanceVertex> childrenOfV = neighbors.successorListOf(iv);

			for(InstanceVertex child : childrenOfV){

				EventType et = child.getType();
				if (et.equals(eChild)) {
					//                    children.add(child);
					InstanceSequence newISq = new InstanceSequence(isq.getSequenceData());
					newISq.append(InstanceData.createFromInstanceVertex(child));
					RelationEdge edge = esGraph.getEdge(iv, child);
					double ci = edge.getWeight();
					newISq.setChainIndex(  Math.min( isq.getChainIndex(), ci) );
				}
			}
		}
		return newInstanceSequences;
	}

}