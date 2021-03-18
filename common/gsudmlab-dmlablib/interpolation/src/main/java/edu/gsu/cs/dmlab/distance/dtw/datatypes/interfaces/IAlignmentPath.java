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
package edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces;

import java.util.Iterator;
import java.util.List;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;

/**
 * Interface for classes that hold the warping information for matching points
 * in one series (I) to points in another series (J). Note that there is no
 * guarantee that all points in either series will have a match in the other.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IAlignmentPath {

	/**
	 * Method that returns the number of point mappings that are contained within
	 * this object.
	 * 
	 * @return The number of point mappings in this object
	 */
	public int size();

	/**
	 * Method that returns the minimum index in series I that is contained within
	 * this mapping
	 * 
	 * @return The minimum index of series I contained in this mapping
	 */
	public int minI();

	/**
	 * Method that returns the minimum index in series J that is contained within
	 * this mapping.
	 * 
	 * @return The minimum index of series J contained in this mapping
	 */
	public int minJ();

	/**
	 * Method that returns the maximum index in series I that is contained within
	 * this mapping.
	 * 
	 * @return The maximum index in series I contained in this mapping
	 */
	public int maxI();

	/**
	 * Method that returns the maximum index in series J that is contained within
	 * this mapping.
	 * 
	 * @return The maximum index in series J contained in this mapping
	 */
	public int maxJ();

	/**
	 * Method that adds a mapping from a point at index i in series I to a point j
	 * in series J at the beginning of the mapping path. Both i and j must be an
	 * index that is lower than or equal to indexes that were used as the previous
	 * first entry in the mapping.
	 * 
	 * @param i Index of series I that is to be mapped
	 * 
	 * @param j Index of series J that is to be mapped
	 */
	public void addFirst(int i, int j);

	/**
	 * Method that adds a mapping from a pint at index i in series I to a point j in
	 * series J at the end of the mapping path. Both i and j must be an indexes that
	 * is greater than or equal to indexes that were used as the previous last entry
	 * in the mapping.
	 * 
	 * @param i Index of series I that is to be mapped
	 * 
	 * @param j Index of series J that is to be mapped
	 */
	public void addLast(int i, int j);

	/**
	 * Method that gets a list of indices for points in series J that are mapped to
	 * index i in series I. Index i must be mapped to at least 1 point in J,
	 * otherwise an exception is thrown.
	 * 
	 * @param i Index in series I to get matching mapped indices in series J for
	 * 
	 * @return List of indices in J that are mapped to the input index of I
	 */
	public List<Integer> getMatchingIndexesForI(int i);

	/**
	 * Method that gets a list of indices for points in series I that are mapped to
	 * index j in series J. Index j must be mapped to at least 1 point in I,
	 * otherwise an exception is thrown.
	 * 
	 * @param j Index in series J to get matching mapped indices in series I for
	 * 
	 * @return List of indices in I that are mapped to the input index of J
	 */
	public List<Integer> getMatchingIndexesForJ(int j);

	/**
	 * Method that gets the mapping of index i (column) to index j (row) at the
	 * index position of this mapping path.
	 * 
	 * @param index The index in the mapping path to return the mapping for
	 * 
	 * @return The mapping of index i (column) to index j (row) at the indicated
	 *         index position of this mapping path
	 */
	public ColMajorCell get(int index);

	/**
	 * Method that gets an iterator for the mappings of index i (column) to index j
	 * (row) in this mapping path.
	 * 
	 * @return An iterator of the mappings of index i (column) to index j (row)
	 */
	public Iterator<ColMajorCell> getMapping();
}
