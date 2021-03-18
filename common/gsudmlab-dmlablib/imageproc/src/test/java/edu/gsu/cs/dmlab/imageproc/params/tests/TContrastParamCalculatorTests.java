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

import edu.gsu.cs.dmlab.imageproc.imageparam.TContrastParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;

public class TContrastParamCalculatorTests {

	@Test
	public void tContrastParamTestThrowsOnNullPatchSize() {

		IMeasures.PatchSize pSize = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TContrastParamCalculator(pSize);
		});
	}

	@Test
	public void tContrastParamTestThrowsOnImageNotMultipOfPatchSize() {
		int height = 63;
		int width = 63;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new TContrastParamCalculator(pSize);
		double[][] img = new double[height][width];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void tContrastParamTestThrowsOnNullImage() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new TContrastParamCalculator(pSize);
		double[][] img = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void tContrastParamTestThrowsOnNullImage2() {
		int height = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new TContrastParamCalculator(pSize);
		double[][] img = new double[height][];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void tContrastParamTestThrowsOnImageNotSquare() {
		int height = 64;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new TContrastParamCalculator(pSize);
		double[][] img = new double[height][width];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void tContrastParamTestOutputOnZeroPatchSize() {
		int width = 128;
		int height = 128;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._0;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;

		IParamCalculator calc = new TContrastParamCalculator(pSize);
		outMatrix = calc.calculateParameter(img);
		assertTrue(outMatrix.length == 1 && outMatrix[0].length == 1);
	}

	@Test
	public void tContrastParamTestOutputValue() {

		int width = 64;
		int height = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;
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
				counter++;
			}
		}
		IParamCalculator calc = new TContrastParamCalculator(pSize);
		outMatrix = calc.calculateParameter(img);

		/*
		 * The actual value is calculated in R using the TContrast formula explained in
		 * TContrastParamCalculator: b <- c(rep(x = 0, 500), rep(x = 100, 500), rep(x =
		 * 30, 3096)) m4 <- kurtosis(b, type = 2) ans <- var(b) / (m4 ^ 0.25)
		 * 
		 */
		assertEquals(569.8427, outMatrix[0][0], 0.001);
	}

	@Test
	public void tContrastParamTestOutputSizeGreaterThanOne() {
		int height = 128;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		int patchSize = pSize.getSize();
		double[][] img = new double[height][width];

		IParamCalculator calc = new TContrastParamCalculator(pSize);
		double[][] outMatrix = calc.calculateParameter(img);

		assertTrue(outMatrix.length == height / patchSize && outMatrix[0].length == width / patchSize);
	}

}
