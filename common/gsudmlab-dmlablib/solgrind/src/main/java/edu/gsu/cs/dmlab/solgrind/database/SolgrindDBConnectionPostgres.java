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
import java.util.Map.Entry;

/**
 * Created by ahmetkucuk on 19/08/16.
 * 
 * @author berkay - Sep 23, 2016
 *
 */
public class SolgrindDBConnectionPostgres implements SolgrindDBConnection {

	private final DataSource dsourc;

	private static final String SELECT_BY_ID = "SELECT trk_id, event_starttime as starttime, event_endtime as endtime, ST_asText(hpc_boundcc) as cc, ST_asText(hpc_bbox) as bbox "
			+ "FROM %s LEFT JOIN %s ON %s.event_id=%s.kb_archivid " + "WHERE trk_id='%s';";

	public SolgrindDBConnectionPostgres(DataSource source) {
		this.dsourc = source;
	}

	@Override
	public Instance getInstance(String tableName, InstanceData instanceData) {
		String trackTableName = instanceData.type + "_tracks_by_event";
		String queryString = String.format(SELECT_BY_ID, trackTableName, tableName, trackTableName, tableName,
				instanceData.id);
		System.out.println(queryString);

		try (Connection con = dsourc.getConnection(); PreparedStatement statement = con.prepareStatement(queryString)) {

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

	public List<Instance> getAllInstances(String tableName, String eventType) {
		return null;
	}

	private static final String SELECT_BY_IDS = ""
			+ "SELECT trk_id, event_starttime as starttime, event_endtime as endtime, "
			+ " ST_asText(hpc_boundcc) as cc, ST_asText(hpc_bbox) as bbox "
			+ "FROM %s LEFT JOIN %s ON %s.event_id=%s.kb_archivid " + "WHERE trk_id IN (%s);"; // replace
																								// the
																								// string
																								// with
																								// the
																								// identifiers
																								// of
																								// instances
																								// (quoted)

	@Override
	public Set<Instance> getInstances(InstanceData[] instanceDatas) {
		HashMap<String, ArrayList<String>> instancesByEventTypes = new HashMap<>();
		for (int i = 0; i < instanceDatas.length; i++) {
			String type = instanceDatas[i].type.toString();
			String id = instanceDatas[i].id;
			if (instancesByEventTypes.get(type) == null) {
				instancesByEventTypes.put(type, new ArrayList<String>());
			}
			instancesByEventTypes.get(type).add(id);
		}
		HashSet<Instance> returnInstances = new HashSet<>();
		for (Entry<String, ArrayList<String>> typeList : instancesByEventTypes.entrySet()) {
			String type = typeList.getKey();
			ArrayList<String> ids = typeList.getValue();
			String instanceIdString = "'" + String.join("','", ids) + "'";

			String trackTableName = type + "_tracks_by_event";
			String tableName = type;

			String queryString = String.format(SELECT_BY_IDS, trackTableName, tableName, trackTableName, tableName,
					instanceIdString); // TODO check this too

			try (Connection con = dsourc.getConnection();
					PreparedStatement statement = con.prepareStatement(queryString)) {
				ResultSet rs = statement.executeQuery();
				Set<Instance> instances = mapResultSetToInstances(rs, new EventType(type));
				returnInstances.addAll(instances);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnInstances;
	}

	private static final String WINDOW_SEARCH_QUERY = ""
			+ "SELECT trk_id, event_starttime as starttime, event_endtime as endtime,"
			+ "	 		ST_asText(hpc_boundcc) as cc, ST_asText(hpc_bbox) as bbox " + "from _eventtable as et, "
			+ "		(SELECT distinct trk_id "
			+ "		FROM 	_eventtable LEFT JOIN _tracktable ON _tracktable.event_id=_eventtable.kb_archivid"
			+ "		WHERE 	ST_intersects(hpc_bbox,  ST_GeomFromText( \'_wktEnvelope\' ))"
			+ "				AND tsrange(event_starttime, event_endtime) && tsrange(\'_TI_start\', \'_TI_end\')"
			+ "		) as tt " + "where tt.trk_id = et.kb_archivid";

	@Override
	public Set<Instance> searchInstances(Interval twindow, Envelope mbr, String eventType) {

		String query = getWindowSearchQuery(twindow, mbr, eventType);
		// System.out.println(query);
		HashSet<Instance> returnInstances = new HashSet<>();
		try (Connection con = dsourc.getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			Set<Instance> instances = mapResultSetToInstances(rs, new EventType(eventType));
			returnInstances.addAll(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnInstances;

	}

	private static final String INSERT_STATEMENT = "INSERT INTO _tablename ( _columns ) VALUES _values";

	/**
	 * TODO modify method to do followings
	 *
	 * This method creates a table for instance (exp: ar_head_tracks_by_event or
	 * ar_tail_tracks_by_event) if it does not exists and creates a table for event
	 * (exp: ar_head, ar_tail)
	 *
	 * @param ins
	 * @param tablename
	 * @return return True if insertion succeed
	 */
	@Override
	public boolean insertInstance(Instance ins, String tablename) {
		String _columns1 = "kb_archivid, event_starttime, event_endtime, hpc_boundcc, hpc_bbox";
		String _values1 = "";
		ArrayList<String> associatedEventIds = new ArrayList<>();
		for (TGPair tgp : ins.getTrajectory().getTGPairs()) {
			String startTime = tgp.getTInterval().getStart().toString();
			String endTime = tgp.getTInterval().getEnd().toString();
			String wktCC = tgp.getGeometry().toString();
			String wktBB = mbrToWKTPolygon(tgp.getGeometry().getEnvelopeInternal());
			String kbArchivId = ins.getId() + startTime + endTime + "_kb";
			associatedEventIds.add(kbArchivId);
			_values1 += "(" + kbArchivId + ", " + startTime + ", " + endTime + ", " + wktCC + ", " + wktBB + ", "
					+ "),";
		}
		_values1 = _values1.substring(0, _values1.length() - 1); // elimiate the
																	// last
																	// comma
		String insertStatement1 = replaceStringInQuery(INSERT_STATEMENT, "_tablename", tablename);
		insertStatement1 = replaceStringInQuery(insertStatement1, "_columns", _columns1);
		insertStatement1 = replaceStringInQuery(insertStatement1, "_values", _values1);

		String _columns2 = "trk_id, event_id";
		String _values2 = "";
		for (String eventId : associatedEventIds) {
			_values2 += "(" + ins.getId() + ", " + eventId + ") ,";
		}

		System.out.println(INSERT_STATEMENT);
		String insertStatement2 = replaceStringInQuery(INSERT_STATEMENT, "_tablename", tablename + "_tracks_by_event");
		System.out.println(insertStatement2);
		insertStatement2 = replaceStringInQuery(insertStatement2, "_columns", _columns2);
		System.out.println(insertStatement2);
		insertStatement2 = replaceStringInQuery(insertStatement2, "_values", _values2);
		System.out.println(insertStatement2);

		try (Connection con = dsourc.getConnection();
				PreparedStatement statement1 = con.prepareStatement(insertStatement1);
				PreparedStatement statement2 = con.prepareStatement(insertStatement2)) {
			boolean a1 = statement1.execute();
			boolean a2 = statement2.execute();
			return a1 && a2;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean insertInstances(Set<Instance> instances, String tablename) {
		boolean answer = true;
		for (Instance instance : instances) {
			answer = answer && insertInstance(instance, tablename);
		}
		return answer;
	}

	private static final String ST_JOIN_QUERY = "" + // TODO change this from
														// here
			"select tt1.trk_id as trk1, tt2.trk_id as trk2 "
			+ "from _eventtable1 as et1, _eventtable2 as et2, _tracktable1 as tt1, _tracktable2 as tt2 "
			+ "where 	et1.kb_archivid=tt1.event_id AND et2.kb_archivid=tt2.event_id "
			+ " AND ST_Intersects(et1.hpc_bbox, et2.hpc_bbox) "
			+ " AND tsrange(et1.event_starttime, et1.event_endtime) && tsrange(et2.event_starttime, et2.event_endtime)";

	/**
	 * TODO create enum for predicate
	 * 
	 * @param predicate
	 * @param table1
	 * @param table2
	 * @return Map of the join results
	 */
	@Override
	public HashMap<Instance, Instance> stJoin(String predicate, String table1, String table2) {

		String query = getStJoinQuery(predicate, table1, table2);
		HashMap<String, String> intersectingTrajectories = new HashMap<>();
		try (Connection con = dsourc.getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
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
		for (Entry<String, String> ids : intersectingTrajectories.entrySet()) {
			InstanceData iData1 = new InstanceData(ids.getKey(), new EventType(table1));
			Instance i1 = getInstance(iData1.type.getType(), iData1);

			InstanceData iData2 = new InstanceData(ids.getValue(), new EventType(table1));
			Instance i2 = getInstance(iData2.type.getType(), iData2);

			joinReturns.put(i1, i2);

		}
		return joinReturns;
	}

	private Set<Instance> mapResultSetToInstances(ResultSet rs, EventType type) throws SQLException, ParseException {
		HashMap<String, Instance> resultInstances = new HashMap<>();
		if (rs == null) {
			return new HashSet<>(resultInstances.values());
		}
		while (rs.next()) {
			String instanceId = rs.getString("trk_id");

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
		Timestamp sTime = rs.getTimestamp("starttime");
		Timestamp eTime = rs.getTimestamp("endtime");
		String shapeString = rs.getString("cc");
		String bbox = rs.getString("bbox");

		WKTReader reader = new WKTReader();
		Geometry polygon = reader.read(shapeString);
		if (polygon == null || polygon.isEmpty()) {
			polygon = reader.read(bbox);
		}
		return new TGPair(sTime.getTime(), eTime.getTime(), polygon);
	}

	private String mbrToWKTPolygon(Envelope mbr) {
		Geometry polygon = new GeometryFactory().toGeometry(mbr);
		WKTWriter writer = new WKTWriter();
		return writer.write(polygon);
	}

	private String getWindowSearchQuery(Interval tWindow, Envelope mbr, String eventType) {
		String trackTable = eventType + "_tracks_by_event";
		String eventTable = eventType;
		String tiStart = tWindow.getStart().toString();
		String tiEnd = tWindow.getEnd().toString();
		String mbrWKT = mbrToWKTPolygon(mbr); // TODO change this create a
												// polygon here

		String query = replaceStringInQuery(WINDOW_SEARCH_QUERY, "_eventtable", eventTable);
		query = replaceStringInQuery(query, "_tracktable", trackTable);
		query = replaceStringInQuery(query, "_wktEnvelope", mbrWKT);
		query = replaceStringInQuery(query, "_TI_start", tiStart);
		return replaceStringInQuery(query, "_TI_end", tiEnd); // TODO check this
																// too
	}

	private String getStJoinQuery(String predicate, String table1, String table2) {
		String trkTable1 = table1 + "_tracks_by_event";
		String trkTable2 = table2 + "_tracks_by_event";
		String query = replaceStringInQuery(ST_JOIN_QUERY, "_eventtable1", table1);
		query = replaceStringInQuery(query, "_eventtable2", table2);
		query = replaceStringInQuery(query, "_tracktable1", trkTable1);
		return replaceStringInQuery(query, "_tracktable2", trkTable2);
	}

	private String replaceStringInQuery(String query, String toBeReplaced, String targetString) {
		return query.replace(toBeReplaced, targetString);
		// String replacedQuery = "";
		// String [] parts = query.split(toBeReplaced);
		// for(int i = 0; i < parts.length; i++){
		// replacedQuery += parts[i] + targetString ;
		// }
		// replacedQuery += parts[parts.length-1];
		// return replacedQuery;
	}

	@Override
	public boolean createTable(String tableName) {
		return false;
	}

	@Override
	public boolean dropTable(String tableName) {
		return false;
	}
}
