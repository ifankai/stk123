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
import static org.mockito.Mockito.*;
import org.locationtech.jts.geom.Envelope;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import edu.gsu.cs.dmlab.databases.interfaces.ISTImageDBConnection;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgPatchVectorizer;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseMatrixApproximator;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.ISparseDictionaryLearner;
import edu.gsu.cs.dmlab.tracking.appearance.SparseHistoAppearanceModel;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTSparseCandidateModel;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTSparseHistoCreator;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;
import smile.math.matrix.SparseMatrix;

public class SparseHistoAppearanceModelTests {
 
	@Test  
	public void testThrowsOnNullDictionaryLearner() {
		ISparseDictionaryLearner dictionaryLearner = null;
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 1; 

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnNullMatrixApproximator() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = null;
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnNullPatchVectorizer() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = null;
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnNullHistoCreator() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = null;
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnNullCandidateModel() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = null;
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnNullDBConnection() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = null;
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnNullParamPairArray() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = null;
		int cacheSize = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testThrowsOnCacheSizeLessThanZero() {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
					histoCreator, candidateModel, imageDB, params, cacheSize);
		});

	}

	@Test
	public void testOutput() throws MatrixDimensionMismatch, VectorDimensionMismatch, SQLException {
		ISparseDictionaryLearner dictionaryLearner = mock(ISparseDictionaryLearner.class);
		ISparseMatrixApproximator coefExtractor = mock(ISparseMatrixApproximator.class);
		IImgPatchVectorizer patchVectorizer = mock(IImgPatchVectorizer.class);
		ISTSparseHistoCreator histoCreator = mock(ISTSparseHistoCreator.class);
		ISTSparseCandidateModel candidateModel = mock(ISTSparseCandidateModel.class);
		ISTImageDBConnection imageDB = mock(ISTImageDBConnection.class);
		ImageDBWaveParamPair[] params = new ImageDBWaveParamPair[0];
		int cacheSize = 0;

		ISTAppearanceModel model = new SparseHistoAppearanceModel(dictionaryLearner, coefExtractor, patchVectorizer,
				histoCreator, candidateModel, imageDB, params, cacheSize);
		ISTTrackingTrajectory leftTrack = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent leftEv = mock(ISTTrackingEvent.class);
		when(leftTrack.getLast()).thenReturn(leftEv);
		UUID uid = UUID.randomUUID();
		when(leftEv.getUUID()).thenReturn(uid);
		Envelope trainBbox = new Envelope();
		when(leftEv.getEnvelope()).thenReturn(trainBbox);

		ISTTrackingTrajectory rightTrack = mock(ISTTrackingTrajectory.class);
		ISTTrackingEvent rightEv = mock(ISTTrackingEvent.class);
		when(rightTrack.getFirst()).thenReturn(rightEv);
		Envelope targetCandidateBbox = new Envelope();
		when(rightEv.getEnvelope()).thenReturn(targetCandidateBbox);

		DenseMatrix[] dimMatArrOrig = new DenseMatrix[0];
		DenseMatrix[] dimMatArrTarg = new DenseMatrix[0];
		when(imageDB.getImageParamForEv(eq(leftEv), any(ImageDBWaveParamPair[].class), eq(true)))
				.thenReturn(dimMatArrOrig);
		when(imageDB.getImageParamForEv(eq(rightEv), any(ImageDBWaveParamPair[].class), eq(false)))
				.thenReturn(dimMatArrTarg);

		JMatrix dataMatOrig = new JMatrix(new double[1][1]);
		JMatrix dataMatTarg = new JMatrix(new double[1][1]);
		when(patchVectorizer.vectorize(dimMatArrOrig)).thenReturn(dataMatOrig);
		when(patchVectorizer.vectorize(dimMatArrTarg)).thenReturn(dataMatTarg);

		JMatrix dictionary = new JMatrix(new double[1][1]);
		when(dictionaryLearner.train(any())).thenReturn(dictionary);

		// SparseMatrix alphaOrig = new SparseMatrix(new double[1][1]);
		SparseMatrix alphaTarg = new SparseMatrix(new double[1][1]);
		// when(coefExtractor.estimateCoeffs(dataMatOrig,
		// dictionary)).thenReturn(alphaOrig);
		when(coefExtractor.estimateCoeffs(any(), any())).thenReturn(alphaTarg);

		double[] sourceModelHisto = new double[] { 1 };
		when(histoCreator.createTargetHisto(any(), any())).thenReturn(sourceModelHisto);
		double[] targetCandidateModelHisto = new double[] { 1 };
		when(histoCreator.createCandidateHisto(any(), any(), any())).thenReturn(targetCandidateModelHisto);

		when(candidateModel.getCandidateProb(any(), any(), any())).thenReturn(-1.322);
		double val = model.calcProbAppearance(leftTrack, rightTrack);
		assertEquals(0.998, val, 1.001);
	}

}
