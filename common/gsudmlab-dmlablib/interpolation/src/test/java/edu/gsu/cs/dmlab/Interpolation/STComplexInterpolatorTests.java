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
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.distance.dtw.interfaces.IShapeSeriesAligner;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.factory.interfaces.ISeriesAlignmentFactory;
import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;
import edu.gsu.cs.dmlab.interpolation.STComplexPolygonInterpolator;
import edu.gsu.cs.dmlab.interpolation.interfaces.IInterpolator;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

class STComplexInterpolatorTests {

	@Test
	void testConstructorThrowsOnNullAligner() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = null;
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNullFactory() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = null;
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNullMultiPolyValidator() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = null;
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNullSimplifyValidator() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = null;
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNullStep() {

		Duration step = null;
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNULLAlignFactory() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = null;
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNULLShapeAlign() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = null;
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testConstructorThrowsOnNULLAreaInterpolator() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = null;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner, tsAligner,
					multipolyValidator, simplifyValidator, step);
		});
	}

	@Test
	void testInterpolateTrajectoryThrowsOnNullInputTrajectory() {
		long duration = 360000;
		Duration step = new Duration(duration);

		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationTrajectory inTrajectory = null;

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent first = null;
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = null;

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent first = null;
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		DateTime firstTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(new Interval(firstTime, step));

		DateTime secondTime = new DateTime(2012, 12, 12, 1, 12);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(new Interval(secondTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 1, 15);

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		DateTime firstTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(new Interval(firstTime, step));

		DateTime secondTime = new DateTime(2012, 12, 12, 1, 12);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(new Interval(secondTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 0, 54);

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent ev = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		DateTime evTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);
		when(ev.getTimePeriod()).thenReturn(new Interval(evTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 1, 54);

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent ev = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
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
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		ISeriesAlignmentFactory tsAlignFactory = mock(ISeriesAlignmentFactory.class);
		IShapeSeriesAligner tsAligner = mock(IShapeSeriesAligner.class);
		IInterpolator areaInterpolator = mock(IInterpolator.class);

		DateTime evTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);
		when(ev.getTimePeriod()).thenReturn(new Interval(evTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 0, 54);

		IInterpolator interp = new STComplexPolygonInterpolator(factory, tsAlignFactory, areaInterpolator, aligner,
				tsAligner, multipolyValidator, simplifyValidator, step);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAfterAtTime(ev, interpDateTime);
		});
	}
}
