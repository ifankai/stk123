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

import javax.sql.DataSource;

import edu.gsu.cs.dmlab.databases.interfaces.IFeatureDBCreator;
import edu.gsu.cs.dmlab.datatypes.EventType;
import edu.gsu.cs.dmlab.datatypes.ImageDBWaveParamPair;
import edu.gsu.cs.dmlab.util.Utility;

/**
 * Class that creates tables and stores feature scores for each event type in
 * our dataset.
 * 
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public class SingleFeatureDBCreator implements IFeatureDBCreator {

	private DataSource dsourc;

	/**
	 * Constructor for this database creator.
	 * 
	 * @param dsourc The data source that connects to the database schema in which
	 *               we will be creating the tables.
	 */
	public SingleFeatureDBCreator(DataSource dsourc) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in SingleFeatureDBCreator constructor.");
		this.dsourc = dsourc;
	}

	@Override
	public void finalize() throws Throwable {
		this.dsourc = null;
	}

	@Override
	public boolean createFeatureScoreTable(EventType type) throws SQLException {
		Connection con = null;
		try {

			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			// Check for table and create if not there.
			String query = this.queryFeatureComboTableExistsString(type);
			PreparedStatement tableExistsPrepStmt = con.prepareStatement(query);
			ResultSet res = tableExistsPrepStmt.executeQuery();
			if (!res.next()) {
				query = this.createFeatureCombosTableString(type);
				tableExistsPrepStmt = con.prepareStatement(query);
				tableExistsPrepStmt.execute();
			}

		} finally {
			if (con != null) {
				con.close();
			}
		}
		return true;
	}

	@Override
	public boolean insertParamFeatureStatVal(EventType type, ImageDBWaveParamPair id, float statVal)
			throws SQLException {
		Connection con = null;

		boolean success = false;
		try {

			con = this.dsourc.getConnection();
			con.setAutoCommit(true);

			String query = this.insertFeatureComboTable(type);
			PreparedStatement stmt = con.prepareStatement(query);

			stmt.setInt(1, id.parameter);
			stmt.setInt(2, Utility.convertWavebandToInt(id.wavelength));
			stmt.setFloat(3, statVal);
			int rowCount = stmt.executeUpdate();
			if (rowCount == 1)
				success = true;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return success;
	}

	private String insertFeatureComboTable(EventType type) {
		String typeString = type.toQualifiedString();

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO `");
		sb.append(typeString);
		sb.append("_features` ");
		sb.append("VALUES(?,?,?);");
		return sb.toString();
	}

	private String queryFeatureComboTableExistsString(EventType type) {
		String typeString = type.toQualifiedString();

		StringBuilder sb = new StringBuilder();
		sb.append("SHOW TABLES LIKE '");
		sb.append(typeString);
		sb.append("_features';");

		return sb.toString();
	}

	private String createFeatureCombosTableString(EventType type) {

		String typeString = type.toQualifiedString();

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `");
		sb.append(typeString);
		sb.append("_features` (");
		sb.append("`param` int(3) NOT NULL, ");
		sb.append("`wavelength` int(4) NOT NULL, ");
		sb.append("`stat_val` decimal(15,7) NOT NULL, ");
		sb.append("PRIMARY KEY (`param`,`wavelength`) ");
		sb.append(");");
		return sb.toString();
	}

}
