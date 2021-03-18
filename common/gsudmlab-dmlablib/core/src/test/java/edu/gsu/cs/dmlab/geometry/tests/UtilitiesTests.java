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
package edu.gsu.cs.dmlab.geometry.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import edu.gsu.cs.dmlab.geometry.GeometryUtilities;

public class UtilitiesTests {

	@Test
	public void testScalePolygonOnX() {
		int[] xArr = { 4, 4, 8, 8 };
		int[] yArr = { 4, 8, 8, 4 };
		int divisor = 4;

		Coordinate[] coords = new Coordinate[xArr.length + 1];
		for (int i = 0; i < coords.length; i++) {
			double x = xArr[i % xArr.length];
			double y = yArr[i % xArr.length];
			coords[i] = new Coordinate(x, y);
		}

		GeometryFactory geoFactory = new GeometryFactory();
		Geometry poly = geoFactory.createLinearRing(coords);
		Geometry scaledPgon = GeometryUtilities.scaleGeometry(poly, divisor);

		int[] xResultsArr = new int[xArr.length];
		coords = scaledPgon.getCoordinates();
		for (int i = 0; i < xArr.length; i++) {
			xResultsArr[i] = (int) coords[i].getX();
		}
		assertArrayEquals(new int[] { xArr[0] / divisor, xArr[1] / divisor, xArr[2] / divisor, xArr[3] / divisor },
				xResultsArr);
	}

	@Test
	public void testScalePolygonOnY() {
		int[] xArr = { 4, 4, 8, 8 };
		int[] yArr = { 4, 8, 8, 4 };
		int divisor = 4;

		Coordinate[] coords = new Coordinate[xArr.length + 1];
		for (int i = 0; i < coords.length; i++) {
			double x = xArr[i % xArr.length];
			double y = yArr[i % xArr.length];
			coords[i] = new Coordinate(x, y);
		}

		GeometryFactory geoFactory = new GeometryFactory();
		Geometry poly = geoFactory.createLinearRing(coords);
		Geometry scaledPgon = GeometryUtilities.scaleGeometry(poly, divisor);

		int[] yResultsArr = new int[yArr.length];
		coords = scaledPgon.getCoordinates();
		for (int i = 0; i < yArr.length; i++) {
			yResultsArr[i] = (int) coords[i].getY();
		}
		assertArrayEquals(new int[] { yArr[0] / divisor, yArr[1] / divisor, yArr[2] / divisor, yArr[3] / divisor },
				yResultsArr);
	}

	@Test
	public void testScaleBoundingBoxX() {
		int x = 4;
		int y = 16;
		int height = 32;
		int width = 8;
		Envelope rect = new Envelope(x, x + width, y, y + height);
		int divisor = 4;
		Envelope scaledRect = GeometryUtilities.scaleEnvelope(rect, divisor);

		assertTrue(scaledRect.getMinX() == x / divisor);

	}

	@Test
	public void testScaleBoundingBoxY() {
		int x = 4;
		int y = 16;
		int height = 32;
		int width = 8;
		Envelope rect = new Envelope(x, x + width, y, y + height);
		int divisor = 4;
		Envelope scaledRect = GeometryUtilities.scaleEnvelope(rect, divisor);

		assertTrue(scaledRect.getMinY() == y / divisor);

	}

	@Test
	public void testScaleBoundingBoxHeight() {
		int x = 4;
		int y = 16;
		int height = 32;
		int width = 8;
		Envelope rect = new Envelope(x, x + width, y, y + height);
		int divisor = 4;
		Envelope scaledRect = GeometryUtilities.scaleEnvelope(rect, divisor);

		assertTrue(scaledRect.getHeight() == height / divisor);

	}

	@Test
	public void testScaleBoundingBoxWidth() {
		int x = 4;
		int y = 16;
		int height = 32;
		int width = 8;
		Envelope rect = new Envelope(x, x + width, y, y + height);
		int divisor = 4;
		Envelope scaledRect = GeometryUtilities.scaleEnvelope(rect, divisor);

		assertTrue(scaledRect.getWidth() == width / divisor);

	}
}
