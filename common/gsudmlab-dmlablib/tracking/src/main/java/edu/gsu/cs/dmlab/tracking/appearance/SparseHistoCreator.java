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

import java.util.Arrays;
import org.locationtech.jts.geom.Envelope;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTSparseHistoCreator;
import smile.math.Math;
import smile.math.matrix.SparseMatrix;

/**
 * This class is used to create a histogram of the sparse representation
 * elements. It assumes that the minimum size of an object is 5x5 as this is
 * what objects are automatically sized to if they are smaller than this when
 * using the database connection class in this library. It also assumes that
 * patches are extracted by starting in the upper left corner of the object,
 * then moving down by one step to extract the next patch, until the bottom of
 * the object is reached. Then it moves to the right by one and starts at the
 * top again. This is what is done by the Image Patch Vectorizer in this library
 * when step size is set to one. The weights of each element are determined by
 * how close center of the patch being processed is to the center of the object,
 * using an isotropic gaussian kernel. From Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SparseHistoCreator implements ISTSparseHistoCreator {

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
	public SparseHistoCreator(int patchSize, int paramDownSample) {
		if (patchSize <= 0)
			throw new IllegalArgumentException("The patch size cannot be less than 1.");
		if (paramDownSample <= 0)
			throw new IllegalArgumentException("The down sample cannot be less than 1.");
		this.patchSize = patchSize;
		this.paramDownSample = paramDownSample;
		this.halfPatchSize = (this.patchSize - 1) / 2;
	}

	@Override
	public double[] createTargetHisto(SparseMatrix alpha, Envelope bbox) {
		double[] tempModelHisto = new double[alpha.nrows()];

		for (int i = 0; i < tempModelHisto.length; i++)
			tempModelHisto[i] = this.getModelBin(i, bbox, alpha);

		double[] modelHisto = this.normalize(tempModelHisto);
		return modelHisto;
	}

	@Override
	public double[] createCandidateHisto(SparseMatrix alpha, Envelope originalBBox, Envelope candidateBBox) {
		double[] tempModelHisto = new double[alpha.nrows()];

		for (int i = 0; i < tempModelHisto.length; i++)
			tempModelHisto[i] = this.getModelCandidateBin(i, originalBBox, candidateBBox, alpha);

		double[] modelHisto = this.normalize(tempModelHisto);
		return modelHisto;
	}

	private double getModelCandidateBin(int binNum, Envelope origwindowSize, Envelope candidateWindowSize,
			SparseMatrix alpha) {
		double sum = 0;

		double[] a = this.getRow(alpha, binNum);

		// calculate how the candidate is scaled to match the original
		// starting with height
		double heightScale;
		// Also, calculate how tall the window is in patches
		int height;
		int[] scaledCenter = new int[2];
		if (origwindowSize.getHeight() / this.paramDownSample < 5) {
			if (candidateWindowSize.getHeight() / this.paramDownSample < 5) {
				heightScale = 1;
			} else {
				heightScale = 5 / (candidateWindowSize.getHeight() / this.paramDownSample);
			}
			// this needs updated to take into account step
			height = 5 - (patchSize - 1);

			scaledCenter[1] = 2;
		} else {
			if (candidateWindowSize.getHeight() / this.paramDownSample < 5) {
				heightScale = (origwindowSize.getHeight() / this.paramDownSample) / 5;
				height = 5 - (patchSize - 1);
			} else {
				heightScale = (origwindowSize.getHeight() / this.paramDownSample)
						/ (candidateWindowSize.getHeight() / this.paramDownSample);

				// same need for step here, but it is 1 for now.
				height = ((int) candidateWindowSize.getHeight() / this.paramDownSample) - (patchSize - 1);
			}

			scaledCenter[1] = (int) ((origwindowSize.getHeight() / this.paramDownSample) / 2.0);
		}

		// then the width
		double widthScale;
		if (origwindowSize.getWidth() / this.paramDownSample < 5) {
			if (candidateWindowSize.getWidth() / this.paramDownSample < 5) {
				widthScale = 1;
			} else {
				widthScale = 5 / (candidateWindowSize.getWidth() / this.paramDownSample);
			}
			scaledCenter[0] = 2;
		} else {
			if (candidateWindowSize.getWidth() / this.paramDownSample < 5) {
				widthScale = (origwindowSize.getWidth() / this.paramDownSample) / 5;
			} else {
				widthScale = (origwindowSize.getWidth() / this.paramDownSample)
						/ (candidateWindowSize.getWidth() / this.paramDownSample);
			}
			scaledCenter[0] = (int) ((origwindowSize.getWidth() / this.paramDownSample) / 2.0);
		}

		// find a sigma value that is appropriate for the isotropic function
		double sigmaSquared = Math.pow(1 + (Math.max(origwindowSize.getWidth(), origwindowSize.getHeight()) / 5.0), 2);

		for (int i = 0; i < alpha.ncols(); i++) {
			int posY = (int) (((i % height) + halfPatchSize) * heightScale);
			int posX = (int) (((i / height) + halfPatchSize) * widthScale);
			// System.out.println("(x,y): "+(posX - center[0])+","+(posY -
			// center[1]));
			double aVal = a[i];
			sum += (Math.abs(aVal)
					* this.isotropicGaussian2d((posX - scaledCenter[0]), (posY - scaledCenter[1]), sigmaSquared));
		}
		return sum;
	}

	private double getModelBin(int binNum, Envelope windowSize, SparseMatrix alpha) {

		double sum = 0;

		double[] a = this.getRow(alpha, binNum);

		// get the center location of the bounding box
		int[] center = new int[2];
		// and the height of the object, with the patch subtracted
		// this is the number of times the extraction process moved the window
		// down before it moved over one at the top of the column.
		int height;
		// first the x center
		if ((windowSize.getWidth() / this.paramDownSample) < 5) {
			// if too small then the min size is set to 5 with 3 the center.
			center[0] = 2;
		} else {
			// otherwise it is just where ever it lies
			center[0] = (int) ((windowSize.getWidth() / this.paramDownSample) / 2.0);
		}

		// then the y center
		if ((windowSize.getHeight() / this.paramDownSample) < 5) {
			// if too small then the min size is set to 5 with 3 in the center.
			center[1] = 2;
			// this needs updated to take into account step
			height = 5 - (patchSize - 1);
		} else {
			// otherwise it is just where ever it lies
			center[1] = (int) ((windowSize.getHeight() / this.paramDownSample) / 2.0);
			// same need for step here, but it is 1 for now.
			height = ((int) windowSize.getHeight() / this.paramDownSample) - (patchSize - 1);
		}

		// find a sigma value that is appropriate for the isotropic function
		double sigmaSquared = Math.pow(1 + (Math.max(windowSize.getWidth(), windowSize.getHeight()) / 5.0), 2);

		for (int i = 0; i < alpha.ncols(); i++) {
			int posY = (i % height) + halfPatchSize;
			int posX = (i / height) + halfPatchSize;
			// System.out.println("(x,y): "+(posX - center[0])+","+(posY -
			// center[1]));
			double aVal = a[i];
			sum += (Math.abs(aVal) * this.isotropicGaussian2d((posX - center[0]), (posY - center[1]), sigmaSquared));
		}
		return sum;
	}

	private double isotropicGaussian2d(double x, double y, double sigmaSquared) {
		double exp = Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * sigmaSquared));
		double denom = 2 * Math.PI * sigmaSquared;
		double result = exp / denom;
		return result;
	}

	private double[] getRow(SparseMatrix A, int j) {
		double[] row = new double[A.ncols()];
		for (int i = 0; i < A.ncols(); i++) {
			row[i] = A.get(j, i);
		}
		return row;
	}

	private double[] normalize(double[] data) {
		double[] retData = new double[data.length];
		double sum = 0;
		for (double a : data)
			sum += a;
		if (sum == 0) {
			Arrays.fill(retData, 0);
			return retData;
		}
		for (int i = 0; i < data.length; i++) {
			retData[i] = (float) (data[i] / sum);
		}
		return retData;
	}

}
