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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.io.WKTWriter;

import au.com.bytecode.opencsv.CSVWriter;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;

public class CSVDataWriter extends DataWriter {

	public static String OUTPUT_DIR = "/home/baydin2/Desktop/INTERPOLATED/interpolated_Jan_2012_v2";
	public static final boolean VALIDATION_CHECK = true;
	public static CSVWriter csvWriter = null;
	public static final char CSV_SEPARATOR = '\t';
	public static final String FILE_NAME_EXTENSION = ".csv";
	public static final String[] OUT_COLUMN_NAMES = { "instanceID", "starttime", "endtime", "geom" };
	public static final WKTWriter wktWriter = new WKTWriter();

	@Override
	public void write(Collection<Instance> instances, String outfile) {
		System.out.println("Writing... " + outfile);
		try {
			Files.createDirectories(Paths.get(OUTPUT_DIR));
			Path outfilePath = Paths.get(OUTPUT_DIR, outfile.concat(FILE_NAME_EXTENSION));
			Files.createFile(outfilePath);
			BufferedWriter bw = Files.newBufferedWriter(outfilePath);

			csvWriter = new CSVWriter(bw, CSV_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);

			for (Instance ins : instances) {

				List<String[]> rowStrings = convertToRowStrings(ins, OUT_COLUMN_NAMES);
				for (String[] rowString : rowStrings) {
					csvWriter.writeNext(rowString);
				}
			}
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void setOutputDir(String dir) {
		OUTPUT_DIR = dir;
	}

	private List<String[]> convertToRowStrings(Instance ins, String[] outColumnNames) {
		List<String[]> rowStrings = new ArrayList<>();
		for (TGPair tgp : ins.getTrajectory().getTGPairs()) {
			String[] rowString = new String[outColumnNames.length];
			for (int i = 0; i < outColumnNames.length; i++) {
				String colName = outColumnNames[i];
				if (colName.equalsIgnoreCase("instanceID")) {
					rowString[i] = ins.getId();
				} else if (colName.equalsIgnoreCase("starttime")) {
					rowString[i] = "" + tgp.getTInterval().getStart();
				} else if (colName.equalsIgnoreCase("endtime")) {
					rowString[i] = "" + tgp.getTInterval().getEnd();
					;
				} else if (colName.equalsIgnoreCase("geom")) {
					rowString[i] = wktWriter.write(tgp.getGeometry());
				}
			}
			rowStrings.add(rowString);
		}

		return rowStrings;
	}

}
