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
import edu.gsu.cs.dmlab.imageproc.imageparam.util.NumberOfBinsGenerator;
import smile.math.Histogram;

/**
 * This class is designed to compute the <b>entropy</b> of each patch of the
 * given <code>BufferedImage</code>, based on the following formula:<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; E = - SUM{p(z_i)* log_2(p(z_i))} <br>
 * <br>
 * where:
 * <UL>
 * <LI>p: the histogram of this patch.
 * <LI>z_i: the intensity value of the i-th pixel in this patch.
 * <LI>p(z_i): The frequency of the intensity z_i in the histogram of this patch
 * whose entropy parameter is being calculated.
 * </UL>
 * <br>
 * This formula was given in several other literature such as:<br>
 * <UL>
 * <LI>Digital Image processing, 3rd Ed, Addison Wesley, {page 513}
 * <LI>http://math.ucr.edu/home/baez/information_loss.pdf
 * </UL>
 * 
 * <b>Note:</b> The convention in Shannon Entropy: 0*log(0) = 0 <br>
 * <b>Note:</b> For each patch, the histogram of that particular patch is used
 * in the formula, and not the histogram of the entire image.<br>
 *
 * <b>Note:</b> For comparison of results, Entropy can be computed using the
 * following script in <code>R</code>:<br>
 * <br>
 * <code>library(CulturalAnalytics)</code><br>
 * <code> library(jpeg) </code><br>
 * <code> img &lt;- readJPEG('CODES/DMLab_git/dmlablib/sdo.jpg') </code><br>
 * <code> histogram &lt;- hist(imageToIntensity(img), breaks=90, plot=FALSE) </code>
 * <code> entropy &lt;- imageEntropy(histogram) </code> <br>
 * <br>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class EntropyParamCalculator implements IParamCalculator {

	IMeasures.PatchSize patchSize;
	final static double epsilon = 1e-32;

	/*
	 * This is either the actual number of bins, or the index representing it.
	 */
	int binsInfo;

	/* This is a scalar to allow thinner bins (thinner than 1 unit). */
	int factor;

	/* This flag indicates whether or not an index is used. */
	boolean indexUsed = false;

	double minPixelVal;
	double maxPixelVal;

	/**
	 * @param patchSize
	 *            size of each patch of the image the parameter should be calculated
	 *            on.
	 * @param numberOfBins
	 *            is used to get the histogram of colors based on which the
	 *            probability should be calculated. A good choice could be one tenth
	 *            of the sample size (i.e., 0.01*(maxPixelVal - minPixelVal).
	 * @param minPixelValue
	 *            is the minimum intensity value of a pixel. For JPEG formats, it is
	 *            0, and for FITS files it is also 0, given that the data is cleaned
	 *            and negative noises are removed.
	 * @param maxPixelValue
	 *            is the maximum intensity value of a pixel. For JPEG formats, it is
	 *            255, and for FITS files it is 16383 (2^14). <br>
	 * 
	 *            <br>
	 *            <b>Note:</b> In case different mins and maxes are needed for
	 *            different wavebands, use the provided setters where the method
	 *            <code>calculateParameter</code> is called. If this is the case,
	 *            Use the other constructor which uses the index of nOfBins instead
	 *            of nOfBins.
	 */
	public EntropyParamCalculator(IMeasures.PatchSize patchSize, int numberOfBins, double minPixelValue,
			double maxPixelValue) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in EntropyParamCalculator");
		if (minPixelValue >= maxPixelValue)
			throw new IllegalArgumentException("minPixelValue cannot be greater than or equal to maxPixelvalue!");

		this.patchSize = patchSize;
		this.minPixelVal = minPixelValue;
		this.maxPixelVal = maxPixelValue;
		this.binsInfo = numberOfBins;
		this.factor = 1; // ineffective
		this.indexUsed = false;
	}

	/**
	 * The second constructor to be used when instead of the actual nOfBins, their
	 * index is provided. This is needed for the cases when generic nOfBins are
	 * needed.
	 * 
	 * @param patchSize
	 *            size of each patch of the image the parameter should be calculated
	 *            on.
	 * @param indexOfNumberOfBins
	 *            For the cases where the number of bins depends on the waveband,
	 *            this argument allows to assign the number of bins in a generic
	 *            way. This requires a prior knowledge about the minPixelVal and
	 *            maxPixelVal. This can be achieved by calling the provided setters
	 *            where the method <code>calculateParameter</code> should be called.
	 * @param factor
	 *            is a scalar by which the bin width of the color histogram will be
	 *            divided. This is done by multiplying the max color intensity when
	 *            nOfBins is to be generated (see,
	 *            <code>findNOfBinsForIndex_Bounded</code> in this method). In other
	 *            words, with factor = 1 for min=0 and max=44, the number of bins
	 *            that will be tried are {2,4,6, ..., 44} while for factor=10, for
	 *            the same range, the set will be changed to {20, 41, ..., 440}. Set
	 *            this to 1, if it should be ineffective.
	 */
	public EntropyParamCalculator(IMeasures.PatchSize patchSize, int indexOfNumberOfBins, int factor) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in EntropyParamCalculator");

		this.patchSize = patchSize;
		this.binsInfo = indexOfNumberOfBins;
		this.minPixelVal = 0;
		this.maxPixelVal = 0;
		this.factor = factor;
		this.indexUsed = true;
	}

	/**
	 * This method computes the <b>entropy</b> for each patch of the given image in
	 * the form of a 2D array. <br>
	 * <b>Note:</b> See the definition of entropy {@link EntropyParamCalculator}
	 */
	@Override
	public double[][] calculateParameter(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		int nOfBins = 0;
		double[][] entropies = null;
		double[][] histogramResult = null;
		double[] hist = null;
		double[] flatPatch = null;
		double[] breaks = null;

		double totalNOfPixelsInThisPatch = 0.0;
		double sum = 0.0;
		double probability = 0.0;

		int imageH = image.length;
		int imageW = image[0].length;

		/*
		 * patchSize = 0 is reserved for the cases when one value of entropy for the
		 * entire image is requested.
		 */
		int pSize = (this.patchSize.getSize() == 0) ? imageH : patchSize.getSize();

		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Image must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Image's width must be eqaul to its height!");

		totalNOfPixelsInThisPatch = pSize * pSize;
		entropies = new double[imageH / pSize][imageW / pSize];

		// Iterate over the image (patch by patch)
		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {
				sum = 0;
				flatPatch = MatrixUtil.getSubMatrixAsArray(image, row * pSize, col * pSize, pSize);

				if (!this.indexUsed) {
					nOfBins = this.binsInfo;
				} else {
					nOfBins = NumberOfBinsGenerator.findNOfBinsForIndex_Bounded(this.minPixelVal,
							this.maxPixelVal * this.factor, this.binsInfo);
				}

				breaks = Histogram.breaks(this.minPixelVal, this.maxPixelVal, nOfBins);
				histogramResult = smile.math.Histogram.histogram(flatPatch, breaks);
				// histogramResult: [3 X nOfBins]
				hist = histogramResult[2];

				// Iterate over the histogram of flatPatch (0 to nOfBins)
				// Entropy = - SUM {p(z_i) * log_2(p(z_i))}
				for (int p = 0; p < hist.length; p++) {

					probability = (double) hist[p] / totalNOfPixelsInThisPatch;
					sum += probability * (Math.log(probability + epsilon) / Math.log(2));
					// epsilon: to avoid log(0) in the numerator
				}
				entropies[row][col] = 0 - sum;
			}
		}
		return entropies;
	}

	public void setMinPixelVal(double minPixelVal) {
		this.minPixelVal = minPixelVal;
	}

	public void setMaxPixelVal(double maxPixelVal) {
		this.maxPixelVal = maxPixelVal;
	}

}