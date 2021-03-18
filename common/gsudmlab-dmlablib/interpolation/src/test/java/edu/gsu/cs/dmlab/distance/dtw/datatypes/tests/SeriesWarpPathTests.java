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
package edu.gsu.cs.dmlab.distance.dtw.datatypes.tests;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.SeriesWarpPath;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;

class SeriesWarpPathTests {

	@Test
	void testWarpPathThrowsWhenICrossesFirst() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addFirst(2, 3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			path.addFirst(3, 3);
		});
	}

	@Test
	void testWarpPathThrowsWhenJCrossesFirst() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addFirst(2, 3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			path.addFirst(1, 4);
		});
	}

	@Test
	void testWarpPathThrowsWhenICrossesLast() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			path.addLast(1, 3);
		});
	}

	@Test
	void testWarpPathThrowsWhenJCrossesLast() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			path.addLast(3, 2);
		});
	}

	@Test
	void testWarpPathSizeCorrectAfterInsert() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addFirst(1, 1);
		Assertions.assertTrue(path.size() == 2);
	}

	@Test
	void testWarpPathMinICorrectAfterInsertFirst() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addFirst(1, 1);
		Assertions.assertTrue(path.minI() == 1);
	}

	@Test
	void testWarpPathMinICorrectAfterInsertLast() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		Assertions.assertTrue(path.minI() == 2);
	}

	@Test
	void testWarpPathMinJCorrectAfterInsertFirst() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addFirst(1, 1);
		Assertions.assertTrue(path.minJ() == 1);
	}

	@Test
	void testWarpPathMinJCorrectAfterInsertLast() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		Assertions.assertTrue(path.minJ() == 3);
	}

	@Test
	void testWarpPathMaxICorrectAfterInsertFirst() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addFirst(1, 1);
		Assertions.assertTrue(path.maxI() == 2);
	}

	@Test
	void testWarpPathMaxICorrectAfterInsertLast() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		Assertions.assertTrue(path.maxI() == 3);
	}

	@Test
	void testWarpPathMaxJCorrectAfterInsertFirst() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addFirst(1, 1);
		Assertions.assertTrue(path.maxJ() == 3);
	}

	@Test
	void testWarpPathMaxJCorrectAfterInsertLast() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		Assertions.assertTrue(path.maxJ() == 3);
	}

	@Test
	void testWarpPathGetMatchingForICorrect() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		List<Integer> lst = path.getMatchingIndexesForI(2);
		Assertions.assertTrue(lst.get(0) == 3);
	}

	@Test
	void testWarpPathGetMatchingForJCorrect() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		List<Integer> lst = path.getMatchingIndexesForJ(3);
		Assertions.assertTrue(lst.get(0) == 2);
		Assertions.assertTrue(lst.get(1) == 3);
	}

	@Test
	void testWarpPathGetMappingCorrect() {
		IAlignmentPath path = new SeriesWarpPath();
		path.addLast(2, 3);
		path.addLast(3, 3);
		Iterator<ColMajorCell> itr = path.getMapping();
		ColMajorCell cell = itr.next();
		Assertions.assertTrue(cell.getCol() == 2);
		Assertions.assertTrue(cell.getRow() == 3);
		cell = itr.next();
		Assertions.assertTrue(cell.getCol() == 3);
		Assertions.assertTrue(cell.getRow() == 3);
	}

}
