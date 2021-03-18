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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.math.Quantiles;
import com.google.common.math.Quantiles.Scale;
import com.google.common.primitives.Doubles;

import edu.gsu.cs.dmlab.imageproc.interfaces.IPeakDetector;

/**
 * Peak detection is a designed to find dominant peaks of a time series based on
 * the settings provided by the user. For a peak to be considered dominant,
 * three constraints can be set: a threshold on the frequency domain, a minimum
 * peak-to-peak distance, and a maximum number of dominant peaks. <br>
 * The main task is done in the method <code>findPeaks</code> which initially
 * finds all the peaks (i.e., any data point whose value is larger than both of
 * its previous and next neighbors), sorts them by their height, and then
 * removes those which do not fall into the set constraints. <br>
 * 
 * @author Azim Ahmadzdeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class PeakDetection implements IPeakDetector {

	int n = 0;
	int width = 0;
	double threshold = 0.0;
	boolean isPercentile = false;
	Map<Integer, Double> posAndValues = null;

	/**
	 * Constructor for the class PeakDetection.
	 * 
	 * @param width
	 *            is the radius of the neighborhood of an accepted peak within which
	 *            no other peaks is allowed. To relax this condition, set it to 1 or
	 *            0. The results would be similar, i.e., no peaks will be removed
	 *            because of the neighboring constraints.
	 * 
	 * @param threshold
	 *            on the frequency domain of the time series below which all the
	 *            peaks will be ignored.<br>
	 *            <b>Note:</b> To relax this condition, set it to the minimum values
	 *            of the given sequence, and not to zero, unless the sequence is
	 *            guaranteed to have positive values only.
	 * 
	 * @param n
	 *            the maximum number of peaks to be found, from the highest to
	 *            lowest. If <code>n=0</code>, all of the detected peaks will be
	 *            returned.
	 * @param isPercentile
	 *            if <code>true</code>, then the value of <code>threshold</code> is
	 *            interpreted as the percentile. Then the accepted values are
	 *            doubles within [0,100]. If false, then the given
	 *            <code>double</code> value will be used directly as the actual
	 *            threshold on the frequency domain.
	 * 
	 */
	public PeakDetection(int width, double threshold, int n, boolean isPercentile) {

		this.n = n;
		this.width = width;
		this.threshold = threshold;
		this.isPercentile = isPercentile;

		if (this.isPercentile) {
			if (threshold > 100 || threshold < 0) {
				throw new IllegalArgumentException("Percentile must rely within the interval [0, 100]!");
			}
		}

		if (this.width < 0 || this.n < 0) {
			throw new IllegalArgumentException("The values of 'width' or 'n' cannot be negative!");
		}

	}

	/**
	 * This method finds the peaks of the given time series based on the constraints
	 * provided for the class.<br>
	 * 
	 * <b>Important:</b> The returned list is sorted by the height of the peaks, so
	 * the indices are not ordered.
	 */
	@Override
	public List<Integer> findPeaks(double[] data) {

		List<Integer> peaks = null;
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();

		peaks = findCandidatePeaks(data);

		/*
		 * Store the index and the value of the peaks in 'map'
		 */
		makeTreemapUsingCollections(map, peaks, data);

		/*
		 * Sort 'map' and store it in 'sortedMap'
		 */
		map.entrySet().stream().sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

		/*
		 * Remove all the peaks below the 'threshold'
		 */
		double t = 0.0;
		if (this.isPercentile) {
			Scale s = Quantiles.percentiles();
			t = s.index((int) this.threshold).compute(data);
		} else {
			t = this.threshold;
		}

		for (Integer i : peaks) {
			if (sortedMap.get(i) < t) {
				sortedMap.remove(i);
			}
		}

		/*
		 * Remove all the peaks within the excluded neighborhood of the bigger peaks
		 */
		List<Integer> removedPeaks = new LinkedList<Integer>();
		List<Integer> peaks2 = new LinkedList<Integer>(sortedMap.keySet());
		int from, to;
		for (Integer peakPosition : peaks2) {
			if (!removedPeaks.contains(peakPosition)) {
				from = peakPosition - this.width; // TODO
				to = peakPosition + this.width;
				for (int i = from; i <= to; i++) {
					if ((sortedMap.containsKey(i)) && (i != peakPosition)) {
						sortedMap.remove(i);
						removedPeaks.add(i);
					}
				}
			}

		}

		/*
		 * Keep the first 'n' (dominant) peaks only
		 */
		int counter = this.n;
		Map<Integer, Double> requestedMap = new LinkedHashMap<Integer, Double>();
		if (this.n != 0) {
			for (Map.Entry<Integer, Double> entry : sortedMap.entrySet()) {
				counter--;
				requestedMap.put(entry.getKey(), entry.getValue());
				if (counter == 0)
					break;
			}
		} else {
			requestedMap = sortedMap;
		}

		this.posAndValues = requestedMap;
		peaks = new LinkedList<Integer>(requestedMap.keySet());

		// No need to shift the peak indices anymore
		// for(int i = 0; i < peaks.size(); i++)
		// peaks.set(i, peaks.get(i)+1);

		return peaks;
	}

	/**
	 * It finds the position of any peaks on the time series.
	 * 
	 * @param data
	 * @return List of peak index locations
	 */
	public static List<Integer> findCandidatePeaks(double[] data) {

		int mid = 1;
		int end = data.length;
		List<Integer> peaks = new LinkedList<Integer>();
		// Subtraction allows the actual beginning to be a peak if possible
		double[] ext_begin = { data[0] - 0.1 };
		// Subtraction allows the actual ending to be a peak if possible
		double[] ext_end = { data[end - 1] - 0.1 };
		double[] ts = Doubles.concat(ext_begin, data, ext_end);

		while (mid <= end) {

			if (ts[mid - 1] < ts[mid] && ts[mid + 1] < ts[mid]) {
				// We had added one element to the beginning of this array, hence -1.
				peaks.add(new Integer(mid - 1));
			}
			mid++;
		}
		return peaks;

	}

	public static void makeTreemapUsingCollections(Map<Integer, Double> m, List<Integer> peaks, double[] data) {

		for (int i = 0; i < peaks.size(); i++) {
			m.put(peaks.get(i), data[peaks.get(i)]);
		}

	}

}