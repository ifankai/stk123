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
 * 
 * This code is based on code in the javaml library https://github.com/AbeelLab/javaml
 */
package edu.gsu.cs.dmlab.distance.dtw.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.gsu.cs.dmlab.distance.dtw.datatypes.ColMajorCell;
import edu.gsu.cs.dmlab.distance.dtw.search.interfaces.ISearchWindow;

/**
 * Abstract Search Window 
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com, refactored by Dustin
 *         Kempton, Data Mining Lab, Georgia State University
 * 
 */
public abstract class SearchWindow implements ISearchWindow {

	private final int minValues[];
	private final int maxValues[];
	private final int maxJ;
	private int size;
	private int modCount;

	private final class SearchWindowIterator implements Iterator<ColMajorCell> {

		private int currentI;
		private int currentJ;
		private final SearchWindow window;
		private boolean hasMoreElements;
		private final int expectedModCount;

		private SearchWindowIterator(SearchWindow w) {
			this.window = w;
			this.hasMoreElements = window.size() > 0;
			this.currentI = window.minI();
			this.currentJ = window.minJ();
			this.expectedModCount = w.modCount;
		}

		public boolean hasNext() {
			return this.hasMoreElements;
		}

		public ColMajorCell next() {
			// If the window was updated while utilizing this iterator, then throw an
			// exception to indicate this.
			if (modCount != this.expectedModCount)
				throw new ConcurrentModificationException();

			// If next was called again after reaching the end of the search window, then
			// throw an exception indicating this.
			if (!this.hasMoreElements)
				throw new NoSuchElementException();

			ColMajorCell cell = new ColMajorCell(this.currentI, this.currentJ);
			if (++this.currentJ > this.window.maxJforI(this.currentI))
				if (++this.currentI <= this.window.maxI())
					this.currentJ = this.window.minJforI(this.currentI);
				else
					this.hasMoreElements = false;
			return cell;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Constructor
	 * 
	 * @param tsIsize The length of the first point series
	 * 
	 * @param tsJsize The length of the second point series
	 */
	public SearchWindow(int tsIsize, int tsJsize) {
		this.minValues = new int[tsIsize];
		this.maxValues = new int[tsIsize];
		Arrays.fill(this.minValues, -1);
		this.maxJ = tsJsize - 1;
		this.size = 0;
		this.modCount = 0;
	}

	@Override
	public int minI() {
		return 0;
	}

	@Override
	public int maxI() {
		return this.minValues.length - 1;
	}

	@Override
	public int minJ() {
		return 0;
	}

	@Override
	public int maxJ() {
		return this.maxJ;
	}

	@Override
	public int minJforI(int i) {
		return this.minValues[i];
	}

	@Override
	public int maxJforI(int i) {
		return this.maxValues[i];
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public Iterator<ColMajorCell> iterator() {
		return new SearchWindowIterator(this);
	}

	protected void expandWindow(int radius) {
		if (radius > 0) {
			this.expandSearchWindow(1);
			this.expandSearchWindow(radius - 1);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void expandSearchWindow(int radius) {
		if (radius > 0) {
			//First, we iterate over the cells that have la
			ArrayList windowCells = new ArrayList(this.size());
			for (Iterator cellIter = this.iterator(); cellIter.hasNext(); windowCells.add(cellIter.next()))
				;
			for (int cell = 0; cell < windowCells.size(); cell++) {
				ColMajorCell currentCell = (ColMajorCell) windowCells.get(cell);
				int targetCol;
				int targetRow;
				if (currentCell.getCol() != this.minI() && currentCell.getRow() != this.maxJ()) {
					targetCol = currentCell.getCol() - radius;
					targetRow = currentCell.getRow() + radius;
					if (targetCol >= this.minI() && targetRow <= this.maxJ()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = Math.max(this.minI() - targetCol, targetRow - this.maxJ());
						this.markVisited(targetCol + cellsPastEdge, targetRow - cellsPastEdge);
					}
				}
				if (currentCell.getRow() != this.maxJ()) {
					targetCol = currentCell.getCol();
					targetRow = currentCell.getRow() + radius;
					if (targetRow <= this.maxJ()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = targetRow - this.maxJ();
						this.markVisited(targetCol, targetRow - cellsPastEdge);
					}
				}
				if (currentCell.getCol() != this.maxI() && currentCell.getRow() != this.maxJ()) {
					targetCol = currentCell.getCol() + radius;
					targetRow = currentCell.getRow() + radius;
					if (targetCol <= this.maxI() && targetRow <= this.maxJ()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = Math.max(targetCol - this.maxI(), targetRow - this.maxJ());
						this.markVisited(targetCol - cellsPastEdge, targetRow - cellsPastEdge);
					}
				}
				if (currentCell.getCol() != this.minI()) {
					targetCol = currentCell.getCol() - radius;
					targetRow = currentCell.getRow();
					if (targetCol >= this.minI()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = this.minI() - targetCol;
						this.markVisited(targetCol + cellsPastEdge, targetRow);
					}
				}
				if (currentCell.getCol() != this.maxI()) {
					targetCol = currentCell.getCol() + radius;
					targetRow = currentCell.getRow();
					if (targetCol <= this.maxI()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = targetCol - this.maxI();
						this.markVisited(targetCol - cellsPastEdge, targetRow);
					}
				}
				if (currentCell.getCol() != this.minI() && currentCell.getRow() != this.minJ()) {
					targetCol = currentCell.getCol() - radius;
					targetRow = currentCell.getRow() - radius;
					if (targetCol >= this.minI() && targetRow >= this.minJ()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = Math.max(this.minI() - targetCol, this.minJ() - targetRow);
						this.markVisited(targetCol + cellsPastEdge, targetRow + cellsPastEdge);
					}
				}
				if (currentCell.getRow() != this.minJ()) {
					targetCol = currentCell.getCol();
					targetRow = currentCell.getRow() - radius;
					if (targetRow >= this.minJ()) {
						this.markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = this.minJ() - targetRow;
						this.markVisited(targetCol, targetRow + cellsPastEdge);
					}
				}
				if (currentCell.getCol() == this.maxI() || currentCell.getRow() == this.minJ())
					continue;
				targetCol = currentCell.getCol() + radius;
				targetRow = currentCell.getRow() - radius;
				if (targetCol <= this.maxI() && targetRow >= this.minJ()) {
					this.markVisited(targetCol, targetRow);
				} else {
					int cellsPastEdge = Math.max(targetCol - this.maxI(), this.minJ() - targetRow);
					this.markVisited(targetCol - cellsPastEdge, targetRow + cellsPastEdge);
				}
			}

		}
	}

	protected final void markVisited(int col, int row) {

		// In all cases of this method, the modCount is used to indicate that the window
		// was edited 1 more time. This is utilized in the iterator to check if the
		// window was edited while iterating through it.

		// If a value for this column (point in the first point series) hasn't been
		// entered into the search window yet, then we set the min value and max value
		// indicator to the same position and up the count of visited points by one.
		if (this.minValues[col] == -1) {
			this.minValues[col] = row;
			this.maxValues[col] = row;
			this.size++;
			this.modCount++;
		} else if (this.minValues[col] > row) {
			// If the row value for this column is greater than the initial location stored
			// as the minimum visited location, then we update the search window size by the
			// difference between the initial value and the new one.
			this.size += this.minValues[col] - row;
			this.minValues[col] = row;
			this.modCount++;
		} else if (this.maxValues[col] < row) {
			// If the row value for this column is less than the initial location stored as
			// the maximum visited location, then we update the search window size by the
			// difference between the initial value and the new one.
			this.size += row - this.maxValues[col];
			this.maxValues[col] = row;
			this.modCount++;
		}
	}

}