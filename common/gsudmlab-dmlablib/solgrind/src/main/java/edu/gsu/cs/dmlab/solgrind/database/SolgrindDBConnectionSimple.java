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

import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.TGPair;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceData;
import edu.gsu.cs.dmlab.solgrind.database.interfaces.SolgrindDBConnection;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by ahmetkucuk on 28/09/16.
 */
public class SolgrindDBConnectionSimple implements SolgrindDBConnection {

	private DataSource dataSource;
	private GeometryFactory geometryFactory;
	private WKTWriter wktWriter;
	private WKTReader wktReader;

	public SolgrindDBConnectionSimple(DataSource source) {
		this.dataSource = source;
		this.geometryFactory = new GeometryFactory();
		this.wktWriter = new WKTWriter();
		this.wktReader = new WKTReader();

	}

	private static final String SELECT_ALL = "SELECT trj_id, start_time, end_time, ST_asText(geom) as geom "
			+ " FROM %s LEFT JOIN %s ON %s.event_id=%s.event_id";

	private static final String SELECT_BY_ID = "SELECT trj_id, start_time, end_time, ST_asText(geom) as geom "
			+ "FROM %s LEFT JOIN %s ON %s.event_id=%s.event_id " + "WHERE trj_id='%s';";

	private static final String CREATE_TRAJECTORY = "CREATE TABLE IF NOT EXISTS %s_trj ("
			+ "trj_id VARCHAR, event_id VARCHAR" + ");";

	private static final String CREATE_EVENT = "CREATE TABLE IF NOT EXISTS %s_event (event_id VARCHAR PRIMARY KEY, start_time TIMESTAMP, end_time TIMESTAMP, geom GEOMETRY);";
	private static final String DROP_EVENT = "DROP TABLE IF EXISTS %s_event CASCADE;";
	private static final String DROP_TRAJECTORY = "DROP TABLE IF EXISTS %s_trj CASCADE;";

	@Override
	public Instance getInstance(String tableName, InstanceData instanceData) {
		String trjTableName = tableName + "_trj";
		String eventTableName = tableName + "_event";
		String queryString = String.format(SELECT_BY_ID, trjTableName, eventTableName, trjTableName, eventTableName,
				instanceData.id);

		try (Connection con = dataSource.getConnection();
				PreparedStatement statement = con.prepareStatement(queryString)) {

			ResultSet rs = statement.executeQuery();
			Trajectory trajectory = new Trajectory();

			boolean isTrajectoryFound = false;
			while (rs.next()) {
				trajectory.addTGPair(mapToTGPair(rs, instanceData.type));
				isTrajectoryFound = true;
			}
			if (!isTrajectoryFound)
				return null;

			return new Instance(instanceData.id, instanceData.type, trajectory);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<Instance> getInstances(InstanceData[] instanceDatas) {

		Set<Instance> result = new HashSet<>();
		for (InstanceData instanceData : instanceDatas) {
			result.add(getInstance(instanceData.type.toString(), instanceData));
		}
		return result;
	}

	@Override
	public List<Instance> getAllInstances(String tableName, String eventType) {

		String trjTableName = tableName + "_trj";
		String eventTableName = tableName + "_event";
		String query = String.format(SELECT_ALL, trjTableName, eventTableName, trjTableName, eventTableName);

		List<Instance> result = new ArrayList<>();
		try (Connection con = dataSource.getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			Set<Instance> instances = mapResultSetToInstances(rs, new EventType(eventType));
			result.addAll(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static final String WINDOW_SEARCH_QUERY = "" + "SELECT trj_id, start_time, end_time,"
			+ "	 		ST_asText(geom) as geom " + "from _tracktable as et RIGHT JOIN " + "		(SELECT * "
			+ "		FROM 	_eventtable " + "		WHERE 	ST_intersects(geom,  ST_GeomFromText( '_wktEnvelope' ))"
			+ "				AND tsrange(start_time, end_time) && tsrange(\'_TI_start\', \'_TI_end\')"
			+ "		) as tt " + "ON tt.event_id = et.event_id;";

	@Override
	public Set<Instance> searchInstances(Interval twindow, Envelope mbr, String eventType) {

		String query = getWindowSearchQuery(twindow, mbr, eventType);
		HashSet<Instance> returnInstances = new HashSet<>();
		try (Connection con = dataSource.getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			Set<Instance> instances = mapResultSetToInstances(rs, new EventType(eventType));
			returnInstances.addAll(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnInstances;

	}

	private static final String INSERT_STATEMENT = "INSERT INTO %s ( %s ) VALUES %s;";

	@Override
	public boolean insertInstance(Instance trj, String tablename) {

		String trjTableName = tablename + "_trj";
		String eventTableName = tablename + "_event";
		boolean result = true;
		String _columns1 = "event_id, start_time, end_time, geom";
		ArrayList<String> associatedEventIds = new ArrayList<>();
		for (TGPair tgp : trj.getTrajectory().getTGPairs()) {
			String startTime = tgp.getTInterval().getStart().toString();
			String endTime = tgp.getTInterval().getEnd().toString();
			String geom = tgp.getGeometry().toString();
			String eventID = UUID.randomUUID().toString();
			associatedEventIds.add(eventID);
			String _values1 = "(" + "'" + eventID + "', " + "'" + startTime + "', " + "'" + endTime + "', "
					+ "ST_GeomFromText('" + geom + "')" + ")";

			String insertStatement1 = String.format(INSERT_STATEMENT, eventTableName, _columns1, _values1);
			insertQuery(insertStatement1);
		}

		String _columns2 = "trj_id, event_id";
		for (String eventId : associatedEventIds) {
			String _values2 = "(" + trj.getId() + ", '" + eventId + "')";
			String insertStatement2 = String.format(INSERT_STATEMENT, trjTableName, _columns2, _values2);
			insertQuery(insertStatement2);
		}
		return result;
	}

	private static final String ST_JOIN_QUERY = "" + // TODO change this from
														// here
			"select tt1.trj_id as trk1, tt2.trj_id as trk2 "
			+ "from _eventtable1 as et1, _eventtable2 as et2, _tracktable1 as tt1, _tracktable2 as tt2 "
			+ "where 	et1.event_id=tt1.event_id AND et2.event_id=tt2.event_id "
			+ " AND ST_Intersects(et1.geom, et2.geom) "
			+ " AND tsrange(et1.start_time, et1.end_time) && tsrange(et2.start_time, et2.end_time)";

	@Override
	public boolean insertInstances(Set<Instance> instances, String tablename) {
		boolean answer = true;
		for (Instance instance : instances) {
			answer = answer && insertInstance(instance, tablename);
		}
		return answer;
	}

	@Override
	public HashMap<Instance, Instance> stJoin(String predicate, String table1, String table2) {
		String query = getStJoinQuery(predicate, table1, table2);
		HashMap<String, String> intersectingTrajectories = new HashMap<>();
		try (Connection con = dataSource.getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String insId1 = rs.getString("trk1");
				String insId2 = rs.getString("trk2");
				intersectingTrajectories.put(insId1, insId2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		HashMap<Instance, Instance> joinReturns = new HashMap<>();
		for (Map.Entry<String, String> ids : intersectingTrajectories.entrySet()) {
			InstanceData iData1 = new InstanceData(ids.getKey(), new EventType(table1));
			Instance i1 = getInstance(iData1.type.getType(), iData1);

			InstanceData iData2 = new InstanceData(ids.getValue(), new EventType(table1));
			Instance i2 = getInstance(iData2.type.getType(), iData2);

			joinReturns.put(i1, i2);

		}
		return joinReturns;
	}

	public boolean createTable(String tableName) {
		executeCommand(String.format(CREATE_TRAJECTORY, tableName));
		executeCommand(String.format(CREATE_EVENT, tableName));

		return true;
	}

	public boolean dropTable(String tableName) {
		executeCommand(String.format(DROP_TRAJECTORY, tableName));
		executeCommand(String.format(DROP_EVENT, tableName));
		return true;
	}

	private String getStJoinQuery(String predicate, String table1, String table2) {
		String trkTable1 = table1 + "_trj";
		String trkTable2 = table2 + "_trj";
		String eventTable1 = table1 + "_event";
		String eventTable2 = table2 + "_event";
		String query = ST_JOIN_QUERY.replace("_eventtable1", eventTable1);
		query = query.replaceAll("_eventtable2", eventTable2);
		query = query.replaceAll("_tracktable1", trkTable1);
		return query.replaceAll("_tracktable2", trkTable2);
	}

	private String getWindowSearchQuery(Interval tWindow, Envelope mbr, String eventType) {
		String trjTable = eventType + "_trj";
		String eventTable = eventType + "_event";
		String tiStart = tWindow.getStart().toString();
		String tiEnd = tWindow.getEnd().toString();
		String mbrWKT = mbrToWKTPolygon(mbr); // TODO change this create a
												// polygon here

		String query = WINDOW_SEARCH_QUERY.replaceAll("_eventtable", eventTable);
		query = query.replaceAll("_tracktable", trjTable);
		query = query.replaceAll("_wktEnvelope", mbrWKT);
		query = query.replaceAll("_TI_start", tiStart);
		return query.replaceAll("_TI_end", tiEnd); // TODO check this too
	}

	private Set<Instance> mapResultSetToInstances(ResultSet rs, EventType type) throws SQLException, ParseException {
		HashMap<String, Instance> resultInstances = new HashMap<>();
		if (rs == null) {
			return new HashSet<>(resultInstances.values());
		}
		while (rs.next()) {
			String instanceId = rs.getString("trj_id");

			// create TGPair object from individual row in the result set
			TGPair tgp = mapToTGPair(rs, type);

			resultInstances.putIfAbsent(instanceId, new Instance(instanceId, type));

			resultInstances.get(instanceId).getTrajectory().addTGPair(tgp); // add
																			// the
																			// tgpair
																			// to
																			// instance's
																			// trajectory

		}
		return new HashSet<>(resultInstances.values());
	}

	private TGPair mapToTGPair(ResultSet rs, EventType type) throws ParseException, SQLException {
		Timestamp sTime = rs.getTimestamp("start_time");
		Timestamp eTime = rs.getTimestamp("end_time");
		String shapeString = rs.getString("geom");

		WKTReader reader = new WKTReader();
		Geometry polygon = reader.read(shapeString);
		return new TGPair(sTime.getTime(), eTime.getTime(), polygon);
	}

	private String mbrToWKTPolygon(Envelope mbr) {
		Geometry polygon = geometryFactory.toGeometry(mbr);
		return wktWriter.write(polygon);
	}

	private boolean executeCommand(String query) {
		try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {

			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean insertQuery(String query) {
		try (Connection con = dataSource.getConnection(); PreparedStatement statement = con.prepareStatement(query)) {

			return statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
