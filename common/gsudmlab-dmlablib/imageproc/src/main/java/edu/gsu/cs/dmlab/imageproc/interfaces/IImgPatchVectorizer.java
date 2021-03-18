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
package edu.gsu.cs.dmlab.imageproc.interfaces;

import smile.math.matrix.DenseMatrix;

/**
 * Interface for various methods of converting a patches of an image into a
 * vector of values.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IImgPatchVectorizer {
	/**
	 * Creates column vectors of the patches of a patch size from each of the passed
	 * in image dimensions. After each patch vectorization the patch is advanced by
	 * a step size and the process is repeated.
	 * 
	 * @param imageDims
	 *            The set of image dimensions to vectorize patches from.
	 * @return The set of vectorized patches in in column major order.
	 */
	public DenseMatrix vectorize(DenseMatrix[] imageDims);
}
