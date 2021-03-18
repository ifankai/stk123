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
package edu.gsu.cs.dmlab.util.interfaces;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

/**
 * Interface for classes used to produce search areas based on an input and some
 * elappesed amount of time.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface ISTSearchAreaProducer {

	/**
	 * Create a search region based upon the input rectangle and the passed time.
	 * 
	 * @param bBox The input region.
	 * 
	 * @param span The elapsed time in days.
	 * 
	 * @return A search are based on the input.
	 */
	public Geometry getSearchRegion(Envelope bBox, double span);

	/**
	 * Create a search region based upon the input rectangle, previous movement
	 * vector, and the passed time.
	 * 
	 * @param bBox         The input region.
	 * 
	 * @param movementVect The previous movement vector.
	 * 
	 * @param span         The elapsed time in days.
	 * 
	 * @return A search region.
	 */
	public Geometry getSearchRegion(Envelope bBox, float[] movementVect, double span);

	/**
	 * Create a search region based upon the input rectangle and the elapsed time.
	 * However, it is in the opposite direction (back in time).
	 * 
	 * @param bBox The input region.
	 * 
	 * @param span The elapsed time in days to look back.
	 * 
	 * @return A search region.
	 */
	public Geometry getSearchRegionBack(Envelope bBox, double span);

}
