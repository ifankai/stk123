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

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import smile.math.matrix.DenseMatrix;

/**
 * Interfaces for objects intended to learn a dictionary used in sparse coding.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISparseDictionaryLearner {

	/**
	 * Trains the sparse dictionary for the passed Data.
	 * 
	 * @param X The data to train on. Each element in the list is a column vector in
	 *          the input matrix.
	 * 
	 * @return The sparse dictionary.
	 * 
	 * @throws MatrixDimensionMismatch When matrix dimensions are mismatched for
	 *                                 this operation.
	 * 
	 * @throws VectorDimensionMismatch If this is thrown then there was something
	 *                                 wrong internally.
	 */
	public DenseMatrix train(List<double[]> X) throws MatrixDimensionMismatch, VectorDimensionMismatch;

}
