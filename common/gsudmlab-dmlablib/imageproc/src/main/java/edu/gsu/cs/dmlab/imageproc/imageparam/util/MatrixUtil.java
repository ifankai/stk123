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
package edu.gsu.cs.dmlab.imageproc.imageparam.util;

/**
 * This class is designed to extract sub-matrices, in the form of a 2D or 1D
 * array. The assumption is that the sub-matrix is always a square matrix.
 * 
 * <b>Note:</b> This class follows the convention of reading/writing matrices in
 * a row-by-row fashion.<br>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public abstract class MatrixUtil {

	/**
	 * For a given 2D matrix, this method returns a sub-matrix in the form of a 1D
	 * array. The output array is formed by putting each row of the sub-matrix
	 * concatenated to its previous row. (As opposed to a column-by-column
	 * concatenation.)
	 * 
	 * @param m
	 *            the given matrix
	 * @param startingCol
	 *            the starting row (width) where the sub-matrix should be extracted.
	 * @param startingRow
	 *            the starting col (height) where the sub-matrix should be
	 *            extracted.
	 * @param pSize
	 *            the size of the square sub-matrix. (Zero or negative values are
	 *            not allowed.)
	 * @return a 1D array representing the sub-matrix
	 */
	public static double[] getSubMatrixAsArray(double[][] m, int startingRow, int startingCol, int pSize) {

		double[] flatPatch = null;
		// Check basic requirements
		if (m == null || m[0] == null) {
			throw new IllegalArgumentException("The image matrix cannot be null in ImageManipulator.");
		}
		if (pSize <= 0) {
			throw new IllegalArgumentException("The submatrix width or height cannot be zero or negative!");
		}
		if ((m.length % pSize) != 0 || (m[0].length % pSize) != 0)
			throw new IllegalArgumentException("The image matrix must be divisible by the given patchSize!");
		if (m.length != m[0].length)
			throw new IllegalArgumentException("The image matrix's width must be eqaul to its height!");

		if ((pSize + startingRow > m.length) || (pSize + startingCol > m[0].length)) {
			throw new IllegalArgumentException("The requested sub-matrix goes out of boundary of the given matrix.");
		}

		flatPatch = new double[pSize * pSize];

		for (int row = 0; row < pSize; row++) {
			for (int col = 0; col < pSize; col++) {
				flatPatch[col + (pSize * row)] = m[row + startingRow][col + startingCol];
			}
		}

		return flatPatch;
	}

	/**
	 * For a given 2D matrix, this method returns a sub-matrix in the form of a 2D
	 * array.
	 * 
	 * @param m
	 *            the given matrix
	 * @param startingRow
	 *            the starting row (width) where the sub-matrix should be extracted.
	 * @param startingCol
	 *            the starting col (height) where the sub-matrix should be
	 *            extracted.
	 * @param pSize
	 *            the size of the square sub-matrix. (Zero or negative values are
	 *            not allowed.)
	 * @return a 2D array representing the sub-matrix
	 */
	public static double[][] getSubMatrixAsMatrix(double[][] m, int startingRow, int startingCol, int pSize) {
		double[][] matrixPatch = null;

		// Check basic requirements
		if (m == null || m[0] == null) {
			throw new IllegalArgumentException("The image matrix cannot be null in ImageManipulator.");
		}
		if (pSize <= 0) {
			throw new IllegalArgumentException("The submatrix width or height cannot be zero or negative!");
		}
		if ((m.length % pSize) != 0 || (m[0].length % pSize) != 0)
			throw new IllegalArgumentException("The image matrix must be divisible by the given patchSize!");
		if (m.length != m[0].length)
			throw new IllegalArgumentException("The image matrix's width must be eqaul to its height!");

		if ((pSize + startingRow > m.length) || (pSize + startingCol > m[0].length)) {
			throw new IllegalArgumentException("The requested sub-matrix goes out of boundary of the given matrix.");
		}

		matrixPatch = new double[pSize][pSize];

		for (int row = 0; row < pSize; row++) {
			for (int col = 0; col < pSize; col++) {
				matrixPatch[row][col] = m[row + startingRow][col + startingCol];
			}
		}

		return matrixPatch;
	}

	/**
	 * This method converts a 1D array to a 2D array given the expected width and
	 * height
	 * 
	 * @param array
	 * @param width
	 *            is similar to output.length
	 * @param height
	 *            is similar to output[0].length
	 * @return A 2D array of size width X height
	 */
	public static double[][] convertTo2DArray(double[] array, int width, int height) {

		// Check basic requirements
		if (array == null)
			throw new IllegalArgumentException("The input array is null!");
		if (width * height != array.length) {
			throw new IllegalArgumentException(
					"The given width and height do not match the length of the given array!");
		}
		double[][] result = new double[height][width];

		for (int i = 0; i < array.length; i++) {
			result[i / width][i % width] = array[i];
		}
		return result;

	}
}