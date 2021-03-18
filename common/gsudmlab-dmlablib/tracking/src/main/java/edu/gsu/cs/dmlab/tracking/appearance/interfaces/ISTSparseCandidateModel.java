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

import smile.math.matrix.DenseMatrix;

/**
 * Interface for classes that are used as a candidate for matching evaluator.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISTSparseCandidateModel {

	/**
	 * Gets the likelihood value of the target candidate being a correct match based
	 * upon the error of the recreation matrix.
	 * 
	 * @param errorMatCandidate The error of the recreation matrix of the target
	 *                          candidate used to calculate the likelihood
	 * @param origWindow        The original area used to determine how much the
	 *                          candidate needs to scale to match
	 * @param candidateWindow   The target candidate area we are interested in.
	 * 
	 * @return The likelihood value of the target candidate being correct, based on
	 *         the recreation error.
	 */
	public double getCandidateProb(DenseMatrix errorMatCandidate, Envelope origWindow, Envelope candidateWindow);
}
