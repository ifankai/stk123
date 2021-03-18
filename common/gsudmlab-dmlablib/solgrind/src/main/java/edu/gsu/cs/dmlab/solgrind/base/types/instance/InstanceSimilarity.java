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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.gsu.cs.dmlab.solgrind.base.Instance;

/**
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */

public class InstanceSimilarity {

	private Set<InstanceData> similarityData;
	private double similarityIndex;
	private String similarityIndexType;

	public InstanceSimilarity() {
		similarityData = new HashSet<InstanceData>();
	}

	public InstanceSimilarity(InstanceData data) {
		similarityData = new HashSet<InstanceData>();
		similarityData.add(data);
	}

	public InstanceSimilarity(List<InstanceData> data) {
		similarityData = new HashSet<InstanceData>(data);
	}

	public static InstanceSimilarity createInstanceSequence(Collection<Instance> instances) {
		InstanceSimilarity similarity = new InstanceSimilarity();
		for (Instance inst : instances) {
			similarity.append(InstanceData.createInstanceData(inst));
		}
		return similarity;
	}

	private void append(InstanceData instanceData) {
		similarityData.add(instanceData);
	}

	public void setSimilarityIndex(double si) {
		similarityIndex = si;
	}

	public double getSimilarityIndex() {
		return similarityIndex;
	}

	public void setSimilarityIndexType(String siType) {
		if (siType.equals("Edit") || siType.equals("Euclidean") || siType.equals("DTW")) {
			similarityIndexType = siType;
		} else {
			similarityIndexType = "N/A";
		}
	}

	public String getSimilarityIndexType() {
		return similarityIndexType;
	}

}
