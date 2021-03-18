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
package edu.gsu.cs.dmlab.math;

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * This class provides an easy access location for the Basic Linear Algebra
 * Subprograms. Make calls to these functions instead of to the compute
 * functions in the implementing classes, that way we can easily change them at
 * a later date without having to change code that relies on the subprograms.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class LAFunctions {

	/**
	 * Performs matrix matrix multiplication and returns the result of A*B.
	 * 
	 * @param A
	 *            Input matrix
	 * @param B
	 *            Input matrix
	 * @return The results of the matrix matrix multiplication.
	 * @throws MatrixDimensionMismatch
	 *             Thrown when the dimensions do not match in shuch a way that
	 *             the operation can be performed.
	 */
	public static DenseMatrix abmm(DenseMatrix A, SparseMatrix B) throws MatrixDimensionMismatch {
		if (A.ncols() != B.nrows()) {
			throw new MatrixDimensionMismatch(String.format("Matrix multiplication A * B: %d x %d vs %d x %d",
					A.nrows(), A.ncols(), B.nrows(), B.ncols()));
		}

		int m = A.nrows();
		int n = B.ncols();
		int l = B.nrows();

		double[][] result = new double[A.nrows()][B.ncols()];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				result[i][j] = 0.0;
				for (int k = 0; k < l; k++) {
					result[i][j] += A.get(i, k) * B.get(k, j);
				}
			}
		}
		return new JMatrix(result);
	}

}
