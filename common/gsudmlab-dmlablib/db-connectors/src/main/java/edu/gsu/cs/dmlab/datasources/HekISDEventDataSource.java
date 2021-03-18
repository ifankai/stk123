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
package edu.gsu.cs.dmlab.datasources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.gsu.cs.dmlab.datasources.interfaces.IISDEventDataSource;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.interfaces.IISDEventReport;
import edu.gsu.cs.dmlab.factory.interfaces.IHEKObjectFactory;

/**
 * This class is used to retrieve event reports coming from the source location
 * that are intended to be inserted into our database.
 * 
 * @author Dustin Kempton, Surabhi Priya, Data Mining Lab, Georgia State University
 *
 */
public class HekISDEventDataSource implements IISDEventDataSource {
	
	private static final Logger logger = LoggerFactory.getLogger(HekISDEventDataSource.class);
	

	/**
	 * HEK api that includes formatting for specific event type, page, event start
	 * time, and event end time
	 */
	private static final String QueryURL = "https://www.lmsal.com/hek/her?cosec=2&cmd=search&type=column&event_type=%s&event_region=all&event_coordsys=helioprojective&x1=-5000&x2=5000&y1=-5000&y2=5000&result_limit=%d&page=%d&event_starttime=%s&event_endtime=%s";
	private static final int NumberOfElementInEachQuery = 200;
	private static final DateTimeFormatter Formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC();
	private IHEKObjectFactory factory;

	public HekISDEventDataSource(IHEKObjectFactory factory) {
		if (factory == null)
			new IllegalArgumentException("IObjectFactory cannot be null in HekISDEventDataSource constructor.");
		this.factory = factory;
	}

	@Override
	public List<IISDEventReport> getReports(DateTime startTime, DateTime endTime, EventType type) {

		List<IISDEventReport> results = new ArrayList<IISDEventReport>();
		boolean done = false;
		int page = 0;
		while (!done) {

			String query = String.format(QueryURL, type.toQualifiedString(), NumberOfElementInEachQuery, page,
					startTime.toString(Formatter), endTime.toString(Formatter));

			try {
				String resultString = this.downloadByUrl(query);
				Pair<JsonArray, Boolean> p = this.getResultArray(resultString);
				JsonArray jsonResultArray = p.getFirst();
				done = p.getSecond();

				for (JsonElement j : jsonResultArray) {
					IISDEventReport report = factory.getEventReportFromJson(j.getAsJsonObject());
					results.add(report);
				}

			} catch (IOException e) {
				logger.error("IO Exception occurred", e);
				done = true;
			}

			page++;
		}

		return results;
	}

	/**
	 * Method that extracts all of the data from the HEK api and returns it as a
	 * string
	 * 
	 * @param url
	 *            - the HEK api url
	 * @return - the data from the api
	 * @throws IOException
	 *             - input/output exception caused from reading from url
	 */
	private String downloadByUrl(String url) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
			StringBuilder sb = new StringBuilder();

			String inputLine;

			// Build a string from streamed data
			while ((inputLine = reader.readLine()) != null) {
				sb.append(inputLine + "\n");
			}
			return sb.toString();
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException");
			//System.out.println("[EventJsonDownloader-downloadByUrl] MalformedURLException");
		}
		return null;
	}

	/**
	 * Method that takes a json formatted string and turns it into a json object.
	 * The "result" attribute is then looked up from the json object and returned as
	 * a json array.
	 * 
	 * @param result
	 *            - the json formatted string from the HEK api
	 * @return - the data of the "result" attribute from the json containing
	 *         information about the event
	 */
	private Pair<JsonArray, Boolean> getResultArray(String result) {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(result);
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		boolean isFinished = !jsonObject.get("overmax").getAsBoolean();

		Pair<JsonArray, Boolean> p = new Pair<JsonArray, Boolean>(jsonObject.get("result").getAsJsonArray(),
				Boolean.valueOf(isFinished));
		return p;
	}

}
