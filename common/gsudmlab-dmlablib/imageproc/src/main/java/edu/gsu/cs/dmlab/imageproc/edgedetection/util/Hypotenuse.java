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
/**************************************************************************
 * @author Jason Altschuler
 *
 * Calculate hypotenuse of triangle given length of other two sides. 
 ************************************************************************/

package edu.gsu.cs.dmlab.imageproc.edgedetection.util;

/**
 * 
 * @author Jason Altschuler, modified by Azim Ahmadzadeh of Data Mining Lab,
 *         Georgia State University
 * 
 */
public class Hypotenuse {

	/**
	 * |Hypotenuse| = |x| + |y|
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static double L1(double x, double y) {
		return Math.abs(x) + Math.abs(y);
	}

	/**
	 * |Hypotenuse| = sqrt(x^2 + y^2)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static double L2(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
}
