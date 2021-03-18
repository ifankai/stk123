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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.ImgPatchVectorizer;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Matrix;

public class ImagePatchVectorizerTests {

	@Test
	public void testThrowsOnStepLessThanOne() {
		int step = 0;
		int patchSize = 2;
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);
		});
	}

	@Test
	public void testThrowsOnPatchSizeLessThanOne() {
		int step = 1;
		int patchSize = 0;
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
		@SuppressWarnings("unused")
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);
		});
	}

	@Test
	public void testReturnEmptyWhenZeroLength() {

		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(0);
		when(mockedImg.ncols()).thenReturn(0);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		assertTrue(ans.nrows() == 0);

	}

	@Test
	public void testReturnNonZeroLength() {

		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(2);
		when(mockedImg.ncols()).thenReturn(2);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		assertTrue(ans.ncols() > 0);
	}

	@Test
	public void testReturnNumberOfVals() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(2);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(0, 1)).thenReturn((double) 3);
		when(mockedImg.get(1, 1)).thenReturn((double) 4);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		assertTrue(ans.nrows() == 4);
	}

	@Test
	public void testReturnedValsForOnePatchOneInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(2);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(0, 1)).thenReturn((double) 3);
		when(mockedImg.get(1, 1)).thenReturn((double) 4);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[4];
		for (int i = 0; i < 4; i++)
			ansArr[i] = ans.get(i, 0);
		assertArrayEquals(new double[] { 1, 2, 3, 4 }, ansArr, 0.1);
	}

	@Test
	public void testFirstColVectReturnedValsForTwoPatchOneInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(3);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(2, 0)).thenReturn((double) 3);
		when(mockedImg.get(0, 1)).thenReturn((double) 4);
		when(mockedImg.get(1, 1)).thenReturn((double) 5);
		when(mockedImg.get(2, 1)).thenReturn((double) 6);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[4];
		for (int i = 0; i < 4; i++)
			ansArr[i] = ans.get(i, 0);
		assertArrayEquals(new double[] { 1, 2, 4, 5 }, ansArr, 0.1);
	}

	@Test
	public void testFirstColVectReturnedValsForTwoPatchColsOneInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(2);
		when(mockedImg.ncols()).thenReturn(3);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(0, 1)).thenReturn((double) 3);
		when(mockedImg.get(1, 1)).thenReturn((double) 4);
		when(mockedImg.get(0, 2)).thenReturn((double) 5);
		when(mockedImg.get(1, 2)).thenReturn((double) 6);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[4];
		for (int i = 0; i < 4; i++)
			ansArr[i] = ans.get(i, 0);
		assertArrayEquals(new double[] { 1, 2, 3, 4 }, ansArr, 0.1);
	}

	@Test
	public void testSecondColVectReturnedValsForTwoPatchOneInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(3);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(2, 0)).thenReturn((double) 3);
		when(mockedImg.get(0, 1)).thenReturn((double) 4);
		when(mockedImg.get(1, 1)).thenReturn((double) 5);
		when(mockedImg.get(2, 1)).thenReturn((double) 6);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[4];
		for (int i = 0; i < 4; i++)
			ansArr[i] = ans.get(i, 1);
		assertArrayEquals(new double[] { 2, 3, 5, 6 }, ansArr, 0.1);
	}

	@Test
	public void testSecondColVectReturnedValsForTwoPatchColsOneInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(2);
		when(mockedImg.ncols()).thenReturn(3);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(0, 1)).thenReturn((double) 3);
		when(mockedImg.get(1, 1)).thenReturn((double) 4);
		when(mockedImg.get(0, 2)).thenReturn((double) 5);
		when(mockedImg.get(1, 2)).thenReturn((double) 6);
		DenseMatrix[] imgArr = { mockedImg };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[4];
		for (int i = 0; i < 4; i++)
			ansArr[i] = ans.get(i, 1);
		assertArrayEquals(new double[] { 3, 4, 5, 6 }, ansArr, 0.1);
	}

	@Test
	public void testReturnedValsForOnePatchTwoInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(2);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(0, 1)).thenReturn((double) 3);
		when(mockedImg.get(1, 1)).thenReturn((double) 4);

		DenseMatrix mockedImg2 = mock(DenseMatrix.class);
		when(mockedImg2.nrows()).thenReturn(2);
		when(mockedImg2.ncols()).thenReturn(2);
		when(mockedImg2.get(0, 0)).thenReturn((double) 5);
		when(mockedImg2.get(1, 0)).thenReturn((double) 6);
		when(mockedImg2.get(0, 1)).thenReturn((double) 7);
		when(mockedImg2.get(1, 1)).thenReturn((double) 8);

		DenseMatrix[] imgArr = { mockedImg, mockedImg2 };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[8];
		for (int i = 0; i < 8; i++)
			ansArr[i] = ans.get(i, 0);
		assertArrayEquals(new double[] { 1, 5, 2, 6, 3, 7, 4, 8 }, ansArr, 0.1);
	}

	@Test
	public void testFirstColReturnedValsForTwoPatchTwoInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(3);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(2, 0)).thenReturn((double) 3);
		when(mockedImg.get(0, 1)).thenReturn((double) 4);
		when(mockedImg.get(1, 1)).thenReturn((double) 5);
		when(mockedImg.get(2, 1)).thenReturn((double) 6);

		DenseMatrix mockedImg2 = mock(DenseMatrix.class);
		when(mockedImg2.nrows()).thenReturn(3);
		when(mockedImg2.ncols()).thenReturn(2);
		when(mockedImg2.get(0, 0)).thenReturn((double) 7);
		when(mockedImg2.get(1, 0)).thenReturn((double) 8);
		when(mockedImg2.get(2, 0)).thenReturn((double) 9);
		when(mockedImg2.get(0, 1)).thenReturn((double) 10);
		when(mockedImg2.get(1, 1)).thenReturn((double) 11);
		when(mockedImg2.get(2, 1)).thenReturn((double) 12);

		DenseMatrix[] imgArr = { mockedImg, mockedImg2 };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[8];
		for (int i = 0; i < 8; i++)
			ansArr[i] = ans.get(i, 0);
		assertArrayEquals(new double[] { 1, 7, 2, 8, 4, 10, 5, 11 }, ansArr, 0.1);
	}

	@Test
	public void testSecondColReturnedValsForTwoPatchTwoInputMat() {
		int step = 1;
		int patchSize = 2;
		ImgPatchVectorizer vectorizer = new ImgPatchVectorizer(step, patchSize);

		DenseMatrix mockedImg = mock(DenseMatrix.class);
		when(mockedImg.nrows()).thenReturn(3);
		when(mockedImg.ncols()).thenReturn(2);
		when(mockedImg.get(0, 0)).thenReturn((double) 1);
		when(mockedImg.get(1, 0)).thenReturn((double) 2);
		when(mockedImg.get(2, 0)).thenReturn((double) 3);
		when(mockedImg.get(0, 1)).thenReturn((double) 4);
		when(mockedImg.get(1, 1)).thenReturn((double) 5);
		when(mockedImg.get(2, 1)).thenReturn((double) 6);

		DenseMatrix mockedImg2 = mock(DenseMatrix.class);
		when(mockedImg2.nrows()).thenReturn(3);
		when(mockedImg2.ncols()).thenReturn(2);
		when(mockedImg2.get(0, 0)).thenReturn((double) 7);
		when(mockedImg2.get(1, 0)).thenReturn((double) 8);
		when(mockedImg2.get(2, 0)).thenReturn((double) 9);
		when(mockedImg2.get(0, 1)).thenReturn((double) 10);
		when(mockedImg2.get(1, 1)).thenReturn((double) 11);
		when(mockedImg2.get(2, 1)).thenReturn((double) 12);

		DenseMatrix[] imgArr = { mockedImg, mockedImg2 };

		Matrix ans = vectorizer.vectorize(imgArr);
		double[] ansArr = new double[8];
		for (int i = 0; i < 8; i++)
			ansArr[i] = ans.get(i, 1);
		assertArrayEquals(new double[] { 2, 8, 3, 9, 5, 11, 6, 12 }, ansArr, 0.000001);
	}

}
