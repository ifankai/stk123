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

import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IPooledCoefProducer {
	/**
	 * Calculates the coefficients on the vectorized image and the pools them. The
	 * first vector represents the final pooled vector, the following vectors should
	 * represent the layer just under the final pooled layer.
	 * 
	 * @param vectorizedImg
	 *            The vectorized image to calculate the coefficients on and then
	 *            pool.
	 * @return An array of pooled vectors.
	 * @throws VectorDimensionMismatch
	 *             If there is a mismatch between the image and the dictionary
	 *             within the pooling coef producer.
	 */
	public SparseMatrix[] getPooledCoefficients(DenseMatrix vectorizedImg) throws VectorDimensionMismatch;
}
