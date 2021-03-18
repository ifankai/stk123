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
package edu.gsu.cs.dmlab.solgrind.fileio;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import au.com.bytecode.opencsv.CSVReader;
import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;

/**
 * This class parses the tracks for now.
 * 
 * @author berkay - Nov 27, 2016
 *
 */
public class CSVDataParser extends DataParser {

	public static String TRAJECTORY_DATA_DIR_PATH = "/Users/ahmetkucuk/Documents/Research/Berkay/interpolated_2012_v2/";
	public static final String[] COLUMN_NAMES = { "instanceID", "starttime", "endtime", "geom_polygon" };
	// public static final String[] COLUMN_NAMES = {"instanceID", "starttime",
	// "endtime",
	// "geom_polygon", "geom_mbr", "eventId", "kb_archivid", "interpolated "};

	public static final String timestampFormat = "LONG"; // or TIMESTAMP
	public static final WKTReader wktReader = new WKTReader();
	public static CSVReader csvReader;
	public static final char SEPERATOR = '\t';
	public static final String FILE_NAME_EXTENSION = ".csv";
	public static int MP_FIX_COUNT = 0;

	public CSVDataParser() {

	}

	public CSVDataParser(String path) {
		TRAJECTORY_DATA_DIR_PATH = path;
	}

	public static void setDataDirectoryPath(String dir) {
		TRAJECTORY_DATA_DIR_PATH = dir;
	}

	@Override
	public Set<Instance> readInstances(EventType e) {

		Map<String, Instance> resultMap = new HashMap<>();
		try {
			Path file = Paths.get(TRAJECTORY_DATA_DIR_PATH, e.getType() + FILE_NAME_EXTENSION);
			csvReader = new CSVReader(new FileReader(file.toFile()), SEPERATOR);
			System.out.println("Reading... " + file.toAbsolutePath());

			String[] nextLine;
			while ((nextLine = csvReader.readNext()) != null) {
				if (nextLine != null) {
					// Verifying the read data here
					HashMap<String, String> rowMap = matchColumnsToData(COLUMN_NAMES, nextLine);
					if (rowMap == null) {
						System.out.println("ERROR: Error reading CSV file," + " column list does not match values!"
								+ "\nCheck the file: " + file.toAbsolutePath() + "\nColumn names"
								+ Arrays.toString(COLUMN_NAMES).toString());
					} else {
						TGPair tgp = rowmapToTGPair(rowMap);
						String instanceID = rowMap.get("instanceID");
						EventType eventType = e;

						// System.out.println(tgp);
						Instance instance = resultMap.get(instanceID);
						if (instance == null) { // if there are no instances here
							// System.out.println("what is my InstanceID: " + instanceID);
							instance = new Instance(instanceID, eventType);
							instance.getTrajectory().addTGPair(tgp);
							resultMap.put(instanceID, instance);
						} else {
							instance.getTrajectory().addTGPair(tgp);
						}
					}
					// System.out.print(nextLine.length + " --"); //DEBUG-ONLY
					// System.out.println(Arrays.toString(nextLine)); //DEBUG-ONLY
				}
			}
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		System.out.println("Results:::" + resultMap.size()); // DEBUG-ONLY
		return new HashSet<>(resultMap.values());
	}

	/**
	 * Read the instances in a given folder's CSV files to an instance map This is
	 * usually for data mining operations in solgrind
	 * 
	 * @param eventTypes arraylist of event types that will be used as file names
	 *                   too
	 * @return instance map (EventType to a Colelction of Instances)
	 */
	public Map<EventType, Collection<Instance>> readToInstanceMap(Collection<EventType> eventTypes) {

		Map<EventType, Collection<Instance>> instanceMap = new HashMap<>();
		for (EventType e : eventTypes) {
			Set<Instance> instances = readInstances(e);
			instanceMap.put(e, instances);
		}
		return instanceMap;
	}

	/**
	 * This is the time filtered version of the read to instances map method above.
	 * 
	 * @param eventTypes
	 * @param timeInterval
	 * @return
	 */
	public Map<EventType, Set<Instance>> readToInstanceMap(Collection<EventType> eventTypes, Interval timeInterval) {

		Map<EventType, Set<Instance>> instanceMap = new HashMap<>();
		for (EventType e : eventTypes) {
			Set<Instance> instances = readInstances(e, timeInterval);
			instanceMap.put(e, instances);
		}
		System.out.println("I FIXED " + MP_FIX_COUNT + " MULTIPOLYGONS, CHECK YOUR DATASET");
		return instanceMap;
	}

	private Set<Instance> readInstances(EventType e, Interval timeInterval) {

		Map<String, Instance> resultMap = new HashMap<>();
		try {
			Path file = Paths.get(TRAJECTORY_DATA_DIR_PATH, e.getType() + FILE_NAME_EXTENSION);
			csvReader = new CSVReader(new FileReader(file.toFile()), SEPERATOR);
			System.out.println("Reading... " + file.toAbsolutePath());

			String[] nextLine;
			while ((nextLine = csvReader.readNext()) != null) {
				if (nextLine != null) {
					// Verifying the read data here
					HashMap<String, String> rowMap = matchColumnsToData(COLUMN_NAMES, nextLine);
					if (rowMap == null) {
						System.out.println("ERROR: Error reading CSV file," + " column list does not match values!"
								+ "\nCheck the file: " + file.toAbsolutePath() + "\nColumn names"
								+ Arrays.toString(COLUMN_NAMES).toString());
					} else {
						TGPair tgp = rowmapToTGPair(rowMap);
						if (!tgp.getTInterval().overlaps(timeInterval)) {
							continue;
						}
						String instanceID = rowMap.get("instanceID");
						EventType eventType = e;

						// System.out.println(tgp);
						Instance instance = resultMap.get(instanceID);
						if (instance == null) { // if there are no instances here
							// System.out.println("what is my InstanceID: " + instanceID);
							instance = new Instance(instanceID, eventType);
							instance.getTrajectory().addTGPair(tgp);
							resultMap.put(instanceID, instance);
						} else {
							instance.getTrajectory().addTGPair(tgp);
						}
					}
					// System.out.print(nextLine.length + " --"); //DEBUG-ONLY
					// System.out.println(Arrays.toString(nextLine)); //DEBUG-ONLY
				}
			}
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		// System.out.println("Results:::" + resultMap); //DEBUG-ONLY
		return new HashSet<>(resultMap.values());
	}

	private static TGPair rowmapToTGPair(HashMap<String, String> rowMap) {
		long startTime = -1L;
		long endTime = -1L;
		if (timestampFormat.equals("LONG")) {
			startTime = new Long(rowMap.get("starttime"));
			endTime = new Long(rowMap.get("endtime"));
		} else if (timestampFormat.equalsIgnoreCase("TIMESTAMP")) {
			startTime = DateTime.parse(rowMap.get("starttime")).getMillis();
			endTime = DateTime.parse(rowMap.get("endtime")).getMillis();
		}

		Geometry geom = null;
		Geometry geom_polygon = null;
		Geometry geom_mbr = null;
		String geom_polygon_wkt = rowMap.get("geom_polygon");
		String geom_mbr_wkt = rowMap.get("geom_mbr");

		try {
			geom_polygon = wktReader.read(geom_polygon_wkt);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (geom_polygon == null || geom_polygon.isEmpty()) {
			try {
				geom_mbr = wktReader.read(geom_mbr_wkt);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (geom_mbr == null || geom_mbr.isEmpty()) {
				System.out.println("ERROR: There is no geometry!");
				return null;
			} else {
				geom = geom_mbr;
			}
		} else {

			if (geom_polygon instanceof Polygon) {
				geom = geom_polygon;
			} else {
				System.out.println("Geom_polygon expects a polygon, we have " + geom_polygon_wkt.getClass());
				System.out.println("I am applying a convex hull, darn you interpolated dataset");
				geom = geom_polygon.convexHull();
				MP_FIX_COUNT++;
			}

		}

		// validate geometry here, if not valid
		Geometry geom_r = null;
		if (!geom.isValid() || !(geom instanceof Polygon)) {
			// System.out.println("WARNING::: Validating HEK geometry");
			geom_r = geom.convexHull();// GeometryValidator.validateGeometry(geom);
		} else {
			geom_r = geom;
		}
		if (geom_r.isEmpty()) {
			geom_r = geom.convexHull();
			// System.out.println("ERROR! -- EMPTY GEOMETRY AFTER HEK VALIDATION!");
		}

		return new TGPair(startTime, endTime, geom_r);
	}

	/**
	 * Get two sorted string arrays that corresponds the column names and csvRows.
	 * Create a map where key is the column name and value is the attribute
	 * 
	 * @param columnNames
	 * @param nextLine
	 * @return
	 */
	private HashMap<String, String> matchColumnsToData(String[] columnNames, String[] csvRow) {
		if (columnNames.length != csvRow.length) {
			return null;
		} else {
			HashMap<String, String> rowMap = new HashMap<>();
			// System.out.println(Arrays.toString(csvRow));
			for (int i = 0; i < columnNames.length; i++) {
				rowMap.put(columnNames[i], csvRow[i]);
			}
			// System.out.println("RowMap: " + rowMap);
			return rowMap;
		}
	}

	public static List<String> fileList(Path directory) {
		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				fileNames.add(path.toString());
			}
		} catch (IOException ex) {
		}
		return fileNames;
	}

}
