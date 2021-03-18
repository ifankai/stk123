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
package edu.gsu.cs.dmlab.solgrind.base;

/**
 * 
 * @author Berkay Aydin, Data Mining Lab, Georgia State University
 * 
 */
public class EventType implements Comparable<EventType> {

	private String type;

	public EventType(String EventType) {
		type = EventType.toLowerCase();
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object t) {

		if (this == t)
			return true;

		if (!(t instanceof EventType)) {
			return false;
		}
		return this.type.equalsIgnoreCase(((EventType) t).getType());
	}

//	public String getColor() {
//		switch (type) {
//		case "ar":
//			return "red";
//		case "ch":
//			return "black";
//		case "ef":
//			return "green";
//		case "fi":
//			return "blue";
//		case "sg":
//			return "purple";
//		case "ss":
//			return "yellow";
//		case "f1":
//			return "red";
//		case "f2":
//			return "black";
//		case "f3":
//			return "green";
//		case "f4":
//			return "blue";
//		case "f5":
//			return "purple";
//		case "f6":
//			return "yellow";
//		}
//		return "grey";
//	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	public boolean equalsIgnoreCase(EventType t) {
		return this.type.equalsIgnoreCase(t.type);
	}

	public String toString() {
		return type;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new EventType(type);
	}

	@Override
	public int compareTo(EventType o) {
		return type.compareToIgnoreCase(o.getType());
	}
}
