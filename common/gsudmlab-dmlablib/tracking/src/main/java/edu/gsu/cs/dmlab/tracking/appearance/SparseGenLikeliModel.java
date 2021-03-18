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
package edu.gsu.cs.dmlab.tracking.appearance;

import org.locationtech.jts.geom.Envelope;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTSparseCandidateModel;
import smile.math.Math;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;

/**
 * This class is used to calculate the generative likelihood of the candidate
 * object given a learned dictionary on a training object. The input is the
 * error of reconstruction of the candidate using the dictionary. It assumes
 * that the minimum size of an object is 5x5 as this is what objects are
 * automatically sized to if they are smaller than this when using the database
 * connection class in this library. It also assumes that patches are extracted
 * by starting in the upper left corner of the object, then moving down by one
 * step to extract the next patch, until the bottom of the object is reached.
 * Then it moves to the right by one and starts at the top again. This is what
 * is done by the Image Patch Vectorizer in this library when step size is set
 * to one. The weights of each element are determined by how close center of the
 * patch being processed is to the center of the object, using an isotropic
 * gaussian kernel. From Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class SparseGenLikeliModel implements ISTSparseCandidateModel {

	private int patchSize;
	private int paramDownSample;
	private int halfPatchSize;

	/**
	 * Constructor
	 * 
	 * @param patchSize       The size of the patches used in the construction of
	 *                        the input matrix when creating the sparse
	 *                        representation matrix.
	 * 
	 * @param paramDownSample The down sampling value for the objects that are
	 *                        passed in. The objects are generally in full
	 *                        resolution size but the image parameters are a down
	 *                        sampling of the original image, so we need to shrink
	 *                        the objects using this value.
	 */
	public SparseGenLikeliModel(int patchSize, int paramDownSample) {
		if (patchSize <= 0)
			throw new IllegalArgumentException("The patch size cannot be less than 1.");
		if (paramDownSample <= 0)
			throw new IllegalArgumentException("The down sample cannot be less than 1.");
		this.patchSize = patchSize;
		this.paramDownSample = paramDownSample;
		this.halfPatchSize = (this.patchSize - 1) / 2;
	}

	@Override
	public double getCandidateProb(DenseMatrix errorMatCandidate, Envelope bbox, Envelope bbox2) {

		// calculate how the candidate is scaled to match the original
		// starting with height
		double heightScale;
		// Also, calculate how tall the window is in patches
		int height;
		int[] scaledCenter = new int[2];
		if (bbox.getHeight() / this.paramDownSample < 5) {
			if (bbox2.getHeight() / this.paramDownSample < 5) {
				heightScale = 1;
			} else {
				heightScale = 5 / (bbox2.getHeight() / this.paramDownSample);
			}
			// this needs updated to take into account step
			height = 5 - (patchSize - 1);

			scaledCenter[1] = 2;
		} else {
			if (bbox2.getHeight() / this.paramDownSample < 5) {
				heightScale = (bbox.getHeight() / this.paramDownSample) / 5;
				height = 5 - (patchSize - 1);
			} else {
				heightScale = (bbox.getHeight() / this.paramDownSample) / (bbox2.getHeight() / this.paramDownSample);

				// same need for step here, but it is 1 for now.
				height = ((int) bbox2.getHeight() / this.paramDownSample) - (patchSize - 1);
			}

			scaledCenter[1] = (int) ((bbox.getHeight() / this.paramDownSample) / 2.0);
		}

		// then the width
		double widthScale;
		if (bbox.getWidth() / this.paramDownSample < 5) {
			if (bbox2.getWidth() / this.paramDownSample < 5) {
				widthScale = 1;
			} else {
				widthScale = 5 / (bbox2.getWidth() / this.paramDownSample);
			}
			scaledCenter[0] = 2;
		} else {
			if (bbox2.getWidth() / this.paramDownSample < 5) {
				widthScale = (bbox.getWidth() / this.paramDownSample) / 5;
			} else {
				widthScale = (bbox.getWidth() / this.paramDownSample) / (bbox2.getWidth() / this.paramDownSample);
			}
			scaledCenter[0] = (int) ((bbox.getWidth() / this.paramDownSample) / 2.0);
		}

		// Calculate the sum of all the patch probabilities.
		double[] err = new double[errorMatCandidate.nrows()];

		double patchSum = 0;
		// find a sigma value that is appropriate for the isotropic function
		double isosigmaSquared = Math.pow(1 + (Math.max(bbox.getWidth(), bbox.getHeight()) / 5.0), 2);

		double var = this.getVariance((JMatrix) errorMatCandidate);
		for (int j = 0; j < errorMatCandidate.ncols(); j++) {

			// get the column from the error matrix which corresponds to a
			// patch
			this.getCol(((JMatrix) errorMatCandidate).array(), err, j);

			// calculate where the patch is located within the scaled kernel
			// mask
			int posY = (int) (((j % height) + this.halfPatchSize) * heightScale);
			int posX = (int) (((j / height) + this.halfPatchSize) * widthScale);

			// get the weight for the patch at this location
			double isoVal = this.isotropicGaussian2d(posX - scaledCenter[0], posY - scaledCenter[1], isosigmaSquared);

			// get the norm
			double errNorm = Math.norm2(err);

			double ratio;
			if (var != 0) {
				ratio = errNorm / var;
			} else {
				ratio = 1;
			}
			patchSum += -(isoVal * ratio);
		}

		return patchSum;

	}

	private double isotropicGaussian2d(double x, double y, double sigmaSquared) {
		double exp = Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * sigmaSquared));
		double denom = 2 * Math.PI * sigmaSquared;
		double result = exp / denom;
		return result;
	}

	private void getCol(double[][] A, double[] vect, int j) {
		for (int i = 0; i < A.length; i++)
			vect[i] = A[i][j];
	}

	private double getVariance(JMatrix data) {
		int num = (data.ncols() * data.nrows());
		double mean = data.sum() / num;
		double temp = 0;
		for (double[] row : data.array()) {
			for (double a : row) {
				temp += Math.pow((a - mean), 2);
			}
		}
		return (temp / num);
	}

}
