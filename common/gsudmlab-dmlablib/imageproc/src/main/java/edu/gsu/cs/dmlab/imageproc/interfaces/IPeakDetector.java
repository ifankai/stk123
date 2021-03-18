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

import java.util.List;

/**
 * Interface for classes that find local maxima in arrays
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IPeakDetector {

	/**
	 * Method for finding peaks, or local mixima, in the input array.
	 * 
	 * @param data
	 *            The input array to find local maxima in.
	 * @return List of index locations of the found local maxima. The list may or
	 *         may not be sorted by the height of the peaks, depending on the method
	 *         implements this interface.
	 */
	public List<Integer> findPeaks(double[] data);
}
