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

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.UniformityParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;

public class UniformityParamCalculatorTests {

	double minPixelVal = 0;
	double maxPixelVal = 16384 - 1;
	int numberOfBins = 64 * 64;

	@Test
	public void uniformityParamTestThrowsOnNullPatchSize() {

		IMeasures.PatchSize pSize = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		});
	}

	@Test
	public void uniformityParamTestThrowsOnImageNotMultipOfPatchSize() {

		int height = 63;
		int width = 63;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		double[][] img = new double[height][width];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void uniformityParamTestThrowsOnNullImage() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		double[][] img = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void uniformityParamTestThrowsOnNullImage2() {

		int height = 64;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		double[][] img = new double[height][];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void uniformityParamTestThrowsOnImageNotSquare() {

		int height = 64;
		int width = 128;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		double[][] img = new double[height][width];
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void uniformityParamTestOutputOnZeroPatchSize() {

		int width = 128;
		int height = 128;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._0;
		double[][] img = new double[height][width];
		double[][] outMatrix = null;
		Random rand = new Random();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				img[row][col] = rand.nextInt(256);// in [0,255]
			}
		}

		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		outMatrix = calc.calculateParameter(img);
		assertTrue(outMatrix.length == 1 && outMatrix[0].length == 1);
	}

	@Test
	public void uniformityParamTestOutputValue() {

		int width = 64;
		int height = 64;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		double[][] img = new double[height][width];
		double[][] outMatrix = null;
		int counter = 0;
		for (int row = 0; row < img.length; row++) {
			for (int col = 0; col < img[0].length; col++) {
				img[row][col] = counter;
				counter++;
			}
		}
		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		outMatrix = calc.calculateParameter(img);

		/*
		 * Depending on the bin size set in class 'UniformityParamCalculator' the final
		 * calculation might be different. (For nOfBins = 4096 uniformity is equal to
		 * -SUM_{0}^{4096}{(1/4096)^2} = - 1/4096 = -0.0002441406
		 * 
		 */
		assertEquals(0.00097, outMatrix[0][0], 0.0001);
	}

	@Test
	public void uniformityParamTestOutputSizeGreaterThanOne() {

		int height = 1024;
		int width = 1024;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		int patchSize = pSize.getSize();
		double[][] img = new double[height][width];
		Random rand = new Random();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				img[row][col] = rand.nextInt(256);// in [0,255]
			}
		}

		IParamCalculator calc = new UniformityParamCalculator(pSize, numberOfBins, minPixelVal, maxPixelVal);
		double[][] outMatrix = calc.calculateParameter(img);

		assertTrue(outMatrix.length == height / patchSize && outMatrix[0].length == width / patchSize);
	}
}