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
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;
import edu.gsu.cs.dmlab.interpolation.STArealPolygonInterpolator;
import edu.gsu.cs.dmlab.interpolation.interfaces.IInterpolator;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

class STArealInterpolatorTests {

	@Test
	void testConstructorThrowsOnNullAligner() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = null;
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		double areaBufferDistance = 1.5;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator, step,
					areaBufferDistance);
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
		double areaBufferDistance = 1.5;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator, step,
					areaBufferDistance);
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
		double areaBufferDistance = 1.5;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator, step,
					areaBufferDistance);
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
		double areaBufferDistance = 1.5;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator, step,
					areaBufferDistance);
		});
	}

	@Test
	void testConstructorThrowsOnNullStep() {

		Duration step = null;
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		double areaBufferDistance = 1.5;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator, step,
					areaBufferDistance);
		});
	}

	@Test
	void testConstructorThrowsOnBufferDistanceLEQZero() {

		long duration = 3600000;
		Duration step = new Duration(duration);
		ITemporalAligner aligner = mock(ITemporalAligner.class);
		IInterpolationFactory factory = mock(IInterpolationFactory.class);
		IGeometryValidator multipolyValidator = mock(IGeometryValidator.class);
		IGeometryValidator simplifyValidator = mock(IGeometryValidator.class);
		double areaBufferDistance = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator, step,
					areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationTrajectory inTrajectory = null;

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent first = null;
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = null;

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent first = null;
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		DateTime firstTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(new Interval(firstTime, step));

		DateTime secondTime = new DateTime(2012, 12, 12, 1, 12);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(new Interval(secondTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 1, 15);

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		DateTime firstTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent first = mock(ISTInterpolationEvent.class);
		when(first.getTimePeriod()).thenReturn(new Interval(firstTime, step));

		DateTime secondTime = new DateTime(2012, 12, 12, 1, 12);
		ISTInterpolationEvent second = mock(ISTInterpolationEvent.class);
		when(second.getTimePeriod()).thenReturn(new Interval(secondTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 0, 54);

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent ev = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		DateTime evTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);
		when(ev.getTimePeriod()).thenReturn(new Interval(evTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 1, 54);

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent ev = null;

		DateTime dateTime = new DateTime();

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);

		DateTime dateTime = null;

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
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
		double areaBufferDistance = 1;

		DateTime evTime = new DateTime(2012, 12, 12, 1, 0);
		ISTInterpolationEvent ev = mock(ISTInterpolationEvent.class);
		when(ev.getTimePeriod()).thenReturn(new Interval(evTime, step));

		DateTime interpDateTime = new DateTime(2012, 12, 12, 0, 54);

		IInterpolator interp = new STArealPolygonInterpolator(factory, aligner, multipolyValidator, simplifyValidator,
				step, areaBufferDistance);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			interp.interpolateAfterAtTime(ev, interpDateTime);
		});
	}
}
