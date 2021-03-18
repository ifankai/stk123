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
package edu.gsu.cs.dmlab.distance.dtw;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentInfo;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;
import edu.gsu.cs.dmlab.distance.dtw.search.interfaces.ISearchWindow;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IAggregateShapeSeries;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;
import edu.gsu.cs.dmlab.factory.interfaces.ISeriesAlignmentFactory;

/**
 * An implementation of a fast dynamic time warping algorithm
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class FastDTW extends DTW {

	private ISeriesAlignmentFactory factory;
	private int searchRadius;

	/**
	 * Constructor
	 * 
	 * @param factory      Factory object used to produce objects used and returned
	 *                     from this object.
	 * 
	 * @param searchRadius Used to set a minimum length of a reduced resolution
	 *                     shape series object used in the calculation of the DTW
	 *                     calculation
	 */
	public FastDTW(ISeriesAlignmentFactory factory, int searchRadius) {
		super(factory);
		if (factory == null)
			throw new IllegalArgumentException("IAlignmentFactory cannot be null in FastDTW class");
		this.searchRadius = searchRadius;
		this.factory = factory;
	}

	@Override
	public IAlignmentInfo getWarpInfoBetween(IShapeSeries tsI, IShapeSeries tsJ) {
		return this.fastDTW(tsI, tsJ, this.searchRadius);
	}

	////////////// Private Methods\\\\\\\\\\\\\\\\\\\\
	private IAlignmentPath getWarpPathBetween(IShapeSeries tsI, IShapeSeries tsJ, int searchRadius) {
		return this.fastDTW(tsI, tsJ, searchRadius).getPath();
	}

	private IAlignmentInfo fastDTW(IShapeSeries tsI, IShapeSeries tsJ, int searchRadius) {
		if (searchRadius < 0)
			searchRadius = 0;
		int minTSsize = searchRadius + 2;
		if (tsI.size() <= minTSsize || tsJ.size() <= minTSsize) {
			return super.getWarpInfoBetween(tsI, tsJ);
		} else {
			IAggregateShapeSeries shrunkI = this.factory.getReducedTimeSeries(tsI, (int) ((double) tsI.size() / 2D));
			IAggregateShapeSeries shrunkJ = this.factory.getReducedTimeSeries(tsJ, (int) ((double) tsJ.size() / 2D));
			IAlignmentPath path = this.getWarpPathBetween(shrunkI, shrunkJ, searchRadius);
			ISearchWindow window = this.factory.getExpandedSearchWindow(tsI, tsJ, shrunkI, shrunkJ, path);
			return super.getWarpInfoBetween(tsI, tsJ, window);
		}
	}
}