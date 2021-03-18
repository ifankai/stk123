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
package edu.gsu.cs.dmlab.tracking.appearance;

import smile.math.Math;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.locationtech.jts.geom.Envelope;
import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import edu.gsu.cs.dmlab.databases.interfaces.ISTImageDBConnection;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgPatchVectorizer;
import edu.gsu.cs.dmlab.math.LAFunctions;
import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseMatrixApproximator;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.ISparseDictionaryLearner;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTSparseCandidateModel;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTSparseHistoCreator;

import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * SparseHistoAppearanceModel compares the visual similarity of two tracks based
 * on their sparse histogram similarity.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SparseHistoAppearanceModel implements ISTAppearanceModel {
 
	private ISparseDictionaryLearner dictionaryLearner;
	private ISTImageDBConnection imageDB;
	private IImgPatchVectorizer patchVectorizer;
	private ISTSparseHistoCreator histoCreator;
	private ImageDBWaveParamPair[] params;
	private ISparseMatrixApproximator coefExtractor;
	private ISTSparseCandidateModel candidateModel;
	private HashFunction hashFunct = Hashing.murmur3_128();
	private LoadingCache<CacheKey, DenseMatrix> cache = null;

	private class CacheKey {
		String key;
		HashCode hc = null;
		DenseMatrix dataMatOrig;

		public CacheKey(String uuidString, DenseMatrix dataMatOrig, HashFunction hashFunct) {
			this.key = uuidString;
			this.hc = hashFunct.newHasher().putString(this.key, Charsets.UTF_8).hash();
			this.dataMatOrig = dataMatOrig;

		}

		@Override
		public void finalize() {
			this.hc = null;
			this.key = null;
		}

		public DenseMatrix getdataMatOrig() {
			return this.dataMatOrig;
		}

		public String getKey() {
			return this.key;
		}

		@Override
		public int hashCode() {
			return this.hc.asInt();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CacheKey) {
				CacheKey val = (CacheKey) obj;
				return val.getKey().compareTo(this.key) == 0;
			} else {
				return false;
			}
		}

	}

	/**
	 * Constructor
	 * 
	 * @param dictionaryLearner The class that is used to learn a sparse dictionary
	 *                          based upon the target area input.
	 * @param coefExtractor     The class used to generate the sparse approximation
	 *                          of the areas of interest using the learned
	 *                          dictionary from the target area.
	 * @param patchVectorizer   The class used to extract the column vectors of
	 *                          patches within the areas of interest.
	 * @param histoCreator2      The class used to create a histogram of the
	 *                          coefficients from the sparse approximation of the
	 *                          areas of interest.
	 * @param candidateModel2    The class used to determine the likelihood of a
	 *                          signal being generated at random from the learned
	 *                          dictionary.
	 * @param imageDB2           The connection to the image database to pull the
	 *                          image parameters for each area of interest.
	 * @param params            A set of image parameter wavelength pairs that tell
	 *                          this object which ones to use from the database.
	 * @param cacheSize         How many target dictionaries to store in the cache
	 *                          of this object, used so the dictionary doesn't need
	 *                          to be calculated every time another candidate is
	 *                          processed for a given target.
	 */
	public SparseHistoAppearanceModel(ISparseDictionaryLearner dictionaryLearner,
			ISparseMatrixApproximator coefExtractor, IImgPatchVectorizer patchVectorizer,
			ISTSparseHistoCreator histoCreator2, ISTSparseCandidateModel candidateModel2, ISTImageDBConnection imageDB2,
			ImageDBWaveParamPair[] params, int cacheSize) {
  
		if (dictionaryLearner == null)
			throw new IllegalArgumentException("Sparse Dictionary Learner cannot be null.");
		if (coefExtractor == null)
			throw new IllegalArgumentException("Coefficient Approximator cannot be null.");
		if (patchVectorizer == null)
			throw new IllegalArgumentException("Patch Vectorizer cannot be null.");
		if (histoCreator2 == null)
			throw new IllegalArgumentException("Sparse Histogram Creator cannot be null.");
		if (candidateModel2 == null)
			throw new IllegalArgumentException("Sparse CandidateModel cannot be null.");
		if (imageDB2 == null)
			throw new IllegalArgumentException("Image Database Connection cannot be null.");
		if (params == null)
			throw new IllegalArgumentException("The list of Parameters cannot be null.");
		if (cacheSize < 0)
			throw new IllegalArgumentException("Cache size cannot be less than zero.");

		this.dictionaryLearner = dictionaryLearner;
		this.patchVectorizer = patchVectorizer;
		this.histoCreator = histoCreator2;
		this.coefExtractor = coefExtractor;
		this.candidateModel = candidateModel2;
		this.imageDB = imageDB2;
		this.params = params;

		this.cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new CacheLoader<CacheKey, DenseMatrix>() {
			public DenseMatrix load(CacheKey key) throws MatrixDimensionMismatch, VectorDimensionMismatch {
				return fetchDictionary(key);
			}
		});
	}

	@Override
	public void finalize() throws Throwable {
		this.dictionaryLearner = null;
		this.imageDB = null;
		this.patchVectorizer = null;
		this.histoCreator = null;
		this.params = null;
		this.coefExtractor = null;
		this.candidateModel = null;
		this.hashFunct = null;
		this.cache.cleanUp();
		this.cache = null;
	}

	@Override
	public double calcProbAppearance(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {

		try {
			DenseMatrix[] dimMatArrOrig = this.imageDB.getImageParamForEv(leftTrack.getLast(), this.params, true);
			DenseMatrix[] dimMatArrTarg = this.imageDB.getImageParamForEv(rightTrack.getFirst(), this.params, false);

			// Get the patches of the original object and normalize the resultant
			// vectors
			DenseMatrix dataMatOrig = this.patchVectorizer.vectorize(dimMatArrOrig);
			double[] dataMatMeans = dataMatOrig.colMeans();
			for (int i = 0; i < dataMatMeans.length; i++)
				for (int j = 0; j < dataMatOrig.nrows(); j++) {
					dataMatOrig.sub(j, i, dataMatMeans[i]);
				}

			// Get the patches of the target object and normalize the resultant
			// vectors
			DenseMatrix dataMatTarg = this.patchVectorizer.vectorize(dimMatArrTarg);
			double[] dataMatTargMeans = dataMatTarg.colMeans();
			for (int i = 0; i < dataMatTargMeans.length; i++)
				for (int j = 0; j < dataMatTarg.nrows(); j++) {
					dataMatTarg.sub(j, i, dataMatTargMeans[i]);
				}

			CacheKey key = new CacheKey(leftTrack.getLast().getUUID().toString(), dataMatOrig, this.hashFunct);
			JMatrix dictionary = (JMatrix) this.cache.get(key);

			SparseMatrix alphaOrig = this.coefExtractor.estimateCoeffs(dataMatOrig, dictionary);
			SparseMatrix alphaTarg = this.coefExtractor.estimateCoeffs(dataMatTarg, dictionary);

			// Recreate the target based upon the coefficients produced with the
			// dictionary.
			DenseMatrix recreationTarg = LAFunctions.abmm(dictionary, alphaTarg);

			// Find the error between the recreation of the target and the
			// actual target.
			DenseMatrix errorTarg = new JMatrix(dataMatTarg.nrows(), dataMatTarg.ncols());
			errorTarg = recreationTarg.sub(dataMatTarg, errorTarg);

			// compute sparse histo for source
			Envelope trainBbox = leftTrack.getLast().getEnvelope();
			double[] sourceModelHisto = this.histoCreator.createTargetHisto(alphaOrig, trainBbox);

			// compute sparse histo for target
			Envelope targetCandidateBbox = rightTrack.getFirst().getEnvelope();
			double[] targetCandidateModelHisto = this.histoCreator.createCandidateHisto(alphaTarg, trainBbox,
					targetCandidateBbox);

			double targProb = this.candidateModel.getCandidateProb(errorTarg, trainBbox, targetCandidateBbox);

			float sumLikely = 0;
			for (int i = 0; i < sourceModelHisto.length; i++) {
				sumLikely += Math.sqrt(sourceModelHisto[i] * targetCandidateModelHisto[i]);
			}

			// System.out.println("TargProb: " + Math.exp(targProb));
			// System.out.println("SumLikli: " + sumLikely);
			double prod = sumLikely * Math.exp(targProb);
			// System.out.println("Prod: " + prod);
			// System.out.println("Appearance: " + this.sigAppear(prod));
			return this.sigAppear(prod);

		} catch (ExecutionException | VectorDimensionMismatch | MatrixDimensionMismatch | SQLException e) {
			// if we get here then there is something messed up with the
			// dictionary learning.
			e.printStackTrace();

			// If we don't have a dictionary then who knows. Just give it a
			// 50/50
			return 0.5;
		}

	}

	private double sigAppear(double val) {
		double retVal = 1.0 / (1 + Math.exp(-4 * ((val) - 0.4)));
		return retVal;
	}

	private DenseMatrix fetchDictionary(CacheKey key) throws MatrixDimensionMismatch, VectorDimensionMismatch {

		List<double[]> dataList = new ArrayList<double[]>();
		DenseMatrix dataMat = key.getdataMatOrig();
		for (int j = 0; j < dataMat.ncols(); j++) {
			double[] dataCol = new double[dataMat.nrows()];
			for (int i = 0; i < dataMat.nrows(); i++) {
				dataCol[i] = dataMat.get(i, j);
			}
			dataList.add(dataCol);
		}

		DenseMatrix dictionary = this.dictionaryLearner.train(dataList);
		return dictionary;
	}

}
