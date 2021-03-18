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
package edu.gsu.cs.dmlab.features.interfaces;

import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;

/**
 * Interface for classes that produce a value representing the fitness of a
 * parameter for representing an event type.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IStatProducer {

	/**
	 * Method for computing the fitness statistic for the image descriptor
	 * constructed with the passed in image wavelength filter and parameter
	 * calculation pairs.
	 * 
	 * @param dims
	 *            The dimensions or image filter wavelength and parameter
	 *            calculation pairs used to create a descriptor of an event
	 *            detection.
	 * @return The fitness score for descriptors based on the passed in list.
	 */
	public float computeStat(ImageDBWaveParamPair[] dims);
}
