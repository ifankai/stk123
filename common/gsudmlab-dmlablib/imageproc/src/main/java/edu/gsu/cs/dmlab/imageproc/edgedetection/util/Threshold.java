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
/************************************************************************
 * This class was originally implemented by <i>Jason Altschuler</i>, at  
 * <a href= "http://www.mit.edu/~jasonalt/">www.mit.edu/~jasonalt/</a>.
 * 
 * Jason kindly let us modify and employ his implementation to our open-source
 * library, DMlabLib.
 ************************************************************************/
package edu.gsu.cs.dmlab.imageproc.edgedetection.util;

/**
 * 
 * @author Azim Ahmadzadeh of Data Mining Lab, Georgia State University
 * 
 */
public class Threshold {

	/**
	 * Calculates threshold as the mean of the |G| matrix for edge detection
	 * algorithms.
	 * 
	 * @param magnitude
	 * @return
	 */
	public static double calcThresholdEdges(double[][] magnitude) {
		return Statistics.calcMean(magnitude);
	}

	/**
	 * Returns BufferedImage where color at (i, j) is black if pixel intensity &gt;
	 * threshold; white otherwise.
	 * 
	 * @param pixels
	 * @param threshold
	 * @return
	 */
	public static double[][] applyThreshold(int[][] pixels, int threshold) {
		int height = pixels.length;
		int width = pixels[0].length;

		double[][] output = new double[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				output[i][j] = pixels[i][j] > threshold ? 255 : 0;
			}
		}

		return output;

		// BufferedImage thresholdedImage = new BufferedImage(width, height,
		// BufferedImage.TYPE_BYTE_GRAY);
		// WritableRaster raster = thresholdedImage.getRaster();
		//
		// int[] black = {0, 0, 0};
		// int[] white = {255, 255, 255};
		//
		// // cache-efficient for both BufferedImage and int[][]
		// for (int row = 0; row < height; row++)
		// for (int col = 0; col < width; col++)
		// raster.setPixel(col, row, pixels[row][col] > threshold ? white : black);
		//
		// return thresholdedImage;
	}

	/**
	 * 
	 * @param pixels
	 * @return
	 */
	public static double[][] applyThreshold(boolean[][] pixels) {
		int height = pixels.length;
		int width = pixels[0].length;

		double[][] output = new double[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				output[i][j] = pixels[i][j] ? 255 : 0;
			}
		}

		return output;

		// BufferedImage thresholdedImage = new BufferedImage(width, height,
		// BufferedImage.TYPE_BYTE_GRAY);
		// WritableRaster raster = thresholdedImage.getRaster();
		//
		// int[] black = {0, 0, 0};
		// int[] white = {255, 255, 255};
		//
		// // cache efficient for both BufferedImage and int[][]
		// for (int row = 0; row < height; row++)
		// for (int col = 0; col < width; col++)
		// raster.setPixel(col, row, pixels[row][col] ? white : black);
		//
		// return thresholdedImage;
	}

	/**
	 * 
	 * @param pixels
	 * @return
	 */
	public static double[][] applyThresholdReversed(boolean[][] pixels) {
		int height = pixels.length;
		int width = pixels[0].length;

		double[][] output = new double[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				output[i][j] = pixels[i][j] ? 0 : 255;
			}
		}

		return output;
		// BufferedImage thresholdedImage = new BufferedImage(width, height,
		// BufferedImage.TYPE_BYTE_GRAY);
		// WritableRaster raster = thresholdedImage.getRaster();
		//
		// int[] black = {0, 0, 0};
		// int[] white = {255, 255, 255};
		//
		// // cache efficient for both BufferedImage and int[][]
		// for (int row = 0; row < height; row++)
		// for (int col = 0; col < width; col++)
		// raster.setPixel(col, row, pixels[row][col] ? black : white);
		//
		// return thresholdedImage;
	}

	/**
	 * 
	 * @param weakEdges
	 * @param strongEdges
	 * @return
	 */
	public static double[][] applyThresholdWeakStrongCanny(boolean[][] weakEdges, boolean[][] strongEdges) {
		int height = weakEdges.length;
		int width = weakEdges[0].length;

		double[][] output = new double[height][width];

		// cache efficient for both BufferedImage and int[][]
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++) {
				if (strongEdges[row][col])
					output[row][col] = 0;
				else if (weakEdges[row][col])
					output[row][col] = 0;
				else
					output[row][col] = 255;
			}

		return output;

		// BufferedImage thresholdedImage = new BufferedImage(width, height,
		// BufferedImage.TYPE_3BYTE_BGR);
		// WritableRaster raster = thresholdedImage.getRaster();
		//
		// int[] white = {255, 255, 255};
		// int[] blue = {0, 0, 255};
		// int[] green = {0, 255, 0};
		//
		// // cache efficient for both BufferedImage and int[][]
		// for (int row = 0; row < height; row++)
		// for (int col = 0; col < width; col++) {
		// if (strongEdges[row][col])
		// raster.setPixel(col, row, green);
		// else if (weakEdges[row][col])
		// raster.setPixel(col, row, blue);
		// else
		// raster.setPixel(col, row, white);
		// }
		//
		// return thresholdedImage;
	}

	public static double[][] applyThresholdOriginal(boolean[][] edges, double[][] originalImage) {
		int height = edges.length;
		int width = edges[0].length;

		double[][] output = new double[height][width];
		double temp;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (!edges[row][col]) { // not edge
					output[row][col] = 255;
				} else { // edge
					temp = originalImage[row][col]; // get original pixel color
					// scale to max intensity
					double scale = 255.0 / (255.0 - temp);
					temp = 255 - (scale * (255.0 - temp));
					output[row][col] = temp;
				}
			}
		}

		return output;

		// BufferedImage newImage = new BufferedImage(width, height,
		// BufferedImage.TYPE_3BYTE_BGR);
		// WritableRaster raster_new = newImage.getRaster();
		// WritableRaster raster_old = originalImage.getRaster();
		//
		// int[] white = {255, 255, 255};
		// int[] arr = new int[3];
		// int min;
		//
		// for (int row = 0; row < height; row++) {
		// for (int col = 0; col < width; col++) {
		// if (!edges[row][col]) { // not edge
		// raster_new.setPixel(col, row, white);
		// } else { // edge
		// // get original pixel color
		// raster_old.getPixel(col, row, arr);
		//
		// // scale to max intensity
		// min = 255;
		// for (int i : arr)
		// if (i < min)
		// min = i;
		// double scale = 255.0 / (255.0 - min);
		// for (int i = 0; i < 3; i++)
		// arr[i] = 255 - (int) (scale * (255.0 - arr[i]));
		// raster_new.setPixel(col, row, arr);
		// }
		// }
		// }
		//
		// return newImage;
	}
}
