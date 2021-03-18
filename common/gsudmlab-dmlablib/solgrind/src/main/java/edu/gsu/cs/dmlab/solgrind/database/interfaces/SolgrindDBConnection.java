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
package edu.gsu.cs.dmlab.solgrind.database.interfaces;

import org.joda.time.Interval;
import org.locationtech.jts.geom.Envelope;

import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.types.instance.InstanceData;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ahmetkucuk on 15/08/16.
 * 
 * @author berkay - Aug 15, 2016
 *
 */
public interface SolgrindDBConnection {

	/**
	 * get trajectory with given trajectory identifier
	 * 
	 * @return
	 */
	public Instance getInstance(String tableName, InstanceData instanceData);

	/**
	 * //get a set of trajectories given an array of identifiers
	 * 
	 * @return
	 */
	public Set<Instance> getInstances(InstanceData[] instanceDatas);

	/**
	 *
	 * @param tableName
	 * @return set of instances
	 */
	public List<Instance> getAllInstances(String tableName, String eventType);

	/**
	 * //this is spatiotemporal windows search on an event type
	 * 
	 * @param twindow
	 * @param mbr
	 * @return
	 */
	public Set<Instance> searchInstances(Interval twindow, Envelope mbr, String eventType);

	/**
	 * this inserts a trajectory to tablename it could be ideal if one checks the
	 * table is correctly modeled
	 * 
	 * @param trj
	 * @param tablename
	 * @return
	 */
	public boolean insertInstance(Instance trj, String tablename);

	/**
	 * this inserts a set of trajectories to a table in batch
	 * 
	 * @param trj
	 * @param tablename
	 * @return
	 */
	public boolean insertInstances(Set<Instance> trj, String tablename);

	/**
	 * this performs a spatiotemporal join (first do overlap) select * from table1
	 * as t1, table2 as t2 where t1.timerange intersects with t2.timerange abd
	 * t1.geom intersercts with t2.geom
	 *
	 * @param predicate
	 * @param table1
	 * @param table2
	 * @return
	 */
	public HashMap<Instance, Instance> stJoin(String predicate, String table1, String table2);

	public boolean createTable(String tableName);

	public boolean dropTable(String tableName);
}
