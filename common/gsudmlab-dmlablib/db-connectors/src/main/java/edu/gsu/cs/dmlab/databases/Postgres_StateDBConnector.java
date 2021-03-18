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
import java.util.List;
import org.slf4j.*;

import javax.sql.DataSource;

import org.joda.time.DateTime;

import edu.gsu.cs.dmlab.databases.interfaces.IISDStateDBConnector;
import edu.gsu.cs.dmlab.datatypes.EventType;

/**
 * A connection class to test and update various pieces of information in a
 * database to determine if this process can do its thing and to inform other
 * processes that it is.
 * 
 * @author Dustin Kempton, Surabhi Priya, Data Mining Lab, Georgia State
 *         University
 *
 */

public class Postgres_StateDBConnector implements IISDStateDBConnector {
	private static class TABLE_NAMES {
		public static final String STATE_TABLE_NAME = "state_table";
	}

	private String processName;
	private DataSource dsourc;
	private List<String> blockingProcesses;
	private DateTime defaultTime;
	private final Logger logger;

	/**
	 * Constructor
	 * 
	 * @param dsourc            The data source used to connect to the database.
	 * 
	 * @param blockingProcesses A list of process names that will block this process
	 *                          in the state table. If empty, then it is assumed
	 *                          that no processes will block the processing of
	 *                          EventTypes in this process
	 * 
	 * @param defaultTime       The default time returned for the last processed
	 *                          time entry of the state table when this process has
	 *                          not previously had an entry
	 * 
	 * @param processName       The name of the process to use when accessing the
	 *                          state table
	 * 
	 * @param logger            The SLF4J logging object used to log errors that
	 *                          occur in this object.
	 */
	public Postgres_StateDBConnector(DataSource dsourc, List<String> blockingProcesses, DateTime defaultTime,
			String processName, Logger logger) {
		if (dsourc == null)
			throw new IllegalArgumentException(
					"DataSource cannot be null in HEKProcessorStateDBConnector constructor.");
		if (blockingProcesses == null)
			throw new IllegalArgumentException(
					"Blocking Process list cannot be null in HEKProcessorStateDBConnector constructor.");
		if (processName == null)
			throw new IllegalArgumentException("Process Name cannot be null in stateDBConnector class");
		if (defaultTime == null)
			throw new IllegalArgumentException("DateTime cannot be null in stateDBConnector class");
		if (logger == null)
			throw new IllegalArgumentException("Logger cannot be null in stateDBConnector class");

		this.dsourc = dsourc;
		this.blockingProcesses = blockingProcesses;
		this.defaultTime = defaultTime;
		this.processName = processName;
		this.logger = logger;

	}

	@Override
	public boolean checkOKToProcess(EventType type) throws SQLException {
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String schema = con.getSchema();
			String query = this.generateQuery_checkOKToProcess(type, schema);

			try (PreparedStatement createTableStatement = con.prepareStatement(query)) {
				try (ResultSet rs = createTableStatement.executeQuery()) {
					if (rs.next()) {

						int r = rs.getInt("count");
						if (r > 0) {
							return false;

						} else
							return true;
					}

				}
			}

		} catch (SQLException e) {
			logger.error("SQL Exception while executing method checkOKTOProcess", e);
			throw e;
		}

		return false;
	}

	@Override
	public boolean setIsProcessing(EventType type) throws SQLException {
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String schema = con.getSchema();

			String query = this.generateQuery_checkRecordExists(type, schema);
			try (PreparedStatement recordExistsStatement = con.prepareStatement(query)) {
				try (ResultSet res = recordExistsStatement.executeQuery()) {
					if (res.next()) {
						if (res.getBoolean(1)) {

							query = this.generateQuery_setIsProcessing(type, schema);
							try (PreparedStatement isProcessingStatement = con.prepareStatement(query)) {
								int affectedRows = isProcessingStatement.executeUpdate();
								if (affectedRows != 0) {
									return true;
								} else {
									return false;
								}
							}

						} else {
							// the record does not exist, so a new record must be inserted.
							query = this.generateQuery_insertNewStateRecord(type, true, schema);
							try (PreparedStatement insertNewStateStatement = con.prepareStatement(query)) {
								int affectedRows = insertNewStateStatement.executeUpdate();
								if (affectedRows != 0) {
									return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Exception while executing method setIsProcessing", e);
			throw e;
		}
		return false;
	}

	@Override
	public boolean setFinishedProcessing(EventType type) throws SQLException, IllegalAccessException {
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String schema = con.getSchema();

			String query = this.generateQuery_checkRecordExists(type, schema);
			try (PreparedStatement recordExistsStatement = con.prepareStatement(query)) {
				try (ResultSet res = recordExistsStatement.executeQuery()) {

					if (res.next()) {
						if (res.getBoolean(1)) {
							// the record already exists, so update the state of that record
							query = this.generateQuery_setFinishedProcessing(type, schema);
							try (PreparedStatement isProcessingStatement = con.prepareStatement(query)) {
								int affectedRows = isProcessingStatement.executeUpdate();
								if (affectedRows != 0) {
									return true;
								} else {
									return false;

								}
							}
						}
					}
				}

			}
		} catch (SQLException e) {
			logger.error("SQL Exception while executing method setFinishedProcessing", e);
			throw e;
		}
		return false;
	}

	@Override
	public boolean updateLastProcessedTime(EventType type, DateTime processedTime) throws SQLException {

		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String schema = con.getSchema();
			String query = this.generateQuery_UpdateLastProcessedTime(type, processedTime, schema);
			try (PreparedStatement updateLastProcessedTimeStatement = con.prepareStatement(query)) {
				int affectedRows = updateLastProcessedTimeStatement.executeUpdate();
				if (affectedRows != 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Exception while executing method updateLastProcessedTime", e);
			throw e;
		}
	}

	@Override
	public DateTime getLastProcessedTime(EventType type) throws SQLException {
		DateTime resTime = null;
		try (Connection con = this.dsourc.getConnection()) {
			con.setAutoCommit(true);
			String schema = con.getSchema();
			String query = this.generateQuery_getLastProcessedTime(type, schema);
			try (PreparedStatement getLastProcessedTimeStatement = con.prepareStatement(query)) {
				try (ResultSet rs = getLastProcessedTimeStatement.executeQuery()) {

					if (rs.next()) {
						resTime = new DateTime(rs.getTimestamp("last_inserted_date")); // Default: TimeZone.UTC
						return resTime;
					}

				}
			}
		} catch (SQLException e) {
			logger.error("SQL Exception while executing method getLastProcessedTime", e);
			throw e;
		}

		return null;

	}

	/*-----------------Query Generators----------------*/
	/**
	 * Generates the following query for an event type such as 'ar':
	 * 
	 * SELECT COUNT(*) FROM public.state_table WHERE busy_state AND event_type =
	 * 'ar' AND process_name IN ('tracking_process', .., 'another_process');
	 * 
	 * @param type
	 * @return
	 */
	private String generateQuery_checkOKToProcess(EventType type, String schema) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT COUNT(*) AS count FROM ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME);
		builder.append(" WHERE busy_state = True ");
		if (type != null) {
			builder.append("AND event_type IN ('");
			builder.append(type.toQualifiedString());
			builder.append("', 'NULL') ");
		}
		builder.append("AND process_name IN (");
		for (String p : blockingProcesses) {
			builder.append("'");
			builder.append(p);
			builder.append("', ");
		}
		builder.append("'");
		builder.append(this.processName); // checks for the existing process too !
		builder.append("'");
		builder.append(");");

		return builder.toString();
	}

	/**
	 * Generates the following query for an event type such as 'ar': UPDATE
	 * public.processing_state SET ar = 1 WHERE process_name = "hek_processor"
	 * 
	 * to flag that 'ar' table is currently busy by 'hek_processor'.
	 * 
	 * @param type
	 * @return
	 */
	private String generateQuery_setIsProcessing(EventType type, String schema) {
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME);
		builder.append(" SET busy_state = TRUE");
		builder.append(" WHERE process_name = '");
		builder.append(this.processName);
		builder.append("' AND event_type = '");
		if (type == null) {
			builder.append("NULL");
		} else {
			builder.append(type.toQualifiedString());
		}
		builder.append("';");
		return builder.toString();
	}

	/**
	 * Generates the following query for an event type such as 'ar': UPDATE
	 * public.state_table SET event_type = 'ar', busy_state = FALSE WHERE
	 * process_name = 'hek_processor'
	 * 
	 * to flag that 'ar' table is currently busy by 'hek_processor'.
	 * 
	 * @param type
	 * @return
	 */
	private String generateQuery_setFinishedProcessing(EventType type, String schema) {

		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME);
		builder.append(" SET busy_state = FALSE");
		builder.append(" WHERE process_name = '");
		builder.append(this.processName);
		builder.append("' AND event_type = '");
		if (type == null) {
			builder.append("NULL");
		} else {
			builder.append(type.toQualifiedString().toLowerCase());
		}
		builder.append("';");

		return builder.toString();
	}

	/**
	 * Generates the following query for an event type such as 'ar': UPDATE
	 * public.processing_state SET last_modified_data = 2019-01-01 00:00:00 WHERE
	 * process_name = "hek_processor"
	 * 
	 * to flag that 'ar' table is currently busy by 'hek_processor'.
	 * 
	 * @param type
	 * @param processedTime
	 * @return
	 */
	private String generateQuery_UpdateLastProcessedTime(EventType type, DateTime processedTime, String schema) {
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME);
		builder.append(" SET ");
		builder.append(" last_inserted_date = '");
		builder.append(processedTime.toString());
		builder.append("' WHERE process_name = '");
		builder.append(processName);
		builder.append("' AND event_type = '");
		if (type == null) {
			builder.append("NULL");
		} else {
			builder.append(type.toQualifiedString().toLowerCase());
		}
		builder.append("';");

		return builder.toString();
	}

	/**
	 * Generates the following (example ) query to get last_inserted_date of a
	 * process from the state_tabel: SELECT last_inserted_date WHERE process_name =
	 * 'hek_process' AND event_type = 'ar';
	 * 
	 * @param type
	 * @return the generated query
	 */
	private String generateQuery_getLastProcessedTime(EventType type, String schema) {

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT last_inserted_date FROM ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME);
		builder.append(" WHERE process_name = '");
		builder.append(this.processName);
		builder.append("' AND event_type = '");
		if (type == null) {
			builder.append("NULL");
		} else {
			builder.append(type.toQualifiedString().toLowerCase());
		}
		builder.append("';");

		return builder.toString();
	}

	/**
	 * Generates the following (example ) query to insert a new record to
	 * state_table: INSERT INTO public.state_table (process_name, event_type,
	 * busy_state, last_inserted_date) VALUES ( 'hek_process', 'ar', TRUE,
	 * '2010-06-02 00:06:00');
	 * 
	 * @param type
	 * @return the generated query
	 */
	private String generateQuery_insertNewStateRecord(EventType type, boolean state, String schema) {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME + " (");
		builder.append("process_name, event_type, busy_state, last_inserted_date ) ");
		builder.append("VALUES ( '");
		builder.append(this.processName);
		builder.append("', '");
		if (type == null) {
			builder.append("NULL");
		} else {
			builder.append(type.toQualifiedString());
		}
		builder.append("', '");
		builder.append(state);
		builder.append("', '");
		builder.append(this.defaultTime.toString());
		builder.append("' );");

		return builder.toString();
	}

	/**
	 * Generates the following query to check if a record indicating that this
	 * process is working on the given table exists: SELECT EXISTS ( SELECT 1 FROM
	 * public.state_table WHERE process_name = 'hek_process' AND event_type = 'ar';
	 * 
	 * @param type
	 * @param schema
	 * @return the generated query
	 */
	private String generateQuery_checkRecordExists(EventType type, String schema) {
		StringBuilder builder = new StringBuilder();

		builder.append("SELECT EXISTS ( SELECT 1 FROM ");
		builder.append(schema);
		builder.append(".");
		builder.append(TABLE_NAMES.STATE_TABLE_NAME);
		builder.append(" WHERE process_name = '");
		builder.append(this.processName);
		builder.append("' AND event_type = '");
		if (type == null) {
			builder.append("NULL");
		} else {
			builder.append(type.toQualifiedString());
		}
		builder.append("');");
		return builder.toString();
	}

}
