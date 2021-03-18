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

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import smile.math.matrix.DenseMatrix;

/**
 * Interface for objects that do a sparse vecor approximation such that it
 * computes x, such that b = Ax using some method
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISparseVectorApproximator {

	/**
	 * Computes x, such that b = Ax using some method.
	 * 
	 * @param b vector on LHS of equation
	 * 
	 * @param A Matrix A to use in the solution
	 * 
	 * @param x vector to compute and return
	 * 
	 * @throws VectorDimensionMismatch If there is some sort of internal error.
	 * 
	 * @throws MatrixDimensionMismatch When there is an error
	 */
	public void solve(double[] b, DenseMatrix A, double[] x) throws VectorDimensionMismatch, MatrixDimensionMismatch;

	/**
	 * Computes x, such that b = Ax using some method.
	 * 
	 * @param b   vector on LHS of equation
	 * 
	 * @param A   Matrix A to use in the solution
	 * 
	 * @param AtA Matrix AtA to use in the solution, is the Gram matrix of A.
	 * 
	 * @param x   vector to compute and return
	 * 
	 * @throws VectorDimensionMismatch If there is some sort of internal error.
	 * 
	 * @throws MatrixDimensionMismatch When there is an error
	 */
	public void solve(double[] b, DenseMatrix A, DenseMatrix AtA, double[] x)
			throws VectorDimensionMismatch, MatrixDimensionMismatch;
}
