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
package edu.gsu.cs.dmlab.imageproc.params.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.KurtosisParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;

public class KurtosisParamCalculatorTests {

	@Test
	public void kurtosisParamTestThrowsOnNullPatchSize() {

		IMeasures.PatchSize pSize = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			IParamCalculator calc = new KurtosisParamCalculator(pSize);
		});
	}

	@Test
	public void kurtosisParamTestThrowsOnImageNotMultipOfPatchSize() {
		int height = 63;
		int width = 63;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		double[][] img = new double[height][width];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void kurtosisParamTestThrowsOnNullImage() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		double[][] img = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void kurtosisParamTestThrowsOnNullImage2() {
		int height = 63;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		double[][] img = new double[height][];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void kurtosisParamTestThrowsOnImageNotSquare() {
		int height = 64;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		double[][] img = new double[height][width];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void kurtosisParamTestOutputOnZeroPatchSize() {
		int width = 128;
		int height = 128;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._0;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;

		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		outMatrix = calc.calculateParameter(img);
		assertTrue(outMatrix.length == 1 && outMatrix[0].length == 1);
	}

	@Test
	public void skewnessParamTestOutputValue() {

		int width = 64;
		int height = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;
		double[] data = new double[height * width];
		int counter = 0;
		for (int row = 0; row < img.length; row++) {
			for (int col = 0; col < img[0].length; col++) {
				if (counter < 500) {
					img[row][col] = 0;
				} else if (counter < 1000) {
					img[row][col] = 100;
				} else {
					img[row][col] = 30;
				}
				data[col + (height + row)] = img[row][col];
				counter++;
			}
		}
		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		outMatrix = calc.calculateParameter(img);

		/*
		 * Actual value is calculated in R (library(e1071)): b <- c(rep(x = 0, 500),
		 * rep(x = 100, 500), rep(x = 30, 3096)) kurtosis(b, type = 2)
		 */
		assertEquals(2.079936, outMatrix[0][0], 0.01);
	}

	@Test
	public void kurtosisParamTestOutputSizeGreaterThanOne() {
		int height = 128;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		int patchSize = pSize.getSize();
		double[][] img = new double[height][width];

		IParamCalculator calc = new KurtosisParamCalculator(pSize);
		double[][] outMatrix = calc.calculateParameter(img);

		assertTrue(outMatrix.length == height / patchSize && outMatrix[0].length == width / patchSize);
	}

}
