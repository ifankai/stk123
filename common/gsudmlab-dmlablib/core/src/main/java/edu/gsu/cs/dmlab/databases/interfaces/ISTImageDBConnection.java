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

import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.ImageDBDateIdPair;
import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.SparseMatrix;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This is the public interface for image database connections for any project
 * that depends on the image/image parameter database created for the Data
 * Mining Lab at Georgia State University
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTImageDBConnection {

	/**
	 * Gets the image parameters for the given wavelength that intersect the MBR of
	 * the IEvent that is passed in. The parameters are in the form [x][y][paramId]
	 * 
	 * @param event      The event that we want the image parameters for.
	 * 
	 * @param wavelength The wavelength of image that we want the parameters of.
	 * 
	 * @param leftSide   If left side, we use the end time of the IEvent, if not we
	 *                   use the begin time.
	 * 
	 * @return The image parameters that intersect the MBR.
	 * 
	 * @throws SQLException
	 */
	double[][][] getImageParamForWave(ISpatialTemporalObj event, Waveband wavelength, boolean leftSide)
			throws SQLException;

	/**
	 * Gets the image parameters for the given image parameter wavelength pair array
	 * that intersect the MBR of the IEvent passed in. The parameters are in the
	 * from that each matrix represents a particular image parameter at a given
	 * wavelength that corresponds to the position they were in on the params array.
	 * 
	 * @param event    The event that we want the image parameters for.
	 * 
	 * @param params   The array of specific parameter/wavelength pairs that we wish
	 *                 to get.
	 * 
	 * @param leftSide If left side, we use the end time of the IEvent, if not we
	 *                 use the begin time.
	 * 
	 * @return The image parameters that intersect the MBR.
	 * 
	 * @throws Exception
	 */
	DenseMatrix[] getImageParamForEv(ISpatialTemporalObj event, ImageDBWaveParamPair[] params, boolean leftSide)
			throws SQLException;

	/**
	 * Gets the first thumbnail image in the database that happens after the start
	 * time of the input interval, at a given wavelength.
	 * 
	 * @param period     The period to start the search from.
	 * 
	 * @param wavelength The wavelength of Image we wish to get.
	 * 
	 * @return The first image starting after the period begin time.
	 * 
	 * @throws SQLException If something went wrong with the server.
	 * 
	 * @throws IOException  If something went wrong with the decoding of the image.
	 */
	BufferedImage getFirstImage(Interval period, Waveband wavelength) throws SQLException, IOException;

	/**
	 * Gets the first full resolution image in the database that happens after the
	 * start time of the input interval, at a given wavelength.
	 * 
	 * @param period     The period to start the search from.
	 * 
	 * @param wavelength The wavelength of Image we wish to get.
	 * 
	 * @return The first image starting after the period begin time.
	 * 
	 * @throws SQLException If something went wrong with the server.
	 * 
	 * @throws IOException  If something went wrong with the decoding of the image.
	 */
	BufferedImage getFirstFullImage(Interval period, Waveband wavelength) throws SQLException, IOException;

	/**
	 * Gets the thumbnail image in the month that the period begins that has the
	 * passed in id value.
	 * 
	 * @param period The month in which to get the image.
	 * 
	 * @param id     The id associated with the image we wish to get.
	 * 
	 * @return The buffered image with the given id from the given month.
	 * 
	 * @throws SQLException If something went wrong with the server.
	 * 
	 * @throws IOException  If something went wrong with decoding of the image
	 */
	BufferedImage getImgForId(Interval period, int id) throws SQLException, IOException;

	/**
	 * Gets the full resolution image in the month that the period begins that has
	 * the passed in id value.
	 * 
	 * @param period The month in which to get the image.
	 * 
	 * @param id     The id associated with the image we wish to get.
	 * 
	 * @return The buffered image with the given id from the given month.
	 * 
	 * @throws SQLException If something went wrong with the server.
	 * 
	 * @throws IOException  If something went wrong with decoding of the image
	 */
	BufferedImage getFullImgForId(Interval period, int id) throws SQLException, IOException;

	/**
	 * Gets the image ids from the month that the input period begins through either
	 * the end of the period or the end of the month, which ever comes first. The
	 * ids will be for the input wavelength of images only.
	 * 
	 * @param period     The period over which we wish to get Ids for.
	 * 
	 * @param wavelength The wavelength of images we wish to get Ids for.
	 * 
	 * @return The Date and Id pairs of all the images in the range and of the input
	 *         wavelength.
	 * 
	 * @throws SQLException
	 */
	ImageDBDateIdPair[] getImageIdsForInterval(Interval period, Waveband wavelength) throws SQLException;

	/**
	 * Gets the full disk set of image parameters for all image parameters of the
	 * image with the given id in the month that the given input period starts.
	 * 
	 * @param period The month in which we wish to get the parameters for.
	 * 
	 * @param id     The id of the image in the given month.
	 * 
	 * @return The set of full disk parameters for the image with the given id.
	 * 
	 * @throws SQLException
	 */
	DenseMatrix[] getImageParamForId(Interval period, int id) throws SQLException;

	/**
	 * Gets all of the sparse image descriptor vectors for the image with the given
	 * id, in the given month.
	 * 
	 * @param period The month we wish to query from
	 * 
	 * @param id     The id of the image we wish to query.
	 * 
	 * @return The set of all sparse image descriptors for the given image.
	 * 
	 * @throws SQLException When something with the server failed.
	 */
	SparseMatrix[] getImageSparseVectForId(Interval period, int id) throws SQLException;

	/**
	 * Gets only the top level sparse image descriptor for the image with the given
	 * id, in the given month.
	 * 
	 * @param period The month we wish to query.
	 * 
	 * @param id     The id of the image we wish to query.
	 * 
	 * @return The top level descriptor for the given image.
	 * 
	 * @throws SQLException When something with the server failed.
	 */
	SparseMatrix getFirstImageSparseVectForId(Interval period, int id) throws SQLException;

	/**
	 * Gets the header information for the image in the month that the period begins
	 * that has the passed in id value.
	 * 
	 * @param period The month in which to get the image header.
	 * 
	 * @param id     The id associated with the image header we wish to get.
	 * 
	 * @return The header for the image in the month requested.
	 * 
	 * @throws SQLException When something with the serve failed.
	 */
	ImageDBFitsHeaderData getHeaderForId(Interval period, int id) throws SQLException;
}