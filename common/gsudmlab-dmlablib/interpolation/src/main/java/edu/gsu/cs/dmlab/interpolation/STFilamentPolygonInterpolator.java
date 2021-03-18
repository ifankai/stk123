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
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentInfo;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;
import edu.gsu.cs.dmlab.distance.dtw.interfaces.IShapeSeriesAligner;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;
import edu.gsu.cs.dmlab.factory.interfaces.ISeriesAlignmentFactory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;
import edu.gsu.cs.dmlab.interpolation.interfaces.IInterpolator;
import edu.gsu.cs.dmlab.interpolation.utils.interfaces.ISTEndpointFinder;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

/**
 * The method being implemented is from the polygon interpolation methods
 * described in <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi
 * et. al, 2018</a>.
 * 
 * @author Soukaina Filali, updated by Dustin Kempton, Data Mining Lab, Georgia
 *         State University
 *
 */
public class STFilamentPolygonInterpolator extends BaseInterpolation implements IInterpolator {

	private ISeriesAlignmentFactory tsAlignFactory;
	private IShapeSeriesAligner tsAligner;
	private IGeometryValidator multipolyValidator;
	private IGeometryValidator simplifyValidator;
	private IInterpolator arealInterpolator;
	private ISTEndpointFinder endpointFinder;

	private GeometryFactory gf = new GeometryFactory();

	/**
	 * A constructor for the ST Filament Polygon Interpolation class.
	 * 
	 * 
	 * @param factory            The factory object used to create new event and
	 *                           trajectory objects that are returned as results
	 * 
	 * @param tsAlignFactory     The factory object used to create various objects
	 *                           used in determining the proper polygon alignment
	 * 
	 * @param aligner            The temporal aligner that adjusts the start and end
	 *                           time of event reports to be some integer multiple
	 *                           of a step size away from an epoch
	 * 
	 * @param tsAligner          The series alignment object used to align the shape
	 *                           series of two different polygon objects
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
	 * @param arealInterpolator  An Are Interpolation object used to perform
	 *                           interpolation when this class fails to be able to
	 *                           perform the interpolation.
	 * 
	 * @param endpointFinder     An end point finder that is used to approximately
	 *                           align two filament detections, this is used to
	 *                           initialize a more fine grained alignment performed
	 *                           within this object
	 * 
	 * @param step               The temporal step size between interpolated
	 *                           polygons
	 */
	public STFilamentPolygonInterpolator(IInterpolationFactory factory, ISeriesAlignmentFactory tsAlignFactory,
			ITemporalAligner aligner, IShapeSeriesAligner tsAligner, IGeometryValidator multipolyValidator,
			IGeometryValidator simplifyValidator, IInterpolator arealInterpolator, ISTEndpointFinder endpointFinder,
			Duration step) {
		super(factory, aligner, step, "STFilamentPolygonInterpolator");

		if (tsAlignFactory == null)
			throw new IllegalArgumentException(
					"TS Alignment Factory cannot be null in STFilamentPolygonInterpolator constructor.");
		if (tsAligner == null)
			throw new IllegalArgumentException(
					"Shape aligner cannot be null in STFilamentPolygonInterpolator constructor.");
		if (multipolyValidator == null)
			throw new IllegalArgumentException(
					"Multipoly Geometry Validator cannot be null in STFilamentPolygonInterpolator constructor.");
		if (simplifyValidator == null)
			throw new IllegalArgumentException(
					"Simplify Geometry Validator cannot be null in STFilamentPolygonInterpolator constructor.");
		if (arealInterpolator == null)
			throw new IllegalArgumentException(
					"Areal Interpolator cannot be null in STFilamentPolygonInterpolator constructor.");

		this.multipolyValidator = multipolyValidator;
		this.simplifyValidator = simplifyValidator;
		this.arealInterpolator = arealInterpolator;
		this.endpointFinder = endpointFinder;
		this.tsAlignFactory = tsAlignFactory;
		this.tsAligner = tsAligner;
	}

	@Override
	public ISTInterpolationTrajectory interpolateTrajectory(ISTInterpolationTrajectory inTrajectory) {
		if (inTrajectory == null)
			throw new IllegalArgumentException(
					"Input Trajectory cannot be null in STFilamentPolygonInterpolator interpolateTrajecotry.");

		return super.interpolateTrajectory(inTrajectory);
	}

	@Override
	public List<ISTInterpolationEvent> interpolateBetween(ISTInterpolationEvent first, ISTInterpolationEvent second) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STFilamentPolygonInterpolator interpolateBetween.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STFilamentPolygonInterpolator interpolateBetween.");

		return super.interpolateBetween(first, second);
	}

	@Override
	public ISTInterpolationEvent interpolateBeforeAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STFilamentPolygonInterpolator interpolateBeforeAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STFilamentPolygonInterpolator interpolateBeforeAtTime.");
		if (dateTime.isAfter(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STFilamentPolygonInterpolator interpolateBeforeAtTime.");

		return super.projectEventBack(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAfterAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STFilamentPolygonInterpolator interpolateAfterAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STFilamentPolygonInterpolator interpolateAfterAtTime.");
		if (dateTime.isBefore(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STFilamentPolygonInterpolator interpolateAfterAtTime.");

		return super.projectEventForward(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAtTime(ISTInterpolationEvent first, ISTInterpolationEvent second,
			DateTime dateTime) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STFilamentPolygonInterpolator interpolateAtTime.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STFilamentPolygonInterpolator interpolateAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STFilamentPolygonInterpolator interpolateAtTime.");
		if (dateTime.isBefore(first.getTimePeriod().getStart()) || dateTime.isAfter(second.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be outside of the datetime of the input pair in STFilamentPolygonInterpolator interpolateAtTime.");

		boolean arealValid = false;

		// Verify the first polygon is valid, then find the an endpoint to use as the
		// first point when matching polygon mappings later.
		Polygon densifiedGeometry = (Polygon) this.multipolyValidator.produceValidGeometry(first.getGeometry());
		Coordinate endpointsDensifiedGeom = this.endpointFinder.findBestEndpoint(densifiedGeometry);

		// With the endpoint found on the first polygon, rearrange the points in the
		// polygon and convert it to a time series for point mapping later
		Polygon reorganizedDensifiedPolygon = (Polygon) this.rearrangePolygon(densifiedGeometry,
				endpointsDensifiedGeom);
		IShapeSeries firstTS = this.convertToTimeseries(reorganizedDensifiedPolygon);

		// Verify the second polygon is valid, then find an endpoint to use as the first
		// point when matching polygon mappings later.
		Polygon densifiedNextGeom = (Polygon) this.multipolyValidator.produceValidGeometry(second.getGeometry());
		Coordinate endpointsDensifiedNextGeom = this.endpointFinder.findBestEndpoint(densifiedNextGeom);

		// with the endpoint found on the second polygon, rearrange the points in the
		// polygon and convert it to a time series for point mapping later.
		Polygon reorganizedDensifiedNextPolygon = (Polygon) this.rearrangePolygon(densifiedNextGeom,
				endpointsDensifiedNextGeom);
		IShapeSeries nextTS = this.convertToTimeseries(reorganizedDensifiedNextPolygon);

		// start interpolating by finding the fraction of the range between the start
		// and end objcet that we are to be interpolating at
		long startTime = first.getTimePeriod().getStartMillis();
		long endTime = second.getTimePeriod().getStartMillis();
		double factor = (double) (dateTime.getMillis() - startTime) / (double) (endTime - startTime);

		// Get the mapping information for the two polygons
		IAlignmentInfo warpInfo = this.tsAligner.getWarpInfoBetween(firstTS, nextTS);

		// Use the mapping to perform interpolation between the points of the two
		// polygons that were reorganized to have the end point as the first point in
		// the geometry
		Coordinate[] i_coordinates = this.createInterpolatedCoordinates(reorganizedDensifiedPolygon,
				reorganizedDensifiedNextPolygon, warpInfo.getPath(), factor);

		// Construct a new object based on the interpolated polygon and verify that it
		// meets an area validation criteria.
		ISTInterpolationEvent interp = this.createInterpolatedSTEvent(dateTime,
				new DateTime(dateTime.getMillis() + this.step.getMillis()), first.getType(), i_coordinates);
		arealValid = this.arealValidation(interp, first, second);

		// If it doesn't meet the criteria, try using a different interpolation method
		// that tends to be less accurate but shouldn't fail.
		if (!arealValid) {
			interp = this.arealInterpolator.interpolateAtTime(first, second, dateTime);
		}
		return interp;
	}

	///////////////////////// Private Methods\\\\\\\\\\\\\\\\\\\\\\\\\\\

	/**
	 * This method assumes that the events are already aligned and that it is not
	 * supposed to add them to the results.
	 * 
	 * @param first  The first event to be used for interpolation between
	 * @param second The second event to be used for interpolation between
	 * @return A list of event reports that were created as a
	 */
	protected List<ISTInterpolationEvent> interpolateBetweenAlignedNoCopy(ISTInterpolationEvent first,
			ISTInterpolationEvent second) {
		boolean arealValid = false;

		// Verify the first polygon is valid, then find the an endpoint to use as the
		// first point when matching polygon mappings later.
		Polygon densifiedGeometry = (Polygon) this.multipolyValidator.produceValidGeometry(first.getGeometry());
		Coordinate endpointsDensifiedGeom = this.endpointFinder.findBestEndpoint(densifiedGeometry);

		// With the endpoint found on the first polygon, rearrange the points in the
		// polygon and convert it to a time series for point mapping later
		Polygon reorganizedDensifiedPolygon = (Polygon) this.rearrangePolygon(densifiedGeometry,
				endpointsDensifiedGeom);
		IShapeSeries tgpTS = this.convertToTimeseries(reorganizedDensifiedPolygon);

		// Verify the second polygon is valid, then find an endpoint to use as the first
		// point when matching polygon mappings later.
		Polygon densifiedNextGeom = (Polygon) this.multipolyValidator.produceValidGeometry(second.getGeometry());
		Coordinate endpointsDensifiedNextGeom = this.endpointFinder.findBestEndpoint(densifiedNextGeom);

		// with the endpoint found on the second polygon, rearrange the points in the
		// polygon and convert it to a time series for point mapping later.
		Polygon reorganizedDensifiedNextPolygon = (Polygon) this.rearrangePolygon(densifiedNextGeom,
				endpointsDensifiedNextGeom);
		IShapeSeries nexttgpTS = this.convertToTimeseries(reorganizedDensifiedNextPolygon);

		// start interpolating
		long ts = first.getTimePeriod().getStartMillis();
		long te = second.getTimePeriod().getStartMillis();

		// Get the mapping information for the two polygons
		IAlignmentInfo warpInfo = this.tsAligner.getWarpInfoBetween(tgpTS, nexttgpTS);

		// Loop over each of the steps between the two input events and construct an
		// interpolated event for each step
		List<ISTInterpolationEvent> results = new ArrayList<ISTInterpolationEvent>();
		for (long i = ts + this.step.getMillis(); i < te; i += this.step.getMillis()) {
			double factor = (double) (i - ts) / (double) (te - ts);
			Coordinate[] i_coordinates = this.createInterpolatedCoordinates(reorganizedDensifiedPolygon,
					reorganizedDensifiedNextPolygon, warpInfo.getPath(), factor);
			ISTInterpolationEvent interp = this.createInterpolatedSTEvent(new DateTime(i),
					new DateTime(i + this.step.getMillis()), first.getType(), i_coordinates);
			results.add(interp);
		}

		arealValid = this.arealValidation(results, first, second);
		if (!arealValid) {
			results = this.arealInterpolator.interpolateBetween(first, second);
			// Since the area interpolation adds the first and last event, we need to remove
			// them because this method isn't supposed to return results with that in them.
			results.remove(0);
			results.remove(results.size() - 1);
		}

		return results;
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

	//////////////////// Private Methods\\\\\\\\\\\\\\\\

	private boolean arealValidation(List<ISTInterpolationEvent> interpolated, ISTInterpolationEvent tgp,
			ISTInterpolationEvent nextTgp) {
		double initialArea = tgp.getGeometry().getArea();
		double lastArea = nextTgp.getGeometry().getArea();

		double minArea = Math.min(initialArea, lastArea);
		double error = 0.30;
		minArea *= (1.0 - error);

		double interpolatedMinimum = Double.MAX_VALUE;
		for (ISTInterpolationEvent itgp : interpolated) {
			double iArea = itgp.getGeometry().getArea();
			if (iArea < interpolatedMinimum) {
				interpolatedMinimum = iArea;
			}
		}
		double ratio = interpolatedMinimum / minArea;
		if (ratio < (1.0 - error)) {
			return false;
		}

		return true;
	}

	private boolean arealValidation(ISTInterpolationEvent interpolated, ISTInterpolationEvent first,
			ISTInterpolationEvent next) {

		double initialArea = first.getGeometry().getArea();
		double lastArea = next.getGeometry().getArea();

		double minArea = Math.min(initialArea, lastArea);
		double error = 0.30;
		minArea *= (1.0 - error);

		double interpolatedMinimum = Double.MAX_VALUE;

		double iArea = interpolated.getGeometry().getArea();
		if (iArea < interpolatedMinimum) {
			interpolatedMinimum = iArea;
		}

		double ratio = interpolatedMinimum / minArea;
		if (ratio < (1.0 - error)) {
			return false;
		}

		return true;
	}

	/**
	 * Gets the set of interpolated coordinates that represent the interpolated
	 * polygon based on the mapping path and the factor
	 * 
	 * @param firstGeom The first geometry to interpolate between that has a point
	 *                  ordering that matches what is expected in the alignment path
	 * 
	 * @param nextGeom  The second geometry to interpolate between that has a point
	 *                  ordering that matches what is expected in the alignment path
	 * 
	 * @param path      The alignment path that maps points in the first geometry to
	 *                  points in the second geometry
	 * 
	 * @param factor    The fraction of the distance between the two geometries that
	 *                  the interpolated geometry is supposed to be located at
	 * 
	 * @return The set of interpolated coordinates that represent the interpolated
	 *         polygon
	 */
	private Coordinate[] createInterpolatedCoordinates(Geometry firstGeom, Geometry nextGeom, IAlignmentPath path,
			double factor) {
		Coordinate[] i_coordinates = new Coordinate[path.size() + 1];
		Coordinate[] cI_geom = firstGeom.getCoordinates();
		Coordinate[] cJ_geom = nextGeom.getCoordinates();

		Iterator<ColMajorCell> pathIter = path.getMapping();
		int i = 0;
		while (pathIter.hasNext()) {
			ColMajorCell cmc = pathIter.next();
			int colIndex = cmc.getCol();
			int rowIndex = cmc.getRow();

			Coordinate c1 = cI_geom[colIndex];
			Coordinate c2 = cJ_geom[rowIndex];
			double c_i_x = super.linearInterpolate(c1.x, c2.x, factor);
			double c_i_y = super.linearInterpolate(c1.y, c2.y, factor);
			i_coordinates[i++] = new Coordinate(c_i_x, c_i_y);
		}
		// make it a closed ring
		i_coordinates[i_coordinates.length - 1] = i_coordinates[0];
		return i_coordinates;
	}

	/**
	 * Reorganize a polygon such that it starts(and ends) with the input Coordinate.
	 * 
	 * @param polygon  The polygon to reorder so that the input is the first point
	 *                 in the ring
	 * 
	 * @param endpoint The point to search for and place as the first point in the
	 *                 coordinate ring
	 * 
	 * @return the new re-arragend geometry that starts from the best endpoint
	 */
	Geometry rearrangePolygon(Polygon polygon, Coordinate endpoint) {
		int index = Integer.MAX_VALUE;

		for (int i = 0; i < polygon.getNumPoints(); i++) {
			if (polygon.getCoordinates()[i].x == endpoint.x && polygon.getCoordinates()[i].y == endpoint.y) {
				index = i;
				break;
			}
		}
		if (index == Integer.MAX_VALUE) {
			return null;
		} else
			return this.shiftVerticesByOffset(polygon, index);
	}

	private IShapeSeries convertToTimeseries(Geometry geom) {
		if (geom == null) {
			return null;
		} else if (!(geom instanceof Polygon)) {
			geom = this.multipolyValidator.produceValidGeometry(geom);
			return this.convertToTimeseries(geom);
		} else {
			IShapeSeries TSRepresentation = this.tsAlignFactory.getTimeSeries(1); // 1-dimensional time series
			Coordinate centroid = geom.getCentroid().getCoordinate();
			Coordinate[] geomCoordinates = geom.getCoordinates();
			for (int i = 0; i < geom.getCoordinates().length - 1; i++) {
				Coordinate c = geomCoordinates[i];
				final double distance = c.distance(centroid);
				TSRepresentation.addLast((int) i, this.tsAlignFactory.getTSPoint(new double[] { distance }));
			}

			return TSRepresentation;
		}
	}

	private Geometry shiftVerticesByOffset(Geometry geom, int offset) {
		if (geom == null) {
			return null;
		} else if (!(geom instanceof Polygon)) {
			geom = this.multipolyValidator.produceValidGeometry(geom);
			return this.shiftVerticesByOffset(geom, offset);
		} else {
			Coordinate[] coordinates = geom.getCoordinates();
			Coordinate[] shiftedCoordinates = new Coordinate[coordinates.length + 1];
			for (int i = offset; i <= coordinates.length + offset; i++) {
				Coordinate c = coordinates[i % coordinates.length];
				shiftedCoordinates[i - offset] = c;
			}
			return this.gf.createPolygon(shiftedCoordinates);
		}
	}

}
