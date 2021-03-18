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
package edu.gsu.cs.dmlab.tracking.stages.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;
import edu.gsu.cs.dmlab.tracking.stages.EdgeWeightCalculatorStageTwo;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EdgeWeightCalculatorStageTwoTests {

	@Test
	public void testEdgeWeightCalculatorStageTwoThrowsOnNullEnterModel() throws IllegalArgumentException {
		// ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(null, exitLocProbCalc, obsModel,
					appearanceModel, skipModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoThrowsOnNullExitModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		// ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, null, obsModel,
					appearanceModel, skipModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoThrowsOnNullObsModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		// ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc, null,
					appearanceModel, skipModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoThrowsOnNullAppearanceModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		// ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc,
					obsModel, null, skipModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoThrowsOnNullSkipModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		// ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc,
					obsModel, appearanceModel, null);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoSourceEdgeWeight() {
		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		double probVal = 0.5;
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		when(enterLocProbCalc.calcProb(ev)).thenReturn(probVal);

		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc, obsModel,
				appearanceModel, skipModel);
		double result = calculator.sourceEdgeWeight(ev);
		assertTrue(result - (-Math.log(probVal) * 120) < 0.001);
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoSinkEdgeWeight() {
		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		double probVal = 0.5;
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		when(exitLocProbCalc.calcProb(ev)).thenReturn(probVal);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc, obsModel,
				appearanceModel, skipModel);
		double result = calculator.sinkEdgeWeight(ev);
		assertTrue(result - (-Math.log(probVal) * 120) < 0.001);
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoObsEdgeWeight() {
		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		double probVal = 0.5;
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		when(obsModel.getObsProb(ev)).thenReturn(probVal);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc, obsModel,
				appearanceModel, skipModel);

		double result = calculator.observationEdgeWeight(ev);
		assertTrue(result - (Math.log(probVal / (1.0 - probVal)) * (120 * 1.05)) < 0.001);
	}

	@Test
	public void testEdgeWeightCalculatorStageTwoAssociationEdgeWeight() {
		ISTTrackingTrajectory trk = mock(ISTTrackingTrajectory.class);
		ISTTrackingTrajectory trk2 = mock(ISTTrackingTrajectory.class);
		double probVal = 0.5;

		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);

		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		when(appearanceModel.calcProbAppearance(trk, trk2)).thenReturn(probVal);

		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		when(skipModel.getSkipProb(trk, trk2)).thenReturn(probVal);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageTwo(enterLocProbCalc, exitLocProbCalc, obsModel,
				appearanceModel, skipModel);
		double result = calculator.associationEdgeWeight(trk, trk2);
		assertTrue(result - (-(Math.log(probVal * probVal) * 120)) < 0.001);
	}
}
