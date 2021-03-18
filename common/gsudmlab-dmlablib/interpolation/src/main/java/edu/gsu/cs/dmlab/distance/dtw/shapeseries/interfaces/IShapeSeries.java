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
package edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces;

/**
 * Interface for a multidimensional time series object.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IShapeSeries {

	/**
	 * Method to get the number of points in the time series
	 * 
	 * @return Number of points in the series
	 */
	public int size();

	/**
	 * Method to get the number of dimensions the points in this series have
	 * 
	 * @return The number of dimensions for points in this series
	 */
	public int numOfDimensions();

	/**
	 * Method to get the time component for the Nth point in the series
	 * 
	 * @param n The position in the series to get the time component of
	 * 
	 * @return The time component value at position n
	 */
	public double getTimeAtNthPoint(int n);

	/**
	 * Method to get the point value vector for the Nth point in the series
	 * 
	 * @param n The position in the series to get the point value vector of
	 * 
	 * @return The point value vector at position n
	 */
	public double[] getMeasurementVectorAtNthPoint(int n);

	/**
	 * Adds a new point at the beginning of the time series. The time component must
	 * be before the time component of the first item in this series. The values
	 * component must also have the correct number of dimensions for this series.
	 * 
	 * @param time   The time component of the point to insert into the series
	 * 
	 * @param values The point to insert into the series
	 */
	public void addFirst(double time, ISeriesPoint values);

	/**
	 * Adds a new point at the end of the time series. The time component must be
	 * after the time component of the last item in this series. The values
	 * component must also have the correct number of dimensions for this series.
	 * 
	 * @param time
	 * @param values
	 */
	public void addLast(double time, ISeriesPoint values);
}
