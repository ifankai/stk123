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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import edu.gsu.cs.dmlab.databases.interfaces.IImageDBCreator;
import edu.gsu.cs.dmlab.datatypes.ImageDBDateIdPair;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgParamNormalizer;
import edu.gsu.cs.dmlab.util.Utility;

/**
 * This class extends the {@link edu.gsu.cs.dmlab.databases.ImageDBConnection
 * ImageDBConnection} to pull images that are not in the database already, from
 * the <a href="https://api.helioviewer.org/docs/v2/">Helioviewer API</a>. It
 * then converts the downloaded image from the JP2 format that is provided by
 * the Helioviewer API to a JPEG format that is stored in the database that this
 * class is connected to.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class HelioviewerPullingAIAImageDBConnection extends ImageDBConnection {

	IImageDBCreator creator;
	HashMap<Waveband, Integer> helioviewerWave_IdMap;
	DateTimeFormatter dt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	String url = "https://api.helioviewer.org/v2/getJP2Image/";
	String charset = java.nio.charset.StandardCharsets.UTF_8.name();

	private final String USER_AGENT = "GSU DMLab Java Client 0.0.1";
	SSLContext ctx;

	static public String certificateString = "-----BEGIN CERTIFICATE-----\n"
			+ "MIICljCCAf+gAwIBAgIBADANBgkqhkiG9w0BAQ0FADBoMQswCQYDVQQGEwJ1czEL"
			+ "MAkGA1UECAwCR0ExITAfBgNVBAoMGEdlb3JnaWEgU3RhdGUgVW5pdmVyc2l0eTEZ"
			+ "MBcGA1UEAwwQZG1sYWIuY3MuZ3N1LmVkdTEOMAwGA1UECwwFRE1MYWIwHhcNMTYx"
			+ "MDI1MTk1NTA3WhcNMTkxMDI1MTk1NTA3WjBoMQswCQYDVQQGEwJ1czELMAkGA1UE"
			+ "CAwCR0ExITAfBgNVBAoMGEdlb3JnaWEgU3RhdGUgVW5pdmVyc2l0eTEZMBcGA1UE"
			+ "AwwQZG1sYWIuY3MuZ3N1LmVkdTEOMAwGA1UECwwFRE1MYWIwgZ8wDQYJKoZIhvcN"
			+ "AQEBBQADgY0AMIGJAoGBALCv3IDJ3DNV18M1wSdOqEBPwhOcRw2GLYBN/pTx1TEy"
			+ "KLVrYFVTmA3JJyrUC0CmP2JQNKDOwqDXPN8twKLtFUDshAiLRCJpCDiVJdD1vlIS"
			+ "OzNu/nfZn75FBaNN8PKYr9UDcU6EO/ix+Y1ljzxAdCOWcOrh6VtC4XpO5+XblQex"
			+ "AgMBAAGjUDBOMB0GA1UdDgQWBBRYdywzUrSkCjehtDzGc2Gqd76aaTAfBgNVHSME"
			+ "GDAWgBRYdywzUrSkCjehtDzGc2Gqd76aaTAMBgNVHRMEBTADAQH/MA0GCSqGSIb3"
			+ "DQEBDQUAA4GBAAoiX+kG80O7eBQFbPBrRyKGt9GrgQad+c9pxvp1w26Ikl9ahyBG"
			+ "xMZdGnl3CanEVET51zfau+LE/G+44+uxzqegfTiACgznzdRHIDQ6Rsw+uhS0klDl"
			+ "Ea3l2Xtrbxxfg7vg8WKaFmlHh3n+eZ0SQHcNMvrvYi/+/LQK7FO8mGo6" + "\n-----END CERTIFICATE-----";

	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// TODO Auto-generated method stub
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * Constructor that assumes default values for parameter down sample and the
	 * number of parameters for each image cell. Those values are a division by 64
	 * for all coordinates of input events, and 10 image parameters per cell
	 * location.
	 * 
	 * @param dsourc       The data source connection that is used to connect to the
	 *                     database.
	 * 
	 * @param creator      The image DB creator object used to insert images and
	 *                     metadata into the tables used to store this information.
	 * 
	 * @param normalizer   The image parameter normalizer, can be null, and if it
	 *                     is, then no normalization is performed on the parameters
	 *                     before return. Else, parameters are normalized prior to
	 *                     return using this object.
	 * 
	 * @param logger       Logger used to report errors that occurred while
	 *                     processing data requests.
	 * 
	 * @param maxCacheSize The number of input event and wavelength pairs to cache
	 *                     the image parameter cube for before replacing with LRU
	 *                     ordering.
	 */
	public HelioviewerPullingAIAImageDBConnection(DataSource dsourc, IImageDBCreator creator,
			IImgParamNormalizer normalizer, Logger logger, int maxCacheSize) {
		super(dsourc, normalizer, logger, maxCacheSize);
		if (creator == null)
			throw new IllegalArgumentException("IImageDBCreator cannot be null in IImageDBConnection constructor.");
		this.creator = creator;

		this.helioviewerWave_IdMap = new HashMap<Waveband, Integer>();
		this.helioviewerWave_IdMap.put(Waveband.AIA94, Integer.valueOf(8));
		this.helioviewerWave_IdMap.put(Waveband.AIA131, Integer.valueOf(9));
		this.helioviewerWave_IdMap.put(Waveband.AIA171, Integer.valueOf(10));
		this.helioviewerWave_IdMap.put(Waveband.AIA193, Integer.valueOf(11));
		this.helioviewerWave_IdMap.put(Waveband.AIA211, Integer.valueOf(12));
		this.helioviewerWave_IdMap.put(Waveband.AIA304, Integer.valueOf(13));
		this.helioviewerWave_IdMap.put(Waveband.AIA335, Integer.valueOf(14));
		this.helioviewerWave_IdMap.put(Waveband.AIA1600, Integer.valueOf(15));
		this.helioviewerWave_IdMap.put(Waveband.AIA1700, Integer.valueOf(16));
		// this.helioviewerWave_IdMap.put(Integer.valueOf(4500),
		// Integer.valueOf(17));

		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		try {
			TrustManager[] trustManagers = { new DefaultTrustManager() };
			this.ctx = SSLContext.getInstance("TLS");
			this.ctx.init(null, trustManagers, null);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finalize() throws Throwable {
		try {
			this.creator = null;
			this.helioviewerWave_IdMap.clear();
			this.helioviewerWave_IdMap = null;
			this.ctx = null;
		} finally {
			super.finalize();
		}
	}

	@Override
	public BufferedImage getFirstFullImage(Interval period, Waveband wavelength) throws SQLException, IOException {

		BufferedImage bImg = null;
		bImg = super.getFirstFullImage(period, wavelength);

		// If the DB does not contain the image then fetch from helioviewer
		if (bImg == null) {
			ImageDBDateIdPair[] pairs = super.getImageIdsForInterval(period, wavelength);
			if (pairs.length > 0) {
				int i = 0;
				while (bImg == null && i < pairs.length) {
					this.fillDBFromHelioviewer(pairs[i].period, wavelength, pairs[i].id);
					bImg = this.getFullImgForId(pairs[i].period, pairs[i].id);
					i++;
				}
			}
		}
		return bImg;
	}

	@Override
	public BufferedImage getFullImgForId(Interval period, int id) throws SQLException, IOException {
		Connection con = null;
		BufferedImage bImg = null;
		bImg = super.getFullImgForId(period, id);

		// If the DB does not contain the image then fetch from helioviewer
		if (bImg == null) {
			String queryFileString = this.buildFileIdQueryString(period);

			// variables for filling the database with a fetched image.
			Waveband wavelength = null;
			ImageDBDateIdPair pair = new ImageDBDateIdPair();
			boolean gotData = false;

			// try and pull the info from the database
			try {
				con = this.dsourc.getConnection();
				con.setAutoCommit(true);
				PreparedStatement file_prep_stmt = con.prepareStatement(queryFileString);
				file_prep_stmt.setInt(1, id);
				ResultSet imgFileIdResults = file_prep_stmt.executeQuery();
				if (imgFileIdResults.next()) {
					Timestamp ts = imgFileIdResults.getTimestamp(1);
					wavelength = Utility.getWavebandFromInt(imgFileIdResults.getInt(2));
					pair.id = id;
					pair.period = new Interval(ts.getTime(), ts.getTime() + (1000 * 6 * 60));
					gotData = true;
				}
				imgFileIdResults.close();
				file_prep_stmt.close();
			} finally {
				if (con != null) {
					con.close();
				}
			}

			// fill the database and return the filled instance.
			if (gotData) {
				this.fillDBFromHelioviewer(pair.period, wavelength, pair.id);
				bImg = super.getFullImgForId(pair.period, pair.id);
			}
		}
		return bImg;
	}

	private String buildFileIdQueryString(Interval period) {
		// for constructing the table names from the year and month of
		// the event
		Calendar calStart = Calendar.getInstance();
		calStart.setTimeInMillis(period.getStartMillis());
		String calStartYear = "" + calStart.get(Calendar.YEAR);
		String calStartMonth;
		if ((calStart.get(Calendar.MONTH) + 1) < 10) {
			calStartMonth = "0" + (calStart.get(Calendar.MONTH) + 1);
		} else {
			calStartMonth = "" + (calStart.get(Calendar.MONTH) + 1);
		}

		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT startdate, wavelength FROM files_");
		queryString.append(calStartYear);
		queryString.append(calStartMonth);
		queryString.append(" WHERE id = ? ;");

		return queryString.toString();
	}

	private void fillDBFromHelioviewer(Interval period, Waveband wavelength, int id) throws SQLException {

		try {
			String query = String.format("date=%s&sourceId=%s", dt.print(period.getStart()),
					URLEncoder.encode(helioviewerWave_IdMap.get(wavelength).toString(), this.charset));

			// CookieHandler.setDefault(new CookieManager(null,
			// CookiePolicy.ACCEPT_ALL));
			HttpsURLConnection connection = (HttpsURLConnection) new URL(this.url + "?" + query).openConnection();
			connection.setSSLSocketFactory(this.ctx.getSocketFactory());

			connection.setRequestProperty("Accept-Charset", this.charset);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			connection.connect();

			if (connection.getResponseCode() == 200) {

				InputStream response = connection.getInputStream();
				byte[] responseBytes = new byte[connection.getContentLength()];
				int bytesRead = response.read(responseBytes);
				int idx = bytesRead;
				while (bytesRead != -1 && idx < responseBytes.length - 1) {
					bytesRead = response.read(responseBytes, idx, responseBytes.length - idx);
					if (bytesRead != -1)
						idx += bytesRead;
				}

				response.close();
				connection.disconnect();

				// Jpeg200 files consist of chunks with 8 byte headers.
				// So, if it is not at least 8 bytes, it is invalid.
				if (idx > 7) {
					byte[] bytesReadFromResp = Arrays.copyOfRange(responseBytes, 0, idx + 1);
					responseBytes = null;
					if (checkJP2(bytesReadFromResp)) {
						InputStream imgStr = new ByteArrayInputStream(bytesReadFromResp);
						BufferedImage img = ImageIO.read(imgStr);

						if (img != null) {
							this.creator.insertImage(img, id, period);
							// System.out.println("Inserted");
						}
					} else {
						System.out.println("Failed Check.");
					}
				}
			} else {
				InputStream response = connection.getErrorStream();

				byte[] responseBytes = new byte[connection.getContentLength()];
				int bytesRead = response.read(responseBytes);
				int idx = bytesRead;
				while (bytesRead != -1 && idx < responseBytes.length - 1) {
					bytesRead = response.read(responseBytes, idx, responseBytes.length - idx);
					if (bytesRead != -1)
						idx += bytesRead;
				}
				String respStr = new String(responseBytes, StandardCharsets.UTF_8);
				System.out.println(respStr);
				response.close();
				connection.disconnect();

			}
		} catch (IOException | RuntimeException e) {
			e.printStackTrace();
		}
	}

	static final int JP2_SIGNATURE_BOX = 0x6a502020;
	static final byte JP2_CODESTREAM_MARKER_1 = (byte) 0xFF;
	static final byte JP2_CODESTREAM_MARKER_2 = (byte) 0xd9;
	static final int FILE_TYPE_BOX = 0x66747970;
	static final int FT_BR = 0x6a703220;

	private boolean checkJP2(byte[] byteBuffer) {
		// We make sure the first 12 bytes are the JP2 signature box
		int idx = 0;

		if ((idx + 4) < byteBuffer.length) {
			if (this.readInt(byteBuffer, idx) != 0x0000000c)
				return false;
			idx += 4;
		} else {
			return false;
		}

		if ((idx + 4) < byteBuffer.length) {
			if (this.readInt(byteBuffer, idx) != JP2_SIGNATURE_BOX)
				return false;
			idx += 4;
		} else {
			return false;
		}

		if ((idx + 4) < byteBuffer.length) {
			if (this.readInt(byteBuffer, idx) != 0x0d0a870a)
				return false;
			idx += 4;
		} else {
			return false;
		}

		if (byteBuffer[byteBuffer.length - 2] != JP2_CODESTREAM_MARKER_1
				|| byteBuffer[byteBuffer.length - 1] != JP2_CODESTREAM_MARKER_2) {
			return false;
		}

		return this.readFileTypeBox(byteBuffer, idx);
	}

	/**
	 * Reads a signed int (i.e., 32 bit) from the input. Prior to reading, the input
	 * should be realigned at the byte level.
	 * 
	 * @return The next byte-aligned signed int (32 bit) from the input.
	 */
	public final int readInt(byte[] byteBuffer, int pos) {
		return (((byteBuffer[pos++] & 0xFF) << 24) | ((byteBuffer[pos++] & 0xFF) << 16)
				| ((byteBuffer[pos++] & 0xFF) << 8) | (byteBuffer[pos++] & 0xFF));
	}

	final long readLong(byte[] byteBuffer, int pos) {
		return (((long) (byteBuffer[pos++] & 0xFF) << 56) | ((long) (byteBuffer[pos++] & 0xFF) << 48)
				| ((long) (byteBuffer[pos++] & 0xFF) << 40) | ((long) (byteBuffer[pos++] & 0xFF) << 32)
				| ((long) (byteBuffer[pos++] & 0xFF) << 24) | ((long) (byteBuffer[pos++] & 0xFF) << 16)
				| ((long) (byteBuffer[pos++] & 0xFF) << 8) | ((long) (byteBuffer[pos++] & 0xFF)));
	}

	/**
	 * This method reads the File Type box.
	 *
	 * @return false if the File Type box was not found or invalid else true
	 *
	 * @exception java.io.IOException  If an I/O error occurred.
	 * @exception java.io.EOFException If the end of file was reached
	 */
	public boolean readFileTypeBox(byte[] byteBuffer, int pos) {
		int length;

		int nComp;
		boolean foundComp = false;

		// Read box length (LBox)
		length = this.readInt(byteBuffer, pos);
		if (length == 0) { // This can not be last box
			System.out.println("Zero-length of Profile Box");
			return false;
		}
		pos += 4;

		// Check that this is a File Type box (TBox)
		if (this.readInt(byteBuffer, pos) != FILE_TYPE_BOX) {
			System.out.println("Bad File Type Box");
			return false;
		}
		pos += 4;

		// Check for XLBox
		if (length == 1) { // Box has 8 byte length;
			System.out.println("File Too Big");
			return false;
		}

		// Read Brand field
		// in.readInt();
		pos += 4;

		// Read MinV field
		// in.readInt();
		pos += 4;

		// Check that there is at least one FT_BR entry in in
		// compatibility list
		nComp = (length - 16) / 4; // Number of compatibilities.
		for (int i = nComp; i > 0; i--) {
			if (this.readInt(byteBuffer, pos) == FT_BR)
				foundComp = true;
			pos += 4;
		}
		if (!foundComp) {
			System.out.println("No FT_BR entry.");

			return false;
		}

		return true;
	}
}
