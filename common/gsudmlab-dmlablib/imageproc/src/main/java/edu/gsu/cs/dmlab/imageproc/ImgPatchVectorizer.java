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

import java.util.ArrayList;

import edu.gsu.cs.dmlab.imageproc.interfaces.IImgPatchVectorizer;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;

/**
 * This class extracts a set of column vectors from the input matrices. It is
 * assumed that each new matrix in the array of matrices is another parameter at
 * the same position. Therefore, each position 1,1 will be concatenated first,
 * then 2,2 and so forth until the first patch is done. Then the patch is moved
 * from having its upper left hand corner at 1,1 to having it at 1+step,1,
 * repeating this until the bottom of the are is reached. Then the patches
 * starts at the top again at 1,1+step, and so forth.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ImgPatchVectorizer implements IImgPatchVectorizer {

	private int step;
	private int patchSize;

	/**
	 * Constructor constructs a new ImgPatchVectorizer with the given step size.
	 * 
	 * @param step
	 *            The step size of the image patches when vectorizing.
	 * @param patchSize
	 *            The size of the patches to extract from the image.
	 */
	public ImgPatchVectorizer(int step, int patchSize) {
		if (patchSize <= 0)
			throw new IllegalArgumentException("The patch size cannot be less than 1.");
		if (step <= 0)
			throw new IllegalArgumentException("The step cannot be less than 1.");
		this.step = step;
		this.patchSize = patchSize;
	}

	@Override
	public DenseMatrix vectorize(DenseMatrix[] imageDims) {

		int numRows = imageDims[0].nrows();
		int numCols = imageDims[0].ncols();

		ArrayList<double[]> colVects = new ArrayList<double[]>();
		for (int colIdx = 0; colIdx < numCols - (this.patchSize - 1); colIdx += this.step) {
			for (int rowIdx = 0; rowIdx < numRows - (this.patchSize - 1); rowIdx += this.step) {
				double[] vect = this.extract(colIdx, rowIdx, this.patchSize, imageDims);
				colVects.add(vect);
			}
		}

		if (!colVects.isEmpty()) {
			int returnRows = colVects.get(0).length;
			int returnCols = colVects.size();
	
			DenseMatrix returnMat = new JMatrix(returnRows, returnCols);
			for (int col = 0; col < returnCols; col++) {
				double[] colData = colVects.get(col);
				for (int row = 0; row < returnRows; row++) {
					returnMat.set(row, col,colData[row]);
				}
			}

			return returnMat;
		} else {
			return new JMatrix(0, 0);
		}

	}

	private double[] extract(int colIdx, int rowIdx, int patchSize, DenseMatrix[] imageDims) {
		double[] data = new double[patchSize * patchSize * imageDims.length];
		for (int col = 0; col < patchSize; col++) {
			for (int row = 0; row < patchSize; row++) {
				for (int dim = 0; dim < imageDims.length; dim++) {
					int idx = (col * patchSize * imageDims.length + (row * imageDims.length)) + dim;
					data[idx] = imageDims[dim].get(row + rowIdx, col + colIdx);
				}
			}
		}
		return data;
	}
}
