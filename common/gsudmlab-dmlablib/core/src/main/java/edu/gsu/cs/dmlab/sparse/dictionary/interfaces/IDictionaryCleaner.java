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
package edu.gsu.cs.dmlab.sparse.dictionary.interfaces;

import java.util.List;

import smile.math.matrix.DenseMatrix;

/**
 * Interface for objects used to clean a dictionary of unwanted elements used in
 * sparse coding. This is generally done during the dictionary learning process.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IDictionaryCleaner {

	/**
	 * Cleans the dictionary based on some algorithm given the input matrices.
	 * 
	 * @param dictionary Dictionary to clean.
	 * @param input      The input being used to create the dictionary, possibly for
	 *                   sampling a new element. The list is composed of a set of
	 *                   column vectors.
	 * @param gram       The Gram matrix of the dictionary that shows how correlated
	 *                   each element is with each other.
	 */
	public void cleanDictionary(DenseMatrix dictionary, List<double[]> input, DenseMatrix gram);
}
