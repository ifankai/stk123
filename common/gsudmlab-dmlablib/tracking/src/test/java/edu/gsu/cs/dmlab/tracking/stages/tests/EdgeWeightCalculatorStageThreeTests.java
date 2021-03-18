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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingEvent;
import edu.gsu.cs.dmlab.tracking.appearance.interfaces.ISTAppearanceModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTFrameSkipModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTLocationProbCal;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTMotionModel;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;
import edu.gsu.cs.dmlab.tracking.stages.EdgeWeightCalculatorStageThree;
import edu.gsu.cs.dmlab.tracking.stages.interfaces.ISTEdgeWeightCalculator;

public class EdgeWeightCalculatorStageThreeTests {
	@Test
	public void testEdgeWeightCalculatorStageThreeThrowsOnNullEnterModel() throws IllegalArgumentException {
		// ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(null, exitLocProbCalc, obsModel,
					appearanceModel, skipModel, motionModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeThrowsOnNullExitModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		// ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, null, obsModel,
					appearanceModel, skipModel, motionModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeThrowsOnNullObsModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		// ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
					null, appearanceModel, skipModel, motionModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeThrowsOnNullAppearanceModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		// ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
					obsModel, null, skipModel, motionModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeThrowsOnNullSkipModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		// ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
					obsModel, appearanceModel, null, motionModel);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeThrowsOnNullMotionModel() throws IllegalArgumentException {
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		// ISTMotionModel motionModel = mock(ISTMotionModel.class);

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			@SuppressWarnings("unused")
			ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
					obsModel, appearanceModel, skipModel, null);
		});
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeSourceEdgeWeight() {
		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		double probVal = 0.5;
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		when(enterLocProbCalc.calcProb(ev)).thenReturn(probVal);

		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
				obsModel, appearanceModel, skipModel, motionModel);
		double result = calculator.sourceEdgeWeight(ev);
		assertTrue(result - (-Math.log(probVal) * 120) < 0.001);
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeSinkEdgeWeight() {
		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		double probVal = 0.5;
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		when(exitLocProbCalc.calcProb(ev)).thenReturn(probVal);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
				obsModel, appearanceModel, skipModel, motionModel);
		double result = calculator.sinkEdgeWeight(ev);
		assertTrue(result - (-Math.log(probVal) * 120) < 0.001);
	}

	@Test
	public void testEdgeWeightCalculatorStageThreeObsEdgeWeight() {
		ISTTrackingEvent ev = mock(ISTTrackingEvent.class);
		double probVal = 0.5;
		ISTLocationProbCal enterLocProbCalc = mock(ISTLocationProbCal.class);
		ISTLocationProbCal exitLocProbCalc = mock(ISTLocationProbCal.class);
		ISTObsModel obsModel = mock(ISTObsModel.class);
		when(obsModel.getObsProb(ev)).thenReturn(probVal);
		ISTAppearanceModel appearanceModel = mock(ISTAppearanceModel.class);
		ISTFrameSkipModel skipModel = mock(ISTFrameSkipModel.class);
		ISTMotionModel motionModel = mock(ISTMotionModel.class);

		ISTEdgeWeightCalculator calculator = new EdgeWeightCalculatorStageThree(enterLocProbCalc, exitLocProbCalc,
				obsModel, appearanceModel, skipModel, motionModel);
		double result = calculator.observationEdgeWeight(ev);
		assertTrue(Math.abs(result - (Math.log(probVal / (1.0 - probVal)) * (120 * 1.05))) < 0.001);
	}

}
