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

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

/**
 * Base abstract class used for calculating edge weights on a DAG used for the
 * data association problem. Since different stages of Kempton et. al
 * <a href="http://dx.doi.org/10.1016/j.ascom.2015.10.005">2015</a> and
 * <a href="https://doi.org/10.3847/1538-4357/aae9e9"> 2018</a> use
 * different components for edge weight calculation, the getEdgeProb method must
 * be implemented in classes derived from this one.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public abstract class BaseEdgeWeightCalculator implements ISTEdgeWeightCalculator {

	private ISTLocationProbCal enterLocProbCalc;
	private ISTLocationProbCal exitLocProbCalc;
	private ISTObsModel obsModel;

	private double entExitMult = 5.0;
	private double obsMult = 75.0;
	private double assocMult = 235.0;

	/**
	 * Constructor that uses the default values for entExitMult, obsMult, and
	 * assocMult.
	 * 
	 * @param enterLocProbCalc The model used for calculating enter probability on a
	 *                         track.
	 * 
	 * @param exitLocProbCalc  The model used for calculating exit probability on a
	 *                         track.
	 * 
	 * @param obsModel         The model used for calculating the probability of a
	 *                         track being plausible.
	 */
	public BaseEdgeWeightCalculator(ISTLocationProbCal enterLocProbCalc, ISTLocationProbCal exitLocProbCalc,
			ISTObsModel obsModel) {
		if (enterLocProbCalc == null)
			throw new IllegalArgumentException("Enter Probability Calculator cannot be null.");
		if (exitLocProbCalc == null)
			throw new IllegalArgumentException("Exit Probability Calculator cannot be null.");
		if (obsModel == null)
			throw new IllegalArgumentException("Observation Edge Model cannot be null.");
		this.enterLocProbCalc = enterLocProbCalc;
		this.exitLocProbCalc = exitLocProbCalc;
		this.obsModel = obsModel;
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
	 * @param entExitMult      The multiplier for enter and exit probabilities.
	 * 
	 * @param obsMult          The multiplier for the observation edge.
	 * 
	 * @param assocMult        The multiplier for the association edge.
	 */
	public BaseEdgeWeightCalculator(ISTLocationProbCal enterLocProbCalc, ISTLocationProbCal exitLocProbCalc,
			ISTObsModel obsModel, double entExitMult, double obsMult, double assocMult) {
		if (enterLocProbCalc == null)
			throw new IllegalArgumentException("Enter Probability Calculator cannot be null.");
		if (exitLocProbCalc == null)
			throw new IllegalArgumentException("Exit Probability Calculator cannot be null.");
		if (obsModel == null)
			throw new IllegalArgumentException("Observation Edge Model cannot be null.");
		this.enterLocProbCalc = enterLocProbCalc;
		this.exitLocProbCalc = exitLocProbCalc;
		this.obsModel = obsModel;
		this.entExitMult = entExitMult;
		this.obsMult = obsMult;
		this.assocMult = assocMult;
	}

	@Override
	public void finalize() throws Throwable {
		this.enterLocProbCalc = null;
		this.exitLocProbCalc = null;
		this.obsModel = null;
	}

	@Override
	public double sourceEdgeWeight(ISTTrackingEvent event) {
		double entPd = this.enterLocProbCalc.calcProb(event);
		double entP = -(Math.log(entPd) * this.entExitMult);
		// System.out.println("Source Cost: " + entP);
		return entP;
	}

	@Override
	public double sinkEdgeWeight(ISTTrackingEvent event) {
		double exPd = this.exitLocProbCalc.calcProb(event);
		double exP = -(Math.log(exPd) * this.entExitMult);
		// System.out.println("Sink Cost: " + exP);
		return exP;
	}

	@Override
	public double observationEdgeWeight(ISTTrackingEvent event) {
		double retVal = this.obsModel.getObsProb(event);
		double obsCost = (Math.log((1 - retVal) / retVal) * this.obsMult);
		// System.out.println("Obs Cost: " + obsCost);
		return obsCost;
	}

	@Override
	public double associationEdgeWeight(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack) {
		double prob = this.getEdgeProb(leftTrack, rightTrack);
		double weightValD = -(Math.log(prob) * this.assocMult);
		// System.out.println("Assoc Cost: " + weightValD);
		return weightValD;
	}

	protected abstract double getEdgeProb(ISTTrackingTrajectory leftTrack, ISTTrackingTrajectory rightTrack);

}
