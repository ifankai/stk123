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
package edu.gsu.cs.dmlab.solgrind.database.helper;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatasetParser {

	private final long INTERVAL;

	private static WKTReader geometryParser;

	private BufferedReader reader;
	private TGPair cursor;
	private long lastTrajectoryID;
	private String eventType;

	public DatasetParser(Path path, String eventType, long interval) {

		INTERVAL = interval;
		this.eventType = eventType;
		geometryParser = new WKTReader();

		try {
			reader = Files.newBufferedReader(path);
			parseLine(reader.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Instance next() throws IOException {
		// When cursor comes to end of file, we mark lastTrajectoryID to -1
		if (lastTrajectoryID == -1)
			return null;

		Instance instance = new Instance(String.valueOf(lastTrajectoryID), new EventType(eventType));
		long initialTrajectoryID = lastTrajectoryID;
		String line;
		while ((line = reader.readLine()) != null) {
			if (lastTrajectoryID != initialTrajectoryID)
				break;
			instance.getTrajectory().addTGPair(cursor);
			parseLine(line);
		}

		// If cursor comes to end of file, mark lastTrajectoryID to -1
		if (line == null) {

			// Since while loop cannot be executed for adding the last element,
			// we need to add it manually
			// Added value is the last element of the line.
			instance.getTrajectory().addTGPair(cursor);

			lastTrajectoryID = -1;
		}
		return instance;
	}

	private void parseLine(String line) {

		String[] tuples = line.split("\t");
		lastTrajectoryID = Long.parseLong(tuples[0]);
		cursor = lineToTGPair(tuples);
	}

	private TGPair lineToTGPair(String[] tuples) {

		long startTime = Long.parseLong(tuples[1]);
		long endTime = startTime + INTERVAL;

		String geomString = tuples[2];
		Geometry g = parseGeometry(geomString);
		if (g == null)
			return null;
		TGPair tgPair = new TGPair(startTime, endTime, g);
		return tgPair;
	}

	private Geometry parseGeometry(String geom) {

		try {
			return geometryParser.read(geom);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
