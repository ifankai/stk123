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
package edu.gsu.cs.dmlab.imageproc;

import java.util.Arrays;

import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.imageproc.interfaces.IPooledCoefProducer;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseMatrixApproximator;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ImgSparseMaxPoolPyramidProducer implements IPooledCoefProducer {

	// I just want to get this done and don't want to have to figure out
	// the algorithm for n layers with a m by m area. So, we will
	// assume that we have 64 by 64 area (the image params area), and
	// a window of 8 with a step size of 1. This will give 56 rows and 56 colums
	// in our sampling. We will also assume that we want 4 layers starting with
	// the regular size 56x56, then 14x14, then 3x3, and finally 1.

	ISparseMatrixApproximator coefExtractor;
	DenseMatrix dictionary;

	public ImgSparseMaxPoolPyramidProducer(ISparseMatrixApproximator coefExtractor, DenseMatrix dictionary) {

		if (coefExtractor == null)
			throw new IllegalArgumentException("Approximator cannot be null.");
		if (dictionary == null)
			throw new IllegalArgumentException("Dictionary cannot be null.");
		this.coefExtractor = coefExtractor;
		this.dictionary = dictionary;
	}

	@Override
	public void finalize() throws Throwable {
		this.coefExtractor = null;
		this.dictionary = null;
	}

	@Override
	public SparseMatrix[] getPooledCoefficients(DenseMatrix vectorizedImg) throws VectorDimensionMismatch {
		SparseMatrix alphaOrig = this.coefExtractor.estimateCoeffs(vectorizedImg, dictionary);

		// Get this thing into a manageable format for pooling windows of
		// values.
		// Just make a cube of the xy locations with the third being the vector
		// of values at that patch location.

		int size = 56;
		double[][][] origVals = new double[size][size][alphaOrig.nrows()];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				double[] pointVals = origVals[x][y];
				this.copyCol(alphaOrig, pointVals, (x * 56) + y);
			}
		}

		// Now let's pool down to 14x14 cells
		int size2 = 14;
		int windowSamples = 4;
		double[][][] intermediateVals1 = new double[size2][size2][alphaOrig.nrows()];
		for (int x = 0; x < size2; x++) {
			for (int y = 0; y < size2; y++) {
				double[] maxValsForWin = new double[alphaOrig.nrows()];
				Arrays.fill(maxValsForWin, 0);
				for (int xx = 0; xx < windowSamples; xx++) {
					for (int yy = 0; yy < windowSamples; yy++) {
						double[] currentCompVals = origVals[x * windowSamples + xx][y * windowSamples + yy];
						for (int i = 0; i < currentCompVals.length; i++) {
							if (Math.abs(maxValsForWin[i]) < Math.abs(currentCompVals[i]))
								maxValsForWin[i] = currentCompVals[i];
						}
					}
				}
				intermediateVals1[x][y] = maxValsForWin;
			}
		}

		// Now lets pool down to 3x3 cells
		int size3 = 3;
		windowSamples = 5;
		double[][][] intermediateVals2 = new double[size3][size3][alphaOrig.nrows()];
		for (int x = 0; x < size3; x++) {
			for (int y = 0; y < size3; y++) {
				double[] maxValsForWin = new double[alphaOrig.nrows()];
				Arrays.fill(maxValsForWin, 0);
				for (int xx = 0; xx < windowSamples; xx++) {
					for (int yy = 0; yy < windowSamples; yy++) {
						int xpos = x * windowSamples + xx;
						int ypos = y * windowSamples + yy;
						if (xpos < intermediateVals1.length && ypos < intermediateVals1[0].length) {
							double[] currentCompVals = intermediateVals1[xpos][ypos];
							for (int i = 0; i < currentCompVals.length; i++) {
								if (Math.abs(maxValsForWin[i]) < Math.abs(currentCompVals[i]))
									maxValsForWin[i] = currentCompVals[i];

							}
						}
					}
				}
				intermediateVals2[x][y] = maxValsForWin;
			}
		}

		// Put the vectors from the pooled level below the final top level into
		// the results array to return.
		SparseMatrix[] resultVectArr = new SparseMatrix[10];
		for (int i = 1; i < 10; i++) {
			for (int x = 0; x < intermediateVals2.length; x++) {
				for (int y = 0; y < intermediateVals2[0].length; y++) {
					double[][] tmpMat = new double[alphaOrig.nrows()][1];
					for (int j = 0; j < tmpMat.length; j++) {
						tmpMat[j][0] = intermediateVals2[x][y][j];
					}
					int resultIdx = x * intermediateVals2.length + y + 1;
					resultVectArr[resultIdx] = new SparseMatrix(tmpMat);
				}
			}
		}

		// get the top level pooled vector
		double[] result = new double[alphaOrig.nrows()];
		Arrays.fill(result, 0);
		for (int x = 0; x < intermediateVals2.length; x++) {
			for (int y = 0; y < intermediateVals2[0].length; y++) {
				// pool for final top result array and put the results for the
				// next level
				// down in the hierarchy in their corresponding result position.
				for (int i = 0; i < result.length; i++) {
					if (Math.abs(result[i]) < Math.abs(intermediateVals2[x][y][i]))
						result[i] = intermediateVals2[x][y][i];
				}
			}
		}

		// place the top level into the returned array.
		double[][] tmpMat = new double[alphaOrig.nrows()][1];
		for (int i = 0; i < result.length; i++) {
			tmpMat[i][0] = result[i];
		}
		resultVectArr[0] = new SparseMatrix(tmpMat);
		return resultVectArr;
	}

	private void copyCol(SparseMatrix A, double[] y, int col) {
		for (int i = 0; i < A.nrows(); i++) {
			y[i] = A.get(i, col);
		}
	}
}
