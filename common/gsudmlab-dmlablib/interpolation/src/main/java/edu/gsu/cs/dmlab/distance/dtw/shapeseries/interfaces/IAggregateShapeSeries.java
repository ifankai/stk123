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
 * Interface for a aggregate reduction multidimensional time series object.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IAggregateShapeSeries extends IShapeSeries {

	/**
	 * Method that returns the number of points that were used to produce the
	 * aggregate point at the indicated index of the time series.
	 * 
	 * @param idx The index of the point to get the information for
	 * 
	 * @return The number of points in the original time series that were used to
	 *         produce the aggregate point
	 */
	public int aggregatePtSize(int idx);
}
