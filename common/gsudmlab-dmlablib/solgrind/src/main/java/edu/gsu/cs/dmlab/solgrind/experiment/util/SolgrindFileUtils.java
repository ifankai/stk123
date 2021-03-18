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
package edu.gsu.cs.dmlab.solgrind.experiment.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import edu.gsu.cs.dmlab.solgrind.database.helper.DatasetDBLoader;

/**
 * Created by ahmet on 10/14/16.
 */
public class SolgrindFileUtils {

	public static <S extends Comparable<S>, T> void writeResultsInMap(Map<S, T> map, Path p) {

		System.out.println("Results are written in " + p.toString());
		p.toFile().getParentFile().mkdirs();
		try (BufferedWriter writer = Files.newBufferedWriter(p)) {

			for (S es : new TreeSet<>(map.keySet())) {
				writer.write(es + "\t" + map.get(es) + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getEventTypes(String folder) {
		List<Path> files = DatasetDBLoader.findFilesFromFolder(folder);
		String[] eventTypes = new String[files.size()];
		int i = 0;
		for (Path s : files) {
			String pathString = s.toString();
			String eventType = pathString.substring(pathString.lastIndexOf("/") + 1);
			eventTypes[i] = eventType;
			i++;
		}
		return eventTypes;
	}
}
