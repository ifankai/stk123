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
package edu.gsu.cs.dmlab.datatypes;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;

/**
 * Is a generic spatialtemporal object type. One should derive a class by
 * extending this class if a specific implementation for your specific project
 * is needed.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class GeneralSTObject extends BaseTemporalObject implements ISpatialTemporalObj {

	private Geometry geometry = null;

	public GeneralSTObject(Interval timePeriod, Geometry geometry) {
		super(timePeriod);
		if (geometry == null)
			throw new IllegalArgumentException("Geometry cannot be null in GeneralSTObject constructor.");
		this.geometry = geometry;
	}

	@Override
	public Point getCentroid() {
		return this.geometry.getCentroid();
	}

	@Override
	public Envelope getEnvelope() {
		return this.geometry.getEnvelopeInternal();
	}

	@Override
	public Geometry getGeometry() {
		return this.geometry;
	}

	@Override
	public double getVolume() {
		return getTimePeriod().toDurationMillis() * this.getGeometry().getArea();
	}

}
