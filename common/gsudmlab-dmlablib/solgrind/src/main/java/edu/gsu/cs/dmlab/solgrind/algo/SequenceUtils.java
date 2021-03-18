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
package edu.gsu.cs.dmlab.solgrind.algo;

import org.joda.time.DateTime;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import edu.gsu.cs.dmlab.solgrind.SolgrindConstants;
import edu.gsu.cs.dmlab.solgrind.algo.measures.significance.JStar;
import edu.gsu.cs.dmlab.solgrind.algo.measures.significance.Jaccard;
import edu.gsu.cs.dmlab.solgrind.algo.measures.significance.Omax;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.operations.STOperations;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceData;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceSequence;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.TwoSequence;

import java.util.*;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class SequenceUtils {

	/**
	 * Generates head segment using head interval strategy Gets the trajectory
	 * segment that covers initial 'headInterval' time interval.
	 * 
	 * @param ins          - instance whose head is to be generated
	 * @param headInterval - head interval parameter
	 * @return head segment of the instance which is a Trajectory object
	 */
	public static Instance generateHead(Instance ins, long headInterval) {

		Trajectory instanceTrajectory = ins.getTrajectory();
		DateTime startTime = ins.getStartTime();
		DateTime headEndTime = startTime.plusMillis((int) headInterval);
		Instance head = new Instance(ins.getId(), ins.getType(), instanceTrajectory.getSegment(startTime, headEndTime));

		return head;

	}

	/**
	 * Generates head segment using head ratio strategy Gets the trajectory segment
	 * that covers initial 'headRatio' amount of trajectory's lifespan.
	 * 
	 * @param ins
	 * @param headRatio
	 * @return
	 */
	public static Instance generateHeadwithRatio(Instance ins, double headRatio) {

		Trajectory instanceTrajectory = ins.getTrajectory();
		DateTime startTime = ins.getStartTime();

		int size = instanceTrajectory.getTGPairSize();
		int ceiledSize = (int) Math.ceil(headRatio * size);
		DateTime headEndTime = startTime.plusMillis((int) (ceiledSize * SolgrindConstants.SAMPLING_INTERVAL));

		Instance headInstance = new Instance(ins.getId(), ins.getType(),
				instanceTrajectory.getSegment(startTime, headEndTime));

		return headInstance;

	}

	/**
	 * Generates the tail segment using tail interval strategy Gets the trajectory
	 * segment that covers last 'tailInterval' time interval.
	 * 
	 * @param ins          - instance whose head is to be generated
	 * @param tailInterval - tail interval parameter
	 * @return tail segment of the trajectory
	 */
	public static Trajectory generateTail(Instance ins, long tailInterval) {
		Trajectory instanceTrajectory = ins.getTrajectory();
		DateTime endTime = ins.getStartTime();
		DateTime tailStartTime = endTime.minusMillis((int) tailInterval);

		return instanceTrajectory.getSegment(tailStartTime, endTime);
	}

	/**
	 * Generates tail segment using tail ratio strategy Gets the trajectory segment
	 * that covers last 'tailRatio' amount of trajectory's lifespan.
	 * 
	 * @param ins
	 * @param tailRatio
	 * @return
	 */
	public static Trajectory generateTail(Instance ins, float tailRatio) {
		Trajectory instanceTrajectory = ins.getTrajectory();
		DateTime endTime = ins.getStartTime();
		long lifespan = ins.getEndTime().getMillis() - ins.getStartTime().getMillis();
		DateTime tailStartTime = endTime.minusMillis((int) (lifespan * tailRatio));

		return instanceTrajectory.getSegment(tailStartTime, endTime);
	}

	/**
	 * Generates tail buffer from a given tail segment of the trajectory using
	 * bufferDistance parameter. Simply applies a spatial buffer to all geometries
	 * 
	 * @param tail           - tail trajectory segment
	 * @param bufferDistance - buffer distance parameter
	 * @return
	 */
	public static Trajectory generateTailBuffer(Trajectory tail, double bufferDistance) {
		Trajectory tailBuffer = new Trajectory();
		for (TGPair tgp : tail.getTGPairs()) {
			Geometry bufferedGeom = tgp.getGeometry().buffer(bufferDistance);
			tailBuffer.addTGPair(tgp.getTInterval().getStart(), tgp.getTInterval().getEnd(), bufferedGeom);

		}
		return tailBuffer;
	}

	/**
	 * Generates tail window using the buffered tail trajectory by propagating it
	 * 
	 * @param tailBuffer       - buffered tail trajectory
	 * @param tailValidity     - the period that will be used to propagate the
	 *                         tgpairs in trajectory
	 * @param samplingInterval - the sampling interval for creating a better tail
	 *                         window
	 * @return - tail window trajectory
	 */
	public static Trajectory generateTailWindow(Trajectory tailBuffer, long tailValidity, long samplingInterval) {

		Trajectory sampledTrajectory = SequenceUtils.sampleTGPairs(tailBuffer, samplingInterval);
		Trajectory tailWindow = new Trajectory();

		TGPair[] tgpairArray = (TGPair[]) sampledTrajectory.getTGPairs().toArray();
		for (int i = 0; i < tgpairArray.length; i++) {

			// get time-geometry pair
			TGPair tgp = tgpairArray[i];
			long startTime = tgp.getTInterval().getEndMillis();
			long validityEndTime = startTime + tailValidity;

			// propagate the geometry for tail validity interval
			Trajectory propagated = new Trajectory();
			propagated.addTGPair(startTime, validityEndTime, tgp.getGeometry());

			// merge the propagated window of an individual time-geometry pair
			tailWindow = STOperations.union(tailWindow, propagated);

		}

		return tailWindow;
	}

	/**
	 * Based on a sampling interval, sample the time-geometry pairs in the
	 * trajectory
	 * 
	 * @param traj
	 * @param samplingInterval
	 * @return
	 */
	public static Trajectory sampleTGPairs(Trajectory traj, long samplingInterval) {

		if (samplingInterval == -1L) {
			return traj;
		}

		Trajectory sampledTraj = new Trajectory();

		TGPair[] tgpArray = (TGPair[]) traj.getTGPairs().toArray();
		for (int i = 0; i < traj.getTGPairs().size(); i++) {
			TGPair tgp = tgpArray[i];

			long tgpStartTime = tgp.getTInterval().getStartMillis();
			long tgpEndTime = tgp.getTInterval().getEndMillis();
			long lifespan = tgpEndTime - tgpStartTime;

			long sampledTgpStart = tgpStartTime;
			long sampledTgpEnd = tgpStartTime + samplingInterval;

			while (sampledTgpEnd <= tgpEndTime) {
				// start from first time interval start and keep iterating with sampling
				// interval
				Geometry sampledGeom = null;
				double samplingRatio = (double) sampledTgpStart / (double) lifespan;

				if (i != tgpArray.length) {
					sampledGeom = STOperations.interpolate(tgp.getGeometry(), tgpArray[i + 1].getGeometry(),
							samplingRatio);
				} else {
					sampledGeom = STOperations.interpolate(tgp.getGeometry(), sampledTgpStart - tgpStartTime);
				}

				sampledTraj.addTGPair(sampledTgpStart, sampledTgpEnd, sampledGeom);

				sampledTgpStart += samplingInterval;
				sampledTgpEnd += samplingInterval;

				if (sampledTgpStart < tgpEndTime && sampledTgpEnd > tgpEndTime) {
					sampledTgpEnd = tgpEndTime;
				}
			}
		}

		return sampledTraj;
	}

	public static Instance generateTailWindowWithRatio(Instance instance, double tailRatio, double bufferDistance,
			long tailValidityCount) {

		int size = instance.getTrajectory().getTGPairSize();
		int ceiledSize = (int) Math.ceil(tailRatio * size);
		long tailInterval = ceiledSize * SolgrindConstants.SAMPLING_INTERVAL;
		try {
			return generateTailWindow(instance, tailInterval, bufferDistance, tailValidityCount);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Instance generateTailWindow(Instance instance, long tailInterval, double bufferDistance,
			long tailValidityCount) throws ParseException {

		Trajectory trajectory = instance.getTrajectory();

		Trajectory tailSegment = trajectory.getSegment(new DateTime(trajectory.getEndTime().getMillis() - tailInterval),
				trajectory.getEndTime());

		for (TGPair tgPair : tailSegment.getTGPairs()) {
			tgPair.setGeometry(tgPair.getGeometry().buffer(bufferDistance));
		}

		List<TGPair> tailSegmentList = new ArrayList<>(tailSegment.getTGPairs());
		new GeometryFactory().createGeometry(null);
		long startTime = tailSegment.getTGPairs().last().getTInterval().getStartMillis();
		for (int i = 0; i < tailValidityCount; i++) {
			Geometry emptyGeom = new WKTReader().read("POLYGON EMPTY");
			startTime += SolgrindConstants.SAMPLING_INTERVAL;
			TGPair tgPair = new TGPair(startTime, startTime + SolgrindConstants.SAMPLING_INTERVAL, emptyGeom);
			tailSegmentList.add(tgPair);
		}

		Trajectory tailWindow = new Trajectory();
		for (int i = tailSegmentList.size() - 1; i >= 0; i--) {
			TGPair tgPair = tailSegmentList.get(i);
			TGPair tgPairOnWindow;
			for (int k = i; k >= i - tailValidityCount; k--) {
				if (k < 0)
					break;
				tgPairOnWindow = tailSegmentList.get(k);
				tgPair.setGeometry(tgPairOnWindow.getGeometry().union(tgPair.getGeometry()));
			}

			tailWindow.addTGPair(tgPair);
		}

		return new Instance(instance.getId(), instance.getType(), tailWindow);
	}

	/**
	 * Generates tail window from an instance
	 * 
	 * @param ins
	 * @param tailInterval
	 * @param bufferDistance
	 * @param tailValidity
	 * @param samplingInterval
	 * @return
	 */
	public static Trajectory createTailWindow(Instance ins, long tailInterval, double bufferDistance, long tailValidity,
			long samplingInterval) {

		return generateTailWindow(generateTailBuffer(generateTail(ins, tailInterval), bufferDistance), tailValidity,
				samplingInterval);

	}

	/**
	 * Generates tail window from an instance
	 * 
	 * @param ins
	 * @param tailRatio
	 * @param bufferDistance
	 * @param tailValidity
	 * @param samplingInterval
	 * @return
	 */
	public static Trajectory createTailWindow(Instance ins, float tailRatio, double bufferDistance, long tailValidity,
			long samplingInterval) {

		return generateTailWindow(generateTailBuffer(generateTail(ins, tailRatio), bufferDistance), tailValidity,
				samplingInterval);

	}

	/**
	 * Given two instance trajectories in form of InstanceData objects and
	 * trajectories (head and tailWindow), method returns an TwoSequence
	 * (2-sequence) object, which describes a follow relationship
	 * 
	 * @param headData - the data for head
	 * @param head     - head segment
	 * @param tailData - the data for tail window
	 * @param tailW    - tail window segment
	 * @param ciType   - chain index type
	 * @return
	 */
	public static TwoSequence discoverFollowRelationship(InstanceData headData, Trajectory head, InstanceData tailData,
			Trajectory tailW, String ciType) {
		double measureValue = 0.0;
		if (ciType.equals("J")) {
			measureValue = new Jaccard().calculateT(head, tailW);
		} else if (ciType.equals("J*")) {
			measureValue = new JStar().calculateT(head, tailW);
		} else if (ciType.equals("OMAX")) {
			measureValue = new Omax().calculateT(head, tailW);
		} else {
			return null;
			// don't calculate the thing
		}

		ArrayList<InstanceData> instances = new ArrayList<InstanceData>();
		instances.add(tailData);
		instances.add(headData);

		TwoSequence iSequence = new TwoSequence(instances); // st follow is
															// essentially a two
															// sequence
		iSequence.setChainIndex(measureValue);
		iSequence.setChainIndexType(ciType);
		return iSequence;

	}

	/**
	 * Gets a set of instance sequences (InstanceSequence objects), and connects
	 * them based on the equality of prefix and suffix (of possible followee and
	 * follower instance sequences)
	 * 
	 * @param instanceSequences - set of instance sequences to be examined
	 * @param desiredLength     - the length of resulting instance sequences
	 * @return
	 */
	public static Set<InstanceSequence> instanceSequenceConnector(HashSet<InstanceSequence> instanceSequences,
			int desiredLength) {
		TreeSet<InstanceSequence> connectedISequences = new TreeSet<>();
		if (instanceSequences == null || instanceSequences.size() == 0) {
			System.out.println("Instance sequences set is null or empty");
			return null;
		}
		int length = instanceSequences.iterator().next().getLength();
		if (length == 0) {
			System.out.println("The instance sequence is invalid (empty)");
			return null;
		}
		for (InstanceSequence isq : instanceSequences) {
			if (length != isq.getLength()) {
				System.out.println("The instance sequence lengths are not the same");
				return null;
			}
		}

		if (desiredLength > 2 * length - 1 || desiredLength <= length) {
			System.out.println("Desired length of the sequences are inappropriate");
			return null;
		}

		int offset = 2 * length - desiredLength;
		// now we checked the pre-conditions.
		for (InstanceSequence isq : instanceSequences) {

			InstanceSequence subsequence1 = isq.getSubsequence(length - offset, length); // get
																							// last
																							// 'offset'-length
																							// subsequence
			for (InstanceSequence isqFollower : instanceSequences) {
				InstanceSequence subsequence2 = isqFollower.getSubsequence(0, offset); // get
																						// first
																						// 'offset'-length
																						// subsequence
				if (subsequence1.equals(subsequence2)) {
					connectedISequences.add(mergeSequence(isq, isqFollower, offset));
				}
			}
		}
		return connectedISequences;
	}

	private static InstanceSequence mergeSequence(InstanceSequence isq, InstanceSequence isqFollower, int offset) {
		InstanceSequence mergedSequence = new InstanceSequence(isq.getSequenceData());
		InstanceSequence followerSuffix = isqFollower.getSubsequence(offset, isqFollower.getLength());
		for (InstanceData ins : followerSuffix.getSequenceData()) {
			mergedSequence.append(ins);
		}
		return mergedSequence;
	}

}
