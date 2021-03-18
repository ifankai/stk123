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
package edu.gsu.cs.dmlab.tracking.appearance.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;

import edu.gsu.cs.dmlab.tracking.appearance.SparseHistoCreator;
import smile.math.matrix.SparseMatrix;

public class SparseHistoCreatorTests {

	@Test
	public void testThrowsOnPatchSizeLessThanOne() {
		int patchSize = 0;
		int paramDownSample = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		});
	}

	@Test
	public void testThrowsOnParamDownSampleLessThanOne() {
		int patchSize = 2;
		int paramDownSample = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		});
	}

	@Test
	public void testAllCoeffsInSameRow() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 0, 5, 5);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 1;
		data[1][2] = 1;
		data[1][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createTargetHisto(alpha, bbox);
		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 1;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testAllCoeffsInSameRowObjectSmallerThanMin() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 0, 4, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 1;
		data[1][2] = 1;
		data[1][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createTargetHisto(alpha, bbox);
		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 1;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testCoeffsInDiffRows() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 5, 0, 5);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[2][1] = 1;
		data[3][2] = 1;
		data[4][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createTargetHisto(alpha, bbox);

		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 0.215;
		ansArr[2] = 0.251;
		ansArr[3] = 0.251;
		ansArr[4] = 0.28;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testCoeffsInDiffRowsObjectSmallerThanMin() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 4, 0, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[2][1] = 1;
		data[3][2] = 1;
		data[4][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createTargetHisto(alpha, bbox);

		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 0.215;
		ansArr[2] = 0.251;
		ansArr[3] = 0.251;
		ansArr[4] = 0.28;

		assertArrayEquals(ansArr, hist, 0.01);
	}

	// ********Testing Candidate Method***************

	@Test
	public void testAllCoeffsInSameRowCandidate() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 4, 0, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 1;
		data[1][2] = 1;
		data[1][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createCandidateHisto(alpha, bbox, bbox);
		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 1;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testAllCoeffsInSameRowObjectSmallerThanMinCandidate() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 4, 0, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 1;
		data[1][2] = 1;
		data[1][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createCandidateHisto(alpha, bbox, bbox);
		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 1;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testAllCoeffsInSameRowTargetObjectSmallerThanMinCandidateObjectOk() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 0, 4, 4);

		Envelope bbox2 = new Envelope();
		bbox2.init(0, 0, 5, 5);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 1;
		data[1][2] = 1;
		data[1][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createCandidateHisto(alpha, bbox, bbox2);
		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 1;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testAllCoeffsInSameRowTargetObjectGreaterThanMinCandidateObjectLess() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 0, 5, 5);

		Envelope bbox2 = new Envelope();
		bbox2.init(0, 0, 4, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 1;
		data[1][2] = 1;
		data[1][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createCandidateHisto(alpha, bbox, bbox2);
		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 1;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testCoeffsInDiffRowsCandidate() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 5, 0, 5);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[2][1] = 1;
		data[3][2] = 1;
		data[4][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createCandidateHisto(alpha, bbox, bbox);

		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 0.215;
		ansArr[2] = 0.251;
		ansArr[3] = 0.251;
		ansArr[4] = 0.28;

		assertArrayEquals(ansArr, hist, 0.005);
	}

	@Test
	public void testCoeffsInDiffRowsObjectSmallerThanMinCandidate() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseHistoCreator creator = new SparseHistoCreator(patchSize, paramDownSample);
		Envelope bbox = new Envelope();
		bbox.init(0, 4, 0, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[2][1] = 1;
		data[3][2] = 1;
		data[4][3] = 1;
		SparseMatrix alpha = new SparseMatrix(data);

		double[] hist = creator.createCandidateHisto(alpha, bbox, bbox);

		double[] ansArr = new double[16];
		Arrays.fill(ansArr, 0);
		ansArr[1] = 0.215;
		ansArr[2] = 0.251;
		ansArr[3] = 0.251;
		ansArr[4] = 0.28;

		assertArrayEquals(ansArr, hist, 0.01);
	}

}
