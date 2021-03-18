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

import java.util.Collections;
import java.util.List;

import edu.gsu.cs.dmlab.datatypes.Gradient;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;
import edu.gsu.cs.dmlab.imageproc.interfaces.IGradientCalculator;
import edu.gsu.cs.dmlab.imageproc.interfaces.IPeakDetector;
import smile.math.Histogram;
import smile.math.Math;

/**
 * 
 * This class is designed to compute <b>Tamura Directionality</b> parameter of
 * each patch of the given <code>BufferedImage</code>, based on a MatLab
 * implementation which is copied below:<br>
 * <br>
 * <code>
 *	function Fdir = Tamura_Directionality(Im),	<br>
 *	[gx,gy] = gradient(Im);						<br>
 *	[t,r] = cart2pol(gx,gy);					<br>
 *	nbins = 125;								<br>
 *	r(r&lt;.15.*max(r(:))) = 0;					<br>
 *	t0 = t;										<br>
 *	t0(abs(r)&lt;1e-4) = 0;						<br>
 *	r = r(:)';									<br>
 *	t0 = t0(:)';								<br>
 *	Hd = hist(t0,nbins);						<br>
 *	nrm = hist(r(:).^2 + t0(:).^2, nbins);		<br>
 *	fmx = find(Hd==max(Hd));					<br>
 *	ff  = 1:length(Hd);							<br>
 *	fmxNew = ones(size(ff)) .* fmx; %added by me<br>
 *	%ff2 = (ff - fmx).^2;						<br>
 *	ff2  = (ff - fmxNew).^2;		%added by me<br>
 *	Fdir = sum(Hd.*ff2)./sum(nrm);				<br>
 *	Fdir = abs(log(Fdir+eps));					<br>
 *	return;										<br>
 *	</code> <br>
 * <br>
 * <b>Note:</b> Unlike what was assumed in the several implementations of this
 * parameter, the histogram of the images after having the Gradient filter on,
 * does not have one single prominent peak. The histogram seems to be almost
 * exactly symmetric (when all angles from -PI to PI are considered) with at
 * least 5 clear peaks (for 125 bins). Therefore, instead of finding the global
 * max in the histogram, all peaks (local max) are identified and their
 * corresponding index-weights are constructed to have a correct implementation
 * of Directionality parameter.
 * 
 * <br>
 * <br>
 * For further investigation the following sources are recommended:
 * <UL>
 * <LI>Original paper: "Textural Features Corresponding Visual Perception" by
 * <i>Hideyuki Tamura, Shunji Mori, Takashi Yamawaki</i>
 * <LI>Another Matlab implementation: <a href=
 * "https://github.com/Sdhir/TamuraFeatures/blob/master/Tamura.m">Tamura.m</a>
 * <LI>A C++ implementation: NA
 * <LI>Good explanation: <a href=
 * "https://www.cs.auckland.ac.nz/courses/compsci708s1c/lectures/Glect-html/topic4c708FSC.htm">CBIR</a>
 * </UL>
 * 
 * <br>
 * <b>Note:</b> Implementation of <i>Gradient</i> is based on the Matlab
 * function, see: <a href=
 * "https://www.mathworks.com/help/matlab/ref/gradient.html">gradient</a>. <br>
 * 
 * <b>Note:</b> To obtain a <i>histogram</i> of an array, the
 * <code>Histogram</code> class from the <code>Smile</code> package is used:
 * <a href=
 * "http://haifengl.github.io/smile/api/java/index.html?smile/math/Histogram.html">Histogram</a>.<br>
 * 
 * This method (<code>Histogram.histogram</code>) returns a 3-by-k matrix whose
 * last row contains the frequencies. The first row is the lower bound of bins,
 * the second row is the upper bound of bins. <br>
 * <br>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 */
public class TDirectionalityParamCalculator implements IParamCalculator {

	final static double radiusThresholdPercentage = 0.15;
	final static double epsilon = 2.2204e-16;
	final static double insignificantRadius = 1e-4;

	IGradientCalculator gradientCalculator;
	IPeakDetector peakDetector;
	IMeasures.PatchSize patchSize;
	int quantizationLevel;

	/**
	 * 
	 * @param patchSize
	 * @param gradientCalculator
	 *            Calculator for gradient of pixel values in the image being
	 *            processed.
	 * @param peakDetector
	 *            The class that finds local maxima in arrays
	 * @param quantizationLevel
	 *            Quantization level for the continuous spectrum of the angles. this
	 *            is the number of bins in the histogram of the angles. (See the
	 *            formula for Directionality)
	 */
	public TDirectionalityParamCalculator(IMeasures.PatchSize patchSize, IGradientCalculator gradientCalculator,
			IPeakDetector peakDetector, int quantizationLevel) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in TDirectionalityParamCalculator");
		if (gradientCalculator == null)
			throw new IllegalArgumentException("GradientCalculator cannot be null in TDirectionalityParamCalculator");
		if (peakDetector == null)
			throw new IllegalArgumentException("PeakDetector cannot be null in TDirectionalityParamCalculator");
		if (quantizationLevel <= 0)
			throw new IllegalArgumentException(
					"QuantizationLevel cannot be zero or negative in TDirectionalityParamCalculator");

		this.patchSize = patchSize;
		this.quantizationLevel = quantizationLevel;
		this.gradientCalculator = gradientCalculator;
		this.peakDetector = peakDetector;

	}

	@Override
	public double[][] calculateParameter(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		double[][] thisPatch = null;
		double[][] tDirectionalities = null;
		int imageH = image.length;
		int imageW = image[0].length;

		// patchSize = 0 is reserved for the cases when one TDirectionality for the
		// entire
		// image is requested.
		int pSize = (this.patchSize.getSize() == 0) ? imageH : patchSize.getSize();

		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Matrix must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Matrix's width must be eqaul to its height!");

		tDirectionalities = new double[imageH / pSize][imageW / pSize];

		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {
				// Get a patch from the image
				thisPatch = MatrixUtil.getSubMatrixAsMatrix(image, row * pSize, col * pSize, pSize);
				tDirectionalities[row][col] = computeDirectionalityForPatch(thisPatch);
			}
		}

		return tDirectionalities;
	}

	/**
	 * The main idea of Tamura Directionality is implemented in this method which is
	 * being called for each patch, however, its architecture is independent of
	 * having the original image divided into patches.<br>
	 * <br>
	 * For details, see the class documentation.<br>
	 * <br>
	 * 
	 * @param image
	 *            the given image (in this case, the given patch of image) for which
	 *            the Directionality parameter is needed to be computed.
	 * 
	 * @return a single double value representing the Directionality of the given
	 *         image/patch.
	 */
	private double computeDirectionalityForPatch(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		double fDir = 0.0;
		double radiusThreshold = 0.0;

		// Compute the gradient of 'bImage' and convert it to Polar cord.
		// system.
		Gradient gradient = this.gradientCalculator.calculateGradientPolar(image);

		// Use the 1-D (flat) version of the gradients
		double[] tFlat = gradient.gx_f;
		double[] rFlat = gradient.gy_f;

		// Clean data in 'rFlat' (set all radii below the threshold to zero)
		radiusThreshold = radiusThresholdPercentage * Math.max(rFlat);
		for (int i = 0; i < rFlat.length; i++) {
			if (rFlat[i] < radiusThreshold)
				rFlat[i] = 0;
		}

		// Clean data in 'tFlat'
		for (int i = 0; i < tFlat.length; i++) {
			if (Math.abs(rFlat[i]) < insignificantRadius)
				tFlat[i] = 0;
		}

		// Note: Values of tFlar varies from -3.14 (-PI) to 3.14 (PI). To avoid
		// repetition
		// in the histogram of angles (hist_t), we build the histogram based on the
		// breaks
		// which is initialized by numbers from 0 (not -PI) to 3.14 (PI), in
		// 'quantizationLevel'
		// steps.

		int lastRow = 2;
		// Compute the histogram of all angles ('t')
		double[] breaks = Histogram.breaks(0, Math.PI, quantizationLevel);
		double[][] tempHist = Histogram.histogram(tFlat, breaks);
		// Resize the first bin, since this bin is always disproportionately larger than
		// others, and it represents the texture-less regions.
		double[] hist_t = tempHist[lastRow];
		hist_t[0] = (int) (hist_t[0] / 100);

		// Find indices of peaks in hist_t (This is denoted by 'Phi' in the formula)
		List<Integer> peaksIndex = this.peakDetector.findPeaks(hist_t);

		// findPeaks will give us the index of peaks sorted by their height, but
		// we want them to be sorted by the value of their index
		Collections.sort(peaksIndex);

		int numberOfPeaks = peaksIndex.size();

		// If all elements of 'hist_t' are zero, then there will be no peaks found
		if (numberOfPeaks == 0) {
			fDir = 0;
			return fDir;
		}
		// Find the middle points
		// Get the index of the middle points on the x axis (:break).
		double[] middlePoints = findMiddlePoints(0, peaksIndex, breaks.length - 1);

		int from = 0, to = 0;
		int[] inc = null;
		int thisPeakIndex = 0;
		double innerSum = 0.0;
		double outerSum = 0.0;
		// Compute the double-sum of the Directionality formula
		for (int i = 0; i < numberOfPeaks; i++) {
			// 1. Get the interval
			from = (int) middlePoints[i];
			to = (int) middlePoints[i + 1];
			// 2. Create the incremental array for this interval
			inc = incrementalArray(from, to);
			// 3. Using the incremental array and the index of peak, compute
			// the weights
			innerSum = 0;
			thisPeakIndex = peaksIndex.get(i);
			for (int j = 0; j < inc.length - 1; j++) {
				innerSum += Math.pow(breaks[inc[j]] - breaks[thisPeakIndex], 2) * hist_t[inc[j]];
			}
			outerSum += innerSum;
		}

		fDir = numberOfPeaks * outerSum;

		// Normalization related to quantizing level
		// (Not explained how to find the normalization factor) A reasonable
		// normalization requires
		// the max value of the outerSum, which is not easy to get!
		double normVal = Math.sum(hist_t);
		fDir = fDir / normVal;

		// TODO: Final subtraction from 1 is commented out since the normalization is
		// not done properly, and this final operation does not play any role in the
		// classification model. If we can normalize the value of fDir, then
		// "max(fDir) - fDir" should be replaced with fDir to ensure that the higher
		// values of fDir corresponds to higher degree of directionality.

		// fDir = 1 - fDir;

		return fDir;
	}

	/**
	 * This method finds all the middle points between the elements of the given
	 * array.
	 * 
	 * @param startingPoint
	 *            the starting point of the output array
	 * @param array
	 *            the given array whose middle points are requested.
	 * @param endingPoint
	 *            the ending point of the output array
	 * @return an array of the middle points, including the starting and ending
	 *         points.
	 * 
	 */
	private double[] findMiddlePoints(double startingPoint, List<Integer> array, double endingPoint) {

		double[] points = new double[array.size() + 1];
		int firstIndex = 0;
		int lastIndex = points.length - 1;

		if (array.size() == 0) {
			throw new IllegalArgumentException("The given colleciton of integers cannot be empty!");
		}
		if (startingPoint > array.get(0) || endingPoint < array.get(array.size() - 1)) {
			throw new IllegalArgumentException("Either StartingPoint or EndingPoint is not correct!");
		}

		// first point is 'startingPoint'
		points[firstIndex] = startingPoint;

		for (int i = 1; i < points.length - 1; i++) {

			points[i] = (array.get(i - 1) + array.get(i)) / 2;
		}
		// last point is 'endingPoint'
		points[lastIndex] = endingPoint;

		return points;
	}

	/**
	 * This method generates all the integers between <code>first</code> and
	 * <code>last</code>, inclusive.
	 * 
	 * @param first
	 *            the first element of the requested array.
	 * @param last
	 *            the last element of the requested array.
	 * @return an array of the requested integers.
	 */
	private int[] incrementalArray(int first, int last) {

		if (first >= last)
			return null;

		int[] results = new int[(int) Math.floor(last - first) + 1];
		for (int i = 0; i < results.length; i++) {
			results[i] = first + i;
		}
		return results;
	}

}