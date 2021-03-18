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
package edu.gsu.cs.dmlab.tracking.stages;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;

/**
 * This class calculates the edge weights in the graph association problem. It
 * is used for stage two as the edge between two tracks is calculated
 * differently than in stages three and above. Namely, it doesn't use the motion
 * model because it is assumed that track fragments are not long enough yet to
 * be able to calculate motion on them. An implementation of the edge weight
 * calculation based upon Kempton et. al
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class EdgeWeightCalculatorStageTwo extends BaseEdgeWeightCalculator {

	private ISTAppearanceModel appearanceModel;
	private ISTFrameSkipModel skipModel;

	private double appearWeight = 1;
	private double skipWeight = 1;

	private double sigFunctMean = 0.25;

	/**
	 * Constructor
	 * 
	 * @param enterLocProbCalc The model used for calculating enter probability on a
	 *                         track.
	 * 
	 * @param exitLocProbCalc  The model used for calculating exit probability on a
	 *                         track.
	 * 
	 * @param obsModel         The model used for calculating the probability of a
	 *                         track being plausible.
	 * 
	 * @param appearanceModel  The model used for calculating the probability of two
	 *                         tracks being linked base on their visual similarity.
	 * 
	 * @param skipModel        The model used for calculating the probability that a
	 *                         track skipped n frames before being detected again.
	 */
	public EdgeWeightCalculatorStageTwo(ISTLocationProbCal enterLocProbCalc, ISTLocationProbCal exitLocProbCalc,
			ISTObsModel obsModel, ISTAppearanceModel appearanceModel, ISTFrameSkipModel skipModel) {
		super(enterLocProbCalc, exitLocProbCalc, obsModel);

		if (appearanceModel == null)
			throw new IllegalArgumentException("Apperance Model cannot be null.");
		if (skipModel == null)
			throw new IllegalArgumentException("Frame Skip Model cannot be null.");

		this.appearanceModel = appearanceModel;
		this.skipModel = skipModel;
	}

	/**
	 * Constructor
	 * 
	 * @param enterLocProbCalc The model used for calculating enter probability on a
	 *                         track.
	 * 
	 * @param exitLocProbCalc  The model used for calculating exit probability on a
	 *                         track.
	 * 
	 * @param obsModel         The model used for calculating the probability of a
	 *                         track being plausible.
	 * 
	 * @param appearanceModel  The model used for calculating the probability of two
	 *                         tracks being linked base on their visual similarity.
	 * 
	 * @param skipModel        The model used for calculating the probability that a
	 *                         track skipped n frames before being detected again.
	 * 
	 * @param entExitMult      The multiplier value used to weight the enter/exit
	 *                         edges values.
	 * 
	 * @param obsMult          The multiplier value used to weight the observation
	 *                         edge values.
	 * 
	 * @param assocMult        The multiplier value used to weight the association
	 *                         edge values.
	 */
	public EdgeWeightCalculatorStageTwo(ISTLocationProbCal enterLocProbCalc, ISTLocationProbCal exitLocProbCalc,
			ISTObsModel obsModel, ISTAppearanceModel appearanceModel, ISTFrameSkipModel skipModel, double entExitMult,
			double obsMult, double assocMult) {
		super(enterLocProbCalc, exitLocProbCalc, obsModel, entExitMult, obsMult, assocMult);

		if (appearanceModel == null)
			throw new IllegalArgumentException("Apperance Model cannot be null.");
		if (skipModel == null)
			throw new IllegalArgumentException("Frame Skip Model cannot be null.");

		this.appearanceModel = appearanceModel;
		this.skipModel = skipModel;
	}

	/**
	 * Constructor
	 * 
	 * @param enterLocProbCalc The model used for calculating enter probability on a
	 *                         track.
	 * 
	 * @param exitLocProbCalc  The model used for calculating exit probability on a
	 *                         track.
	 * 
	 * @param obsModel         The model used for calculating the probability of a
	 *                         track being plausible.
	 * 
	 * @param appearanceModel  The model used for calculating the probability of two
	 *                         tracks being linked base on their visual similarity.
	 * 
	 * @param skipModel        The model used for calculating the probability that a
	 *                         track skipped n frames before being detected again.
	 * 
	 * @param entExitMult      The multiplier value used to weight the enter/exit
	 *                         edges values.
	 * 
	 * @param obsMult          The multiplier value used to weight the observation
	 *                         edge values.
	 * 
	 * @param assocMult        The multiplier value used to weight the association
	 *                         edge values.
	 * 
	 * @param appearWeight     The weight for the appearance model values in the
	 *                         association edge values.
	 * 
	 * @param skipWeight       The weight for the skip model values in the
	 *                         association edge values.
	 * 
	 */
	public EdgeWeightCalculatorStageTwo(ISTLocationProbCal enterLocProbCalc, ISTLocationProbCal exitLocProbCalc,
			ISTObsModel obsModel, ISTAppearanceModel appearanceModel, ISTFrameSkipModel skipModel, double entExitMult,
			double obsMult, double assocMult, double appearWeight, double skipWeight) {
		super(enterLocProbCalc, exitLocProbCalc, obsModel, entExitMult, obsMult, assocMult);

		if (appearanceModel == null)
			throw new IllegalArgumentException("Apperance Model cannot be null.");
		if (skipModel == null)
			throw new IllegalArgumentException("Frame Skip Model cannot be null.");

		this.appearanceModel = appearanceModel;
		this.skipModel = skipModel;
		this.appearWeight = appearWeight;
		this.skipWeight = skipWeight;
		this.sigFunctMean = (0.2 * this.appearWeight * this.skipWeight);
	}

	@Override
	public void finalize() throws Throwable {
		try {
			this.appearanceModel = null;
			this.skipModel = null;
		} finally {
			super.finalize();
		}
	}

	@Override
	protected double getEdgeProb(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {
		double p = 0;
		p += this.appearWeight * this.appearanceModel.calcProbAppearance(leftTrack, rightTrack);
		p += this.skipWeight * this.skipModel.getSkipProb(leftTrack, rightTrack);

		// System.out.println("Assoc: " + sigAll(p));
		return this.sigAll(p);
	}

	private double sigAll(double val) {
		double retVal = 1.0 / (1 + Math.exp(-4 * (val - this.sigFunctMean)));
		return retVal;
	}

}
