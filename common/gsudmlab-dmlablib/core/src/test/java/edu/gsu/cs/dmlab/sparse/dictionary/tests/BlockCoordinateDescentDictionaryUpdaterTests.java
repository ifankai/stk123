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
package edu.gsu.cs.dmlab.sparse.dictionary.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.sparse.dictionary.BlockCoordinateDescentDictionaryUpdater;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryUpdater;
import smile.math.matrix.JMatrix;

public class BlockCoordinateDescentDictionaryUpdaterTests {

	@Test
	public void testBlockCoordianteDictionaryUpdaterThrowsOnTooSmallEpsilon() {

		float epsilon = 0;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			IDictionaryUpdater updater = new BlockCoordinateDescentDictionaryUpdater(epsilon);
		});
	}

	@Test
	public void testBlockCoordinateDictionaryUpdaterThrowsWhenDictionaryAndAuxMatVMismatch()
			throws VectorDimensionMismatch, MatrixDimensionMismatch {

		float epsilon = 10;
		IDictionaryUpdater updater = new BlockCoordinateDescentDictionaryUpdater(epsilon);

		JMatrix dictionary = mock(JMatrix.class);
		when(dictionary.nrows()).thenReturn(2);

		JMatrix auxMatrixV = mock(JMatrix.class);
		when(auxMatrixV.nrows()).thenReturn(1);

		JMatrix auxMatrixU = mock(JMatrix.class);

		Assertions.assertThrows(MatrixDimensionMismatch.class, () -> {
			updater.updateDictionary(dictionary, auxMatrixU, auxMatrixV);
		});

	}

	@Test
	public void testBlockCoordinateDictionaryUpdaterThrowsWhenDictionaryAndAuxMatUMismatchRow()
			throws VectorDimensionMismatch, MatrixDimensionMismatch {

		float epsilon = 10;
		IDictionaryUpdater updater = new BlockCoordinateDescentDictionaryUpdater(epsilon);

		JMatrix dictionary = mock(JMatrix.class);
		when(dictionary.nrows()).thenReturn(2);
		when(dictionary.ncols()).thenReturn(2);

		JMatrix auxMatrixV = mock(JMatrix.class);
		when(auxMatrixV.nrows()).thenReturn(2);

		JMatrix auxMatrixU = mock(JMatrix.class);
		when(auxMatrixU.nrows()).thenReturn(1);
		when(auxMatrixU.ncols()).thenReturn(2);

		Assertions.assertThrows(MatrixDimensionMismatch.class, () -> {
			updater.updateDictionary(dictionary, auxMatrixU, auxMatrixV);
		});

	}

	@Test
	public void testBlockCoordinateDictionaryUpdaterThrowsWhenDictionaryAndAuxMatUMismatchCol()
			throws VectorDimensionMismatch, MatrixDimensionMismatch {

		float epsilon = 10;
		IDictionaryUpdater updater = new BlockCoordinateDescentDictionaryUpdater(epsilon);

		JMatrix dictionary = mock(JMatrix.class);
		when(dictionary.nrows()).thenReturn(2);
		when(dictionary.ncols()).thenReturn(2);

		JMatrix auxMatrixV = mock(JMatrix.class);
		when(auxMatrixV.nrows()).thenReturn(2);

		JMatrix auxMatrixU = mock(JMatrix.class);
		when(auxMatrixU.nrows()).thenReturn(2);
		when(auxMatrixU.ncols()).thenReturn(1);

		Assertions.assertThrows(MatrixDimensionMismatch.class, () -> {
			updater.updateDictionary(dictionary, auxMatrixU, auxMatrixV);
		});

	}

	@Test
	public void testBlockCoordinateDictionaryUpdater() throws VectorDimensionMismatch, MatrixDimensionMismatch {

		float epsilon = (float) 0.005;
		IDictionaryUpdater updater = new BlockCoordinateDescentDictionaryUpdater(epsilon);

		double[][] data = { { 1, 3 }, { 2, 4 } };
		JMatrix dictionary = new JMatrix(data);
		double[][] data2 = { { 1, 1 }, { 1, 1 } };
		JMatrix auxMatrixU = new JMatrix(data2);
		double[][] data3 = { { 1, 1 }, { 1, 1 } };
		JMatrix auxMatrixV = new JMatrix(data3);

		updater.updateDictionary(dictionary, auxMatrixU, auxMatrixV);
		double[][] ans = { { 0.35, 0.65 }, { 0.23, 0.76 } };
		assertArrayEquals(ans[0], dictionary.array()[0], 0.01);
		assertArrayEquals(ans[1], dictionary.array()[1], 0.01);
	}

}
