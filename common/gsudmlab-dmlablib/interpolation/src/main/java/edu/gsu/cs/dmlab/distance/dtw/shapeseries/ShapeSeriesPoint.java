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

import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.ISeriesPoint;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ShapeSeriesPoint implements ISeriesPoint {

	private double measurements[];

	public ShapeSeriesPoint(double values[]) {

		this.measurements = new double[values.length];
		for (int x = 0; x < values.length; x++) {
			this.measurements[x] = values[x];
		}

	}

	@Override
	public int numDims() {
		return this.measurements.length;
	}

	@Override
	public double getDimValue(int dimension) {
		return this.measurements[dimension];
	}

	@Override
	public void setDimValue(int dimension, double newValue) {
		this.measurements[dimension] = newValue;
	}

	@Override
	public double[] toArray() {
		return this.measurements;
	}
}