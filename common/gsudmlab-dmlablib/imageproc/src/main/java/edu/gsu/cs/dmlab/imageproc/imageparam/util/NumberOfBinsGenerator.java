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

import smile.math.Histogram;

/**
 * <p>
 * This class is designed to assist approaching a specific problem. To
 * experiment on the performance of some parameters (such as Entropy and
 * Uniformity), we would need to decide what domain for the argument
 * <code>nOfBins</code> (i.e., number of bins) should those parameters be tested
 * on. For JP2, the decision is simple. A pre-defined set of nOfBins could be
 * used across different wavelength channels. However, when it comes to clipped
 * FITS formats, the range of color intensities varies from a wavelength to
 * another. Therefore, a dynamic domain is required. Using this class, we
 * generate such domain depending on the (wavelength, or to be exact) min and
 * max values of the color intensity. However, for JP2 images, the domain remain
 * the same across different wavelengths. <br>
 * In case of FITS images, the color intensities are usually extremely skewed
 * and makes the histogram of the image mainly piled on the few left-most bins.
 * It should also be noted that after clipped of these images, the color values
 * are no longer integers. So, bin size is not bounded to 1 as its minimum
 * width. Therefore, in case of FITS images one could multiply the upper bound
 * of the color intensities by 10, and then use these methods. <br>
 * Example: For AIA_94 images, the range after clipping is [0, 44] while in most
 * of the image, the maximum color intensity is less than 1, resulting in
 * allowing all 4096 pixel colors in the left-most bar of the histogram. If we
 * artificially use [0, 44X10], the following candidates for nOfBins will be
 * generated: <br>
 * {20, 41, 62, 83, 104, 125, 146, 167, 188, 209, 230, 251, 272, 293, 314, 335,
 * 356, 377, 398, 419, 440} <br>
 * which allows much thinner bins.
 * </p>
 * 
 * <br>
 * The pre-defined static field, <code>TOTAL_NUMBER_OF_TRIALS</code>, indicates
 * the size of the queried domain. In case a different value is needed, instead
 * of changing this field, use those methods which have the extra argument
 * <code>total</code>. <br>
 * 
 * 
 *
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 *
 */
public abstract class NumberOfBinsGenerator {

	public static final int TOTAL_NUMBER_OF_TRIALS = 20;

	/**
	 * This function uses the histogram function for finding the array of all
	 * possible nOfBins that should be tried for the range [min,max], and returns
	 * the index-th element of the array. For instance, for AIA94 images of FITS
	 * format, the clipped range is [0,44], therefore the following array of nOfBins
	 * will be generated: {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 23, 25, 27, 29, 31,
	 * 33, 35, 37, 39, 41, 44}, thus, for index = 0 for example, it returns 2; the
	 * first element of the array.
	 * 
	 * @param min
	 *            minimum intensity value
	 * @param max
	 *            maximum intensity value
	 * @param index
	 *            starts at zero, as the first element
	 * @return
	 */
	public static int findNOfBinsForIndex_Bounded(double min, double max, int index) {

		int nOfBins = (int) Math.floor(Histogram.breaks(min, max, TOTAL_NUMBER_OF_TRIALS + 1)[index + 1]);
		return nOfBins;
	}

	/**
	 * This is similar to <code>findNOfBinsForIndex</code> except that it allows
	 * different values for the constant field <code>TOTAL_NUMBER_OF_TRAILS</code>.
	 * 
	 * @param min
	 *            minimum intensity value
	 * @param max
	 *            maximum intensity value
	 * @param index
	 *            starts at zero, as the first element
	 * @param total
	 *            total number of bins out of which the <code>index</code>-th
	 *            nOfBins should be returned.
	 * @return
	 */
	public static int findNOfBinsForIndex_Bounded(double min, double max, int index, int total) {

		int nOfBins = (int) Math.floor(Histogram.breaks(min, max, total + 1)[index + 1]);
		return nOfBins;
	}

	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int[] findAllNOfBins_Bounded(double min, double max) {

		double[] h = Histogram.breaks(min, max, TOTAL_NUMBER_OF_TRIALS + 1);
		int[] allNOfBins = new int[h.length - 1];
		for (int i = 0; i < allNOfBins.length; i++) {
			allNOfBins[i] = (int) Math.floor(h[i + 1]);
		}
		return allNOfBins;
	}

	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int[] findAllNOfBins_Bounded(double min, double max, int total) {

		double[] h = Histogram.breaks(min, max, total + 1);
		int[] allNOfBins = new int[h.length - 1];
		for (int i = 0; i < allNOfBins.length; i++) {
			allNOfBins[i] = (int) Math.floor(h[i + 1]);
		}
		return allNOfBins;
	}
}
