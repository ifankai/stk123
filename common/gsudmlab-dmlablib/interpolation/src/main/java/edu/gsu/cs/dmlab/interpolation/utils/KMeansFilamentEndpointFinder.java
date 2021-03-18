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
package edu.gsu.cs.dmlab.interpolation.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.math3.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import edu.gsu.cs.dmlab.interpolation.utils.interfaces.ISTEndpointFinder;

/**
 * An end point finder that is used to approximately align two filament
 * detections, this is used to initialize a more fine grained alignment
 * performed within this object. The method being implemented is from the
 * polygon interpolation methods described in
 * <a href="https://doi.org/10.3847/1538-4365/aab763">Boubrahimi et. al,
 * 2018</a>.
 * 
 * @author Soukaina Filali, updated by Dustin Kempton, Data Mining Lab, Georgia
 *         State University
 *
 */
public class KMeansFilamentEndpointFinder implements ISTEndpointFinder {

	private double percentageCoordinate = 0.2;
	private int numClusters = 2;

	/**
	 * Constructor that assumes percentage of maximal distance coordinates from the
	 * centroid is 20 and the number of clusters to use is 2.
	 */
	public KMeansFilamentEndpointFinder() {

	}

	/**
	 * Constructor that takes in values for the number of clusters to use when
	 * attempting to find the end point and the percentage of the maximal distance
	 * coordinates from the centroid of a polygon should be used in those clusters
	 * 
	 * @param numClusters          Number of clusters to use when finding the end
	 *                             point
	 * 
	 * @param percentageCoordinate Percentage of points ordered by their distance
	 *                             from the centroid to use, where range is 0..1.
	 */
	public KMeansFilamentEndpointFinder(int numClusters, double percentageCoordinate) {

	}

	/**
	 * Returns the best end point between the end point pairs. A best end point is
	 * the one that has the highest y coordinate (Filaments do not tilt more than 40
	 * degrees).
	 * 
	 * @param densifiedGeometry The geometry object to find the best end point on
	 * 
	 * @return A coordinate that is considered to be the best based on some criteria
	 */
	@Override
	public Coordinate findBestEndpoint(Polygon densifiedGeometry) {
		List<Coordinate> sub = this.getMostDistantCoordinates(densifiedGeometry);
		Pair<Coordinate, Coordinate> centroids = this.getKMeansCentroids(sub);

		KMeans km = new KMeans(sub, centroids, this.numClusters);
		List<Cluster> clusters = km.run();

		Pair<Coordinate, Coordinate> endpoints = this.findEndpointsfromClusters(clusters);

		if (endpoints.getFirst().y >= endpoints.getSecond().y)
			return endpoints.getFirst();
		else
			return endpoints.getSecond();
	}

	/**
	 * Finds the most distant pairs of coordinates belonging to 2 different clusters
	 * 
	 * @param clusters
	 * @return the two endpoints of a polygon
	 */
	public Pair<Coordinate, Coordinate> findEndpointsfromClusters(List<Cluster> clusters) {
		TreeMap<Double, ArrayList<Coordinate>> distanceBetweenClusterElementsMap = new TreeMap<Double, ArrayList<Coordinate>>(
				Collections.reverseOrder());
		Pair<Coordinate, Coordinate> endpoints;

		for (int i = 0; i < clusters.get(0).Coordinates.size(); i++) {
			for (int j = 0; j < clusters.get(1).Coordinates.size(); j++) {
				ArrayList<Coordinate> pair = new ArrayList<Coordinate>();
				pair.add(clusters.get(0).Coordinates.get(i));
				pair.add(clusters.get(1).Coordinates.get(j));
				double distance = pair.get(0).distance(pair.get(1));

				distanceBetweenClusterElementsMap.put(distance, pair);
			}
		}
		double maxdistance = distanceBetweenClusterElementsMap.firstKey();
		endpoints = new Pair<Coordinate, Coordinate>(distanceBetweenClusterElementsMap.get(maxdistance).get(0),
				distanceBetweenClusterElementsMap.get(maxdistance).get(1));
		return endpoints;
	}

	/**
	 * Returns the centroids of the clusters of the 2-Means algorithm.
	 * 
	 * @param mostDistantCoordinates
	 * @return
	 */
	private Pair<Coordinate, Coordinate> getKMeansCentroids(List<Coordinate> mostDistantCoordinates) {
		TreeMap<Double, ArrayList<Coordinate>> distanceCoorindateMap = new TreeMap<Double, ArrayList<Coordinate>>(
				Collections.reverseOrder());
		Pair<Coordinate, Coordinate> centroids;

		for (int k = 0; k < mostDistantCoordinates.size(); k++) {
			for (int j = k + 1; j < mostDistantCoordinates.size(); j++) {
				ArrayList<Coordinate> pair = new ArrayList<Coordinate>();
				pair.add(mostDistantCoordinates.get(k));
				pair.add(mostDistantCoordinates.get(j));
				double distance = pair.get(0).distance(pair.get(1));
				distanceCoorindateMap.put(distance, pair);
			}
		}

		double maxdistance = distanceCoorindateMap.firstKey();
		centroids = new Pair<Coordinate, Coordinate>(distanceCoorindateMap.get(maxdistance).get(0),
				distanceCoorindateMap.get(maxdistance).get(1));
		return centroids;

	}

	/**
	 * Finds a percentage of the most distant coordinate pairs of a polygon
	 * 
	 * @param PERCENTAGE_COORDINATE
	 * @param polygon
	 * @return a percentage of most distant coordinate pairs
	 */
	private List<Coordinate> getMostDistantCoordinates(Polygon polygon) {
		TreeMap<Double, Coordinate> CentroiddistanceCoorindateMap = new TreeMap<Double, Coordinate>(
				Collections.reverseOrder());
		Coordinate centroid = polygon.getCentroid().getCoordinate();
		ArrayList<Coordinate> sorted = new ArrayList<Coordinate>();

		for (int j = 0; j < polygon.getNumPoints(); j++) {
			double distance = polygon.getCoordinates()[j].distance(centroid);
			CentroiddistanceCoorindateMap.put(distance, polygon.getCoordinates()[j]);
		}

		Set<Entry<Double, Coordinate>> mappings = CentroiddistanceCoorindateMap.entrySet();
		for (Entry<Double, Coordinate> mapping : mappings) {
			sorted.add(mapping.getValue());
		}

		List<Coordinate> sub = sorted.subList(0, (int) (this.percentageCoordinate * polygon.getNumPoints()));
		return sub;
	}

	public class KMeans {

		private List<Coordinate> Coordinates;
		private Pair<Coordinate, Coordinate> centroids;
		private int numClusters = 2;

		public KMeans(List<Coordinate> coordinates, Pair<Coordinate, Coordinate> centroids, int numClusters) {
			this.Coordinates = coordinates;
			this.centroids = centroids;
			this.numClusters = numClusters;
		}

		public List<Cluster> run() {
			return calculate();
		}

		// Initializes the process
		private List<Cluster> init() {
			// Create Clusters
			List<Cluster> clusters = new ArrayList<>();
			for (int i = 0; i < this.numClusters; i++) {
				Cluster cluster = new Cluster(i);
				if (i == 0)
					cluster.setCentroid(centroids.getFirst());
				else
					cluster.setCentroid(centroids.getSecond());
				clusters.add(cluster);
			}
			// Print Initial state
			// plotClusters(clusters);
			return clusters;
		}

		// The process to calculate the K Means, with iterating method.
		private List<Cluster> calculate() {
			boolean finish = false;

			List<Cluster> clusters = init();

			// Add in new data, one at a time, recalculating centroids with each new
			// one.
			while (!finish) {
				// Clear cluster state
				clusters = this.clearClusters(clusters);

				List<Coordinate> lastCentroids = new ArrayList<>();
				lastCentroids = this.getKMeansCentroids(clusters);

				// Assign Coordinates to the closer cluster
				clusters = this.assignCluster(clusters);

				// Calculate new centroids.
				clusters = this.calculateCentroids(clusters);

				List<Coordinate> currentCentroids = this.getKMeansCentroids(clusters);

				// Calculates total distance between new and old Centroids
				double distance = 0;
				for (int i = 0; i < lastCentroids.size(); i++) {
					distance += lastCentroids.get(i).distance(currentCentroids.get(i));
				}

				if (distance == 0) {
					finish = true;
				}
			}
			return clusters;
		}

		private List<Cluster> clearClusters(List<Cluster> clusters) {
			for (int i = 0; i < this.numClusters; i++) {
				((Cluster) clusters.get(i)).clear();
			}
			return clusters;
		}

		private List<Coordinate> getKMeansCentroids(List<Cluster> clusters) {
			List<Coordinate> centroids = new ArrayList<Coordinate>(this.numClusters);
			for (int i = 0; i < this.numClusters; i++) {
				Coordinate aux = ((Cluster) clusters.get(i)).getCentroid();
				Coordinate Coordinate = new Coordinate(aux.x, aux.y);
				centroids.add(Coordinate);
			}
			return centroids;
		}

		private List<Cluster> assignCluster(List<Cluster> clusters) {
			double max = Double.MAX_VALUE;
			double min = max;
			int cluster = 0;
			double distance = 0.0;

			for (int k = 0; k < this.Coordinates.size(); k++) {
				Coordinate cor = (Coordinate) this.Coordinates.get(k);
				min = max;
				for (int i = 0; i < this.numClusters; i++) {
					Cluster c = (Cluster) clusters.get(i);
					distance = cor.distance(c.getCentroid());
					if (distance < min) {
						min = distance;
						cluster = i;
					}
				}
				((Cluster) clusters.get(cluster)).addCoordinate(cor);
			}
			return clusters;
		}

		private List<Cluster> calculateCentroids(List<Cluster> clusters) {
			for (int i = 0; i < this.numClusters; i++) {
				double sumX = 0;
				double sumY = 0;
				List<Coordinate> list = ((Cluster) clusters.get(i)).getCoordinates();
				int n_Coordinates = list.size();

				for (int j = 0; j < list.size(); j++) {
					sumX += list.get(j).x;
					sumY += list.get(j).y;
				}

				Coordinate centroid = ((Cluster) clusters.get(i)).getCentroid();
				if (n_Coordinates > 0) {
					double newX = sumX / n_Coordinates;
					double newY = sumY / n_Coordinates;
					centroid = new Coordinate(newX, newY);
				}
				clusters.get(i).setCentroid(centroid);
			}
			return clusters;
		}
	}

	public class Cluster {

		public List<Coordinate> Coordinates;
		public Coordinate centroid;
		public int id;

		// Creates a new Cluster
		public Cluster(int id) {
			this.id = id;
			this.Coordinates = new ArrayList<Coordinate>();
			this.centroid = null;
		}

		public List<Coordinate> getCoordinates() {
			return Coordinates;
		}

		public void addCoordinate(Coordinate Coordinate) {
			Coordinates.add(Coordinate);
		}

		public void setCoordinates(List<Coordinate> Coordinates) {
			this.Coordinates = Coordinates;
		}

		public Coordinate getCentroid() {
			return centroid;
		}

		public void setCentroid(Coordinate centroid) {
			this.centroid = centroid;
		}

		public int getId() {
			return id;
		}

		public void clear() {
			Coordinates.clear();
		}

	}
}