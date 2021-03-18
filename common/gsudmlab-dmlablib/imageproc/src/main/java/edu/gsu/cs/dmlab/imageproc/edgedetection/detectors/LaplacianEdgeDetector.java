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
/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Laplacian edge detector algorithm
 * 
 * Finds edges by finding pixel intensities where the Laplacian operator
 * (divergence of gradient; 2nd order differential operator) is 0.
 * (Discrete image derivative found by image convolutions).  However, 
 * this method historically has been replaced by Sobel, Canny, etc. because 
 * it finds many false edges. The reason is that 2nd derivative could mean
 * a local min or max of first derivative. We only want the max's;
 * the mins are false edges.
 *
 * For full documentation, see the README
  ************************************************************************/

/************************************************************************
 * This class was originally implemented by Jason Altschuler at  
 * www.mit.edu/~jasonalt/.
 * 
 * Jason kindly let us modify and employ his implementation to our open-source
 * library, DMlabLib.
 ************************************************************************/

package edu.gsu.cs.dmlab.imageproc.edgedetection.detectors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.gsu.cs.dmlab.imageproc.edgedetection.imagederivatives.ConvolutionKernel;
import edu.gsu.cs.dmlab.imageproc.edgedetection.imagederivatives.ImageConvolution;
import edu.gsu.cs.dmlab.imageproc.edgedetection.util.Threshold;
import edu.gsu.cs.dmlab.imageproc.imageparam.util.BufferedImage2Matrix;

/**
 * This class detects the edges of grayscale images using Laplacian convolution
 * kernel. <br>
 * <b>Note:</b> The final matrix of edges is slightly smaller than the original
 * image. The difference depends on the size of the kernel matrix.
 * 
 * @see edu.gsu.cs.dmlab.imageproc.edgedetection.imagederivatives.ImageConvolution
 * 
 * @author Jason Altschuler, modified by Azim Ahmadzadeh and Dustin Kempton of
 *         Data Mining Lab, Georgia State University
 * 
 *
 */
public class LaplacianEdgeDetector {

	/************************************************************************
	 * Data structures
	 ***********************************************************************/

	// dimensions are slightly smaller than original image because of discrete
	// convolution.
	private boolean[][] edges;

	// threshold used to find edges; one requirement for [i,j] to be edge is
	// |G[i,j]| = |f'[i,j]| > threshold.
	private double threshold;

	// convolution kernel; discretized appromixation of 2nd derivative
	private double[][] kernel = { { -1, -1, -1 }, { -1, 8, -1 }, { -1, -1, -1 } };

	/***********************************************************************
	 * Detect edges
	 ***********************************************************************/

	/**
	 * All work is done in the constructor.
	 * 
	 * @param filePath path to image
	 */
	public LaplacianEdgeDetector(String filePath) {
		// read image and get pixels
		BufferedImage originalImage;
		try {
			originalImage = ImageIO.read(new File(filePath));
			findEdges(BufferedImage2Matrix.get2DArrayFromImage(originalImage));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find beautiful edges.
	 * <P>
	 * All work is done in the constructor.
	 * 
	 * @param image
	 */
	public LaplacianEdgeDetector(double[][] image) {
		findEdges(image);
	}

	/**
	 * Finds only the most beautiful edges.
	 * 
	 * @param image
	 */
	private void findEdges(double[][] image) {
		// convolve image with Gaussian kernel
		ImageConvolution gaussianConvolution = new ImageConvolution(image, ConvolutionKernel.GAUSSIAN_KERNEL);
		double[][] smoothedImage = gaussianConvolution.getConvolvedImage();

		// apply convolutions to smoothed image
		ImageConvolution ic = new ImageConvolution(smoothedImage, kernel);

		// calculate magnitude of gradients
		double[][] convolvedImage = ic.getConvolvedImage();
		int rows = convolvedImage.length;
		int columns = convolvedImage[0].length;

		// calculate threshold intensity to be edge
		threshold = Threshold.calcThresholdEdges(convolvedImage);

		// threshold image to find edges
		edges = new boolean[rows][columns];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				edges[i][j] = Math.abs(convolvedImage[i][j]) == 0.0;
	}

	public boolean[][] getEdges() {
		return edges;
	}

	public double getThreshold() {
		return threshold;
	}

}
