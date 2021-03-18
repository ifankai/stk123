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
package edu.gsu.cs.dmlab.imageproc.imageparam;

import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;

/**
 * 
 * This class is designed to compute the <b>mean</b> of each patch of the given
 * <code>2D array</code>, based on the following formula:<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; m = (1/L) * SUM{z_i} <br>
 * <br>
 * where:
 * <UL>
 * <LI>L: the total number of pixels in this patch.
 * <LI>z_i: the intensity value of the i-th pixel in this patch.
 * </UL>
 *
 *
 *
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 *
 */
public class MeanParamCalculator extends BaseMeanCalculator implements IParamCalculator {

	IMeasures.PatchSize patchSize;

	public MeanParamCalculator(IMeasures.PatchSize patchSize) {
		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in MeanParamCalculator.");
		this.patchSize = patchSize;
	}

	@Override
	public double[][] calculateParameter(double[][] image) {
		return this.calculateMeanParameter(image, this.patchSize);
	}
}