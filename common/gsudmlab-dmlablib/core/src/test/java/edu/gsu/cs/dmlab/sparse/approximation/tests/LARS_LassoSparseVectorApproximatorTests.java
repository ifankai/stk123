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
package edu.gsu.cs.dmlab.sparse.approximation.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.sparse.approximation.LARS_LassoCoeffVectorApproximator;
import edu.gsu.cs.dmlab.sparse.approximation.LassoMode;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseVectorApproximator;
import smile.math.matrix.JMatrix;

public class LARS_LassoSparseVectorApproximatorTests {

	@Test
	public void testOutVectMode1() throws VectorDimensionMismatch, MatrixDimensionMismatch {
		double[][] D = {
				{ -0.43226, -0.48248, -0.26537, -0.22232, 0.03624, -0.42496, 0.32353, 0.27588, -0.65685, 0.44362,
						0.63198, 0.62269, -0.06507, -0.36859, 0.54211, -0.17860, 0.51460, 0.13098, 0.40871, 0.27474 },
				{ 0.44599, 0.00582, 0.81872, 0.09056, 0.25939, 0.68683, -0.48404, -0.75269, 0.41448, -0.34969, 0.64789,
						-0.21849, -0.27033, 0.66193, 0.76116, 0.82731, 0.38141, -0.54159, 0.79934, -0.20662 },
				{ 0.50651, 0.52484, -0.07106, 0.43821, 0.59507, 0.13729, -0.39485, 0.17669, 0.56712, -0.20704, -0.04988,
						-0.22987, 0.66883, 0.23142, 0.20948, -0.34107, 0.22700, -0.42406, 0.01332, -0.49021 },
				{ 0.42697, 0.31947, -0.09549, 0.28259, -0.70532, -0.54390, 0.15781, 0.09874, 0.27152, 0.71037, 0.31484,
						-0.48220, 0.40399, -0.45297, -0.23731, -0.03023, 0.72124, -0.46835, -0.34585, -0.07590 },
				{ -0.41879, 0.62423, -0.49509, -0.81883, -0.28251, -0.18165, 0.69298, 0.56248, 0.03743, -0.36528,
						0.28146, -0.52836, -0.55870, 0.40896, 0.16295, 0.40793, 0.13417, 0.53883, -0.27245,
						-0.79735 } };

		double[] y = { 0.89426, 0.16915, 0.07855, -0.22207, -0.34089 }; // original
																		// signal

		LassoMode mode = LassoMode.PENALTY;
		ISparseVectorApproximator lasso = new LARS_LassoCoeffVectorApproximator(0.15, mode);
		JMatrix Dm = new JMatrix(D);
		double[] coeffs = new double[Dm.ncols()];
		lasso.solve(y, Dm, coeffs);

		double[] coeffAns = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.64373, 0, 0, 0.28417, 0, 0.20423, 0, 0,
				0 };

		assertArrayEquals(coeffAns, coeffs, 0.0001);

	}

	@Test
	public void testOutVectMode2() throws VectorDimensionMismatch, MatrixDimensionMismatch {
		double[][] D = {
				{ -0.43226, -0.48248, -0.26537, -0.22232, 0.03624, -0.42496, 0.32353, 0.27588, -0.65685, 0.44362,
						0.63198, 0.62269, -0.06507, -0.36859, 0.54211, -0.17860, 0.51460, 0.13098, 0.40871, 0.27474 },
				{ 0.44599, 0.00582, 0.81872, 0.09056, 0.25939, 0.68683, -0.48404, -0.75269, 0.41448, -0.34969, 0.64789,
						-0.21849, -0.27033, 0.66193, 0.76116, 0.82731, 0.38141, -0.54159, 0.79934, -0.20662 },
				{ 0.50651, 0.52484, -0.07106, 0.43821, 0.59507, 0.13729, -0.39485, 0.17669, 0.56712, -0.20704, -0.04988,
						-0.22987, 0.66883, 0.23142, 0.20948, -0.34107, 0.22700, -0.42406, 0.01332, -0.49021 },
				{ 0.42697, 0.31947, -0.09549, 0.28259, -0.70532, -0.54390, 0.15781, 0.09874, 0.27152, 0.71037, 0.31484,
						-0.48220, 0.40399, -0.45297, -0.23731, -0.03023, 0.72124, -0.46835, -0.34585, -0.07590 },
				{ -0.41879, 0.62423, -0.49509, -0.81883, -0.28251, -0.18165, 0.69298, 0.56248, 0.03743, -0.36528,
						0.28146, -0.52836, -0.55870, 0.40896, 0.16295, 0.40793, 0.13417, 0.53883, -0.27245,
						-0.79735 } };

		double[] y = { 0.39968, 0.27496, 0.51755, 0.16276, -0.68578 }; // original
																		// signal

		LassoMode mode = LassoMode.L1COEFF;
		ISparseVectorApproximator lasso = new LARS_LassoCoeffVectorApproximator(0.15, mode);
		double[] coeffs = new double[D[0].length];
		lasso.solve(y, new JMatrix(D), coeffs);

		double[] coeffAns = new double[] { 0, 0, 0, 0.10141, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.04859, 0, 0 };

		assertArrayEquals(coeffAns, coeffs, 0.0001);

	}

	@Test
	public void testOutVectSmall() throws VectorDimensionMismatch, MatrixDimensionMismatch {
		double[][] D = { { -1, 1 }, { 0, 0 }, { 1, 1 } };

		double[] y = { -1.111, 0, -1.111 }; // original
											// signal

		LassoMode mode = LassoMode.L1COEFF;
		ISparseVectorApproximator lasso = new LARS_LassoCoeffVectorApproximator(0.9611, mode);
		double[] coeffs = new double[D[0].length];
		lasso.solve(y, new JMatrix(D), coeffs);

		double[] coeffAns = new double[] { 0.0, -0.9611 };

		assertArrayEquals(coeffAns, coeffs, 0.0001);

	}

}
