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
package edu.gsu.cs.dmlab.solgrind.base.types.instance;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.Interval;

/**
 * This class implements utility operations and necessary operations for pattern
 * instances in STCOP mining
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class InstanceCooccurrence {
	public static final double INVALID_CCE = Double.MIN_VALUE;
	private Set<InstanceData> cooccurrenceData;
	private double cce; /* Co-occurrence coefficient */
	private String cceType;

	private Trajectory iTraj = null;
	private Trajectory uTraj = null;
	private SortedSet<Interval> xcoTI = null;

	public InstanceCooccurrence() {
		cooccurrenceData = new HashSet<InstanceData>();
		cce = INVALID_CCE;
	}

	public InstanceCooccurrence(InstanceData data) {
		cooccurrenceData = new HashSet<InstanceData>();
		cooccurrenceData.add(data);
	}

	public InstanceCooccurrence(List<InstanceData> data) {
		cooccurrenceData = new HashSet<InstanceData>(data);
	}

	public static InstanceCooccurrence createInstanceCooccurrence(Collection<Instance> instances) {

		InstanceCooccurrence coOccurrence = new InstanceCooccurrence();
		for (Instance inst : instances) {
			coOccurrence.append(InstanceData.createInstanceData(inst));
		}
		return coOccurrence;
	}

	public void append(InstanceData instanceData) {
		cooccurrenceData.add(instanceData);
	}

	public Set<InstanceData> getInstanceData() {
		return cooccurrenceData;
	}

	public String getInstanceIdOfType(EventType et) {
		for (InstanceData idata : cooccurrenceData) {
			if (idata.type.equals(et)) {
				return idata.id;
			}
		}
		return null;
	}

	public double getCce() {
		return cce;
	}

	public void setCce(double cce) {
		this.cce = cce;
	}

	public String getcceType() {
		return cceType;
	}

	public void setcceType(String cceType) {
		if (cceType.equals("J") || cceType.equals("J*") || cceType.equals("OMAX")) {
			this.cceType = cceType;
		} else {
			cceType = "N/A";
		}
	}

	public Trajectory getIntersectionTrajectory() {
		return iTraj;
	}

	public void setIntersectionTrajectory(Trajectory iTraj) {
		this.iTraj = iTraj;
	}

	public Trajectory getUnionTrajectory() {
		return uTraj;
	}

	public void setUnionTrajectory(Trajectory uTraj) {
		this.uTraj = uTraj;
	}

	public SortedSet<Interval> getXcoTimeIntervals() {
		return xcoTI;
	}

	public void setXcoTimeIntervals(SortedSet<Interval> xcoTI) {
		this.xcoTI = xcoTI;
	}

	public TreeSet<Interval> mergeXcoTimeIntervals(InstanceCooccurrence ic1, InstanceCooccurrence ic2) {
		return null; // TODO implement this
	}

	public boolean isCceValid() {
		return (cce != InstanceCooccurrence.INVALID_CCE) && (cce <= 1.0) && (cce >= 0.0);
	}

	public String toString() {
		return this.cooccurrenceData.toString() + "\tCCE: " + this.getCce();
	}

}
