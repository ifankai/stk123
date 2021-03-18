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
package edu.gsu.cs.dmlab.imageproc;

import java.util.Arrays;

import edu.gsu.cs.dmlab.databases.interfaces.ISTImageDBConnection;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;
import edu.gsu.cs.dmlab.imageproc.interfaces.ISTHistogramProducer;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Matrix;

/**
 * HistogramProducer produces a 15 bin histogram for a passed in object
 * detection for each of the passed in image parameter/wavelength pairs. From
 * Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class HistogramProducer implements ISTHistogramProducer {

	private int histSize = 15;
	private ISTImageDBConnection imageDB;
	private double[] dimRange = { 0.0, 1.0 };
	private double binSize = (dimRange[1] - dimRange[0]) / (this.histSize);

	/**
	 * Constructor that takes in a connection to the image database.
	 * 
	 * @param imageDB The image database connection used to pull the image data from
	 *                the database.
	 */
	public HistogramProducer(ISTImageDBConnection imageDB) {
		if (imageDB == null)
			throw new IllegalArgumentException("IImageDBConnection cannot be null in HistogramProducer constructor.");
		this.imageDB = imageDB;
	}

	@Override
	public void finalize() throws Throwable {
		this.imageDB = null;
		this.dimRange = null;
	}

	@Override
	public int[][] getHist(ISpatialTemporalObj event, ImageDBWaveParamPair[] dims, boolean left) {

		int depth = dims.length;
		int[][] returnHistos = new int[depth][];
		try {
			Matrix[] dimMatArr = this.imageDB.getImageParamForEv(event, dims, left);
			for (int i = 0; i < depth; i++) {
				returnHistos[i] = this.calcHisto(((DenseMatrix) dimMatArr[i]).array(), dims[i].parameter);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return returnHistos;
	}

	private int[] calcHisto(double[][] rawMatData, int dim) {
		int[] data = new int[this.histSize];
		Arrays.fill(data, 0);

		double shifVal = Math.abs(dimRange[0]);
		for (int j = 0; j < rawMatData.length; j++) {
			for (int i = 0; i < rawMatData[j].length; i++) {
				try {
					int idx = (int) (Math.abs(rawMatData[j][i] - shifVal) / binSize);
					if (idx < data.length)
						data[idx]++;
					else if (idx == data.length)
						data[idx - 1]++;
					else
						System.out.println("Out of bounds on dim (" + dim + ") value: " + rawMatData[j][i]);

				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Dim: " + dim);
					throw e;
				}
			}
		}
		return data;
	}
}