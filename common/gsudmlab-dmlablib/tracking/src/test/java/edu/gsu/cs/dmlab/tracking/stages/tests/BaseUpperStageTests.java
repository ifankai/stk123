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
package edu.gsu.cs.dmlab.tracking.stages.tests;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.factory.interfaces.ISTEventTrackingFactory;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;
import edu.gsu.cs.dmlab.tracking.stages.BaseUpperStage;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTProcessingStage;
import edu.gsu.cs.dmlab.util.interfaces.ISTSearchAreaProducer;

public class BaseUpperStageTests {

	@Test
	public void testBaseUpperStageThrowsOnNullSearchAreaProducer() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = null;// mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 1;
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsOnNullEventTrackingFactory() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = null;// mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 1;
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsOnNullTrackIndexer() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = null;// mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 1;
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsOnNullEventIndexer() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = null;// mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 1;
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsOnFrameSkipLessThanZero() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = -1;
		int stage = 1;
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsStageLessThanOne() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 0;
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsOnNumThreadsLessThanNegOne() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 1;
		int numThreads = -2;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

	@Test
	public void testBaseUpperStageThrowsOnNumThreadsZero() throws IllegalArgumentException {
		ISTSearchAreaProducer predictor = mock(ISTSearchAreaProducer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		ISTTrackingTrajectoryIndexer tracksIdxr = mock(ISTTrackingTrajectoryIndexer.class);
		ISTTrackingEventIndexer evntIdxr = mock(ISTTrackingEventIndexer.class);
		int maxFrameSkip = 0;
		int stage = 1;
		int numThreads = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage baseStage = new BaseUpperStage(predictor, factory, tracksIdxr, evntIdxr, maxFrameSkip,
					stage, numThreads);
		});
	}

}
