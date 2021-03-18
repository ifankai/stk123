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
package edu.gsu.cs.dmlab.solgrind.base.types.essential;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class TGPair implements Comparable<TGPair> {

	private Interval interval;
	private Geometry geometry;
	private boolean interpolated;

	public TGPair(long start, long end, Geometry geom) {
		interval = new Interval(start, end);
		geometry = new GeometryFactory().createGeometry(geom);
	}

	public TGPair(long start, long end, Geometry geom, String KBarchivID, Boolean interpolated) {
		interval = new Interval(start, end);
		geometry = new GeometryFactory().createGeometry(geom);

		this.interpolated = interpolated;
	}

	public TGPair(long start, long end, Geometry geom, String KBarchivID) {
		interval = new Interval(start, end);
		geometry = new GeometryFactory().createGeometry(geom);

	}

	public TGPair(long stime, Geometry geom) {

		interval = new Interval(stime);

		geometry = new GeometryFactory().createGeometry(geom);
	}

	public TGPair(DateTime start, DateTime end, Geometry geom) {
		interval = new Interval(start, end);
		geometry = new GeometryFactory().createGeometry(geom);
	}

	public TGPair(String startTimestamp, Geometry geom) {

		interval = new Interval(startTimestamp);

		geometry = new GeometryFactory().createGeometry(geom);
	}

	public Envelope getEnvelope() {
		return geometry.getEnvelopeInternal();
	}

	public double getVolume() {
		return this.getTInterval().toDurationMillis() * this.getGeometry().getArea();
	}

	public boolean getInterpolated() {
		return interpolated;
	}

	public void setInterpolated(boolean interpolated) {
		this.interpolated = interpolated;
	}

	/**
	 * Spatiotemporal overlaps method. Checks if the given time geometry pair both
	 * temporally and spatially intersects with the parameter tgp.
	 * 
	 * @return True if overlap occurs, False otherwise
	 */
	public boolean stOverlaps(TGPair tgp) {
		boolean tOverlaps = this.tOverlaps(tgp);
		boolean sOverlaps = this.sOverlaps(tgp);
		return tOverlaps && sOverlaps;
	}

	/**
	 * Spatial-only overlaps method. Checks if the geometries of the objects overlap
	 * in space
	 * 
	 * @param tgp
	 * @return
	 */
	public boolean sOverlaps(TGPair tgp) {
		return this.geometry.overlaps(tgp.geometry);
	}

	/**
	 * Temporal-only overlaps method Checks of the intervals of the objects overlap
	 * in time
	 * 
	 * @param tgp
	 * @return
	 */
	public boolean tOverlaps(TGPair tgp) {
		return this.interval.overlaps(tgp.interval);
	}

	public Interval getTInterval() {
		return this.interval;
	}

	public Geometry getGeometry() {
		return this.geometry;

	}

	public void setGeometry(Geometry geom) {
		this.geometry = new GeometryFactory().createGeometry(geom);
	}

	@Override
	public int compareTo(TGPair tgp) {
		if (this.getTInterval().getStartMillis() < tgp.getTInterval().getStartMillis()) {
			return -1;
		} else if (this.getTInterval().getStartMillis() > tgp.getTInterval().getStartMillis()) {
			return 1;
		} else /*
				 * if(tgp1.getTInterval().getStartTime() == tgp.getTInterval().getStartTime())
				 */ {
			return 0;
		}
	}

}
