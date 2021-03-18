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
package edu.gsu.cs.dmlab.factory;

import java.util.List;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationEvent;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTInterpolationTrajectory;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentInfo;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.IAlignmentPath;
import edu.gsu.cs.dmlab.distance.dtw.datatypes.interfaces.ICostMatrix;
import edu.gsu.cs.dmlab.distance.dtw.interfaces.IShapeSeriesAligner;
import edu.gsu.cs.dmlab.distance.dtw.search.interfaces.ISearchWindow;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IAggregateShapeSeries;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.ISeriesPoint;
import edu.gsu.cs.dmlab.distance.dtw.shapeseries.interfaces.IShapeSeries;
import edu.gsu.cs.dmlab.factory.interfaces.ISeriesAlignmentFactory;
import edu.gsu.cs.dmlab.factory.interfaces.IInterpolationFactory;

public class InterpolationFactory implements IInterpolationFactory, ISeriesAlignmentFactory {

	@Override
	public ISTInterpolationEvent getSTEvent(Interval timePeriod, EventType type, Geometry geometry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISTInterpolationEvent getSTEvent(int id, Interval timePeriod, EventType type, Geometry geometry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISTInterpolationTrajectory getSTTrajectory(List<ISTInterpolationEvent> events) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShapeSeriesAligner getAligner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICostMatrix getCostMatrix(ISearchWindow window) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlignmentPath getAlignmentPath(int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAlignmentInfo getAlignmentInfo(double distance, IAlignmentPath path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISeriesPoint getTSPoint(double[] values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShapeSeries getTimeSeries(int nDims) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAggregateShapeSeries getReducedTimeSeries(IShapeSeries series, int reducedSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchWindow getExpandedSearchWindow(IShapeSeries tsI, IShapeSeries tsJ, IAggregateShapeSeries shrunkI,
			IAggregateShapeSeries shrunkJ, IAlignmentPath shrunkWarpPath) {
		// TODO Auto-generated method stub
		return null;
	}

}
