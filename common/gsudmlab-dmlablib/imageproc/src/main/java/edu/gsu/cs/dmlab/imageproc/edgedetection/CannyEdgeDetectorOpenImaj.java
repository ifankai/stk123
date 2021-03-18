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

import org.openimaj.image.FImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;

import edu.gsu.cs.dmlab.imageproc.interfaces.IEdgeDetector;

/**
 * This class is only a wrapper for the external class
 * <code>org.openimaj.image.processing.edges.CannyEdgeDetector.CannyEdgeDetector</code>.
 * This is needed since in our library any edge detector class must implement
 * IEdgeDetector interface.
 * 
 *
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 */
public class CannyEdgeDetectorOpenImaj implements IEdgeDetector {

	private float lowThreshold;
	private float highThreshold;
	private float sigma;

	public CannyEdgeDetectorOpenImaj(float lt, float ht, float s) {

		this.lowThreshold = lt;
		this.highThreshold = ht;
		this.sigma = s;

	}

	@Override
	public double[][] getEdges(double[][] sourceImg, double[] colors) {

		CannyEdgeDetector ced = new CannyEdgeDetector(this.lowThreshold, this.highThreshold, this.sigma);

		// Create an FImage from the input matrix
		FImage fImage = new FImage(this.convertDoubleToFloat(sourceImg));

		// Process the image in place
		ced.processImage(fImage);

		// Replace edges with 255 (white), and background with 0 (black)
		double[][] output = this.convertFloatToDouble(fImage.pixels);
		this.thresholdEdges(output, colors);

		return output;
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
	private void thresholdEdges(double[][] input, double[] cols) {

		double background = cols[0];
		double foreground = cols[1];

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length; j++) {
				input[i][j] = input[i][j] > 0 ? foreground : background;
			}
		}

	}

	/**
	 * Converts a 2D array of floats to doubles
	 * 
	 * @param input
	 * @return
	 */
	public double[][] convertFloatToDouble(float[][] input) {

		int imageH = input.length;
		int imageW = input[0].length;

		double[][] output = new double[imageH][imageW];

		for (int r = 0; r < imageH; r++) {
			for (int c = 0; c < imageW; c++) {
				output[r][c] = (double) input[r][c];
			}
		}

		return output;
	}

	/**
	 * Converts a 2D array of doubles to floats
	 * 
	 * @param input
	 * @return
	 */
	private float[][] convertDoubleToFloat(double[][] input) {

		int imageH = input.length;
		int imageW = input[0].length;

		float[][] output = new float[imageH][imageW];

		for (int r = 0; r < imageH; r++) {
			for (int c = 0; c < imageW; c++) {
				output[r][c] = (float) input[r][c];
			}
		}

		return output;
	}
}
