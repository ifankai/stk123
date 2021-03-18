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
package edu.gsu.cs.dmlab.tracking.appearance.interfaces;

import org.locationtech.jts.geom.Envelope;

import smile.math.matrix.SparseMatrix;

/**
 * Interface for classes used to create sparse histograms of events.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTSparseHistoCreator {

	/**
	 * Returns a histogram representing the rows of the sparse input matrix.
	 * 
	 * @param alpha The sparse matrix representing the area of interest to create
	 *              the histogram from the rows.
	 * @param bbox  The area that is represented by alpha.
	 * @return The histogram of the input matrix.
	 */
	public double[] createTargetHisto(SparseMatrix alpha, Envelope bbox);

	/**
	 * Returns a histogram representing the rows of the sparse input matrix.
	 * 
	 * @param alpha         The sparse matrix representing the area of interest to
	 *                      create the histogram from the rows.
	 * @param originalBBox  The original object area used to adjust weights by how
	 *                      much the candidate needs to scale to match the original.
	 * @param candidateBBox The candidate area that is represented by alpha.
	 * @return The histogram of the input matrix.
	 */
	public double[] createCandidateHisto(SparseMatrix alpha, Envelope originalBBox, Envelope candidateBBox);
}
