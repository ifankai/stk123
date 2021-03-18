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
package edu.gsu.cs.dmlab.sparse.dictionary;

import java.util.List;
import java.util.Random;

import edu.gsu.cs.dmlab.sparse.dictionary.interfaces.IDictionaryCleaner;
import smile.math.Math;
import smile.math.matrix.DenseMatrix;

/**
 * Class used to update a dictionary used in sparse coding. This is generally
 * done during the dictionary learning process. If elements are highly
 * correlated one is selected for replacement with a randomly drawn signal from
 * the input.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class CorrelationDictionaryCleaner implements IDictionaryCleaner {

	private Random rand = new Random(0);

	@Override
	public void cleanDictionary(DenseMatrix dictionary, List<double[]> input, DenseMatrix gram) {
		int k = dictionary.ncols();
		int n = dictionary.nrows();
		int M = input.size();

		boolean hadNan = false;
		for (int i = 0; i < k; i++) {
			for (int j = i; j < k; j++) {
				if ((j > i && Math.abs(gram.get(j, i) / Math.sqr(gram.get(i, i) * gram.get(j, j))) > 0.99999)
						|| (j == i && Math.abs(gram.get(j, i)) < 1e-4) || gram.get(j, i) == Double.NaN) {
					// remove element j and replace it with a random element
					// from the input.
					if (gram.get(j, i) == Double.NaN)
						hadNan = true;

					int ind = this.rand.nextInt(M);
					double[] newVal = new double[n];
					this.getCol(input, newVal, ind);
					double mean = Math.mean(newVal);
					double norm = Math.norm(newVal);
					// System.out.println("Norm: " + norm);
					for (int p = 0; p < n; p++) {
						if (norm > 1e-10)
							newVal[p] = (newVal[p] - mean) / norm;
						else
							newVal[p] = (newVal[p] - mean);

						dictionary.set(p, j, newVal[p]);
					}

					// Update the Gram
					double[] gramNewVal = new double[k];
					gramNewVal = dictionary.atx(newVal, gramNewVal);
					newVal = null;

					for (int l = 0; l < k; l++) {
						gram.set(l, j, gramNewVal[l]);
						gram.set(j, l, gramNewVal[l]);
					}
					gramNewVal = null;
				}
			}
		}
		if (hadNan)
			System.out.println("Had NaN");

	}

	private void getCol(List<double[]> A, double[] vect, int j) {
		double[] colVect = A.get(j);
		for (int i = 0; i < colVect.length; i++) {
			vect[i] = colVect[i];
		}
	}

}
