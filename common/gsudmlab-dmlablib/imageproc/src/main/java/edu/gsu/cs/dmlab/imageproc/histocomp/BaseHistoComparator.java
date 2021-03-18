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
package edu.gsu.cs.dmlab.imageproc.histocomp;

/**
 * Base class used by multiple histogram comparison methods. This class
 * implements some of the shared function between many of the histogram
 * comparison methods.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public abstract class BaseHistoComparator {

	/**
	 * Computes the mean value of the passed in dense vector of integer values.
	 * 
	 * @param dataSet
	 *            The vector to compute the mean of.
	 * @return The mean of the passed in vector.
	 */
	float getMean(int[][] dataSet) {
		float sum = 0;
		int count = 0;
		for (int i = 0; i < dataSet.length; i++) {
			int[] data = dataSet[i];
			sum += this.getSum(data);
			count += data.length;
		}

		return sum / count;
	}

	/**
	 * Computes the mean of the passed in array of integers.
	 * 
	 * @param data
	 *            The array to compute the mean of.
	 * @return The mean of the passed in array.
	 */
	float getMean(int[] data) {
		float sum = this.getSum(data);
		return sum / data.length;
	}

	/**
	 * Computes the sum of the passed in array of integers.
	 * 
	 * @param data
	 *            The array to compute the sum of.
	 * @return The sum of the passed in array.
	 */
	float getSum(int[] data) {
		float sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return sum;
	}
}
