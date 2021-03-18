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
package edu.gsu.cs.dmlab.solgrind.index;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.Serializable;

public class CooccurrenceGraph extends SimpleWeightedGraph<InstanceVertex, RelationEdge>
		implements WeightedGraph<InstanceVertex, RelationEdge>, Serializable {

	/**
	 * generated serial version uid
	 */
	private static final long serialVersionUID = -8686250470583163430L;

	public CooccurrenceGraph(Class<? extends RelationEdge> edgeClass) {
		super(edgeClass);
	}

}
