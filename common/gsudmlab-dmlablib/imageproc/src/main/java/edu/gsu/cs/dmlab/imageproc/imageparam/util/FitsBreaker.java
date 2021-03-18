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
package edu.gsu.cs.dmlab.imageproc.imageparam.util;

import java.io.IOException;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.ImageData;
import nom.tam.fits.ImageHDU;
import nom.tam.image.compression.hdu.CompressedImageHDU;

/**
 * This class is designed to provide an easier access to the content of the FITS
 * files. After constructing an instance of this class, the <code>read</code>
 * function must be call for the file to be actually read. In this function, the
 * given FITS image will be copied (cast and normalized if requested) to the
 * field <code>image</code>. In addition, some important values of the FITS
 * header, and the minimum and maximum valid values will be extracted and stored
 * in appropriate fields of the class. <br>
 * <br>
 * Depending on the boolean argument of the constructor,
 * <code>isCompressed</code> this class treats the given FITS file as either
 * compressed or uncompressed. In the compressed format, the values of the image
 * are originally stored as 2-Byte Integers, while in the uncompressed formats,
 * the values are of type double. The process of compression is a non-lossy
 * method (such as Rice) which multiplies the double values by a large number so
 * that it can keep the values as integers without losing precision.<br>
 * The other boolean argument of the constructor, <code>isOutsideDisk</code>, is
 * to provide the user with the option of ignoring the pixel values outside the
 * Sun's disk. This are should be black, but due to the device noise, other
 * values could be recorded. It is recommended to ignore those values. To
 * calculate the acceptable region of the disk, the center of the disk (CRPIX1,
 * CRPIX2) and the radius of the Sun in pixel (R_SUN) are used. These values
 * will be extracted from each FITS file's header individually. <br>
 * Note: To check if a FITS file is compressed or not, the card "TTYPE1" should
 * be read from the header. For example,
 * <code>TTYPE1  = 'COMPRESSED_DATA'</code>. Another way to make sure if a file
 * is compressed or not is that only in an uncompressed file, the cards
 * <code>NAXIS1</code> and <code>NAXIS2</code> correspond to the width and
 * height of the image. <br>
 * <br>
 * Some references:
 * <ul>
 * <li><a href=
 * "https://heasarc.gsfc.nasa.gov/docs/heasarc/fits/java/v1.0/javadoc/">Javadoc
 * for nom.tam library</a>
 * <li>
 * <a href="http://nom-tam-fits.github.io/nom-tam-fits/intro.html">Introduction
 * to nom.tam library</a>
 * <li><a href=
 * "http://jsoc.stanford.edu/doc/keywords/AIA/AIA02840_K_AIA-SDO_FITS_Keyword_Document.pdf">AIA
 * FITS keywords</a>
 * </ul>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class FitsBreaker {

	/** The minimum value of a pixel of the image */
	private final static int MIN_FITS_VAL = 0;
	/** The maximum value of a pixel of the image (2^14) */
	private final static int MAX_FITS_VAL = 16383;

	private Fits f;
	/**
	 * Cast, cleaned, normalized (optional), and transformed so that the origin is
	 * on top-left corner.
	 */
	private double[][] image;
	/** This stores the HDUs (Header-Data Units) of uncompressed fits files. */
	private ImageHDU hdu;
	/** This stores the HDUs (Header-Data Units) of compressed fits files. */
	private CompressedImageHDU chdu;
	/**
	 * This stores some spatial information of header as an ImageDBFitsHeaderData
	 * object.
	 */
	private ImageDBFitsHeaderData headerData;
	/** This indicates whether or not a fits file is compressed. */
	private boolean isCompressed;

	/**
	 * This indicates whether or not the values outside of the Sun's disk should be
	 * ignored. If True, -1 will be set to those pixels.
	 */
	private boolean ignoreOutsideDisk;

	/**
	 * The equivalent of DATAMIN in fits header. This could be some negative numbers
	 * due to noise.
	 */
	private double minValidValue;
	/**
	 * The equivalent of DATAMAX in fits header. This cannot exceed MAX_FITS_VAL.
	 */
	private double maxValidValue;
	/** If normalization required, this stores the new min value after scaling. */
	private double normalizedMin;
	/** If normalization required, this stores the new max value after scaling. */
	private double normalizedMax;

	/**
	 * This constructor reads the fits file, gets some necessary information from
	 * its header, detects whether or not this file is compressed or not, and
	 * extract a 2D double array representing the (aia or hmi) image. If
	 * normalization of the values in the image array is needed, the output array
	 * will be min-max normalized based on the new min and new max passed to the
	 * method:<br>
	 * <code>processImage(double[][] im, double norm, double newMin, double newMax)</code>.
	 * <br>
	 * 
	 * @param isCompressed
	 *            if true, the constructor reads the fits file assuming that it is
	 *            compressed. (AIA images are compressed, while HMI files are not.)
	 * @param ignoreOutsideDisk
	 *            if true, all intensity values of pixels which lie outside the
	 *            Sun's disk will be replaced with -1.
	 */
	public FitsBreaker(boolean isCompressed, boolean ignoreOutsideDisk) {

		this.chdu = null;
		this.f = null;
		this.image = null;
		this.hdu = null;
		this.headerData = null;
		this.isCompressed = isCompressed;
		this.ignoreOutsideDisk = ignoreOutsideDisk;
		this.minValidValue = 0;
		this.maxValidValue = 0;
		this.normalizedMin = 0;
		;
		this.normalizedMax = 0;
	}

	/**
	 * 
	 * @param fitsDir
	 */
	public void read(String fitsDir) {

		try {
			this.f = new Fits(fitsDir);
			/* do not add f.read() or f.readHDU() here. */

			if (this.isCompressed) {
				breakCompressedFits(this.f);
			} else {
				breakUncompressedFits(this.f);
			}

		} catch (FitsException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}

		try {
			this.f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method reads the image from an uncompressed FITS file, as well as the
	 * value of some of the cards in the header.
	 * 
	 * @param f
	 * @throws FitsException
	 * @throws IOException
	 */
	private void breakUncompressedFits(Fits f) throws FitsException, IOException {

		/*
		 * Get the image from the fits file
		 */
		f.read(); /* do not remove this line */
		hdu = (ImageHDU) f.getHDU(1);
		int[][] im = (int[][]) hdu.getKernel();

		this.minValidValue = hdu.getMinimumValue();
		this.maxValidValue = hdu.getMaximumValue();
		this.normalizedMin = this.minValidValue;
		this.normalizedMax = this.maxValidValue;

		/*
		 * Copy some header info to ImageDBFitsHeaderData
		 */
		headerData = new ImageDBFitsHeaderData();
		Header header = hdu.getHeader();
		this.headerData.X0 = header.getDoubleValue("CRPIX1");
		this.headerData.Y0 = header.getDoubleValue("CRPIX2");
		this.headerData.CDELT = header.getDoubleValue("CDELT1");
		this.headerData.DSUN = header.getDoubleValue("DSUN_OBS");
		this.headerData.R_SUN = header.getDoubleValue("R_SUN");

		/*
		 * Cast short to double ([[S to [[D)
		 */
		this.image = processImage(im, 0);
		// this.image = processImage(im, 1, 0, 255); //In case normalization is needed.
	}

	/**
	 * This method reads the image from a compressed FITS file, as well as the value
	 * of some of the cards in the header.
	 * 
	 * @param f
	 * @throws FitsException
	 * @throws IOException
	 */
	private void breakCompressedFits(Fits f) throws FitsException, IOException {

		ImageHDU uncompressedImage = null;
		ImageData imageData = null;

		/*
		 * Get the image from the fits file
		 */
		f.readHDU(); /* do not remove this line */
		this.chdu = (CompressedImageHDU) f.readHDU();
		uncompressedImage = this.chdu.asImageHDU();
		imageData = uncompressedImage.getData();
		short[][] im = (short[][]) imageData.getData();

		this.minValidValue = chdu.getMinimumValue();
		this.maxValidValue = chdu.getMaximumValue();
		this.normalizedMin = this.minValidValue;
		this.normalizedMax = this.maxValidValue;

		/*
		 * x Copy some header info to ImageDBFitsHeaderData
		 */
		headerData = new ImageDBFitsHeaderData();
		Header header = chdu.getHeader();
		this.headerData.X0 = header.getDoubleValue("CRPIX1");
		this.headerData.Y0 = header.getDoubleValue("CRPIX2");
		this.headerData.CDELT = header.getDoubleValue("CDELT1");
		this.headerData.DSUN = header.getDoubleValue("DSUN_OBS");
		this.headerData.R_SUN = header.getDoubleValue("R_SUN");

		/*
		 * Cast short to double ([[S to [[D)
		 */
		this.image = processImage(im, 0);
		// this.image = processImage(im, 1, 0, 255); //In case normalization is needed.
	}

	/**
	 * This method normalize (only if provided with new min and max) and cast the
	 * given array to a 2D array of double.<br>
	 * <b>Usage:</b><br>
	 * <code>processImage(double[][] im, double norm, double newMin, double newMax)</code><br>
	 * <code>processImage(image, 1, newMin, newMax)</code><br>
	 * <code>processImage(image, 0)</code><br>
	 * 
	 * @param im
	 *            the input image in the form of a 2D array of type short
	 * @param normalizationParameters
	 *            takes 1 or 3 values, as an array or single double values. if its
	 *            first value is zero, it indicates no normalization is needed, and
	 *            therefore the other two values, if provided, will be ignored. If
	 *            the first value is 1, then the second and third values are
	 *            considered the new min and max, respectively. <br>
	 *            <b>Note:</b> The (0,0) point in the image stored in FITS files is
	 *            considered at the left-bottom corner, however, while casting, this
	 *            method rotates it in such a way that the (0,0) point is at the
	 *            top-left corner, to be consistent with the other parts of the
	 *            dmlablib library.
	 * 
	 * @return a 2D array of double (cast from type int) which depending on the
	 *         provided parameters may or may not be normalized.
	 */
	private double[][] processImage(int[][] im, double... normalizationParameters) {

		if (normalizationParameters[0] != 1 && normalizationParameters[0] != 0) {
			throw new IllegalArgumentException(
					"The second argument should be either 0 (= no normalization needed) or 1 (= do normalize).");
		}
		if (normalizationParameters[0] == 1 && normalizationParameters.length != 3) {
			throw new IllegalArgumentException(
					"For normalization, new min and max should be provided as the 3rd and 4th arguments.");
		}

		double normVal = 0;

		if (normalizationParameters[0] == 1) {

			this.normalizedMin = normalizationParameters[1];
			this.normalizedMax = normalizationParameters[2];

		}

		double[][] processedImage = new double[im.length][im[0].length];

		// Transform, remove negatives, cast to double, and (if requested) normalize
		int nRows = im.length;
		int nCols = im[0].length;

		if (normalizationParameters[0] == 1) {
			for (int row = 0; row < nRows; row++) {
				for (int col = 0; col < nCols; col++) {
					// Get rid of possible negative values (noises)
					normVal = (im[row][col] < MIN_FITS_VAL) ? MIN_FITS_VAL : im[row][col];
					// Min-Max normalization

					if (this.ignoreOutsideDisk) {
						// Set -1 to values out of the disk
						if (isOutsideDisk(col, row)) {
							normVal = -1;
						} else {
							// Min-Max normalization
							normVal = ((normVal - MIN_FITS_VAL) / (MAX_FITS_VAL - MIN_FITS_VAL))
									* (this.normalizedMax - this.normalizedMin) + this.normalizedMin;
						}
					} else {
						// Min-Max normalization
						normVal = ((normVal - MIN_FITS_VAL) / (MAX_FITS_VAL - MIN_FITS_VAL))
								* (this.normalizedMax - this.normalizedMin) + this.normalizedMin;
					}

					processedImage[nRows - row - 1][col] = normVal;
				}
			}
		}
		// Transform, remove negatives, cast to double
		else {
			for (int row = 0; row < nRows; row++) {
				for (int col = 0; col < nCols; col++) {
					// Get rid of possible negative values (noises)
					processedImage[nRows - row - 1][col] = (im[row][col] < MIN_FITS_VAL) ? MIN_FITS_VAL : im[row][col];

					if (this.ignoreOutsideDisk) {
						// Set -1 to values out of the disk
						if (isOutsideDisk(col, row)) {
							processedImage[nRows - row - 1][col] = -1;
						}
					}
				}
			}
		}
		return processedImage;
	}

	/**
	 * This method normalize (only if provided with new min and max) and cast the
	 * given array to a 2D array of double.<br>
	 * <b>Usage:</b><br>
	 * <code>processImage(image, 1, newMin, newMax)</code><br>
	 * <code>processImage(image, 0)</code><br>
	 * 
	 * @param im
	 *            the input image in the form of a 2D array of short
	 * @param normalizationParameters
	 *            takes 1 or 3 values, as an array or single double values. if its
	 *            first value is zero, it indicates no normalization is needed, and
	 *            therefore the other two values will be ignored if provided. If the
	 *            first value is 1, then the second and third values are considered
	 *            the new min and max, respectively. <br>
	 *            <b>Note:</b> The (0,0) point in the image stored in FITS files is
	 *            considered at the left-bottom corner, however, while casting, this
	 *            method rotates it in such a way that the (0,0) point is at the
	 *            top-left corner, to be consistent with the other parts of the
	 *            dmlablib library.
	 * 
	 * @return a 2D array of double (cast from type short) which depending on the
	 *         provided parameters may or may not be normalized.
	 */
	private double[][] processImage(short[][] im, double... normalizationParameters) {

		if (normalizationParameters[0] != 1 && normalizationParameters[0] != 0) {
			throw new IllegalArgumentException(
					"The second argument should be either 0 (= no normalization needed) or 1 (= do normalize).");
		}
		if (normalizationParameters[0] == 1 && normalizationParameters.length != 3) {
			throw new IllegalArgumentException(
					"For normalization, new min and max should be provided as the 3rd and 4th arguments.");
		}

		if (normalizationParameters[0] == 1) {
			this.normalizedMin = normalizationParameters[1];
			this.normalizedMax = normalizationParameters[2];
		}

		double normVal = 0;
		double[][] processedImage = new double[im.length][im[0].length];

		// Transform, cast to double, and (if requested) normalize
		int nRows = im.length;
		int nCols = im[0].length;

		if (normalizationParameters[0] == 1) {
			for (int row = 0; row < nRows; row++) {
				for (int col = 0; col < nCols; col++) {

					// Get rid of possible negative values (noises)
					normVal = (im[row][col] < MIN_FITS_VAL) ? MIN_FITS_VAL : im[row][col];

					if (this.ignoreOutsideDisk) {
						// Set -1 to values out of the disk
						if (isOutsideDisk(col, row)) {
							normVal = -1;
						} else {
							// Min-Max normalization
							normVal = ((normVal - MIN_FITS_VAL) / (MAX_FITS_VAL - MIN_FITS_VAL))
									* (this.normalizedMax - this.normalizedMin) + this.normalizedMin;
						}
					} else {
						// Min-Max normalization
						normVal = ((normVal - MIN_FITS_VAL) / (MAX_FITS_VAL - MIN_FITS_VAL))
								* (this.normalizedMax - this.normalizedMin) + this.normalizedMin;
					}

					processedImage[nRows - row - 1][col] = normVal;
				}
			}
		}
		// Transform, cast to double
		else {
			for (int row = 0; row < nRows; row++) {
				for (int col = 0; col < nCols; col++) {
					// Get rid of possible negative values (noises)
					processedImage[nRows - row - 1][col] = (im[row][col] < MIN_FITS_VAL) ? MIN_FITS_VAL : im[row][col];

					if (this.ignoreOutsideDisk) {
						// Set -1 to values out of the disk
						if (isOutsideDisk(col, row)) {
							processedImage[nRows - row - 1][col] = -1;
						}
					}
				}
			}
		}

		return processedImage;
	}

	/**
	 * This method calculates whether or not a pixel at row y and column x is
	 * outside the Sun's disk or not.
	 * 
	 * @param x
	 *            column index of the pixel
	 * @param y
	 *            row index of the pixel
	 * @return True if pixel (x,y) is outside the disk. False, otherwise.
	 */
	private boolean isOutsideDisk(int x, int y) {

		double distFromCenter = 0;
		double[] thisPoint = new double[] { x, y };
		double[] sunCenter = { this.headerData.X0, this.headerData.X0 };
		EuclideanDistance ed = new EuclideanDistance();
		distFromCenter = ed.compute(thisPoint, sunCenter);
		if (distFromCenter > this.headerData.R_SUN)
			return true;

		return false;
	}

//	public BasicHDU getHdu() {
//
//		if (this.isCompressed)
//			return chdu;
//		else
//			return hdu;
//	}

	public double[][] getImage() {
		return image;
	}

	public double getMinValidValue() {
		return minValidValue;
	}

	public double getMaxValidValue() {
		return maxValidValue;
	}

	public double getNormalizedMin() {
		return normalizedMin;
	}

	public double getNormalizedMax() {
		return normalizedMax;
	}

	public boolean isOutsideDiskIgnored() {
		return this.ignoreOutsideDisk;
	}

	public ImageDBFitsHeaderData getHeaderData() {
		return headerData;
	}

}