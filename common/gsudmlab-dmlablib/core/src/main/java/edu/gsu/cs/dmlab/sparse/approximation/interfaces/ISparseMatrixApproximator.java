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
package edu.gsu.cs.dmlab.sparse.approximation.interfaces;

import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * IApproximator is the public interface for sparse approximation tools or
 * classes that approximately solve a system of equations finding a sparse
 * vector combination dictionary elements that reproduce the passed signal
 * elements.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISparseMatrixApproximator {
	/**
	 * Estimates the sparse coefficients of the input signal given the dictionary.
	 * 
	 * @param signal Matrix of input signals where each column is another m-dim
	 *               signal
	 * 
	 * @param D      Dictionary of n-dim components used to reconstruct each m-dim
	 *               signal
	 * 
	 * @return Matrix of sparse coefficients used to reconstruct the signals using
	 *         the dictionary
	 * 
	 * @throws VectorDimensionMismatch When there is a mismatch between the
	 *                                 dimensions of the signal matrix and the
	 *                                 Dictionary matrix
	 */
	public SparseMatrix estimateCoeffs(DenseMatrix signal, DenseMatrix D) throws VectorDimensionMismatch;
}
