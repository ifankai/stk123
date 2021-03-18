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
package edu.gsu.cs.dmlab.imageproc.imageparam.util;

/**
 * This class is a standard insertion sort but it is designed to work on 2
 * arrays in parallel. It sorts the first array in descending order and
 * meanwhile rearrange the second array accordingly.
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public abstract class ParallelInsetionSort {

	/**
	 * 
	 * @param values
	 *            an array to be sorted in descending order
	 * @param indices
	 *            an array to be rearranged according to the changes that take place
	 *            in a.
	 * 
	 */
	public static void sortTogether(Double[] values, Integer[] indices) {

		double tempA;
		int tempB;

		// Start sorting a[]
		for (int i = 1; i < values.length; i++) {
			for (int j = i; j > 0; j--) {
				if (values[j] > values[j - 1]) {
					tempA = values[j];
					values[j] = values[j - 1];
					values[j - 1] = tempA;

					// Rearrange b[] accordingly
					tempB = indices[j];
					indices[j] = indices[j - 1];
					indices[j - 1] = tempB;
				}
			}
		}
	}

}
