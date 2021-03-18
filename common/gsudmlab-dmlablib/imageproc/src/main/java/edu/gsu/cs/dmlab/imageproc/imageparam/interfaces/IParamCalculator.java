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
package edu.gsu.cs.dmlab.imageproc.imageparam.interfaces;

public interface IParamCalculator {

	/**
	 * This polymorphic method is designed to compute any of the 10 parameters
	 * at a time. It iterates over the given image, patch by patch, and then in
	 * every patch, pixel by pixel, to compute the parameter value for each
	 * patch.<br>
	 * 
	 * <b>Note:</b> In all the classes, matrices are read and write, row by row.
	 * <br>
	 * <b>Note:</b> It is required that each class works independently, meaning
	 * that they must rely on their own computations. Therefore, some
	 * calculations (e.g. mean intensity value) might be calculated several
	 * times for different parameters (e.g. once for <i>skewness</i> and another
	 * time for <i>std. deviation</i>).
	 * <br>
	 * <b>Note:</b> No fixed range for the color intensity values of the given
	 * images is assumed. (colors do not have to be within the range [0,255].)
	 * 
	 *
	 * @param bImage
	 *            a 2D array representing the input image for which the parameter
	 *            should be computed.
	 * @return a 2D matrix whose each entry corresponds to the calculated
	 *         parameter for one particular patch of the image.
	 */
	public double[][] calculateParameter(double[][] bImage);
	

}