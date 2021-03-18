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

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.sparse.approximation.interfaces.ISparseVectorApproximator;
import edu.gsu.cs.dmlab.sparse.dictionary.OnlineDictionaryLearner;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryCleaner;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryUpdater;
import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.ISparseDictionaryLearner;

public class OnlineDictionaryLearnerTests {

	@Test
	public void testOnlineDictionaryThrowsOnBelowNeg1ThreadCountConstructor1() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);

		double fractionOfInputSize = 1.0;
		int batchSize = 1;
		int numThreads = -2;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnBelowNeg1ThreadCountConstructor2() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		int numElements = 2;
		int batchSize = 1;
		int numThreads = -2;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnZeroThreadCountConstructor1() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		double fractionOfInputSize = 1.0;
		int batchSize = 1;
		int numThreads = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnZeroThreadCountConstructor2() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		int numElements = 2;
		int batchSize = 1;
		int numThreads = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnNullVectorApproxConstructor1() {

		ISparseVectorApproximator leqSolver = null;
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		double fractionOfInputSize = 1.0;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnNullVectorApproxConstructor2() {

		ISparseVectorApproximator leqSolver = null;
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		int numElements = 2;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnNullDictionaryUpdaterConstructor1() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = null;
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		double fractionOfInputSize = 1.0;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnNullDictionaryUpdaterConstructor2() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = null;
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		int numElements = 2;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnNullDictionaryCleanerConstructor1() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = null;
		double fractionOfInputSize = 1.0;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsOnNullDictionaryCleanerConstructor2() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = null;
		int numElements = 2;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsBatchLessThanOneConstructor1() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		double fractionOfInputSize = 1.0;
		int batchSize = 0;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsBatchLessThanOneConstructor2() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		int numElements = 2;
		int batchSize = 0;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsFractionLessThanLimConstructor1() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		double fractionOfInputSize = 0.015;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					fractionOfInputSize, batchSize, numThreads);
		});
	}

	@Test
	public void testOnlineDictionaryThrowsNumElementsLessThanOneConstructor2() {

		ISparseVectorApproximator leqSolver = mock(ISparseVectorApproximator.class);
		IDictionaryUpdater dictionaryUpdater = mock(IDictionaryUpdater.class);
		IDictionaryCleaner cleaner = mock(IDictionaryCleaner.class);
		int numElements = 0;
		int batchSize = 1;
		int numThreads = 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISparseDictionaryLearner learner = new OnlineDictionaryLearner(leqSolver, dictionaryUpdater, cleaner,
					numElements, batchSize, numThreads);
		});
	}
}
