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

import edu.gsu.cs.dmlab.datatypes.interfaces.IISDEventReport;
import edu.gsu.cs.dmlab.exceptions.InvalidAttributeException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Class used to represent the Solar Event reports that have been pulled from
 * HEK.
 * 
 * 
 * @author Surabhi Priya, Dustin Kempton Data Mining Lab, Georgia State University
 *
 */
public class ISDSolarEventReport implements IISDEventReport {

	private EventType eventType;
	private JsonObject eventJson;

	/**
	 * Constructor that expects a JsonObject as input. The object is also expected
	 * to contain the "event_type" attribute and will throw an exception if this
	 * attribute is not present.
	 * 
	 * @param jsonInput event input in JSONObject format
	 */
	public ISDSolarEventReport(JsonObject jsonInput) {
		if (jsonInput == null)
			new IllegalArgumentException("Json Input cannot be null");

		this.eventJson = jsonInput;

		try {
			this.eventType = EventType.fromString(this.getAttr("event_type"));
		} catch (InvalidAttributeException e) {
			throw new IllegalArgumentException("Json Input does not contain event type.");
		}

	}

	public ISDSolarEventReport(JsonObject jsonInput, EventType eventType) {
		if (jsonInput == null)
			throw new IllegalArgumentException("Json Input cannot be null");
		if (eventType == null)
			throw new IllegalArgumentException("EventType cannot be null");

		this.eventJson = jsonInput;
		this.eventType = eventType;
	}

	@Override
	public String getAttr(String attr) throws InvalidAttributeException {
		// If eventJson does not have requested attribute
		if (!eventJson.has(attr))
			throw new InvalidAttributeException("Attribute " + attr + " does not exit in object.");

		// Retrieving jsonElement attribute from eventJson
		JsonElement jsonElement = eventJson.get(attr);
		if (jsonElement != null && jsonElement.isJsonArray()) {
			// Return jsonElement as String
			return jsonElement.toString();
		}
		return jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : null;
	}

	@Override
	public EventType getEventType() {
		return this.eventType;
	}

}
