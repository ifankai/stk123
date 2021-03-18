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
 * Interface for objects used to normalize the passed in parameter arrays.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IImgParamNormalizer {
	/**
	 * Normalizes the parameter values to have a range between 0..1 inclusive,
	 * based upon the range of values expected for the particular parameter and
	 * not the values seen in the given input.
	 * 
	 * @param parameters
	 *            The input to normalize.
	 */
	public void normalizeParameterValues(double[][][] parameters);
}
