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
import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;


/**
 * @author Azim Ahmadzadeh, updated by Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 *
 */
public abstract class BaseMeanCalculator {

	/**
	 * This method calculates the mean intensity color for each patch of the given image.
	 * @param image a 2D matrix representing the given image
	 * @param patchSize the size of each square patch for which the mean value should be calculated.
	 * @return a 2D array of the mean intensity color of each patch.
	 */
	protected double[][] calculateMeanParameter(double[][] image, IMeasures.PatchSize patchSize){

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");
		
		double[][] means = null;
		double[] flatPatch = null;
		int imageH = image.length;
		int imageW = image[0].length;
		
		/*
		 * patchSize = 0 is reserved for the cases when one MEAN for the entire image is requested.
		 */
		int pSize = (patchSize.getSize() == 0) ? imageH : patchSize.getSize();
		
		// Check basic requirements
		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Matrix must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Matrix's width must be eqaul to its height!");

		means = new double[imageH / pSize][imageW / pSize];

		// Iterate over the image (patch by patch)
		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {

				// Get the average of all numbers in each patch
				flatPatch = MatrixUtil.getSubMatrixAsArray(image, row * pSize, col * pSize, pSize);
				means[row][col] = smile.math.Math.mean(flatPatch);
			}
		}

		return means;
	}
	
}