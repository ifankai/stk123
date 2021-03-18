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
package edu.gsu.cs.dmlab.imageproc.colormaps;

import java.util.Arrays;

import edu.gsu.cs.dmlab.exceptions.InvalidConfigException;
import edu.gsu.cs.dmlab.imageproc.ColorMap;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SDO_AIA_193 extends ColorMap {

	static final double r[] = { 0, 0.320621, 0.549464, 0.731219, 0.884956, 1.0 };
	static final double g[] = { 0, 0.0887672, 0.298675, 0.532756, 0.784329, 1.0 };
	static final double b[] = { 0, 0.00176397, 0.0787188, 0.272389, 0.601831, 1.0 };

	static final double idx[] = { 0.0, 0.100000, 0.298598, 0.530707, 0.780554, 1.0 };

	public SDO_AIA_193() throws InvalidConfigException {
		super();
		this.init(256);
	}

	@Override
	protected void init(int n) throws InvalidConfigException {
		double[] X = Arrays.copyOf(idx, idx.length);
		double[] rMat = Arrays.copyOf(r, r.length);
		double[] gMat = Arrays.copyOf(g, g.length);
		double[] bMat = Arrays.copyOf(b, b.length);

		this._lut = ColorMap.linear_colormap(X, rMat, // red
				gMat, // green
				bMat, // blue
				n); // number of sample points

	}

}
