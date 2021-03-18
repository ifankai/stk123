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
package edu.gsu.cs.dmlab.imageproc;

import java.util.HashMap;

import edu.gsu.cs.dmlab.imageproc.interfaces.IImgParamNormalizer;

/**
 * ImgParamNormalizer class simply does min-max normalization on the passed in
 * array of parameter values.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ImgParamNormalizer implements IImgParamNormalizer {

	private HashMap<Integer, double[]> rangeMap;

	/**
	 * Constructor that takes in the set of min-max pair for each dimension the
	 * parameter arrays this object is meant to normalize. Any values will be
	 * clipped to outside the ranges will be clipped to be the edge value.
	 * 
	 * @param rangeMap The set of min-max pairs for each dimension of the passed in
	 *                 parameter arrays.
	 */
	public ImgParamNormalizer(HashMap<Integer, double[]> rangeMap) {
		if (rangeMap == null)
			throw new IllegalArgumentException("HashMap cannot be null in ImgParamNormalizer constructor.");
		this.rangeMap = rangeMap;
	}

	@Override
	public void finalize() throws Throwable {
		this.rangeMap = null;
	}

	@Override
	public void normalizeParameterValues(double[][][] parameters) {
		for (int i = 0; i < parameters[0][0].length; i++) {
			double[] rangeArr = this.rangeMap.get(Integer.valueOf(i + 1));
			double range = rangeArr[1] - rangeArr[0];

			for (int x = 0; x < parameters.length; x++) {
				for (int y = 0; y < parameters[x].length; y++) {
					double val = parameters[x][y][i];

					if (rangeArr.length == 3) {
						val = Math.log(val);
					}

					if (val < rangeArr[0])
						val = rangeArr[0];
					if (val > rangeArr[1])
						val = rangeArr[1];

					val -= rangeArr[0];
					val /= range;
					parameters[x][y][i] = (float) val;
				}
			}
		}

	}

}
