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
package edu.gsu.cs.dmlab.solgrind.index.tests;

import edu.gsu.cs.dmlab.solgrind.index.RelationEdge;
import edu.gsu.cs.dmlab.solgrind.index.SequenceGraph;
import edu.gsu.cs.dmlab.solgrind.tests.SolgrindTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Created by ahmetkucuk on 07/10/16.
 */
public class SequenceGraphTests {

	@Test
	public void testSetFilter() {
		SequenceGraph esGraph = SolgrindTestUtils.createTestGraph();
		assertEquals(10, esGraph.edgeSet().size());
		esGraph.ciFilter(0.45);

		for (RelationEdge e : esGraph.edgeSet()) {
			assertTrue(e.getWeight() > 0.45);
		}

		assertEquals(2, esGraph.edgeSet().size());

	}

	@Test
	public void testClone() {
		SequenceGraph esGraph = SolgrindTestUtils.createTestGraph();
		SequenceGraph clonedGraph = new SequenceGraph(esGraph);

		assertEquals(esGraph.edgeSet().size(), clonedGraph.edgeSet().size());
		clonedGraph.ciFilter(4);

		int clonedSize = clonedGraph.edgeSet().size();
		assertEquals(0, clonedSize);
	}

	@Test
	public void testFindKPercentThreshold() {
		SequenceGraph esGraph = SolgrindTestUtils.createTestGraph();
		assertTrue(esGraph.findKPercentThreshold(0.7) == 0.2);
	}

	@Test
	public void testRandomSampleEdges() {
		SequenceGraph esGraph = SolgrindTestUtils.createTestGraph();
		int totalSize = esGraph.edgeSet().size();
		assertEquals(esGraph.randomSampleEdges(0.3).edgeSet().size(), (int) Math.ceil(totalSize * 0.3));
	}
}
