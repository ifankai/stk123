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
package edu.gsu.cs.dmlab.sparse.dictionary;

import smile.math.Math;
import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryUpdater;
import smile.math.matrix.DenseMatrix;

/**
 * Implements the block coordinate descent for dictionary update from Mairal et
 * al., 2010. The algorithm can be found in "Sparse Modeling, Theory,
 * Algorithms, and Applications" by Irina Rish and Genady Ya. Grabarnik.
 * Published by CRC Press 2015, on page 173. The block-coordinate descent
 * general algorithm can be found on p 154 of the same.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class BlockCoordinateDescentDictionaryUpdater implements IDictionaryUpdater {

	private double epsilon = 1e-15;
	private int maxIterations = 35;

	/**
	 * Constructor with the error stopping point (epsilon) passed in.
	 * 
	 * @param epsilon
	 *            The acceptable difference in the norm between updates. A proxy
	 *            for error after update. This really needs to be around 1e-4 or
	 *            lower to do much.
	 */
	public BlockCoordinateDescentDictionaryUpdater(double epsilon) {
		if (epsilon <= 0)
			throw new IllegalArgumentException("Error threshold epsilon cannot be zero or smaller.");
		this.epsilon = epsilon;
	}

	@Override
	public void updateDictionary(DenseMatrix dictionary, DenseMatrix auxMatrixU, DenseMatrix auxMatrixV)
			throws MatrixDimensionMismatch, VectorDimensionMismatch {

		// test input for correct format.
		if (dictionary.nrows() != auxMatrixV.nrows())
			throw new MatrixDimensionMismatch("Dictionary Rows do not match Aux Matrix V rows");
		if (dictionary.ncols() != auxMatrixU.ncols() || dictionary.ncols() != auxMatrixU.nrows())
			throw new MatrixDimensionMismatch("Dictionary Columns do not match Aux Matrix U");

		int numDictElem = dictionary.ncols();
		double previousNormOfDict = dictionary.normFro();
		double thisNormOfDict = previousNormOfDict;

		double[] auxMatrixUCol_j = new double[auxMatrixU.nrows()];
		double[] auxMatrixVCol_j = new double[auxMatrixV.nrows()];
		double[] multResultVect = new double[auxMatrixVCol_j.length];
		double[] dictionaryCol_j = new double[dictionary.nrows()];

		int iterCount = 0;
		do {
			previousNormOfDict = thisNormOfDict;
			for (int j = 0; j < numDictElem; j++) {
				if (auxMatrixU.get(j, j) > 1e-6) {
					// System.out.println("NotZero");
					// u_j
					this.getCol(auxMatrixU, auxMatrixUCol_j, j);
					// v_j
					this.getCol(auxMatrixV, auxMatrixVCol_j, j);

					// multResultVect = Du_j
					dictionary.ax(auxMatrixUCol_j, multResultVect);
					// Math.ax(dictionaryRaw, auxMatrixUCol_j, multResultVect);

					// tmp = v_j - Du_j
					for (int k = 0; k < multResultVect.length; k++)
						multResultVect[k] = auxMatrixVCol_j[k] - multResultVect[k];

					// tmp = 1/u_jj(v_j-Du_j)
					Math.scale(1 / auxMatrixU.get(j, j), multResultVect);

					// d_j
					this.getCol(dictionary, dictionaryCol_j, j);

					// tmp = (1/U_jj)(v_j-Du_j)+d_j
					for (int k = 0; k < dictionaryCol_j.length; k++)
						multResultVect[k] += dictionaryCol_j[k];

					// d_j = (1/(max(||z_j||_2, 1)))*z_j
					Math.scale((1.0 / Math.max(Math.norm2(multResultVect), 1)), multResultVect);
					for (int i = 0; i < multResultVect.length; i++) {
						dictionary.set(i, j, multResultVect[i]);

					}
				}

			}

			thisNormOfDict = dictionary.normFro();
		} while ((++iterCount < this.maxIterations) && Math.abs(thisNormOfDict - previousNormOfDict) > this.epsilon);

		if (iterCount > 3)
			System.out.println("Iterations: " + iterCount);
		
		auxMatrixUCol_j = null;
		auxMatrixVCol_j = null;
		multResultVect = null;
		dictionaryCol_j = null;
	}

	private void getCol(DenseMatrix A, double[] vect, int j) {
		for (int i = 0; i < A.nrows(); i++) {
			vect[i] = A.get(i, j);
		}
	}

}
