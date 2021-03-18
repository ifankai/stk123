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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ITrackingGraphProblemFactory;
import edu.gsu.cs.dmlab.graph.interfaces.ITrackingGraph;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTAssociationProblem;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

/**
 * This class is the graph used to find the optimal multi-commodity flow and
 * then use the results to associate the tracks into longer tracks. This class
 * partitions the data set based on start time of each track and processes these
 * partitions in parallel at the solving step as they are mutually disjoint.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University Michael
 *         Tinglof, Data Mining Lab, Georgia State University
 * 
 */
public class ParallelGraphAssociationProblem implements ISTAssociationProblem {

	ITrackingGraphProblemFactory factory;
	ISTEdgeWeightCalculator weightCalculator;
	List<ISTTrackingTrajectory> tracks;

	DateTime start;
	ITrackingGraph[] grphs;
	long partPeriod;
	boolean solved = false;

	Lock vertexLoc;

	/**
	 * Constructor constructs a new object.
	 * 
	 * @param tracks           The tracks to solve the association problem on.
	 * @param factory          A factory for creating stuff that this object needs.
	 * 
	 * @param weightCalculator Calculates the weight for the edges in the graphs
	 *                         that are used for the association problem.
	 * @param numPartitions    The number of partitions to break the global problem
	 *                         into. This equates to how many graphs to create and
	 *                         solve the association problem on, where each graph is
	 *                         solved independently as another problem at the same
	 *                         time. They are then all merged for a global solution.
	 */
	public ParallelGraphAssociationProblem(List<ISTTrackingTrajectory> tracks, ITrackingGraphProblemFactory factory,
			ISTEdgeWeightCalculator weightCalculator, int numPartitions) {

		if (tracks == null)
			throw new IllegalArgumentException("Tracks cannot be null.");
		if (tracks.size() < 1)
			throw new IllegalArgumentException(
					"Tracks cannot be empty. Really, why are you calling this with no tracks?");
		if (factory == null)
			throw new IllegalArgumentException("Factory cannot be null.");
		if (weightCalculator == null)
			throw new IllegalArgumentException("Weight Calculator cannot be null.");
		if (numPartitions <= 0)
			throw new IllegalArgumentException("Number of partitions needs to be greater than zero.");

		this.tracks = tracks;
		this.factory = factory;
		this.weightCalculator = weightCalculator;
		this.grphs = new ITrackingGraph[numPartitions];

		this.vertexLoc = new ReentrantLock();
		for (int i = 0; i < this.grphs.length; i++)
			this.grphs[i] = this.factory.getGraph(weightCalculator);
		this.buildGraphs();
	}

	private void buildGraphs() {

		// We first need to determine the period over which we will be
		// processing tracks.
		DateTime start = this.tracks.get(0).getFirst().getTimePeriod().getStart();
		DateTime end = this.tracks.get(0).getLast().getTimePeriod().getEnd();
		for (ISTTrackingTrajectory trk : this.tracks) {
			DateTime tmpStart = trk.getFirst().getTimePeriod().getStart();
			DateTime tmpEnd = trk.getLast().getTimePeriod().getEnd();
			if (tmpStart.isBefore(start))
				start = tmpStart;
			if (tmpEnd.isAfter(end))
				end = tmpEnd;
		}
		this.start = start;

		// Given the period for processing, we find the time in each partition.
		this.partPeriod = ((end.getMillis() - start.getMillis()) / this.grphs.length);

		this.tracks.forEach(trk -> {
			int idx = (int) ((trk.getFirst().getTimePeriod().getStart().getMillis() - this.start.getMillis())
					/ this.partPeriod);
			this.grphs[idx].addTrackToGraph(trk);
		});
	}

	@Override
	public void addAssociationPossibility(ISTTrackingTrajectory from, ISTTrackingTrajectory to) {

		int idx;
		this.vertexLoc.lock();
		try {
			idx = (int) ((from.getFirst().getTimePeriod().getStart().getMillis() - this.start.getMillis())
					/ this.partPeriod);

			// see if start track is in the original graph it was meant to be
			// or has been moved to the previous partition by additions previously
			// executed.
			if (!this.grphs[idx].containsTrack(from)) {
				idx = idx - 1;
			}

			// see if the to track is in the same graph as the starting track.
			// If not, we need to move it into the same one so we can do the
			// association.
			if (!this.grphs[idx].containsTrack(to)) {
				// it should be in the next one if we didn't have too many
				// partitions.
				if (this.grphs[idx + 1].containsTrack(to)) {
					this.grphs[idx + 1].moveTrackToGraph(to, this.grphs[idx]);
				} else {
					// if we have too many partitions, then just search for which
					// one it is in.
					for (int i = 0; i < this.grphs.length; i++) {
						if (this.grphs[i].containsTrack(to))
							this.grphs[i].moveTrackToGraph(to, this.grphs[idx]);
					}
				}
			}
		} finally {
			this.vertexLoc.unlock();
		}
		// they should now be in the same graph. Now we add the association
		// possibility.
		this.grphs[idx].addAssociationPossibility(from, to);
	}

	@Override
	public boolean solve() {
		// Process each of the graphs in parallel as they are mutually disjoint
		// sets of data at this point.
		IntStream.range(0, this.grphs.length).parallel().forEach(i -> {
			// System.out.println("Beging Graph Problem Solution.");
			List<ISTTrackingTrajectory[]> trkSolutions = this.factory.getGraphSolver().solve(this.grphs[i]);
			// System.out.println("End Graph Problem Solution.");
			// The solver should have returned the list of tracks that the
			// solution traverses.
			for (ISTTrackingTrajectory[] edges : trkSolutions) {
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
		});

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
