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

import edu.gsu.cs.dmlab.imageproc.imageparam.util.Matrix2BufferedImage;

public class Matrix2BufferedImageTests {

	@Test
	public void getBufferedImageThrowsOnNullMatrix() {

		double[][] matrix = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Matrix2BufferedImage.getBufferedImage(matrix);
		});
	}

	@Test
	public void getBufferedImageThrowsOnNullMatrix2() {

		double[][] matrix = new double[64][];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Matrix2BufferedImage.getBufferedImage(matrix);
		});
	}

	@Test
	public void getBufferedImageTestOutputSize() {

		double[][] matrix = new double[4][8];// [row][col] :: row X col :: height X width

		BufferedImage bImage = Matrix2BufferedImage.getBufferedImage(matrix);

		double[] expected = new double[] { 4, 8 };
		double[] outputSize = new double[] { bImage.getHeight(), bImage.getWidth() };

		assertArrayEquals(expected, outputSize, 0.000001);
	}

}