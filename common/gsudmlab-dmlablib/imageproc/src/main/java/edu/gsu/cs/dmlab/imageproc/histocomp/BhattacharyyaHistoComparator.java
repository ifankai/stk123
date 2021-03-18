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
 * Computes the Bhattacharyya distance between two histograms.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class BhattacharyyaHistoComparator extends BaseHistoComparator implements IHistoComparator {

	@Override
	public float compareHist(int[] hist1, int[] hist2) {
		int[] rawData1 = hist1;
		int[] rawData2 = hist2;

		float mean1 = this.getMean(rawData1);
		float mean2 = this.getMean(rawData2);
		float nSquare = (float) Math.pow(rawData1.length + rawData2.length, 2);

		float sumOfMultRoot = 0;
		for (int i = 0; i < rawData1.length; i++) {
			sumOfMultRoot += (float) Math.sqrt(rawData1[i] * rawData2[i]);
		}

		float ans = (float) Math.sqrt(1.0 - (1.0 / Math.sqrt(mean1 * mean2 * nSquare)) * sumOfMultRoot);
		return ans;
	}

	@Override
	public float compareHists(int[][] hists1, int[][] hists2) {
		float mean1 = this.getMean(hists1);
		float mean2 = this.getMean(hists2);
		float n = 0;
		float sumOfMultRoot = 0;
		for (int j = 0; j < hists1.length; j++) {
			int[] rawData1 = hists1[j];
			int[] rawData2 = hists2[j];

			n += (float) (rawData1.length + rawData2.length);

			for (int i = 0; i < rawData1.length; i++) {
				sumOfMultRoot += (float) Math.sqrt(rawData1[i] * rawData2[i]);
			}
		}

		float nSquare = (float) Math.pow(n, 2);
		float ans = (float) Math.sqrt(1.0 - (1.0 / Math.sqrt(mean1 * mean2 * nSquare)) * sumOfMultRoot);
		return ans;
	}

}
