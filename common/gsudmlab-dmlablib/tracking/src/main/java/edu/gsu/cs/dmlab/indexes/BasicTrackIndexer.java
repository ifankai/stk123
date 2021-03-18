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
package edu.gsu.cs.dmlab.indexes;

import edu.gsu.cs.dmlab.datatypes.interfaces.IBaseTemporalObject;
import edu.gsu.cs.dmlab.datatypes.interfaces.ISTTrackingTrajectory;
import edu.gsu.cs.dmlab.geometry.GeometryUtilities;
import edu.gsu.cs.dmlab.indexes.interfaces.AbsMatIndexer;
import edu.gsu.cs.dmlab.indexes.interfaces.ISTTrackingTrajectoryIndexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * This class provides event indexing for ISTTrackingTrajectory objects. The
 * indexing is based on a grid of ArrayLists, where the grid represents the
 * space over which the index is valid. The ArrayLists in each location of the
 * grid are used to sort the ISTTrackingTrajectory objects that intersect each
 * spatial coordinate based on time.
 * 
 * @author Thaddeus Gholston, Data Mining Lab, Georgia State University
 * 
 */
public class BasicTrackIndexer extends AbsMatIndexer<ISTTrackingTrajectory> implements ISTTrackingTrajectoryIndexer {

	private ForkJoinPool forkJoinPool = null;

	/**
	 * Constructor, constructs a new BasicTrackindexer.
	 * 
	 * @param list            The list of tracks to index.
	 * @param regionDimension The length for both the x and y spatial domain. For
	 *                        example x will be valid for [0, regionDimension].
	 *                        
	 * @param regionDiv       The divisor used to down size each of the
	 *                        ISTTrackingTrajectory objects to fit inside the
	 *                        spatial domain specified by the regionDimension
	 *                        parameter.
	 *                        
	 * @param numThreads      The number of threads to use when processing batch. -1
	 *                        for all available &gt; 0 for a specific number. JavaEE
	 *                        doesn't seem to like more than one thread, so only use
	 *                        1 for applications built for web application servers.
	 *                        
	 * @throws IllegalArgumentException When any of the passed in arguments are
	 *                                  null, or if the regionDimension is less than
	 *                                  1 same with divisor.
	 */
	public BasicTrackIndexer(List<ISTTrackingTrajectory> list, int regionDimension, int regionDiv, int numThreads)
			throws IllegalArgumentException {
		super(list, regionDimension, regionDiv);

		if (numThreads < -1 || numThreads == 0)
			throw new IllegalArgumentException("numThreads must be -1 or > 0 in Indexer constructor.");

		if (numThreads == -1) {
			this.forkJoinPool = new ForkJoinPool();
		} else {
			this.forkJoinPool = new ForkJoinPool(numThreads);
		}

		this.buildIndex();
	}

	@Override
	public void finalize() throws Throwable {
		try {
			this.forkJoinPool.shutdownNow();
			this.forkJoinPool = null;
		} finally {
			super.finalize();
		}
	}

	protected void buildIndex() {
		// add all the objects to the index
		try {
			this.forkJoinPool.submit(() -> {
				// Add each object to index
				this.objectList.forEach(track -> {
					this.indexTrack(track);
				});

				// Sort objects in each box in the index by the time of the
				// objects in those regions.
				IntStream.range(0, this.regionDimension * this.regionDimension).parallel().forEach(i -> {
					int x = i / this.regionDimension;
					int y = i % this.regionDimension;
					Collections.sort(this.searchSpace[x][y], IBaseTemporalObject.baseTemporalComparator);
				});
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	private void indexTrack(ISTTrackingTrajectory track) {
		this.pushTrackIntoMatrix(track);
	}

	private void pushTrackIntoMatrix(ISTTrackingTrajectory track) {
		// add for forward indexing
		{
			GeometryFactory geoFactory = new GeometryFactory();
			Geometry scaledGeometry = GeometryUtilities.scaleGeometry(track.getFirst().getGeometry(),
					this.regionDivisor);
			Envelope scaledBoundingBox = scaledGeometry.getEnvelopeInternal();

			IntStream.rangeClosed((int) scaledBoundingBox.getMinX(), (int) scaledBoundingBox.getMaxX()).parallel()
					.forEach(x -> {
						IntStream.rangeClosed((int) scaledBoundingBox.getMinY(), (int) scaledBoundingBox.getMaxY())
								.forEach(y -> {
									Geometry testGeometry = geoFactory.toGeometry(new Envelope(x + 1, x, y + 1, y));
									if (scaledGeometry.intersects(testGeometry)) {
										if (x > -1 && x < this.regionDimension && y > -1 && y < this.regionDimension) {
											searchSpace[x][y].add(track);
										}
									}
								});
					});
		}
	}

	@Override
	public ArrayList<ISTTrackingTrajectory> getAll() {
		HashMap<UUID, ISTTrackingTrajectory> uniqueTracks = new HashMap<UUID, ISTTrackingTrajectory>();
		for (int i = 0; i < this.objectList.size(); i++) {
			ISTTrackingTrajectory tmpTrk = this.objectList.get(i);
			if (!uniqueTracks.containsKey(tmpTrk.getFirst().getUUID())) {
				uniqueTracks.put(tmpTrk.getFirst().getUUID(), tmpTrk);
			}
		}

		ArrayList<ISTTrackingTrajectory> returnVals = new ArrayList<ISTTrackingTrajectory>(uniqueTracks.values());
		return returnVals;
	}
}