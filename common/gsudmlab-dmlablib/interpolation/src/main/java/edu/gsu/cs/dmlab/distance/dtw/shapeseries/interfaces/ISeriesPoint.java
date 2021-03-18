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
 * Interface for a simple point object used in a multidimensional time series.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISeriesPoint {

	/**
	 * Method gets the number of dimensions in the point
	 * 
	 * @return The number of dimensions for this point
	 */
	public int numDims();

	/**
	 * Method to get the value of a specific dimension of this point
	 * 
	 * @param dimension The dimension to return the value of
	 * 
	 * @return The value of the dimension that was passed in
	 */
	public double getDimValue(int dimension);

	/**
	 * Method to set the value of a specific dimension for this point
	 * 
	 * @param dimension The dimension to set the value of
	 * 
	 * @param newValue  The value to set the dimension of this point to
	 */
	public void setDimValue(int dimension, double newValue);

	/**
	 * Method returns an array of values that represent the value taken by this
	 * point on each dimension it is a member of.
	 * 
	 * @return An array of values this point takes on each dimension
	 */
	public double[] toArray();

}
