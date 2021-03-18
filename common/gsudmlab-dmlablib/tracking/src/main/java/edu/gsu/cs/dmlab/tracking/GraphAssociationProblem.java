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
package edu.gsu.cs.dmlab.tracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ITrackingGraphProblemFactory;
import edu.gsu.cs.dmlab.graph.algo.interfaces.ITrackingGraphProblemSolver;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTAssociationProblem;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

/**
 * This class is the graph used to find the optimal multi-commodity flow and
 * then use the results to associate the tracks into longer tracks.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class GraphAssociationProblem implements ISTAssociationProblem {

	private ITrackingGraphProblemFactory factory;
	private ISTEdgeWeightCalculator weightCalculator;
	private List<ISTTrackingTrajectory> tracks;
	private ITrackingGraph grph;
	private boolean solved = false;

	/**
	 * Constructor constructs a new object.
	 * 
	 * @param tracks            The tracks to solve the association problem on.
	 * @param factory2          A factory for creating stuff that this object needs.
	 * @param weightCalculator2 The weight calculator used to calculate the weight
	 *                          on each of the edges in the problem.
	 */
	public GraphAssociationProblem(List<ISTTrackingTrajectory> tracks, ITrackingGraphProblemFactory factory2,
			ISTEdgeWeightCalculator weightCalculator2) {

		if (tracks == null)
			throw new IllegalArgumentException("Tracks cannot be null.");
		if (factory2 == null)
			throw new IllegalArgumentException("Factory cannot be null.");
		if (weightCalculator2 == null)
			throw new IllegalArgumentException("Weight Calculator cannot be null.");

		this.tracks = tracks;
		this.factory = factory2;
		this.weightCalculator = weightCalculator2;

		this.grph = this.factory.getGraph(this.weightCalculator);
		this.buildGraph();
	}

	@Override
	public void finalize() throws Throwable {
		this.factory = null;
		this.weightCalculator = null;
		this.tracks = null;
		this.grph = null;
	}

	private void buildGraph() {
		for (ISTTrackingTrajectory trk : this.tracks) {
			this.grph.addTrackToGraph(trk);
		}
	}

	@Override
	public void addAssociationPossibility(ISTTrackingTrajectory from, ISTTrackingTrajectory to) {
		this.grph.addAssociationPossibility(from, to);

		if (this.solved == true)
			this.solved = false;
	}

	@Override
	public boolean solve() {

		if (this.grph.edgesOf(this.grph.getSourceName()).size() > 0) {
			ITrackingGraphProblemSolver solver = this.factory.getGraphSolver();
			List<ISTTrackingTrajectory[]> edgesList = solver.solve(grph);

			// The solver should have returned the list of tracks that the
			// solution traverses.
			for (int i = 0; i < edgesList.size(); i++) {
				ISTTrackingTrajectory[] edges = edgesList.get(i);
				// some of the edges are between the observation edges, just
				// ignore those.
				if (edges[0].getUUID() != edges[1].getUUID()) {
					// We only get here on the association edges. Link the two
					// tracks into one and move on.
					ISTTrackingTrajectory leftTrack = edges[0];
					ISTTrackingTrajectory rightTrack = edges[1];
					ISTTrackingEvent leftEvent = leftTrack.getLast();
					ISTTrackingEvent rightEvent = rightTrack.getFirst();
					leftEvent.setNext(rightEvent);
					rightEvent.setPrevious(leftEvent);
				}
			}
		}
		this.solved = true;
		return true;
	}

	@Override
	public ArrayList<ISTTrackingTrajectory> getTrackLinked() {
		// If solve wasn't called, then call it.
		if (this.solved == false) {
			this.solve();
		}

		// After solving, some of the track pointers are pointing to the same
		// linked list of events. We just want to prune the duplicates here.
		HashMap<UUID, ISTTrackingTrajectory> uniqueTracks = new HashMap<UUID, ISTTrackingTrajectory>();
		for (int i = 0; i < this.tracks.size(); i++) {
			ISTTrackingTrajectory tmpTrk = this.tracks.get(i);
			if (!uniqueTracks.containsKey(tmpTrk.getFirst().getUUID())) {
				uniqueTracks.put(tmpTrk.getFirst().getUUID(), tmpTrk);
			}
		}

		ArrayList<ISTTrackingTrajectory> returnVals = new ArrayList<ISTTrackingTrajectory>(uniqueTracks.values());
		return returnVals;
	}

}
