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

/*
 * The methods below are inspired from Tom Gibara's
 * implementation of Canny Edge Detection > readLuminance().
 * See:
 * http://www.tomgibara.com/computer-vision/canny-edge-detector 
 */
import java.awt.image.BufferedImage;

import org.apache.commons.math3.exception.NullArgumentException;

/**
 * This is an <code>abstract</code> class which is designed to convert a given
 * matrix into a <code>BufferedImage</code>. <br>
 * The methods below are inspired from Tom Gibara's implementation of Canny Edge
 * Detection &gt; readLuminance(). <br>
 * See his code <a href=
 * "http://www.tomgibara.com/computer-vision/canny-edge-detector">here</a>. <br>
 * <b>Note:</b> This class follows the convention of reading/writing matrices in
 * a row-by-row fashion.<br>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 * 
 */
public abstract class Matrix2BufferedImage {

	/**
	 * This method converts a matrix of double values to a
	 * <code>bufferedImage</code>, using <code>setRGB</code> method. If the give
	 * values are outside of the acceptable range (i.e., [0,255]), it throws an
	 * exception and returns <code>null</code>. <br>
	 * The <code>BufferedImage</code> will be created with the default image type
	 * <code>TYPE_BYTE_GRAY</code>. If the image type needs to be specified, use
	 * {@link Matrix2BufferedImage#getBufferedImage}.
	 * 
	 * @param matrix
	 *            to be converted to a BufferedImage object.
	 * @return The <code>BufferedImage</code> constructed based on the given matrix.
	 * @throws NullArgumentException
	 */
	public static BufferedImage getBufferedImage(double[][] matrix) throws NullArgumentException {

		if (matrix == null || matrix[0] == null)
			throw new NullArgumentException();

		return (getBufferedImage(matrix, BufferedImage.TYPE_BYTE_GRAY));
	}

	/**
	 * This method converts a matrix of double values to a BufferedImage, using
	 * <code>setRGB</code> method. If the give values are outside of the acceptable
	 * range (i.e., [0,255]), it throws an exception and returns. <br>
	 * 
	 * @param matrix
	 *            to be converted to a BufferedImage object.
	 * @param imageType
	 *            type of the bufferedImage. Acceptable choices are:
	 *            <code>BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_BYTE_GRAY </code>.
	 * @return The <code>BufferedImage</code> constructed based on the given matrix.
	 * @throws NullArgumentException
	 *             <br>
	 *             <b>Note:</b> It is recommended to use "TYPE_INT_RGB" in case of
	 *             jp2 format, and "TYPE_3BYTE_BGR" in case of jpg format.
	 */
	public static BufferedImage getBufferedImage(double[][] matrix, int imageType) throws NullArgumentException {

		if (matrix == null || matrix[0] == null)
			throw new NullArgumentException();

		int color, value = 0;
		int nRows = matrix.length;
		int nCols = matrix[0].length;

		BufferedImage bImage = new BufferedImage(nCols, nRows, imageType);

		for (int row = 0; row < nRows; row++) {
			for (int col = 0; col < nCols; col++) {

				color = (int) matrix[row][col];
				if (color > 255 | color < 0) {
					new IllegalArgumentException("The given matrix contains values outside the range [0, 255]!");
					return null;
				}

				value = 0xFF000000 | color << 16 | color << 8 | color;
				bImage.setRGB(col, row, value);
			}
		}
		return bImage;
	}

}