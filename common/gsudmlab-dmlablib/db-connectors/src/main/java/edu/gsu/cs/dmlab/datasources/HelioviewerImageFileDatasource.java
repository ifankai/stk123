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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import edu.gsu.cs.dmlab.datasources.interfaces.IImageFileDataSource;
import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.util.Utility;

/**
 * This class is intended to pull images from the
 * <a href="https://api.helioviewer.org/docs/v2/">Helioviewer API</a>. These
 * images are returned in a raw byte array, which is meant to be either read as
 * a JP2 image or saved to disk as a JP2 image file.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class HelioviewerImageFileDatasource implements IImageFileDataSource {

	private String baseAddress = "https://helioviewer.org/jp2/AIA/";
	private Map<String, List<String>> imageDirsMap;
	private LoadingCache<CacheKey, NavigableSet<DateNameLoc>> cache = null;
	private HashFunction hashFunct = Hashing.murmur3_128();
	private Lock updateLock;
	private int cadenceMin;

	private Logger logger = null;

	private class DateNameLocComparator implements Comparator<DateNameLoc> {

		@Override
		public int compare(DateNameLoc arg0, DateNameLoc arg1) {
			return arg0.dateOfObs.compareTo(arg1.dateOfObs);
		}

	}

	private class DateNameLoc {
		DateTime dateOfObs;
		String nameOfFile;
		String directory;
	}

	private class CacheKey {

		int hashInt;
		String key;
		Waveband wavelength;
		DateTime timeStamp;

		public CacheKey(DateTime timeStamp, Waveband wavelength, HashFunction hashFunct) {
			this.key = this.conStructKey(timeStamp, wavelength);
			this.timeStamp = timeStamp;
			this.wavelength = wavelength;
			this.hashInt = hashFunct.newHasher().putString(this.key, Charsets.UTF_8).hash().asInt();
		}

		private String conStructKey(DateTime timeStamp, Waveband wavelength) {
			StringBuilder sb = new StringBuilder();
			sb.append(timeStamp.getYear());
			sb.append("/");
			sb.append(String.format("%02d", timeStamp.getMonthOfYear()));
			sb.append("/");
			sb.append(String.format("%02d", timeStamp.getDayOfMonth()));
			sb.append("/");
			sb.append(Utility.convertWavebandToInt(wavelength));
			sb.append("/");
			return sb.toString();
		}

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

		public String getKey() {
			return this.key;
		}

		public DateTime getTime() {
			return this.timeStamp;
		}

		public Waveband getWave() {
			return this.wavelength;
		}

	}

	/**
	 * Constructor builds a Helioviewer Image File puller that requires the returned
	 * image to be within as set number of minutes of the queried time. The object
	 * uses a cache on the list of image files in a directory on the datasource that
	 * correspond to a particular day. Since the datasource is updating that list
	 * for the current day, it is necessary to purge that list to have an updated
	 * list of files available. To do this the cache is set to purge that list after
	 * a number of minutes of that list not being accessed.
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
	 *                      cache and will need to be pulled from the datasource
	 *                      again.
	 */
	public HelioviewerImageFileDatasource(int maxCacheSize, int cadenceMin, int expireMinutes) {

		this.updateLock = new ReentrantLock();
		this.imageDirsMap = new HashMap<String, List<String>>();
		this.cadenceMin = cadenceMin;

		this.cache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.expireAfterAccess(Duration.of(expireMinutes, ChronoUnit.MINUTES))
				.build(new CacheLoader<CacheKey, NavigableSet<DateNameLoc>>() {
					public NavigableSet<DateNameLoc> load(CacheKey key) {
						return fetchImagesForDayWave(key);
					}
				});

	}

	/**
	 * Constructor builds a Helioviewer Image File puller that requires the returned
	 * image to be within as set number of minutes of the queried time. The object
	 * uses a cache on the list of image files in a directory on the datasource that
	 * correspond to a particular day. Since the datasource is updating that list
	 * for the current day, it is necessary to purge that list to have an updated
	 * list of files available. To do this the cache is set to purge that list after
	 * a number of minutes of that list not being accessed.
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
	 *                      cache and will need to be pulled from the datasource
	 *                      again.
	 * 
	 * @param logger        A logging mechanism so we can see what is going on
	 */
	public HelioviewerImageFileDatasource(int maxCacheSize, int cadenceMin, int expireMinutes, Logger logger) {

		this.updateLock = new ReentrantLock();
		this.imageDirsMap = new HashMap<String, List<String>>();
		this.cadenceMin = cadenceMin;
		this.logger = logger;

		this.cache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.expireAfterAccess(Duration.of(expireMinutes, ChronoUnit.MINUTES))
				.build(new CacheLoader<CacheKey, NavigableSet<DateNameLoc>>() {
					public NavigableSet<DateNameLoc> load(CacheKey key) {
						return fetchImagesForDayWave(key);
					}
				});

	}

	@Override
	public Pair<byte[], String> getImageAtTime(DateTime timeStamp, Waveband wavelength) {
		CacheKey key = new CacheKey(timeStamp, wavelength, this.hashFunct);
		NavigableSet<DateNameLoc> setForInput = this.cache.getUnchecked(key);
		if (setForInput != null) {
			DateNameLoc tmpDNL = new DateNameLoc();
			tmpDNL.dateOfObs = timeStamp;
			DateNameLoc resultDNL = setForInput.ceiling(tmpDNL);
			if (resultDNL != null) {
				DateTime timeOfFile = resultDNL.dateOfObs;
				int diff = Minutes.minutesBetween(timeStamp, timeOfFile).getMinutes();
				if (Math.abs(diff) <= this.cadenceMin) {
					String append = resultDNL.directory + resultDNL.nameOfFile;
					// Images are less than 10MB so this should be enough. The library defaulted to
					// 1MB but they are larger that than.
					int maxBodySize = 10 * 1024 * 1024;

					boolean success = false;
					int tryCount = 0;

					while (!success && tryCount < 3) {
						try {
							Connection con = Jsoup.connect(this.baseAddress + append).ignoreContentType(true)
									.maxBodySize(maxBodySize);
							Response resp = con.execute();
							if (resp.statusCode() == 200) {
								byte[] byteArr = resp.bodyAsBytes();
								Pair<byte[], String> results = new Pair<byte[], String>(byteArr, resultDNL.nameOfFile);
								success = true;
								return results;
							}
						} catch (Exception e) {
							if (this.logger != null) {
								this.logger.error("Error while getting image bytes for ( " + this.baseAddress + append
										+ " ) in HelioviewerImageFileDatasource", e);
							}
						}
						tryCount++;
					}
				}
			}
		}
		return null;
	}

	private NavigableSet<DateNameLoc> fetchImagesForDayWave(CacheKey key) {
		NavigableSet<DateNameLoc> results = null;
		List<String> dirsForDate = this.getDirsForRequested(key);
		// If the map has the dirs of date then we process them.
		if (dirsForDate != null) {
			results = this.pullImgSetFromDirs(dirsForDate);
		} else {
			results = new TreeSet<DateNameLoc>(new DateNameLocComparator());
		}
		return results;
	}

	private NavigableSet<DateNameLoc> pullImgSetFromDirs(List<String> dirsForDate) {
		NavigableSet<DateNameLoc> results = new TreeSet<DateNameLoc>(new DateNameLocComparator());

		for (String dir : dirsForDate) {
			List<String> imagNames = this.getDirectoryStructure(dir);
			for (String imgName : imagNames) {
				DateNameLoc dnl = new DateNameLoc();
				dnl.nameOfFile = imgName;
				dnl.directory = dir;
				int year = Integer.parseInt(imgName.substring(0, 4));
				int month = Integer.parseInt(imgName.substring(5, 7));
				int day = Integer.parseInt(imgName.substring(8, 10));
				int hour = Integer.parseInt(imgName.substring(12, 14));
				int minute = Integer.parseInt(imgName.substring(15, 17));
				int second = Integer.parseInt(imgName.substring(18, 20));
				dnl.dateOfObs = new DateTime(year, month, day, hour, minute, second);
				results.add(dnl);
			}
		}
		return results;
	}

	private List<String> getDirsForRequested(CacheKey key) {
		DateTime time = key.getTime();
		Waveband wavelength = key.getWave();
		List<String> results;
		try {
			this.updateLock.lock();

			results = this.imageDirsMap.get(key.getKey());
			if (results == null) {
				String year = time.getYear() + "/";
				String month = String.format("%02d", time.getMonthOfYear()) + "/";
				String day = String.format("%02d", time.getDayOfMonth()) + "/";
				String wave = Utility.convertWavebandToInt(wavelength) + "/";
				String yearKeyStr = year + month + day + wave;
				String waveKeyStr = wave + year + month + day;

				// Since they didn't keep a consistent directory structure we need to change how
				// we process based on what year it is.
				if (time.getYear() < 2013) {
					if (time.getYear() > 2010) {
						if (this.checkYMDExistsInWave(year, month, day, wave)) {
							results = new ArrayList<String>(2);
							results.add(waveKeyStr);
						}
						if (this.checkMDWExistsInYear(year, month, day, wave)) {
							if (results == null)
								results = new ArrayList<String>(1);
							results.add(yearKeyStr);
						}
					} else {
						if (this.checkYMDExistsInWave(year, month, day, wave)) {
							results = new ArrayList<String>(1);
							results.add(waveKeyStr);
						}
					}
				} else {
					if (this.checkMDWExistsInYear(year, month, day, wave)) {
						results = new ArrayList<String>(1);
						results.add(key.getKey());
					}
				}
				if (results != null)
					this.imageDirsMap.put(key.getKey(), results);
			}
		} finally {
			this.updateLock.unlock();
		}
		return results;
	}

	///////////////// Checking wave/year/month/day directories\\\\\\\\\\\\\\\\\\\\\\
	private boolean checkYMDExistsInWave(String year, String month, String day, String wave) {
		boolean result = false;
		List<String> years = this.getDirectoryStructure(wave);
		for (String yr : years) {
			if (yr.contains(year)) {
				String append = wave + yr;
				result = this.checkMDExistsInWave(append, month, day);
				break;
			}
		}
		return result;
	}

	private boolean checkMDExistsInWave(String append, String month, String day) {
		boolean result = false;
		List<String> months = this.getDirectoryStructure(append);
		for (String mo : months) {
			if (mo.contains(month)) {
				append = append + mo;
				result = this.checkDExistsInWave(append, day);
				break;
			}
		}
		return result;
	}

	private boolean checkDExistsInWave(String append, String day) {
		boolean result = false;
		List<String> days = this.getDirectoryStructure(append);
		for (String dy : days) {
			if (dy.contains(day)) {
				result = true;
				break;
			}
		}
		return result;
	}

	///////////////// Checking year/month/day/wave directories\\\\\\\\\\\\\\\\\\\\
	private boolean checkMDWExistsInYear(String year, String month, String day, String wave) {
		boolean result = false;
		List<String> months = this.getDirectoryStructure(year);
		for (String mo : months) {
			if (mo.contains(month)) {
				String append = year + mo;
				result = this.checkDWExistsInYear(append, day, wave);
				break;
			}
		}
		return result;
	}

	private boolean checkDWExistsInYear(String append, String day, String wave) {
		boolean result = false;
		List<String> days = this.getDirectoryStructure(append);
		for (String dy : days) {
			if (dy.contains(day)) {
				append = append + dy;
				result = this.checkWExistsInYear(append, wave);
				break;
			}
		}
		return result;
	}

	private boolean checkWExistsInYear(String append, String wave) {
		boolean result = false;
		List<String> waves = this.getDirectoryStructure(append);
		for (String wv : waves) {
			if (wv.contains(wave)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private List<String> getDirectoryStructure(String append) {
		List<String> results = new ArrayList<String>();
		boolean success = false;
		int tryCount = 0;
		while (!success && tryCount < 3) {
			try {
				Document doc = Jsoup.connect(this.baseAddress + append).get();

				Elements directoryLinks = doc.select("a[href]");
				for (Element dirLink : directoryLinks) {
					String href = dirLink.attr("href");
					if (href.startsWith("?") || href.startsWith("_") || href.startsWith("/")) {
						// Skip these, they are not what we want.
					} else {
						results.add(href);
						success = true;
					}
				}
			} catch (Exception e) {
				if (this.logger != null)
					this.logger.error("Error while getting directory structure for( " + this.baseAddress + append
							+ " ) in HelioviewerImageFileDatasource.", e);
			}
			tryCount++;
		}
		return results;
	}

}
