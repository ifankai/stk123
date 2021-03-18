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
package edu.gsu.cs.dmlab.datasources.interfaces;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.datatypes.Waveband;

/**
 * This is the public interface for classes used to retrieve images coming from
 * the source location that are intended to be inserted into our database.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IImageFileDataSource {

	/**
	 * Method that gets an image as a byte array that corresponds to the input time
	 * and wavelength from the datasource. It returns a pair of byte array and
	 * string, where the byte array is the byte representation of the image in
	 * whatever format it is stored in its respective source and the string is the
	 * name of the image from the datasourc. The byte array is intended to be the
	 * raw file representation of the image and can be saved as a file or read into
	 * whatever decoding method is necessary to get the desired representation.
	 * 
	 * @param timeStamp  The time that the image was recorded.
	 * 
	 * @param wavelength The wavelength the image was recorded in.
	 * 
	 * @return The pair of byte array and string that represents the results, or
	 *         null if no image was found fitting the constraints desired.
	 */
	public Pair<byte[], String> getImageAtTime(DateTime timeStamp, Waveband wavelength);
}
