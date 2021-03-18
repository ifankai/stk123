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

import java.util.Arrays;

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseVectorApproximator;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;
import smile.math.Math;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class LARS_LassoCoeffVectorApproximator implements ISparseVectorApproximator {

	double lambda;
	LassoMode mode;

	/**
	 * Constructor
	 * 
	 * @param lambda Depending on mode, lambda is used differently. If mode 1, then
	 *               lambda min amount of correlation between a coefficient in A and
	 *               signal b for the algorithm to proceed. If mode 2, max L1 norm
	 *               of the coefficient vector.
	 * @param mode   What mode to use PENALTY or L1COEFF. See lambda for
	 *               differences.
	 */
	public LARS_LassoCoeffVectorApproximator(double lambda, LassoMode mode) {
		if (mode == null)
			throw new IllegalArgumentException("Mode can not be null in LARS Coeff Vector Approximator constructor.");
		this.lambda = lambda;
		this.mode = mode;
	}

	/**
	 * Computes a sparse vector x, such that b = Ax using the LARS algorithm.
	 * 
	 * 
	 * @param b vector on LHS of equation, which is assumed to have mean 0.
	 * @param A Matrix A to use in the solution, which is assumed to have had its
	 *          columns normalized to have zero mean and unit l2 norm.
	 * @param x vector to compute and return
	 * @throws VectorDimensionMismatch If there is some sort of internal error.
	 * @throws MatrixDimensionMismatch
	 */
	@Override
	public void solve(double[] b, DenseMatrix A, double[] x) throws VectorDimensionMismatch, MatrixDimensionMismatch {

		// Compute the Gramm Matrix D'*D on the dictionary.
		DenseMatrix Gm = A.ata();

		// Add to the diagonal to avoid divide by zero
		for (int i = 0; i < Gm.nrows(); i++)
			Gm.add(i, i, 1e-10);

		this.solve(b, A, Gm, x);

	}

	/**
	 * Computes a sparse vector x, such that b = Ax using the LARS algorithm. Use
	 * this method when computing several vectors against the same matrix A to avoid
	 * computing AtA every method call.
	 * 
	 * 
	 * @param b   vector on LHS of equation, which is assumed to have mean 0.
	 * @param A   Matrix A to use in the solution, which is assumed to have had its
	 *            columns normalized to have zero mean and unit l2 norm.
	 * @param AtA The Gram Matrix of A used in the solution.
	 * @param x   vector to compute and return
	 * @throws VectorDimensionMismatch If there is some sort of internal error.
	 * @throws MatrixDimensionMismatch
	 */
	@Override
	public void solve(double[] b, DenseMatrix A, DenseMatrix AtA, double[] x)
			throws VectorDimensionMismatch, MatrixDimensionMismatch {
		if (b.length != A.nrows())
			new VectorDimensionMismatch("");
		if (x.length != A.ncols())
			new VectorDimensionMismatch("");

		// Setup Limit Values.
		int LL = A.ncols();
		int K = AtA.ncols();
		int L = Math.min(LL, K);
		int length_path = 4 * L;

		// The l2 norm of the input signal.
		double normX = Math.dot(b, b);

		// Calculate the vector of current correlations.
		double[] Rdn = new double[A.ncols()];
		Rdn = A.atx(b, Rdn);

		// The vector of coefficients that we will be updating.
		double[] coeffs = new double[A.ncols()];
		double[] sign = new double[A.ncols()];

		// The active set index vector we will be updating.
		int[] indxVect = new int[A.ncols()];
		Arrays.fill(indxVect, -1);

		// Find the most correlated element.
		int currentInd = this.whichAbsMax(Rdn, Rdn.length);

		if (this.mode == LassoMode.PENALTY && Math.abs(Rdn[currentInd]) < this.lambda)
			return;

		boolean newAtom = true;

		int iter = 1;
		double thrs = 0;

		JMatrix Ga = new JMatrix(0, 0);
		JMatrix Gs = new JMatrix(0, 0);
		JMatrix invGs = new JMatrix(0, 0);

		double[] u = new double[1];

		int i;
		for (i = 0; i < L; i++) {

			if (newAtom) {
				// If we are processing the direction along a new element.
				indxVect[i] = currentInd;
				sign[i] = Rdn[currentInd] > 0 ? 1 : -1;

				Ga = this.resizeAndCopyColMat(AtA, Ga, currentInd);

				// Increase the size of Gs to take the values from col i of Ga
				Gs = this.increaseSquareMatSize(Gs, i + 1);
				for (int j = 0; j <= i; j++)
					Gs.set(j, i, Ga.get(indxVect[j], i));

				invGs = this.increaseSquareMatSize(invGs, i + 1);
				if (i == 0) {
					invGs.set(0, 0, 1.0 / Gs.get(0, 0));
				} else {
					// Multiply invGs by col i of Gs and put it in u[]
					double[] tmpGsColI = this.getColJ(Gs, i);
					u = this.symv(i, invGs, tmpGsColI);

					// Copy the values to be used in the dot product as the
					// method doesn't have a limit and needs to just have two
					// vectors of the same size.
					double[] tmpGsColI2 = new double[i];
					for (int j = 0; j < i; j++)
						tmpGsColI2[j] = tmpGsColI[j];

					double schur = 1.0 / (Gs.get(i, i) - Math.dot(u, tmpGsColI2));
					invGs.set(i, i, schur);

					// Copy i values from u to invGs
					for (int j = 0; j < i; j++) {
						invGs.set(j, i, u[j] * (-schur));
					}

					// Rank one update: adds a symmetric matrix
					// to the product of a scaling factor, a vector, and its
					// transpose.
					invGs = this.syr(invGs, u, schur, i);

				}
			}
			// Now out of the new atom section, process from here every time.

			// Compute the path direction
			double[] work = new double[i + 1];
			for (int j = 0; j <= i; j++) {
				work[j] = Rdn[indxVect[j]] > 0 ? 1.0 : -1.0;
			}
			u = this.symv(i + 1, invGs, work);

			// Compute the step on the path
			double step_max = Double.POSITIVE_INFINITY;
			int first_zero = -1;
			for (int j = 0; j <= i; j++) {
				double ratio = -coeffs[j] / u[j];
				if (ratio > 0 && ratio <= step_max) {
					step_max = ratio;
					first_zero = j;
				}
			}

			double current_correlation = Math.abs(Rdn[indxVect[0]]);

			// for what ever reason, we need to make work the proper size for it
			// to be returned correctly.
			work = new double[K];
			work = Ga.ax(u, work);// get the direction and put it into work.

			// Array to use for finding the next direction after update.
			double[] work2 = new double[2 * K];

			// Copy the direction values over to the an array that gets two
			// copies starting at 0 and K
			for (int j = 0; j < K; j++) {
				work2[j] = work[j];
				work2[j + K] = work[j];
			}

			// Set those spots already in the active set to inf
			for (int j = 0; j <= i; ++j) {
				work2[indxVect[j]] = Double.POSITIVE_INFINITY;
				work2[indxVect[j] + K] = Double.POSITIVE_INFINITY;
			}

			// Now loop over each direction and test/update
			for (int j = 0; j < K; ++j) {
				work2[j] = ((work2[j] < Double.POSITIVE_INFINITY) && (work2[j] > -1.0))
						? (Rdn[j] + current_correlation) / (1.0 + work2[j])
						: Double.POSITIVE_INFINITY;

				work2[j + K] = ((work2[j + K] < Double.POSITIVE_INFINITY) && (work2[j + K] < 1.0))
						? (current_correlation - Rdn[j]) / (1.0 - work2[j + K])
						: Double.POSITIVE_INFINITY;
			}

			int idx = this.whichAbsMin(work2, 2 * K);
			double step = work2[idx];
			if (Math.abs(step) < 1e-15)
				step = 1e-15;

			// Set the next element
			currentInd = idx % K;

			// compute the coefficients of the poly representing normX^2
			double coeff1 = 0;
			double coeff2 = 0;
			for (int j = 0; j <= i; ++j) {
				coeff1 += Rdn[indxVect[j]] > 0 ? u[j] : -u[j];
				coeff2 += Rdn[indxVect[j]] * u[j];
			}
			// for (int j = 0; j <= i; ++j)

			double step_max2;
			if (this.mode == LassoMode.PENALTY) {
				// Simply error penalty
				step_max2 = current_correlation - this.lambda;
			} else {
				// L1 coeffs
				step_max2 = coeff1 < 0 ? Double.POSITIVE_INFINITY : (this.lambda - thrs) / coeff1;
				step_max2 = Math.min(current_correlation, step_max2);

				// Update the norm1
				thrs += step * coeff1;
			}

			step = Math.min(Math.min(step, step_max2), step_max);
			if (step == Double.POSITIVE_INFINITY) {
				System.out.println("Broken Step.");
				break; // stop the path.
			}

			// Update the normX
			double t = coeff1 * step * step - 2 * coeff2 * step;
			if (t > 0 || Double.isNaN(t) || Double.isInfinite(t)) {
				indxVect[i] = -1;
				break;
			}
			normX += t;

			// Update the coefficients
			this.axpy(i + 1, step, u, coeffs);

			// Update correlation
			this.axpy(K, -step, work, Rdn);

			if (step == step_max) {

				// Downdate, remove first_zero
				// Downdate Ga, Gs, invGs, ind, coeffs

				// First Ga, ind, and coeffs
				// copy Ga col j+1 back on to col j
				Ga = this.removeColumn(first_zero, Ga);
				for (int j = first_zero; j < i; j++) {
					indxVect[j] = indxVect[j + 1];
					coeffs[j] = coeffs[j + 1];
				}
				indxVect[i] = -1;
				coeffs[i] = 0;

				// Next Gs
				// copy first_zero elements from Gs col j+1 to Gs col j
				// Also, copy i-first_zero elements from Gs col j+1 to Gs col j
				for (int j = first_zero; j < i; ++j) {
					// copy first_zero elements from GS[][j+1] to Gs[][j]
					for (int m = 0; m < first_zero; m++) {
						Gs.set(m, j, Gs.get(m, j + 1));
					}
					// copy i-first_zero elements from Gs[first_zero+1][j+1] to
					// Gs[first_zero][j]
					for (int m = 0; m < (i - first_zero); m++) {
						Gs.set(m + first_zero, j, Gs.get(m + first_zero + 1, j + 1));
					}
				}
				Gs = this.decreaseSquareMatSize(Gs, Gs.nrows() - 1);

				// Now update invGs
				// Start by copy schur for imvGs[first_zeor][first_zero]
				// then copy first_zero elements from invGs col first_zero, to u
				// then i-first_zero elements from invGs col first_zero+1
				// starting at row first_zero, to u
				double schur = invGs.get(first_zero, first_zero);
				u = new double[i];

				// copy first_zero elements from invGs[][first_zero] into u[]
				for (int m = 0; m < first_zero; m++) {
					u[m] = invGs.get(m, first_zero);
				}

				// copy i-first_zero elements from
				// invGs[first_zero][first_zero+1] to u[first_zero]
				for (int m = 0; m < (i - first_zero); m++) {
					u[m + first_zero] = invGs.get(m + first_zero, first_zero + 1);
				}

				// Now finish update of invGs by moving cols beyond first_zero
				// back by one.
				for (int j = first_zero; j < i; ++j) {
					// copy first_zero elements from invGs[][j+1] to invGs[][j]
					for (int m = 0; m < first_zero; m++) {
						invGs.set(m, j, invGs.get(m, j + 1));
					}
					// copy i-first_zero elements from invGs[first_zero+1][j+1]
					// to invgs[first_zero][j]
					for (int m = 0; m < (i - first_zero); m++) {
						invGs.set(m + first_zero, j, invGs.get(m + first_zero + 1, j + 1));
					}
				}
				invGs = this.decreaseSquareMatSize(invGs, invGs.nrows() - 1);
				this.syr(invGs, u, -1.0 / schur, i);

				newAtom = false;
				i -= 2;
			} else {
				newAtom = true;
			}

			iter++;

			if (mode == LassoMode.PENALTY) {
				thrs = Math.abs(Rdn[indxVect[0]]);
			}

			if ((i == (L - 1)) || Math.abs(step) < 1e-15 || step == step_max2 || (normX < 1e-15)
					|| (iter >= length_path - 1) || (i < -1)
					|| (mode == LassoMode.L1COEFF && ((this.lambda - thrs) < 1e-15))) {
				// System.out.println("iter: " + (iter >= length_path - 1));
				// System.out.println("L-1: " + (i == (L - 1)));
				// System.out.println("Step: " + (Math.abs(step) < 1e-15));
				// System.out.println("StepMax: " + (step == step_max2));
				// System.out.println("MormX: " + (normX < 1e-15));
				// if (mode == LassoMode.L1COEFF) {
				// System.out.println("Thrs: " + (this.lambda - thrs));
				// }
				break;
			}
		}

		Arrays.fill(x, 0.0);
		for (int j = 0; j < i + 1; j++) {
			if (indxVect[j] == -1) {
				break;
			} else {
				x[indxVect[j]] = coeffs[j];
			}
		}

	}

	/**
	 * Computes a matrix-vector product for a symmetric matrix represented by a
	 * upper triangular matrix. y := alpha*A*x
	 * 
	 * @return
	 */
	private double[] symv(int rank, JMatrix A, double[] x) {

		double[] y = new double[rank];

		for (int j = 0; j < rank; j++) {
			double tmp1 = x[j];
			double tmp2 = 0.0;
			for (int i = 0; i < j; i++) {
				y[i] += tmp1 * A.get(i, j);
				tmp2 += A.get(i, j) * x[i];
			}
			y[j] += tmp1 * A.get(j, j) + tmp2;
		}
		return y;
	}

	/**
	 * Performs a rank-1 update of a symmetric matrix. A := alpha*x*x' + A, where
	 * alpha is a real scalar, x is an n element vector and A is an 33 n by n
	 * symmetric matrix.
	 * 
	 * @param A
	 * @param x
	 * @param alpha
	 * @return
	 */
	private JMatrix syr(JMatrix A, double[] x, double alpha, int rank) {

		for (int j = 0; j < rank; j++) {
			if (x[j] != 0.0) {
				double tmp = alpha * x[j];
				for (int i = 0; i <= j; i++)
					A.add(i, j, x[i] * tmp);
			}
		}

		return A;
	}

	private void axpy(int lim, double a, double[] x, double[] y) {
		for (int i = 0; i < lim; i++) {
			y[i] += (a * x[i]);
		}
	}

	private int whichAbsMin(double[] arr, int lim) {
		double minVal = Math.abs(arr[0]);
		int idxVal = 0;
		for (int i = 1; i < lim; i++) {
			double cur = Math.abs(arr[i]);
			if (cur < minVal) {
				minVal = cur;
				idxVal = i;
			}
		}
		return idxVal;
	}

	private int whichAbsMax(double[] arr, int lim) {
		double maxVal = 0;
		int idxVal = 0;
		for (int i = 0; i < lim; i++) {
			if (maxVal < Math.abs(arr[i])) {
				maxVal = Math.abs(arr[i]);
				idxVal = i;
			}
		}
		return idxVal;
	}

	private JMatrix increaseSquareMatSize(JMatrix mat, int size) {
		JMatrix newMat = new JMatrix(size, size);

		for (int i = 0; i < mat.nrows(); i++) {
			for (int j = 0; j < mat.ncols(); j++)
				newMat.set(i, j, mat.get(i, j));
		}
		return newMat;
	}

	private JMatrix decreaseSquareMatSize(JMatrix mat, int size) {
		if (size == 0)
			return new JMatrix(0, 0);

		JMatrix newMat = new JMatrix(size, size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++)
				newMat.set(i, j, mat.get(i, j));
		}
		return newMat;
	}

	private JMatrix resizeAndCopyColMat(DenseMatrix sourceMat, JMatrix destMat, int colNo) {

		JMatrix newMat = new JMatrix(sourceMat.ncols(), destMat.ncols() + 1);
		for (int i = 0; i < sourceMat.nrows(); i++) {
			for (int j = 0; j < destMat.ncols(); j++)
				newMat.set(i, j, destMat.get(i, j));
		}
		for (int i = 0; i < sourceMat.nrows(); i++) {
			newMat.set(i, destMat.ncols(), sourceMat.get(i, colNo));
		}
		return newMat;
	}

	private JMatrix removeColumn(int j, JMatrix A) {

		JMatrix newMat = new JMatrix(A.nrows(), A.ncols() - 1);

		// First copy the data that is unaffected by the column removal
		for (int i = 0; i < j; i++) {
			for (int m = 0; m < A.nrows(); m++) {
				newMat.set(m, i, A.get(m, i));
			}
		}

		// Then copy the columns from the right of the removed.
		for (int i = j; i < A.ncols() - 1; i++) {
			for (int m = 0; m < A.nrows(); m++) {
				newMat.set(m, i, A.get(m, i + 1));
			}
		}
		return newMat;
	}

	private double[] getColJ(JMatrix sourceMat, int j) {
		double[] data = new double[sourceMat.nrows()];
		for (int i = 0; i < data.length; i++) {
			data[i] = sourceMat.get(i, j);
		}
		return data;
	}

}
