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
package edu.gsu.cs.dmlab.imageproc.edgedetection.util;

/**
 * 
 * @author Azim Ahmadzadeh of Data Mining Lab, Georgia State University
 * 
 */
public class Statistics {

	/**
	 * Calculates mean pixel intensity
	 * 
	 * @param image
	 * @return
	 */
	public static double calcMean(double[][] image) {
		double mean = 0;

		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[0].length; j++)
				mean += image[i][j];

		return mean / (double) (image.length * image[0].length);
	}

	/**
	 * Calculates mean pixel intensity
	 * 
	 * @param image
	 * @return
	 */
	public static double calcMean(int[][] image) {
		double mean = 0;

		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[0].length; j++)
				mean += image[i][j];

		return mean / (double) (image.length * image[0].length);
	}

	/**
	 * Calculates standard deviation of pixel intensity (uncorrected sample std.
	 * dev.)
	 * 
	 * @param image
	 * @param mean
	 * @return
	 */
	public static double calcStdDev(double[][] image, double mean) {
		double sigma = 0;

		double offMean;
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[0].length; j++) {
				offMean = mean - image[i][j];
				sigma += offMean * offMean;
			}
		}

		return Math.sqrt(sigma / (double) (image.length * image[0].length - 1));
	}

	/**
	 * Calculates standard deviation of pixel intensity (uncorrected sample std.
	 * dev.)
	 * 
	 * @param image
	 * @param mean
	 * @return
	 */
	public static double calcStdDev(int[][] image, double mean) {
		double sigma = 0;

		double offMean;
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[0].length; j++) {
				offMean = mean - image[i][j];
				sigma += offMean * offMean;
			}
		}

		return Math.sqrt(sigma / (double) (image.length * image[0].length - 1));
	}

}
