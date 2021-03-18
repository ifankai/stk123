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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.edgedetection.CannyEdgeDetector;
import edu.gsu.cs.dmlab.imageproc.imageparam.FractalDimParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;
import edu.gsu.cs.dmlab.imageproc.interfaces.IEdgeDetector;

public class FractalDimParamCalculatorTests {

	@Test
	public void fractalDimParamTestThrowsOnNullPatchSize() {

		IMeasures.PatchSize pSize = null;
		IEdgeDetector ed = new CannyEdgeDetector(2.5f, 7.5f, 2f, 16, false);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new FractalDimParamCalculator(pSize, ed);
		});
	}

	@Test
	public void fractalDimParamTestThrowsOnNullEdgeDetector() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IEdgeDetector ed = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new FractalDimParamCalculator(pSize, ed);
		});
	}

	@Test
	public void fractalDimParamTestThrowsOnNullImage() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IEdgeDetector ed = mock(IEdgeDetector.class);
		double[][] image = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			IParamCalculator calc = new FractalDimParamCalculator(pSize, ed);
			calc.calculateParameter(image);
		});
	}

	@Test
	public void fractalDimParamTestThrowsOnNullImage2() {

		int height = 64;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IEdgeDetector ed = mock(IEdgeDetector.class);
		double[][] image = new double[height][];

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			IParamCalculator calc = new FractalDimParamCalculator(pSize, ed);
			calc.calculateParameter(image);
		});
	}

	@Test
	public void fractalDimParamTestThrowsOnImageNotMultipOfPatchSize() {

		int height = 63;
		int width = 63;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IEdgeDetector ed = mock(IEdgeDetector.class);
		double[][] image = new double[height][width];

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			IParamCalculator calc = new FractalDimParamCalculator(pSize, ed);
			calc.calculateParameter(image);
		});

	}

	@Test
	public void fractalDimParamTestThrowsOnImageNotSquare() {

		int height = 64;
		int width = 128;

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IEdgeDetector ed = mock(IEdgeDetector.class);
		double[][] image = new double[height][width];

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			IParamCalculator calc = new FractalDimParamCalculator(pSize, ed);
			calc.calculateParameter(image);
		});

	}

	@Test
	public void getAllSplitsTest1() {

		int[] expected = { 16, 8, 4, 2 };
		IEdgeDetector ed = mock(IEdgeDetector.class);
		FractalDimParamCalculator fDim = new FractalDimParamCalculator(IMeasures.PatchSize._64, ed);
		int[] result = fDim.getBoxSizes();
		assertArrayEquals(expected, result);
	}

	@Test
	public void getAllSplitsTest2() {

		int[] expected = { 64, 32, 16, 8, 4, 2 };
		IEdgeDetector ed = mock(IEdgeDetector.class);
		FractalDimParamCalculator fDim = new FractalDimParamCalculator(IMeasures.PatchSize._0, ed);
		int[] result = fDim.getBoxSizes();
		assertArrayEquals(expected, result);
	}

	@Test
	public void getMaxBoxSizeTest1() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;
		IEdgeDetector ed = mock(IEdgeDetector.class);
		FractalDimParamCalculator fDim = new FractalDimParamCalculator(pSize, ed);
		int result = fDim.getMaxBoxSize();
		assertEquals(result, 16);
	}

	@Test
	public void getMaxBoxSizeTest2() {

		IMeasures.PatchSize pSize = IMeasures.PatchSize._0;
		IEdgeDetector ed = mock(IEdgeDetector.class);
		FractalDimParamCalculator fDim = new FractalDimParamCalculator(pSize, ed);
		int result = fDim.getMaxBoxSize();
		assertEquals(result, 64);
	}

	/**
	 * This test makes sure that 'countBoxes' method correctly counts the number of
	 * boxes with some foreground on them.
	 */
	@Test
	public void fractalDimParamTestCountBoxesOutputValue() {

		int boxSize = 8;
		IMeasures.PatchSize pSize = IMeasures.PatchSize._64;

		// 1. Create a 64X64 image (matrix)
		double[][] data = new double[64][64];
		for (int row = 0; row < data.length; row++) {
			for (int col = 0; col < data[0].length; col++) {
				data[row][col] = 0.0;
			}
		}
		// 2. Add some white foreground to it ( 8i <= row <= 8(i+1)-1 )
		data[9][9] = 255;
		data[10][10] = 255;
		data[11][11] = 255;

		data[17][17] = 255;
		data[18][18] = 255;
		data[18][18] = 255;

		data[26][26] = 255;
		data[27][27] = 255;
		data[28][28] = 255;

		data[60][60] = 255;
		data[61][61] = 255;
		data[62][62] = 255;

		double[] image = Arrays.stream(data).flatMapToDouble(Arrays::stream).toArray();
		double[] colors = new double[] { 0.0, 255 };

		IEdgeDetector ed = mock(IEdgeDetector.class);
		FractalDimParamCalculator calc = new FractalDimParamCalculator(pSize, ed);

		double outputNBoxes = calc.countBoxes(image, 64, 64, boxSize, colors);

		double expectedNBoxes = 4;

		assertTrue(outputNBoxes == expectedNBoxes);
	}

}