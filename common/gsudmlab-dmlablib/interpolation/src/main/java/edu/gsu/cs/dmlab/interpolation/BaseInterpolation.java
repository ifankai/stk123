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
package edu.gsu.cs.dmlab.interpolation;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;
import edu.gsu.cs.dmlab.util.PositionEstimator;

public abstract class BaseInterpolation {

	protected IInterpolationFactory factory;
	protected ITemporalAligner aligner;
	protected Duration step;

	public BaseInterpolation(IInterpolationFactory factory, ITemporalAligner aligner, Duration step,
			String derivedName) {
		if (derivedName == null)
			throw new IllegalArgumentException("String derived name cannot be null in BaseInterpolation constructor.");
		if (aligner == null)
			throw new IllegalArgumentException("aligner cannot be null in " + derivedName + " constructor.");
		if (factory == null)
			throw new IllegalArgumentException("factory cannot be null in " + derivedName + " constructor.");
		if (step == null)
			throw new IllegalArgumentException("step cannot be null in " + derivedName + " constructor.");

		this.factory = factory;
		this.aligner = aligner;
		this.step = step;
	}

	//////////////////////// Abstract Methods\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	/**
	 * This method assumes that the events are already aligned and that it is not
	 * supposed to add them to the results.
	 * 
	 * @param first  The first event to be used for interpolation between
	 * 
	 * @param second The second event to be used for interpolation between
	 * 
	 * @return A list of event reports that were created as a
	 */
	protected abstract List<ISTInterpolationEvent> interpolateBetweenAlignedNoCopy(ISTInterpolationEvent first,
			ISTInterpolationEvent second);

	/**
	 * This method is used to produce and verify the polygon of an event with the
	 * input data.
	 * 
	 * @param startTime     The start time of the event
	 * 
	 * @param endTime       The end time of the event
	 * 
	 * @param type          The type of the event
	 * 
	 * @param i_coordinates The coordinates of the polygon to use in the event
	 * @return
	 */
	protected abstract ISTInterpolationEvent createInterpolatedSTEvent(DateTime startTime, DateTime endTime,
			EventType type, Coordinate[] i_coordinates);

	///////// Actual Implementations Shared With Multiple Classes \\\\\\\\\\\\
	/**
	 * Interpolates new event reports between the reports contained within the input
	 * trajectory and returns a new trajectory object with the new data. The
	 * original event reports are copied and temporally aligned with a epoch and a
	 * set cadence rate. The original trajectory shall remain unedited.
	 * 
	 * @param inTrajectory The input trajectory that is to be interpolated.
	 * 
	 * @return An interpolated trajectory that is a representation of the input if
	 *         it had reports at the desired cadence rate.
	 */
	protected ISTInterpolationTrajectory interpolateTrajectory(ISTInterpolationTrajectory inTrajectory) {

		// First align the trajectory event reports to be an integer multiple of some
		// step away from some epoch
		inTrajectory = this.aligner.alignEventsForStepFromEpoch(inTrajectory);

		// Construct a list to hold the interpolation results
		List<ISTInterpolationEvent> result = new ArrayList<ISTInterpolationEvent>();

		// Get the list of event reports to process.
		List<ISTInterpolationEvent> inputEventList = new ArrayList<ISTInterpolationEvent>(inTrajectory.getSTObjects());
		for (int i = 0; i < inputEventList.size() - 1; i++) {

			// Get the first event to interpolate between
			ISTInterpolationEvent first = inputEventList.get(i);
			// Get the second event to interpolate between
			ISTInterpolationEvent second = inputEventList.get(i + 1);

			long steps = (second.getTimePeriod().getStartMillis() - (first.getTimePeriod().getStartMillis() - 1))
					/ this.step.getMillis();

			// If the length of the interval for the first event is greater than 1, then we
			// can process to add interpolated events between it and the next event in the
			// trajectory.
			if (steps > 1) {

				// Add the first object but update its interval to be only one step in size.
				ISTInterpolationEvent firstCopy;
				if (first.isInterpolated()) {
					// If it is an interpolated event report, then it doesn't have an id
					firstCopy = this.factory.getSTEvent(
							new Interval(first.getTimePeriod().getStartMillis(),
									first.getTimePeriod().getStartMillis() + this.step.getMillis()),
							first.getType(), first.getGeometry());
				} else {
					firstCopy = this.factory.getSTEvent(first.getId(),
							new Interval(first.getTimePeriod().getStartMillis(),
									first.getTimePeriod().getStartMillis() + this.step.getMillis()),
							first.getType(), first.getGeometry());
				}
				result.add(firstCopy);

				// Get the list of interpolated events between the two events being processed
				List<ISTInterpolationEvent> betweenList = this.interpolateBetweenAlignedNoCopy(first, second);
				result.addAll(betweenList);
			} else {
				// If the interval was less than or equal to 1 step, then we don't process, just
				// add the aligned copy to the results.
				result.add(first);
			}
		}

		// Now we need to handle the extrapolation of the last event in the trajectory.
		ISTInterpolationEvent last = inputEventList.get(inputEventList.size() - 1);
		if (last.getTimePeriod().toDurationMillis() / this.step.getMillis() > 1) {
			// Same as before, if the interval is greater than 1 step, then we process
			ISTInterpolationEvent lastCopy;
			if (last.isInterpolated()) {
				// If event is interpolated, then it doesn't have an id
				lastCopy = this.factory.getSTEvent(new Interval(last.getTimePeriod().getStart(), this.step),
						last.getType(), last.getGeometry());
			} else {
				lastCopy = this.factory.getSTEvent(last.getId(),
						new Interval(last.getTimePeriod().getStart(), this.step), last.getType(), last.getGeometry());
			}
			result.add(lastCopy);

			List<ISTInterpolationEvent> betweenList = this.extrapolateLastEvent(last);
			result.addAll(betweenList);
		} else {
			// If the interval was less than or equal to 1 step, then we don't process, just
			// add the aligned copy to the results
			result.add(last);
		}

		return this.factory.getSTTrajectory(result);
	}

	/**
	 * Interpolates new event reports between the two reports that are passed in and
	 * returns a list of the interpolated polygon representations. The original
	 * event reports are copied to be the first and last elements in the returned
	 * list. They are also temporally aligned with an epoch and a set cadence rate.
	 * The original objects shall remain unedited.
	 * 
	 * @param first  The first element to interpolate between
	 * 
	 * @param second The end element to interpolate between
	 * 
	 * @return A list of interpolated polygon objects between the two input objects
	 */
	protected List<ISTInterpolationEvent> interpolateBetween(ISTInterpolationEvent first,
			ISTInterpolationEvent second) {

		// First we align the input events so they are an integer multiple of steps away
		// from an epoch
		first = this.aligner.alignEventsForInterpolation(first);
		second = this.aligner.alignEventsForInterpolation(second);

		long steps = (second.getTimePeriod().getStartMillis() - (first.getTimePeriod().getStartMillis() - 1))
				/ this.step.getMillis();

		List<ISTInterpolationEvent> results = new ArrayList<ISTInterpolationEvent>();

		// If the length of the interval for the first event is greater than 1, then we
		// can process to add interpolated events between it and the next event
		if (steps > 1) {
			ISTInterpolationEvent firstCopy;
			if (!first.isInterpolated()) {
				Interval timePeriod = new Interval(first.getTimePeriod().getStart(), this.step);
				firstCopy = this.factory.getSTEvent(first.getId(), timePeriod, first.getType(), first.getGeometry());
			} else {
				Interval timePeriod = new Interval(first.getTimePeriod().getStart(), this.step);
				firstCopy = this.factory.getSTEvent(timePeriod, first.getType(), first.getGeometry());
			}
			results.add(firstCopy);
			List<ISTInterpolationEvent> between = this.interpolateBetweenAlignedNoCopy(first, second);
			results.addAll(between);
		} else {
			// If the interval was less than or equal to 1 step, then we don't process, just
			// add the aligned copy to the results
			results.add(first);
		}

		if (second.getTimePeriod().toDurationMillis() / this.step.getMillis() > 1) {
			// Same as before, if the interval is greater than 1 step, then we process
			ISTInterpolationEvent secondCopy;
			if (second.isInterpolated()) {
				// If event is interpolated, then it doesn't have an id
				secondCopy = this.factory.getSTEvent(new Interval(second.getTimePeriod().getStart(), this.step),
						second.getType(), second.getGeometry());
			} else {
				secondCopy = this.factory.getSTEvent(second.getId(),
						new Interval(second.getTimePeriod().getStart(), this.step), second.getType(),
						second.getGeometry());
			}

			results.add(secondCopy);
		} else {
			// If the interval was less than or equal to 1 step, then we don't process, just
			// add the aligned copy to the results
			results.add(second);
		}

		return results;
	}

	/**
	 * Method projects an event forward using the approximate differential rotation
	 * of the sun
	 * 
	 * @param ev   The event to project forward
	 * 
	 * @param time The new time to project the location of the event at
	 * 
	 * @return A new interpolated event projected forward in time
	 */
	protected ISTInterpolationEvent projectEventForward(ISTInterpolationEvent ev, DateTime time) {

		long offset = time.getMillis() - ev.getTimePeriod().getStartMillis();
		Coordinate[] coords = PositionEstimator.getPredictedPos((Polygon) ev.getGeometry(), offset).getCoordinates();
		ISTInterpolationEvent interpEv = this.createInterpolatedSTEvent(time,
				new DateTime(time.getMillis() + this.step.getMillis()), ev.getType(), coords);

		return interpEv;
	}

	/**
	 * Method projects an event back using the approximate differential rotation of
	 * the sun
	 * 
	 * @param ev   The event to project backwards
	 * 
	 * @param time The new time to project the location of the event at
	 * 
	 * @return A new interpolated event project back in time
	 */
	protected ISTInterpolationEvent projectEventBack(ISTInterpolationEvent ev, DateTime time) {

		long offset = ev.getTimePeriod().getStartMillis() - time.getMillis();
		Coordinate[] coords = PositionEstimator.getPredictedPos((Polygon) ev.getGeometry(), -offset).getCoordinates();
		ISTInterpolationEvent interpEv = this.createInterpolatedSTEvent(time,
				new DateTime(time.getMillis() + this.step.getMillis()), ev.getType(), coords);

		return interpEv;
	}

	/**
	 * Linearly interpolates a 1D variable (between x_0 and x_n). factor is the step
	 * size Say you have x_0 = 1, x_n = 11, factor=0.4, your interpolated result
	 * will be 5
	 * 
	 * @param x_0    - minimum bound
	 * @param x_n    - maximum bound
	 * @param factor - the factor of linear interpolation (factor must be between 0
	 *               and 1)
	 * @return - interpolated result
	 */
	protected double linearInterpolate(double x_0, double x_n, double factor) {

		if (factor < 0.0 || factor > 1.0) {
			return Double.NaN;
		} else {
			return x_0 + ((x_n - x_0) * factor);
		}

	}

	///////////////////////// Private Methods\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	private List<ISTInterpolationEvent> extrapolateLastEvent(ISTInterpolationEvent ev) {
		List<ISTInterpolationEvent> result = new ArrayList<ISTInterpolationEvent>();

		// start extrapolating
		long ts = ev.getTimePeriod().getStartMillis();
		long te = ev.getTimePeriod().getEndMillis();

		for (long i = ts + this.step.getMillis(); i < te; i += this.step.getMillis()) {
			ISTInterpolationEvent interpEv = this.createInterpolatedSTEvent(new DateTime(i),
					new DateTime(i + this.step.getMillis()), ev.getType(),
					PositionEstimator.getPredictedPos((Polygon) ev.getGeometry(), (i - ts)).getCoordinates());
			result.add(interpEv);
		} // end of interpolation

		return result;
	}

}
