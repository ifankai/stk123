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
package edu.gsu.cs.dmlab.distance.dtw.search.interfaces;

import java.util.Iterator;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;

/**
 * Interface for search windows in a Dynamic Time Warping Algorithm
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISearchWindow {

	/**
	 * Method that returns the minimum value of I in this search window, where I is
	 * the column index in an (I,J) = (Column, Row) matrix indexing scheme
	 * 
	 * @return The minimum value I takes in this window
	 */
	public int minI();

	/**
	 * Method that returns the maximum value of I in this search window, where I is
	 * the column index in an (I,J) = (Column, Row) matrix indexing scheme
	 * 
	 * @return The maximum value I takes in this window
	 */
	public int maxI();

	/**
	 * Method that returns the minimum value of J in this search window, where J is
	 * the row index in an (I,J) = (Column, Row) matrix indexing scheme
	 * 
	 * @return The minimum value J takes in this window
	 */
	public int minJ();

	/**
	 * Method that returns the maximum value of J in this search window, where J is
	 * the row index in an (I,J) = (Column, Row) matrix indexing scheme
	 * 
	 * @return The maximum value J takes in this window
	 */
	public int maxJ();

	/**
	 * Method that returns the minimum value of J in this search window at position
	 * I, where J is the row index in an (I,J) = (Column, Row) matrix indexing
	 * scheme
	 * 
	 * @return The minimum value J takes in this window
	 */
	public int minJforI(int i);

	/**
	 * Method that returns the maximum value of J in this search window at position
	 * I, where J is the row index in an (I,J) = (Column, Row) matrix indexing
	 * scheme
	 * 
	 * @return The maximum value J takes in this window
	 */
	public int maxJforI(int i);
	
	/**
	 * Method that returns the size of the search window
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Method that returns an iterator that produces the index pairs of all the
	 * cells within this window
	 * 
	 * @return Iterator used to iterate over the cells in the search window
	 */
	public Iterator<ColMajorCell> iterator();
}
