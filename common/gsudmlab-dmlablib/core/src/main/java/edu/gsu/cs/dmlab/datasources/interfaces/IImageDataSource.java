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

import java.awt.image.BufferedImage;
import java.io.File;

import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.Waveband;

/**
 * This is the public interface for classes used to retrieve images coming from
 * the source location that are intended to be inserted into our database.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IImageDataSource {

	/**
	 * Method that gets an image as a bufferedImage that corresponds to the input
	 * time and wavelength from the datasource.
	 * 
	 * @param date       The time that the image was recorded.
	 * 
	 * @param wavelength The wavelength the image was recorded in.
	 * 
	 * @return The BufferedImage representing the results, or null if no image was
	 *         found fitting the constraints desired.
	 */
	public BufferedImage getImage(DateTime date, Waveband wavelength);

	/**
	 * Method that gets the header information for an image that corresponds to the
	 * input time and wavelength from the datasource.
	 * 
	 * @param date       The time that the image was recorded.
	 * 
	 * @param wavelength The wavelength the image was recorded in.
	 * 
	 * @return The ImageDBFitsHeaderData representing the results, or null if no
	 *         image was found fitting the constraints desired.
	 */
	public ImageDBFitsHeaderData getHeader(DateTime date, Waveband wavelength);

	/**
	 * Method that gets the file representation of an image that corresponds to the
	 * input time and wavelength from the datasource.
	 * 
	 * @param date       The time that the image was recorded.
	 * 
	 * @param wavelength The wavelength the image was recorded in.
	 * 
	 * @return The File representing the results, or null if no image was found
	 *         fitting the constraints desired.
	 */
	public File getImageFile(DateTime date, Waveband wavelength);

}
