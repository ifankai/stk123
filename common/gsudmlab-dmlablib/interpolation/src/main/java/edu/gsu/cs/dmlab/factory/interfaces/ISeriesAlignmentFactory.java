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
package edu.gsu.cs.dmlab.factory.interfaces;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentInfo;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.ICostMatrix;
import edu.gsu.cs.dmlab.distance.dtw.interfaces.IShapeSeriesAligner;
import edu.gsu.cs.dmlab.distance.dtw.search.interfaces.ISearchWindow;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IAggregateShapeSeries;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.ISeriesPoint;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;

/**
 * The public interface for classes that will be used to create objects used for
 * finding the alignment of shape series.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISeriesAlignmentFactory {

	/**
	 * Method gets a shape aligner object used to find the least cost alignment of
	 * points in a series that is treated as though it was a timeseries.
	 * 
	 * @return A shape series alignment object.
	 */
	public IShapeSeriesAligner getAligner();

	/**
	 * Method gets a cost matrix for a search window
	 * 
	 * @param window The window to get a cost matrix for.
	 * 
	 * @return A cost matrix for the input search window
	 */
	public ICostMatrix getCostMatrix(ISearchWindow window);

	/**
	 * Method gets an alignment path object of a set size
	 * 
	 * @param size The size of of the path to return
	 * 
	 * @return
	 */
	public IAlignmentPath getAlignmentPath(int size);

	public IAlignmentInfo getAlignmentInfo(double distance, IAlignmentPath path);

	public ISeriesPoint getTSPoint(double[] values);

	public IShapeSeries getTimeSeries(int nDims);

	public IAggregateShapeSeries getReducedTimeSeries(IShapeSeries series, int reducedSize);

	public ISearchWindow getExpandedSearchWindow(IShapeSeries tsI, IShapeSeries tsJ, IAggregateShapeSeries shrunkI,
			IAggregateShapeSeries shrunkJ, IAlignmentPath shrunkWarpPath);
}
