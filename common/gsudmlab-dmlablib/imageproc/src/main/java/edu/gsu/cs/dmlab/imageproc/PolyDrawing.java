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
package edu.gsu.cs.dmlab.imageproc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import edu.gsu.cs.dmlab.datatypes.interfaces.ISpatialTemporalObj;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class PolyDrawing {
	public static void drawEvent(BufferedImage src, ISpatialTemporalObj event, Color color, double scale) {
		drawPolygon(src, event.getGeometry(), color, scale);
	}

	public static void drawEvent(BufferedImage src, ISpatialTemporalObj event, Color color, double scale,
			double linewidth) {
		drawPolygon(src, event.getGeometry(), color, scale, linewidth);
	}

	public static void drawPolygon(BufferedImage src, Geometry pgon, Color color, double scale) {
		PolyDrawing.drawPolygon(src, pgon, color, scale, 0.5);
	}

	public static void drawPolygon(BufferedImage src, Geometry pgon, Color color, double scale, double linewidth) {

		Graphics2D g2d = src.createGraphics();
		BasicStroke bs = new BasicStroke((float) linewidth);
		g2d.setColor(color);
		g2d.setStroke(bs);

		Coordinate[] coords = pgon.getCoordinates();
		for (int i = 0; i < coords.length - 1; i++) {
			Coordinate cord = coords[i];
			Coordinate cord2 = coords[i + 1];
			g2d.drawLine((int) (cord.x * scale), (int) (cord.y * scale), (int) (cord2.x * scale),
					(int) (cord2.y * scale));
		}
		g2d.dispose();
		g2d = null;
		bs = null;
	}
}
