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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import edu.gsu.cs.dmlab.datatypes.Waveband;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;
import edu.gsu.cs.dmlab.imageproc.interfaces.IImgParamNormalizer;

/**
 * This class extends the
 * {@link edu.gsu.cs.dmlab.databases.NonCacheImageDBConnection
 * NonCacheImageDBConnecton} by adding a cache to the image parameters that are
 * pulled by IEvent from the database. It does not change any other
 * functionality of the super class.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 */
public class ImageDBConnection extends NonCacheImageDBConnection {

	LoadingCache<CacheKey, double[][][]> cache = null;
	HashFunction hashFunct = Hashing.murmur3_128();

	private class CacheKey {
		String key;
		ISpatialTemporalObj event;
		Waveband wavelength;
		boolean leftSide;
		int hashInt;

		public CacheKey(ISpatialTemporalObj event, boolean leftSide, Waveband wavelength, HashFunction hashFunct) {
			StringBuilder evntName = new StringBuilder();
			evntName.append(event.getUUID().toString());
			evntName.append(wavelength);
			if (leftSide) {
				evntName.append("T");
			} else {
				evntName.append("F");
			}
			this.key = evntName.toString();
			this.hashInt = hashFunct.newHasher().putString(this.key, Charsets.UTF_8).hash().asInt();
			this.event = event;
			this.leftSide = leftSide;
			this.wavelength = wavelength;
		}

		@Override
		public void finalize() throws Throwable {
			this.wavelength = null;
			this.key = null;
			this.event = null;
		}

		public ISpatialTemporalObj getEvent() {
			return this.event;
		}

		public boolean isLeftSide() {
			return this.leftSide;
		}

		public Waveband getWavelength() {
			return this.wavelength;
		}

		public String getKey() {
			return key;
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

	/**
	 * Constructor that assumes default values for parameter down sample and the
	 * number of parameters for each image cell. Those values are a division by 64
	 * for all coordinates of input events, and 10 image parameters per cell
	 * location.
	 * 
	 * @param dsourc       The data source connection that is used to connect to the
	 *                     database.
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
	public ImageDBConnection(DataSource dsourc, IImgParamNormalizer normalizer, Logger logger, int maxCacheSize) {
		super(dsourc, normalizer, logger);

		this.cache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.build(new CacheLoader<CacheKey, double[][][]>() {
					public double[][][] load(CacheKey key) throws SQLException {
						return fetchImageParams(key);
					}
				});
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
	 * 
	 * @param maxCacheSize    The number of input event and wavelength pairs to
	 *                        cache the image parameter cube for before replacing
	 *                        with LRU ordering.
	 */
	public ImageDBConnection(DataSource dsourc, IImgParamNormalizer normalizer, int paramDim, int paramDownSample,
			Logger logger, int maxCacheSize) {
		super(dsourc, normalizer, paramDim, paramDownSample, logger);

		this.cache = CacheBuilder.newBuilder().maximumSize(maxCacheSize)
				.build(new CacheLoader<CacheKey, double[][][]>() {
					public double[][][] load(CacheKey key) throws SQLException {
						return fetchImageParams(key);
					}
				});
	}

	@Override
	public void finalize() throws Throwable {
		try {
			this.cache.cleanUp();
			this.cache = null;
			this.hashFunct = null;
		} finally {
			super.finalize();
		}
	}

	@Override
	public double[][][] getImageParamForWave(ISpatialTemporalObj event, Waveband wavelength, boolean leftSide) {
		CacheKey key = new CacheKey(event, leftSide, wavelength, this.hashFunct);
		double[][][] returnValue = this.cache.getUnchecked(key);
		key = null;
		return returnValue;
	}

	private double[][][] fetchImageParams(CacheKey key) throws SQLException {
		double[][][] retVal = super.getImageParamForWave(key.getEvent(), key.getWavelength(), key.isLeftSide());
		return retVal;
	}

}