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
package edu.gsu.cs.dmlab.solgrind.algo.stesminers;

import edu.gsu.cs.dmlab.solgrind.base.types.event.EventSequence;
import edu.gsu.cs.dmlab.solgrind.index.SequenceGraph;

import java.util.*;


/**
 * 
 * @author Ahmet Kucuk, Data Mining Lab, Georgia State University
 * 
 */
public class BtspESMiner {

    private Map<EventSequence, List<Double>> results;

    /**
     * Given an event sequence graph (esGraph), BtspESMiner randomly resamples the sequence edges
     * in the graph, and simply creates a subgraph. sampleRatio shows the fraction of edges to be resampled
     * bootstrapCount shows how many times the mining is performed.
     * @param graph
     * @param resampleRatio
     * @param bootstrapCount
     */
    public BtspESMiner(SequenceGraph graph, double resampleRatio, int bootstrapCount) {

        results = new HashMap<>();

        for(int i = 0; i < bootstrapCount; i++) {
            SequenceGraph sampledGraph = graph.randomSampleEdges(resampleRatio);
            Map<EventSequence, Double> map = new ESGrowthMiner(sampledGraph, 0).getResultInMap();
            for(Map.Entry<EventSequence, Double> entry: map.entrySet()) {
                results.putIfAbsent(entry.getKey(), new ArrayList<>());
                results.get(entry.getKey()).add(entry.getValue());
            }
        }

        for(EventSequence e : results.keySet()) {
            List<Double> tempList = results.get(e);
            while(tempList.size() < bootstrapCount) tempList.add(0.0);
        }
        System.out.println("Found " + results.size() + " event sequences from " + bootstrapCount + " bootstrap runs..");
        System.out.println("==END OF MINING (BTSP-ESMiner)");
    }

    public Map<EventSequence, List<Double>> getResults() {
        return results;
    }
}
