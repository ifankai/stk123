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
package edu.gsu.cs.dmlab.distance.dtw.datatypes;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.ICostMatrix;
import edu.gsu.cs.dmlab.distance.dtw.search.interfaces.ISearchWindow;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class WindowMatrix implements ICostMatrix {

	private final ISearchWindow window;
	private double cellValues[];
	private int colOffsets[];

	public WindowMatrix(ISearchWindow searchWindow) {
		this.window = searchWindow;
		this.cellValues = new double[this.window.size()];
		this.colOffsets = new int[this.window.maxI() + 1];
		int currentOffset = 0;
		for (int i = this.window.minI(); i <= this.window.maxI(); i++) {
			colOffsets[i] = currentOffset;
			currentOffset += (this.window.maxJforI(i) - this.window.minJforI(i)) + 1;
		}
	}

	@Override
	public void put(int col, int row, double value) {
		if (row < this.window.minJforI(col) || row > this.window.maxJforI(col)) {
			throw new InternalError("CostMatrix is filled in a cell (col=" + col + ", row=" + row
					+ ") that is not in the " + "search window");
		} else {
			this.cellValues[(this.colOffsets[col] + row) - this.window.minJforI(col)] = value;
			return;
		}
	}

	@Override
	public double get(int col, int row) {
		if (row < this.window.minJforI(col) || row > window.maxJforI(col))
			return (1.0D / 0.0D);
		else
			return this.cellValues[(this.colOffsets[col] + row) - this.window.minJforI(col)];
	}

	@Override
	public int size() {
		return this.cellValues.length;
	}

}