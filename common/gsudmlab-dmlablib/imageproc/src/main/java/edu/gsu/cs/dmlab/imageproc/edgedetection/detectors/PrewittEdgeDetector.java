/**
 * dmLabLib, a Library created for use in various projects at the Data Mining Lab  
 * (http://dmlab.cs.gsu.edu/) of Georgia State University (http://www.gsu.edu/).  
 *  
 * Copyright (C) 201 Georgia State University
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
 * ALGORITHM: Prewitt edge detector algorithm
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

import edu.gsu.cs.dmlab.imageproc.imageparam.util.BufferedImage2Matrix;

/**
 * This class detects the edges of grayscale images using Prewitt convolution
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
public class PrewittEdgeDetector extends GaussianEdgeDetector {

	/*********************************************************************
	 * Convolution kernels
	 *********************************************************************/

	private final static double[][] X_kernel = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };

	private final static double[][] Y_kernel = { { 1, 1, 1 }, { 0, 0, 0 }, { -1, -1, -1 } };

	/*********************************************************************
	 * Implemented abstract methods
	 *********************************************************************/

	/**
	 * @Override {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}}
	 */
	public double[][] getXkernel() {
		return PrewittEdgeDetector.X_kernel;
	}

	/**
	 * @Override {{1, 1, 1}, {0, 0, 0}, {-1, -1, -1}}
	 */
	public double[][] getYkernel() {
		return PrewittEdgeDetector.Y_kernel;
	}

	/*********************************************************************
	 * Constructor
	 **********************************************************************/

	/**
	 * All work is done in constructor.
	 * 
	 * @param filePath path to image
	 */
	public PrewittEdgeDetector(String filePath) {
		// read image and get pixels
		BufferedImage originalImage;
		try {
			originalImage = ImageIO.read(new File(filePath));
			// findEdges(Grayscale.imgToGrayPixels(originalImage), false);
			findEdges(BufferedImage2Matrix.get2DArrayFromImage(originalImage), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * All work is done in constructor.
	 * <P>
	 * Uses L2 norm by default.
	 * 
	 * @param image the image to process
	 */
	public PrewittEdgeDetector(double[][] image) {
		findEdges(image, false);
	}

	/**
	 * All work is done in constructor.
	 * <P>
	 * Gives option to use L1 or L2 norm.
	 * 
	 * @param image  image to process
	 * 
	 * @param L1norm indicates whether to use L1 norm, L2 will be used if false
	 */
	public PrewittEdgeDetector(double[][] image, boolean L1norm) {
		findEdges(image, L1norm);
	}

}
