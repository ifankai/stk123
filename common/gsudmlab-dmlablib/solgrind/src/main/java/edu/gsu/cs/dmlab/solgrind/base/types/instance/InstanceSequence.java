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

import java.util.ArrayList;
import java.util.List;

import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.event.EventSequence;

/**
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class InstanceSequence {

	protected List<InstanceData> sequenceData;
	private double chainIndex;
	private String chainIndexType;

	public InstanceSequence() {
		sequenceData = new ArrayList<InstanceData>();
	}

	public InstanceSequence(InstanceData data) {
		sequenceData = new ArrayList<InstanceData>();
		sequenceData.add(data);
	}

	public InstanceSequence(List<InstanceData> data) {
		sequenceData = new ArrayList<InstanceData>(data);
	}

	public static InstanceSequence createInstanceSequence(List<Instance> instances) {

		// soft check: for start times of the instances
		for (int i = 0; i < instances.size() - 1; i++) {
			if (instances.get(i).getStartTime().isAfter(instances.get(i + 1).getStartTime())) {
				return null;
			}
		} // returns null if it can't pass

		InstanceSequence sequence = new InstanceSequence();
		for (Instance inst : instances) {
			sequence.append(InstanceData.createInstanceData(inst));
		}
		return sequence;
	}

	public EventSequence getEventSequenceType() {
		EventSequence esq = new EventSequence();
		for (int i = 0; i < this.getLength(); i++) {
			esq.insert(this.getSequenceData().get(i).type);
		}
		return esq;
	}

	public void append(InstanceData instanceData) {
		sequenceData.add(instanceData);
	}

	public InstanceSequence appendAndCreate(InstanceData iData) {
		List<InstanceData> list = new ArrayList<>(this.getSequenceData());
		list.add(iData);
		return new InstanceSequence(list);
	}

	public InstanceData getLast() {
		if (sequenceData == null || sequenceData.size() == 0) {
			return null;
		}
		return sequenceData.get(this.getLength() - 1);
	}

	public void setChainIndex(double ci) {
		chainIndex = ci;
	}

	public double getChainIndex() {
		return chainIndex;
	}

	public int getLength() {
		if (sequenceData == null) {
			return 0;
		}
		return sequenceData.size();
	}

	public void setChainIndexType(String ciType) {

		if (ciType.equals("J") || ciType.equals("J*") || ciType.equals("OMAX")) {
			chainIndexType = ciType;
		} else {
			chainIndexType = "N/A";
		}

	}

	public String getChainIndexType() {
		return chainIndexType;
	}

	/**
	 * Returns an InstanceSequence of the portion of this InstanceSequence between
	 * the specified fromIndex (from), inclusive, and toIndex (to), exclusive. (If
	 * fromIndex and toIndex are equal, the returned list is empty.)
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public InstanceSequence getSubsequence(int from, int to) {
		ArrayList<InstanceData> subsequenceList = new ArrayList<>(this.sequenceData.subList(from, to));
		return new InstanceSequence(subsequenceList);
	}

	public List<InstanceData> getSequenceData() {
		return sequenceData;
	}

	public boolean equals(InstanceSequence other) {

		if (this.getLength() != other.getLength()) {
			return false;
		} else {
			for (int i = 0; i < this.getLength(); i++) {
				if (!this.sequenceData.get(i).equals(other.sequenceData.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	public String toString() {

		return this.getSequenceData().toString();

	}

	public int hashCode() {
		int hash = 0;
		for (InstanceData ins : sequenceData) {
			hash += ins.hashCode();
		}
		return hash;
	}

}
