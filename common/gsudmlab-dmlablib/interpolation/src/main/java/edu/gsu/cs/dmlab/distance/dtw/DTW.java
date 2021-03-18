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

import java.util.Iterator;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentInfo;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.ICostMatrix;
import edu.gsu.cs.dmlab.distance.dtw.interfaces.IShapeSeriesAligner;
import edu.gsu.cs.dmlab.distance.dtw.search.interfaces.ISearchWindow;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;
import edu.gsu.cs.dmlab.factory.interfaces.ISeriesAlignmentFactory;

/**
 * An implementation of the dynamic time warping algorithm
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class DTW implements IShapeSeriesAligner {

	ISeriesAlignmentFactory factory;

	/**
	 * Constructor
	 * 
	 * @param factory Factory object used to produce objects used and returned from
	 *                this object.
	 */
	public DTW(ISeriesAlignmentFactory factory) {
		if (factory == null)
			throw new IllegalArgumentException("IAlignmentFactory cannot be null in DTW class");
		this.factory = factory;
	}

	@Override
	public IAlignmentInfo getWarpInfoBetween(IShapeSeries tsI, IShapeSeries tsJ) {
		return this.dynamicTimeWarp(tsI, tsJ);
	}

	////////////////// Protected Methods\\\\\\\\\\\\\
	protected IAlignmentInfo getWarpInfoBetween(IShapeSeries tsI, IShapeSeries tsJ, ISearchWindow window) {
		return this.constrainedTimeWarp(tsI, tsJ, window);
	}
	///////////////// Private Methods\\\\\\\\\\\\\\\\\

	private IAlignmentInfo dynamicTimeWarp(IShapeSeries tsI, IShapeSeries tsJ) {
		double costMatrix[][] = new double[tsI.size()][tsJ.size()];
		int maxI = tsI.size() - 1;
		int maxJ = tsJ.size() - 1;
		costMatrix[0][0] = this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(0),
				tsJ.getMeasurementVectorAtNthPoint(0));
		for (int j = 1; j <= maxJ; j++)
			costMatrix[0][j] = costMatrix[0][j - 1]
					+ this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(0), tsJ.getMeasurementVectorAtNthPoint(j));

		for (int i = 1; i <= maxI; i++) {
			costMatrix[i][0] = costMatrix[i - 1][0]
					+ this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(i), tsJ.getMeasurementVectorAtNthPoint(0));
			for (int j = 1; j <= maxJ; j++) {
				double minGlobalCost = Math.min(costMatrix[i - 1][j],
						Math.min(costMatrix[i - 1][j - 1], costMatrix[i][j - 1]));
				costMatrix[i][j] = minGlobalCost + this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(i),
						tsJ.getMeasurementVectorAtNthPoint(j));
			}

		}

		double minimumCost = costMatrix[maxI][maxJ];
		IAlignmentPath minCostPath = this.factory.getAlignmentPath((maxI + maxJ) - 1);
		int i = maxI;
		int j = maxJ;
		minCostPath.addFirst(i, j);
		for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
			double diagCost;
			if (i > 0 && j > 0)
				diagCost = costMatrix[i - 1][j - 1];
			else
				diagCost = Double.POSITIVE_INFINITY;
			double leftCost;
			if (i > 0)
				leftCost = costMatrix[i - 1][j];
			else
				leftCost = Double.POSITIVE_INFINITY;
			double downCost;
			if (j > 0)
				downCost = costMatrix[i][j - 1];
			else
				downCost = Double.POSITIVE_INFINITY;
			if (diagCost <= leftCost && diagCost <= downCost) {
				i--;
				j--;
				continue;
			}
			if (leftCost < diagCost && leftCost < downCost) {
				i--;
				continue;
			}
			if (downCost < diagCost && downCost < leftCost) {
				j--;
				continue;
			}
			if (i <= j)
				j--;
			else
				i--;
		}

		return this.factory.getAlignmentInfo(minimumCost, minCostPath);
	}

	private IAlignmentInfo constrainedTimeWarp(IShapeSeries tsI, IShapeSeries tsJ, ISearchWindow window) {
		ICostMatrix costMatrix = this.factory.getCostMatrix(window);
		int maxI = tsI.size() - 1;
		int maxJ = tsJ.size() - 1;
		for (@SuppressWarnings("rawtypes")
		Iterator matrixIterator = window.iterator(); matrixIterator.hasNext();) {
			ColMajorCell currentCell = (ColMajorCell) matrixIterator.next();
			int i = currentCell.getCol();
			int j = currentCell.getRow();
			if (i == 0 && j == 0)
				costMatrix.put(i, j, this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(0),
						tsJ.getMeasurementVectorAtNthPoint(0)));
			else if (i == 0)
				costMatrix.put(i, j,
						this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(0), tsJ.getMeasurementVectorAtNthPoint(j))
								+ costMatrix.get(i, j - 1));
			else if (j == 0) {
				costMatrix.put(i, j,
						this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(i), tsJ.getMeasurementVectorAtNthPoint(0))
								+ costMatrix.get(i - 1, j));
			} else {
				double minGlobalCost = Math.min(costMatrix.get(i - 1, j),
						Math.min(costMatrix.get(i - 1, j - 1), costMatrix.get(i, j - 1)));
				costMatrix.put(i, j, minGlobalCost + this.euclideanDist(tsI.getMeasurementVectorAtNthPoint(i),
						tsJ.getMeasurementVectorAtNthPoint(j)));
			}
		}

		double minimumCost = costMatrix.get(maxI, maxJ);
		IAlignmentPath minCostPath = this.factory.getAlignmentPath((maxI + maxJ) - 1);
		int i = maxI;
		int j = maxJ;
		minCostPath.addFirst(i, j);
		for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
			double diagCost;
			if (i > 0 && j > 0)
				diagCost = costMatrix.get(i - 1, j - 1);
			else
				diagCost = Double.POSITIVE_INFINITY;
			double leftCost;
			if (i > 0)
				leftCost = costMatrix.get(i - 1, j);
			else
				leftCost = Double.POSITIVE_INFINITY;
			double downCost;
			if (j > 0)
				downCost = costMatrix.get(i, j - 1);
			else
				downCost = Double.POSITIVE_INFINITY;
			if (diagCost <= leftCost && diagCost <= downCost) {
				i--;
				j--;
				continue;
			}
			if (leftCost < diagCost && leftCost < downCost) {
				i--;
				continue;
			}
			if (downCost < diagCost && downCost < leftCost) {
				j--;
				continue;
			}
			if (i <= j)
				j--;
			else
				i--;
		}

		return this.factory.getAlignmentInfo(minimumCost, minCostPath);
	}

	private double euclideanDist(double vector1[], double vector2[]) {
		if (vector1.length != vector2.length)
			throw new InternalError("ERROR:  cannot calculate the distance between vectors of different sizes.");
		double sqSum = 0.0D;
		for (int x = 0; x < vector1.length; x++) {
			double diff = vector1[x] - vector2[x];
			sqSum += diff * diff;
		}
		return Math.sqrt(sqSum);
	}
}