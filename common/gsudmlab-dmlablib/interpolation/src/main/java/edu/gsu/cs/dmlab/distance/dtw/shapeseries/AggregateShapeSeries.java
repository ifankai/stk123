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
 * 
 * This code is based on code in the javaml library https://github.com/AbeelLab/javaml
 */
package edu.gsu.cs.dmlab.distance.dtw.shapeseries;

import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IAggregateShapeSeries;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;
import edu.gsu.cs.dmlab.factory.interfaces.ISeriesAlignmentFactory;

/**
 * Shape series class that shrinks the length and resolution of a series by
 * averaging consecutive point ranges into a single representative point. The
 * number of points averaged from a consecutive range is dependent upon the
 * length of the input shape series and the desired size after being resized.
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class AggregateShapeSeries extends ShapeSeries implements IAggregateShapeSeries {

	private ISeriesAlignmentFactory factory;
	private int aggPtSize[];

	/**
	 * Constructor
	 * 
	 * @param factory    Factory object used for construction of various objects
	 *                   used and returned by this object
	 * 
	 * @param ts         The input shape series to be represented by this reduced
	 *                   resolution series
	 * 
	 * @param shrunkSize The size of this series after reduction of the input series
	 */
	public AggregateShapeSeries(ISeriesAlignmentFactory factory, IShapeSeries ts, int shrunkSize) {
		super();
		if (factory == null)
			throw new IllegalArgumentException("IAlignmentFactory cannot be null in AggregateShpaeSeries class");
		if (ts == null)
			throw new IllegalArgumentException("IShapeSeries cannot be null in AggregateShpaeSeries class");
		if (shrunkSize > ts.size())
			throw new InternalError(
					"ERROR:  The size of an aggregate representation may not be largerr than the \noriginal time series (shrunkSize="
							+ shrunkSize + " , origSize=" + ts.size() + ").");
		if (shrunkSize <= 0)
			throw new InternalError(
					"ERROR:  The size of an aggregate representation must be greater than zero and \nno larger than the original time series.");
		this.factory = factory;
		this.aggPtSize = new int[shrunkSize];
		super.setMaxCapacity(shrunkSize);

		// Calculate the number of points each aggregate will need to represent if the
		// desired size is to be reached.
		double reducedPtSize = (double) ts.size() / (double) shrunkSize;

		int ptToReadTo;
		for (int ptToReadFrom = 0; ptToReadFrom < ts.size(); ptToReadFrom = ptToReadTo + 1) {
			// Calculate the index of the end point for the aggregation operation
			ptToReadTo = (int) Math.round(reducedPtSize * (double) (this.size() + 1)) - 1;

			// Calculate the number of points between the end point and the current position
			int ptsToRead = (ptToReadTo - ptToReadFrom) + 1;

			// Perform the aggregation operation over the points calculated above.
			double timeSum = 0.0D;
			double measurementSums[] = new double[ts.numOfDimensions()];
			for (int pt = ptToReadFrom; pt <= ptToReadTo; pt++) {
				double currentPoint[] = ts.getMeasurementVectorAtNthPoint(pt);
				timeSum += ts.getTimeAtNthPoint(pt);
				for (int dim = 0; dim < ts.numOfDimensions(); dim++)
					measurementSums[dim] += currentPoint[dim];

			}

			// Average the aggregation operation for the points processed
			timeSum /= ptsToRead;
			for (int dim = 0; dim < ts.numOfDimensions(); dim++)
				measurementSums[dim] = measurementSums[dim] / (double) ptsToRead;

			// Store the number of points aggregated for this point in the reduced time
			// series
			this.aggPtSize[this.size()] = ptsToRead;
			// Add the new aggregated and averaged point to this series.
			this.addLast(timeSum, this.factory.getTSPoint(measurementSums));
		}

	}

	@Override
	public int aggregatePtSize(int idx) {
		return this.aggPtSize[idx];
	}

}