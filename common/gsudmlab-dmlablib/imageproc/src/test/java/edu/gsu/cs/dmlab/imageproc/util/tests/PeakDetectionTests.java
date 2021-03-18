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
package edu.gsu.cs.dmlab.imageproc.util.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.util.PeakDetection;
import edu.gsu.cs.dmlab.imageproc.interfaces.IPeakDetector;

/**
 * This test class is associated with the class:
 * {@link edu.gsu.cs.dmlab.imageproc.imageparam.util.PeakDetection}.
 * 
 * In all the test-cases, the expected results have been visualized and
 * verified. Find the utilized script (R) at the bottom of this code.
 *
 * @author Azim Ahmadzdeh, Data Mining Lab, Georgia State University
 * 
 */
public class PeakDetectionTests {

	@Test
	public void test0PeaksIdx1() {

		int n = 3;
		int width = -1;
		double threshold = 0;
		boolean isPercentile = false;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PeakDetection(width, threshold, n, isPercentile);
		});
	}

	@Test
	public void test0PeaksIdx2() {

		int n = -1;
		int width = 1;
		double threshold = 0;
		boolean isPercentile = false;
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PeakDetection(width, threshold, n, isPercentile);
		});
	}

	@Test
	public void test1PeaksIdx1() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 0;
		int width = 0;
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89, 44, 22, 67, 0, 31, 58, 76, 13, 49, 40, 85, 52, 74, 35, 60, 4, 78, 82,
				29, 54, 2, 11, 15, 33, 56, 7, 9, 80, 37);
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test1PeaksIdx2() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 0;
		int width = 0;
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89, 44, 22, 67, 0, 31, 58, 76, 13, 49, 40, 85, 52, 74, 35, 60, 4, 78, 82,
				29, 54, 2, 11, 15, 33, 56, 7, 9, 80, 37);
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test1PeaksIdx3() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 0;
		int width = 10; // <- parameter of interest
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89, 44, 22, 67, 0, 78, 11, 33, 56);
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test1PeaksIdx4() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 0;
		int width = data.length; // <- parameter of interest
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89);
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test2PeaksIdx1() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 1; // <- parameter of interest
		int width = 1;
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89);// Tallest peak
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test2PeaksIdx2() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 3; // <- parameter of interest
		int width = 1;
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89, 44, 22);// Top-three peaks in order
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test2PeaksIdx3() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 5; // <- parameter of interest
		int width = 1;
		double threshold = smile.math.Math.min(data);
		boolean isPercentile = false;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89, 44, 22, 67, 0);// Top-five peaks in order
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test3PeaksIdx1() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 0;
		int width = 1;
		double threshold = 90; // i.e., 10.7 // <- parameter of interest
		boolean isPercentile = true;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89, 44, 22, 67, 0, 31, 58, 76, 13);
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test3PeaksIdx2() {

		double[] data = new double[] { 31.0, 1.0, 2.0, 1.0, 4.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 17.0, 0.0, 2.0,
				0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 73.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 4.0, 0.0, 22.0, 0.0, 2.0, 0.0, 5.0,
				0.0, 1.0, 0.0, 6.0, 8.0, 6.0, 4.0, 0.0, 101.0, 0.0, 1.0, 3.0, 5.0, 10.0, 7.0, 0.0, 7.0, 0.0, 3.0, 0.0,
				2.0, 1.0, 20.0, 0.0, 5.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 41.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 5.0, 0.0,
				19.0, 0.0, 4.0, 0.0, 1.0, 0.0, 4.0, 0.0, 1.0, 7.0, 0.0, 0.0, 1.0, 151.0 };

		int n = 0;
		int width = 1;
		double threshold = 99; // i.e., 106.5 // <- parameter of interest
		boolean isPercentile = true;

		IPeakDetector detector = new PeakDetection(width, threshold, n, isPercentile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(89);
		assertTrue(peaks.equals(expected));
	}

	@Test
	public void test4PeaksIdx1() {

		int n = 0;
		int width = 20;

		boolean isQuartile = false;
		double[] data = { -0.44577, -0.14955, 0.2, 0.50787, 0.74741, 1.0572, 1.6702, 2.4714, 3.0658, 3.3093, 3.1225,
				3.3345, 4.0302, 4.3111, 3.7795, 3.4029, 3.3897, 3.7693, 3.4501, 2.7577, 2.2441, 2.1245, 1.895, 1.7162,
				1.5222, 1.603, 1.575, 1.4164, 1.3447, 1.4159, 1.1812, 1.134, 1.1512, 1.2555, 1.1255, 1.1974, 0.98036,
				0.92696, 0.8719, 0.79973, 0.53603, 0.52067, 0.27678, 0.18736, 0.13817, 0.0322, -0.045819, -0.18055,
				-0.31973, -0.32153, -0.34243, -0.37171, -0.45309, -0.47262, -0.44717, -0.38377, -0.35712, -0.21171,
				-0.092552, -0.035228, -0.038763, 0.11097, 0.15773, 0.42194, 0.42945, 0.49024, 0.56971, 0.5679, 0.49275,
				0.57516, 0.44126, 0.51072, 0.42903, 0.39923, 0.42747, 0.39481, 0.3225, 0.13741, 0.033226, 0.0039706,
				-0.039881, -0.01727, -0.081528, -0.15376, -0.11997, -0.2185, -0.25926, -0.31431, -0.39866, -0.39231,
				-0.41355, -0.39443, -0.34262, -0.37076, -0.38375, -0.33039, -0.24428, -0.17583, -0.22332, -0.25558,
				-0.23814, -0.17132, -0.20496, -0.29448, -0.29075, -0.22842, -0.10093, 0.054393, 0.084546, 0.093431,
				0.13267, 0.10606, 0.12686, 0.11689, 0.031447, -0.0304, 0.03228, 0.10035, 0.094202, -0.0037564,
				-0.057352, 0.0016874, 0.097189, 0.038858, -0.082226, -0.079524, -0.00063963, -0.042538, -0.18068,
				-0.28327, -0.28393, -0.24755, -0.2799, -0.33563, -0.36375, -0.35784, -0.35077, -0.31813, -0.26286,
				-0.23365, -0.23093, -0.20113, -0.12033, -0.081693, -0.18005, -0.25359, -0.22509, -0.1373, -0.10437,
				-0.17583, -0.23228, -0.17573, -0.16766, -0.25606, -0.35944, -0.39935, -0.36891, -0.35238, -0.39192,
				-0.43466, -0.44533, -0.45652, -0.49073, -0.53293, -0.55446, -0.54892, -0.53088, -0.51835, -0.5151,
				-0.52184, -0.53531, -0.54511, -0.55743, -0.57187, -0.58777, -0.60705, -0.62874, -0.64809, -0.66074,
				-0.66991, -0.67089, -0.66054, -0.65643, -0.6361, -0.62832, -0.62756, -0.61692, -0.62709, -0.59501,
				-0.56994, -0.55994, -0.53548, -0.53698, -0.53744, -0.53417, -0.57183, -0.58156, -0.57251, -0.55923,
				-0.5438, -0.56438, -0.57902, -0.59147, -0.60773, -0.64602, -0.69082, -0.71569, -0.71148, -0.70982,
				-0.71511, -0.73177, -0.74103, -0.7491, -0.75739, -0.76514, -0.7717, -0.77384, -0.77175, -0.77395,
				-0.76117, -0.76129, -0.75699, -0.74669, -0.73777, -0.70512, -0.69816, -0.69549, -0.68523, -0.6765,
				-0.66387, -0.68241, -0.70878, -0.70869, -0.72135, -0.73134, -0.75633, -0.78766, -0.78703, -0.79476,
				-0.80217, -0.80878, -0.82176, -0.82844, -0.83598, -0.84567, -0.86266, -0.87642, -0.88884, -0.89092,
				-0.89326, -0.89398, -0.89451, -0.89509, -0.89443, -0.89369, -0.89441 };

		double threshold = smile.math.Math.min(data);

		IPeakDetector detector = new PeakDetection(width, threshold, n, isQuartile);
		List<Integer> peaks = detector.findPeaks(data);
		// System.out.println(Arrays.toString(peaks.toArray()));
		List<Integer> expected = Arrays.asList(13, 35, 69, 110, 143, 168, 194, 229, 254);
		assertTrue(peaks.equals(expected));

	}
}
