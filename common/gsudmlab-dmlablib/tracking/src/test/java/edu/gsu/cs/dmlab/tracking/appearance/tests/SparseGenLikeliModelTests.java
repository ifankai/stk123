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

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.locationtech.jts.geom.Envelope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.tracking.appearance.SparseGenLikeliModel;

import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;

public class SparseGenLikeliModelTests {

	@Test
	public void testThrowsOnPatchSizeLessThanOne() {
		int patchSize = 0;
		int paramDownSample = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			SparseGenLikeliModel lik = new SparseGenLikeliModel(patchSize, paramDownSample);
		});
	}

	@Test  
	public void testThrowsOnParamDownSampleLessThanOne() {
		int patchSize = 2;
		int paramDownSample = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			SparseGenLikeliModel lik = new SparseGenLikeliModel(patchSize, paramDownSample);
		});
	}
  
	@Test
	public void testReturnValueWhenScaleSameAndLargerThanMin() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseGenLikeliModel lik = new SparseGenLikeliModel(patchSize, paramDownSample);

		Envelope bbox = new Envelope();
		bbox.init(0, 5, 0, 5);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 2;
		data[1][2] = 2;
		data[1][3] = 2;

		DenseMatrix errorMatCandidate = new JMatrix(data);

		double ans = -1.322;
		double val = lik.getCandidateProb(errorMatCandidate, bbox, bbox);
		assertEquals(ans, val, 0.01);
	}

	@Test
	public void testReturnValueWhenTargLessThanMin() {  
		int patchSize = 4;
		int paramDownSample = 1;

		SparseGenLikeliModel lik = new SparseGenLikeliModel(patchSize, paramDownSample);

		Envelope bbox = new Envelope();
		bbox.init(0, 5, 0, 5);

		Envelope bbox2 = new Envelope();
		bbox2.init(0, 4, 0, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 2;
		data[1][2] = 2;
		data[1][3] = 2;

		DenseMatrix errorMatCandidate = new JMatrix(data);

		double ans = -1.322;
		double val = lik.getCandidateProb(errorMatCandidate, bbox, bbox2);
		assertEquals(ans, val, 0.01);
	}

	@Test
	public void testReturnValueWhenScaleSameAndLessThanMin() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseGenLikeliModel lik = new SparseGenLikeliModel(patchSize, paramDownSample);

		Envelope bbox = new Envelope();
		bbox.init(0, 4, 0, 4);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 2;
		data[1][2] = 2;
		data[1][3] = 2;

		DenseMatrix errorMatCandidate = new JMatrix(data);

		double ans = -1.58;
		double val = lik.getCandidateProb(errorMatCandidate, bbox, bbox);
		assertEquals(ans, val, 0.01);
	}

	@Test
	public void testReturnValueWhenOrigLessThanMin() {
		int patchSize = 4;
		int paramDownSample = 1;

		SparseGenLikeliModel lik = new SparseGenLikeliModel(patchSize, paramDownSample);

		Envelope bbox = new Envelope();
		bbox.init(0, 4, 0, 4);

		Envelope bbox2 = new Envelope();
		bbox2.init(0, 5, 0, 5);

		double[][] data = new double[16][4];
		data[1][0] = 1;
		data[1][1] = 2;
		data[1][2] = 2;
		data[1][3] = 2;

		DenseMatrix errorMatCandidate = new JMatrix(data);

		double ans = -1.58;
		double val = lik.getCandidateProb(errorMatCandidate, bbox, bbox2);
		assertEquals(ans, val, 0.01);
	}
}
