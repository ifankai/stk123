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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.MeanParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;

public class MeanParamCalculatorTests {

	@Test
	public void meanParamTestThrowsOnNullPatchSize() {

		IMeasures.PatchSize pSize = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new MeanParamCalculator(pSize);
		});
	}

	@Test
	public void meanParamTestThrowsOnImageNotSquare2() {
		int height = 64;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = new double[height][width];

		IParamCalculator mpc = new MeanParamCalculator(pSize);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			mpc.calculateParameter(img);
		});
	}

	@Test
	public void meanParamTestThrowsOnNullImage() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = null;

		IParamCalculator mpc = new MeanParamCalculator(pSize);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			mpc.calculateParameter(img);
		});
	}

	@Test
	public void meanParamTestThrowsOnNUllImage2() {
		int height = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = new double[height][];

		IParamCalculator mpc = new MeanParamCalculator(pSize);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			mpc.calculateParameter(img);
		});
	}

	@Test
	public void meanParamTestOutputOnZeroPatchSize() {
		int width = 128;
		int height = 128;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._0;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;

		IParamCalculator mpc = new MeanParamCalculator(pSize);
		outMatrix = mpc.calculateParameter(img);
		assertTrue(outMatrix.length == 1 && outMatrix[0].length == 1);
	}

	@Test
	public void meanParamTestOutputValue() {

		int height = 64;
		int width = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		double[][] img = new double[height][width];
		double[][] outMatrix = null;

		for (int row = 0; row < img.length; row++) {
			for (int col = 0; col < img[0].length; col++) {
				img[row][col] = 64;
				if (row < (height / 2) || col < (width / 2)) {
					img[row][col] = 66;
				}
			}
		}
		IParamCalculator mpc = new MeanParamCalculator(pSize);
		outMatrix = mpc.calculateParameter(img);
		assertArrayEquals(new double[][] { { 65.5 } }, outMatrix);
	}

	@Test
	public void meanParamTestOutputSizeGreaterThanOne2() {

		int height = 128;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		int patchSize = pSize.getSize();
		double[][] img = new double[height][width];
		double[][] outMatrix = null;

		IParamCalculator mpc = new MeanParamCalculator(pSize);
		outMatrix = mpc.calculateParameter(img);

		assertTrue(outMatrix.length == height / patchSize && outMatrix[0].length == width / patchSize);
	}

}
