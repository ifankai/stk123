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
package edu.gsu.cs.dmlab.datatypes;

/**
 * A classes for holding the gradient values of an image in the x and y
 * direction.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class Gradient {

	/**
	 * In case of Gradient in the Cartesian system, this is the matrix of gradient
	 * values when comparing pixels in the X direction. In case of Gradient in the
	 * Polar system, this is the matrix of angles.
	 */
	public double[][] gx;

	public double[] gx_f;
	/**
	 * In case of Gradient in the Cartesian system, this is the matrix of gradient
	 * values when comparing pixels in the Y direction. In case of Gradient in the
	 * Polar system, this is the matrix of Radii.
	 */
	public double[][] gy;

	public double[] gy_f;
	/**
	 * This is the same for both Cartesian and Polar system. This is an auxiliary
	 * matrix to help distinguish the zero values in the Polar system. Gradient in
	 * the Polar system gets zero in the following cases and without 'gd' there is
	 * no way to distinguish them: 1. gx[i][j] = 0, gy[i][j] = 0 --&gt; because (i,j)
	 * lies on a solid area. 2. gx[i][j] = 0, gy[i][j] = 0 --&gt; because (i,j) lies on
	 * a vertical line of width 1 px. 3. gx[i][j] = 0, gy[i][j] = 0 --&gt; because
	 * (i,j) lies on a horizontal line of width 2 px.
	 */
	public double[][] gd;

	public double[] gd_f;

	@Override
	public void finalize() throws Throwable {
		this.gx = null;
		this.gx_f = null;
		this.gy = null;
		this.gy_f = null;
		this.gd = null;
		this.gd_f = null;
	}
}