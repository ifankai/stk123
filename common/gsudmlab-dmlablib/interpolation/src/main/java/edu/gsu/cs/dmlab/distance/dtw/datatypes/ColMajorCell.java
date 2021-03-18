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

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ColMajorCell {

	private final int col;
	private final int row;

	public int getCol() {
		return this.col;
	}

	public int getRow() {
		return this.row;
	}

	public ColMajorCell(int column, int row) {
		this.col = column;
		this.row = row;
	}

}