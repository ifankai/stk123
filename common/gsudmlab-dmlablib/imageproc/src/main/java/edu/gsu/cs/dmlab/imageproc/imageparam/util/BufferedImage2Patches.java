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
package edu.gsu.cs.dmlab.imageproc.imageparam.util;

import java.awt.image.BufferedImage;

import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;

/**
 * This is an <code>abstract</code> class designed solely to divide a given
 * <code>BufferedImage</code> into <code>n</code> smaller
 * <code>BufferedImage</code>'s, each of size <code>PatchSize</code> X
 * <code>PatchSize</code>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public abstract class BufferedImage2Patches {

	/**
	 * 
	 * @param bImage
	 *            the input image that is supposed to be divided into patches
	 * @param patchChoice
	 *            can be chosen from the enumeration <code>PatchSize</code>. This
	 *            determines the size of each patch which has a square shape (i.e.
	 *            height = width).
	 * @return A square matrix whose entities are of type <code>BufferedImage</code>
	 * @throws IllegalArgumentException
	 *             in the following cases:<br>
	 *             <ul>
	 *             <li>If the given <code>bImage</code> is not square-shaped
	 *             <li>If the <code>patchChoice</code> is not from the enumeration
	 *             in <code>PatchSize</code>
	 *             <li>If the <code>bImage</code> cannot be divided into
	 *             <code>patchChoice</code> patches
	 *             </ul>
	 */
	public static BufferedImage[][] getAllPatches(BufferedImage bImage, IMeasures.PatchSize patchChoice)
			throws IllegalArgumentException {

		int width = bImage.getWidth();
		int height = bImage.getHeight();
		BufferedImage[][] patchesArray = null;
		int patchSize = 0;
		// Choose the patchSize
		switch (patchChoice) {
		case _4: {
			patchSize = 4;
			break;
		}
		case _16: {
			patchSize = 16;
			break;
		}
		case _32: {
			patchSize = 32;
			break;
		}
		case _64: {
			patchSize = 64;
			break;
		}
		case _128: {
			patchSize = 128;
			break;
		}
		case _256: {
			patchSize = 256;
			break;
		}
		case _512: {
			patchSize = 512;
			break;
		}
		case _1024: {
			patchSize = 1024;
			break;
		}
		default: {
			throw new IllegalArgumentException(patchChoice
					+ " is not a valid argument. (Valid arguments are '_4, _16', '_32', '_64', '_128', '_256', '_512' and '_1024')");
		}
		}
		// Check if 'bImage' is a NOT square image
		if (!(bImage.getHeight() == bImage.getWidth())) {
			throw new IllegalArgumentException("Input BufferedImage must have equal width and height!");
		}

		// Check if 'bImage' cannot be divided into patchSize patches.
		if (width % patchSize != 0) {
			throw new IllegalArgumentException(
					"Input BufferedImage cannot be divided into 'PatchSize' pathces proporly!");
		}

		patchesArray = new BufferedImage[width / patchSize][height / patchSize];
		int i = 0; // corresponds to 'w'
		int j = 0; // corresponds to 'h'
		for (int h = 0; h < height; h = h + patchSize) {
			for (int w = 0; w < width; w = w + patchSize) {
				patchesArray[i][j] = bImage.getSubimage(w, h, patchSize, patchSize);
				i++;
			}
			i = 0;
			j++;
		}

		return patchesArray;
	}
}
