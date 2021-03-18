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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;
import edu.gsu.cs.dmlab.interpolation.interfaces.IInterpolator;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

/**
 * The method being implemented is from the polygon interpolation methods
 * described in <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi
 * et. al, 2018</a>.
 * 
 * @author Original Soukaina Filali Boubrahimi, refactored by Dustin Kempton,
 *         Data Mining Lab, Georgia State University
 *
 */
public class STArealPolygonInterpolator extends BaseInterpolation implements IInterpolator {

	private Duration step;
	private double areaBufferDistance = 1.5;
	private IGeometryValidator multipolyValidator;
	private IGeometryValidator simplifyValidator;
	private GeometryFactory gf = new GeometryFactory();

	/**
	 * Constructor for the Areal Polygon Interpolator method.
	 * 
	 * @param factory            The factory object used to create new event and
	 *                           trajectory objects that are returned as results
	 * 
	 * @param aligner            The temporal aligner that adjusts the start and end
	 *                           time of event reports to be some integer multiple
	 *                           of a step size away from an epoch
	 * 
	 * @param multipolyValidator The polygon validator object that cleans up the
	 *                           interpolated polygons, mostly used for complex
	 *                           geometry objects that may contain multiple polygons
	 * 
	 * @param simplifyValidator  A simple polygon validator that is really only used
	 *                           to verify that the points in the polygon are listed
	 *                           in counter clockwise order and are a minimum
	 *                           distance from each other, otherwise they are
	 *                           removed to simplify the polygon input
	 * 
	 * @param step               The temporal step size between interpolated
	 *                           polygons
	 * 
	 * @param areaBufferDistance The buffer that is added to input polygons to
	 *                           perform a simple interpolation action, the larger
	 *                           the value the less similar the interpolated polygon
	 *                           will be to the original. The value must be a
	 *                           positive value, greater than zero. The default used
	 *                           previously was 1.5, so that might be a good place
	 *                           to start.
	 */
	public STArealPolygonInterpolator(IInterpolationFactory factory, ITemporalAligner aligner,
			IGeometryValidator multipolyValidator, IGeometryValidator simplifyValidator, Duration step,
			double areaBufferDistance) {
		super(factory, aligner, step, "STArealPolygonInterpolator");
		if (multipolyValidator == null)
			throw new IllegalArgumentException(
					"Multipoly Geometry Validator cannot be null in STArealPolygonInterpolator constructor.");
		if (simplifyValidator == null)
			throw new IllegalArgumentException(
					"Simplify Geometry Validator cannot be null in STArealPolygonInterpolator constructor.");
		if (step == null)
			throw new IllegalArgumentException("Step cannot be null in STArealPolygonInterpolator constructor.");
		if (areaBufferDistance <= 0)
			throw new IllegalArgumentException(
					"Buffer Distance cannot be less than 0 in STArealPolygonInterpolator constructor.");

		this.areaBufferDistance = areaBufferDistance;
		this.multipolyValidator = multipolyValidator;
		this.simplifyValidator = simplifyValidator;
	}

	@Override
	public ISTInterpolationTrajectory interpolateTrajectory(ISTInterpolationTrajectory inTrajectory) {
		if (inTrajectory == null)
			throw new IllegalArgumentException(
					"Input Trajectory cannot be null in STArealPolygonInterpolator interpolateTrajecotry.");

		return super.interpolateTrajectory(inTrajectory);
	}

	@Override
	public List<ISTInterpolationEvent> interpolateBetween(ISTInterpolationEvent first, ISTInterpolationEvent second) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STArealPolygonInterpolator interpolateBetween.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STArealPolygonInterpolator interpolateBetween.");

		return super.interpolateBetween(first, second);
	}

	@Override
	public ISTInterpolationEvent interpolateBeforeAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STArealPolygonInterpolator interpolateBeforeAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STArealPolygonInterpolator interpolateBeforeAtTime.");
		if (dateTime.isAfter(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STArealPolygonInterpolator interpolateBeforeAtTime.");

		return super.projectEventBack(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAfterAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STArealPolygonInterpolator interpolateAfterAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STArealPolygonInterpolator interpolateAfterAtTime.");
		if (dateTime.isBefore(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STArealPolygonInterpolator interpolateAfterAtTime.");

		return super.projectEventForward(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAtTime(ISTInterpolationEvent first, ISTInterpolationEvent second,
			DateTime dateTime) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STArealPolygonInterpolator interpolateAtTime.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STArealPolygonInterpolator interpolateAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STArealPolygonInterpolator interpolateAtTime.");
		if (dateTime.isBefore(first.getTimePeriod().getStart()) || dateTime.isAfter(second.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be outside of the datetime of the input pair in STArealPolygonInterpolator interpolateAtTime.");

		// start interpolating
		long ts = first.getTimePeriod().getStartMillis();
		long te = second.getTimePeriod().getStartMillis();

		double lifespan = (double) (te - ts);
		// What fraction of the temporal distance between the two input events is it
		// that we are to generate an event at.
		double firstfactor = (double) (dateTime.getMillis() - ts) / lifespan;
		// What is the remainder of the distance between the two input events after the
		// event we are to generate.
		double secondfactor = 1.0 - firstfactor;

		// The desired area is then the weighted sum of the are of the first and second
		// input geometries
		double desiredArea = firstfactor * first.getGeometry().getArea()
				+ secondfactor * second.getGeometry().getArea();

		ISTInterpolationEvent result = null;
		long midPointMillis = ts + (long) (lifespan / 2);
		if (dateTime.getMillis() <= midPointMillis) {
			// If at midpoint or less, then we use the first geometry as the buffered
			// geometry
			Geometry interpolatedGeometry = first.getGeometry();
			interpolatedGeometry = this.createBufferedGeometry(interpolatedGeometry, desiredArea);

			// If the buffered geometry failed, then we fall back to simply moving the
			// original
			if (interpolatedGeometry == null || interpolatedGeometry.isEmpty())
				interpolatedGeometry = first.getGeometry();

			// Move the buffered geometry towards the end event by the first distance
			// calculated above
			Coordinate[] i_coordinates = this.moveCoordinates(second.getGeometry(), interpolatedGeometry, firstfactor);
			result = this.createInterpolatedSTEvent(dateTime,
					new DateTime(dateTime.getMillis() + this.step.getMillis()), first.getType(), i_coordinates);
		} else {
			// If past midpoint, then we use the second geometry as the buffered geometry
			Geometry interpolatedGeometry = second.getGeometry();
			interpolatedGeometry = this.createBufferedGeometry(interpolatedGeometry, desiredArea);

			// If the buffered geometry failed, then we fall back to simply moving the
			// original
			if (interpolatedGeometry == null || interpolatedGeometry.isEmpty())
				interpolatedGeometry = second.getGeometry();

			// Move the buffered geometry towards the beginning event by the second distance
			// calculated above
			Coordinate[] i_coordinates = this.moveCoordinates(first.getGeometry(), interpolatedGeometry, secondfactor);
			result = this.createInterpolatedSTEvent(dateTime,
					new DateTime(dateTime.getMillis() + this.step.getMillis()), first.getType(), i_coordinates);
		}

		return result;
	}

	/////////////////////////// Private Methods\\\\\\\\\\\\\\\\\\\\\\\\\

	/**
	 * This method assumes that the events are already aligned and that it is not
	 * supposed to add them to the results.
	 * 
	 * @param first  The first event to be used for interpolation between
	 * @param second The second event to be used for interpolation between
	 * @return A list of event reports that were created as a
	 */
	@Override
	protected List<ISTInterpolationEvent> interpolateBetweenAlignedNoCopy(ISTInterpolationEvent first,
			ISTInterpolationEvent second) {
		List<ISTInterpolationEvent> result = new ArrayList<ISTInterpolationEvent>();

		// start interpolating
		long ts = first.getTimePeriod().getStartMillis();
		long te = second.getTimePeriod().getStartMillis();
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
		Geometry sgeom = this.simplifyValidator.produceValidGeometry(geom);

		if (sgeom != null) {
			geom = this.multipolyValidator.produceValidGeometry(sgeom);
		} else if (sgeom == null && geom != null) {
			geom = this.multipolyValidator.produceValidGeometry(geom);
		}

		return this.factory.getSTEvent(new Interval(startTime, endTime), type, geom);
	}

	/**
	 * Move a coordinate array in space by interpolating the centroid with respect
	 * to time and use the produced offset to move the geometry.
	 * 
	 * @param targetGeometry The geometry that the input geometry is to be moved
	 *                       towards.
	 * 
	 * @param geometryToMove The geometry that is to be moved.
	 * 
	 * @param factor         The fraction of the distance between the input and
	 *                       target that the input is to be moved. [0,1] range,
	 *                       where 0 means the input geometry isn't moved at all
	 *                       towards the target geometry and 1 means the input is
	 *                       moved all the way to the input geometry.
	 * 
	 * @return A copy of the input geometry, but the points have been moved towards
	 *         the target geometry by the factor
	 */
	private Coordinate[] moveCoordinates(Geometry targetGeometry, Geometry geometryToMove, double factor) {
		Point center_target = targetGeometry.getCentroid();
		Point center = geometryToMove.getCentroid();

		Coordinate moved_center = new Coordinate(this.linearInterpolate(center.getX(), center_target.getX(), factor),
				this.linearInterpolate(center.getY(), center_target.getY(), factor));

		double offsetX = moved_center.x - center.getX();
		double offsetY = moved_center.y - center.getY();

		Coordinate[] orig_coordinates = geometryToMove.getCoordinates();
		Coordinate[] moved_coordinates = new Coordinate[geometryToMove.getCoordinates().length];

		for (int i = 0; i < orig_coordinates.length; i++) {
			Coordinate c = orig_coordinates[i];
			Coordinate mooved_c = new Coordinate(c.x + offsetX, c.y + offsetY);
			moved_coordinates[i] = mooved_c;
		}

		return moved_coordinates;

	}

	/**
	 * Create an interpolated geometry based on an initial geometry that will be
	 * buffered to reach a desired area. The buffer distance will be reduced once
	 * the area of the geometry approaches the desired area for better accuracy.
	 * 
	 * @param geomToInterp the geometry object to interpolate using a buffer
	 * 
	 * @param desiredArea  The area that is desired for the interpolated
	 *                     approximation
	 * 
	 * @return The approximated geometry produced by adding a buffer.
	 */
	private Geometry createBufferedGeometry(Geometry geomToInterp, double desiredArea) {

		double geomArea = geomToInterp.getArea();
		// If already there, then why process any more.
		if (geomArea == desiredArea)
			return geomToInterp;
		else if (geomArea < desiredArea) {
			double bufferIncrement = this.areaBufferDistance;
			while (geomArea < desiredArea) {
				// To increase accuracy, buffer slowly when the desired area is approached
				if (geomArea > 0.8 * desiredArea) {
					bufferIncrement = 0.05;
				}
				// Add buffer then force the geometry to be valid
				geomToInterp = geomToInterp.buffer(bufferIncrement);
				geomToInterp = this.multipolyValidator.produceValidGeometry(geomToInterp);
				geomArea = geomToInterp.getArea();
			}
		} else {
			double bufferIncrement = -this.areaBufferDistance;

			while (geomArea > desiredArea) {
				// To increase accuracy, increment slowly when the desired area is approached
				if (geomArea < 1.2 * desiredArea) {
					bufferIncrement = -0.05;
				}

				geomToInterp = geomToInterp.buffer(bufferIncrement);
				if (geomToInterp instanceof MultiPolygon) {
					geomToInterp = this.multipolyValidator.produceValidGeometry(geomToInterp);
					geomArea = geomToInterp.getArea();
					// The MAX POLYGON from the MULTIPOLYGON is good enough
					if (geomArea > 0.7 * desiredArea && geomArea < desiredArea) {
						return geomToInterp;
					}
					// In case the MULTIPOLYGON has been validated by a convex hull strategy
					else if (geomArea > desiredArea) {
						continue;
					} else {
						return null;
					}
				} else {
					geomToInterp = this.multipolyValidator.produceValidGeometry(geomToInterp);
				}
			}
		}
		return geomToInterp;
	}

}
