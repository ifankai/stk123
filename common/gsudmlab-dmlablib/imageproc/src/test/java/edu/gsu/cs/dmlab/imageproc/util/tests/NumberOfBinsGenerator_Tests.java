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

import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.imageproc.imageparam.util.NumberOfBinsGenerator;

public class NumberOfBinsGenerator_Tests {

	/**
	 * Get the first nOfBins for min=0, max=44, TOTAL_NUMBER_OF_TRIALS=20
	 */
	@Test
	public void testIdx1() {

		int actual = NumberOfBinsGenerator.findNOfBinsForIndex_Bounded(0, 44, 0);
		int expected = 2;
		assertTrue(actual == expected);
	}

	/**
	 * Get the last nOfBins for min=0, max=44, TOTAL_NUMBER_OF_TRIALS=20
	 */
	@Test
	public void testIdx2() {

		int actual = NumberOfBinsGenerator.findNOfBinsForIndex_Bounded(0, 44,
				NumberOfBinsGenerator.TOTAL_NUMBER_OF_TRIALS);
		int expected = 44;

		assertTrue(actual == expected);
	}

	/**
	 * Get the entire arrya of nOfBins for min=0, max=44, TOTAL_NUMBER_OF_TRIALS=20
	 */
	@Test
	public void testIdx3() {

		int[] actual = NumberOfBinsGenerator.findAllNOfBins_Bounded(0, 44);
//		System.out.println(Arrays.toString(actual));
		int[] expected = new int[] { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 44 };
		assertTrue(Arrays.equals(expected, actual));
	}

	/**
	 * Similar to the test above, but this time for the case that the histogram is
	 * extremely skewed and non-integers are allowed.
	 */
	@Test
	public void testIdx4() {

		int[] actual = NumberOfBinsGenerator.findAllNOfBins_Bounded(0, 4138 * 10);
//		System.out.println(Arrays.toString(actual));
		int[] expected = new int[] { 1970, 3940, 5911, 7881, 9852, 11822, 13793, 15763, 17734, 19704, 21675, 23645,
				25616, 27586, 29557, 31527, 33498, 35468, 37439, 39409, 41380 };
		assertTrue(Arrays.equals(expected, actual));
	}

}
