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
 * Abstract parent class for PrewittEdgeDetector.java, SobelEdgeDetector.java,
 * and RobertsCrossEdgeDetector.java
 ************************************************************************/

/************************************************************************
 * This class was originally implemented by Jason Altschuler at  
 * www.mit.edu/~jasonalt/.
 * 
 * Jason kindly let us modify and employ his implementation to our open-source
 * library, DMlabLib.
 ************************************************************************/
package edu.gsu.cs.dmlab.imageproc.edgedetection.detectors;

import edu.gsu.cs.dmlab.imageproc.edgedetection.imagederivatives.ImageConvolution;
import edu.gsu.cs.dmlab.imageproc.edgedetection.util.Hypotenuse;
import edu.gsu.cs.dmlab.imageproc.edgedetection.util.NonMaximumSuppression;
import edu.gsu.cs.dmlab.imageproc.edgedetection.util.Threshold;

/**
 * This is the parent class for the following edge detector classes:
 * <ul>
 * <li>{@link edu.gsu.cs.dmlab.imageproc.edgedetection.CannyEdgeDetector}
 * <li>{@link edu.gsu.cs.dmlab.imageproc.edgedetection.detectors.LaplacianEdgeDetector}
 * <li>{@link edu.gsu.cs.dmlab.imageproc.edgedetection.detectors.RobertsCrossEdgeDetector}
 * <li>{@link edu.gsu.cs.dmlab.imageproc.edgedetection.detectors.PrewittEdgeDetector}
 * <li>{@link edu.gsu.cs.dmlab.imageproc.edgedetection.detectors.SobelEdgeDetector}
 * </ul>
 * 
 * @author Jason Altschuler, modified by Azim Ahmadzadeh and Dustin Kempton of
 *         Data Mining Lab, Georgia State Universit
 * 
 */
public abstract class GaussianEdgeDetector {

	/************************************************************************
	 * Data structures
	 ***********************************************************************/
	// dimensions are slightly smaller than original image because of discrete
	// convolution.
	protected boolean[][] edges;

	// threshold used to find edges; one requirement for [i,j] to be edge is
	// |G[i,j]| = |f'[i,j]| > threshold.
	protected double threshold;

	// true --> use L1 norm. false --> use L2. L1 is less precise, but faster.
	protected boolean L1norm;

	/************************************************************************
	 * Abstract methods to implement
	 ***********************************************************************/
	protected abstract double[][] getXkernel();

	protected abstract double[][] getYkernel();

	/***********************************************************************
	 * Detect edges
	 ***********************************************************************/

	/**
	 * Find beautiful edges.
	 * 
	 * @param image
	 */
	protected void findEdges(double[][] image, boolean L1norm) {
		// get convolution kernels
		double[][] x_kernel = getXkernel();
		double[][] y_kernel = getYkernel();

		// apply convolutions to original image
		ImageConvolution x_ic = new ImageConvolution(image, x_kernel);
		ImageConvolution y_ic = new ImageConvolution(image, y_kernel);

		// calculate magnitude of gradients
		double[][] x_imageConvolution = x_ic.getConvolvedImage();
		double[][] y_imageConvolution = y_ic.getConvolvedImage();

		// note that smoothed image have slightly different dimensions than original
		// image (because image convolution)
		int rows = x_imageConvolution.length;
		int columns = x_imageConvolution[0].length;

		// calculate magnitude of gradient for each pixel, and angle of edge direction
		double[][] mag = new double[rows][columns];
		NonMaximumSuppression.EdgeDirection[][] angle = new NonMaximumSuppression.EdgeDirection[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				mag[i][j] = L1norm ? Hypotenuse.L1(x_imageConvolution[i][j], y_imageConvolution[i][j])
						: Hypotenuse.L2(x_imageConvolution[i][j], y_imageConvolution[i][j]);
				angle[i][j] = NonMaximumSuppression.EdgeDirection.getDirection(x_imageConvolution[i][j],
						y_imageConvolution[i][j]);
			}
		}

		// apply threshold and non-maximum suppression
		edges = new boolean[rows][columns];
		threshold = Threshold.calcThresholdEdges(mag);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				edges[i][j] = (mag[i][j] < threshold) ? false
						: NonMaximumSuppression.nonMaximumSuppression(mag, angle[i][j], i, j);
	}

	/*********************************************************************
	 * Accessors
	 *********************************************************************/

	/**
	 * @return detected edges
	 */
	public boolean[][] getEdges() {
		return edges;
	}

	/**
	 * @return threshold compared with sgradient magnitudes to find edges
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @return whether used L1 or L2 distance norm
	 */
	public boolean getL1norm() {
		return L1norm;
	}
}
