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
package edu.gsu.cs.dmlab.imageproc.interfaces;

/**
 * The interface for classes that compare histograms.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IHistoComparator {
	/**
	 * Compares two single dimensional histograms and returns the value computed by
	 * their comparison.
	 * 
	 * @param hist1
	 *            The first histogram in the comparison.
	 * @param hist2
	 *            The second histogram in the comparison.
	 * @return The value computed by the comparison of the two input histograms.
	 */
	public float compareHist(int[] hist1, int[] hist2);

	/**
	 * Compares two multi-dimensional histograms and returns the value computed by
	 * their comparison.
	 * 
	 * @param hists1
	 *            The first histogram in the comparison.
	 * @param hists2
	 *            The second histogram in the comparison.
	 * @return The value computed by the comparison of the two input histograms.
	 */
	public float compareHists(int[][] hists1, int[][] hists2);
}
