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
package edu.gsu.cs.dmlab.solgrind.index;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * 
 * @author berkay - Jul 28, 2016
 *
 */

public class RelationEdge extends DefaultWeightedEdge {

	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = -8918765472039977390L;

	public enum RelationType {
		COOCCURRENCE, SEQUENCE, SIMILARITY
	}

	private RelationType relation;

	public RelationEdge() {
		super();
	}

	public RelationEdge(RelationType type) {
		super();
		relation = type;
	}

	public RelationType getRelation() {
		return relation;
	}

	public void setRelation(RelationType relation) {
		this.relation = relation;
	}

	public void setRelation(String relation) {
		if (relation.equalsIgnoreCase("SEQUENCE")) {
			this.relation = RelationType.SEQUENCE;
		} else if (relation.equalsIgnoreCase("COOCCURRENCE")) {
			this.relation = RelationType.COOCCURRENCE;
		} else if (relation.equalsIgnoreCase("SIMILARITY")) {
			this.relation = RelationType.SIMILARITY;
		} else {
			System.err.println("Edge relation cannot be found");
		}
	}

	public double getWeight() {
		return super.getWeight();
	}

	public String toString() {
		if (relation == null)
			return "weight: " + getWeight();
		String str = "";
		switch (relation) {
		case COOCCURRENCE:
			str = "co[" + getWeight() + "]";
			break;
		case SEQUENCE:
			str = "sq[" + getWeight() + "]";
			break;
		case SIMILARITY:
			str = "sm[" + getWeight() + "]";
			break;
		}
		return str;
	}

}
