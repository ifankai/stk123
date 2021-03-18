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

import org.joda.time.DateTime;
import org.joda.time.Interval;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingEventIndexer;
import edu.gsu.cs.dmlab.tracking.interfaces.ISTObsModel;
import smile.stat.distribution.PoissonDistribution;

/**
 * This class is used to calculate the observation cost of a given track using a
 * Poisson model of observation that uses the change in the number of events
 * detected in the current frame from the previous. It also uses the average
 * from a number of pervious frames.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class PoissonObsModel implements ISTObsModel {

	private static int NUMSPAN = 10;
	private ISTTrackingEventIndexer evntsIdxr;
	private int span;

	/**
	 * Constructor
	 * 
	 * @param evntsIdxr Indexer which is used to get the expected change per frame.
	 * @param span      The time span, in seconds, to calculate the expected change
	 *                  per frame on. (probably spanOf event type times the number
	 *                  of frames over which to calculate).
	 */
	public PoissonObsModel(ISTTrackingEventIndexer evntsIdxr, int span) {
		if (evntsIdxr == null)
			throw new IllegalArgumentException("Event Indexer cannot be null.");
		if (span <= 0)
			throw new IllegalArgumentException("Span must be greater than Zero.");

		this.evntsIdxr = evntsIdxr;
		this.span = span;

	}

	@Override
	public void finalize() throws Throwable {
		this.evntsIdxr = null;
	}

	@Override
	public double getObsProb(IBaseTemporalObject ev) {
		return this.getPoissonProb(ev);
	}

	private double getPoissonProb(IBaseTemporalObject event) {
		// get the start of the index
		DateTime start = this.evntsIdxr.getFirstTime();
		// DateTime end = this.evntsIdxr.getLastTime();
		// calculate the span before the current event to process expected
		// change.
		DateTime startProcessRange = event.getTimePeriod().getStart().minusSeconds(this.span * NUMSPAN);
		DateTime endProcessRange = event.getTimePeriod().getStart();

		Interval timePeriod = new Interval(startProcessRange, endProcessRange);
		int lambda = 0;
		int delta = 0;
		// if the start of the index is within the process period, we need to
		// fix the range. We don't need to worry about running past the end
		// because every event is guaranteed to be within the index by
		// definition.
		if (timePeriod.contains(start) || start.isEqual(timePeriod.getEnd()) || start.isAfter(timePeriod.getEnd())) {
			// first get the expected change from the beginning of the index to
			// the end of this event.
			Interval range;
			try {
				range = new Interval(start, event.getTimePeriod().getEnd());
			} catch (IllegalArgumentException e) {
				System.out.println(start.toLocalDate().toString());
				System.out.println(event.getTimePeriod().toString());
				throw e;
			}
			lambda = this.evntsIdxr.getExpectedChangePerFrame(range);

			// then get the expected change over the period of the event (just
			// how many events there are at this time). The event is guaranteed
			// to be within the index. So a check is not needed.
			range = event.getTimePeriod();
			delta = this.evntsIdxr
					.getExpectedChangePerFrame(new Interval(range.getStart(), range.getEnd().plus(this.span)));

			// We can calculate the proper way here
		} else {
			// First get the expected change from the beginning of the
			// processing period to the end of the processing period.
			lambda = this.evntsIdxr.getExpectedChangePerFrame(timePeriod);

			// Then get the expected change over the period of the event.
			// The event is guaranteed
			// to be within the index. So a check is not needed.
			Interval range = event.getTimePeriod();
			delta = this.evntsIdxr
					.getExpectedChangePerFrame(new Interval(range.getStart().plus(-this.span), range.getEnd()));
		}

		// System.out.println("Lambda: " + lambda);
		// System.out.println("Delta: " + delta);

		PoissonDistribution poiss = new PoissonDistribution(lambda + 1);
		double val = poiss.p(delta + 1);
		// System.out.println("Val: " + val);
		double retVal = 1.0 / (1 + Math.exp(-5 * (val - (poiss.p(lambda) / 4.0))));
		// System.out.println("Obs:" + retVal);
		return retVal;

	}
}
