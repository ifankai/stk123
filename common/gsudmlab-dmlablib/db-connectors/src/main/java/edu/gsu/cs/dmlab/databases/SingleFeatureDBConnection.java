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
package edu.gsu.cs.dmlab.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import edu.gsu.cs.dmlab.databases.interfaces.IFeatureDBConnection;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.util.Utility;

/**
 * This class is used to access feature descriptors ranked by some ranking
 * method and inserted into the database by the
 * {@link edu.gsu.cs.dmlab.databases.SingleFeatureDBCreator
 * SingleFeatureDBCreator}.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SingleFeatureDBConnection implements IFeatureDBConnection {

	DataSource dsourc;

	/**
	 * Constructor
	 * 
	 * @param dsourc The database connection object that we use to query the
	 *               database.
	 */
	public SingleFeatureDBConnection(DataSource dsourc) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in SingleFeatureDBConnection constructor.");
		this.dsourc = dsourc;
	}

	@Override
	public void finalize() throws Throwable {
		this.dsourc = null;
	}

	@Override
	public ImageDBWaveParamPair[] getBestFeatures(EventType type, int num) throws SQLException {
		Connection con = null;
		ArrayList<ImageDBWaveParamPair> resultList = new ArrayList<ImageDBWaveParamPair>();
		try {
			String queryString = this.buildQueryFeatureComboTable(type);
			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			PreparedStatement stmt = con.prepareStatement(queryString);
			stmt.setInt(1, num);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int param = rs.getInt(1);
				int wave = rs.getInt(2);
				ImageDBWaveParamPair val = new ImageDBWaveParamPair();
				val.parameter = param;
				val.wavelength = Utility.getWavebandFromInt(wave);
				resultList.add(val);
			}

		} finally {
			if (con != null) {
				con.close();
			}
		}

		ImageDBWaveParamPair[] resultArr = new ImageDBWaveParamPair[resultList.size()];
		resultList.toArray(resultArr);
		return resultArr;
	}

	private String buildQueryFeatureComboTable(EventType type) {
		String typeString = this.getTypeString(type);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT param, wavelength FROM `");
		sb.append(typeString);
		sb.append("_features` ");
		sb.append("ORDER BY stat_val DESC LIMIT ?;");
		return sb.toString();
	}

	@SuppressWarnings("incomplete-switch")
	private String getTypeString(EventType type) {
		String typeString = "";

		switch (type) {
		case ACTIVE_REGION:
			typeString = "ar";
			break;
		case CORONAL_HOLE:
			typeString = "ch";
			break;
		case FILAMENT:
			typeString = "fi";
			break;
		case SIGMOID:
			typeString = "sg";
			break;
		case SUNSPOT:
			typeString = "ss";
			break;
		case EMERGING_FLUX:
			typeString = "ef";
			break;
		case FLARE:
			typeString = "fl";
			break;
		}
		return typeString;
	}

	@Override
	public boolean featureTableExists(EventType type) throws SQLException {
		String query = "SHOW TABLES LIKE '" + this.getTypeString(type) + "_features';";
		Connection con = null;
		boolean result = false;
		try {
			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			PreparedStatement stmt = con.prepareStatement(query);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				result = true;
			}

		} finally {
			if (con != null) {
				con.close();
			}
		}
		return result;
	}
}
