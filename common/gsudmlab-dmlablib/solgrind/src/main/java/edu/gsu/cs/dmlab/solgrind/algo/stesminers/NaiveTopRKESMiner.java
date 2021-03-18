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
public class NaiveTopRKESMiner {

    private Set<EventSequence> resultSet = new HashSet<>();
    private Map<EventSequence, Double> resultMap = new HashMap<>();
    private ESGrowthMiner esGrowthMiner;

    public NaiveTopRKESMiner(SequenceGraph esGraph, double rPercent, int K) {
        esGraph.rPercentFilter(rPercent);
        this.esGrowthMiner = new ESGrowthMiner(esGraph, 0);
        collectTopK(K);
        System.out.println("==END OF MINING (Naive Top-(R%, K) Miner)");
    }

    private void collectTopK(int K) {

        List<EventSequence> all = new ArrayList<>(esGrowthMiner.getResultSet());
        Collections.sort(all, EventSequence.piComparator);
        
        resultSet.addAll(all.subList(Math.max(all.size()-K, 0), all.size()));
        double minPI = all.get(Math.max(all.size()-K, 0) ).getPiValue();
        for(EventSequence e: resultSet) {
            resultMap.put(e, e.getPiValue());
        }
        System.out.println("Found Top-" + resultSet.size() + " STESs.. Min pi=" + minPI );
    }

    public Set<EventSequence> getResultSet() {
        return resultSet;
    }


    public Map<EventSequence, Double> getResultInMap() {
        return resultMap;
    }

}
