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
package edu.gsu.cs.dmlab.sparse.approximation;

import java.util.ArrayList;
import java.util.stream.IntStream;

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseMatrixApproximator;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class LARS_LassoSparseMatrixApproximator extends LARS_LassoCoeffVectorApproximator
		implements ISparseMatrixApproximator {

	/**
	 * Constructor
	 * 
	 * @param lambda
	 *            Depending on mode, lambda is used differently. If mode 1, then
	 *            lambda min amount of correlation between a coefficient in A and
	 *            signal b for the algorithm to proceed. If mode 2, max L1 norm of
	 *            the coefficient vector.
	 * @param mode
	 *            What mode to use PENALTY or L1COEFF. See lambda for differences.
	 */
	public LARS_LassoSparseMatrixApproximator(double lambda, LassoMode mode) {
		super(lambda, mode);
	}

	@Override
	public SparseMatrix estimateCoeffs(DenseMatrix signal, DenseMatrix D) {
		if (signal.nrows() != D.nrows())
			new MatrixDimensionMismatch("");

		ArrayList<double[]> results = new ArrayList<double[]>();
		double[] sigVect = new double[signal.nrows()];

		// Compute the Gramm Matrix D'*D on the dictionary.
		DenseMatrix Gm = D.ata();

		// Add to the diagonal to avoid divide by zero
		for (int i = 0; i < Gm.nrows(); i++)
			Gm.add(i, i, 1e-10);

		// ISparseVectorApproximator vApprox = new
		// LARS_LassoVectorApproximatorUnoptomized(this.lambda);
		for (int i = 0; i < signal.ncols(); i++) {
			// get the signal vector from the signal matrix
			this.getCol(signal, sigVect, i);

			// calculate the lasso problem and get the coefficients
			try {
				double[] sparseRep = new double[D.ncols()];
				this.solve(sigVect, D, Gm, sparseRep);
				results.add(sparseRep);
			} catch (Exception e) {
				double[] coeffs = new double[D.ncols()];
				results.add(coeffs);
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		return this.getSparseMatrixFromResults(results);
	}

	private void getCol(DenseMatrix A, double[] vect, int colNum) {
		for (int rowNum = 0; rowNum < A.nrows(); rowNum++) {
			vect[rowNum] = A.get(rowNum, colNum);
		}
	}

	private SparseMatrix getSparseMatrixFromResults(ArrayList<double[]> resultSet) {

		int cols = resultSet.size();
		double[][] resultMatArr = new double[resultSet.get(0).length][cols];
		IntStream.range(0, cols).forEach(colNum -> {
			double[] colArray = resultSet.get(colNum);
			for (int rowNum = 0; rowNum < colArray.length; rowNum++) {
				resultMatArr[rowNum][colNum] = colArray[rowNum];
			}
		});

		return new SparseMatrix(resultMatArr);
	}

}