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
package edu.gsu.cs.dmlab.solgrind.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	static Connection dbConn;

	private DBConnection() throws SQLException {
		if (dbConn == null) {
			dbConn = getConnection();
		} else if (dbConn.isClosed()) {
			dbConn = getConnection();
		}
	}

	public static Connection getConnection() throws SQLException {
		String dbPassword = "xxxxxxx";
		String dbUser = "postgres";
		String dbURL = "jdbc:postgresql://localhost/stesdb?allowMultiQueries=true";

		Properties props = new Properties();
		props.setProperty("user", dbUser);
		props.setProperty("password", dbPassword);
		props.setProperty("ssl", "true");
		dbConn = DriverManager.getConnection(dbURL, props);

		return dbConn;

	}

}
