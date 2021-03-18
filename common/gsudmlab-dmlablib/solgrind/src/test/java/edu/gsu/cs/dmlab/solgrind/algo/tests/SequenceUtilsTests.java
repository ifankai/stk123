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
package edu.gsu.cs.dmlab.solgrind.algo.tests;

import edu.gsu.cs.dmlab.solgrind.SolgrindConstants;
import edu.gsu.cs.dmlab.solgrind.algo.SequenceUtils;
import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;

import org.junit.jupiter.api.Test;

/**
 * Created by ahmetkucuk on 07/10/16.
 */
public class SequenceUtilsTests {

	@Test
	public void testGenerateHeadwithTailRatio_1() {
		SolgrindConstants.SAMPLING_INTERVAL = 1;

		Instance i = new Instance("1", new EventType("ar"));
		i.getTrajectory().addTGPair(1, 2, null);
		i.getTrajectory().addTGPair(2, 3, null);
		i.getTrajectory().addTGPair(3, 4, null);
		i.getTrajectory().addTGPair(4, 5, null);

		Instance head = SequenceUtils.generateHeadwithRatio(i, 0.1);
		// System.out.println(head.getTrajectory());
		// Assert.assertEquals(0, head.getTrajectory().getTGPairs().size());
		// Assert.assertTrue(head.getTrajectory().getTGPairs().first().getTInterval().equals(new
		// TInterval(1,2)));
	}

	@Test
	public void testGenerateHeadwithTailRatio_2() {
		SolgrindConstants.SAMPLING_INTERVAL = 1;

		Instance i = new Instance("1", new EventType("ar"));
		i.getTrajectory().addTGPair(1, 2, null);
		i.getTrajectory().addTGPair(2, 3, null);
		i.getTrajectory().addTGPair(3, 4, null);
		i.getTrajectory().addTGPair(4, 5, null);

		Instance head = SequenceUtils.generateHeadwithRatio(i, 0.26);
		// System.out.println(head.getTrajectory());
		// Assert.assertEquals(head.getTrajectory().getTGPairs().size(), 2);
		// Assert.assertTrue(head.getTrajectory().getTGPairs().first().getTInterval().equals(new
		// TInterval(1,2)));
	}
}
