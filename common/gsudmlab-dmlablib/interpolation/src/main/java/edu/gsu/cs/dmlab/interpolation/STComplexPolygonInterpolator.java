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
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.locationtech.jts.densify.Densifier;
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
public class STComplexPolygonInterpolator extends BaseInterpolation implements IInterpolator {

	// These were constants in the previous version, so I made them constant here.
	// Both densifierPointBound and simplifierDistance are used as settings on
	// increasing the points in a polygon.
	private double densifierPointBound = 150;
	private double simplifierDistance = 1.0;
	// acceptedError is the difference in area allowed between the interpolated
	// polygon and the original polygons
	private double acceptedError = 0.3;
	// Number of different offsets are tried to determine the best alignment of the
	// two polygons.
	private int timeSeriesSimilaritySteps = 30;

	private ISeriesAlignmentFactory tsAlignFactory;
	private IShapeSeriesAligner tsAligner;
	private IGeometryValidator multipolyValidator;
	private IGeometryValidator simplifyValidator;
	private GeometryFactory gf = new GeometryFactory();
	private IInterpolator areaInterpolator;

	/**
	 * Constructor for the Complex Polygon Interpolator method that uses DTW on the
	 * shape signature to find an alignment of the input polygons.
	 * 
	 * @param factory            The factory object used to create new event and
	 *                           trajectory objects that are returned as results
	 * 
	 * @param tsAlignFactory     The factory object used to create various objects
	 *                           used in determining the proper polygon alignment
	 * 
	 * @param areaInterpolator   An interpolation object used when this
	 *                           interpolation object fails to produce an
	 *                           interpolation due to various difficulties
	 *                           encountered while attempting to interpolate
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
	 * @param step               The temporal step size between interpolated
	 *                           polygons
	 */
	public STComplexPolygonInterpolator(IInterpolationFactory factory, ISeriesAlignmentFactory tsAlignFactory,
			IInterpolator areaInterpolator, ITemporalAligner aligner, IShapeSeriesAligner tsAligner,
			IGeometryValidator multipolyValidator, IGeometryValidator simplifyValidator, Duration step) {
		super(factory, aligner, step, "STComplexPolygonInterpolator");

		if (tsAlignFactory == null)
			throw new IllegalArgumentException(
					"TS Alignment Factory cannot be null in STComplexPolygonInterpolator constructor.");
		if (tsAligner == null)
			throw new IllegalArgumentException(
					"Shape Aligner cannot be null in STComplexPolygonInterpolator constructor.");
		if (areaInterpolator == null)
			throw new IllegalArgumentException(
					"Area Interpolator cannot be null in STComplexPolygonInterpolator constructor.");
		if (multipolyValidator == null)
			throw new IllegalArgumentException(
					"Multipoly Geometry Validator cannot be null in STComplexPolygonInterpolator constructor.");
		if (simplifyValidator == null)
			throw new IllegalArgumentException(
					"Simplify Geometry Validator cannot be null in STComplexPolygonInterpolator constructor.");

		this.multipolyValidator = multipolyValidator;
		this.simplifyValidator = simplifyValidator;
		this.areaInterpolator = areaInterpolator;
		this.tsAlignFactory = tsAlignFactory;
		this.tsAligner = tsAligner;
	}

	@Override
	public ISTInterpolationTrajectory interpolateTrajectory(ISTInterpolationTrajectory inTrajectory) {
		if (inTrajectory == null)
			throw new IllegalArgumentException(
					"Input Trajectory cannot be null in STComplexPolygonInterpolator interpolateTrajecotry.");

		return super.interpolateTrajectory(inTrajectory);
	}

	@Override
	public List<ISTInterpolationEvent> interpolateBetween(ISTInterpolationEvent first, ISTInterpolationEvent second) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STComplexPolygonInterpolator interpolateBetween.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STComplexPolygonInterpolator interpolateBetween.");

		return super.interpolateBetween(first, second);
	}

	@Override
	public ISTInterpolationEvent interpolateBeforeAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STComplexPolygonInterpolator interpolateBeforeAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STComplexPolygonInterpolator interpolateBeforeAtTime.");
		if (dateTime.isAfter(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STComplexPolygonInterpolator interpolateBeforeAtTime.");

		return super.projectEventBack(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAfterAtTime(ISTInterpolationEvent ev, DateTime dateTime) {
		if (ev == null)
			throw new IllegalArgumentException(
					"Input event cannot be null in STComplexPolygonInterpolator interpolateAfterAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STComplexPolygonInterpolator interpolateAfterAtTime.");
		if (dateTime.isBefore(ev.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be after the datetime of the input eveent in STComplexPolygonInterpolator interpolateAfterAtTime.");

		return super.projectEventForward(ev, dateTime);
	}

	@Override
	public ISTInterpolationEvent interpolateAtTime(ISTInterpolationEvent first, ISTInterpolationEvent second,
			DateTime dateTime) {
		if (first == null)
			throw new IllegalArgumentException(
					"First input event cannot be null in STComplexPolygonInterpolator interpolateAtTime.");
		if (second == null)
			throw new IllegalArgumentException(
					"Second input event cannot be null in STComplexPolygonInterpolator interpolateAtTime.");
		if (dateTime == null)
			throw new IllegalArgumentException(
					"DateTime input cannot be null in STComplexPolygonInterpolator interpolateAtTime.");
		if (dateTime.isBefore(first.getTimePeriod().getStart()) || dateTime.isAfter(second.getTimePeriod().getStart()))
			throw new IllegalArgumentException(
					"DateTime input cannot be outside of the datetime of the input pair in STComplexPolygonInterpolator interpolateAtTime.");

		Geometry firstGeom = first.getGeometry();
		Geometry secondGeom = second.getGeometry();

		// Try to make sure the input geometry objects are both properly dense and
		// valid.
		if (firstGeom.getNumPoints() > 10 && secondGeom.getNumPoints() > 10) {
			firstGeom = this.multipolyValidator.produceValidGeometry(this.densify(firstGeom));
			secondGeom = this.multipolyValidator.produceValidGeometry(this.densify(secondGeom));
		}

		// Generate a set of offset shape series to calculate warping path information
		// on.
		IShapeSeries firstTS = this.convertToTimeseries(firstGeom);
		List<Pair<Integer, IShapeSeries>> offsetTimeSeriesMap = this.createOffsetTimeSeriesMap(secondGeom);

		// Calculate the warping path and cost for each of the offset shape series
		List<Pair<Integer, IAlignmentInfo>> warpsForOffsets = new ArrayList<Pair<Integer, IAlignmentInfo>>();
		for (Pair<Integer, IShapeSeries> series : offsetTimeSeriesMap) {
			IAlignmentInfo warpInfo = this.tsAligner.getWarpInfoBetween(firstTS, series.getSecond());
			if (warpInfo != null)
				warpsForOffsets.add(new Pair<Integer, IAlignmentInfo>(series.getFirst(), warpInfo));
		}

		// start interpolating
		long ts = first.getTimePeriod().getStartMillis();
		long te = second.getTimePeriod().getStartMillis();
		double factor = (double) (dateTime.getMillis() - ts) / (double) (te - ts);

		boolean arealValid = false;
		int K = 0;
		ISTInterpolationEvent result = null;
		// Attempt offsets in order of least cost warping path, until one produces a
		// valid polygon as determined by the area validation method
		do {
			K++;
			// Get the Kth least cost warping path and how much the end geometry is rotated
			// in respect to the original geometry
			Pair<Integer, IAlignmentInfo> offsetWarpInfoPair = this.getKthMostSimilarWarpPathInfo(warpsForOffsets, K);
			int offset = offsetWarpInfoPair.getKey();
			IAlignmentInfo warpInfo = offsetWarpInfoPair.getValue();

			// Use the warping path and rotation information to create the interpolated
			// geometry representation
			Geometry localSecondGeom = this.shiftVerticesByOffset(secondGeom, offset);
			Coordinate[] coords = this.createInterpolatedCoords(firstGeom, localSecondGeom, warpInfo, factor);

			// Check if the new interpolated geometry representation is valid
			arealValid = this.arealValidation(this.gf.createPolygon(coords), firstGeom, secondGeom);

			if (!arealValid) {
				// If not valid, check if we have reached the limited number of tries and break
				// if we have, else, we try again.
				if (K > warpsForOffsets.size() / 3) {
					break;
				}
			} else {
				// If valid, then we return the new event at the specified time
				result = this.createInterpolatedSTEvent(dateTime,
						new DateTime(dateTime.getMillis() + this.step.getMillis()), first.getType(), coords);
			}
		} while (!arealValid);

		// If we didn't get any results using the alignment method, then we will use a
		// simpler interpolation method that should return a value.
		if (result == null)
			result = this.areaInterpolator.interpolateAtTime(first, second, new DateTime(dateTime));

		return result;
	}

	//////////////////////////// Private Methods\\\\\\\\\\\\\\\\\\\\\\\\\
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
		List<ISTInterpolationEvent> result = new ArrayList<ISTInterpolationEvent>();

		Geometry firstGeom = first.getGeometry();
		Geometry secondGeom = second.getGeometry();

		// Try to make sure the input geometry objects are both properly dense and
		// valid.
		if (firstGeom.getNumPoints() > 10 && secondGeom.getNumPoints() > 10) {
			firstGeom = this.multipolyValidator.produceValidGeometry(this.densify(firstGeom));
			secondGeom = this.multipolyValidator.produceValidGeometry(this.densify(secondGeom));
		}

		// Generate a set of offset shape series to calculate warping path information
		// on.
		IShapeSeries firstTS = this.convertToTimeseries(firstGeom);
		List<Pair<Integer, IShapeSeries>> offsetTimeSeriesMap = this.createOffsetTimeSeriesMap(secondGeom);

		// Calculate the warping path and cost for each of the offset shape series
		List<Pair<Integer, IAlignmentInfo>> warpsForOffsets = new ArrayList<Pair<Integer, IAlignmentInfo>>();
		for (Pair<Integer, IShapeSeries> series : offsetTimeSeriesMap) {
			IAlignmentInfo warpInfo = this.tsAligner.getWarpInfoBetween(firstTS, series.getSecond());
			if (warpInfo != null)
				warpsForOffsets.add(new Pair<Integer, IAlignmentInfo>(series.getFirst(), warpInfo));
		}

		// start interpolating
		long timeStart = first.getTimePeriod().getStartMillis();
		long timeEnd = second.getTimePeriod().getStartMillis();
		double range = timeEnd - timeStart;
		boolean arealValid = false;
		int K = 0;
		// Attempt offsets in order of least cost warping path, until one produces a
		// valid set of polygons as determined by the area validation method
		do {
			K++;
			// Get the Kth least cost warping path and how much the end geometry is rotated
			// in respect to the original geometry
			Pair<Integer, IAlignmentInfo> offsetWarpInfoPair = this.getKthMostSimilarWarpPathInfo(warpsForOffsets, K);
			int offset = offsetWarpInfoPair.getKey();
			IAlignmentInfo warpInfo = offsetWarpInfoPair.getValue();

			Geometry localSecondGeom = this.shiftVerticesByOffset(secondGeom, offset);

			boolean localArealValid = true;
			List<Pair<DateTime, Coordinate[]>> tempResult = new ArrayList<Pair<DateTime, Coordinate[]>>();
			// Use the warping path and rotation information to create the set of
			// interpolated geometry representations
			for (long timeInterp = timeStart + this.step.getMillis(); timeInterp < timeEnd; timeInterp += this.step
					.getMillis()) {
				double factor = (double) (timeInterp - timeStart) / range;
				Coordinate[] coords = this.createInterpolatedCoords(firstGeom, localSecondGeom, warpInfo, factor);

				// Check if the new interpolated geometry representation is valid
				localArealValid = localArealValid
						&& this.arealValidation(this.gf.createPolygon(coords), firstGeom, secondGeom);
				// If we got one that is invalid, we will move to the next rotation and warping
				// path
				if (!localArealValid) {
					break;
				} else {
					// If is valid then we add to the set and move on to the next time step
					Pair<DateTime, Coordinate[]> p = new Pair<DateTime, Coordinate[]>(new DateTime(timeInterp), coords);
					tempResult.add(p);
				}
			}
			arealValid = localArealValid;

			if (!arealValid) {
				// If one was not valid then we check to see if we have tried the max number of
				// tries and move on to using a different method if we have.
				if (K >= warpsForOffsets.size() / 3) {
					break;
				}
			} else {
				// If all were valid, then we produce new event reports for each one and add
				// them to the result set.
				for (Pair<DateTime, Coordinate[]> p : tempResult) {
					DateTime dateTime = p.getFirst();
					Coordinate[] coords = p.getSecond();
					result.add(this.createInterpolatedSTEvent(dateTime,
							new DateTime(dateTime.getMillis() + this.step.getMillis()), first.getType(), coords));

				}
			}
		} while (!arealValid);

		// If we didn't produce any using the alignment method, we will attempt to do so
		// with a simpler interpolation method that should return a value.
		if (result.isEmpty()) {
			for (long timeInterp = timeStart + this.step.getMillis(); timeInterp < timeEnd; timeInterp += this.step
					.getMillis()) {
				result.add(this.areaInterpolator.interpolateAtTime(first, second, new DateTime(timeInterp)));
			}
		}
		return result;
	}

	private Pair<Integer, IAlignmentInfo> getKthMostSimilarWarpPathInfo(
			List<Pair<Integer, IAlignmentInfo>> warpsForOffsets, int k) {

		TreeMap<Double, Integer> scoreOffsetMap = new TreeMap<Double, Integer>();
		TreeMap<Integer, IAlignmentInfo> offsetWarpInfoMap = new TreeMap<Integer, IAlignmentInfo>();

		for (int i = 0; i < warpsForOffsets.size(); i++) {
			Pair<Integer, IAlignmentInfo> p = warpsForOffsets.get(i);
			IAlignmentInfo warpInfo = p.getSecond();

			double tempScore = warpInfo.getDistance();
			scoreOffsetMap.put(tempScore, Integer.valueOf(p.getFirst()));
			offsetWarpInfoMap.put(Integer.valueOf(p.getFirst()), warpInfo);
		}

		ArrayList<Double> scoreList = new ArrayList<Double>(scoreOffsetMap.keySet());

		// get Kth smallest score (k starts from 1, so decrease 1)
		double kth_score = scoreList.get((k - 1) % scoreList.size());

		int kth_offset = scoreOffsetMap.get(kth_score);
		IAlignmentInfo kth_warpInfo = offsetWarpInfoMap.get(kth_offset);
		Pair<Integer, IAlignmentInfo> distWarpInfoEntry = new Pair<Integer, IAlignmentInfo>(kth_offset, kth_warpInfo);

		return distWarpInfoEntry;
	}

	private boolean arealValidation(Geometry interpolated, Geometry firstGeom, Geometry secondGeom) {

		double initialArea = firstGeom.getArea();
		double lastArea = secondGeom.getArea();

		double minArea = Math.min(initialArea, lastArea);
		minArea *= (1.0 - this.acceptedError);

		double iArea = interpolated.getArea();

		double ratio = iArea / minArea;
		if (ratio < (1.0 - this.acceptedError)) {
			return false;
		}

		return true;
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

	private Coordinate[] createInterpolatedCoords(Geometry densifiedGeom, Geometry densifiedNextGeom,
			IAlignmentInfo warpInfo, double factor) {
		IAlignmentPath path = warpInfo.getPath();
		Coordinate[] i_coordinates = new Coordinate[path.size() + 1];
		Coordinate[] cI_geom = densifiedGeom.getCoordinates();
		Coordinate[] cJ_geom = densifiedNextGeom.getCoordinates();

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

	private List<Pair<Integer, IShapeSeries>> createOffsetTimeSeriesMap(Geometry densifiedNextGeom) {
		List<Pair<Integer, IShapeSeries>> offsetTimeSeriesMap = new ArrayList<Pair<Integer, IShapeSeries>>();
		int coordinateCount = densifiedNextGeom.getNumPoints();
		for (int i = 0; i < this.timeSeriesSimilaritySteps; i++) {
			int offset = (int) (i * ((double) coordinateCount / (double) this.timeSeriesSimilaritySteps));
			IShapeSeries ts = this.convertToTimeseriesByOffset(
					this.multipolyValidator.produceValidGeometry(densifiedNextGeom), offset);
			if (ts != null) {
				offsetTimeSeriesMap.add(new Pair<Integer, IShapeSeries>(Integer.valueOf(offset), ts));
			}
		}
		return offsetTimeSeriesMap;
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

	private IShapeSeries convertToTimeseries(Geometry geom) {
		if (geom == null) {
			return null;
		} else if (!(geom instanceof Polygon)) {
			geom = this.multipolyValidator.produceValidGeometry(geom);
			return this.convertToTimeseries(geom);
		} else {
			// 1-dimensional time series
			IShapeSeries TSRepresentation = this.tsAlignFactory.getTimeSeries(1);
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

	private IShapeSeries convertToTimeseriesByOffset(Geometry geom, int offset) {

		if (geom == null) {
			return null;
		} else if (!(geom instanceof Polygon)) {
			geom = this.multipolyValidator.produceValidGeometry(geom);
			return this.convertToTimeseriesByOffset(geom, offset);
		} else {
			// 1-dimensional shape series
			IShapeSeries TSRepresentation = this.tsAlignFactory.getTimeSeries(1);
			Coordinate centroid = geom.getCentroid().getCoordinate();
			Coordinate[] coordinates = geom.getCoordinates();
			for (int i = offset; i < coordinates.length + offset; i++) {
				Coordinate c = coordinates[i % coordinates.length];
				final double distance = c.distance(centroid);
				TSRepresentation.addLast((int) i, this.tsAlignFactory.getTSPoint(new double[] { distance }));
			}

			return TSRepresentation;
		}
	}

	private Geometry densify(Geometry geom) {
		double length = geom.getLength();
		double unitLength = length / this.densifierPointBound;
		Densifier densifier = new Densifier(geom);
		if (unitLength > 0) {
			densifier.setDistanceTolerance(unitLength);
		} else {
			// Set a minimum of at least 1
			densifier.setDistanceTolerance(this.simplifierDistance);
		}
		return densifier.getResultGeometry();
	}

}
