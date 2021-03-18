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
package edu.gsu.cs.dmlab.imageproc.util.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;

public class MatrixUtilTests {

	@Test
	public void matrixUtilGetSubmatrixAsArrayThrowsOnNullImageMatrix() {
		int startingCol = 0;
		int startingRow = 0;
		int pSize = 4;
		double[][] imageMatrix = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsArrayThrowsOnNullImageMatrix2() {
		int startingCol = 0;
		int startingRow = 0;
		int pSize = 4;
		double[][] imageMatrix = new double[64][];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsArrayThrowsOnNotMultipOfPSize() {
		int startingCol = 0;
		int startingRow = 0;
		int pSize = 5; // Not multiple of 64
		double[][] imageMatrix = new double[64][64];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsArrayThrowsOnInvalidPSize() {
		int startingCol = 0;
		int startingRow = 0;
		int pSize = 0; // invalid pSize
		double[][] imageMatrix = new double[64][64];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsArrayThrowsOnMatrixNotSquare() {
		int startingCol = 0;
		int startingRow = 0;
		int pSize = 4;
		double[][] imageMatrix = new double[64][32];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubMatrixAsArrayThrowsOnOutofboundSubmatrix() {
		int wStart = 7;
		int hStart = 7;
		int pSize = 2;
		double[][] imageMatrix = new double[8][8];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsArray(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsArrayOutputValue() {

		int startingCol = 1;
		int startingRow = 1;
		int pSize = 2;
		double[] expected = new double[] { 2, 2, 2, 2 };
		double[] output = null;

		double[][] imageMatrix = new double[][] { { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 2, 2, 1 }, { 1, 1, 1, 1 } };

		output = MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);
		assertArrayEquals(expected, output, 0.00001);

	}

	@Test
	public void matrixUtilGetSubmatrixAsArrayOutputValue2() {

		int startingRow = 2;
		int startingCol = 1;
		int pSize = 2;
		double[] expected = new double[] { 2, 2, 1, 1 };
		double[] output = null;

		double[][] imageMatrix = new double[][] { { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 2, 2, 1 }, { 1, 1, 1, 1 } };

		output = MatrixUtil.getSubMatrixAsArray(imageMatrix, startingRow, startingCol, pSize);

		assertArrayEquals(expected, output, 0.000001);

	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixThrowsOnNullImageMatrix() {
		int wStart = 0;
		int hStart = 0;
		int pSize = 4;
		double[][] imageMatrix = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsMatrix(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixThrowsOnNullImageMatrix2() {
		int wStart = 0;
		int hStart = 0;
		int pSize = 4;
		double[][] imageMatrix = new double[64][];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsMatrix(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixThrowsOnNotMultipOfPSize() {
		int wStart = 0;
		int hStart = 0;
		int pSize = 5; // Not multiple of 64
		double[][] imageMatrix = new double[64][64];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsMatrix(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixThrowsOnInvalidPSize() {
		int wStart = 0;
		int hStart = 0;
		int pSize = 0; // invalid pSize
		double[][] imageMatrix = new double[64][64];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsMatrix(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixThrowsOnMatrixNotSquare() {
		int wStart = 0;
		int hStart = 0;
		int pSize = 4;
		double[][] imageMatrix = new double[64][32];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsMatrix(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubMatrixAsMatrixThrowsOnOutofboundSubmatrix() {
		int wStart = 7;
		int hStart = 7;
		int pSize = 2;
		double[][] imageMatrix = new double[8][8];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.getSubMatrixAsMatrix(imageMatrix, wStart, hStart, pSize);
		});
	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixOutputValue() {

		int startingCol = 1;
		int startingRow = 1;
		int pSize = 2;
		double[][] expected = new double[][] { { 2, 2 }, { 2, 2 } };
		double[][] output = null;

		double[][] imageMatrix = new double[][] { { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 2, 2, 1 }, { 1, 1, 1, 1 } };

		output = MatrixUtil.getSubMatrixAsMatrix(imageMatrix, startingRow, startingCol, pSize);
		assertArrayEquals(expected, output);
	}

	@Test
	public void matrixUtilGetSubmatrixAsMatrixOutputValue2() {

		int startingRow = 2;
		int startingCol = 1;
		int pSize = 2;
		double[][] expected = new double[][] { { 2, 2 }, { 1, 1 } };
		double[][] output = null;

		double[][] imageMatrix = new double[][] { { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 2, 2, 1 }, { 1, 1, 1, 1 } };

		output = MatrixUtil.getSubMatrixAsMatrix(imageMatrix, startingRow, startingCol, pSize);

		/*
		 * for(int i = 0; i < output.length; i++){
		 * System.out.println(Arrays.toString(output[i])); }
		 * 
		 * for(int i = 0; i < expected.length; i++){
		 * System.out.println(Arrays.toString(expected[i])); }
		 */
		assertArrayEquals(expected, output);
	}

	@Test
	public void matrixUtilConvertTo2DArrayThrowsOnNullArray() {

		double[] arr = null; // null array
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.convertTo2DArray(arr, 3, 3);
		});
	}

	@Test
	public void matrixUtilConvertTo2DArrayThrowsOnMismatchingSize() {

		int width = 3;
		int height = 4;
		double[] arr = new double[9]; // mismatching array size
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			MatrixUtil.convertTo2DArray(arr, width, height);
		});
	}

	@Test
	public void matrixUtilConvertTo2DArrayOutputTest() {

		int width = 4;
		int height = 3;
		double[] arr = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		// Output should be of size 3 X 4
		double[][] expectedOutput = new double[][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } };
		double[][] actualOutput = MatrixUtil.convertTo2DArray(arr, width, height);
		assertTrue(Arrays.deepEquals(expectedOutput, actualOutput));

	}

}