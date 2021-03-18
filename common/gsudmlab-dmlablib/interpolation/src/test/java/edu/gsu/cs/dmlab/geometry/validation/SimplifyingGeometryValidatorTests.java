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
package edu.gsu.cs.dmlab.geometry.validation;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.geometry.validation.interfaces.IGeometryValidator;

public class SimplifyingGeometryValidatorTests {

	@Test
	void testThrowsOnNullPolyValidator() {
		IGeometryValidator polygonValidator = null;// mock(IGeometryValidator.class);
		double simplifierDistance = 0;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new SimplifyingGeometryValidator(polygonValidator, simplifierDistance);
		});
	}

	@Test
	void testThrowsOnDistanceLessThanZero() {
		IGeometryValidator polygonValidator = mock(IGeometryValidator.class);
		double simplifierDistance = -1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new SimplifyingGeometryValidator(polygonValidator, simplifierDistance);
		});
	}
}
