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

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseVectorApproximator;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryCleaner;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryUpdater;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.ISparseDictionaryLearner;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;

/**
 * Implements the online dictionary learning algorithm of Mairal et al., 2010.
 * The algorithm can be found in "Sparse Modeling, Theory, Algorithms, and
 * Applications" by Irina Rish and Genady Ya. Grabarnik. Published by CRC Press
 * 2015, on page 172.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class OnlineDictionaryLearner implements ISparseDictionaryLearner {

	private Random rand = new Random(0);

	private IDictionaryCleaner cleaner;
	private IDictionaryUpdater dictionaryUpdater;
	private ISparseVectorApproximator leqSolver;
	private boolean constantDictionarySize;
	private double fractionOfInputForDictionaryElements;
	private int constantNumber;
	private int batchSize;
	private double t0 = 1e-5;

	private ForkJoinPool forkJoinPool = null;

	/**
	 * Constructor that specifies the number of dictionary elements to contain in
	 * the returned dictionary as a fraction of the input matrix size.
	 * 
	 * @param leqSolver           System of Linear Equations Solver used by this
	 *                            object.
	 * @param dictionaryUpdater   Dictionary updated used by this object.
	 * @param cleaner             Dictionary cleaner used by this object.
	 * @param fractionOfInputSize The fraction of the input size that is wished as
	 *                            an output number of dictionary elements. Must be
	 *                            0.025 (2.5%) or greater.
	 * @param batchSize           The number of elements to select from the input
	 *                            between dictionary updates.
	 * @param numThreads          The number of threads to use when processing
	 *                            batch. -1 for all available &gt; 0 for a specific
	 *                            number.
	 */
	public OnlineDictionaryLearner(ISparseVectorApproximator leqSolver, IDictionaryUpdater dictionaryUpdater,
			IDictionaryCleaner cleaner, double fractionOfInputSize, int batchSize, int numThreads) {
		if (leqSolver == null)
			throw new IllegalArgumentException(
					"System of Linear Equations Solver cannot be null in Online Dictionary Learner.");
		if (dictionaryUpdater == null)
			throw new IllegalArgumentException("The dictionary updater cannot be null in Dictionary Learner.");
		if (cleaner == null)
			throw new IllegalArgumentException("The dictionary cleaner cannot be null in Dictionary Learner.");
		if (numThreads < -1 || numThreads == 0)
			throw new IllegalArgumentException("numThreads must be -1 or > 0 in OnlineDictionaryLearner constructor.");
		if (batchSize < 1)
			throw new IllegalArgumentException(
					"Batch size must be at least 1  in OnlineDictionaryLearner constructor.");
		if (fractionOfInputSize < 0.025)
			throw new IllegalArgumentException(
					"fractionOfInputSize must be at least 2.5% in OnlineDictionaryLearner constructor.");

		this.dictionaryUpdater = dictionaryUpdater;
		this.fractionOfInputForDictionaryElements = fractionOfInputSize;
		this.constantDictionarySize = false;
		this.leqSolver = leqSolver;
		this.batchSize = batchSize;
		this.cleaner = cleaner;

		if (numThreads == -1) {
			this.forkJoinPool = new ForkJoinPool();
		} else if (numThreads > 1) {
			this.forkJoinPool = new ForkJoinPool(numThreads);
		}

	}

	/**
	 * Constructor that specifies the number of dictionary elements to contain in
	 * the returned dictionary.
	 * 
	 * @param leqSolver          System of Linear Equations Solver used by this
	 *                           object.
	 * @param dictionaryUpdater  Dictionary updated used by this object.
	 * @param cleaner            Dictionary cleaner used by this object.
	 * @param dictionaryElements The number of dictionary elements to include in the
	 *                           returned dictionary.
	 * @param batchSize          The number of elements to select from the input
	 *                           between dictionary updates.
	 * @param numThreads         The number of threads to use when processing batch.
	 *                           -1 for all available &gt; 0 for a specific number.
	 */
	public OnlineDictionaryLearner(ISparseVectorApproximator leqSolver, IDictionaryUpdater dictionaryUpdater,
			IDictionaryCleaner cleaner, int dictionaryElements, int batchSize, int numThreads) {
		if (leqSolver == null)
			throw new IllegalArgumentException(
					"System of Linear Equations Solver cannot be null in Online Dictionary Learner.");
		if (dictionaryUpdater == null)
			throw new IllegalArgumentException("The dictionary updater cannot be null in Dictionary Learner.");
		if (cleaner == null)
			throw new IllegalArgumentException("The dictionary cleaner cannot be null in Dictionary Learner.");
		if (numThreads < -1 || numThreads == 0)
			throw new IllegalArgumentException("numThreads must be -1 or > 0 in OnlineDictionaryLearner constructor.");
		if (batchSize < 1)
			throw new IllegalArgumentException(
					"Batch size must be at least 1  in OnlineDictionaryLearner constructor.");
		if (dictionaryElements < 1)
			throw new IllegalArgumentException(
					"dictionaryElements must be at least 1 in OnlineDictionaryLearner constructor.");

		this.dictionaryUpdater = dictionaryUpdater;
		this.constantDictionarySize = true;
		this.constantNumber = dictionaryElements;
		this.leqSolver = leqSolver;
		this.batchSize = batchSize;
		this.cleaner = cleaner;

		if (numThreads == -1) {
			this.forkJoinPool = new ForkJoinPool();
		} else if (numThreads > 1) {
			this.forkJoinPool = new ForkJoinPool(numThreads);
		}

	}

	@Override
	public void finalize() throws Throwable {

		this.cleaner = null;
		this.dictionaryUpdater = null;
		this.leqSolver = null;

		if (this.forkJoinPool != null) {
			this.forkJoinPool.shutdownNow();
			this.forkJoinPool = null;
		}

	}

	@Override
	public DenseMatrix train(List<double[]> inputSamples) throws MatrixDimensionMismatch, VectorDimensionMismatch {
		int numSamples = inputSamples.size();

		int numColsInDict;
		if (constantDictionarySize) {
			numColsInDict = constantNumber;
		} else {
			numColsInDict = (int) Math.ceil((inputSamples.get(0).length * this.fractionOfInputForDictionaryElements));
		}
		final int inputRows = inputSamples.get(0).length;
		final int numberIterations;

		numberIterations = inputSamples.size();

		// D_0 initial dictionary
		double[][] tmpDict = new double[inputRows][numColsInDict];

		// Initialize the dictionary with random values from the input data.
		for (int i = 0; i < numColsInDict; i++) {
			int idx = this.rand.nextInt(numSamples);
			double[] colVect = inputSamples.get(idx);
			for (int j = 0; j < inputRows; j++) {
				tmpDict[j][i] = colVect[j];
			}
		}

		// center the columns in the dictionary to have zero mean and unit
		// variance.
		Math.normalize(tmpDict, true);
		JMatrix dictionary = new JMatrix(tmpDict);
		tmpDict = null;

		if (this.forkJoinPool != null) {
			this.parallelLearn(numberIterations, inputSamples, dictionary);
		} else {
			this.sequentialLearn(numberIterations, inputSamples, dictionary);
		}

		return dictionary;
	}

	private void sequentialLearn(final int numberIterations, List<double[]> inputSamples, JMatrix dictionary)
			throws MatrixDimensionMismatch, VectorDimensionMismatch {
		int numColsInDict = dictionary.ncols();
		int inputRows = inputSamples.get(0).length;

		JMatrix auxMatrixU = new JMatrix(numColsInDict, numColsInDict, 0.0);
		JMatrix auxMatrixUOdd = new JMatrix(numColsInDict, numColsInDict, 0.0);
		JMatrix auxMatrixUEven = new JMatrix(numColsInDict, numColsInDict, 0.0);
		JMatrix auxMatrixUtmp = new JMatrix(numColsInDict, numColsInDict, 0.0);

		JMatrix auxMatrixV = new JMatrix(inputRows, numColsInDict, 0.0);
		JMatrix auxMatrixVOdd = new JMatrix(inputRows, numColsInDict, 0.0);
		JMatrix auxMatrixVEven = new JMatrix(inputRows, numColsInDict, 0.0);
		JMatrix auxMatrixVtmp = new JMatrix(inputRows, numColsInDict, 0.0);

		JMatrix auxMatrixUOrig = new JMatrix(numColsInDict, numColsInDict, 0.0);
		for (int i = 0; i < auxMatrixUOrig.nrows(); i++)
			auxMatrixUOrig.set(i, i, this.t0);

		JMatrix auxMatrixVOrig = dictionary.copy();
		auxMatrixVOrig.mul(this.t0);

		boolean even = false;
		int[] permutationOfIdx = Math.permutate(inputSamples.size());

		for (int j = 0; j < numberIterations; j++) {

			// Compute the Gram Matrix D'*D on the dictionary.
			JMatrix Gm = dictionary.ata();
			this.cleaner.cleanDictionary(dictionary, inputSamples, Gm);

			// Add to the diagonal to avoid divide by zero
			for (int i = 0; i < Gm.nrows(); i++)
				Gm.add(i, i, 10e-10);

			// Process the batch with n parallel threads
			for (int i = 0; i < this.batchSize; i++) {
				int idx = permutationOfIdx[(i + (j * this.batchSize)) % permutationOfIdx.length];

				// y_i
				double[] dataCol = new double[inputRows];
				this.getCol(inputSamples, dataCol, idx);
				double dataColMean = Math.mean(dataCol);

				for (int k = 0; k < dataCol.length; k++) {
					dataCol[k] = (dataCol[k] - dataColMean);
				}

				// x_i = sparse coding computation using
				double[] sparseRep = new double[dictionary.ncols()];
				try {
					this.leqSolver.solve(dataCol, dictionary, Gm, sparseRep);
				} catch (RuntimeException | VectorDimensionMismatch | MatrixDimensionMismatch ex) {
					ex.printStackTrace();
				}

				// U_i <- U_i-1 + x_i*x_i^T
				this.rank1Update(sparseRep, auxMatrixUtmp);
				// V_i <- V_i-1 + y_i*x_i^T
				this.rank1Update(auxMatrixVtmp, dataCol, sparseRep);

				dataCol = null;
				sparseRep = null;
			}

			// Get rid of the Gram matrix as we don't use it past this point for
			// this iteration, and the next iteration is using a newly
			// calculated one.
			Gm = null;

			int epoch = (((j + 1) % inputRows) * this.batchSize) / inputRows;
			if ((even && ((epoch % 2) == 1)) || (!even && ((epoch % 2) == 0))) {
				// System.out.println("epoch");
				auxMatrixUOdd = auxMatrixUEven;
				auxMatrixUEven = new JMatrix(numColsInDict, numColsInDict, 0.0);
				auxMatrixVOdd = auxMatrixVEven;
				auxMatrixVEven = new JMatrix(inputRows, numColsInDict, 0.0);
				even = !even;
			}

			double scale = Math.max(0.95, Math.pow(j / (j + 1), -1.0));

			auxMatrixUOdd.mul(scale);
			auxMatrixUEven.mul(scale);
			auxMatrixVOdd.mul(scale);
			auxMatrixVEven.mul(scale);

			auxMatrixUEven.add(auxMatrixUtmp);
			this.clearMat(auxMatrixUtmp);

			auxMatrixVEven.add(auxMatrixVtmp);
			this.clearMat(auxMatrixVtmp);

			if (j * this.batchSize < 10000) {
				auxMatrixUOrig.mul(scale);
				auxMatrixVOrig.mul(scale);
				auxMatrixU = auxMatrixUOrig.copy();
				auxMatrixV = auxMatrixVOrig.copy();
			} else {
				this.clearMat(auxMatrixU);
				this.clearMat(auxMatrixV);
			}

			auxMatrixU.add(auxMatrixUOdd);
			auxMatrixU.add(auxMatrixUEven);
			auxMatrixV.add(auxMatrixVOdd);
			auxMatrixV.add(auxMatrixVEven);

			this.dictionaryUpdater.updateDictionary(dictionary, auxMatrixU, auxMatrixV);
		}

	}

	private void parallelLearn(final int numberIterations, List<double[]> inputSamples, JMatrix dictionary)
			throws MatrixDimensionMismatch, VectorDimensionMismatch {
		int numColsInDict = dictionary.ncols();
		int inputRows = inputSamples.get(0).length;

		int parallelCount = this.forkJoinPool.getParallelism();

		// U_0 array
		JMatrix auxMatrixUArr[] = new JMatrix[parallelCount];
		JMatrix auxMatrixUOdd = new JMatrix(numColsInDict, numColsInDict, 0.0);
		JMatrix auxMatrixUEven = new JMatrix(numColsInDict, numColsInDict, 0.0);

		// V_0 array
		JMatrix auxMatrixVArr[] = new JMatrix[parallelCount];
		JMatrix auxMatrixVOdd = new JMatrix(inputRows, numColsInDict, 0.0);
		JMatrix auxMatrixVEven = new JMatrix(inputRows, numColsInDict, 0.0);

		JMatrix auxMatrixU = new JMatrix(numColsInDict, numColsInDict, 0.0);
		JMatrix auxMatrixV = new JMatrix(inputRows, numColsInDict, 0.0);

		JMatrix auxMatrixUOrig = new JMatrix(numColsInDict, numColsInDict, 0.0);
		for (int i = 0; i < auxMatrixUOrig.nrows(); i++)
			auxMatrixUOrig.set(i, i, this.t0);

		JMatrix auxMatrixVOrig = dictionary.copy();
		auxMatrixVOrig.mul(this.t0);

		// Create a new V_0 and U_0 for each thread.
		for (int i = 0; i < parallelCount; i++) {
			// U_0
			auxMatrixUArr[i] = new JMatrix(numColsInDict, numColsInDict, 0.0);
			// V_0
			auxMatrixVArr[i] = new JMatrix(inputRows, numColsInDict, 0.0);
		}

		boolean even = false;
		int[] permutationOfIdx = Math.permutate(inputSamples.size());

		for (int j = 0; j < numberIterations; j++) {

			// Compute the Gram Matrix D'*D on the dictionary.
			JMatrix Gm = dictionary.ata();
			this.cleaner.cleanDictionary(dictionary, inputSamples, Gm);

			// Add to the diagonal to avoid divide by zero
			for (int i = 0; i < Gm.nrows(); i++)
				Gm.add(i, i, 10e-10);

			// Process the batch with n parallel threads
			final int JJ = j;
			try {
				this.forkJoinPool.submit(() -> {
					IntStream.range(0, this.batchSize).parallel().forEach(i -> {
						// for (int i = 0; i < this.batchSize; i++) {
						int idx = permutationOfIdx[(i + (JJ * this.batchSize)) % permutationOfIdx.length];

						// y_i
						double[] dataCol = new double[inputRows];
						this.getCol(inputSamples, dataCol, idx);
						double dataColMean = Math.mean(dataCol);

						for (int k = 0; k < dataCol.length; k++) {
							dataCol[k] = (dataCol[k] - dataColMean);
						}

						// x_i = sparse coding computation using
						double[] sparseRep = new double[dictionary.ncols()];
						try {
							this.leqSolver.solve(dataCol, dictionary, Gm, sparseRep);
						} catch (RuntimeException | VectorDimensionMismatch | MatrixDimensionMismatch ex) {
							System.out.println(ex.getMessage());
						}

						int idx2 = (int) (Thread.currentThread().getId() % parallelCount);

						// U_i <- U_i-1 + x_i*x_i^T
						this.rank1Update(sparseRep, auxMatrixUArr[idx2]);
						// V_i <- V_i-1 + y_i*x_i^T
						this.rank1Update(auxMatrixVArr[idx2], dataCol, sparseRep);
						dataCol = null;
						sparseRep = null;
					});
				}).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			int epoch = (((j + 1) % inputRows) * this.batchSize) / inputRows;
			if ((even && ((epoch % 2) == 1)) || (!even && ((epoch % 2) == 0))) {
				auxMatrixUOdd = auxMatrixUEven;
				auxMatrixUEven = new JMatrix(numColsInDict, numColsInDict, 0.0);
				auxMatrixVOdd = auxMatrixVEven;
				auxMatrixVEven = new JMatrix(inputRows, numColsInDict, 0.0);
				even = !even;
			}

			double scale = Math.max(0.95, Math.pow(j / (j + 1), -1.0));

			auxMatrixUOdd.mul(scale);
			auxMatrixUEven.mul(scale);
			auxMatrixVOdd.mul(scale);
			auxMatrixVEven.mul(scale);

			double scale2 = 1.0 / this.batchSize;
			for (int i = 0; i < parallelCount; i++) {
				auxMatrixUArr[i].mul(scale2);
				auxMatrixUEven.add(auxMatrixUArr[i]);
				this.clearMat(auxMatrixUArr[i]);

				auxMatrixVArr[i].mul(scale2);
				auxMatrixVEven.add(auxMatrixVArr[i]);
				this.clearMat(auxMatrixVArr[i]);
			}

			if (j * this.batchSize < 10000) {
				auxMatrixUOrig.mul(scale);
				auxMatrixVOrig.mul(scale);
				auxMatrixU = auxMatrixUOrig.copy();
				auxMatrixV = auxMatrixVOrig.copy();
			} else {
				this.clearMat(auxMatrixU);
				this.clearMat(auxMatrixV);
			}

			auxMatrixU.add(auxMatrixUOdd);
			auxMatrixU.add(auxMatrixUEven);
			auxMatrixV.add(auxMatrixVOdd);
			auxMatrixV.add(auxMatrixVEven);

			this.dictionaryUpdater.updateDictionary(dictionary, auxMatrixU, auxMatrixV);
		}
	}

	public void rank1Update(JMatrix mat, double[] vec1, double[] vec2) {
		for (int i = 0; i < mat.ncols(); i++) {
			if (vec2[i] != 0) {
				for (int j = 0; j < mat.nrows(); j++) {
					mat.add(j, i, (vec1[j] * vec2[i]));
				}
			}
		}

	}

	private void rank1Update(double[] x, JMatrix mat) {
		for (int i = 0; i < mat.ncols(); i++) {
			if (x[i] != 0) {
				for (int j = 0; j < mat.nrows(); j++) {
					mat.add(j, i, (x[i] * x[j]));
				}
			}
		}

	}

	private void getCol(List<double[]> A, double[] vect, int j) {
		double[] colVect = A.get(j);
		for (int i = 0; i < colVect.length; i++) {
			vect[i] = colVect[i];
		}
	}

	private void clearMat(DenseMatrix A) {
		for (int i = 0; i < A.nrows(); i++)
			for (int j = 0; j < A.ncols(); j++)
				A.set(i, j, 0.0);
	}

}
