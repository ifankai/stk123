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

import edu.gsu.cs.dmlab.imageproc.imageparam.StdDeviationParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;
import smile.math.Math;

public class StdDevParamCalculatorTests {

	@Test
	public void stdDevParamTestThrowsOnNullPatchSize() {

		IMeasures.PatchSize pSize = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new StdDeviationParamCalculator(pSize);
		});
	}

	@Test
	public void stdDevParamTestThrowsOnImageNotSquare() {

		int height = 64;
		int width = 128;
		double[][] img = new double[height][width];

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new StdDeviationParamCalculator(pSize);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void stdDevParamTestThrowsOnNullImage() {

		double[][] img = null;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new StdDeviationParamCalculator(pSize);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void stdDevParamTestThrowsOnNullImage2() {
		int height = 64;

		double[][] img = new double[height][];

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		IParamCalculator calc = new StdDeviationParamCalculator(pSize);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			calc.calculateParameter(img);
		});
	}

	@Test
	public void meanParamTestOutputOnZeroPatchSize() {
		int width = 128;
		int height = 128;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._0;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;

		IParamCalculator calc = new StdDeviationParamCalculator(pSize);
		outMatrix = calc.calculateParameter(img);
		assertTrue(outMatrix.length == 1 && outMatrix[0].length == 1);
	}

	@Test
	public void stdDevParamTestOutputValue() {

		int height = 64;
		int width = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;
		double[] data = new double[height * width];

		for (int row = 0; row < img.length; row++) {
			for (int col = 0; col < img[0].length; col++) {
				img[row][col] = 64;
				if (row < (height / 2) || col < (width / 2)) {
					img[row][col] = 66;
				}
				data[col + (height * row)] = img[row][col];
			}
		}
		IParamCalculator calc = new StdDeviationParamCalculator(pSize);
		outMatrix = calc.calculateParameter(img);
		double ans = Math.sd(data);

		assertEquals(ans, outMatrix[0][0], 0.00015);
	}

	@Test
	public void stdDevParamTestOtputSizeGreaterThanOne() {
		int height = 128;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		int patchSize = pSize.getSize();
		double[][] img = new double[height][width];

		IParamCalculator calc = new StdDeviationParamCalculator(pSize);
		double[][] outMatrix = calc.calculateParameter(img);

		assertTrue(outMatrix.length == height / patchSize && outMatrix[0].length == width / patchSize);

	}
}
