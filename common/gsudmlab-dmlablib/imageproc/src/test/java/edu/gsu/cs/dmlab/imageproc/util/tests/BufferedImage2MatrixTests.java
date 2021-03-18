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
package edu.gsu.cs.dmlab.imageproc.util.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.util.BufferedImage2Matrix;

public class BufferedImage2MatrixTests {

	@Test
	public void bufferedImage2MatrixGetArrayTestThrowsOnNullBufferedImage() {

		BufferedImage img = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			BufferedImage2Matrix.getArrayFromImage(img);
		});
	}

	@Test
	public void bufferedImage2MatrixGet2DArrayTestThrowsOnNullBufferedImage() {

		BufferedImage img = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			BufferedImage2Matrix.get2DArrayFromImage(img);
		});
	}

	@Test
	public void bufferedImage2MatrixGetArrayTestOutputValue() {

		int width = 4;
		int height = 6;
		double[] outputImage = null;
		double[] expected = new double[width * height];

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				double val = 0;
				if (row < (height / 2)) {
					val = 255;
				}

				int red = (((int) val) << 16) & 0x00FF0000;
				int green = (((int) val) << 8) & 0x0000FF00;
				int blue = ((int) val) & 0x000000FF;
				int colVal = 0xFF000000 | red | green | blue;
				img.setRGB(col, row, colVal);
			}
		}

		for (int i = 0; i < width * height; i++) {
			expected[i] = 0;
			if (i < (width * height) / 2) {
				expected[i] = 255;
			}
		}

		outputImage = BufferedImage2Matrix.getArrayFromImage(img);
		assertArrayEquals(expected, outputImage, 0.00001);
	}

	@Test
	public void bufferedImage2MatrixGetArrayTestOutputValue2() {

		int width = 4;
		int height = 6;
		double[] outputImage = null;
		double[] expected = new double[width * height];

		double val = 0;
		int n = 0;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);// BufferedImage.TYPE_BYTE_GRAY
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				val = 2 * (n++) + 10;

				int red = (((int) val) << 16) & 0x00FF0000;
				int green = (((int) val) << 8) & 0x0000FF00;
				int blue = ((int) val) & 0x000000FF;
				int colVal = 0xFF000000 | red | green | blue;
				img.setRGB(col, row, colVal);
			}
		}

		int m = 0;
		for (int i = 0; i < width * height; i++) {
			expected[i] = 2 * (m++) + 10;
		}

		outputImage = BufferedImage2Matrix.getArrayFromImage(img);

		// System.out.println("Output: \t" + Arrays.toString(outputImage));
		// System.out.println("Expected:\t" + Arrays.toString(expected));

		assertArrayEquals(expected, outputImage, 0.00001);
	}

}