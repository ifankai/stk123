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
package edu.gsu.cs.dmlab.imageproc.imageparam;

import java.util.Arrays;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import smile.math.Math;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IMeasures;
import edu.gsu.cs.dmlab.imageproc.imageparam.interfaces.IParamCalculator;
import edu.gsu.cs.dmlab.imageproc.imageparam.util.MatrixUtil;
import edu.gsu.cs.dmlab.imageproc.interfaces.IEdgeDetector;

/**
 * This class is designed to compute the <b>Fractal Dimension</b> parameter for
 * each patch of the given <code>BufferedImage</code>. In this class, a <i>Box
 * counting</i> approach known as <i>Minkowski–Bouligand Dimension</i> is
 * implemented. See <a href=
 * "https://en.wikipedia.org/wiki/Minkowski%E2%80%93Bouligand_dimension">M-B
 * Dimension</a>. <br>
 * <b>Note:</b> If <code>null</code> is passed as the edge-detector interface,
 * the given image will be considered binary with only two values: 0
 * (black:background) and 255 (white:foreground). <br>
 * <br>
 * The general steps are as follows:
 * <OL>
 * <LI>divide the image into small patches,</LI>
 * <LI>apply <code>CannyEdgeDetector</code> on each patch, to get a B &amp; W image of
 * edges,</LI>
 * <LI>run the <code>countBoxes</code> method for each edge-detected patch, and
 * get the number of boxes that intersect with a detected edge,</LI>
 * <LI>for the line introduced by the <code>boxSizes</code> and
 * <code>counts</code>, find the slope fo the regression, as the Fractal
 * Dimension of that patch,</LI>
 * <LI>finally, return a matrix of fractal dimensions of all patches.</LI>
 * </OL>
 * 
 * <b>*</b> The algorithm for the method <code>countBoxes()</code> is inspired
 * from: <a href=
 * "https://imagej.nih.gov/ij/developer/source/index.html">FractalBoxCounter</a>
 * (Look for the method <code>count(...)</code>). <br>
 * 
 * <b>*</b> For Edge Detection, a class implemented by <i>Tom Gibar</i> is
 * imported. see {@link edu.gsu.cs.dmlab.imageproc.edgedetection.CannyEdgeDetector}
 * 
 * <br>
 * <br>
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class FractalDimParamCalculator implements IParamCalculator {

	final static double epsilon = 1e-32;

	IMeasures.PatchSize patchSize;
	IEdgeDetector edgeDetector;
	int[] boxSizes;
	int maxBoxSize;
	double[] binaryColors = null;

	/**
	 * This constructor should be used when the edge detection step is needed and
	 * the input image is not in the binary mode. <br>
	 * This constructor only initializes the class fields.
	 * 
	 * @param patchSize
	 *            the size of the boxes by which the image will be processed.
	 * @param edgeDetector
	 *            the algorithm to be used to extract the binary image of edges from
	 *            the input image.
	 */
	public FractalDimParamCalculator(IMeasures.PatchSize patchSize, IEdgeDetector edgeDetector) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in FractalDimParamcalculator");
		if (edgeDetector == null)
			throw new IllegalArgumentException("EdgeDetector cannot be null in FractalDimParamcalculator"
					+ "You need to provide a valid edge detector, or use the other contructor.");

		this.patchSize = patchSize;
		this.edgeDetector = edgeDetector;
		this.binaryColors = null;
		this.maxBoxSize = findMaxBoxSize(this.patchSize.getSize());
		this.boxSizes = getAllSplits(this.maxBoxSize);
	}

	/**
	 * This constructor should be used when the edge detection step is not needed.
	 * In this case, the input image is assumed to be binary with edges separated
	 * from the background. The argument <code>binaryColors
	 * </code> represents the background (<code>binaryColors[0]</code>) and the
	 * foreground (<code>binaryColors[1]</code>) of the image. <br>
	 * This constructor only initializes the class fields.
	 * 
	 * @param patchSize
	 *            the size of the boxes by which the image will be processed.
	 * @param binaryColors
	 *            represents the background (<code>binaryColors[0]</code>) and the
	 *            foreground (<code>binaryColors[1]</code>) of the image.
	 */
	public FractalDimParamCalculator(IMeasures.PatchSize patchSize, double[] binaryColors) {

		if (patchSize == null)
			throw new IllegalArgumentException("PatchSize cannot be null in FractalDimParamcalculator");
		if (binaryColors == null)
			throw new IllegalArgumentException("binaryColors cannot be null in FractalDimParamcalculator");

		this.patchSize = patchSize;
		this.edgeDetector = null;
		this.binaryColors = binaryColors;

		this.maxBoxSize = findMaxBoxSize(this.patchSize.getSize());
		this.boxSizes = getAllSplits(this.maxBoxSize);
	}

	@Override
	public double[][] calculateParameter(double[][] image) {

		if (image == null || image[0] == null)
			throw new IllegalArgumentException("Matrix cannot be null in any of its dimensions!");

		double[][] thisPatch = null;
		double[][] fDimensions = null;
		int imageH = image.length;
		int imageW = image[0].length;
		// patchSize = 0 is reserved for the case when the entire image is to be
		// processed as onces.
		int pSize = (this.patchSize.getSize() == 0) ? imageH : patchSize.getSize();

		// Check basic requirements
		if ((imageW % pSize) != 0 || (imageH % pSize) != 0)
			throw new IllegalArgumentException("Image must be divisible by the given patchSize!");
		if (imageW != imageH)
			throw new IllegalArgumentException("Image's width must be eqaul to its height!");

		fDimensions = new double[imageH / pSize][imageW / pSize];

		for (int row = 0; row < imageH / pSize; row++) {
			for (int col = 0; col < imageW / pSize; col++) {

				thisPatch = MatrixUtil.getSubMatrixAsMatrix(image, row * pSize, col * pSize, pSize);
				fDimensions[row][col] = computeFDimensionForPatch(thisPatch);
			}
		}

		return fDimensions;
	}

	/**
	 * This method prepares the image for the box-counting method. It applies the
	 * given Edge Detection algorithm on the image (assigns white:255 to pixels
	 * representing the edges and black:0 to others), and then converts it into a 1D
	 * array and passes it to countBox method. If <code>null</code> was passed to
	 * the constructor of this class, then the given image will be considered
	 * binary, hence no preparation will be carried out.
	 * 
	 * @param image
	 * @return a single double number as the Fractal Dimension of the given image.
	 */
	private double computeFDimensionForPatch(double[][] image) {

		int imageH = image.length;
		int imageW = image[0].length;
		double[][] binraryImage = null;
		double[] colors = null;
		int[] counts = new int[this.boxSizes.length];

		// If no edge detector is provided, then use the given colors
		if (this.edgeDetector == null) {
			colors = this.binaryColors;
			binraryImage = image;
		} else {
			colors = new double[] { 0, 255 }; // colors[0]: (B) background, colors[1]:(W) foreground
			binraryImage = this.edgeDetector.getEdges(image, colors);
		}

		/*
		 * After applying edge detection, this array contains only two types of values:
		 * 0 (black:background) and 255 (white:foreground)
		 */
		double[] g = Arrays.stream(binraryImage).flatMapToDouble(Arrays::stream).toArray();

		/*
		 * Do counting for boxes of different sizes.
		 */
		for (int i = 0; i < this.boxSizes.length; i++) {
			counts[i] = countBoxes(g, imageW, imageH, this.boxSizes[i], colors);
		}

		/*
		 * Find the regression slope for the points in the plot X: log(boxSize), Y:
		 * log(counts)
		 */
		SimpleRegression reg = new SimpleRegression();
		for (int i = 0; i < counts.length; i++) {
			reg.addData(Math.log(this.boxSizes[i]), Math.log(counts[i]));
		}

		// double regressionSlope = Double.isNaN(reg.getSlope()) ? 0 : reg.getSlope();
		double regressionSlope = Double.isFinite(reg.getSlope()) ? reg.getSlope() : 0;

		/*
		 * The regression slope of such data is always negative, but we only care about
		 * the magnitude of the slope, hence the absolute value.
		 */
		return Math.abs(regressionSlope);
	}

	/**
	 * This method counts the number of boxes which intersect with at least one
	 * pixel of the foreground (i.e., detected edges)
	 * 
	 * <br>
	 * <b>Note:</b> This method assumes that the given image (represented by the 1D
	 * matrix) is already <i>edge-detected</i>, meaning that the array
	 * <code>image</code> is a binary array containing only <code>colors[0]</code>
	 * as the background and <code>colors[1]</code> as the foreground. <br>
	 * <b>Note:</b> The box counting algorithm is inspired from: <a href =
	 * "https://imagej.nih.gov/ij/developer/source/index.html">FractalBoxCounter</a>
	 * 
	 * @param image
	 *            The given image in the form of a 1D matrix of binary values;
	 *            background and foreground.
	 * @param imageW
	 *            The width of the image
	 * @param imageH
	 *            The height of the image
	 * @param boxSize
	 *            The size of the box using for applying the box-counting method on
	 *            the edges.
	 * @param colors
	 *            An array of length two. <code>colors[0]</code> represents the
	 *            background color (0:black) and color[1] the foreground color (255:
	 *            white).
	 * @return The number of boxes needed to cover all the edges on the given image.
	 */
	public int countBoxes(double[] image, int imageW, int imageH, int boxSize, double[] colors) {

		if (image.length != imageW * imageH)
			throw new IllegalArgumentException("the given array does not match with the given width and height!");
		if ((boxSize > imageW) || (boxSize > imageH))
			throw new IllegalArgumentException(
					"The give boxSize is larger than the patch on which the box counting should take place");

		double[] subPatch;
		int x = 0;
		int y = 0;
		int boxW = boxSize;
		int boxH = boxSize;
		boolean done = false;
		int boxCounter = 0;

		do {
			subPatch = getSubMatrix(image, imageW, imageH, x, y, boxW, boxH);

			for (int i = 0; i < subPatch.length; i++) {
				if (subPatch[i] == colors[1]) { // If subPatch has any foreground color in it
					/*
					 * this subPatch spans over a segment of an edge, so it should be counted and
					 * there is no need to proceed any further.
					 */
					boxCounter++;
					break;
				}
			}

			// Move the box
			x += boxSize;
			if (x + boxSize > imageW) { // If the remaining horizontal space is
										// less than a box
				boxW = imageW % boxSize; // shrink the box horizontally to fit
											// the remaining space
				if (x >= imageW) {
					// Reset w
					boxW = boxSize;
					// Reset x
					x = 0;
					// shift y
					y += boxSize;
					if (y + boxSize > imageH) { // If the remaining vertical
												// space is less than a box
						boxH = imageH % boxSize; // shrink the box vertically to
													// fit the remaining space
						done = (y >= imageH); // done if the entire image is
												// covered
					}
				}
			}
		} while (!done);

		return boxCounter;
	}

	/**
	 * This method simply returns a sub-matrix of a given matrix, except that its
	 * input and output are both 1D arrays representing 2D matrices.
	 * 
	 * @param matrix
	 *            The given matrix whose sub-matrix is inquired. This is a 1D array
	 *            representing a matrix.
	 * @param rowLength
	 *            The length of each row of the given matrix.
	 * @param colLength
	 *            The length of each column of the given matrix.
	 * @param xBox
	 *            The x coordinate of the inquired sub-matrix on the given matrix.
	 * @param yBox
	 *            The y coordinate of the inquired sub-matrix on the given matrix.
	 * @param boxW
	 *            The length of each row of the inquired sub-matrix.
	 * @param boxH
	 *            The length of each column of the inquired sub-matrix.
	 * @return a 1D array representing the inquired sub-matrix of the given matrix.
	 */
	private double[] getSubMatrix(double[] matrix, int rowLength, int colLength, int xBox, int yBox, int boxW,
			int boxH) {

		if (matrix.length != rowLength * colLength)
			throw new IllegalArgumentException(
					"the given matrix doesn't match with the given rowLength and colLength!");

		if ((boxW > rowLength) || (boxH > colLength))
			throw new IllegalArgumentException("The expected sub-matrix is bigger than the given matrix!");

		if ((boxW + xBox > rowLength) || (boxH + yBox > colLength))
			throw new IllegalArgumentException("The expected sub-matrix is out of the boundary of the given matrix!");

		double[] subMatrix = new double[boxW * boxH];
		int k = 0;

		for (int i = 0; i < boxH; i++) {
			for (int j = 0; j < boxW; j++) {
				subMatrix[k] = matrix[(xBox + j) + ((yBox + i) * rowLength)];
				k++;
			}
		}

		return subMatrix;
	}

	/**
	 * This method splits the given number recursively until the splits pass the
	 * threshold of 2.
	 * 
	 * @param a
	 * @return an array of the splits including the given number as the first
	 *         element.
	 */
	private static int[] getAllSplits(int a) {

		if (a < 2) {
			throw new IllegalArgumentException("The argument is too small to be split.");
		}

		int[] results = new int[(int) Math.log2(a * 1.0)];
		results[0] = a;
		int i = 1;
		while ((a /= 2) > 1) {
			results[i++] = a;
		}
		return results;
	}

	/*
	 * This method finds the maximum box size as the sliding window in box counting
	 * method. The calculation for a param calculator with patchSize of size x, will
	 * be: maxBoxSize = round(sqrt(x)) * 2 For example, when patchSize = 64,
	 * maxBoxSize will be 16 ( = sqrt(64) * 2).
	 */
	private static int findMaxBoxSize(int pSize) {

		/*
		 * if pSize=0, then fractal dimension is going to be calculated for the entire
		 * image at once. So, in this case, we set the maxBoxSize to 64 pixel.
		 */
		if (pSize == 0) {
			return 64;
		}

		return ((int) (2 * Math.round(Math.sqrt(pSize))));

	}

	public int[] getBoxSizes() {
		return this.boxSizes;
	}

	public int getMaxBoxSize() {
		return this.maxBoxSize;
	}
}