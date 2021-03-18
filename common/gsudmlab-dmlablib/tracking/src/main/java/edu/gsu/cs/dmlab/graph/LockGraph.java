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
package edu.gsu.cs.dmlab.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ITrackingGraphProblemFactory;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

/**
 * This class is a wrapper for a simple directed weighted graph that provides
 * locking on insert of new edges using
 * {@link edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory
 * ISTTrackingTrajectory} objects as the nodes. Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class LockGraph extends SimpleDirectedWeightedGraph<String, Edge> implements ITrackingGraph {

	private static final long serialVersionUID = -2991471537875150443L;
	Lock edgeLoc;
	Lock vertexLoc;
	ISTEdgeWeightCalculator weightCalculator;
	ITrackingGraphProblemFactory factory;

	HashMap<String, ISTTrackingTrajectory> tracksMap;
	String SOURCE = "Source";
	String SINK = "Sink";

	public LockGraph(Class<? extends Edge> edgeClass, ITrackingGraphProblemFactory factory,
			ISTEdgeWeightCalculator weightCalculator) {
		super(edgeClass);
		if (factory == null)
			throw new IllegalArgumentException("Factory cannot be null.");
		if (weightCalculator == null)
			throw new IllegalArgumentException("Weight Calculator cannot be null.");

		this.weightCalculator = weightCalculator;
		this.factory = factory;

		this.edgeLoc = new ReentrantLock();
		this.vertexLoc = new ReentrantLock();
		this.addVertex(this.SOURCE);
		this.addVertex(this.SINK);
		this.tracksMap = new HashMap<String, ISTTrackingTrajectory>();
	}

	@Override
	public void finalize() throws Throwable {
		this.weightCalculator = null;
		this.factory = null;

		this.edgeLoc = null;
		this.vertexLoc = null;

		this.tracksMap.clear();
		this.tracksMap = null;
	}

	@Override
	public boolean addEdge(String sourceVertex, String targetVertex, Edge edge) {
		if (sourceVertex == null)
			throw new IllegalArgumentException("Source Vertex cannot be null.");
		if (targetVertex == null)
			throw new IllegalArgumentException("Target Vertex cannot be null.");
		if (edge == null)
			throw new IllegalArgumentException("Edge cannot be null.");

		this.edgeLoc.lock();
		boolean val = super.addEdge(sourceVertex, targetVertex, edge);
		this.edgeLoc.unlock();
		return val;
	}

	@Override
	public void setEdgeWeight(Edge e, double weight) {
		this.edgeLoc.lock();
		super.setEdgeWeight(e, weight);
		e.setWeight(weight);
		this.edgeLoc.unlock();
	}

	@Override
	public void addTrackToGraph(ISTTrackingTrajectory track) {

		// Add two vertices for each track.
		this.vertexLoc.lock();
		String trkUUID1 = track.getFirst().getUUID().toString() + 1;
		String trkUUID2 = track.getLast().getUUID().toString() + 2;
		this.tracksMap.put(trkUUID1, track);
		this.tracksMap.put(trkUUID2, track);

		super.addVertex(trkUUID1);
		super.addVertex(trkUUID2);
		this.vertexLoc.unlock();

		// Add edge from source to first vertex for track
		double entP = this.weightCalculator.sourceEdgeWeight(track.getFirst());
		Edge inEdg = this.factory.getEdge(entP);
		this.addEdge(SOURCE, trkUUID1, inEdg);
		this.setEdgeWeight(inEdg, inEdg.getWeight());

		// Add edge to sink from the second vertex for track.
		double exP = this.weightCalculator.sinkEdgeWeight(track.getLast());
		Edge exEdg = this.factory.getEdge(exP);
		this.addEdge(trkUUID2, SINK, exEdg);
		this.setEdgeWeight(exEdg, exEdg.getWeight());

		// Add observation edge between the two vertices for this track.
		double obsCost = this.weightCalculator.observationEdgeWeight(track.getFirst());
		Edge obsEdg = this.factory.getEdge(obsCost);
		this.addEdge(trkUUID1, trkUUID2, obsEdg);
		this.setEdgeWeight(obsEdg, obsEdg.getWeight());

	}

	@Override
	public String getSinkName() {
		return this.SINK;
	}

	@Override
	public String getSourceName() {
		return this.SOURCE;
	}

	@Override
	public ISTTrackingTrajectory getTrackForVertex(String name) {
		return this.tracksMap.get(name);
	}

	@Override
	public boolean addAssociationPossibility(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {

		double weightVal = this.weightCalculator.associationEdgeWeight(leftTrack, rightTrack);
		this.vertexLoc.lock();
		if (this.containsTrackNoLock(leftTrack) && this.containsTrackNoLock(rightTrack)) {
			String fromUUID2 = leftTrack.getLast().getUUID().toString() + 2;
			String toUUID1 = rightTrack.getFirst().getUUID().toString() + 1;

			Edge edg = this.factory.getEdge(weightVal);

			this.addEdge(fromUUID2, toUUID1, edg);
			this.setEdgeWeight(edg, weightVal);
			this.vertexLoc.unlock();
			return true;
		} else {
			this.vertexLoc.unlock();
			return false;
		}
	}

	private boolean containsTrackNoLock(ISTTrackingTrajectory track) {
		String trkUUID1 = track.getFirst().getUUID().toString() + 1;
		ISTTrackingTrajectory trk = this.tracksMap.get(trkUUID1);
		if (trk != null)
			return true;
		return false;
	}

	@Override
	public boolean containsTrack(ISTTrackingTrajectory track) {
		String trkUUID1 = track.getFirst().getUUID().toString() + 1;
		this.vertexLoc.lock();
		ISTTrackingTrajectory trk = this.tracksMap.get(trkUUID1);
		this.vertexLoc.unlock();
		if (trk != null)
			return true;
		return false;
	}

	@Override
	public boolean moveTrackToGraph(ISTTrackingTrajectory track, ITrackingGraph graph) {
		// First lock down the graph so nothing changes while we are changing
		// this graph. If you pass in this graph as the graph to move to, then
		// there WILL BE a deadlock condition and you cannot say I didn't warn
		// you.
		this.vertexLoc.lock();
		this.edgeLoc.lock();
		if (containsTrackNoLock(track)) {
			this.moveTrackToGraphNoLock(track, graph);
			this.edgeLoc.unlock();
			this.vertexLoc.unlock();
			return true;
		} else {
			this.edgeLoc.unlock();
			this.vertexLoc.unlock();
			return false;
		}
	}

	private void moveTrackToGraphNoLock(ISTTrackingTrajectory track, ITrackingGraph graph) {
		// Get the vertex id for both vertices associated with this track.
		String trkUUID1 = track.getFirst().getUUID().toString() + 1;
		String trkUUID2 = track.getLast().getUUID().toString() + 2;

		// Remove them from the map so next time we check to see if the track is
		// in this graph, we get false.
		this.tracksMap.remove(trkUUID1);
		this.tracksMap.remove(trkUUID2);

		// Add this track to the other graph.
		graph.addTrackToGraph(track);

		// Get all the edges coming into this track.
		Set<Edge> inSet = this.edgesOf(trkUUID1);

		// Create list to hold tracks attached to this track
		ArrayList<ISTTrackingTrajectory> trackList = new ArrayList<ISTTrackingTrajectory>();
		inSet.forEach(edg -> {
			// if the edge is not coming from the source or from this vertex
			String src = this.getEdgeSource(edg);
			if (!src.equals(this.SOURCE) && !src.equals(trkUUID1)) {
				// then we get the track for the source
				ISTTrackingTrajectory trk = this.tracksMap.get(src);
				String tmpTrkUUID = trk.getLast().getUUID().toString() + 2;
				// just make sure the other graph doesn't have the track and add
				// it if it does not.
				if (!graph.containsTrack(trk)) {
					graph.addTrackToGraph(trk);
				}

				// Now add the edge from that track to the one we are
				// processing.
				graph.addEdge(tmpTrkUUID, trkUUID1, edg);
				graph.setEdgeWeight(edg, edg.getWeight());

				// And remove the edge from this graph.
				this.removeEdge(edg);
				// Add the track to the list to be processed after this one.
				trackList.add(trk);
			} else {
				// if the edge was coming from the source or from the first
				// vertex of this track, then just remove it.
				// When we add the track to the other graph it will calculate a
				// new edge from the source of that graph and between the two
				// vertices of this track.
				this.removeEdge(edg);
			}
		});

		// Now we need to process those edges leaving this track
		Set<Edge> outSet = this.edgesOf(trkUUID2);
		outSet.forEach(edg -> {
			// if the edge target is not the sink or this vertex.
			String trgt = this.getEdgeTarget(edg);
			if (!trgt.equals(this.SINK) && !trgt.equals(trkUUID2)) {
				// then we need to get the target track
				ISTTrackingTrajectory trk = this.tracksMap.get(trgt);
				String tmpTrkUUID1 = trk.getFirst().getUUID().toString() + 1;
				// just make sure the other graph doesn't have this track and
				// add it if it does not.
				if (!graph.containsTrack(trk)) {
					graph.addTrackToGraph(trk);
				}
				// now add the edge from the track we are processing to the
				// other one we just pulled from the edge set.
				graph.addEdge(trkUUID2, tmpTrkUUID1, edg);
				graph.setEdgeWeight(edg, edg.getWeight());

				// And remove the edge from this graph.
				this.removeEdge(edg);
				// Add the track to the list to be processed after this one.
				trackList.add(trk);
			} else {
				// if the edge was going to the sink or coming from the other
				// vertex of this track, then just remove it.
				// When we add the track to the other graph it will calculate a
				// new edge to the sink of that graph and the edge between the
				// two vertices of the track.
				this.removeEdge(edg);
			}
		});

		trackList.forEach(trk -> {
			this.moveTrackToGraphNoLock(trk, graph);
		});
	}
}
