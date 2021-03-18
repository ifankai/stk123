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
package edu.gsu.cs.dmlab.tracking.appearance;

import org.apache.commons.math3.special.Erf;

import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.imageproc.interfaces.IHistoComparator;
import edu.gsu.cs.dmlab.imageproc.interfaces.ISTHistogramProducer;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;

/**
 * Appearance model based upon the distance of two histograms of image
 * parameters. From Kempton et. al.
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class HistoAppearanceModel implements ISTAppearanceModel {

	double sameMean, sameStdDev, diffMean, diffStdDev;
	ISTHistogramProducer histoProducer;
	IHistoComparator histoComparator;
	ImageDBWaveParamPair[] dims;
	int compMethod;

	/**
	 * Constructor
	 * 
	 * @param sameMean        The mean of the normal distribution of histogram
	 *                        distances from events in the same track.
	 * 
	 * @param sameStdDev      The standard deviation of the normal distribution of
	 *                        histogram distances from events in the same track.
	 * 
	 * @param diffMean        The mean of the normal distribution of histogram
	 *                        distances from events in different tracks.
	 * 
	 * @param diffStdDev      The standard deviation of the normal distribution of
	 *                        histogram distances from events in different tracks.
	 * 
	 * @param histoProducer   The object used to produce histograms of events that
	 *                        are to be compared.
	 * 
	 * @param histoComparator The object that performs the histogram comparison and
	 *                        returns some distance value.
	 * 
	 * @param dims            The list of image parameters and wavelengths to use to
	 *                        produce the histograms for each event.
	 */
	public HistoAppearanceModel(double sameMean, double sameStdDev, double diffMean, double diffStdDev,
			ISTHistogramProducer histoProducer, IHistoComparator histoComparator, ImageDBWaveParamPair[] dims) {

		if (histoProducer == null)
			throw new IllegalArgumentException("Histogram Producer cannot be null.");
		if (histoComparator == null)
			throw new IllegalArgumentException("Histogram Comparator cannot be null.");
		this.sameMean = sameMean;
		this.sameStdDev = sameStdDev;
		this.diffMean = diffMean;
		this.diffStdDev = diffStdDev;
		this.histoProducer = histoProducer;
		this.histoComparator = histoComparator;
		this.dims = dims;
	}

	@Override
	public void finalize() throws Throwable {
		this.histoProducer = null;
		this.histoComparator = null;
		this.dims = null;
	}

	@Override
	public double calcProbAppearance(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {

		int[][] leftFrameHists = this.histoProducer.getHist(leftTrack.getLast(), this.dims, true);
		int[][] rightFrameHists = this.histoProducer.getHist(rightTrack.getFirst(), this.dims, false);
		double compVal = this.histoComparator.compareHists(leftFrameHists, rightFrameHists);
		double sameProb, diffProb;
		sameProb = this.calcNormProb(compVal, sameMean, sameStdDev);
		diffProb = this.calcNormProb(compVal, diffMean, diffStdDev);

		double likeliehood = sameProb / (sameProb + diffProb);
		likeliehood *= 10;
		return likeliehood;
	}

	private double calcNormProb(double x, double mean, double stdDev) {
		double val1 = normCDF(x - stdDev, mean, stdDev);
		double val2 = normCDF(x + stdDev, mean, stdDev);
		double val = val2 - val1;

		return val;
	}

	private double normCDF(double x, double mean, double stdDev) {
		double val = (x - mean) / (stdDev * Math.sqrt(2.0));
		val = 1.0 + Erf.erf(val);
		val = val * 0.5;
		return val;
	}
}
