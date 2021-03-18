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
package edu.gsu.cs.dmlab.databases;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.slf4j.Logger;

import edu.gsu.cs.dmlab.databases.interfaces.ISTImageDBConnection;
import edu.gsu.cs.dmlab.datatypes.ImageDBDateIdPair;
import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgParamNormalizer;
import edu.gsu.cs.dmlab.util.Utility;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.JMatrix;
import smile.math.matrix.SparseMatrix;

/**
 * This class is used to access images, image parameters, sparse descriptors for
 * images, and some additional header data for images from the database created
 * by the {@link edu.gsu.cs.dmlab.databases.ImageDBCreator ImageDBCreator}.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class NonCacheImageDBConnection implements ISTImageDBConnection {
	IImgParamNormalizer normalizer;
	DataSource dsourc = null;

	int paramDownSample = 64;
	int paramDim = 10;
	int vectRows = 256;
	private Logger logger;

	/**
	 * Constructor that assumes default values for parameter down sample and the
	 * number of parameters for each image cell. Those values are a division by 64
	 * for all coordinates of input events, and 10 image parameters per cell
	 * location.
	 * 
	 * @param dsourc     The data source connection that is used to connect to the
	 *                   database.
	 * 
	 * @param normalizer The image parameter normalizer, can be null, and if it is,
	 *                   then no normalization is performed on the parameters before
	 *                   return. Else, parameters are normalized prior to return
	 *                   using this object.
	 * 
	 * @param logger     Logger used to report errors that occurred while processing
	 *                   data requests.
	 * 
	 */
	public NonCacheImageDBConnection(DataSource dsourc, IImgParamNormalizer normalizer, Logger logger) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in ImageDBConnection constructor.");
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null in ImageDBConnection constructor.");
		this.dsourc = dsourc;
		this.normalizer = normalizer;
		this.logger = logger;

	}

	/**
	 * Constructor that defines the parameter dimension and down sampling used to
	 * match the input coordinates with the reduced dimensionality parameter space.
	 * 
	 * @param dsourc          The data source connection that is used to connect to
	 *                        the database.
	 * 
	 * @param normalizer      The image parameter normalizer, can be null, and if it
	 *                        is, then no normalization is performed on the
	 *                        parameters before return. Else, parameters are
	 *                        normalized prior to return using this object.
	 * 
	 * @param paramDim        The depth of image parameters (I.E. the number
	 *                        calculated) at each cell location.
	 * 
	 * @param paramDownSample The divisor used to match the input coordinates with
	 *                        the reduced dimensionality parameter space.
	 * 
	 * @param logger          Logger used to report errors that occurred while
	 *                        processing data requests.
	 */
	public NonCacheImageDBConnection(DataSource dsourc, IImgParamNormalizer normalizer, int paramDim,
			int paramDownSample, Logger logger) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in ImageDBConnection constructor.");
		if (paramDim == 0)
			throw new IllegalArgumentException("paramDim cannot be null in ImageDBConnection constructor.");
		if (paramDownSample == 0)
			throw new IllegalArgumentException("paramDownSample cannot be null in ImageDBConnection constructor.");
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null in ImageDBConnection constructor.");

		this.dsourc = dsourc;
		this.normalizer = normalizer;
		this.paramDim = paramDim;
		this.paramDownSample = paramDownSample;
		this.logger = logger;
	}

	@Override
	public void finalize() throws Throwable {
		this.dsourc = null;
		this.normalizer = null;
		this.dsourc = null;
		this.logger = null;
	}

	@Override
	public double[][][] getImageParamForWave(ISpatialTemporalObj event, Waveband wavelength, boolean leftSide)
			throws SQLException {
		double[][][] retVal = null;

		String queryFileString = this.buildFileQueryString(event, leftSide);
		String queryParamString = this.buildQueryParamString(event.getTimePeriod());

		Timestamp startTime = new Timestamp(event.getTimePeriod().getStartMillis());
		Timestamp endTime = new Timestamp(event.getTimePeriod().getEndMillis());

		Envelope tmpBbox = event.getEnvelope();
		int wSize;
		int xStart;
		if (((int) tmpBbox.getWidth() / this.paramDownSample) < 5) {
			xStart = ((int) tmpBbox.getMinX() / this.paramDownSample) - 2;
			wSize = 5;
		} else {
			xStart = ((int) tmpBbox.getMinX() / this.paramDownSample);
			wSize = ((int) tmpBbox.getWidth() / this.paramDownSample);
		}

		int hSize;
		int yStart;
		if (((int) tmpBbox.getHeight() / this.paramDownSample) < 5) {
			yStart = ((int) tmpBbox.getMinY() / this.paramDownSample) - 2;
			hSize = 5;
		} else {
			yStart = ((int) tmpBbox.getMinY() / this.paramDownSample);
			hSize = ((int) tmpBbox.getHeight() / this.paramDownSample);
		}
		Rectangle rec = new Rectangle(xStart, yStart, wSize, hSize);

		retVal = new double[rec.height][rec.width][this.paramDim];

		int tryCount = 0;
		boolean executed = false;
		try {
			while (!executed && tryCount < 3) {
				try (Connection con = this.dsourc.getConnection()) {
					con.setAutoCommit(true);

					ArrayList<Integer> idList = new ArrayList<Integer>();
					try (PreparedStatement file_prep_stmt = con.prepareStatement(queryFileString)) {
						file_prep_stmt.setTimestamp(1, startTime);
						file_prep_stmt.setTimestamp(2, endTime);
						file_prep_stmt.setTimestamp(3, startTime);
						file_prep_stmt.setTimestamp(4, endTime);
						file_prep_stmt.setInt(5, Utility.convertWavebandToInt(wavelength));

						try (ResultSet imgFileIdResults = file_prep_stmt.executeQuery()) {
							while (imgFileIdResults.next()) {
								int id = imgFileIdResults.getInt("id");
								idList.add(Integer.valueOf(id));
							}
						}
					}

					boolean gotParams = false;
					while (!gotParams && !idList.isEmpty()) {
						try (PreparedStatement param_prep_stmt = con.prepareStatement(queryParamString)) {
							int id = idList.get(0);
							idList.remove(0);
							param_prep_stmt.setInt(1, id);
							param_prep_stmt.setInt(2, (int) rec.x);
							param_prep_stmt.setInt(3, (int) rec.x + (int) rec.width - 1);
							param_prep_stmt.setInt(4, (int) rec.y);
							param_prep_stmt.setInt(5, (int) rec.y + (int) rec.height - 1);

							int count = 0;
							try (ResultSet imgParamRslts = param_prep_stmt.executeQuery()) {
								while (imgParamRslts.next()) {
									int x = imgParamRslts.getInt("x");
									x = x - (int) rec.x;

									int y = imgParamRslts.getInt("y");
									y = y - (int) rec.y;

									for (int i = 1; i <= this.paramDim; i++) {
										retVal[y][x][i - 1] = imgParamRslts.getFloat("p" + i);
									}
									count++;
								}
							}

							if (count == (int) rec.height * (int) rec.width) {
								executed = true;
								gotParams = true;
							}
						}
					}

				} catch (SQLException e) {
					if (tryCount >= 3)
						throw e;
				}
				tryCount++;
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method getImageParamForWave ", e);
			throw e;
		}

		if (this.normalizer != null)
			this.normalizer.normalizeParameterValues(retVal);
		return retVal;
	}

	@Override
	public BufferedImage getFirstImage(Interval period, Waveband wavelength) throws SQLException, IOException {

		BufferedImage bImg = null;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			// for pulling out the year and month for the table name to select
			// from

			ImageDBDateIdPair[] pairs = this.getImageIdsForInterval(period, wavelength);
			if (pairs.length > 0) {

				int frameId = pairs[0].id;
				String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

				String queryString2 = "SELECT image_file FROM image_files_" + name;

				queryString2 += " WHERE file_id = ? ;";
				try (PreparedStatement img_prep_stmt = con.prepareStatement(queryString2)) {
					img_prep_stmt.setInt(1, frameId);

					try (ResultSet imgRslts = img_prep_stmt.executeQuery()) {
						if (imgRslts.next()) {
							Blob blob = imgRslts.getBlob(1);
							try (InputStream bStream = blob.getBinaryStream()) {
								bImg = ImageIO.read(bStream);
							}
							blob.free();
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method getFirstImage", e);
			throw e;
		}

		return bImg;
	}

	@Override
	public BufferedImage getFirstFullImage(Interval period, Waveband wavelength) throws SQLException, IOException {

		BufferedImage bImg = null;

		// for pulling out the year and month for the table name to select
		// from

		ImageDBDateIdPair[] pairs = this.getImageIdsForInterval(period, wavelength);

		if (pairs.length > 0) {
			try (Connection con = this.dsourc.getConnection()) {
				con.setAutoCommit(true);

				int frameId = pairs[0].id;

				String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());
				String queryString = "SELECT image_file FROM image_files_" + name;

				queryString += "_full WHERE file_id = ? ;";
				try (PreparedStatement img_prep_stmt = con.prepareStatement(queryString)) {
					img_prep_stmt.setInt(1, frameId);
					try (ResultSet imgRslts = img_prep_stmt.executeQuery()) {
						if (imgRslts.next()) {
							Blob blob = imgRslts.getBlob(1);
							try (InputStream bStream = blob.getBinaryStream()) {
								bImg = ImageIO.read(bStream);
							}
							blob.free();
						}
					}
				}
			} catch (SQLException | IOException e) {
				logger.error("Exception occurred while executing method getFirstFullImage", e);
				throw e;
			}
		}
		return bImg;
	}

	private String buildFileQueryString(ISpatialTemporalObj ev, boolean isLeft) {
		// for constructing the table names from the year and month of
		// the event

		String calStartYear = "" + ev.getTimePeriod().getStart().getYear();

		String calStartMonth = String.format("%02d", ev.getTimePeriod().getStart().getMonthOfYear());

		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT id FROM files_");
		queryString.append(calStartYear);
		queryString.append(calStartMonth);
		queryString.append(" WHERE(((startdate >= ? AND startdate < ?) OR (enddate > ? AND enddate <= ?))");
		queryString.append(" AND (wavelength = ?)) ORDER BY startdate");
		if (isLeft) {
			queryString.append(" DESC;");
		} else {
			queryString.append(" ASC;");
		}
		return queryString.toString();
	}

	private String buildFileQueryString(Interval period) {
		// for constructing the table names from the year and month of
		// the event
		String calStartYear = "" + period.getStart().getYear();

		String calStartMonth = String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT id, startdate FROM files_");
		queryString.append(calStartYear);
		queryString.append(calStartMonth);
		queryString.append(" WHERE(((startdate >= ? AND startdate < ?) OR (enddate > ? AND enddate <= ?))");
		queryString.append(" AND (wavelength = ?)) ORDER BY startdate");
		queryString.append(" ASC;");
		return queryString.toString();
	}

	private String buildVectorsQueryString(Interval period) {
		// for constructing the table names from the year and month of
		// the event
		String calStartYear = "" + period.getStart().getYear();

		String calStartMonth = String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT vect_id, vect_idx, val FROM image_vectors_");
		queryString.append(calStartYear);
		queryString.append(calStartMonth);
		queryString.append(" WHERE file_id = ? ;");
		return queryString.toString();
	}

	private String buildSingleVectorsQueryString(Interval period) {
		// for constructing the table names from the year and month of
		// the event
		String calStartYear = "" + period.getStart().getYear();

		String calStartMonth = String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT vect_idx, val FROM image_vectors_");
		queryString.append(calStartYear);
		queryString.append(calStartMonth);
		queryString.append(" WHERE file_id = ?");
		queryString.append(" AND vect_id = 0;");
		return queryString.toString();
	}

	private String buildQueryParamString(Interval period) {
		// for constructing the table names from the year and month of
		// the event
		String calStartYear = "" + period.getStart().getYear();

		String calStartMonth = String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT x, y, ");
		for (int i = 1; i < this.paramDim; i++) {
			queryString.append("p" + i);
			queryString.append(", ");
		}
		queryString.append("p" + this.paramDim);
		queryString.append(" FROM image_params_");
		queryString.append(calStartYear);
		queryString.append(calStartMonth);
		// between is inclusive
		queryString.append(" WHERE ( file_id = ? ) AND (x BETWEEN ? AND ? ) AND (y BETWEEN ? AND ?);");
		return queryString.toString();
	}

	@Override
	public DenseMatrix[] getImageParamForEv(ISpatialTemporalObj event, ImageDBWaveParamPair[] params, boolean leftSide)
			throws SQLException {
		// Get the image parameters for each wavelength in the set of dimensions
		DenseMatrix[] dimMatArr = new DenseMatrix[params.length];
		for (int i = 0; i < params.length; i++) {

			double[][][] paramVals = this.getImageParamForWave(event, params[i].wavelength, leftSide);
			int rows = paramVals.length;
			int cols = paramVals[0].length;

			// get the param indicated by the dims array at depth i
			// the param value is from 1 to 10 whare array index is 0-9
			// hence the -1
			int paramIdx = params[i].parameter - 1;
			double[][] data = new double[rows][cols];
			for (int x = 0; x < cols; x++) {
				for (int y = 0; y < rows; y++) {
					data[y][x] = paramVals[y][x][paramIdx];
				}
			}
			dimMatArr[i] = new JMatrix(data);
		}

		return dimMatArr;
	}

	@Override
	public ImageDBDateIdPair[] getImageIdsForInterval(Interval period, Waveband wavelength) throws SQLException {
		ImageDBDateIdPair[] result = null;
		if (period == null || wavelength == null)
			return result;

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			DateTime startDateTime = period.getStart();
			DateTime endDateTime = period.getEnd();

			ArrayList<ImageDBDateIdPair> idList = new ArrayList<ImageDBDateIdPair>();

			boolean runAgain = false;
			do {
				Interval queryPeriod = new Interval(startDateTime, endDateTime);
				String queryFileString = this.buildFileQueryString(queryPeriod);
				Timestamp startTime = new Timestamp(queryPeriod.getStartMillis());
				Timestamp endTime = new Timestamp(queryPeriod.getEndMillis());

				try (PreparedStatement file_prep_stmt = con.prepareStatement(queryFileString)) {
					file_prep_stmt.setTimestamp(1, startTime);
					file_prep_stmt.setTimestamp(2, endTime);
					file_prep_stmt.setTimestamp(3, startTime);
					file_prep_stmt.setTimestamp(4, endTime);
					file_prep_stmt.setInt(5, Utility.convertWavebandToInt(wavelength));

					try (ResultSet imgFileIdResults = file_prep_stmt.executeQuery()) {
						while (imgFileIdResults.next()) {
							int id = imgFileIdResults.getInt("id");
							Timestamp ts = imgFileIdResults.getTimestamp(2);
							ImageDBDateIdPair pair = new ImageDBDateIdPair();
							pair.id = id;
							pair.period = new Interval(ts.getTime(), ts.getTime() + (1000 * 6 * 60));
							idList.add(pair);
						}
					}
				}

				if (startDateTime.getMonthOfYear() == 12) {
					startDateTime = new DateTime(startDateTime.getYear() + 1, 1, 1, 0, 0, 0);
				} else {
					startDateTime = new DateTime(startDateTime.getYear(), startDateTime.getMonthOfYear() + 1, 1, 0, 0,
							0);
				}

				if (startDateTime.getYear() < endDateTime.getYear()) {
					runAgain = true;
				} else if (startDateTime.getYear() == endDateTime.getYear()) {
					if (startDateTime.getMonthOfYear() <= endDateTime.getMonthOfYear()) {
						runAgain = true;
					} else {
						runAgain = false;
					}
				} else {
					runAgain = false;
				}
			} while (runAgain);

			result = new ImageDBDateIdPair[idList.size()];
			for (int i = 0; i < result.length; i++)
				result[i] = idList.get(i);
		} catch (SQLException ex) {
			logger.error("SQLException occurred while executing method getImageIdsForInterval", ex);
			throw ex;

		}

		return result;
	}

	@Override
	public DenseMatrix[] getImageParamForId(Interval period, int id) throws SQLException {
		String queryParamString = this.buildQueryParamString(period);
		DenseMatrix[] dimMatArr = new DenseMatrix[10];

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			double[][][] retVal = new double[64][64][10];
			try (PreparedStatement param_prep_stmt = con.prepareStatement(queryParamString)) {
				param_prep_stmt.setInt(1, id);
				param_prep_stmt.setInt(2, (int) 1);
				param_prep_stmt.setInt(3, (int) 64);
				param_prep_stmt.setInt(4, (int) 1);
				param_prep_stmt.setInt(5, (int) 64);

				try (ResultSet imgParamRslts = param_prep_stmt.executeQuery()) {
					while (imgParamRslts.next()) {
						int x = imgParamRslts.getInt("x");
						int y = imgParamRslts.getInt("y");

						for (int i = 1; i < 11; i++) {
							retVal[y - 1][x - 1][i - 1] = imgParamRslts.getFloat("p" + i);
						}
					}
				}
			}

			if (this.normalizer != null)
				this.normalizer.normalizeParameterValues(retVal);
			int rows = retVal.length;
			int cols = retVal[0].length;

			// get the param indicated by the dims array at depth i
			// the param value is from 1 to 10 whare array index is 0-9
			// hence the -1
			for (int paramIdx = 0; paramIdx < 10; paramIdx++) {
				double[][] data = new double[rows][cols];
				for (int x = 0; x < cols; x++) {
					for (int y = 0; y < rows; y++) {
						data[y][x] = retVal[y][x][paramIdx];
					}
				}
				dimMatArr[paramIdx] = new JMatrix(data);
			}

		} catch (SQLException ex) {
			logger.error("Exception occurred while executing method getImageParamForId", ex);
			throw ex;

		}

		return dimMatArr;
	}

	@Override
	public SparseMatrix[] getImageSparseVectForId(Interval period, int id) throws SQLException {
		String queryVectString = this.buildVectorsQueryString(period);

		// setup to hold data
		ArrayList<double[][]> dataArr = new ArrayList<double[][]>();
		for (int i = 0; i < 10; i++)
			dataArr.add(new double[this.vectRows][1]);

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			try (PreparedStatement param_prep_stmt = con.prepareStatement(queryVectString)) {
				param_prep_stmt.setInt(1, id);
				try (ResultSet rs = param_prep_stmt.executeQuery()) {
					while (rs.next()) {
						int arrNum = rs.getInt(1);
						int arrIdx = rs.getInt(2);
						double val = rs.getDouble(3);
						dataArr.get(arrNum)[arrIdx][0] = val;
					}
				}
			}
		} catch (SQLException ex) {
			logger.error("Exception occurred while executing method getImageSparseVectForId", ex);
			throw ex;

		}

		SparseMatrix[] retVal = new SparseMatrix[dataArr.size()];
		for (int i = 0; i < retVal.length; i++)
			retVal[i] = new SparseMatrix(dataArr.get(i));
		return retVal;
	}

	@Override
	public SparseMatrix getFirstImageSparseVectForId(Interval period, int id) throws SQLException {
		String queryVectString = this.buildSingleVectorsQueryString(period);
		SparseMatrix retVal = null;

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			try (PreparedStatement param_prep_stmt = con.prepareStatement(queryVectString)) {
				param_prep_stmt.setInt(1, id);
				double[][] data = new double[this.vectRows][1];
				try (ResultSet rs = param_prep_stmt.executeQuery()) {
					while (rs.next()) {
						int idx = rs.getInt(1);
						double val = rs.getDouble(2);
						data[idx][0] = val;
					}
				}
				retVal = new SparseMatrix(data);
			}
		} catch (SQLException ex) {
			logger.error("Exception occurred while executing method getFirstImageSparseVectForId", ex);
			throw ex;

		}

		return retVal;
	}

	@Override
	public BufferedImage getImgForId(Interval period, int id) throws SQLException, IOException {

		BufferedImage bImg = null;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			// for pulling out the year and month for the table name to select
			// from
			String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

			String queryString = "SELECT image_file FROM image_files_" + name;

			queryString += " WHERE file_id = ? ;";
			try (PreparedStatement img_prep_stmt = con.prepareStatement(queryString)) {
				img_prep_stmt.setInt(1, id);

				try (ResultSet imgRslts = img_prep_stmt.executeQuery()) {
					if (imgRslts.next()) {
						Blob blob = imgRslts.getBlob(1);
						try (InputStream stream = blob.getBinaryStream()) {
							bImg = ImageIO.read(stream);
						}
						blob.free();
					}
				}
			}
		} catch (SQLException | IOException e) {
			logger.error("SQLException occurred while executing method getImgForId", e);
			throw e;
		}
		return bImg;
	}

	public BufferedImage getFullImgForId(Interval period, int id) throws SQLException, IOException {

		BufferedImage bImg = null;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			// for pulling out the year and month for the table name to select
			// from
			String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

			String queryString = "SELECT image_file FROM image_files_" + name;

			queryString += "_full WHERE file_id = ? ;";
			try (PreparedStatement img_prep_stmt = con.prepareStatement(queryString)) {
				img_prep_stmt.setInt(1, id);
				try (ResultSet imgRslts = img_prep_stmt.executeQuery()) {
					if (imgRslts.next()) {
						Blob blob = imgRslts.getBlob(1);
						try (InputStream stream = blob.getBinaryStream()) {
							bImg = ImageIO.read(stream);
						}
						blob.free();
					}
				}
			}
		} catch (SQLException | IOException e) {
			logger.error("Exception occurred while executing method getFullImgForId", e);
			throw e;
		}
		return bImg;
	}

	@Override
	public ImageDBFitsHeaderData getHeaderForId(Interval period, int id) throws SQLException {

		ImageDBFitsHeaderData headerData = null;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			// for pulling out the year and month for the table name to select
			// from
			String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

			String queryString = "SELECT x0, y0, rSun, dSun, cdelt, quality FROM image_header_" + name;

			queryString += " WHERE file_id = ? ;";

			try (PreparedStatement hdr_prep_stmt = con.prepareStatement(queryString)) {
				hdr_prep_stmt.setInt(1, id);
				try (ResultSet hdrRslts = hdr_prep_stmt.executeQuery()) {
					if (hdrRslts.next()) {
						headerData = new ImageDBFitsHeaderData();
						headerData.X0 = hdrRslts.getDouble(1);
						headerData.Y0 = hdrRslts.getDouble(2);
						headerData.R_SUN = hdrRslts.getDouble(3);
						headerData.DSUN = hdrRslts.getDouble(4);
						headerData.CDELT = hdrRslts.getDouble(5);
						headerData.QUALITY = hdrRslts.getInt(6);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method getHeaderForId", e);
			throw e;
		}

		return headerData;
	}

}