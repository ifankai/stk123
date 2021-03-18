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

import edu.gsu.cs.dmlab.imageproc.interfaces.IHistoComparator;

/**
 * Computes the Intersection between two histograms.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class IntersectionHistoComparator implements IHistoComparator {

	@Override
	public float compareHist(int[] hist1, int[] hist2) {
		float sum = 0;
		for (int i = 0; i < hist1.length; i++) {
			sum += Math.min(hist1[i], hist2[i]);
		}
		return sum;
	}

	@Override
	public float compareHists(int[][] hists1, int[][] hists2) {
		float sum = 0;

		for (int i = 0; i < hists1.length; i++) {
			int[] hist1Dim = hists1[i];
			int[] hist2Dim = hists2[i];
			for (int j = 0; j < hist1Dim.length; j++) {
				sum += Math.min(hist1Dim[j], hist2Dim[j]);
			}
		}
		return sum;
	}

}
