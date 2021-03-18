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

import edu.gsu.cs.dmlab.solgrind.SolgrindConstants;
import edu.gsu.cs.dmlab.solgrind.algo.SequenceUtils;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.database.interfaces.SolgrindDBConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author berkay - Jul 12, 2016 Loads the datasets to the database with SQL
 *         statements
 */

public class DatasetDBLoader {

	private SolgrindDBConnection solgrindDBConnection;
	private long timeInterval;
	private String datasetFolder;
	private ExecutorService executorService;

	public DatasetDBLoader(SolgrindDBConnection dbConnection, String datasetFolder, long timeInterval) {
		this.timeInterval = timeInterval;
		this.datasetFolder = datasetFolder;
		solgrindDBConnection = dbConnection;
		executorService = Executors.newFixedThreadPool(20);

	}

	public void loadData() {

		List<Path> files = DatasetDBLoader.findFilesFromFolder(datasetFolder);
		for (Path s : files) {
			try {
				String pathString = s.toString();
				String eventType = pathString.substring(pathString.lastIndexOf("/") + 1);
				//
				solgrindDBConnection.createTable(eventType);
				DatasetParser parser = new DatasetParser(s, eventType, timeInterval);
				Instance instance1;

				final String headTable = eventType + "_head";
				final String tailTable = eventType + "_tail";
				solgrindDBConnection.dropTable(headTable);
				solgrindDBConnection.dropTable(tailTable);
				solgrindDBConnection.createTable(headTable);
				solgrindDBConnection.createTable(tailTable);

				while ((instance1 = parser.next()) != null) {
					final Instance instance = instance1;

					Runnable r = new Runnable() {

						@Override
						public void run() {

							solgrindDBConnection.insertInstance(instance, eventType);
							solgrindDBConnection.insertInstance(
									SequenceUtils.generateHeadwithRatio(instance, SolgrindConstants.H_R), headTable);
							solgrindDBConnection.insertInstance(SequenceUtils.generateTailWindowWithRatio(instance,
									SolgrindConstants.T_R, SolgrindConstants.BD, SolgrindConstants.TV), tailTable);
						}
					};
					executorService.execute(r);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Given a folder, this method returns all the files excluding metadata and
	 * mac os indexes
	 *
	 * TODO We really need to have trajectory type file extension like .trj
	 * 
	 * @param folder
	 * @return List of files
	 */
	public static List<Path> findFilesFromFolder(String folder) {
		try (Stream<Path> stream = Files.walk(Paths.get(folder), 1)) {
			List<Path> fileList = stream.filter(path -> !path.endsWith(".DS_Store"))
					.filter(path -> path.toString().indexOf("metadata") == -1).filter(path -> !Files.isDirectory(path))
					.collect(Collectors.toList());
			return fileList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
