///**
// * dmLabLib, a Library created for use in various projects at the Data Mining Lab 
// * (http://dmlab.cs.gsu.edu/) of Georgia State University (http://www.gsu.edu/).  
// *  
// * Copyright (C) 2019 Georgia State University
// * 
// * This program is free software: you can redistribute it and/or modify it under
// * the terms of the GNU General Public License as published by the Free Software
// * Foundation version 3.
// * 
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// * details.
// * 
// * You should have received a copy of the GNU General Public License along with
// * this program. If not, see <http://www.gnu.org/licenses/>.
// */
//package edu.gsu.cs.dmlab.imageproc;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//import java.util.Map;
//
//import edu.gsu.cs.dmlab.datatypes.EventType;
//import edu.gsu.cs.dmlab.datatypes.interfaces.IEvent;
//
///**
// * @author Data Mining Lab, Georgia State University
// * 
// */
//public class SOLEVdrawing {
//
//	int VideoScale = 1;
//
//	public SOLEVdrawing(int scale) {
//		this.VideoScale = scale;
//	}
//
//	public void drawEvent(BufferedImage src, BufferedImage icon, IEvent event, double scale, double linewidth) {
//		Color color;
//		if (event.getType() == EventType.ACTIVE_REGION) {
//			color = Color.CYAN;
//
//		} else if (event.getType() == EventType.CORONAL_HOLE) {
//			color = Color.YELLOW;
//
//		} else if (event.getType() == EventType.EMERGING_FLUX) {
//			color = Color.gray;
//
//		} else if (event.getType() == EventType.FLARE) {
//			color = Color.BLACK;
//
//		} else if (event.getType() == EventType.SIGMOID) {
//			color = Color.red;
//
//		} else if (event.getType() == EventType.FILAMENT) {
//			color = Color.MAGENTA;
//
//		} else if (event.getType() == EventType.SUNSPOT) {
//			color = Color.GREEN;
//
//		} else {
//			color = Color.WHITE;
//
//		}
//
//		drawPolygon(src, icon, event, color, scale, linewidth);
//	}
//
//	public void drawEvents(BufferedImage src, BufferedImage icon, ArrayList<IEvent> events,
//			Map<EventType, Color> colors, double scale, double linewidth) {
//		for (int i = 0; i < events.size(); i++) {
//			EventType type = events.get(i).getType();
//			// Color color = getEventTypeColor(type, 1);
//			// Color color= colors.get(events.get(i).getType());
//			// System.out.println(color.toString());
//			drawPolygon(src, icon, events.get(i), colors.get(type), scale, linewidth);
//		}
//	}
//
//	/*
//	 * private static Color getEventTypeColor(EventType type, int repetition) {
//	 * Color color; if(repetition < 1){ System.err.
//	 * println("You must be kidding me: repetition cannot be less than one. getEventTypeColor - SOLEVdrawing"
//	 * ); } if (repetition == 1) { if (type == EventType.ACTIVE_REGION) { color
//	 * = Color.CYAN; } else if (type == EventType.CORONAL_HOLE) { color =
//	 * Color.YELLOW; } else if (type == EventType.EMERGING_FLUX) { color =
//	 * Color.gray; } else if (type == EventType.FLARE) { color = Color.BLACK; }
//	 * else if (type == EventType.SIGMOID) { color = Color.red; } else if (type
//	 * == EventType.FILAMENT) { color = Color.MAGENTA; } else if (type ==
//	 * EventType.SUNSPOT) { color = Color.GREEN; } else { color = Color.WHITE; }
//	 * } else{ color = getEventTypeColor(type, repetition-1).brighter(); }
//	 * 
//	 * return color; }
//	 */
//	public void drawTimeandLegend(BufferedImage src, String time, Map<EventType, Color> colors) {
//		Graphics2D g3d = src.createGraphics();
//		g3d.setColor(Color.green);
//		g3d.setFont(new Font("Arial", Font.BOLD, 13 * VideoScale));
//		g3d.drawString(time, 5 * VideoScale, 15 * VideoScale);
//		// Draw Color Legend
//		int linebreak = 10 * VideoScale;
//		for (EventType key : colors.keySet()) {
//			g3d.setColor(colors.get(key));
//			g3d.setFont(new Font("Arial", Font.BOLD, 10 * VideoScale));
//			g3d.drawString(shortEventType(key.name()), 5 * VideoScale, 25 * VideoScale + linebreak);
//			linebreak += 8 * VideoScale;
//
//		}
//	}
//
//	public void drawPolygon(BufferedImage src, BufferedImage icon, IEvent evnt, Color color, double scale,
//			double linewidth) {
//
//		Graphics2D g2d = src.createGraphics();
//		BasicStroke bs = new BasicStroke((float) linewidth);
//		g2d.setColor(color);
//		g2d.setStroke(bs);
//
//		// Graphics2D g3d = src.createGraphics();
//		// g3d.setColor(Color.green);
//		// g3d.setFont(new Font("Arial", Font.BOLD, 13));
//		// g3d.drawString(evnt.getTimePeriod().getStart().toString(), 5,15);
//
//		for (int i = 0; i < evnt.getShape().xpoints.length - 1; i++) {
//			g2d.drawLine((int) (evnt.getShape().xpoints[i] * scale), (int) (evnt.getShape().ypoints[i] * scale),
//					(int) (evnt.getShape().xpoints[i + 1] * scale), (int) (evnt.getShape().ypoints[i + 1] * scale));
//		}
//		// icon = new BufferedImage(33, 15, icon.getType());
//		// g2d.drawImage(icon,2*VideoScale, 200*VideoScale, null);
//		g2d.dispose();
//	}
//
//	public String shortEventType(String type) {
//		if (type.equalsIgnoreCase("ACTIVE_REGION"))
//			return "AR";
//		if (type.equalsIgnoreCase("CORONAL_HOLE"))
//			return "CH";
//		if (type.equalsIgnoreCase("FILAMENT"))
//			return "FI";
//		if (type.equalsIgnoreCase("SIGMOID"))
//			return "SG";
//		if (type.equalsIgnoreCase("SUNSPOT"))
//			return "SS";
//		if (type.equalsIgnoreCase("FLARE"))
//			return "FL";
//		if (type.equalsIgnoreCase("EMERGING_FLUX"))
//			return "EF";
//		return "";
//
//	}
//}
