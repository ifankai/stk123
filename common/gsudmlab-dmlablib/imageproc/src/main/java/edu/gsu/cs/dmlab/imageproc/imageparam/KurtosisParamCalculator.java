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
 * This class is designed to compute the <b>kurtosises</b> of each patch of the
 * given <code>2D array</code>, based on the following formula:<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; Mu_4 = SUM{((z_i - m)^4) * p(z_i)} <br>
 * <br>
 * or to be precise, using <a href=
 * "https://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/stat/descriptive/moment/Kurtosis.html">Kurtosis</a>
 * in Apache library: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; { [n(n+1) / (n -1)(n - 2)(n-3)] SUM[(x_i - m)^4] /
 * std^4 } - [3(n-1)^2 / (n-2)(n-3)] <br>
 * <br>
 * where:
 * <UL>
 * <LI>p: the histogram of this patch.
 * <LI>z_i: the intensity value of the i-th pixel in this patch.
 * <LI>p(z_i): The frequency of the intensity z_i in the histogram of this patch
 * whose kurtosis parameter is being calculated.
 * <LI>m: the mean intensity value of this patch.
 * </UL>
 * 
 * <br>
 * <b>Note:</b> For each patch, the histogram of that particular patch is used
 * in the formula, and NOT the histogram of the entire image.<br>
 *
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class KurtosisParamCalculator extends BaseMeanCalculator implements IParamCalculator {

	IMeasures.PatchSize patchSize;

	public KurtosisParamCalculator(IMeasures.PatchSize patchSize) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in SkewnessParamCalculator");

		this.patchSize = patchSize;
	}

	@Override
	public double[][] calculateParameter(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		double[][] kurtosises = null;
		double[] flatPatch = null;
		Kurtosis m4 = new Kurtosis();

		int imageH = image.length;
		int imageW = image[0].length;

		// patchSize = 0 is reserved for the cases when one MEAN for the entire image is
		// requested.
		int pSize = (this.patchSize.getSize() == 0) ? imageH : patchSize.getSize();

		// Check basic requirements
		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Matrix must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Matrix's width must be eqaul to its height!");

		kurtosises = new double[imageH / pSize][imageW / pSize];

		// Iterate over the image (patch by patch)
		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {
				// Get a patch from the image
				flatPatch = MatrixUtil.getSubMatrixAsArray(image, row * pSize, col * pSize, pSize);

				/*-
				 * Compute forth moment (kurtosis) using Apache library:
				 * 'org.apache.commons.math3.stat.descriptive.moment.Kurtosis'
				 * the formula used in this library is:
				 * 	 m4 = { [n(n+1) / (n -1)(n - 2)(n-3)] sum[(x_i - mean)^4] / std^4 } - [3(n-1)^2 / (n-2)(n-3)]
				 */
				kurtosises[row][col] = m4.evaluate(flatPatch);
				if (Double.isNaN(kurtosises[row][col])) {
					kurtosises[row][col] = 0;
				}
			}
		}
		return kurtosises;
	}

}