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
package edu.gsu.cs.dmlab.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.apache.commons.math3.stat.inference.OneWayAnova;

import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.features.interfaces.IStatProducer;
import edu.gsu.cs.dmlab.imageproc.interfaces.IHistoComparator;
import edu.gsu.cs.dmlab.imageproc.interfaces.ISTHistogramProducer;

/**
 * Calculates the F-Statistic for a parameter when representing a given event
 * type.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class FStatProducer implements IStatProducer {

	private List<ISTTrackingTrajectory> tracks;
	private ISTHistogramProducer histoProducer;
	private IHistoComparator comparator;

	/**
	 * Constructor
	 * 
	 * @param tracks        Tracks to compute on
	 * 
	 * @param histoProducer A histogram producer to use.
	 * 
	 * @param comparator    A histogram comparator to use on the histograms.
	 */
	public FStatProducer(List<ISTTrackingTrajectory> tracks, ISTHistogramProducer histoProducer,
			IHistoComparator comparator) {

		if (tracks == null)
			throw new IllegalArgumentException("Tracks cannot be null in FStat Producer constructor.");
		if (histoProducer == null)
			throw new IllegalArgumentException("HistoProducer cannot be null in FStat Producer constructor.");
		if (comparator == null)
			throw new IllegalArgumentException("Comparator cannot be null in FStat Producer constructor.");

		this.tracks = tracks;
		this.histoProducer = histoProducer;
		this.comparator = comparator;
	}

	@Override
	public void finalize() throws Throwable {
		this.tracks = null;
		this.histoProducer = null;
		this.comparator = null;
	}

	@Override
	public float computeStat(ImageDBWaveParamPair[] dims) {
		ArrayList<double[]> comparValues = new ArrayList<double[]>();
		Random rnd = new Random(10);

		Lock loc = new ReentrantLock();

		// We want to repeat the procedure ten times
		for (int p = 0; p < 10; p++) {
			// loop over all the tracks and process for this paramCombo
			IntStream.range(0, this.tracks.size()).parallel().forEach(i -> {

				// process one track
				ISTTrackingTrajectory trk = tracks.get(i);

				ISTTrackingEvent[] tmpSameEvents = new ISTTrackingEvent[trk.size()];
				trk.getSTObjects().toArray(tmpSameEvents);

				// if we have enough IEvents in the track to calculate the same
				// and
				// difference historams then we can proceed
				if (tmpSameEvents.length >= 2) {

					// loop through each of the IEvents in the track
					for (int sameStartIdx = 0; sameStartIdx < tmpSameEvents.length - 2; sameStartIdx++) {

						// find some random track to compare this set of IEvents
						// to
						int idx = rnd.nextInt(tracks.size());
						ISTTrackingEvent[] tmpDiffEvents = new ISTTrackingEvent[tracks.get(idx).size()];
						tracks.get(idx).getSTObjects().toArray(tmpDiffEvents);

						// make sure there are enough IEvents in the different
						// track
						// to do the processing
						while (tmpDiffEvents.length < 2) {
							idx = rnd.nextInt(tracks.size());
							tmpDiffEvents = new ISTTrackingEvent[tracks.get(idx).size()];
							tracks.get(idx).getSTObjects().toArray(tmpDiffEvents);
						}

						// get the same and different IEvents for processing
						ISTTrackingEvent[] sameEvents = new ISTTrackingEvent[2];

						for (int k = 0; k < 2; k++) {
							sameEvents[k] = tmpSameEvents[sameStartIdx + k];
						}
						ISTTrackingEvent diffEvent = tmpDiffEvents[rnd.nextInt(tmpDiffEvents.length - 1)];

						int[][] sameHist1 = this.histoProducer.getHist(sameEvents[0], dims, true);
						int[][] sameHist2 = this.histoProducer.getHist(sameEvents[1], dims, false);

						int[][] diffHist1 = this.histoProducer.getHist(diffEvent, dims, false);
						double sameCompVal = this.comparator.compareHists(sameHist1, sameHist2);
						double diffCompVal = this.comparator.compareHists(sameHist1, diffHist1);
						double[] tmpCompVals = new double[2];
						tmpCompVals[0] = sameCompVal;
						tmpCompVals[1] = diffCompVal;
						loc.lock();
						comparValues.add(tmpCompVals);
						loc.unlock();
					}
				}
			});
		}

		return Float.valueOf(this.calcFstatValue(comparValues));
	}

	private float calcFstatValue(ArrayList<double[]> values) {
		OneWayAnova ow = new OneWayAnova();
		double[] sameCat = new double[values.size()];
		double[] diffCat = new double[values.size()];

		for (int i = 0; i < values.size(); i++) {
			double[] tmp = values.get(i);
			sameCat[i] = tmp[0];
			diffCat[i] = tmp[1];
		}

		ArrayList<double[]> catList = new ArrayList<double[]>();
		catList.add(sameCat);
		catList.add(diffCat);

		double fval = ow.anovaFValue(catList);

		if (Double.isNaN(fval))
			return 0;

		return (float) fval;
	}

}
