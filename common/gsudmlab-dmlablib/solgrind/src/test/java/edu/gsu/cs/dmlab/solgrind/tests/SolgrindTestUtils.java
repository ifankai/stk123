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
package edu.gsu.cs.dmlab.solgrind.tests;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.index.InstanceVertex;
import edu.gsu.cs.dmlab.solgrind.index.RelationEdge;
import edu.gsu.cs.dmlab.solgrind.index.SequenceGraph;

/**
 * Created by ahmetkucuk on 07/10/16.
 */
public class SolgrindTestUtils {

    public static SequenceGraph createTestGraph() {

    	InstanceVertex i0 = new InstanceVertex("0", new EventType("SG"));
        InstanceVertex i1 = new InstanceVertex("1", new EventType("AR"));
        InstanceVertex i2 = new InstanceVertex("2", new EventType("CH"));
        InstanceVertex i3 = new InstanceVertex("3", new EventType("AR"));
        InstanceVertex i4 = new InstanceVertex("4", new EventType("CH"));
        InstanceVertex i5 = new InstanceVertex("5", new EventType("AR"));
        InstanceVertex i6 = new InstanceVertex("6", new EventType("FI"));
        InstanceVertex i7 = new InstanceVertex("7", new EventType("FI"));
        InstanceVertex i8 = new InstanceVertex("8", new EventType("CH"));
        InstanceVertex i9 = new InstanceVertex("9", new EventType("SG"));
        InstanceVertex i10 = new InstanceVertex("10", new EventType("SG"));
        InstanceVertex i11 = new InstanceVertex("11", new EventType("SG"));

        SequenceGraph esGraph = new SequenceGraph();

        esGraph.addVertex(i0);
        esGraph.addVertex(i1);
        esGraph.addVertex(i2);
        esGraph.addVertex(i3);
        esGraph.addVertex(i4);
        esGraph.addVertex(i5);
        esGraph.addVertex(i6);
        esGraph.addVertex(i7);
        esGraph.addVertex(i8);
        esGraph.addVertex(i9);
        esGraph.addVertex(i10);
        esGraph.addVertex(i11);

        RelationEdge edge = esGraph.addEdge(i0, i1);
        esGraph.setEdgeWeight(edge, 0.1);
        
        edge = esGraph.addEdge(i1, i2);
        esGraph.setEdgeWeight(edge, 0.1);

        edge = esGraph.addEdge(i3, i4);
        esGraph.setEdgeWeight(edge, 0.2);

        edge = esGraph.addEdge(i4, i5);
        esGraph.setEdgeWeight(edge, 0.3);

        edge = esGraph.addEdge(i6, i7);
        esGraph.setEdgeWeight(edge, 0.4);

        edge = esGraph.addEdge(i3, i6);
        esGraph.setEdgeWeight(edge, 0.5);

        edge = esGraph.addEdge(i6, i5);
        esGraph.setEdgeWeight(edge, 0.6);

        edge = esGraph.addEdge(i2, i8);
        esGraph.setEdgeWeight(edge, 0.4);

        edge = esGraph.addEdge(i2, i9);
        esGraph.setEdgeWeight(edge, 0.3);

        edge = esGraph.addEdge(i8, i9);
        esGraph.setEdgeWeight(edge, 0.1);

        return esGraph;
    }
}
