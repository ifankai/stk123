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
package edu.gsu.cs.dmlab.factory.interfaces;

import com.google.gson.JsonObject;

import edu.gsu.cs.dmlab.datatypes.interfaces.IISDEventReport;

/**
 * The interface for factories that will construct an IISDEventReport from a
 * JsonObject.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 *
 */
public interface IHEKObjectFactory {

	/**
	 * Produces an IISDEventReport from the input json object.
	 * 
	 * @param jsonInput The json object that contains the data for the
	 *                  IISDEventReport
	 * 
	 * @throws IllegalArgumentException If the object passed in is null.
	 */
	public IISDEventReport getEventReportFromJson(JsonObject jsonInput) throws IllegalArgumentException;

}
