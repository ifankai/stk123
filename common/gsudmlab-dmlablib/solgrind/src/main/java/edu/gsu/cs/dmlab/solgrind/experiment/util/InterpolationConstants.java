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
package edu.gsu.cs.dmlab.solgrind.experiment.util;

import edu.gsu.cs.dmlab.solgrind.base.EventType;

public class InterpolationConstants {

	public static final long I_INTERVAL = 360000; // in milliseconds (this is 6
													// minutes)

	public static final long EPOCH = 0; // starts at 0, goes to every 10 minutes

	public static final RoundingStrategy ROUNDING = RoundingStrategy.ROUND;

	public static final double DENSIFIER_POINT_BOUND = 150;

	public static final int TIMESERIES_SIMILARITY_STEPS = 30;

	public static final DistanceMeasure TS_SCORE_TYPE = DistanceMeasure.DTW;

	public static final double SIMPLIFIER_DISTANCE_TOLERANCE = 1.0;

	public static final double PERCENTAGE_COORDINATE = 0.2;

	public static final int NUM_CLUSTERS = 2;

	public static final double AREAL_I_BUFFER_DISTANCE = 1.5;

	public enum RoundingStrategy {
		UP, DOWN, ROUND
	}

	public enum DistanceMeasure {
		DTW, EUCLIDEAN // we can add more if necessary
	}

	public static long getEventPropagation(EventType eventType) {
		if (eventType.equalsIgnoreCase(new EventType("AR")) || eventType.equalsIgnoreCase(new EventType("CH"))) {
			return 14400000;
		} else if (eventType.equalsIgnoreCase(new EventType("EF"))) {
			return 9600000;
		} else if (eventType.equalsIgnoreCase(new EventType("FI"))) {
			return 21600000;
		} else if (eventType.equalsIgnoreCase(new EventType("FL")) || eventType.equalsIgnoreCase(new EventType("SS"))) {
			return 600000;
		} else if (eventType.equalsIgnoreCase(new EventType("SG"))) {
			return 5400000;
		} else {
			return I_INTERVAL;
		}
	}

}
