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
package edu.gsu.cs.dmlab.temporal;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.RoundingStrategy;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.temporal.interfaces.ITemporalAligner;

/**
 * Performs minor adjustments to the timestamp of a spatiotemporal event report
 * to align with an epoch and step size in interpolation methods described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public class TemporalAligner implements ITemporalAligner {
	private IInterpolationFactory factory;
	private Duration step;
	private DateTime epoch;
	private RoundingStrategy strategy;

	/**
	 * Constructor that provides an epoch that is used to align the timestamp of
	 * each event report in passed in trajectories against. The alignment forces the
	 * timestamps to be an integer multiple number of steps from the input epoch
	 * time. The rounding strategy is also provided, it can be UP, DOWN, or ROUND.
	 * Where UP always takes the timestamp to the next closest integer multiple,
	 * down always takes the timestamp to the previous, and ROUND takes it to the
	 * closest integer multiple.
	 * 
	 * @param factory  The factory used to create new objects to return
	 * 
	 * @param epoch    The epoch time to align against
	 * 
	 * @param step     The step to use to force the timestamps to be an integer
	 *                 multiple of
	 * 
	 * @param strategy The rounding strategy
	 */
	public TemporalAligner(IInterpolationFactory factory, DateTime epoch, Duration step, RoundingStrategy strategy) {
		if (factory == null)
			throw new IllegalArgumentException("factory cannot be null in TemporalAligner constructor.");
		if (epoch == null)
			throw new IllegalArgumentException("epoch cannot be null in TemporalAligner constructor.");
		if (strategy == null)
			throw new IllegalArgumentException("strategy cannot be null in TemporalAligner constructor.");
		
		
		this.factory = factory;  
		this.step = step;
		this.epoch = epoch;
		this.strategy = strategy;

	}

	@Override
	public ISTInterpolationTrajectory alignEventsForStepFromEpoch(ISTInterpolationTrajectory trajectory) {

		ArrayList<ISTInterpolationEvent> tgpList = new ArrayList<ISTInterpolationEvent>(trajectory.getSTObjects());

		ArrayList<ISTInterpolationEvent> tgplist1 = new ArrayList<ISTInterpolationEvent>();

		for (int i = 0; i < tgpList.size() - 1; i++) {
  
			ISTInterpolationEvent prevTgp = tgpList.get(i);
			ISTInterpolationEvent nextTgp = tgpList.get(i + 1);
			long prevStartTime = prevTgp.getTimePeriod().getStartMillis();
			long nextStartTime = nextTgp.getTimePeriod().getStartMillis();
  
			prevStartTime = this.alignTimestamp(prevStartTime);
			nextStartTime = this.alignTimestamp(nextStartTime);

			Interval timePeriod = new Interval(prevStartTime, nextStartTime);

			Geometry geom = prevTgp.getGeometry();
			EventType type = prevTgp.getType();

			ISTInterpolationEvent val;
			if (tgpList.get(i).isInterpolated()) {
				val = factory.getSTEvent(timePeriod, type, geom);// call method that doesnt take an id
			} else {
				int id = prevTgp.getId();
				val = factory.getSTEvent(id, timePeriod, type, geom);
			}

			tgplist1.add(val);

		}

		if (tgpList.size() > 0) {
			int i = tgpList.size() - 1;
			ISTInterpolationEvent prevTgp = tgpList.get(i);

			long startTime = prevTgp.getTimePeriod().getStartMillis();
			long endTime = prevTgp.getTimePeriod().getEndMillis();

			startTime = this.alignTimestamp(startTime);
			endTime = this.alignTimestamp(endTime);

			Interval timePeriod = new Interval(startTime, endTime);

			Geometry geom = prevTgp.getGeometry();
			EventType type = prevTgp.getType();

			ISTInterpolationEvent val;
			if (tgpList.get(i).isInterpolated()) {
				val = factory.getSTEvent(timePeriod, type, geom);// call method that doesnt take an id
			} else {
				int id = prevTgp.getId();
				val = factory.getSTEvent(id, timePeriod, type, geom);
			}

			tgplist1.add(val);
		}

		return (factory.getSTTrajectory(tgplist1));
	}

	private long alignTimestamp(long timestamp) {
		if (this.strategy == RoundingStrategy.DOWN) {
			long remainder = (timestamp - this.epoch.getMillis()) % this.step.getMillis();
			if (remainder != 0) {
			timestamp -=   remainder;
			}
		} else if (this.strategy == RoundingStrategy.UP) {
			long remainder = (timestamp - this.epoch.getMillis()) % this.step.getMillis();
			if (remainder != 0) {
				timestamp += (this.step.getMillis() - remainder);
			}
		} else if (this.strategy == RoundingStrategy.ROUND) {
			long remainder = (timestamp - this.epoch.getMillis()) % this.step.getMillis();
			if (remainder >= this.step.getMillis() / 2) { // round up
				timestamp += (this.step.getMillis() - remainder);  
			} else { // round down
				timestamp -=  remainder;
			}

		}
		return timestamp;
	}

	@Override
	public ISTInterpolationEvent alignEventsForInterpolation(ISTInterpolationEvent input) {
	
		
	ISTInterpolationEvent ev = input;
    
    long start_ev= ev.getTimePeriod().getStartMillis();
    long end_ev = ev.getTimePeriod().getEndMillis();
    
    start_ev = this.alignTimestamp(start_ev);
    end_ev = this.alignTimestamp(end_ev);
    
    Interval timePeriod = new Interval(start_ev,end_ev);
    
    Geometry geom = input.getGeometry();
    EventType type = input.getType();
    
    ISTInterpolationEvent res;
    if (ev.isInterpolated()== true) {
        res = factory.getSTEvent(timePeriod, type, geom);
    }
    else {
        int id = ev.getId();
        res = factory.getSTEvent(id, timePeriod, type, geom);
    }
    
    return res;}

}
