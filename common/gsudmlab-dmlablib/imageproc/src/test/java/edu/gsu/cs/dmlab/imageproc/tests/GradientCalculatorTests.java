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
package edu.gsu.cs.dmlab.imageproc.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.Gradient;
import edu.gsu.cs.dmlab.imageproc.edgedetection.GradientCalculator;

import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;

public class GradientCalculatorTests {

	@Test
	public void gradientCalculator2D1D_1() {

		double[][] image = new double[][] { { 1, 5, 1, 5, 1, 5, 1, 5 }, { 2, 6, 2, 6, 2, 6, 2, 6 },
				{ 4, 4, 4, 4, 4, 4, 4, 4 }, { 8, 8, 8, 8, 2, 2, 2, 2 }, { 9, 3, 9, 3, 9, 3, 9, 3 },
				{ 5, 1, 5, 1, 5, 1, 5, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 1, 0, 1, 0, 1, 0 } };

		GradientCalculator gc = new GradientCalculator("sobel");
		Gradient g = gc.calculateGradientCart(image);

		double[][] m1 = g.gx;
		double[][] m2 = MatrixUtil.convertTo2DArray(g.gx_f, image[0].length, image.length);

		// System.out.println(Arrays.toString(g.gx_f));
		assertTrue(Arrays.deepEquals(m1, m2));
	}

	@Test
	public void gradientCalculator2D1D_2() {

		double[][] image = new double[][] { { 1, 5, 1, 5, 1, 5, 1, 5 }, { 2, 6, 2, 6, 2, 6, 2, 6 },
				{ 4, 4, 4, 4, 4, 4, 4, 4 }, { 8, 8, 8, 8, 2, 2, 2, 2 }, { 9, 3, 9, 3, 9, 3, 9, 3 },
				{ 5, 1, 5, 1, 5, 1, 5, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 1, 0, 1, 0, 1, 0 } };

		GradientCalculator gc = new GradientCalculator("sobel");
		Gradient g = gc.calculateGradientCart(image);

		double[][] m1 = g.gy;
		double[][] m2 = MatrixUtil.convertTo2DArray(g.gy_f, image[0].length, image.length);

		assertTrue(Arrays.deepEquals(m1, m2));
	}

	@Test
	public void gradientCalculator2D1D_3() {

		double[][] image = new double[][] { { 1, 5, 1, 5, 1, 5, 1, 5 }, { 2, 6, 2, 6, 2, 6, 2, 6 },
				{ 4, 4, 4, 4, 4, 4, 4, 4 }, { 8, 8, 8, 8, 2, 2, 2, 2 }, { 9, 3, 9, 3, 9, 3, 9, 3 },
				{ 5, 1, 5, 1, 5, 1, 5, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 1, 0, 1, 0, 1, 0 } };

		GradientCalculator gc = new GradientCalculator("sobel");
		Gradient g = gc.calculateGradientCart(image);

		double[][] m1 = g.gd;
		double[][] m2 = MatrixUtil.convertTo2DArray(g.gd_f, image[0].length, image.length);

		assertTrue(Arrays.deepEquals(m1, m2));
	}

}