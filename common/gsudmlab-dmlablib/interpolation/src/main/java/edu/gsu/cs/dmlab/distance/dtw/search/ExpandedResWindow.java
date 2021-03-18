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
package edu.gsu.cs.dmlab.distance.dtw.search;

import java.util.Iterator;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IAggregateShapeSeries;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;

/**
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ExpandedResWindow extends SearchWindow {

	/**
	 * 
	 * @param tsI
	 * @param tsJ
	 * @param shrunkI
	 * @param shrunkJ
	 * @param shrunkWarpPath
	 * @param searchRadius
	 */
	public ExpandedResWindow(IShapeSeries tsI, IShapeSeries tsJ, IAggregateShapeSeries shrunkI,
			IAggregateShapeSeries shrunkJ, IAlignmentPath shrunkWarpPath, int searchRadius) {
		super(tsI.size(), tsJ.size());
		int currentI = shrunkWarpPath.minI();
		int currentJ = shrunkWarpPath.minJ();
		int lastWarpedI = 0x7fffffff;
		int lastWarpedJ = 0x7fffffff;

		Iterator<ColMajorCell> pathIter = shrunkWarpPath.getMapping();
		while (pathIter.hasNext()) {
			ColMajorCell currentCell = pathIter.next();
			int warpedI = currentCell.getCol();
			int warpedJ = currentCell.getRow();
			int blockISize = shrunkI.aggregatePtSize(warpedI);
			int blockJSize = shrunkJ.aggregatePtSize(warpedJ);
			if (warpedJ > lastWarpedJ)
				currentJ += shrunkJ.aggregatePtSize(lastWarpedJ);
			if (warpedI > lastWarpedI)
				currentI += shrunkI.aggregatePtSize(lastWarpedI);
			if (warpedJ > lastWarpedJ && warpedI > lastWarpedI) {
				super.markVisited(currentI - 1, currentJ);
				super.markVisited(currentI, currentJ - 1);
			}
			for (int x = 0; x < blockISize; x++) {
				super.markVisited(currentI + x, currentJ);
				super.markVisited(currentI + x, (currentJ + blockJSize) - 1);
			}

			lastWarpedI = warpedI;
			lastWarpedJ = warpedJ;
		}

		super.expandWindow(searchRadius);
	}

}