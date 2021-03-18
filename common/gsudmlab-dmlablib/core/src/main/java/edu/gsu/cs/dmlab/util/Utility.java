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
package edu.gsu.cs.dmlab.util;

import edu.gsu.cs.dmlab.datatypes.Waveband;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class Utility {

	/**
	 * Constant: number of seconds in a day.
	 */
	public static final double SECONDS_TO_DAYS = 60.0 * 60.0 * 24.0;

	public static int convertWavebandToInt(Waveband wavelength) {
		switch (wavelength) {
		case AIA94:
			return 94;
		case AIA131:
			return 131;
		case AIA171:
			return 171;
		case AIA193:
			return 193;
		case AIA211:
			return 211;
		case AIA304:
			return 304;
		case AIA335:
			return 335;
		case AIA1600:
			return 1600;
		case AIA1700:
			return 1700;
		default:
			return 94;
		}
	}

	public static Waveband getWavebandFromInt(int wavelength) {
		switch (wavelength) {
		case 94:
			return Waveband.AIA94;
		case 131:
			return Waveband.AIA131;
		case 171:
			return Waveband.AIA171;
		case 193:
			return Waveband.AIA193;
		case 211:
			return Waveband.AIA211;
		case 304:
			return Waveband.AIA304;
		case 335:
			return Waveband.AIA335;
		case 1600:
			return Waveband.AIA1600;
		case 1700:
			return Waveband.AIA1700;
		default:
			return null;
		}
	}

}