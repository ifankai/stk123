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
package edu.gsu.cs.dmlab.datatypes.interfaces;

import java.util.SortedSet;

import org.locationtech.jts.geom.Envelope;

/**
 * Is the base interface for a number of spatio-temporal trajectory objects. It
 * provides the minimum definitions need for an object of spatio-temporal
 * trajectory type in this library.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public interface ISpatialTemporalTrajectory<T> extends IBaseTemporalObject {

	/**
	 * Produces a sorted set of all the spatiotemporal objects contained in this
	 * trajectory. The objects are ordered by their start time.
	 * 
	 * @return A sorted set of all the spatiotemporal objects in this trajectory.
	 */
	public SortedSet<T> getSTObjects();

	/**
	 * 
	 * @return
	 */
	public Envelope getMBR();

	/**
	 * Gets the number of spatiotemporal objects contained in this trajectory.
	 * 
	 * @return The number of spatiotemporal objects in this trajectory.
	 */
	public int size();

	/**
	 * Computes the volume of this trajectory by using the volume of each of the
	 * spatiotemporal objects it contains.
	 * 
	 * @return The volume of the trajectory.
	 */
	public double getVolume();
}
