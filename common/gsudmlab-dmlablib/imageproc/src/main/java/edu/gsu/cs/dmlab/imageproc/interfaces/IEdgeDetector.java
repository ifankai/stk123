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
 * The interface for classes that do edge detection on source images.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IEdgeDetector {

	/**
	 * This method extracts the edges of the given image by following the following
	 * three steps: 1) Compute gradient, 2) Perform Hysteresis, 3) Thresholding.
	 * 
	 * @param sourceImg
	 *            The source image whose edges are to be extracted.
	 * @param colors
	 *            An array of length two that specofies the background and edge
	 *            color of the finla results. The first cell carries the background
	 *            color intensity and its second cell carries the foreground color
	 *            intensity. (Examples: new double{0.0, 255.0})
	 * @return A 2D binary array representing the edges and the background.
	 * 
	 */
	public double[][] getEdges(double[][] sourceImg, double[] colors);
}