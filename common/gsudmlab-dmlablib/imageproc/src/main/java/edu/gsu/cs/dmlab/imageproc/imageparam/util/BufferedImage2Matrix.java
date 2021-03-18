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

import java.awt.image.BufferedImage;

/**
 * This class is designed to wrap all minor manipulation tasks needed, such as
 * converting a <code>BufferedImage</code> to a one/two-dimensional array. <br>
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
public abstract class BufferedImage2Matrix {

	/**
	 * This method converts a BufferedImage object into a 1D array of color
	 * intensity values. In this implementation, the conversion of the 2D image is
	 * done in such a way that rows are put one after another to form the 1D array
	 * (as opposed to a column-by-column conversion).
	 * 
	 * @param bImage
	 *            the given BufferedImage object to be converted.
	 * @return a 1D array of the color intensity values (<code>double</code>)
	 *         extracted from the given image.
	 */
	public static double[] getArrayFromImage(BufferedImage bImage) {

		if (bImage == null) {
			throw new IllegalArgumentException("BufferedImage cannot be null in ImageManipulator.");
		}

		int imageW = bImage.getWidth();
		int imageH = bImage.getHeight();
		int picSize = imageW * imageH;
		double[] imageArray = new double[picSize];

		int type = bImage.getType();
		if (type == BufferedImage.TYPE_INT_RGB || type == BufferedImage.TYPE_INT_ARGB) {
			int[] pixels = (int[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				int p = pixels[i];
				int r = (p & 0xff0000) >> 16;
				int g = (p & 0xff00) >> 8;
				int b = p & 0xff;
				imageArray[i] = luminance(r, g, b);
			}
		} else if (type == BufferedImage.TYPE_BYTE_GRAY) {
			byte[] pixels = (byte[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				imageArray[i] = (pixels[i] & 0xff);
			}
		} else if (type == BufferedImage.TYPE_USHORT_GRAY) {
			short[] pixels = (short[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				// For FITS format, this method won't be used.
				// So, the division by 256 is OK.
				imageArray[i] = (pixels[i] & 0xffff) / 256;
			}
		} else if (type == BufferedImage.TYPE_3BYTE_BGR) {
			byte[] pixels = (byte[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			int offset = 0;
			for (int i = 0; i < picSize; i++) {
				int b = pixels[offset++] & 0xff;
				int g = pixels[offset++] & 0xff;
				int r = pixels[offset++] & 0xff;
				imageArray[i] = luminance(r, g, b);
			}
		} else {
			throw new IllegalArgumentException("Unsupported image type: " + type);
		}

		return imageArray;
	}

	/**
	 * This method converts a BufferedImage object into a 2D array of color
	 * intensity values.
	 * 
	 * @param bImage
	 *            the given BufferedImage object to be converted.
	 * @return a 2D array of the color intensity values (<code>double</code>)
	 *         extracted from the given image.
	 */
	public static double[][] get2DArrayFromImage(BufferedImage bImage) {

		if (bImage == null) {
			throw new IllegalArgumentException("BufferedImage cannot be null in ImageManipulator.");
		}
		int imageW = bImage.getWidth();
		int imageH = bImage.getHeight();
		int picSize = imageW * imageH;
		double[][] image2DArray = new double[imageH][imageW];

		int type = bImage.getType();
		if (type == BufferedImage.TYPE_INT_RGB || type == BufferedImage.TYPE_INT_ARGB) {
			int[] pixels = (int[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				int p = pixels[i];
				int r = (p & 0xff0000) >> 16;
				int g = (p & 0xff00) >> 8;
				int b = p & 0xff;
				image2DArray[i / imageW][i % imageW] = luminance(r, g, b);
			}
		} else if (type == BufferedImage.TYPE_BYTE_GRAY || type == BufferedImage.TYPE_BYTE_INDEXED) {
			byte[] pixels = (byte[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				image2DArray[i / imageW][i % imageW] = (pixels[i] & 0xff);
			}
		} else if (type == BufferedImage.TYPE_USHORT_GRAY) {
			short[] pixels = (short[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				// For FITS format, this method won't be used.
				// So, the division by 256 is OK.
				image2DArray[i / imageW][i % imageW] = (pixels[i] & 0xffff) / 256;
			}
		} else if (type == BufferedImage.TYPE_3BYTE_BGR) {
			byte[] pixels = (byte[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			int offset = 0;
			for (int i = 0; i < picSize; i++) {
				int b = pixels[offset++] & 0xff;
				int g = pixels[offset++] & 0xff;
				int r = pixels[offset++] & 0xff;
				image2DArray[i / imageW][i % imageW] = luminance(r, g, b);
			}
		} else {
			throw new IllegalArgumentException("Unsupported image type: " + type);
		}

		return image2DArray;
	}

	/**
	 * The linear luminance is calculated as a weighted sum of the three
	 * linear-intensity values. This is needed to produce a smoother gray-scale
	 * output.
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	private static double luminance(double r, double g, double b) {
		return Math.round(0.299f * r + 0.587f * g + 0.114f * b);
	}

	public static double[][] get2DArrayFromImageIgnoringColorPixels(BufferedImage bImage) {

		if (bImage == null) {
			throw new IllegalArgumentException("BufferedImage cannot be null in ImageManipulator.");
		}
		int imageW = bImage.getWidth();
		int imageH = bImage.getHeight();
		int picSize = imageW * imageH;
		double[][] image2DArray = new double[imageH][imageW];

		int type = bImage.getType();
		if (type == BufferedImage.TYPE_INT_RGB || type == BufferedImage.TYPE_INT_ARGB) {

			int[] pixels = (int[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				int p = pixels[i];
				int r = (p & 0xff0000) >> 16;
				int g = (p & 0xff00) >> 8;
				int b = p & 0xff;

				if (r == g && g == b)
					image2DArray[i / imageW][i % imageW] = luminance(r, g, b);
				else
					image2DArray[i / imageW][i % imageW] = 0;
			}
		} else if (type == BufferedImage.TYPE_BYTE_GRAY) {
			byte[] pixels = (byte[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				image2DArray[i / imageW][i % imageW] = (pixels[i] & 0xff);
			}
		} else if (type == BufferedImage.TYPE_USHORT_GRAY) {
			short[] pixels = (short[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			for (int i = 0; i < picSize; i++) {
				image2DArray[i / imageW][i % imageW] = (pixels[i] & 0xffff) / 256;
			}
		} else if (type == BufferedImage.TYPE_3BYTE_BGR) {
			byte[] pixels = (byte[]) bImage.getData().getDataElements(0, 0, imageW, imageH, null);
			int offset = 0;
			for (int i = 0; i < picSize; i++) {
				int b = pixels[offset++] & 0xff;
				int g = pixels[offset++] & 0xff;
				int r = pixels[offset++] & 0xff;
				if (r == g && g == b) {
					image2DArray[i / imageW][i % imageW] = 0;
				} else {
					image2DArray[i / imageW][i % imageW] = luminance(r, g, b);
				}
			}
		} else {
			throw new IllegalArgumentException("Unsupported image type: " + type);
		}

		return image2DArray;
	}
}
