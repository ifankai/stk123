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
package edu.gsu.cs.dmlab.imageproc.edgedetection;

import java.util.Arrays;

import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;
import edu.gsu.cs.dmlab.imageproc.interfaces.IEdgeDetector;
import smile.math.Math;

/**
 * <p>
 * <em>This class was originally implemented by <b>Tom Gibara</b>, which can be
 * found in
 * <a href= "http://www.tomgibara.com/computer-vision/CannyEdgeDetector.java">
 * CannyEdgeDetector.java</a>. In addition, the wiki page of <a href=
 * "https://en.wikipedia.org/wiki/Canny_edge_detector#Development_of_the_Canny_algorithm">
 * Canny Edge Detector</a> can also be useful. <br>
 *
 * <p>
 * This class provides a configurable implementation of the Canny edge detection
 * algorithm. This classic algorithm has a number of shortcomings, but remains
 * an effective tool in many scenarios.
 * </p>
 *
 *
 * @author Tom Gibara, modified by Azim Ahmadzadeh and Dustin Kempton, Data
 *         Mining Lab, Georgia State University
 * 
 */
public class CannyEdgeDetector implements IEdgeDetector {

	private final static double GAUSSIAN_CUT_OFF = 0.005f;
	private final static double MAGNITUDE_SCALE = 100F;
	private final static double MAGNITUDE_LIMIT = 1000F;
	private final static double MAGNITUDE_MAX = (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);

	private double gaussianKernelRadius;
	private double lowThreshold;
	private double highThreshold;
	private int gaussianKernelWidth;
	private boolean contrastNormalized;
	private double[] finalMagnitude;// TODO; remove this field after experiment

	/**
	 * Constructor of a new detector object with the passed in parameters.
	 * 
	 * @param lowThreshold         The low threshold for hysteresis. Suitable values
	 *                             for this parameter must be determined
	 *                             experimentally for each application. It is
	 *                             nonsensical (though not prohibited) for this
	 *                             value to exceed the high threshold value.<br>
	 *                             <i>Suggested value: 2.5f</i>
	 * @param highThreshold        The high threshold for hysteresis. Suitable
	 *                             values for this parameter must be determined
	 *                             experimentally for each application. It is
	 *                             nonsensical (though not prohibited) for this
	 *                             value to be less than the low threshold
	 *                             value.<br>
	 *                             <i>Suggested value: 7.5f</i>
	 * @param gaussianKernelRadius The radius of the Gaussian convolution kernel
	 *                             used to smooth the source image prior to gradient
	 *                             calculation.<br>
	 *                             <i>Suggested value: 2f</i>
	 * @param gaussianKernelWidth  The number of pixels across which the Gaussian
	 *                             kernel is applied. This implementation will
	 *                             reduce the radius if the contribution of pixel
	 *                             values is deemed negligible, so this is actually
	 *                             a maximum radius.<br>
	 *                             <i>Suggested value: 16</i>
	 * @param contrastNormalized   Whether the luminance data extracted from the
	 *                             source image is normalized by linearizing its
	 *                             histogram prior to edge extraction. <i>Suggested
	 *                             value: false</i> <br>
	 *                             <b>note:</b> Keep
	 *                             <code>contrastNormalized = false</code> since
	 *                             this part is not implemented.
	 */

	public CannyEdgeDetector(double lowThreshold, double highThreshold, double gaussianKernelRadius,
			int gaussianKernelWidth, boolean contrastNormalized) {

		if (lowThreshold < 0)
			throw new IllegalArgumentException();
		if (highThreshold < 0)
			throw new IllegalArgumentException();
		if (gaussianKernelWidth < 2)
			throw new IllegalArgumentException();

		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.gaussianKernelRadius = gaussianKernelRadius;
		this.gaussianKernelWidth = gaussianKernelWidth;
		this.contrastNormalized = contrastNormalized;
	}

	@Override
	public double[][] getEdges(double[][] sourceImage, double[] colors) {

		int height = sourceImage.length;
		int width = sourceImage[0].length;
		int picsize = width * height;

		double[] data = new double[picsize];
		double[] magnitude = new double[picsize];

		double[] xConv = new double[picsize];
		double[] yConv = new double[picsize];
		double[] xGradient = new double[picsize];
		double[] yGradient = new double[picsize];

		data = Arrays.stream(sourceImage).flatMapToDouble(Arrays::stream).toArray();

		if (this.contrastNormalized)
			normalizeContrast(picsize, data);

		this.computeGradients(width, height, data, yConv, xConv, xGradient, yGradient, magnitude);

		int low = (int) Math.round(this.lowThreshold * MAGNITUDE_SCALE);
		int high = (int) Math.round(this.highThreshold * MAGNITUDE_SCALE);

		this.performHysteresis(low, high, height, width, data, magnitude);

		this.thresholdEdges(picsize, data, colors);

		double[][] result = MatrixUtil.convertTo2DArray(data, width, height);

		return result;
	}

	/*
	 * NOTE: The elements of the method below (specifically the technique for
	 * non-maximal suppression and the technique for gradient computation) are
	 * derived from an implementation posted in the following forum (with the clear
	 * intent of others using the code):
	 * http://forum.java.sun.com/thread.jspa?threadID=546211&start=45&tstart=0 My
	 * code effectively mimics the algorithm exhibited above. Since I don't know the
	 * providence of the code that was posted it is a possibility (though I think a
	 * very remote one) that this code violates someone's intellectual property
	 * rights. If this concerns you feel free to contact me for an alternative,
	 * though less efficient, implementation.
	 */
	private void computeGradients(int width, int height, double[] data, double[] yConv, double[] xConv,
			double[] xGradient, double[] yGradient, double[] magnitude) {

		// generate the gaussian convolution masks
		double kernel[] = new double[this.gaussianKernelWidth];
		double diffKernel[] = new double[this.gaussianKernelWidth];
		int kwidth;
		for (kwidth = 0; kwidth < this.gaussianKernelWidth; kwidth++) {
			double g1 = gaussian(kwidth, this.gaussianKernelRadius);
			if (g1 <= GAUSSIAN_CUT_OFF && kwidth >= 2)
				break;
			double g2 = gaussian(kwidth - 0.5f, this.gaussianKernelRadius);
			double g3 = gaussian(kwidth + 0.5f, this.gaussianKernelRadius);
			kernel[kwidth] = (g1 + g2 + g3) / 3f
					/ (2f * (double) Math.PI * this.gaussianKernelRadius * this.gaussianKernelRadius);
			diffKernel[kwidth] = g3 - g2;
		}

		int initX = kwidth - 1;
		int maxX = width - (kwidth - 1);
		int initY = width * (kwidth - 1);
		int maxY = width * (height - (kwidth - 1));

		// perform convolution in x and y directions
		for (int x = initX; x < maxX; x++) {
			for (int y = initY; y < maxY; y += width) {

				int index = x + y;
				double sumX = data[index] * kernel[0];
				double sumY = sumX;
				int xOffset = 1;
				int yOffset = width;
				for (; xOffset < kwidth;) {
					sumY += kernel[xOffset] * (data[index - yOffset] + data[index + yOffset]);
					sumX += kernel[xOffset] * (data[index - xOffset] + data[index + xOffset]);
					yOffset += width;
					xOffset++;
				}

				yConv[index] = sumY;
				xConv[index] = sumX;
			}

		}

		for (int x = initX; x < maxX; x++) {
			for (int y = initY; y < maxY; y += width) {
				double sum = 0f;
				int index = x + y;
				for (int i = 1; i < kwidth; i++)
					sum += diffKernel[i] * (yConv[index - i] - yConv[index + i]);

				xGradient[index] = sum;
			}
		}

		for (int x = kwidth; x < width - kwidth; x++) {
			for (int y = initY; y < maxY; y += width) {
				double sum = 0.0f;
				int index = x + y;
				int yOffset = width;
				for (int i = 1; i < kwidth; i++) {
					sum += diffKernel[i] * (xConv[index - yOffset] - xConv[index + yOffset]);
					yOffset += width;
				}

				yGradient[index] = sum;
			}
		}

		initX = kwidth;
		maxX = width - kwidth;
		initY = width * kwidth;
		maxY = width * (height - kwidth);
		for (int x = initX; x < maxX; x++) {
			for (int y = initY; y < maxY; y += width) {
				int index = x + y;
				int indexN = index - width;
				int indexS = index + width;
				int indexW = index - 1;
				int indexE = index + 1;
				int indexNW = indexN - 1;
				int indexNE = indexN + 1;
				int indexSW = indexS - 1;
				int indexSE = indexS + 1;

				double xGrad = xGradient[index];
				double yGrad = yGradient[index];
				double gradMag = Math.hypot(xGrad, yGrad);

				// perform non-maximal suppression
				double nMag = Math.hypot(xGradient[indexN], yGradient[indexN]);
				double sMag = Math.hypot(xGradient[indexS], yGradient[indexS]);
				double wMag = Math.hypot(xGradient[indexW], yGradient[indexW]);
				double eMag = Math.hypot(xGradient[indexE], yGradient[indexE]);
				double neMag = Math.hypot(xGradient[indexNE], yGradient[indexNE]);
				double seMag = Math.hypot(xGradient[indexSE], yGradient[indexSE]);
				double swMag = Math.hypot(xGradient[indexSW], yGradient[indexSW]);
				double nwMag = Math.hypot(xGradient[indexNW], yGradient[indexNW]);
				double tmp;
				/*
				 * An explanation of what's happening here, for those who want to understand the
				 * source: This performs the "non-maximal suppression" phase of the Canny edge
				 * detection in which we need to compare the gradient magnitude to that in the
				 * direction of the gradient; only if the value is a local maximum do we
				 * consider the point as an edge candidate.
				 * 
				 * We need to break the comparison into a number of different cases depending on
				 * the gradient direction so that the appropriate values can be used. To avoid
				 * computing the gradient direction, we use two simple comparisons: first we
				 * check that the partial derivatives have the same sign (1) and then we check
				 * which is larger (2). As a consequence, we have reduced the problem to one of
				 * four identical cases that each test the central gradient magnitude against
				 * the values at two points with 'identical support'; what this means is that
				 * the geometry required to accurately interpolate the magnitude of gradient
				 * function at those points has an identical geometry (upto
				 * right-angled-rotation/reflection).
				 * 
				 * When comparing the central gradient to the two interpolated values, we avoid
				 * performing any divisions by multiplying both sides of each inequality by the
				 * greater of the two partial derivatives. The common comparand is stored in a
				 * temporary variable (3) and reused in the mirror case (4).
				 * 
				 */
				if (xGrad * yGrad <= (double) 0 /* (1) */
						? Math.abs(xGrad) >= Math.abs(yGrad) /* (2) */
								? (tmp = Math.abs(xGrad * gradMag)) >= Math
										.abs(yGrad * neMag - (xGrad + yGrad) * eMag) /* (3) */
										&& tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag) /* (4) */
								: (tmp = Math.abs(yGrad * gradMag)) >= Math
										.abs(xGrad * neMag - (yGrad + xGrad) * nMag) /* (3) */
										&& tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag) /* (4) */
						: Math.abs(xGrad) >= Math.abs(yGrad) /* (2) */
								? (tmp = Math.abs(xGrad * gradMag)) >= Math
										.abs(yGrad * seMag + (xGrad - yGrad) * eMag) /* (3) */
										&& tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag) /* (4) */
								: (tmp = Math.abs(yGrad * gradMag)) >= Math
										.abs(xGrad * seMag + (yGrad - xGrad) * sMag) /* (3) */
										&& tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag) /* (4) */
				) {
					magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);
					// NOTE: The orientation of the edge is not employed by this
					// implementation. It is a simple matter to compute it at
					// this point as: Math.atan2(yGrad, xGrad);
				} else {
					magnitude[index] = 0;
				}
			}
		}

		this.finalMagnitude = magnitude;
	}

	private double gaussian(double x, double sigma) {
		return Math.exp(-(x * x) / (2f * sigma * sigma));
	}

	private void performHysteresis(int low, int high, int height, int width, double[] data, double[] magnitude) {
		// NOTE: this implementation reuses the data array to store both
		// luminance data from the image, and edge intensity from the
		// processing.
		// This is done for memory efficiency, other implementations may wish
		// to separate these functions.
		Arrays.fill(data, 0);

		int offset = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (data[offset] == 0.0 && magnitude[offset] >= high) {
					follow(x, y, offset, low, width, height, data, magnitude);
				}
				offset++;
			}
		}
	}

	private void follow(int x1, int y1, int i1, int threshold, int width, int height, double[] data,
			double[] magnitude) {
		int x0 = x1 == 0 ? x1 : x1 - 1;
		int x2 = x1 == width - 1 ? x1 : x1 + 1;
		int y0 = y1 == 0 ? y1 : y1 - 1;
		int y2 = y1 == height - 1 ? y1 : y1 + 1;

		data[i1] = magnitude[i1];
		for (int x = x0; x <= x2; x++) {
			for (int y = y0; y <= y2; y++) {
				int i2 = x + y * width;
				if ((y != y1 || x != x1) && data[i2] == 0 && magnitude[i2] >= threshold) {
					follow(x, y, i2, threshold, width, height, data, magnitude);
					return;
				}
			}
		}
	}

	/**
	 * It replaces any zero values with <code>cols[0]</code>, and any non-zero
	 * positive values with <code>cols[1]</code>.
	 * 
	 * @param picsize the size of the image in a 1D array.
	 * @param data    the background (i.e., <code>cols[0]</code>) and foreground
	 *                (i.e., <code>cols[1]</code>) color intensities used for
	 *                showing the detected edges.
	 */
	public void thresholdEdges(int picsize, double[] data, double[] cols) {

		double background = cols[0];
		double foreground = cols[1];
		for (int i = 0; i < picsize; i++) {
			// data[i] = data[i] > 0 ? -1 : 0xff000000;
			// data[i] = data[i] > 0 ? 255 : 0;
			data[i] = data[i] > 0 ? foreground : background;
		}
	}

	private void normalizeContrast(int picsize, double[] data) {

		/*
		 * With new assumptions, this is not easy to change. So, I am ignoring this for
		 * now, since previously we set contrastNormalized = false, and therefore we
		 * never called this function.
		 */
	}

	public double[] getFinalMagnitude() {
		return this.finalMagnitude;
	}

}