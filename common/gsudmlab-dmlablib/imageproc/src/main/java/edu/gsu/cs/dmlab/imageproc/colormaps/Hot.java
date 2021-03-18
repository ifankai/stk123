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
public class Hot extends ColorMap {

	static final double r[] = { 0, 0.03968253968253968, 0.07936507936507936, 0.119047619047619, 0.1587301587301587,
			0.1984126984126984, 0.2380952380952381, 0.2777777777777778, 0.3174603174603174, 0.3571428571428571,
			0.3968253968253968, 0.4365079365079365, 0.4761904761904762, 0.5158730158730158, 0.5555555555555556,
			0.5952380952380952, 0.6349206349206349, 0.6746031746031745, 0.7142857142857142, 0.753968253968254,
			0.7936507936507936, 0.8333333333333333, 0.873015873015873, 0.9126984126984127, 0.9523809523809523,
			0.992063492063492, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1 };
	static final double g[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0.03174603174603163, 0.0714285714285714, 0.1111111111111112, 0.1507936507936507, 0.1904761904761905,
			0.23015873015873, 0.2698412698412698, 0.3095238095238093, 0.3492063492063491, 0.3888888888888888,
			0.4285714285714284, 0.4682539682539679, 0.5079365079365079, 0.5476190476190477, 0.5873015873015872,
			0.6269841269841268, 0.6666666666666665, 0.7063492063492065, 0.746031746031746, 0.7857142857142856,
			0.8253968253968254, 0.8650793650793651, 0.9047619047619047, 0.9444444444444442, 0.984126984126984, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	static final double b[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.04761904761904745, 0.1269841269841265,
			0.2063492063492056, 0.2857142857142856, 0.3650793650793656, 0.4444444444444446, 0.5238095238095237,
			0.6031746031746028, 0.6825396825396828, 0.7619047619047619, 0.8412698412698409, 0.92063492063492, 1 };

	public Hot() throws InvalidConfigException {
		super();
		this.init(256);
	}

	protected void init(int n) throws InvalidConfigException {

		double[] X = ColorMap.linspace(0, 1, r.length);
		double[] rMat = Arrays.copyOf(r, r.length);
		double[] bMat = Arrays.copyOf(b, b.length);
		double[] gMat = Arrays.copyOf(g, g.length);

		this._lut = ColorMap.linear_colormap(X, rMat, // red
				gMat, // green
				bMat, // blue
				n); // number of sample points
	}
}