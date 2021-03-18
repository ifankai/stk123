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
package edu.gsu.cs.dmlab.datatypes.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datatypes.EventType;

class EventTypeTests {

	@Test
	void testFromString1() {
		String ans = "ar";
		EventType x = EventType.fromString(ans);
		assertEquals(EventType.ACTIVE_REGION, x);
	
	}
	
	
	@Test
	void testFromString2() {
		String ans = "ce";
		EventType x = EventType.fromString(ans);
		assertEquals(EventType.CME, x);
	}
	@Test
	void testFromString3() {
		String ans = "qs";
		EventType x = EventType.fromString(ans);
		assertEquals(EventType.QUIET_SUN, x);
	
	}
	@Test
	void testFromString4() {
		String ans = "cj";
		EventType x = EventType.fromString(ans);
		assertEquals(EventType.CORONAL_JET, x);
	
	}
	
	
	
	@Test
	void testtoQualifiedString1() {
		
		String ans = "ar";
		assertEquals(ans, EventType.ACTIVE_REGION.toQualifiedString());
	}
	
	@Test
	void testtoQualifiedString2() {
		
		String ans = "ce";
		assertEquals(ans, EventType.CME.toQualifiedString());
	}

	@Test
	void testtoQualifiedString4() {
		
		String ans = "ch";
		assertEquals(ans, EventType.CORONAL_HOLE.toQualifiedString());
	}
	
void testtoQualifiedString3() {
		
		String ans = "ef";
		assertEquals(ans, EventType.EMERGING_FLUX.toQualifiedString());
	}
	
	

}
