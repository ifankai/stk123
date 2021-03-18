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
package edu.gsu.cs.dmlab.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * This class is just a wrapper class so we can add weights to the edge with a
 * constructor. Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class Edge extends DefaultWeightedEdge {

	private static final long serialVersionUID = 7185657615982000843L;
	double weight;

	/**
	 * Constructor
	 * 
	 * @param weight the weight the edge should have.
	 */
	public Edge(double weight) {
		super();
		this.weight = weight;
	}

	/**
	 * @return Returns the weight of this edge.
	 */
	@Override
	public double getWeight() {
		return this.weight;
	}

	/**
	 * @return A copy of this edge.
	 */
	@Override
	public Object clone() {
		return new Edge(this.getWeight());
	}

	/**
	 * Sets the weight of the edge.
	 * 
	 * @param weight The weight to set on the edge.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
