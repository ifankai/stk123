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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.sql.DataSource;

import org.joda.time.Interval;
import org.slf4j.Logger;

import edu.gsu.cs.dmlab.databases.interfaces.IImageDBCreator;
import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.util.Utility;
import smile.math.matrix.SparseMatrix;

/**
 * This class is used to insert various values into the database used for
 * storing images, image parameters, sparse descriptors for images, and some
 * additional header data for images.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ImageDBCreator implements IImageDBCreator {

	DataSource dsourc;
	int correctParamRowCount = 64 * 64;
	int numParams = 10;
	private Logger logger;

	public ImageDBCreator(DataSource dsourc, Logger logger) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in ImageDBCreator  constructor");
		if (logger == null)
			throw new IllegalArgumentException("logger cannot be null in ImageDBCreator  constructor");
	}

	public ImageDBCreator(DataSource dsourc, int correctParamRowCount, int numParams, Logger logger) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in ImageDBCreator constructor.");
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null in ImageDBCreator constructor.");
		if (correctParamRowCount == 0)
			throw new IllegalArgumentException("correctParamRowCount cannot be 0 in ImageDBCreator constructor.");
		if (numParams == 0)
			throw new IllegalArgumentException("numParams cannot be 0 in ImageDBCreator constructor.");
		this.dsourc = dsourc;
		this.correctParamRowCount = correctParamRowCount;
		this.numParams = numParams;
		this.logger = logger;
	}

	@Override
	public void finalize() throws Throwable {
		this.dsourc = null;
	}

	@Override
	public boolean insertFileDescriptTables(Interval period) throws SQLException {

		boolean done = false;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			// Check for file description table and create if not there.
			String query = this.queryTableExistsFiles(period);
			try (PreparedStatement tableExistsPrepStmt = con.prepareStatement(query)) {
				try (ResultSet res = tableExistsPrepStmt.executeQuery()) {
					if (!res.next()) {
						query = this.createFilesTableString(period);
						try (PreparedStatement tableExistsPrepStmt2 = con.prepareStatement(query)) {
							tableExistsPrepStmt2.execute();
						}
					}
				}
			}

			// Check for Image table and create if not there.
			query = this.queryTableExistsImage(period);
			try (PreparedStatement tableExistsPrepStmt = con.prepareStatement(query)) {
				try (ResultSet res = tableExistsPrepStmt.executeQuery()) {
					if (!res.next()) {
						query = this.createImageTableString(period);
						try (PreparedStatement tableExistsPrepStmt2 = con.prepareStatement(query)) {
							tableExistsPrepStmt2.execute();
						}
					}
				}
			}

			// Check for FullImage table and create if not there.
			query = this.queryTableExistsImageFull(period);
			try (PreparedStatement tableExistsPrepStmt = con.prepareStatement(query)) {
				try (ResultSet res = tableExistsPrepStmt.executeQuery()) {
					if (!res.next()) {
						query = this.createFullImageTableString(period);
						try (PreparedStatement tableExistsPrepStmt2 = con.prepareStatement(query)) {
							tableExistsPrepStmt2.execute();
						}
					}
				}
			}
			// Check for Parameter table and create if not there.
			query = this.queryTableExistsParams(period);
			try (PreparedStatement tableExistsPrepStmt = con.prepareStatement(query)) {
				try (ResultSet res = tableExistsPrepStmt.executeQuery()) {
					if (!res.next()) {
						query = this.createParamsTableString(period);
						try (PreparedStatement tableExistsPrepStmt2 = con.prepareStatement(query)) {
							tableExistsPrepStmt2.execute();
						}
					}
				}
			}

			// Check for Vectors table and create if not there.
			// query = this.queryTableExistsVect(period);
			// tableExistsPrepStmt = con.prepareStatement(query);
			// res = tableExistsPrepStmt.executeQuery();
			// if (!res.next()) {
			// query = this.createVectorTableString(period);
			// tableExistsPrepStmt = con.prepareStatement(query);
			// tableExistsPrepStmt.execute();
			// }

			// check for header table and create if not there
			query = this.queryTableExistsHeader(period);
			try (PreparedStatement tableExistsPrepStmt = con.prepareStatement(query)) {
				try (ResultSet res = tableExistsPrepStmt.executeQuery()) {
					if (!res.next()) {
						query = this.createHeaderTableString(period);
						try (PreparedStatement tableExistsPrepStmt2 = con.prepareStatement(query)) {
							tableExistsPrepStmt2.execute();
						}
					}
				}
			}

			done = true;

		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method insertFileDescriptTables", e);
			throw e;
		}

		return done;
	}

	@Override
	public int insertFileDescript(Waveband wavelength, Interval period) throws SQLException {

		int result = -1;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String query = this.insertFileDescriptQuery(period);
			try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				Timestamp startTime = new Timestamp(period.getStartMillis());
				Timestamp endTime = new Timestamp(period.getEndMillis());
				stmt.setTimestamp(1, startTime);
				stmt.setTimestamp(2, endTime);
				stmt.setInt(3, Utility.convertWavebandToInt(wavelength));
				int affectedRows = stmt.executeUpdate();

				if (affectedRows == 0)
					throw new SQLException("Insert file descriptor failed, no rows affected.");

				try (ResultSet res = stmt.getGeneratedKeys()) {
					if (res.next()) {
						result = res.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while exeuting method insertFileDescript", e);
			throw e;
		}

		return result;
	}

	@Override
	public void insertImage(BufferedImage file, int id, Interval period) throws SQLException, IOException {

		// downsample the image
		BufferedImage img = new BufferedImage(2048, 2048, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = img.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance(.5, .5);
		g.drawRenderedImage(file, at);
		g.dispose();
		at = null;
		g = null;

		// downsample the image
		BufferedImage img2 = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2 = img2.createGraphics();
		AffineTransform at2 = AffineTransform.getScaleInstance(.0625, .0625);
		g2.drawRenderedImage(file, at2);
		g2.dispose();
		at2 = null;
		g2 = null;

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			// Inserting the 2k image
			{
				String query = this.insertFullImageFileQuery(period);
				try (PreparedStatement stmt = con.prepareStatement(query)) {
					try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
						try (ImageOutputStream out = new MemoryCacheImageOutputStream(os)) {

							// get an image writer for jpg images
							ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
							writer.setOutput(out);

							// set the parameter for compression quality
							ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
							iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
							iwparam.setCompressionQuality((float) 0.90);

							// write the image to the output stream
							writer.write(null, new IIOImage(img, null, null), iwparam);

							// get the input stream for inserting into the database.
							try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {

								// set the query parameters and execute the image insert.
								stmt.setInt(1, id);
								stmt.setBinaryStream(2, is, os.size());
								int res = stmt.executeUpdate();

								if (res == 0)
									throw new SQLException("Insert blob failed, no rows affected.");
							}
						}
					}
				} finally {
					img.flush();
					img = null;
				}
			}

			// Inserting the 256 image
			{
				String query = this.insertImageFileQuery(period);
				try (PreparedStatement stmt = con.prepareStatement(query)) {
					try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
						try (ImageOutputStream out = new MemoryCacheImageOutputStream(os)) {

							// get an image writer for jpg images
							ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
							writer.setOutput(out);

							// set the parameter for compression quality
							ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
							iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
							iwparam.setCompressionQuality((float) 0.90);

							// write the image to the output stream
							writer.write(null, new IIOImage(img2, null, null), iwparam);

							// get the input stream for inserting into the database.
							try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
								// set the query parameters and execute the image insert.
								stmt.setInt(1, id);
								stmt.setBinaryStream(2, is, os.size());
								int res = stmt.executeUpdate();

								if (res == 0)
									throw new SQLException("Insert blob failed, no rows affected.");
							}
						}
					}
				} finally {
					img2.flush();
					img2 = null;
				}
			}
		} catch (SQLException | IOException e) {
			logger.error("Exception occurred while executing method insertImage ", e);
			throw e;
		}
	}

	@Override
	public void insertParams(double[][][] params, int id, Interval period) throws SQLException {

		if (!this.checkParamsExist(id, period)) {

			try (Connection con = this.dsourc.getConnection()) {
				con.setAutoCommit(true);
				String query = this.insertImageParamQuery(period);
				try (PreparedStatement stmt = con.prepareStatement(query)) {
					for (int y = 0; y < params.length; y++) {
						for (int x = 0; x < params[y].length; x++) {
							stmt.setInt(1, id);
							stmt.setInt(2, x + 1);
							stmt.setInt(3, y + 1);

							for (int i = 0; i < params[y][x].length; i++) {
								stmt.setFloat(4 + i, ((float) params[y][x][i]));
							}
							stmt.addBatch();
						}
					}

					int[] res = stmt.executeBatch();
					for (int i = 0; i < res.length; i++)
						if (res[i] == 0)
							throw new SQLException("Insert params failed, no rows affected.");
				}
			} catch (SQLException e) {
				logger.error("SQLException occurred while executing method insertParams", e);
				throw e;
			}
		}
	}

	@Override
	public void insertImageSparseVect(SparseMatrix[] vectors, int id, Interval period) throws SQLException {

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			String query = this.insertVectorsQuery(period);
			try (PreparedStatement stmt = con.prepareStatement(query)) {
				boolean hasNonZero = false;
				for (int vectId = 0; vectId < vectors.length; vectId++) {
					SparseMatrix currMat = vectors[vectId];
					for (int i = 0; i < currMat.nrows(); i++) {
						double val = currMat.get(i, 0);
						if (val != 0.0) {
							hasNonZero = true;
							stmt.setInt(1, id);
							stmt.setInt(2, vectId);
							stmt.setInt(3, i);
							stmt.setFloat(4, (float) val);
							stmt.addBatch();
						}
					}
				}

				if (hasNonZero) {
					int[] res = stmt.executeBatch();
					for (int i = 0; i < res.length; i++)
						if (res[i] == 0)
							throw new SQLException("Insert params failed, no rows affected.");
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method insertImageSparseVect", e);
			throw e;
		}
	}

	@Override
	public void insertHeader(ImageDBFitsHeaderData header, int id, Interval period) throws SQLException {

		if (header != null) {
			try (Connection con = this.dsourc.getConnection()) {
				con.setAutoCommit(true);
				String query = this.insertHeaderQuery(period);
				try (PreparedStatement stmt = con.prepareStatement(query)) {

					stmt.setInt(1, id);
					stmt.setDouble(2, header.X0);
					stmt.setDouble(3, header.Y0);
					stmt.setDouble(4, header.R_SUN);
					stmt.setDouble(5, header.DSUN);
					stmt.setDouble(6, header.CDELT);
					stmt.setInt(7, header.QUALITY);

					int res = stmt.executeUpdate();

					if (res == 0)
						throw new SQLException("Insert header failed, no rows affected.");
				}
			} catch (SQLException e) {
				logger.error("SQLException occurred while executing method insertHeader", e);
				throw e;
			}
		}
	}

	@Override
	public boolean checkParamsExist(int id, Interval period) throws SQLException {

		boolean exists = false;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String query = this.checkImageParamQuery(period);
			try (PreparedStatement stmt = con.prepareStatement(query)) {
				stmt.setInt(1, id);
				try (ResultSet results = stmt.executeQuery()) {

					if (results.next()) {

						int numRows = results.getInt(1);

						if (numRows == this.correctParamRowCount) {
							exists = true;
						} else {
							query = this.deleteImageParamQuery(period);
							try (PreparedStatement stmt2 = con.prepareStatement(query)) {
								stmt2.setInt(1, id);
								stmt2.executeUpdate();
							}
						}
					} else {
						query = this.deleteImageParamQuery(period);
						try (PreparedStatement stmt2 = con.prepareStatement(query)) {
							stmt2.setInt(1, id);
							stmt2.executeUpdate();
						}
					}
				}

			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method checkParamsExist", e);
			throw e;
		}

		return exists;

	}

	@Override
	public boolean checkImagesExist(int id, Interval period) throws SQLException {

		boolean exists = false;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			// Check for low resolution image
			String query = this.checkImagesQuery(period);
			boolean needsCleaned = false;

			try (PreparedStatement stmt = con.prepareStatement(query)) {
				stmt.setInt(1, id);
				try (ResultSet results = stmt.executeQuery()) {
					if (results.next()) {
						// Check for high resolution image
						query = this.checkFullImagesQuery(period);
						try (PreparedStatement stmt2 = con.prepareStatement(query)) {
							stmt2.setInt(1, id);
							try (ResultSet results2 = stmt2.executeQuery()) {
								if (!results2.next()) {
									needsCleaned = true;
								}
							}
						}
					} else {
						needsCleaned = true;
					}
				}
			}

			if (needsCleaned) {
				// Just assume they both exist, even though one missing is how we got here.
				// Delete low resolution image
				query = this.deleteImageQuery(period);
				try (PreparedStatement stmt = con.prepareStatement(query)) {
					stmt.setInt(1, id);
					stmt.executeUpdate();
				}

				// Delete high resolution image
				query = this.deleteFullImageQuery(period);
				try (PreparedStatement stmt = con.prepareStatement(query)) {
					stmt.setInt(1, id);
					stmt.executeUpdate();
				}

			} else {
				exists = true;
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while executing method checkImagesExist", e);
			throw e;
		}
		return exists;
	}

	private String deleteImageQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM image_files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append(" WHERE file_id = ?;");

		return sb.toString();
	}

	private String deleteFullImageQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM image_files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("_full");

		sb.append(" WHERE file_id = ?;");

		return sb.toString();
	}

	private String deleteImageParamQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM image_params_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append(" WHERE file_id = ?;");

		return sb.toString();
	}

	private String checkImagesQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT 1 FROM image_files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append(" WHERE file_id = ?;");

		return sb.toString();
	}

	private String checkFullImagesQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT 1 FROM image_files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append("_full");

		sb.append(" WHERE file_id = ?;");

		return sb.toString();
	}

	private String checkImageParamQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(*) FROM image_params_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append(" WHERE file_id = ?;");

		return sb.toString();
	}

	private String insertFileDescriptQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append(" (startdate, enddate, wavelength) VALUES(?,?,?);");
		return sb.toString();
	}

	private String insertImageFileQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO image_files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append(" VALUES( ?, ? );");
		return sb.toString();
	}

	private String insertFullImageFileQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO image_files_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append("_full VALUES( ?, ? );");
		return sb.toString();
	}

	private String insertVectorsQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO image_vectors_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append(" VALUES(?,?,?,?);");
		return sb.toString();
	}

	private String insertImageParamQuery(Interval period) {
		Calendar calStart = Calendar.getInstance();
		calStart.setTimeInMillis(period.getStartMillis());

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO image_params_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append(String.format(" (file_id,x,y,"));
		for (int i = 1; i < this.numParams; i++) {
			sb.append("p" + i + ",");
		}
		sb.append("p" + this.numParams);
		sb.append(") ");
		sb.append(" VALUES(?,?,?,");
		for (int i = 1; i < this.numParams; i++) {
			sb.append("?,");
		}
		sb.append("?);");
		return sb.toString();
	}

	private String insertHeaderQuery(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO image_header_");
		sb.append((period.getStart().getYear()));
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));

		sb.append(" VALUES(?,?,?,?,?,?,?);");
		return sb.toString();
	}

	private String queryTableExistsFiles(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE 'files_");
		sb.append(period.getStart().getYear());
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("';");
		return sb.toString();
	}

	private String queryTableExistsHeader(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE 'image_header_");
		sb.append(period.getStart().getYear());
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("';");
		return sb.toString();
	}

	private String queryTableExistsImage(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE 'image_files_");
		sb.append(period.getStart().getYear());
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("';");
		return sb.toString();
	}

	private String queryTableExistsImageFull(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE 'image_files_");
		sb.append(period.getStart().getYear());
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("_full';");
		return sb.toString();

	}

	private String queryTableExistsParams(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE 'image_params_");
		sb.append(period.getStart().getYear());
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("';");
		return sb.toString();
	}

	private String queryTableExistsVect(Interval period) {

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE 'image_vectors_");
		sb.append(period.getStart().getYear());
		sb.append(String.format("%02d", period.getStart().getMonthOfYear()));
		sb.append("';");
		return sb.toString();
	}

	private String createFilesTableString(Interval period) {

		String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `files_");
		sb.append(name);
		sb.append("` (");
		sb.append("`id` int(10) unsigned NOT NULL AUTO_INCREMENT, ");
		sb.append("`startdate` datetime NOT NULL, ");
		sb.append("`enddate` datetime NOT NULL, ");
		sb.append("`wavelength` smallint(5) unsigned NOT NULL, ");
		sb.append("PRIMARY KEY (`id`), ");
		sb.append("KEY `startdate_wave_");
		sb.append(name);
		sb.append("_idx` (`startdate`,`wavelength`), ");
		sb.append("KEY `enddate_wave_");
		sb.append(name);
		sb.append("_idx` (`enddate`,`wavelength`) ");
		sb.append(") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;");
		return sb.toString();
	}

	private String createHeaderTableString(Interval period) {

		String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `image_header_");
		sb.append(name);
		sb.append("` (");
		sb.append("`file_id` int(10) unsigned NOT NULL, ");
		sb.append("`x0` real(14,8) NOT NULL, ");
		sb.append("`y0` real(14,8) NOT NULL, ");
		sb.append("`rSun` real(14,8) NOT NULL, ");
		sb.append("`dSun` real(14,1) NOT NULL, ");
		sb.append("`cdelt` real(14,8) NOT NULL, ");
		sb.append("`quality` int(11) NOT NULL, ");
		sb.append("PRIMARY KEY (`file_id`), ");
		sb.append("CONSTRAINT `header_id_fk_");
		sb.append(name);
		sb.append("` FOREIGN KEY (`file_id`) REFERENCES `files_");
		sb.append(name);
		sb.append("` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION ");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}

	private String createParamsTableString(Interval period) {

		String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `image_params_");
		sb.append(name);
		sb.append("` (");
		sb.append("`file_id` int(10) unsigned NOT NULL, ");
		sb.append("`x` tinyint(3) unsigned NOT NULL, ");
		sb.append("`y` tinyint(3) unsigned NOT NULL, ");

		for (int i = 0; i < this.numParams; i++) {
			sb.append("`p" + (i + 1) + "` float(9,4) NOT NULL, ");
		}

		sb.append("PRIMARY KEY (`file_id`,`x`,`y`), ");
		sb.append("KEY `image_param_id_idx");
		sb.append(name);
		sb.append("` (`file_id`), ");
		sb.append("CONSTRAINT `image_params_id_fk_");
		sb.append(name);
		sb.append("` FOREIGN KEY (`file_id`) REFERENCES `files_");
		sb.append(name);
		sb.append("` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION ");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}

	private String createImageTableString(Interval period) {

		String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `image_files_");
		sb.append(name);
		sb.append("` ( ");
		sb.append("`file_id` int(10) unsigned NOT NULL, ");
		sb.append("`image_file` blob NOT NULL, ");
		sb.append("PRIMARY KEY (`file_id`), ");
		sb.append("CONSTRAINT `files_id_fk_");
		sb.append(name);
		sb.append("` FOREIGN KEY (`file_id`) REFERENCES `files_");
		sb.append(name);
		sb.append("` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION ");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}

	private String createFullImageTableString(Interval period) {

		String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `image_files_");
		sb.append(name);
		sb.append("_full` ( ");
		sb.append("`file_id` int(10) unsigned NOT NULL, ");
		sb.append("`image_file` mediumblob NOT NULL, ");
		sb.append("PRIMARY KEY (`file_id`), ");
		sb.append("CONSTRAINT `files_id_fk_");
		sb.append(name);
		sb.append("_full` FOREIGN KEY (`file_id`) REFERENCES `files_");
		sb.append(name);
		sb.append("` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION ");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}

	private String createVectorTableString(Interval period) {

		String name = period.getStart().getYear() + String.format("%02d", period.getStart().getMonthOfYear());

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `image_vectors_");
		sb.append(name);
		sb.append("` (");
		sb.append("`file_id` int(10) unsigned NOT NULL, ");
		sb.append("`vect_id` int(3) unsigned NOT NULL, ");
		sb.append("`vect_idx` int(4) unsigned NOT NULL, ");
		sb.append("`val`  REAL(12,6) NOT NULL, ");
		sb.append("PRIMARY KEY (`file_id`,`vect_id`, `vect_idx` ), ");
		sb.append("KEY `image_vect_id_idx");
		sb.append(name);
		sb.append("` (`file_id`), ");
		sb.append("CONSTRAINT `image_vect_id_fk_");
		sb.append(name);
		sb.append("` FOREIGN KEY (`file_id`) REFERENCES `files_");
		sb.append(name);
		sb.append("` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION ");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}

}
