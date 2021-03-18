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

import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;

/**
 * 
 * This class is designed to compute the <b>standard deviation</b> of each patch
 * of the given <code>2D array</code>, based on the following formula:<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; &sigma; = sqrt{(1/(L-1)) SUM{(z_i - m)^2}} <br>
 * <br>
 * where:
 * <UL>
 * <LI>L: the total number of pixels in this patch.
 * <LI>z_i: the intensity value of the i-th pixel in this patch.
 * <LI>m: the mean value corresponding to this patch.
 * </UL>
 *
 *
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class StdDeviationParamCalculator extends BaseMeanCalculator implements IParamCalculator {

	IMeasures.PatchSize patchSize;

	public StdDeviationParamCalculator(IMeasures.PatchSize patchSize) {
		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in StdDeviationParamCalculator");

		this.patchSize = patchSize;
	}

	@Override
	public double[][] calculateParameter(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		double[][] stdDeviations = null;
		double[] flatPatch = null;

		int imageH = image.length;
		int imageW = image[0].length;

		// patchSize = 0 is reserved for the cases when one MEAN for the entire image is
		// requested.
		int pSize = (this.patchSize.getSize() == 0) ? imageH : patchSize.getSize();

		// Check basic requirements
		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Image must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Image's width must be eqaul to its height!");

		stdDeviations = new double[imageH / pSize][imageW / pSize];

		// Iterate over the image (patch by patch)
		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {

				// Get a patch from the image
				flatPatch = MatrixUtil.getSubMatrixAsArray(image, row * pSize, col * pSize, pSize);
				// Compute Standard Deviation of this patch
				stdDeviations[row][col] = smile.math.Math.sd(flatPatch);
				if (Double.isNaN(stdDeviations[row][col])) {
					stdDeviations[row][col] = 0;
				}
			}
		}

		return stdDeviations;
	}
}