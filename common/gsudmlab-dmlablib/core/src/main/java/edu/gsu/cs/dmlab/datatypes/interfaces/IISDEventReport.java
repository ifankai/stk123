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

import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.exceptions.InvalidAttributeException;

/**
 * This is the public interface for classes used to represent event reports
 * coming from the HEK.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IISDEventReport {

	/**
	 * Retrieves attribute from eventJson as element and returns as String.
	 * 
	 * @param attr The name of the attribute that the value of is desired.
	 * 
	 * @return String The string representation of the attribute value that was
	 *         requested, or null if the attribute value is null.
	 * 
	 * @throws InvalidAttributeException If the requested attribute name does not
	 *                                   exist in the object attribute list.
	 */
	public String getAttr(String attr) throws InvalidAttributeException;

	/**
	 * Method returning Event Type of this object.
	 * 
	 * @return eventType The event type of this object.
	 */
	public EventType getEventType();

}
