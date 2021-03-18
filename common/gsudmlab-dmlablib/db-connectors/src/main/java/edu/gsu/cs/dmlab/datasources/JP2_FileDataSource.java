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
package edu.gsu.cs.dmlab.datasources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import edu.gsu.cs.dmlab.datasources.interfaces.IImageDataSource;
import edu.gsu.cs.dmlab.datatypes.ImageDBFitsHeaderData;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.exceptions.ExternalDatasourceException;
import edu.gsu.cs.dmlab.util.Utility;

/**
 * This class is intended to read images from disk that are from the
 * <a href="https://api.helioviewer.org/docs/v2/">Helioviewer API</a>. These are
 * assumed to be stored in a year/month/day/wavelength directory structure.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class JP2_FileDataSource implements IImageDataSource {

	private LoadingCache<CacheKey, NavigableSet<TimeFilePair>> cache = null;
	private HashFunction hashFunct = Hashing.murmur3_128();
	private int cadenceMin;
	private String base;

	private Logger logger = null;

	private Comparator<TimeFilePair> comp = new Comparator<TimeFilePair>() {
		public int compare(TimeFilePair p1, TimeFilePair p2) {
			return p1.timeOfFile.compareTo(p2.timeOfFile);
		}
	};

	private class CacheKey {
		String key;
		int hashInt;

		public CacheKey(String directory, HashFunction hashFunct) {
			this.key = directory;
			this.hashInt = hashFunct.newHasher().putString(this.key, Charsets.UTF_8).hash().asInt();
		}

		public String getKey() {
			return this.key;
		}

		@Override
		public int hashCode() {
			return this.hashInt;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CacheKey) {
				CacheKey val = (CacheKey) obj;
				return val.getKey().equals(this.key);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return this.key;
		}
	}

	private class TimeFilePair {
		public DateTime timeOfFile = null;
		public Path path = null;
	}

	/**
	 * Constructor builds a JP2 file reader that requires the returned image to be
	 * within as set number of minutes of the queried time. The object uses a cache
	 * on the list of image files in a directory on disk that correspond to a
	 * particular day. If the list of files in a directory are changing on disk, say
	 * as more are added to the directory as new observations are recored, then this
	 * cache needs to be purged of that list and updated with the new set of files
	 * in that directory. To do this the cache is set to purge that list after a
	 * number of minutes of that list not being accessed. The images are expected to
	 * be relative to the passed in base directory in a year/month/day/wavelength
	 * directory structure.
	 * 
	 * @param baseDir       The base directory to start the search from.
	 * 
	 * @param maxCacheSize  The maximum number of directories to cache the list of
	 *                      files for, this speeds up searching for files that match
	 *                      the date because the list doesn't need to be rebult each
	 *                      time.
	 * 
	 * @param cadenceMin    The allowed number of minutes difference between the
	 *                      query time and the returned image time.
	 * 
	 * @param expireMinutes The number of minutes of not accessing a list of files
	 *                      available for a day before that list is purged from the
	 *                      cache and will need to be pulled from disk again.
	 */
	public JP2_FileDataSource(String baseDir, int maxCacheSize, int cadenceMin, int expireMinutes) {
		this.base = baseDir;
		this.cadenceMin = cadenceMin;
		this.cache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.expireAfterAccess(Duration.of(expireMinutes, ChronoUnit.MINUTES))
				.build(new CacheLoader<CacheKey, NavigableSet<TimeFilePair>>() {
					public NavigableSet<TimeFilePair> load(CacheKey key) {
						return getDirContents(key);
					}
				});
	}

	/**
	 * Constructor builds a JP2 file reader that requires the returned image to be
	 * within as set number of minutes of the queried time. The object uses a cache
	 * on the list of image files in a directory on disk that correspond to a
	 * particular day. If the list of files in a directory are changing on disk, say
	 * as more are added to the directory as new observations are recored, then this
	 * cache needs to be purged of that list and updated with the new set of files
	 * in that directory. To do this the cache is set to purge that list after a
	 * number of minutes of that list not being accessed. The images are expected to
	 * be relative to the passed in base directory in a year/month/day/wavelength
	 * directory structure.
	 * 
	 * @param baseDir       The base directory to start the search from.
	 * 
	 * @param maxCacheSize  The maximum number of directories to cache the list of
	 *                      files for, this speeds up searching for files that match
	 *                      the date because the list doesn't need to be rebult each
	 *                      time.
	 * 
	 * @param cadenceMin    The allowed number of minutes difference between the
	 *                      query time and the returned image time.
	 * 
	 * @param expireMinutes The number of minutes of not accessing a list of files
	 *                      available for a day before that list is purged from the
	 *                      cache and will need to be pulled from disk again.
	 * 
	 * @param logger        A logging mechanism so we can see what is going on
	 */
	public JP2_FileDataSource(String baseDir, int maxCacheSize, int cadenceMin, int expireMinutes, Logger logger) {
		this.base = baseDir;
		this.cadenceMin = cadenceMin;
		this.logger = logger;

		this.cache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.expireAfterAccess(Duration.of(expireMinutes, ChronoUnit.MINUTES))
				.build(new CacheLoader<CacheKey, NavigableSet<TimeFilePair>>() {
					public NavigableSet<TimeFilePair> load(CacheKey key) {
						return getDirContents(key);
					}
				});
	}

	private NavigableSet<TimeFilePair> getDirContents(CacheKey key) {
		NavigableSet<TimeFilePair> result = new TreeSet<TimeFilePair>(this.comp);

		Path dir = Paths.get(key.getKey());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jp2")) {
			for (Path p : stream) {
				String[] dParts = p.toString().split("/");
				String[] fParts = dParts[dParts.length - 1].split("_");
				dParts = null;

				DateTime timeOfFile = new DateTime(Integer.parseInt(fParts[0]), Integer.parseInt(fParts[1]),
						Integer.parseInt(fParts[2]), Integer.parseInt(fParts[4]), Integer.parseInt(fParts[5]));

				TimeFilePair pair = new TimeFilePair();
				pair.path = p;
				pair.timeOfFile = timeOfFile;
				result.add(pair);
			}
		} catch (IOException e) {
			if (this.logger != null) {
				this.logger.error("Error while attempting to open directory: " + dir.toString(), e);
			}
			// Do nothing, we will assume the directory is empty.
		}

		return result;
	}

	private File getClosestFile(DateTime timeOfFile, Waveband wavelength) {

		StringBuilder sb = new StringBuilder();
		sb.append(this.base);
		sb.append(File.separator);
		sb.append(timeOfFile.getYear());
		sb.append(File.separator);
		String month = String.format("%02d", timeOfFile.getMonthOfYear());

		sb.append(month);
		sb.append(File.separator);
		String day = String.format("%02d", timeOfFile.getDayOfMonth());

		sb.append(day);
		sb.append(File.separator);
		sb.append(Utility.convertWavebandToInt(wavelength));

		CacheKey key = new CacheKey(sb.toString(), this.hashFunct);
		NavigableSet<TimeFilePair> set = this.cache.getUnchecked(key);

		TimeFilePair p = new TimeFilePair();
		p.timeOfFile = timeOfFile;
		p = set.floor(p);

		if (p != null) {
			int diff = 0;
			if (timeOfFile.isBefore(p.timeOfFile)) {
				diff = Minutes.minutesBetween(timeOfFile, p.timeOfFile).getMinutes();
			} else {
				diff = Minutes.minutesBetween(p.timeOfFile, timeOfFile).getMinutes();
			}

			if (Math.abs(diff) <= this.cadenceMin) {
				File file = p.path.toFile();
				return file;
			}
		}

		return null;

	}

	@Override
	public BufferedImage getImage(DateTime date, Waveband wavelength) {

		BufferedImage results = null;
		try {
			File file = this.getClosestFile(date, wavelength);
			if (file != null) {
				if (file != null) {

					byte[] responseBytes = null;
					try (InputStream response = new FileInputStream(file)) {
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							byte[] buffer = new byte[1024];
							int length = 0;

							while ((length = response.read(buffer)) != -1) {
								baos.write(buffer, 0, length);
							}
							responseBytes = baos.toByteArray();
						}
					}

					if (responseBytes != null && responseBytes.length > 7) {
						if (checkJP2(responseBytes)) {
							try (InputStream imgStr = new ByteArrayInputStream(responseBytes)) {
								BufferedImage img = ImageIO.read(imgStr);
								results = img;
							}
						} else {
							throw new ExternalDatasourceException("File failed JP2 Check.");
						}
					}
				}
			}
		} catch (IOException e) {
			if (this.logger != null) {
				this.logger.error("IOError while attempting to read image at: " + date.toString() + ", "
						+ Utility.convertWavebandToInt(wavelength), e);
			}
		}
		return results;
	}

	@Override
	public ImageDBFitsHeaderData getHeader(DateTime date, Waveband wavelength) {

		ImageDBFitsHeaderData results = null;
		try {
			File file = this.getClosestFile(date, wavelength);
			if (file != null) {
				try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
					Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

					if (readers.hasNext()) {
						// pick the first available ImageReader
						ImageReader reader = readers.next();

						// attach source to the reader
						reader.setInput(iis, true);

						// read metadata of first image
						IIOMetadata metadata = reader.getImageMetadata(0);
						results = this.getHeaderData(metadata);
					}
				}
			}
		} catch (IOException e) {
			if (this.logger != null) {
				this.logger.error("Error while attempting to read image header for: " + date.toString() + ", "
						+ Utility.convertWavebandToInt(wavelength), e);
			}
		}
		return results;
	}

	@Override
	public File getImageFile(DateTime date, Waveband wavelength) {
		return this.getClosestFile(date, wavelength);
	}

	ImageDBFitsHeaderData getHeaderData(IIOMetadata metadata) {
		String[] names = metadata.getMetadataFormatNames();
		int length = names.length;

		Document doc = null;
		for (int i = 0; i < length; i++) {
			if (names[i].contains("javax_imageio"))
				doc = this.getFitsDoc(metadata.getAsTree(names[i]));
		}

		ImageDBFitsHeaderData header = null;
		if (doc != null) {
			Element root = doc.getDocumentElement();
			NodeList ndLst = root.getChildNodes();

			// Get the fits data node from the list children of the root of the xml document
			// Then get the children of that node
			NodeList fitsList = null;
			for (int i = 0; i < ndLst.getLength(); i++) {
				Node nde = ndLst.item(i);
				if (nde.getNodeType() == Node.ELEMENT_NODE) {
					String ndName = nde.getNodeName();
					switch (ndName) {
					case "fits":
						fitsList = nde.getChildNodes();
						break;
					}
				}
			}

			// If we found the children of the fits node, then process
			// them to populate the header data.
			if (fitsList != null) {
				header = new ImageDBFitsHeaderData();
				for (int i = 0; i < fitsList.getLength(); i++) {
					Node nde = fitsList.item(i);
					if (nde.getNodeType() == Node.ELEMENT_NODE) {
						String ndName = nde.getNodeName();

						switch (ndName) {
						case "X0_MP":
							header.X0 = Double.parseDouble(nde.getTextContent());
							break;
						case "Y0_MP":
							header.Y0 = Double.parseDouble(nde.getTextContent());
							break;
						case "DSUN_OBS":
							header.DSUN = Double.parseDouble(nde.getTextContent());
							break;
						case "R_SUN":
							header.R_SUN = Double.parseDouble(nde.getTextContent());
							break;
						case "CDELT1":
							header.CDELT = Double.parseDouble(nde.getTextContent());
							break;
						case "QUALITY":
							header.QUALITY = Integer.parseInt(nde.getTextContent());
						}
					}
				}
			}
		}

		return header;
	}

	Document getFitsDoc(Node metadata) {
		Document doc = null;

		// get the child nodes of the metadata node
		NodeList ndLst = metadata.getChildNodes();
		NodeList txtNdList = null;

		// Then find the text node and get its children
		for (int i = 0; i < ndLst.getLength(); i++) {
			Node nde = ndLst.item(i);
			if (nde.getNodeType() == Node.ELEMENT_NODE) {
				String ndName = nde.getNodeName();
				switch (ndName) {
				case "Text":
					txtNdList = nde.getChildNodes();
					break;
				}

			}
		}

		// If we found the text node chlidrent, then process them
		if (txtNdList != null) {

			// We want to find the text entry node in the text node.
			// Then get the attribute map from it.
			NamedNodeMap map = null;
			for (int i = 0; i < txtNdList.getLength(); i++) {
				Node nde = txtNdList.item(i);
				if (nde.getNodeType() == Node.ELEMENT_NODE) {
					String ndName = nde.getNodeName();
					switch (ndName) {
					case "TextEntry":
						map = nde.getAttributes();
						break;
					}
				}
			}

			// If we found the map, then process it to get the
			// FITS header XML document.
			if (map != null) {
				String attribText = null;
				for (int i = 0; i < map.getLength(); i++) {
					Node attr = map.item(i);
					if (attr.getNodeType() == Node.ATTRIBUTE_NODE) {
						String ndName = attr.getNodeName();
						switch (ndName) {
						case "value":
							attribText = attr.getNodeValue();
							break;
						}
					}
				}

				// Parse the XML Fits header document if we found it.
				if (attribText != null) {
					try {
						DocumentBuilderFactory fctry = DocumentBuilderFactory.newInstance();
						DocumentBuilder bldr = fctry.newDocumentBuilder();
						try (InputStream is = new ByteArrayInputStream(attribText.trim().getBytes())) {
							doc = bldr.parse(is);
						}
					} catch (Exception e) {
						if (this.logger != null) {
							this.logger.error("Error while attempting to parse XML from JP2 header.", e);
						}
					}
				}

			}
		}

		return doc;
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
			// System.out.println("Zero-length of Profile Box");
			return false;
		}
		pos += 4;

		// Check that this is a File Type box (TBox)
		if (this.readInt(byteBuffer, pos) != FILE_TYPE_BOX) {
			// System.out.println("Bad File Type Box");
			return false;
		}
		pos += 4;

		// Check for XLBox
		if (length == 1) { // Box has 8 byte length;
			// System.out.println("File Too Big");
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
			// System.out.println("No FT_BR entry.");

			return false;
		}

		return true;
	}

}
