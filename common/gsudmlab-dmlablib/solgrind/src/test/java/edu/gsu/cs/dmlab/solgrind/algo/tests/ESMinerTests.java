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

import org.locationtech.jts.util.Assert;

import edu.gsu.cs.dmlab.solgrind.algo.stesminers.ESGrowthMiner;
import edu.gsu.cs.dmlab.solgrind.algo.stesminers.SequenceConnectMiner;
import edu.gsu.cs.dmlab.solgrind.algo.stesminers.TopRKESMiner;
import edu.gsu.cs.dmlab.solgrind.base.types.event.EventSequence;
import edu.gsu.cs.dmlab.solgrind.index.SequenceGraph;
import edu.gsu.cs.dmlab.solgrind.tests.SolgrindTestUtils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by ahmetkucuk on 05/10/16.
 */
public class ESMinerTests {


    @Test
    public void testMiner() {

        SequenceGraph esGraph = SolgrindTestUtils.createTestGraph();

        new ESGrowthMiner(esGraph, 0);
        new ESGrowthMiner(new SequenceGraph(esGraph), 0);
    }

    @Test
    public void testTopRKESMiner() {

        SequenceGraph esGraph1 = SolgrindTestUtils.createTestGraph();
        SequenceGraph esGraph2 = new SequenceGraph(esGraph1);
        int k = 4;

        double ciThreshold = esGraph1.findKPercentThreshold(1.0);
        TopRKESMiner topRKESMiner = new TopRKESMiner(esGraph1, 1.0, k);

        esGraph2.ciFilter(ciThreshold);
        ESGrowthMiner esGrowthMiner = new ESGrowthMiner(esGraph2, 0);

        List<EventSequence> sortedTopKPR = new ArrayList<>(topRKESMiner.getResultSet());
        List<EventSequence> sortedPi = new ArrayList<>(esGrowthMiner.getResultSet());
        Collections.sort(sortedTopKPR, EventSequence.piComparator);
        Collections.sort(sortedPi, EventSequence.piComparator);
        Assert.equals(sortedTopKPR.get(0).getPiValue(), sortedPi.get(sortedPi.size() - (k)).getPiValue());

    }
    
    @Test
    public void testSqConESMiner() {

    	SequenceGraph esGraph1 = SolgrindTestUtils.createTestGraph();
        esGraph1.ciFilter(0.01);
        SequenceConnectMiner scEsMiner = new SequenceConnectMiner(esGraph1, 0.2);
//        System.out.println(new ArrayList<>(scEsMiner.getResultSet()));
        ArrayList<EventSequence> scList = new ArrayList<>(scEsMiner.getResultSet());
        
        SequenceGraph esGraph2 = SolgrindTestUtils.createTestGraph();
        esGraph2.ciFilter(0.01);
        ESGrowthMiner esgrowth = new ESGrowthMiner(esGraph2, 0.2);
        ArrayList<EventSequence> growthList = new ArrayList<>(esgrowth.getResultSet());
        
        for( EventSequence esq : scList ){
        	
        	int index = growthList.indexOf(esq);
        	Assert.isTrue(index != -1);
        	
        	EventSequence esqInGrowth = growthList.get(index);
        	double piGrowth = esqInGrowth.getPiValue();
        	Assert.equals(esq.getPiValue(), piGrowth);
        	
        }

    }

    @Test
    public void testEventSeqComparator() {

        PriorityQueue<EventSequence> topKEventSequences = new PriorityQueue<>(3, EventSequence.piComparator);
        EventSequence es1 = new EventSequence();
        es1.setPiValue(0.1);
        EventSequence es2 = new EventSequence();
        es2.setPiValue(0.2);
        EventSequence es3 = new EventSequence();
        es3.setPiValue(0.3);
        topKEventSequences.add(es2);
        topKEventSequences.add(es1);
        Assert.equals(topKEventSequences.peek().getPiValue(), es1.getPiValue());
        topKEventSequences.add(es3);
        topKEventSequences.poll();
        Assert.equals(topKEventSequences.peek().getPiValue(), es2.getPiValue());
    }
}
