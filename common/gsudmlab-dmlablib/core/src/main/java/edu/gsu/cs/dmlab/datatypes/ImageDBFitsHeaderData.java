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
 * The header data associated with a particular image in the database. It
 * contains the x,y center of the sun location within that image. The radius of
 * the sun in the image. The observed distance to the sun when the image was
 * taken. And the number of arc/sec per pixel in the image.<br>
 * 
 * For details see: <a href =
 * "http://jsoc.stanford.edu/doc/keywords/AIA/AIA02840_K_AIA-SDO_FITS_Keyword_Document.pdf">
 * AIA FITS Keywords</a>
 * 
 * @author Dustin Kempton, modified by Azim Ahmadzadeh, Data Mining Lab, Georgia
 *         State University
 * 
 */
public class ImageDBFitsHeaderData {

	/**
	 * The x pixel location where the center of the solar radius is on the image
	 * this header data comes from. <br>
	 * <b>AIA FITS Keywords:</b> Reference pixel along array axis j with the center
	 * of the lower left pixel numbered 1 (not 0), i, i.e., the location of disk
	 * center in x and y directions on image, where CRPIX1 = X0_MP + 1, CRPIX2 =
	 * Y0_MP + 1.
	 */
	public double X0;

	/**
	 * The y pixel location where the center of the solar radius is on the image
	 * this header data comes from. <br>
	 * <b>AIA FITS Keywords:</b> Reference pixel along array axis j with the center
	 * of the lower left pixel numbered 1 (not 0), i, i.e., the location of disk
	 * center in x and y directions on image, where CRPIX1 = X0_MP + 1, CRPIX2 =
	 * Y0_MP + 1.
	 */
	public double Y0;

	/**
	 * The solar radius observed in the image that this header data comes from. <br>
	 * <b>AIA FITS Keywords:</b> Radius of the Sun’s image in pixels on the CCD
	 * detector, for the visible light (float).
	 */
	public double R_SUN;

	/**
	 * The distance to the sun observed at the time of the image that this header
	 * came from. <br>
	 * <b> AIA FITS Keywords:</b> Distance from Sun center to SDO in m (double)
	 */
	public double DSUN;

	/**
	 * I believe the number of degrees per pixel on the observed image. <br>
	 * <b> AIA FITS Keywords:</b> Pixel spacing per index value along image axis I,
	 * equal to IM_SCALE except at higher levels when the image has been rescaled
	 * (CDELT1, CDELT2 in x, y directions, respectively).
	 */
	public double CDELT;
	
	/**
	 * A quality flag that is provided by JSOC.
	 */
	public int QUALITY;
}
