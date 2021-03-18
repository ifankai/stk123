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
package edu.gsu.cs.dmlab.imageproc.imageparam;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;

import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;

/**
 * 
 * This class is designed to compute the <b>Tamura Contrast</b> of each patch of
 * the given <code>BufferedImage</code>, based on the following formula:<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; C = (&sigma; ^ 2)/(&mu;4 ^ 0.25) <br>
 * <br>
 * where:
 * <UL>
 * <LI>&sigma;^2: the variance of intensity values in this patch.
 * <LI>&mu;4: the kurtosis (4-th moment about the mean) of intensity values in
 * this patch.
 * </UL>
 *
 * <b>Note:</b> This formula is an approximation proposed by Tamura et al. in
 * "<i>Textual Features Corresponding Visual Perception</i>"<br>
 * and investigated in "<i>On Using SIFT Descriptors for Image Parameter
 * Evaluation</i>"
 *
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class TContrastParamCalculator extends BaseMeanCalculator implements IParamCalculator {

	final static double _1_4th = 0.25;
	final static double epsilon = 1e-32;

	IMeasures.PatchSize patchSize;

	public TContrastParamCalculator(IMeasures.PatchSize patchSize) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in TContrastParamCalculator");

		this.patchSize = patchSize;
	}

	@Override
	public double[][] calculateParameter(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		double[][] tContrasts = null;
		double[] flatPatch = null;

		double sd;
		double kurtosis;
		int imageH = image.length;
		int imageW = image[0].length;
		// patchSize = 0 is reserved for the cases when one MEAN for the entire
		// image is requested.
		int pSize = (this.patchSize.getSize() == 0) ? imageH : patchSize.getSize();

		// Check basic requirements
		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Image must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Image's width must be eqaul to its height!");

		tContrasts = new double[imageH / pSize][imageW / pSize];
		// Iterate over the image (patch by patch)
		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {
				// Get a patch from the image
				flatPatch = MatrixUtil.getSubMatrixAsArray(image, row * pSize, col * pSize, pSize);
				// Calculate standard deviation for this patch
				sd = smile.math.Math.sd(flatPatch);
				// Calculate kurtosis for this patch.
				Kurtosis m4 = new Kurtosis();
				kurtosis = m4.evaluate(flatPatch) + epsilon; // epsilon: to avoid zero in the denominator

				// TContrast = (sd ^ 2)/(kurtosis ^ 0.25)
				tContrasts[row][col] = Math.pow(sd, 2) / Math.pow((kurtosis), _1_4th);
				if (Double.isNaN(tContrasts[row][col])) {
					tContrasts[row][col] = 0;
				}
			}
		}

		return tContrasts;
	}
}