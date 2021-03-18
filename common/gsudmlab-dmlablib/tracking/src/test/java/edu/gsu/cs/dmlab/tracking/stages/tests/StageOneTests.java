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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.ISTEventTrackingFactory;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.tracking.stages.StageOne;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTProcessingStage;
import edu.gsu.cs.dmlab.util.interfaces.ISTSearchAreaProducer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

public class StageOneTests {

	@Test
	public void testStageOneThrowsOnNullSearchAreaProducer() throws IllegalArgumentException {
		// ISTSearchAreaProducer searchAreaProducer =
		// mock(ISTSearchAreaProducer.class);
		ISTTrackingEventIndexer eventIndexer = mock(ISTTrackingEventIndexer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage stage = new StageOne(null, eventIndexer, factory, numThreads);
		});
	}

	@Test
	public void testStageOneThrowsOnNullEventIndexer() throws IllegalArgumentException {
		ISTSearchAreaProducer searchAreaProducer = mock(ISTSearchAreaProducer.class);
		// ISTTrackingEventIndexer eventIndexer = mock(ISTTrackingEventIndexer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage stage = new StageOne(searchAreaProducer, null, factory, numThreads);
		});
	}

	@Test
	public void testStageOneThrowsOnNullFactory() throws IllegalArgumentException {
		ISTSearchAreaProducer searchAreaProducer = mock(ISTSearchAreaProducer.class);
		ISTTrackingEventIndexer eventIndexer = mock(ISTTrackingEventIndexer.class);
		// ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		int numThreads = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage stage = new StageOne(searchAreaProducer, eventIndexer, null, numThreads);
		});
	}

	@Test
	public void testStageOneThrowsOnNumThreadsLessThanNegOne() throws IllegalArgumentException {
		ISTSearchAreaProducer searchAreaProducer = mock(ISTSearchAreaProducer.class);
		ISTTrackingEventIndexer eventIndexer = mock(ISTTrackingEventIndexer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		int numThreads = -2;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage stage = new StageOne(searchAreaProducer, eventIndexer, factory, numThreads);
		});
	}

	@Test
	public void testStageOneThrowsOnNumThreadsZero() throws IllegalArgumentException {
		ISTSearchAreaProducer searchAreaProducer = mock(ISTSearchAreaProducer.class);
		ISTTrackingEventIndexer eventIndexer = mock(ISTTrackingEventIndexer.class);
		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		int numThreads = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTProcessingStage stage = new StageOne(searchAreaProducer, eventIndexer, factory, numThreads);
		});
	}

	@Test
	public void testStageOneReturnsEmptyListOnNoEvents() {
		ISTSearchAreaProducer searchAreaProducer = mock(ISTSearchAreaProducer.class);

		ArrayList<ISTTrackingEvent> events = new ArrayList<ISTTrackingEvent>();
		ISTTrackingEventIndexer eventIndexer = mock(ISTTrackingEventIndexer.class);
		when(eventIndexer.getAll()).thenReturn(events);

		ISTEventTrackingFactory factory = mock(ISTEventTrackingFactory.class);
		int numThreads = -1;
		ISTProcessingStage stage = new StageOne(searchAreaProducer, eventIndexer, factory, numThreads);
		List<ISTTrackingTrajectory> results = stage.process();
		assertTrue(results.size() == 0);
	}
}
