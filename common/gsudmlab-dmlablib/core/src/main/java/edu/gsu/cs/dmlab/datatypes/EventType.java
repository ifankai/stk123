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

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration used to identify which type of solar activity an event object
 * is an instance of.
 * 
 * @author Dustin Kempton, Surabhi Priya, Data Mining Lab, Georgia State University
 * 
 */
public enum EventType {

	ACTIVE_REGION, CME, CORONAL_DIMMING, CORONAL_HOLE, CORONAL_WAVE, FILAMENT, FILAMENT_ERUPTION, FILAMENT_ACTIVATION,
	FLARE, LOOP, OSCILLATION, SUNSPOT, EMERGING_FLUX, CORONAL_JET, PLAGE, OTHER, NOTHING_REPORTED, SIGMOID, SPRAY_SURGE,
	CORONAL_RAIN, CORONAL_CAVITY, ERUPTION, TOPOLOGICAL_OBJECT, HYPOTHESIS, UV_BURST, EXPLOSION_EVENT,
	PROMINENCE_BUBBLE, PEACOCK_TAIL, QUIET_SUN;

	/**
	 * Uses String representation to wrap into the ISDSolarEventType class object.
	 * 
	 * @param input string representation
	 * 
	 * @return EventType object
	 */

	private static Map<EventType, String> enumMap = new HashMap<EventType, String>();

	static {
		enumMap.put(ACTIVE_REGION, "ar");
		enumMap.put(CME, "ce");
		enumMap.put(CORONAL_DIMMING, "cd");
		enumMap.put(CORONAL_HOLE, "ch");
		enumMap.put(CORONAL_WAVE, "cw");
		enumMap.put(FILAMENT, "fi");
		enumMap.put(FILAMENT_ERUPTION, "fe");
		enumMap.put(FILAMENT_ACTIVATION, "fa");
		enumMap.put(FLARE, "fl");
		enumMap.put(LOOP, "lp");
		enumMap.put(OSCILLATION, "os");
		enumMap.put(SUNSPOT, "ss");
		enumMap.put(EMERGING_FLUX, "ef");
		enumMap.put(CORONAL_JET, "cj");
		enumMap.put(PLAGE, "pg");
		enumMap.put(OTHER, "ot");
		enumMap.put(NOTHING_REPORTED, "nr");
		enumMap.put(SIGMOID, "sg");
		enumMap.put(SPRAY_SURGE, "sp");
		enumMap.put(CORONAL_RAIN, "cr");
		enumMap.put(CORONAL_CAVITY, "cc");
		enumMap.put(ERUPTION, "er");
		enumMap.put(TOPOLOGICAL_OBJECT, "tob");
		enumMap.put(HYPOTHESIS, "hy");
		enumMap.put(UV_BURST, "bu");
		enumMap.put(EXPLOSION_EVENT, "ee");
		enumMap.put(PROMINENCE_BUBBLE, "pb");
		enumMap.put(PEACOCK_TAIL, "pt");
		enumMap.put(QUIET_SUN, "qs");
	}

	private static Map<String, EventType> enumMap_string = new HashMap<String, EventType>();

	static {
		enumMap_string.put("ar", EventType.ACTIVE_REGION);
		enumMap_string.put("ce", EventType.CME);
		enumMap_string.put("cd", EventType.CORONAL_DIMMING);
		enumMap_string.put("ch", EventType.CORONAL_HOLE);
		enumMap_string.put("cw", EventType.CORONAL_WAVE);
		enumMap_string.put("fi", EventType.FILAMENT);
		enumMap_string.put("fe", EventType.FILAMENT_ERUPTION);
		enumMap_string.put("fa", EventType.FILAMENT_ACTIVATION);
		enumMap_string.put("fl", EventType.FLARE);
		enumMap_string.put("lp", EventType.LOOP);
		enumMap_string.put("os", EventType.OSCILLATION);
		enumMap_string.put("ss", EventType.SUNSPOT);
		enumMap_string.put("ef", EventType.EMERGING_FLUX);
		enumMap_string.put("cj", EventType.CORONAL_JET);
		enumMap_string.put("pg", EventType.PLAGE);
		enumMap_string.put("ot", EventType.OTHER);
		enumMap_string.put("nr", EventType.NOTHING_REPORTED);
		enumMap_string.put("sg", EventType.SIGMOID);
		enumMap_string.put("sp", EventType.SPRAY_SURGE);
		enumMap_string.put("cr", EventType.CORONAL_RAIN);
		enumMap_string.put("cc", EventType.CORONAL_CAVITY);
		enumMap_string.put("er", EventType.ERUPTION);
		enumMap_string.put("tob", EventType.TOPOLOGICAL_OBJECT);
		enumMap_string.put("hy", EventType.HYPOTHESIS);
		enumMap_string.put("bu", EventType.UV_BURST);
		enumMap_string.put("ee", EventType.EXPLOSION_EVENT);
		enumMap_string.put("pb", EventType.PROMINENCE_BUBBLE);
		enumMap_string.put("pt", EventType.PEACOCK_TAIL);
		enumMap_string.put("qs", EventType.QUIET_SUN);

	}

	/**
	 * 
	 * @param input ,abbreviated String input type of the Solar Event (ex: ar,
	 *              cr,er..)
	 * @return EventType (ex: ACTIVE_REGION, CORONAL_HOLE ,...)
	 */

	public static EventType fromString(String input) {
		String new_val = input.trim().toLowerCase();
		if (new_val.contains("tob") || new_val.contains("to"))
			new_val = "tob";

		EventType res = enumMap_string.get(new_val);
		return (res);
	}

	/**
	 * 
	 * 
	 * @return String representation of EventType object with just two/three
	 *         characters all in lower case,
	 */

	public String toQualifiedString() {

		String value = enumMap.get(this);

		return value.toLowerCase();
	}
}
