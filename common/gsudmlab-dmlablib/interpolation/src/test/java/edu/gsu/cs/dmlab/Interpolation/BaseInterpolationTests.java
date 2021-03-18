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
package edu.gsu.cs.dmlab.Interpolation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.ArgumentCaptor;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.interpolation.BaseInterpolation;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

class BaseInterpolationTests {

	class MockBaseInterp extends BaseInterpolation {

		ISTInterpolationEvent returnEvent = null;

		public MockBaseInterp(IInterpolationFactory factory, ITemporalAligner aligner, Duration step,
				String derivedName) {
			super(factory, aligner, step, derivedName);
		}

		public MockBaseInterp(IInterpolationFactory factory, ITemporalAligner aligner, Duration step,
				String derivedName, ISTInterpolationEvent returnEvent) {
			super(factory, aligner, step, derivedName);
			this.returnEvent = returnEvent;
		}

		@Override
		protected List<ISTInterpolationEvent> interpolateBetweenAlignedNoCopy(ISTInterpolationEvent first,
				ISTInterpolationEvent second) {
			List<ISTInterpolationEvent> evnts = new ArrayList<ISTInterpolationEvent>();
			evnts.add(returnEvent);
			return evnts;
		}

		@Override
		protected ISTInterpolationEvent createInterpolatedSTEvent(DateTime startTime, DateTime endTime, EventType type,
				Coordinate[] i_coordinates) {
			return this.returnEvent;
		}

		public double exposedLinearInterpolate(double x_0, double x_n, double factor) {
			return super.linearInterpolate(x_0, x_n, factor);
		}

		public ISTInterpolationTrajectory exposedInterpolateTrajectory(ISTInterpolationTrajectory inTrajectory) {
			return super.interpolateTrajectory(inTrajectory);
		}

		public List<ISTInterpolationEvent> exposedInterpolateBetween(ISTInterpolationEvent first,
				ISTInterpolationEvent second) {
			return super.interpolateBetween(first, second);
		}
	}

	@Test
	void testThrowsOnNullFactory() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = null;// mock(IInterpolationFactory.class);
		String derivedName = "Test";

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new MockBaseInterp(factory, aligner, step, derivedName);
		});
	}

	@Test
	void testThrowsOnNullAligner() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = null;// mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new MockBaseInterp(factory, aligner, step, derivedName);
		});
	}

	@Test
	void testThrowsOnNullStep() {
		Duration step = null;// new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new MockBaseInterp(factory, aligner, step, derivedName);
		});
	}

	@Test
	void testThrowsOnNullDerivedName() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = null;// "Test";

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new MockBaseInterp(factory, aligner, step, derivedName);
		});
	}

	@Test
	void testLinearInterpCorrect() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);

		double x_0 = 0;
		double x_n = 10;

		// Check mid point
		double factor = 0.5;
		double expected = 5;
		Assertions.assertTrue(() -> {
			double value = interpObj.exposedLinearInterpolate(x_0, x_n, factor);
			return value == expected;
		});

		// Check minimum point
		double factor2 = 0.0;
		double expected2 = 0;
		Assertions.assertTrue(() -> {
			double value = interpObj.exposedLinearInterpolate(x_0, x_n, factor2);
			return value == expected2;
		});

		// Check maximum point
		double factor3 = 1.0;
		double expected3 = 10;
		Assertions.assertTrue(() -> {
			double value = interpObj.exposedLinearInterpolate(x_0, x_n, factor3);
			return value == expected3;
		});
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void testInterpolateTrajectoryCallsAlignerAndFactoryTrajectoryMethodWhenNoInterpNeeded() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		// when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 2.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		// when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(tr.toArray()).thenReturn(list.toArray());

		ISTInterpolationTrajectory inTrajectory = mock(ISTInterpolationTrajectory.class);
		ISTInterpolationTrajectory alignedTrajectory = mock(ISTInterpolationTrajectory.class);
		when(alignedTrajectory.getSTObjects()).thenReturn(tr);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForStepFromEpoch(any(ISTInterpolationTrajectory.class))).thenReturn(alignedTrajectory);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateTrajectory(inTrajectory);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForStepFromEpoch(any(ISTInterpolationTrajectory.class));

		// Here we verify that the factory was called with the adjusted events as the
		// input
		Class<List<ISTInterpolationEvent>> listClass = (Class<List<ISTInterpolationEvent>>) (Class) List.class;
		ArgumentCaptor<List<ISTInterpolationEvent>> resultCaptor = ArgumentCaptor.forClass(listClass);
		verify(factory, times(1)).getSTTrajectory(resultCaptor.capture());
		List<ISTInterpolationEvent> results = resultCaptor.getAllValues().get(0);
		Assertions.assertTrue(() -> {
			return results.get(0) == ev1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == ev2;
		});
	}

	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenFirstNotInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 3.1;
		double endFractOfStep2 = 3.9;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		// when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		@SuppressWarnings("unchecked")
		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(tr.toArray()).thenReturn(list.toArray());

		ISTInterpolationTrajectory inTrajectory = mock(ISTInterpolationTrajectory.class);
		ISTInterpolationTrajectory alignedTrajectory = mock(ISTInterpolationTrajectory.class);
		when(alignedTrajectory.getSTObjects()).thenReturn(tr);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForStepFromEpoch(any(ISTInterpolationTrajectory.class))).thenReturn(alignedTrajectory);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateTrajectory(inTrajectory);

		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));
	}

	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenFirstInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(true);
		when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 3.1;
		double endFractOfStep2 = 3.9;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		// when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		@SuppressWarnings("unchecked")
		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(tr.toArray()).thenReturn(list.toArray());

		ISTInterpolationTrajectory inTrajectory = mock(ISTInterpolationTrajectory.class);
		ISTInterpolationTrajectory alignedTrajectory = mock(ISTInterpolationTrajectory.class);
		when(alignedTrajectory.getSTObjects()).thenReturn(tr);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForStepFromEpoch(any(ISTInterpolationTrajectory.class))).thenReturn(alignedTrajectory);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateTrajectory(inTrajectory);

		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenSecondNotInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		// Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		// when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 0);
		coord[1] = new Coordinate(0, 1);
		coord[2] = new Coordinate(1, 1);
		coord[3] = new Coordinate(1, 0);
		coord[4] = new Coordinate(0, 0);

		GeometryFactory gf = new GeometryFactory();
		Geometry geom = gf.createPolygon(coord);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(tr.toArray()).thenReturn(list.toArray());

		ISTInterpolationTrajectory inTrajectory = mock(ISTInterpolationTrajectory.class);
		ISTInterpolationTrajectory alignedTrajectory = mock(ISTInterpolationTrajectory.class);
		when(alignedTrajectory.getSTObjects()).thenReturn(tr);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForStepFromEpoch(any(ISTInterpolationTrajectory.class))).thenReturn(alignedTrajectory);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		ISTInterpolationEvent ev3 = mock(ISTInterpolationEvent.class);
		when(factory.getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(ev3);
		String derivedName = "Test";

		ISTInterpolationEvent ev4 = mock(ISTInterpolationEvent.class);
		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName, ev4);
		interpObj.exposedInterpolateTrajectory(inTrajectory);

		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));

		// Here we verify that the factory was called with the adjusted events as the
		// input
		Class<List<ISTInterpolationEvent>> listClass = (Class<List<ISTInterpolationEvent>>) (Class) List.class;
		ArgumentCaptor<List<ISTInterpolationEvent>> resultCaptor = ArgumentCaptor.forClass(listClass);
		verify(factory, times(1)).getSTTrajectory(resultCaptor.capture());
		List<ISTInterpolationEvent> results = resultCaptor.getAllValues().get(0);

		Assertions.assertTrue(() -> {
			return results.get(0) == ev1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == ev3;
		});
		Assertions.assertTrue(() -> {
			return results.get(2) == ev4;
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenSecondInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		// when(ev1.getGeometry()).thenReturn(geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 0);
		coord[1] = new Coordinate(0, 1);
		coord[2] = new Coordinate(1, 1);
		coord[3] = new Coordinate(1, 0);
		coord[4] = new Coordinate(0, 0);

		GeometryFactory gf = new GeometryFactory();
		Geometry geom = gf.createPolygon(coord);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(true);
		when(ev2.getGeometry()).thenReturn(geom);
		when(ev2.getType()).thenReturn(type);
		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
		list.add(ev1);
		list.add(ev2);

		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
		when(tr.toArray()).thenReturn(list.toArray());

		ISTInterpolationTrajectory inTrajectory = mock(ISTInterpolationTrajectory.class);
		ISTInterpolationTrajectory alignedTrajectory = mock(ISTInterpolationTrajectory.class);
		when(alignedTrajectory.getSTObjects()).thenReturn(tr);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForStepFromEpoch(any(ISTInterpolationTrajectory.class))).thenReturn(alignedTrajectory);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		ISTInterpolationEvent ev3 = mock(ISTInterpolationEvent.class);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(ev3);
		String derivedName = "Test";

		ISTInterpolationEvent ev4 = mock(ISTInterpolationEvent.class);
		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName, ev4);
		interpObj.exposedInterpolateTrajectory(inTrajectory);

		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

		// Here we verify that the factory was called with the adjusted events as the
		// input
		Class<List<ISTInterpolationEvent>> listClass = (Class<List<ISTInterpolationEvent>>) (Class) List.class;
		ArgumentCaptor<List<ISTInterpolationEvent>> resultCaptor = ArgumentCaptor.forClass(listClass);
		verify(factory, times(1)).getSTTrajectory(resultCaptor.capture());
		List<ISTInterpolationEvent> results = resultCaptor.getAllValues().get(0);

		Assertions.assertTrue(() -> {
			return results.get(0) == ev1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == ev3;
		});
		Assertions.assertTrue(() -> {
			return results.get(2) == ev4;
		});
	}

	@Test
	void testInterpolateBetweenCallsAlignerWhenNoInterpNeeded() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		when(outEv1.getTimePeriod()).thenReturn(timePeriod);
		when(outEv1.isInterpolated()).thenReturn(false);
		// when(outEv1.getGeometry()).thenReturn(geom);
		when(outEv1.getType()).thenReturn(type);
		when(outEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 2.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		when(outEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(outEv2.isInterpolated()).thenReturn(false);
		// when(ev2.getGeometry()).thenReturn(geom);
		when(outEv2.getType()).thenReturn(type);
		when(outEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(outEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(outEv2);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		List<ISTInterpolationEvent> results = interpObj.exposedInterpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		Assertions.assertTrue(() -> {
			return results.get(0) == outEv1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == outEv2;
		});
	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenFirstNotInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		when(outEv1.getTimePeriod()).thenReturn(timePeriod);
		when(outEv1.isInterpolated()).thenReturn(false);
		when(outEv1.getGeometry()).thenReturn(geom);
		when(outEv1.getType()).thenReturn(type);
		when(outEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		when(outEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(outEv2.isInterpolated()).thenReturn(false);
		// when(ev2.getGeometry()).thenReturn(geom);
		when(outEv2.getType()).thenReturn(type);
		when(outEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(outEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(outEv2);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));

	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenFirstInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		when(outEv1.getTimePeriod()).thenReturn(timePeriod);
		when(outEv1.isInterpolated()).thenReturn(true);
		when(outEv1.getGeometry()).thenReturn(geom);
		when(outEv1.getType()).thenReturn(type);
		when(outEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		when(outEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(outEv2.isInterpolated()).thenReturn(false);
		// when(ev2.getGeometry()).thenReturn(geom);
		when(outEv2.getType()).thenReturn(type);
		when(outEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(outEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(outEv2);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenSecondNotInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		when(outEv1.getTimePeriod()).thenReturn(timePeriod);
		when(outEv1.isInterpolated()).thenReturn(true);
		// when(outEv1.getGeometry()).thenReturn(geom);
		when(outEv1.getType()).thenReturn(type);
		when(outEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		when(outEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(outEv2.isInterpolated()).thenReturn(false);
		when(outEv2.getGeometry()).thenReturn(geom);
		when(outEv2.getType()).thenReturn(type);
		when(outEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(outEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(outEv2);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));

	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenSecondInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		Geometry geom = mock(Geometry.class);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		when(outEv1.getTimePeriod()).thenReturn(timePeriod);
		when(outEv1.isInterpolated()).thenReturn(true);
		// when(outEv1.getGeometry()).thenReturn(geom);
		when(outEv1.getType()).thenReturn(type);
		when(outEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		when(outEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(outEv2.isInterpolated()).thenReturn(true);
		when(outEv2.getGeometry()).thenReturn(geom);
		when(outEv2.getType()).thenReturn(type);
		when(outEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(outEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(outEv2);

		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		String derivedName = "Test";

		MockBaseInterp interpObj = new MockBaseInterp(factory, aligner, step, derivedName);
		interpObj.exposedInterpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

	}
}
