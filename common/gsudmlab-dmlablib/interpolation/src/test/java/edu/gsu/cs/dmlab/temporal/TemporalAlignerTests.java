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
package edu.gsu.cs.dmlab.temporal;

import static org.junit.jupiter.api.Assertions.*;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.RoundingStrategy;

class TemporalAlignerTests {
	@Test
	void testNullfactory() {

		IInterpolationFactory factory = null;
		long duration = 300000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		RoundingStrategy strategy = RoundingStrategy.DOWN;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new TemporalAligner(factory, epoch, step, strategy);
		});
	}

	@Test
	void testNullepoch() {

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		long duration = 300000;
		Duration step = new Duration(duration);
		DateTime epoch = null;
		RoundingStrategy strategy = RoundingStrategy.DOWN;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new TemporalAligner(factory, epoch, step, strategy);
		});
	}

	@Test
	void testNullstrategy() {

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		long duration = 300000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		RoundingStrategy strategy = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {

			new TemporalAligner(factory, epoch, step, strategy);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	void TemporalAligner() {
		// Setup the epoch and step
		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		// Set the rounding strategy to test
		RoundingStrategy strategy = RoundingStrategy.UP;
		// These are just filler objects that will be used by both of the following
		// events since no processing is actually done on these in the class that is to
		// be tested.
		EventType type = EventType.ACTIVE_REGION;
		Geometry geom = mock(Geometry.class);

		// Event 1 of 2: since the loop in the aligner class expects at least two events
		// to be stored in the trajectory
		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		// We set the datetimes to be a fraction of the step size after the epoch so
		// that rounding must take place
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		// Setup the functionality of the first event by defining what is to be returned
		// when each of its methods are called
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(true);
		when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
		// Event 2 of 2: since the loop in the aligner class expects at least two events
		// to be stored in the trajectory
		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		// We set the datetimes to be a fraction of the step size after the epoch so
		// that rounding must take place. Since the second event is after the first, the
		// fraction must be larger than that of the first event.
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 1.9;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		// Setup the functionality of the second event by defining what is to be
		// returned
		// when each of its methods are called
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(true);
		when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);
		// Next we create a trajectory object that will be passed into the aligner
		// class. This trajectory should contain the two events we just got done
		// creating for that purpose.
		ISTInterpolationTrajectory trajectory = mock(ISTInterpolationTrajectory.class);
		// To get around the fact that the trajectory returns a sorted set, but a sorted
		// set such as a TreeSet does not like the mock events, we will have to mock the
		// SortedSet as well. So we construct one and tell the trajectory to return it
		// when its getSTObjects method is called.
		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(trajectory.getSTObjects()).thenReturn(tr);
		// The sorted set is passed into the constructor of an array list, which
		// apparently calls a toArray method. So, one of the easiest ways to fake the
		// sorted set toArray method is to create a list to house the event objects and
		// use its toArray method.
		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);
		// when(tr.size()).thenReturn(2);
		when(tr.toArray()).thenReturn(list.toArray());
		// Now we move onto the factory object that is passed into the constructor of
		// the Aligner class
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		// The factory gets called to construct both new events and a new trajectory, so
		// we define what to do when these methods are called here. Since nothing really
		// gets done to the new events that are returned from the factory other than
		// putting them in a list to construct a new trajectory, we just tell
		// it to return any old event without defining any expected behavior of that
		// returned object.
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(mock(ISTInterpolationEvent.class));
		// Similarly, nothing is done with the returned trajectory besides returning it,
		// so we just tell the factory to return any old trajectory as well.
		when(factory.getSTTrajectory(any(List.class))).thenReturn(mock(ISTInterpolationTrajectory.class));
		// We now construct the object we want to test and invoke the method we are
		// testing.
		ITemporalAligner ta = new TemporalAligner(factory, epoch, step, strategy);
		ta.alignEventsForStepFromEpoch(trajectory);
		// Since we want to verify that the interval of the events to be returned from
		// the Aligner have indeed been aligned, we want to capture the intervals of the
		// events that are constructed to be returned. That way we can test them to
		// ensure the desired outcome has happened for the input that we have specified.
		ArgumentCaptor<Interval> intervalCaptor = ArgumentCaptor.forClass(Interval.class);
		Mockito.verify(factory, Mockito.times(2)).getSTEvent(intervalCaptor.capture(), any(EventType.class),
				any(Geometry.class));
		// Here we test the first processed interval and verify that it is indeed an
		// integer multiple of the step size offset from the epoch and that it is the
		// correct number of steps after the epoch.
		List<Interval> opList = intervalCaptor.getAllValues();
		Interval op = opList.get(0);

		// In this instance the start should be rounded up to be 1 step away from the
		// epoch.
		assertTrue((op.getStartMillis() - epoch.getMillis()) / duration == 1);
		assertTrue((op.getStartMillis() - epoch.getMillis()) % duration == 0);
		// And the end should be rounded up to be 2 steps away from the epoch
		assertTrue((op.getEndMillis() - epoch.getMillis()) / duration == 2);
		assertTrue((op.getEndMillis() - epoch.getMillis()) % duration == 0);
		// You still need to add the logic in the TemporalAligner class to process the
		// timestap of the second event in the list. Then you add the tests for the
		// second object Interval in the intervalCaptor list below here. Since there
		// currently is not logic to process the second event, the opList is only going
		// to have 1 Interval in it. However, once you add the logic to process the last
		// event in the list, opList will have a lenght of 2. I think you should be able
		// to continue from here.

		Interval op2 = opList.get(1);

		assertTrue((op2.getStartMillis() - epoch.getMillis()) / duration == 2);
		assertTrue((op2.getStartMillis() - epoch.getMillis()) % duration == 0);
		assertTrue((op2.getEndMillis() - epoch.getMillis()) / duration == 2);
		assertTrue((op2.getEndMillis() - epoch.getMillis()) % duration == 0);

	}

	@SuppressWarnings("unchecked")
	@Test
	void TemporalAlignerDown() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		RoundingStrategy strategy = RoundingStrategy.DOWN;

		EventType type = EventType.ACTIVE_REGION;
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);

		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;

		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));

		Interval timePeriod = new Interval(start_val, end_val);

		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(true);
		when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);

		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 1.9;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(true);
		when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationTrajectory trajectory = mock(ISTInterpolationTrajectory.class);

		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(trajectory.getSTObjects()).thenReturn(tr);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		when(tr.toArray()).thenReturn(list.toArray());

		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(mock(ISTInterpolationEvent.class));

		when(factory.getSTTrajectory(any(List.class))).thenReturn(mock(ISTInterpolationTrajectory.class));

		ITemporalAligner ta = new TemporalAligner(factory, epoch, step, strategy);
		ta.alignEventsForStepFromEpoch(trajectory);

		ArgumentCaptor<Interval> intervalCaptor = ArgumentCaptor.forClass(Interval.class);

		Mockito.verify(factory, Mockito.times(2)).getSTEvent(intervalCaptor.capture(), any(EventType.class),
				any(Geometry.class));

		List<Interval> opList = intervalCaptor.getAllValues();
		Interval op = opList.get(0);

		assertTrue((op.getStartMillis() - epoch.getMillis()) / duration == 0);
		assertTrue((op.getStartMillis() - epoch.getMillis()) % duration == 0);
		assertTrue((op.getEndMillis() - epoch.getMillis()) / duration == 1);
		assertTrue((op.getEndMillis() - epoch.getMillis()) % duration == 0);

		Interval op1 = opList.get(1);

		assertTrue((op1.getStartMillis() - epoch.getMillis()) / duration == 1);
		assertTrue((op1.getStartMillis() - epoch.getMillis()) % duration == 0);
		assertTrue((op1.getEndMillis() - epoch.getMillis()) / duration == 1);
		assertTrue((op1.getEndMillis() - epoch.getMillis()) % duration == 0);

	}

	@SuppressWarnings("unchecked")
	@Test
	void TemporalAlignerRound() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		RoundingStrategy strategy = RoundingStrategy.ROUND;

		EventType type = EventType.ACTIVE_REGION;
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);

		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;

		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));

		Interval timePeriod = new Interval(start_val, end_val);

		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(true);
		when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);

		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 1.9;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(true);
		when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationTrajectory trajectory = mock(ISTInterpolationTrajectory.class);

		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(trajectory.getSTObjects()).thenReturn(tr);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		when(tr.toArray()).thenReturn(list.toArray());

		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(mock(ISTInterpolationEvent.class));

		when(factory.getSTTrajectory(any(List.class))).thenReturn(mock(ISTInterpolationTrajectory.class));

		ITemporalAligner ta = new TemporalAligner(factory, epoch, step, strategy);
		ta.alignEventsForStepFromEpoch(trajectory);

		ArgumentCaptor<Interval> intervalCaptor = ArgumentCaptor.forClass(Interval.class);

		Mockito.verify(factory, Mockito.times(2)).getSTEvent(intervalCaptor.capture(), any(EventType.class),
				any(Geometry.class));

		List<Interval> opList = intervalCaptor.getAllValues();
		Interval op = opList.get(0);

		assertTrue((op.getStartMillis() - epoch.getMillis()) / duration == 0);
		assertTrue((op.getStartMillis() - epoch.getMillis()) % duration == 0);
		assertTrue((op.getEndMillis() - epoch.getMillis()) / duration == 1);
		assertTrue((op.getEndMillis() - epoch.getMillis()) % duration == 0);

		Interval op1 = opList.get(1);

		assertTrue((op1.getStartMillis() - epoch.getMillis()) / duration == 1);
		assertTrue((op1.getStartMillis() - epoch.getMillis()) % duration == 0);
		assertTrue((op1.getEndMillis() - epoch.getMillis()) / duration == 2);
		assertTrue((op1.getEndMillis() - epoch.getMillis()) % duration == 0);

	}

}