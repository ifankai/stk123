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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.mockito.Mockito;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.interpolation.STMBRInterpolator;
import edu.gsu.cs.dmlab.interpolation.interfaces.IInterpolator;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

class STMBRInterpolatorTests {

	@Test
	void testConstructorThrowsOnNullAligner() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = null;
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STMBRInterpolator(factory, aligner, step);
		});
	}

	@Test
	void testConstructorThrowsOnNullFactory() {
		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STMBRInterpolator(factory, aligner, step);
		});
	}

	@Test
	void testConstructorThrowsOnNullStep() {
		Duration step = null;
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STMBRInterpolator(factory, aligner, step);
		});
	}

	@Test
	void testInterpolateTrajectoryThrowsOnNullInputTrajectory() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationTrajectory inTrajectory = null;

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateTrajectory(inTrajectory);
		});
	}

	@Test
	void testInterpolateBetweenThrowsOnNullFirstInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent first = null;
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateBetween(first, second);
		});
	}

	@Test
	void testInterpolateBetweenThrowsOnNullSecondInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = null;

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateBetween(first, second);
		});
	}

	@Test
	void testInterpolateAtTimeThrowsOnNullFirstInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent first = null;
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAtTime(first, second, dateTime);
		});
	}

	@Test
	void testInterpolateAtTimeThrowsOnNullSecondInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAtTime(first, second, dateTime);
		});
	}

	@Test
	void testInterpolateAtTimeThrowsOnNullDateTimeInput() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAtTime(first, second, dateTime);
		});
	}

	@Test
	void testInterpolateAtTimeThrowsOnDateTimeAfterInputEvents() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		DateTime firstTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(new Interval(firstTime, step));

		DateTime secondTime = new DateTime(2012, 12, 12, 1, 12);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(new Interval(secondTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 1, 15);

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAtTime(first, second, interpDateTime);
		});
	}

	@Test
	void testInterpolateAtTimeThrowsOnDateTimeBeforeInputEvents() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		DateTime firstTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(new Interval(firstTime, step));

		DateTime secondTime = new DateTime(2012, 12, 12, 1, 12);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(new Interval(secondTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 0, 54);

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAtTime(first, second, interpDateTime);
		});
	}

	@Test
	void testInterpolateBeforeAtTimeThrowsOnNullInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent ev = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateBeforeAtTime(ev, dateTime);
		});
	}

	@Test
	void testInterpolateBeforeAtTimeThrowsOnNullDateTimeInput() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateBeforeAtTime(ev, dateTime);
		});
	}

	@Test
	void testInterpolateBeforeAtTimeThrowsOnDateTimeAfterInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		DateTime evTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);
		when(ev.getTimePeriod()).thenReturn(new Interval(evTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 1, 54);

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateBeforeAtTime(ev, interpDateTime);
		});
	}

	@Test
	void testInterpolateAfterAtTimeThrowsOnNullInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent ev = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAfterAtTime(ev, dateTime);
		});
	}

	@Test
	void testInterpolateAfterAtTimeThrowsOnNullDateTimeInput() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAfterAtTime(ev, dateTime);
		});
	}

	@Test
	void testInterpolateAfterAtTimeThrowsOnDateTimeAfterInputEvent() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);

		DateTime evTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);
		when(ev.getTimePeriod()).thenReturn(new Interval(evTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 0, 54);

		IInterpolator interp = new STMBRInterpolator(factory, aligner, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAfterAtTime(ev, interpDateTime);
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

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		interpObj.interpolateTrajectory(inTrajectory);

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
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		when(ev1.getGeometry()).thenReturn(first_geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		when(ev2.getGeometry()).thenReturn(second_geom);
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

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		interpObj.interpolateTrajectory(inTrajectory);

		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));
		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));
	}

	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenFirstInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(true);
		when(ev1.getGeometry()).thenReturn(first_geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		when(ev2.getGeometry()).thenReturn(second_geom);
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

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		interpObj.interpolateTrajectory(inTrajectory);

		verify(factory, times(2)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenSecondNotInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		when(ev1.getGeometry()).thenReturn(first_geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(false);
		when(ev2.getGeometry()).thenReturn(second_geom);
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
		ISTInterpolationEvent ev4 = mock(ISTInterpolationEvent.class);
		when(factory.getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(ev3);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(ev4);

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		interpObj.interpolateTrajectory(inTrajectory);

		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testInterpolateTrajectoryCallsCorrectFactoryMethodWhenSecondInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
		when(ev1.getTimePeriod()).thenReturn(timePeriod);
		when(ev1.isInterpolated()).thenReturn(false);
		when(ev1.getGeometry()).thenReturn(first_geom);
		when(ev1.getType()).thenReturn(type);
		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
		when(ev2.isInterpolated()).thenReturn(true);
		when(ev2.getGeometry()).thenReturn(second_geom);
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
		ISTInterpolationEvent ev4 = mock(ISTInterpolationEvent.class);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(ev3)
				.thenReturn(ev4);

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		interpObj.interpolateTrajectory(inTrajectory);

		verify(factory, times(2)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

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

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		List<ISTInterpolationEvent> results = interpObj.interpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		Assertions.assertTrue(() -> {
			return results.size() == 2;
		});
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
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent adjustedEv1 = mock(ISTInterpolationEvent.class);
		when(adjustedEv1.getTimePeriod()).thenReturn(timePeriod);
		when(adjustedEv1.isInterpolated()).thenReturn(false);
		when(adjustedEv1.getGeometry()).thenReturn(first_geom);
		when(adjustedEv1.getType()).thenReturn(type);
		when(adjustedEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent adjustedEv2 = mock(ISTInterpolationEvent.class);
		when(adjustedEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(adjustedEv2.isInterpolated()).thenReturn(false);
		when(adjustedEv2.getGeometry()).thenReturn(second_geom);
		when(adjustedEv2.getType()).thenReturn(type);
		when(adjustedEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(adjustedEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(adjustedEv2);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		when(factory.getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(outEv1);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(outEv2);

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		List<ISTInterpolationEvent> results = interpObj.interpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));
		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

		Assertions.assertTrue(() -> {
			return results.size() == 3;
		});
		Assertions.assertTrue(() -> {
			return results.get(0) == outEv1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == outEv2;
		});
		Assertions.assertTrue(() -> {
			return results.get(2) == adjustedEv2;
		});
	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenFirstInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent adjustedEv1 = mock(ISTInterpolationEvent.class);
		when(adjustedEv1.getTimePeriod()).thenReturn(timePeriod);
		when(adjustedEv1.isInterpolated()).thenReturn(true);
		when(adjustedEv1.getGeometry()).thenReturn(first_geom);
		when(adjustedEv1.getType()).thenReturn(type);
		when(adjustedEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent adjustedEv2 = mock(ISTInterpolationEvent.class);
		when(adjustedEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(adjustedEv2.isInterpolated()).thenReturn(false);
		when(adjustedEv2.getGeometry()).thenReturn(second_geom);
		when(adjustedEv2.getType()).thenReturn(type);
		when(adjustedEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(adjustedEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(adjustedEv2);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent outEv2 = mock(ISTInterpolationEvent.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(outEv1)
				.thenReturn(outEv2);

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		List<ISTInterpolationEvent> results = interpObj.interpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(2)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

		Assertions.assertTrue(() -> {
			return results.size() == 3;
		});
		Assertions.assertTrue(() -> {
			return results.get(0) == outEv1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == outEv2;
		});
		Assertions.assertTrue(() -> {
			return results.get(2) == adjustedEv2;
		});
	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenSecondNotInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent adjustedEv1 = mock(ISTInterpolationEvent.class);
		when(adjustedEv1.getTimePeriod()).thenReturn(timePeriod);
		when(adjustedEv1.isInterpolated()).thenReturn(true);
		when(adjustedEv1.getGeometry()).thenReturn(first_geom);
		when(adjustedEv1.getType()).thenReturn(type);
		when(adjustedEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent adjustedEv2 = mock(ISTInterpolationEvent.class);
		when(adjustedEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(adjustedEv2.isInterpolated()).thenReturn(false);
		when(adjustedEv2.getGeometry()).thenReturn(second_geom);
		when(adjustedEv2.getType()).thenReturn(type);
		when(adjustedEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(adjustedEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(adjustedEv2);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		when(factory.getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class), any(Geometry.class)))
				.thenReturn(outEv1).thenReturn(outEv1);

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		List<ISTInterpolationEvent> results = interpObj.interpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class),
				any(Geometry.class));

		Assertions.assertTrue(() -> {
			return results.size() == 2;
		});
		Assertions.assertTrue(() -> {
			return results.get(0) == adjustedEv1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == outEv1;
		});

	}

	@Test
	void testInterpolateBetweenCallsCorrectFactoryMehtodWhenSecondInterp() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();
		GeometryFactory gf = new GeometryFactory();

		EventType type = EventType.ACTIVE_REGION;

		/// Configure Event number 1
		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 1);
		coord[1] = new Coordinate(0, 4);
		coord[2] = new Coordinate(3, 2);
		coord[3] = new Coordinate(1, 1);
		coord[4] = new Coordinate(0, 1);

		Geometry first_geom = gf.createPolygon(coord);

		ISTInterpolationEvent adjustedEv1 = mock(ISTInterpolationEvent.class);
		when(adjustedEv1.getTimePeriod()).thenReturn(timePeriod);
		when(adjustedEv1.isInterpolated()).thenReturn(true);
		when(adjustedEv1.getGeometry()).thenReturn(first_geom);
		when(adjustedEv1.getType()).thenReturn(type);
		when(adjustedEv1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		// Configure Event number 2
		double startFractOfStep2 = 1.3;
		double endFractOfStep2 = 3.3;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);

		Coordinate[] coord2 = new Coordinate[5];
		coord2[0] = new Coordinate(0, 9);
		coord2[1] = new Coordinate(0, 11);
		coord2[2] = new Coordinate(10, 4);
		coord2[3] = new Coordinate(8, 3);
		coord2[4] = new Coordinate(0, 9);

		Geometry second_geom = gf.createPolygon(coord2);

		ISTInterpolationEvent adjustedEv2 = mock(ISTInterpolationEvent.class);
		when(adjustedEv2.getTimePeriod()).thenReturn(timePeriod2);
		when(adjustedEv2.isInterpolated()).thenReturn(true);
		when(adjustedEv2.getGeometry()).thenReturn(second_geom);
		when(adjustedEv2.getType()).thenReturn(type);
		when(adjustedEv2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);

		ISTInterpolationEvent inEv1 = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent inEv2 = mock(ISTInterpolationEvent.class);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		when(aligner.alignEventsForInterpolation(inEv1)).thenReturn(adjustedEv1);
		when(aligner.alignEventsForInterpolation(inEv2)).thenReturn(adjustedEv2);

		ISTInterpolationEvent outEv1 = mock(ISTInterpolationEvent.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(outEv1)
				.thenReturn(outEv1);

		IInterpolator interpObj = new STMBRInterpolator(factory, aligner, step);
		List<ISTInterpolationEvent> results = interpObj.interpolateBetween(inEv1, inEv2);

		// Here we verify that the aligner was called
		verify(aligner, times(1)).alignEventsForInterpolation(inEv1);
		verify(aligner, times(1)).alignEventsForInterpolation(inEv2);

		// Verify the factory is called to create a new event
		verify(factory, times(1)).getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class));

		Assertions.assertTrue(() -> {
			return results.size() == 2;
		});
		Assertions.assertTrue(() -> {
			return results.get(0) == adjustedEv1;
		});
		Assertions.assertTrue(() -> {
			return results.get(1) == outEv1;
		});
	}

	/*----InterpolateAtTimeNotNull----------*/

	@Test
	void testinterpolateAtTimeNotNull1() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		DateTime epoch = new DateTime();

		Coordinate[] coord = new Coordinate[5];
		coord[0] = new Coordinate(0, 0);
		coord[1] = new Coordinate(0, 1);
		coord[2] = new Coordinate(1, 1);
		coord[3] = new Coordinate(1, 0);
		coord[4] = new Coordinate(0, 0);

		GeometryFactory geom1 = new GeometryFactory();
		Geometry geom = geom1.createPolygon(coord);

		EventType type = EventType.ACTIVE_REGION;

		double startFractOfStep = 0.3;
		double endFractOfStep = 1.3;
		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
		Interval timePeriod = new Interval(start_val, end_val);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(timePeriod);
		when(first.isInterpolated()).thenReturn(false);
		when(first.getGeometry()).thenReturn(geom);
		when(first.getType()).thenReturn(type);
		when(first.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		double startFractOfStep2 = 2.3;
		double endFractOfStep2 = 2.9;
		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
		Interval timePeriod2 = new Interval(start_val2, end_val2);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(timePeriod2);
		when(second.isInterpolated()).thenReturn(false);
		when(second.getGeometry()).thenReturn(geom);
		when(second.getType()).thenReturn(type);
		when(second.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);

		ITemporalAligner aligner = mock(ITemporalAligner.class);

		ISTInterpolationEvent outEv = mock(ISTInterpolationEvent.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(outEv);

		double startFractOfStep3 = 0.6;
		DateTime atTime = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep3));

		IInterpolator obj = new STMBRInterpolator(factory, aligner, step);
		ISTInterpolationEvent out = obj.interpolateAtTime(first, second, atTime);

		ArgumentCaptor<Interval> intervalCaptor = ArgumentCaptor.forClass(Interval.class);
		Mockito.verify(factory, Mockito.times(1)).getSTEvent(intervalCaptor.capture(), any(EventType.class),
				any(Geometry.class));

		List<Interval> opList = intervalCaptor.getAllValues();
		Interval op1 = opList.get(0);

		Assertions.assertTrue(() -> {
			return (op1.getStartMillis() - atTime.getMillis()) == 0;
		});

		Assertions.assertTrue(() -> {
			return outEv == out;
		});
	}

	/*----InterpolationBetween -Second value Not Null -----------*/

//	@Test
//	void interpolateBetween_secondNotNull() {
//
//		long duration = 3800000;
//		Duration step = new Duration(duration);
//
//		DateTime epoch = new DateTime();
//
//		Coordinate[] coord = new Coordinate[5];
//		coord[0] = new Coordinate(0, 0);
//		coord[1] = new Coordinate(0, 1);
//		coord[2] = new Coordinate(1, 1);
//		coord[3] = new Coordinate(1, 0);
//		coord[4] = new Coordinate(0, 0);
//
//		GeometryFactory geom1 = new GeometryFactory();
//		Geometry geom = geom1.createPolygon(coord);
//
//		EventType type = EventType.ACTIVE_REGION;
//
//		double startFractOfStep = 0.3;
//		double endFractOfStep = 1.3;
//		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
//		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
//		Interval timePeriod = new Interval(start_val, end_val);
//
//		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
//
//		when(first.getTimePeriod()).thenReturn(timePeriod);
//		when(first.isInterpolated()).thenReturn(true);
//		when(first.getGeometry()).thenReturn(geom);
//		when(first.getType()).thenReturn(type);
//		when(first.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
//
//		double startFractOfStep2 = 1.3;
//		double endFractOfStep2 = 1.9;
//		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
//		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
//		Interval timePeriod2 = new Interval(start_val2, end_val2);
//
//		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
//
//		when(second.getTimePeriod()).thenReturn(timePeriod2);
//		when(second.isInterpolated()).thenReturn(true);
//		when(second.getGeometry()).thenReturn(geom);
//		when(second.getType()).thenReturn(type);
//		when(second.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
//
//		ITemporalAligner aligner = mock(ITemporalAligner.class);
//		when(aligner.alignEventsForInterpolation(first)).thenReturn(first);
//
//		when(aligner.alignEventsForInterpolation(second)).thenReturn(second);
//
//		IInterpolationFactory factory = mock(IInterpolationFactory.class);
//		IInterpolator obj = new STMBRInterpolator(aligner, factory, step);
//		obj.interpolateBetween(first, second);
//
//		ArgumentCaptor<Interval> intervalCaptor = ArgumentCaptor.forClass(Interval.class);
//		Mockito.verify(factory, Mockito.times(2)).getSTEvent(intervalCaptor.capture(), any(EventType.class),
//				any(Geometry.class));
//
//		List<Interval> opList = intervalCaptor.getAllValues();
//		Interval op1 = opList.get(0);
//
//		assertTrue(op1.getStartMillis() <= second.getTimePeriod().getStartMillis());
//
//	}

	/*-- TestInterpolate----*/

	// test when step is "GREATER THAN 1" .
	// Taking isInterpolated return value as false;
//	@SuppressWarnings("unchecked")
//	@Test
//	void testInterpolate2() {
//
//		// First Event
//		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
//		long duration = 3600000;
//		Duration step = new Duration(duration);
//		DateTime epoch = new DateTime();
//		Envelope env1 = mock(Envelope.class);
//		Geometry geom = mock(Geometry.class);
//		when(geom.getEnvelopeInternal()).thenReturn(env1);
//		EventType type = EventType.ACTIVE_REGION;
//
//		double startFractOfStep = 0.3;
//		double endFractOfStep = 1.3;
//		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
//		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
//		Interval timePeriod = new Interval(start_val, end_val);
//
//		when(ev1.getTimePeriod()).thenReturn(timePeriod);
//		when(ev1.isInterpolated()).thenReturn(false);
//		when(ev1.getGeometry()).thenReturn(geom);
//		when(ev1.getType()).thenReturn(type);
//		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
//
//		// Second Event
//		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
//
//		double startFractOfStep2 = 3.1;
//		double endFractOfStep2 = 3.9;
//		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
//		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
//		Interval timePeriod2 = new Interval(start_val2, end_val2);
//
//		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
//		when(ev2.isInterpolated()).thenReturn(false);
//		when(ev2.getGeometry()).thenReturn(geom);
//		when(ev2.getType()).thenReturn(type);
//		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);
//
//		SortedSet<ISTInterpolationEvent> tr = mock(SortedSet.class);
//
//		List<ISTInterpolationEvent> list = new ArrayList<ISTInterpolationEvent>();
//		list.add(ev1);
//		list.add(ev2);
//
//		when(tr.toArray()).thenReturn(list.toArray());
//
//		ISTInterpolationTrajectory inTrajectory = mock(ISTInterpolationTrajectory.class);
//		when(inTrajectory.getSTObjects()).thenReturn(tr);
//
//		ITemporalAligner aligner = mock(ITemporalAligner.class);
//		when(aligner.alignEventsForStepFromEpoch(inTrajectory)).thenReturn(inTrajectory);
//
//		ISTInterpolationEvent newev1 = mock(ISTInterpolationEvent.class);
//		when(newev1.getTimePeriod()).thenReturn(timePeriod);
//
//		IInterpolationFactory factory = mock(IInterpolationFactory.class);
//		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(newev1);
//		when(factory.getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class), any(Geometry.class)))
//				.thenReturn(ev1);
//
//		IInterpolator obj = new STMBRInterpolator(aligner, factory, step);
//		obj.interpolateTrajectory(inTrajectory);
//
//		/*
//		 * assert that the first interpolated event is the same as the passed
//		 * ISTInterpolationEvent Object
//		 */
//		assertEquals(ev1, list.get(0));
//
//		/*-Assert that interpolatedEvents is one step or less in size.-*/
//
//		ArgumentCaptor<Interval> intervalCaptor = ArgumentCaptor.forClass(Interval.class);
//		Mockito.verify(factory, Mockito.times(2)).getSTEvent(intervalCaptor.capture(), any(EventType.class),
//				any(Geometry.class));
//
//		List<Interval> oplist1 = intervalCaptor.getAllValues();
//		Interval op = oplist1.get(0);
//		Interval op1 = oplist1.get(1);
//
//		assertTrue(((op.getStartMillis() - op1.getEndMillis()) / step.getMillis()) <= 1);
//
//	}

	/*-- Test to check if the Polygon are of the right shape.--*/

//	@Test
//	void testPolygonPostion() {
//
//		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
//		long duration = 3600000;
//		Duration step = new Duration(duration);
//		DateTime epoch = new DateTime();
//		EventType type = EventType.ACTIVE_REGION;
//
//		Coordinate[] coord = new Coordinate[5];
//		coord[0] = new Coordinate(0, 1);
//		coord[1] = new Coordinate(0, 4);
//		coord[2] = new Coordinate(3, 2);
//		coord[3] = new Coordinate(1, 1);
//		coord[4] = new Coordinate(0, 1);
//
//		GeometryFactory geom1 = new GeometryFactory();
//		Geometry first_geom = geom1.createPolygon(coord);
//
//		double startFractOfStep = 0.3;
//		double endFractOfStep = 2.3;
//		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
//		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
//		Interval timePeriod = new Interval(start_val, end_val);
//
//		when(ev1.getTimePeriod()).thenReturn(timePeriod);
//		when(ev1.isInterpolated()).thenReturn(false);
//		when(ev1.getGeometry()).thenReturn(first_geom);
//		when(ev1.getType()).thenReturn(type);
//		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
//
//		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
//
//		Coordinate[] coord2 = new Coordinate[5];
//		coord2[0] = new Coordinate(0, 9);
//		coord2[1] = new Coordinate(0, 11);
//		coord2[2] = new Coordinate(10, 4);
//		coord2[3] = new Coordinate(8, 3);
//		coord2[4] = new Coordinate(0, 9);
//
//		Geometry second_geom = geom1.createPolygon(coord2);
//
//		double startFractOfStep2 = 2.3;
//		double endFractOfStep2 = 4.9;
//		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
//		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
//		Interval timePeriod2 = new Interval(start_val2, end_val2);
//
//		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
//		when(ev2.isInterpolated()).thenReturn(false);
//		when(ev2.getGeometry()).thenReturn(second_geom);
//		when(ev2.getType()).thenReturn(type);
//		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);
//
//		/* --Third Interpolated Event-- */
//		ISTInterpolationEvent ev3 = mock(ISTInterpolationEvent.class);
//
//		ISTInterpolationEvent ev4 = mock(ISTInterpolationEvent.class);
//
//		ISTInterpolationEvent ev5 = mock(ISTInterpolationEvent.class);
//
//		IInterpolationFactory factory = mock(IInterpolationFactory.class);
//		when(factory.getSTEvent(any(Integer.class), any(Interval.class), any(EventType.class), any(Geometry.class)))
//				.thenReturn(ev4).thenReturn(ev5);
//		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(ev3);
//
//		ITemporalAligner aligner = mock(ITemporalAligner.class);
//		when(aligner.alignEventsForInterpolation(ev1)).thenReturn(ev1);
//		when(aligner.alignEventsForInterpolation(ev2)).thenReturn(ev2);
//
//		IInterpolator obj = new STMBRInterpolator(aligner, factory, step);
//		List<ISTInterpolationEvent> a1 = obj.interpolateBetween(ev1, ev2);
//
//		assertEquals(a1.get(0), ev4);
//		assertEquals(a1.get(2), ev5);
//		assertEquals(a1.get(1), ev3);
//
//	}

//	@Test
//	void testPolygonShape_usingInterpolateAtTime() {
//
//		/* -- First Event-- */
//		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
//		long duration = 3600000;
//		Duration step = new Duration(duration);
//		DateTime epoch = new DateTime();
//		EventType type = EventType.ACTIVE_REGION;
//
//		Coordinate[] coord = new Coordinate[5];
//		coord[0] = new Coordinate(0, 1);
//		coord[1] = new Coordinate(0, 4);
//		coord[2] = new Coordinate(3, 2);
//		coord[3] = new Coordinate(1, 1);
//		coord[4] = new Coordinate(0, 1);
//
//		GeometryFactory geom1 = new GeometryFactory();
//		Geometry first_geom = geom1.createPolygon(coord);
//
//		double startFractOfStep = 0.3;
//		double endFractOfStep = 2.3;
//		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
//		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
//		Interval timePeriod = new Interval(start_val, end_val);
//
//		when(ev1.getTimePeriod()).thenReturn(timePeriod);
//		when(ev1.isInterpolated()).thenReturn(false);
//		when(ev1.getGeometry()).thenReturn(first_geom);
//		when(ev1.getType()).thenReturn(type);
//		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
//
//		/*-- Second Event--*/
//
//		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
//
//		Coordinate[] coord2 = new Coordinate[5];
//		coord2[0] = new Coordinate(0, 9);
//		coord2[1] = new Coordinate(0, 11);
//		coord2[2] = new Coordinate(10, 4);
//		coord2[3] = new Coordinate(8, 3);
//		coord2[4] = new Coordinate(0, 9);
//
//		Geometry second_geom = geom1.createPolygon(coord2);
//
//		double startFractOfStep2 = 2.3;
//		double endFractOfStep2 = 4.9;
//		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
//		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
//		Interval timePeriod2 = new Interval(start_val2, end_val2);
//
//		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
//		when(ev2.isInterpolated()).thenReturn(false);
//		when(ev2.getGeometry()).thenReturn(second_geom);
//		when(ev2.getType()).thenReturn(type);
//		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);
//
//		double startFractOfStep3 = 0.6;
//		DateTime atTime = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep3));
//
//		ITemporalAligner aligner = mock(ITemporalAligner.class);
//		when(aligner.alignEventsForInterpolation(ev1)).thenReturn(ev1);
//		when(aligner.alignEventsForInterpolation(ev2)).thenReturn(ev2);
//
//		/* Third Interpolated Object -- */
//		ISTInterpolationEvent ev3 = mock(ISTInterpolationEvent.class);
//
//		IInterpolationFactory factory = mock(IInterpolationFactory.class);
//		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(ev3);
//
//		IInterpolator obj = new STMBRInterpolator(aligner, factory, step);
//		obj.interpolateAtTime(ev1, ev2, atTime);
//
//		ArgumentCaptor<Geometry> geomCaptor = ArgumentCaptor.forClass(Geometry.class);
//		Mockito.verify(factory, Mockito.times(1)).getSTEvent(any(Interval.class), any(EventType.class),
//				geomCaptor.capture());
//
//		List<Geometry> oplist1 = geomCaptor.getAllValues();
//
//		Geometry op1 = oplist1.get(0);
//
//		/*
//		 * -asserting that the x axis of the interpolated event is greater than the x
//		 * axis of first event(ev1) and is smaller than the x-axis of second event(ev2)-
//		 */
//		assertTrue(op1.getEnvelopeInternal().getMaxX() > ev1.getGeometry().getEnvelopeInternal().getMaxX());
//		// assertTrue(op1.getEnvelopeInternal().getMaxX() <
//		// ev2.getGeometry().getEnvelopeInternal().getMaxX());
//
//	}

//	@Test
//	void testPolygonShape_usingInterpolateBetween() {
//
//		/* -- First Event-- */
//		ISTInterpolationEvent ev1 = mock(ISTInterpolationEvent.class);
//		long duration = 3600000;
//		Duration step = new Duration(duration);
//		DateTime epoch = new DateTime();
//		EventType type = EventType.ACTIVE_REGION;
//
//		Coordinate[] coord = new Coordinate[5];
//		coord[0] = new Coordinate(0, 1);
//		coord[1] = new Coordinate(0, 4);
//		coord[2] = new Coordinate(3, 2);
//		coord[3] = new Coordinate(1, 1);
//		coord[4] = new Coordinate(0, 1);
//
//		GeometryFactory geom1 = new GeometryFactory();
//		Geometry first_geom = geom1.createPolygon(coord);
//
//		double startFractOfStep = 0.3;
//		double endFractOfStep = 2.3;
//		DateTime start_val = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep));
//		DateTime end_val = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep));
//		Interval timePeriod = new Interval(start_val, end_val);
//
//		when(ev1.getTimePeriod()).thenReturn(timePeriod);
//		when(ev1.isInterpolated()).thenReturn(false);
//		when(ev1.getGeometry()).thenReturn(first_geom);
//		when(ev1.getType()).thenReturn(type);
//		when(ev1.compareTime(any(IBaseTemporalObject.class))).thenReturn(-1);
//
//		/*-- Second Event--*/
//
//		ISTInterpolationEvent ev2 = mock(ISTInterpolationEvent.class);
//
//		Coordinate[] coord2 = new Coordinate[5];
//		coord2[0] = new Coordinate(0, 9);
//		coord2[1] = new Coordinate(0, 11);
//		coord2[2] = new Coordinate(10, 4);
//		coord2[3] = new Coordinate(8, 3);
//		coord2[4] = new Coordinate(0, 9);
//
//		Geometry second_geom = geom1.createPolygon(coord2);
//
//		double startFractOfStep2 = 3.3;
//		double endFractOfStep2 = 4.3;
//		DateTime start_val2 = new DateTime(epoch.getMillis() + (long) (duration * startFractOfStep2));
//		DateTime end_val2 = new DateTime(epoch.getMillis() + (long) (duration * endFractOfStep2));
//		Interval timePeriod2 = new Interval(start_val2, end_val2);
//
//		when(ev2.getTimePeriod()).thenReturn(timePeriod2);
//		when(ev2.isInterpolated()).thenReturn(false);
//		when(ev2.getGeometry()).thenReturn(second_geom);
//		when(ev2.getType()).thenReturn(type);
//		when(ev2.compareTime(any(IBaseTemporalObject.class))).thenReturn(1);
//
//		ITemporalAligner aligner = mock(ITemporalAligner.class);
//		when(aligner.alignEventsForInterpolation(ev1)).thenReturn(ev1);
//		when(aligner.alignEventsForInterpolation(ev2)).thenReturn(ev2);
//
//		/* Third Interpolated Object -- */
//		ISTInterpolationEvent ev3 = mock(ISTInterpolationEvent.class);
//
//		IInterpolationFactory factory = mock(IInterpolationFactory.class);
//		when(factory.getSTEvent(any(Interval.class), any(EventType.class), any(Geometry.class))).thenReturn(ev3);
//
//		IInterpolator obj = new STMBRInterpolator(aligner, factory, step);
//		obj.interpolateBetween(ev1, ev2);
//
//		ArgumentCaptor<Geometry> geomCaptor = ArgumentCaptor.forClass(Geometry.class);
//		Mockito.verify(factory, Mockito.times(3)).getSTEvent(any(Interval.class), any(EventType.class),
//				geomCaptor.capture());
//
//		List<Geometry> oplist1 = geomCaptor.getAllValues();
//
//		Geometry op1 = oplist1.get(0);
//
//		/*
//		 * -asserting that the x axis of the interpolated event is greater than the x
//		 * axis of first event(ev1) and is smaller than the x-axis of second event(ev2)-
//		 */
//		assertTrue(op1.getEnvelopeInternal().getMaxX() > ev1.getGeometry().getEnvelopeInternal().getMaxX());
//		// assertTrue(op1.getEnvelopeInternal().getMaxX() <
//		// ev2.getGeometry().getEnvelopeInternal().getMaxX());
//
//	}

}
