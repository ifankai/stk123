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
package edu.gsu.cs.dmlab.tracking;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import au.com.bytecode.opencsv.CSVReader;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;
import edu.gsu.cs.dmlab.geometry.GeometryUtilities;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import smile.math.matrix.SparseMatrix;
import smile.math.Math;

/**
 * This class is used to calculate the probability of an event at the given
 * location being either the start of a track or the end of a track(enter
 * prob/exit prob). Which one is dependent upon the file it uses as input.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 			Michael Tinglof, Data Mining Lab, Georgia State University 
 * 
 */
public class LocationProbCalc implements ISTLocationProbCal {

	private SparseMatrix mat;
	private double max;
	private int scalFactor;

	/**
	 * Constructor
	 * 
	 * @param fileLoc   The location of the input file to open and read for data.
	 * @param regionDiv The divisor to make the dimensions of the input event match
	 *                  that of the input file.
	 */
	public LocationProbCalc(String fileLoc, int regionDiv) {
		if (fileLoc == null)
			throw new IllegalArgumentException("FileLocation cannot be null");

		this.mat = this.getProbMat(fileLoc);
		this.scalFactor = regionDiv;
		this.max = Math.max(this.mat.values());
	}

	@Override
	public void finalize() throws Throwable {
		this.mat = null;
	}

	private SparseMatrix getProbMat(String fileLoc) {
		char SEPERATOR = ',';
		try (CSVReader reader = new CSVReader(new FileReader(fileLoc), SEPERATOR)) {

			List<String[]> rows = reader.readAll();
			double[][] vals = new double[rows.size()][rows.size()];
			for (int i = 0; i < rows.size(); i++) {
				String[] row = rows.get(i);
				for (int j = 0; j < row.length; j++) {
					vals[i][j] = Double.parseDouble(row[j]);
				}
			}
			SparseMatrix mat = new SparseMatrix(vals);
			return mat;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public double calcProb(ISpatialTemporalObj ev) {
		Geometry scaledPoly = GeometryUtilities.scaleGeometry(ev.getGeometry(), this.scalFactor);
		Envelope scaledBoundingBox = scaledPoly.getEnvelopeInternal();

		double returnValue = 0;
		int count = 0;

		for (int x = (int) scaledBoundingBox.getMinX(); x <= scaledBoundingBox.getMaxX(); x++) {
			for (int y = (int) scaledBoundingBox.getMinY(); y <= scaledBoundingBox.getMaxY(); y++) {

				double tmp;
				if ((x > -1 && x < this.mat.ncols()) && (y > -1 && y < this.mat.nrows())) {
					tmp = this.mat.get(x, y);
				} else {
					tmp = 0.0;
				}

				returnValue += tmp;
				count++;
			}
		}

		// if (minVal > 0) {
		// returnValue = minVal / this.max;
		// } else {
		returnValue = returnValue / count;
		returnValue = returnValue / this.max;
		// }

		// if we can't calculate a value, who knows what the probability is.
		// We'll just cal it 50/50.
		if (returnValue <= 0) {
			returnValue = 0.5;
		}

		// System.out.println("LocProb: " + returnValue);
		return returnValue;
	}
}
