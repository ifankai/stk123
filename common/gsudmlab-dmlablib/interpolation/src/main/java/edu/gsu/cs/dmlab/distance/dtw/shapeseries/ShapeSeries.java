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

import java.util.ArrayList;

import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.ISeriesPoint;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;

/**
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ShapeSeries implements IShapeSeries {

	private final ArrayList<String> labels;
	private final ArrayList<Double> timeReadings;
	private final ArrayList<ISeriesPoint> tsArray;

	ShapeSeries() {
		this.labels = new ArrayList<String>();
		this.timeReadings = new ArrayList<>();
		this.tsArray = new ArrayList<>();
	}

	public ShapeSeries(int numOfDimensions) {
		this();
		this.labels.add("Time");
		for (int x = 0; x < numOfDimensions; x++)
			this.labels.add("" + x);

	}

	public ShapeSeries(ShapeSeries origTS) {
		this.labels = new ArrayList<String>(origTS.labels);
		this.timeReadings = new ArrayList<>(origTS.timeReadings);
		this.tsArray = new ArrayList<>(origTS.tsArray);
	}

	@Override
	public int size() {
		return this.timeReadings.size();
	}

	@Override
	public int numOfDimensions() {
		return this.labels.size() - 1;
	}

	@Override
	public double getTimeAtNthPoint(int n) {
		return ((Double) this.timeReadings.get(n)).doubleValue();
	}

	@Override
	public double[] getMeasurementVectorAtNthPoint(int n) {
		return this.tsArray.get(n).toArray();
	}

	@Override
	public void addFirst(double time, ISeriesPoint values) {
		if (labels.size() != values.numDims() + 1)
			throw new InternalError("ERROR:  The TimeSeriesPoint: " + values + " contains the wrong number of values. "
					+ "expected:  " + labels.size() + ", " + "found: " + (values.numDims() + 1));
		if (time >= ((Double) this.timeReadings.get(0)).doubleValue()) {
			throw new InternalError(
					"ERROR:  The point being inserted into the beginning of the time series does not have the correct time sequence. ");
		} else {
			this.timeReadings.add(0, Double.valueOf(time));
			this.tsArray.add(0, values);
			return;
		}
	}

	@Override
	public void addLast(double time, ISeriesPoint values) {
		if (labels.size() != values.numDims() + 1)
			throw new InternalError("ERROR:  The TimeSeriesPoint: " + values + " contains the wrong number of values. "
					+ "expected:  " + labels.size() + ", " + "found: " + values.numDims());
		if (size() > 0 && time <= ((Double) this.timeReadings.get(this.timeReadings.size() - 1)).doubleValue()) {
			throw new InternalError(
					"ERROR:  The point being inserted at the end of the time series does not have the correct time sequence. ");
		} else {
			this.timeReadings.add(Double.valueOf(time));
			this.tsArray.add(values);
			return;
		}
	}

	protected void setMaxCapacity(int capacity) {
		this.timeReadings.ensureCapacity(capacity);
		this.tsArray.ensureCapacity(capacity);
	}

}