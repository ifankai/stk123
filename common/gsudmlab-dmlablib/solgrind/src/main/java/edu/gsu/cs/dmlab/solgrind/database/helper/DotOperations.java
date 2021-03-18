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
package edu.gsu.cs.dmlab.solgrind.database.helper;

import edu.gsu.cs.dmlab.solgrind.base.EventType;
import edu.gsu.cs.dmlab.solgrind.index.InstanceVertex;
import edu.gsu.cs.dmlab.solgrind.index.RelationEdge;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.ext.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ahmetkucuk on 01/10/16.
 */
public class DotOperations {

//	public static void toDot(Graph<InstanceVertex, RelationEdge> graph, String fileName) {
//		try {
//			Paths.get(fileName).toFile().getParentFile().mkdirs();
//			if (!graph.vertexSet().isEmpty()) {
//				DOTExporter<InstanceVertex, RelationEdge> exporter = new DOTExporter<>(
//						new VertexNameProvider<InstanceVertex>() {
//							@Override
//							public String getVertexName(InstanceVertex instanceVertex) {
//								return instanceVertex.getId();
//							}
//						}, new VertexNameProvider<InstanceVertex>() {
//							@Override
//							public String getVertexName(InstanceVertex instanceVertex) {
//								return instanceVertex.getType().getType();
//							}
//						}, new EdgeNameProvider<RelationEdge>() {
//							@Override
//							public String getEdgeName(RelationEdge o) {
//								return o.getRelation().name();
//							}
//						}, new ComponentAttributeProvider<InstanceVertex>() {
//							@Override
//							public Map<String, String> getComponentAttributes(InstanceVertex instanceVertex) {
//								Map<String, String> map = new HashMap<>();
//								map.put("fillcolor", instanceVertex.getType().getColor());
//								return map;
//							}
//						}, new ComponentAttributeProvider<RelationEdge>() {
//							@Override
//							public Map<String, String> getComponentAttributes(RelationEdge relationEdge) {
//								Map<String, String> map = new HashMap<>();
//								map.put("weight", String.valueOf(graph.getEdgeWeight(relationEdge)));
//								return map;
//							}
//						});
//
//				exporter.exportGraph(graph, Files.newOutputStream(Paths.get(fileName)));
//
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExportException e) {
//			e.printStackTrace();
//		}
//	}
	

	public static void readDotFile(WeightedGraph<InstanceVertex, RelationEdge> graph, String fileName) {
		try {

			DOTImporter<InstanceVertex, RelationEdge> exporter = new DOTImporter<>(
					new VertexProvider<InstanceVertex>() {
						@Override
						public InstanceVertex buildVertex(String s, Map<String, String> map) {
							InstanceVertex instanceVertex = new InstanceVertex(s, new EventType(map.get("label")));
							graph.addVertex(instanceVertex);
							return instanceVertex;
						}
					}, new EdgeProvider<InstanceVertex, RelationEdge>() {
						@Override
						public RelationEdge buildEdge(InstanceVertex o, InstanceVertex v1, String s,
								Map<String, String> map) {
							RelationEdge edge = graph.getEdgeFactory().createEdge(o, v1);
							edge.setRelation(map.get("label"));
							graph.setEdgeWeight(edge, Double.parseDouble(map.get("weight")));
							return edge;
						}
					}, new VertexUpdater<InstanceVertex>() {
						@Override
						public void updateVertex(InstanceVertex o, @SuppressWarnings("rawtypes") Map map) {

						}
					});

			exporter.importGraph(graph, Files.newBufferedReader(Paths.get(fileName)));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		}
	}
}
