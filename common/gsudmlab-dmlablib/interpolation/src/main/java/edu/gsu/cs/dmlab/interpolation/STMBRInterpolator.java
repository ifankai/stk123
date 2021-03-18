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
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.interpolation.interfaces.IInterpolator;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

/**
 * The method being implemented is from the polygon interpolation methods
 * described in <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi
 * et. al, 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class STMBRInterpolator extends BaseInterpolation implements IInterpolator {

	private GeometryFactory gf = new GeometryFactory();

	/**
	 * Constructor that provides a temporal aligner, a factory, and a step size. The
	 * aligner is used to align the timestamps of the original event reports to be
	 * an integer multiple of a step size from an epoch. The factory object is used
	 * for generating new trajectory and detection objects to hold the resultant
	 * interpolated polygons between the original event reports.
	 * 
	 * @param factory The factory used to generate new objects to return
	 * 
	 * @param aligner Aligner used to adjust the datetime of the input detections
	 * 
	 * @param step    The step size between interpolated reports
	 */
	public STMBRInterpolator(IInterpolationFactory factory, ITemporalAligner aligner, Duration step) {
		super(factory, aligner, step, "STMBRInterpolator");
	}

	@Override
	public ISTInterpolationTrajectory interpolateTrajectory(ISTInterpolationTrajectory inTrajectory) {
		if (inTrajectory == null)
			throw new IllegalArgumentException(
					"Input Trajectory cannot be null in STMBRInterpolator interpolateTrajecotry.");

		return super.interpolateTrajectory(inTrajectory);
	}

	@Override
	public List<ISTInterpolationEvent> interpolateBetween(ISTInterpolationEvent first, ISTInterpolationEvent second) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STMBRInterpolator interpolateBetween.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STMBRInterpolator interpolateBetween.");

		return super.interpolateBetween(first, second);
	}

	@Override
	public ISTInterpolationEvent interpolateBeforeAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STMBRInterpolator interpolateBeforeAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STMBRInterpolator interpolateBeforeAtTime.");
		if (dateTime.isAfter(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STMBRInterpolator interpolateBeforeAtTime.");

		return super.projectEventBack(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAfterAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STMBRInterpolator interpolateAfterAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STMBRInterpolator interpolateAfterAtTime.");
		if (dateTime.isBefore(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STMBRInterpolator interpolateAfterAtTime.");

		return super.projectEventForward(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAtTime(ISTInterpolationEvent first, ISTInterpolationEvent second,
			DateTime dateTime) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STMBRInterpolator interpolateAtTime.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STMBRInterpolator interpolateAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException("DateTime input cannot be null in STMBRInterpolator interpolateAtTime.");
		if (dateTime.isBefore(first.getTimePeriod().getStart()) || dateTime.isAfter(second.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be outside of the datetime of the input pair in STMBRInterpolator interpolateAtTime.");

		// start interpolating by getting the start time and end time of the objects we
		// are interpolating between
		long timeSart = first.getTimePeriod().getStartMillis();
		long timeEnd = second.getTimePeriod().getStartMillis();

		EventType type = first.getType();

		// The points of the first object's MBR
		Envelope env = first.getGeometry().getEnvelopeInternal();
		double maxX_0 = env.getMaxX();
		double maxY_0 = env.getMaxY();
		double minX_0 = env.getMinX();
		double minY_0 = env.getMinY();

		// The points of the second object's MBR
		Envelope nextEnv = second.getGeometry().getEnvelopeInternal();
		double maxX_n = nextEnv.getMaxX();
		double maxY_n = nextEnv.getMaxY();
		double minX_n = nextEnv.getMinX();
		double minY_n = nextEnv.getMinY();

		// Get the fraction of the distance between the start and end times to construct
		// the interpolated MBR for.
		double factor = (double) (dateTime.getMillis() - timeSart) / (double) (timeEnd - timeSart);
		double maxX_i = this.linearInterpolate(maxX_0, maxX_n, factor);
		double minX_i = this.linearInterpolate(minX_0, minX_n, factor);
		double maxY_i = this.linearInterpolate(maxY_0, maxY_n, factor);
		double minY_i = this.linearInterpolate(minY_0, minY_n, factor);

		Coordinate[] coords = new Coordinate[5];
		coords[0] = new Coordinate(minX_i, minY_i);
		coords[0] = new Coordinate(minX_i, maxY_i);
		coords[0] = new Coordinate(maxX_i, maxY_i);
		coords[0] = new Coordinate(maxX_i, minY_i);
		coords[4] = coords[0];

		ISTInterpolationEvent tgpi = this.createInterpolatedSTEvent(dateTime,
				new DateTime(dateTime.getMillis() + this.step.getMillis()), type, coords);
		return tgpi;
	}

	@Override
	protected List<ISTInterpolationEvent> interpolateBetweenAlignedNoCopy(ISTInterpolationEvent first,
			ISTInterpolationEvent second) {
		List<ISTInterpolationEvent> result = new ArrayList<ISTInterpolationEvent>();

		// start interpolating
		long ts = first.getTimePeriod().getStartMillis();
		long te = second.getTimePeriod().getStartMillis();
		// Loop over the range between the start and end by size step
		for (long i = ts + this.step.getMillis(); i < te; i += this.step.getMillis()) {
			ISTInterpolationEvent interpEv = this.interpolateAtTime(first, second, new DateTime(i));
			result.add(interpEv);
			first = interpEv;
		}
		return result;
	}

	@Override
	protected ISTInterpolationEvent createInterpolatedSTEvent(DateTime startTime, DateTime endTime, EventType type,
			Coordinate[] i_coordinates) {
		Geometry geom = this.gf.createPolygon(i_coordinates);
		return this.factory.getSTEvent(new Interval(startTime, endTime), type, geom);
	}

}
