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

import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentInfo;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SeriesWarpInfo implements IAlignmentInfo {

	private final double distance;
	private final IAlignmentPath path;

	/**
	 * Constructor
	 * 
	 * @param dist The sum of the distance between each of the point mappings in the
	 *             path
	 * 
	 * @param wp   The point mappings between two input point series
	 */
	public SeriesWarpInfo(double dist, IAlignmentPath wp) {
		if (wp == null)
			throw new IllegalArgumentException("IAlignmentPath cannot be null in warp info.");

		this.distance = dist;
		this.path = wp;
	}

	public double getDistance() {
		return this.distance;
	}

	public IAlignmentPath getPath() {
		return this.path;
	}

}