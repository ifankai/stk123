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

/**
 * This is the global descriptor id and date for images stored in our databases.
 * It is used to reference a particular thumb-nail image, high resolution image,
 * or image parameters calculated on the original resolution image.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class ImageDBDateIdPair {

	/**
	 * Is the period over which the image we are referencing is valid. In some
	 * cases it is also used in combination with the id to uniquely identify an
	 * image.
	 */
	public Interval period;

	/**
	 * The identifier for the image we are referencing. Used in conjunction with
	 * the period, it uniquely identifies a single image.
	 */
	public int id;
}
