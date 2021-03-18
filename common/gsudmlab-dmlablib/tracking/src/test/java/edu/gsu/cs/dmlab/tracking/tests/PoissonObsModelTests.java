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
package edu.gsu.cs.dmlab.tracking.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.tracking.PoissonObsModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class PoissonObsModelTests {

	@Test
	public void testObsModelThrowsOnNullIndex() throws IllegalArgumentException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTObsModel model = new PoissonObsModel(null, 5);
		});
	}

	@Test
	public void testObsModelThrowsOnSpanNegative() throws IllegalArgumentException {

		ISTTrackingEventIndexer indexer = mock(ISTTrackingEventIndexer.class);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTObsModel model = new PoissonObsModel(indexer, -1);
		});
	}

	@Test
	public void testObsModelNoTimeBoundaryIssues() {

		DateTime start = new DateTime(2012, 1, 1, 0, 0, 0);
		DateTime end = new DateTime(2012, 1, 2, 0, 0, 0);
		ISTTrackingEventIndexer indexer = mock(ISTTrackingEventIndexer.class);
		when(indexer.getFirstTime()).thenReturn(start);
		when(indexer.getLastTime()).thenReturn(end);
		when(indexer.getExpectedChangePerFrame(any(Interval.class))).thenReturn(Integer.valueOf(1));

		ISTObsModel model = new PoissonObsModel(indexer, 5);

		DateTime evStart = new DateTime(2012, 1, 1, 10, 0, 0);
		DateTime evEnd = new DateTime(2012, 1, 1, 10, 0, 5);
		Interval evRange = new Interval(evStart, evEnd);

		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		when(ev.getTimePeriod()).thenReturn(evRange);
		double prob = model.getObsProb(ev);
		// Assert.assertTrue(Math.abs(prob - 0.368) < 0.001);
	}

	@Test
	public void testObsModelStartTimeBoundaryIssuesForAverage() {

		DateTime start = new DateTime(2012, 1, 1, 0, 0, 0);
		DateTime end = new DateTime(2012, 1, 2, 0, 0, 0);
		ISTTrackingEventIndexer indexer = mock(ISTTrackingEventIndexer.class);
		when(indexer.getFirstTime()).thenReturn(start);
		when(indexer.getLastTime()).thenReturn(end);
		when(indexer.getExpectedChangePerFrame(any(Interval.class))).thenReturn(Integer.valueOf(1));

		ISTObsModel model = new PoissonObsModel(indexer, 5);

		DateTime evStart = new DateTime(2012, 1, 1, 0, 0, 0);
		DateTime evEnd = new DateTime(2012, 1, 1, 0, 0, 5);
		Interval evRange = new Interval(evStart, evEnd);

		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		when(ev.getTimePeriod()).thenReturn(evRange);
		model.getObsProb(ev);
		ArgumentCaptor<Interval> captor = ArgumentCaptor.forClass(Interval.class);
		verify(indexer, times(2)).getExpectedChangePerFrame(captor.capture());
		List<Interval> capturedIntervals = captor.getAllValues();
		assertEquals(start, capturedIntervals.get(0).getStart());
	}

}
