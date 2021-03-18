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
package edu.gsu.cs.dmlab.solgrind.algo.measures.significance;

import java.util.Collection;
import java.util.HashSet;

import edu.gsu.cs.dmlab.solgrind.algo.measures.SignificanceMeasure;
import edu.gsu.cs.dmlab.solgrind.base.Instance;
import edu.gsu.cs.dmlab.solgrind.base.operations.STOperations;
import edu.gsu.cs.dmlab.solgrind.base.types.essential.Trajectory;

/**
 * 
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class Omax implements SignificanceMeasure {

	@Override
	public double calculate(Instance ins1, Instance ins2) {

		Trajectory traj1 = ins1.getTrajectory();
		Trajectory traj2 = ins2.getTrajectory();

		double volume1 = traj1.getVolume();
		double volume2 = traj2.getVolume();
		double denom = Math.max(volume1, volume2);

		double numerator = STOperations.intersection(traj1, traj2).getVolume();

		return numerator / denom;

	}

	@Override
	public double calculate(Collection<Instance> instances) {
		double denom = 0.0;

		for (Instance ins : instances) {
			denom = Math.max(denom, ins.getTrajectory().getVolume());
		}

		HashSet<Trajectory> trajectories = new HashSet<Trajectory>();
		for (Instance ins : instances) {
			trajectories.add(ins.getTrajectory());
		}
		double numerator = STOperations.intersectionAll(trajectories).getVolume();

		return numerator / denom;
	}

	@Override
	public double calculateT(Trajectory traj1, Trajectory traj2) {
		double volume1 = traj1.getVolume();
		double volume2 = traj2.getVolume();
		double denom = Math.max(volume1, volume2);

		double numerator = STOperations.intersection(traj1, traj2).getVolume();

		return numerator / denom;
	}

	@Override
	public double calculateT(Collection<Trajectory> trajectories) {
		double denom = 0.0;
		for (Trajectory traj : trajectories) {
			denom = Math.max(denom, traj.getVolume());
		}

		double numerator = STOperations.intersectionAll(trajectories).getVolume();

		return numerator / denom;
	}

}
