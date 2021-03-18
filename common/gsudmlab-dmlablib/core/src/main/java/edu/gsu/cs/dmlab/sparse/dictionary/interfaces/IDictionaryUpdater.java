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

import edu.gsu.cs.dmlab.exceptions.MatrixDimensionMismatch;
import edu.gsu.cs.dmlab.exceptions.VectorDimensionMismatch;
import smile.math.matrix.DenseMatrix;

/**
 * Interface for objects used to update a dictionary used in sparse coding. This
 * is generally done during the dictionary learning process.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface IDictionaryUpdater {

	/**
	 * Does a dictionary update based on some algorithm utilizing the input
	 * matrices.
	 * 
	 * @param dictionary
	 *            The dictionary to be updated.
	 * @param auxMatrixU
	 *            An auxiliary matrix used to update the dictionary.
	 * @param auxMatrixV
	 *            Another auxiliary matrix used to update the dictionary.
	 * @throws MatrixDimensionMismatch
	 *             Thrown when the operations cannot be completed because the
	 *             dimensions of the dictionary and the two auxiliary matrices
	 *             mismatch.
	 * @throws VectorDimensionMismatch
	 *             Thrown when an operation cannot be done be cause a dimension in
	 *             the auxiliary matrices do not work with the input dictionary.
	 */
	public void updateDictionary(DenseMatrix dictionary, DenseMatrix auxMatrixU, DenseMatrix auxMatrixV)
			throws MatrixDimensionMismatch, VectorDimensionMismatch;
}
