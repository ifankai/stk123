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
package edu.gsu.cs.dmlab.solgrind.base.operations;

import org.locationtech.jts.geom.Envelope;

import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class SOperations {

	public static boolean sIntersectsMBR(Trajectory traj1, Trajectory traj2) {

		Envelope t1mbr = traj1.getMBR();
		Envelope t2mbr = traj2.getMBR();

		return t1mbr.intersects(t2mbr);
	}

}
