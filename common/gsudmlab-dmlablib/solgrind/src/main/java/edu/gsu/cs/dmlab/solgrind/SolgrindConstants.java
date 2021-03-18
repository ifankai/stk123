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
package edu.gsu.cs.dmlab.solgrind;

/**
 * This set of values should really be imported as configuration values and not
 * set as constants in part of a library.
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class SolgrindConstants {

	public static final String DATASET_DIR = "/Users/ahmetkucuk/Documents/Research/BERKAY/1Mo_out/";

	// public static final String[] EVENT_TYPES = new String[]{"ar", "ch", "fi",
	// "ss", "ef", "sg"};

	/*
	 * Event Co-occurrence parameters
	 */
	// cce threshold
	public static final double CCE_th = 0.01;
	// pi threshold
	// public static final double PI_th = 0.01;

	public static long SAMPLING_INTERVAL = 10 * 60 * 1000; // in milliseconds now

	/*
	 * Event EventEventSequence parameters
	 */
	// head interval (in seconds)
	public static final long H_in = 24; // 3600 seconds is an hour
	public static final double H_R = 0.10;
	// tail interval (in seconds)
	public static final long T_in = 24;
	public static final double T_R = 0.20;
	// head interval (in seconds)
	public static final long TV = 12; // Tail validity interval is (TV * SAMPLING_INTERVAL) milliseconds
	// buffer distance (in arcsec)
	public static final double BD = 10;
	// ci threshold
	public static final double CI_th = 0.01;

}
