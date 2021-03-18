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
package edu.gsu.cs.dmlab.databases.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import smile.math.matrix.SparseMatrix;

/**
 * This is the public interface for classes used to create a database storing
 * images and various descriptors for images.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IImageDBCreator {

	/**
	 * Creates the tables for holding images and image parameter data for the month
	 * represented by the start of the passed in interval.
	 * 
	 * @param period The period that the table corresponds with
	 * 
	 * @return True if table was created, else False.
	 * 
	 * @throws SQLException If something went wrong with the connection.
	 */
	public boolean insertFileDescriptTables(Interval period) throws SQLException;

	/**
	 * Inserts the descriptor for a particular image.
	 * 
	 * @param wavelength The wavelength of the image to be stored
	 * 
	 * @param period     The period of time that the image corresponds to.
	 * 
	 * @return The id for the image description
	 * 
	 * @throws SQLException If the operation fails.
	 */
	public int insertFileDescript(Waveband wavelength, Interval period) throws SQLException;

	/**
	 * Inserts the image for a given descriptor.
	 * 
	 * @param file   The file containing the image.
	 * 
	 * @param id     The id of the image descriptor.
	 * 
	 * @param period The period that the image corresponds to. This is used to
	 *               determine which table to insert into, so it only needs to be in
	 *               the same month.
	 * 
	 * @throws SQLException If the operation files.
	 * 
	 * @throws IOException
	 */
	public void insertImage(BufferedImage file, int id, Interval period) throws SQLException, IOException;

	/**
	 * Inserts the image parameters for the image matching the description id.
	 * 
	 * @param params The matrix containing the calculated image parameters.
	 * 
	 * @param id     The id of the image descriptor.
	 * 
	 * @param period The period that the image corresponds to. This is used to
	 *               determine which table to insert into, so it only needs to be in
	 *               the same month.
	 * 
	 * @throws SQLException If the operation fails.
	 */
	public void insertParams(double[][][] params, int id, Interval period) throws SQLException;

	/**
	 * Inserts the image descriptor vectors for the image matching the description
	 * id.
	 * 
	 * @param vectors Vectors used to describe the image.
	 * 
	 * @param id      The id of the image that the vectors describe.
	 * 
	 * @param period  The period that the image corresponds to. This is used to
	 *                determine which table to insert into, so it only needs to be
	 *                in the same month.
	 * 
	 * @throws SQLException If the operation fails.
	 */
	public void insertImageSparseVect(SparseMatrix[] vectors, int id, Interval period) throws SQLException;

	/**
	 * Inserts the fits header information for the image matching the description
	 * id.
	 * 
	 * @param header The fits header data to insert.
	 * 
	 * @param id     The id of the image descriptor.
	 * 
	 * @param period The period that the image corresponds to. This is used to
	 *               determine which table to insert into, so it only needs to be in
	 *               the same month.
	 * 
	 * @throws SQLException If the operation fails.
	 */
	public void insertHeader(ImageDBFitsHeaderData header, int id, Interval period) throws SQLException;

	/**
	 * A function that checks the database to see if the parameters are in the
	 * database for the passed in identifier.
	 * 
	 * @param id     Identifier that shall be checked against.
	 * 
	 * @param period The month that the image corresponds to.
	 * 
	 * @return True if there, false otherwise.
	 * 
	 * @throws SQLException If the operation fails.
	 */
	public boolean checkParamsExist(int id, Interval period) throws SQLException;

	/**
	 * A function that checks the database to see if the images are in the database
	 * for the passed in identifier.
	 * 
	 * @param id     Identifier that shall be checked against.
	 * 
	 * @param period The month that the image corresponds to.
	 * 
	 * @return True if there, false otherwise.
	 * 
	 * @throws SQLException If the operation fails.
	 */
	public boolean checkImagesExist(int id, Interval period) throws SQLException;
}
