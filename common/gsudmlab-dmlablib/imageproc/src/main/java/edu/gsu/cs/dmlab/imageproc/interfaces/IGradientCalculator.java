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

import edu.gsu.cs.dmlab.datatypes.Gradient;

/**
 * The interface for classes that calculate the gradient of pixel intensities on
 * source images.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IGradientCalculator {

	/**
	 * Calculates the gradient of pixel intensities on the input image and returns
	 * the results in the original Cartesian coordinate system.
	 * 
	 * @param input
	 *            The input image in the form of a 2D array of doubles
	 * @return The gradient in x and y direction
	 */
	public Gradient calculateGradientCart(double[][] input);

	/**
	 * Calculates the gradient of pixel intensities on the input image and returns
	 * the results in a Polar coordinate system
	 * 
	 * @param input
	 *            The input image in the form of a 2D array of doubles
	 * @return The gradient as gx=theta and gy=r <br>
	 * 
	 *         The range of theta is (-3.14, +3.14)
	 */
	public Gradient calculateGradientPolar(double[][] input);
}