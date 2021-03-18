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
import org.slf4j.*;

import javax.sql.DataSource;

import edu.gsu.cs.dmlab.databases.interfaces.IISDStateDBCreator;

/**
 * 
 * @author Dustin Kempton, Surabhi Priya, Data Mining Lab, Georgia State
 *         University
 *
 */
public class Postgres_StateDBCreator implements IISDStateDBCreator {

	private final Logger logger;

	public static class TABLE_NAMES {
		public static final String STATE_TABLE_NAME = "state_table";
	}

	/* Class Fields */
	private DataSource dsourc;

	private final String stateTableName = TABLE_NAMES.STATE_TABLE_NAME;

	/**
	 * 
	 * @param dsourc The datasource connection object used to connect to the
	 *               database.
	 *               
	 * @param logger The SLF4J logging object used to log errors that occur in this
	 *               object.
	 */
	public Postgres_StateDBCreator(DataSource dsourc, Logger logger) {
		if (dsourc == null)
			throw new IllegalArgumentException("DataSource cannot be null in StateDBConnector constructor.");
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null in ");
		this.dsourc = dsourc;
		this.logger = logger;

	}

	/* ------------------------ Operations ----------------------- */

	@Override
	public boolean checkStateTableExists() throws SQLException {

		boolean exists = false;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String schema = con.getSchema();

			// Check for the table and create if not there.
			String query = this.generateQuery_stateTableExists(schema);
			try (PreparedStatement tableExistsPrepStmt = con.prepareStatement(query)) {
				try (ResultSet res = tableExistsPrepStmt.executeQuery()) {
					if (res.next()) {
						exists = res.getBoolean(1);
					}
				}
			}

		} catch (SQLException e) {
			logger.error("SQL Exception while executing method checkStateTableExists", e);
			throw e;
		}
		return exists;
	}

	@Override
	public boolean createStateTable() throws SQLException {
		String query = "";
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);

			if (!this.checkStateTableExists()) {
				String schema = con.getSchema();
				query = this.generateQuery_createStateTable(schema);

				try (PreparedStatement createTableStatement = con.prepareStatement(query)) {
					if (createTableStatement.executeUpdate() == 0) {
						return true;

					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Exception while executing method createStateTable", e);
			throw e;
		}

		return false;
	}

	/* ------------------------ Query Generators ----------------------- */

	/**
	 * 
	 * @return
	 */
	private String generateQuery_stateTableExists(String schema) {
		String tableName = stateTableName;
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT EXISTS (");
		sb.append("SELECT 1 FROM pg_tables where schemaname = '");
		sb.append(schema);
		sb.append("' AND tablename = '");
		sb.append(tableName);
		sb.append("' );");

		return sb.toString();
	}

	/**
	 * Generates a query that create State table with its appropriate columns. The
	 * columns are as follow: processor_name<text, PK> |
	 * last_modified_data<timestamp> | ar<integer> | ch<integer> | ...
	 * 
	 * @return
	 */
	private String generateQuery_createStateTable(String schema) {
		String tableName = stateTableName;

		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE IF NOT EXISTS ");
		builder.append(schema);
		builder.append(".");
		builder.append(tableName);
		builder.append(" ( ");
		builder.append("process_name TEXT NOT NULL, ");
		builder.append("event_type TEXT NOT NULL, ");
		builder.append("busy_state BOOLEAN NOT NULL, ");
		builder.append("last_inserted_date TIMESTAMP WITHOUT TIME ZONE, ");
		builder.append("PRIMARY KEY (process_name, event_type) );");
		return builder.toString();
	}
}
