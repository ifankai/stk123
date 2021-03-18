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
package edu.gsu.cs.dmlab.imageproc.edgedetection;

import edu.gsu.cs.dmlab.datatypes.Gradient;
import edu.gsu.cs.dmlab.imageproc.interfaces.IGradientCalculator;
import smile.math.Math;

/**
 * A class for calculating the gradient of pixel intensities on source images.
 * This class uses different kernels, depending on the provided gradient
 * operator name, and calculated both horizontal and vertical derivatives, as
 * well as the magnitude and angle of changes in the color intensity of
 * pixels.<br>
 * Pass the string "prewitt" to its constructor to apply
 * <a href= "https://en.wikipedia.org/wiki/Prewitt_operator">Prewitt
 * operator</a> or "sobel" to apply
 * <a href= "https://en.wikipedia.org/wiki/Sobel_operator">Sobel
 * operator</a>.<br>
 * 
 * There exists other operators which have not been yet implemented in this
 * class:<br>
 * 
 * <ui>
 * <li><a href= "https://en.wikipedia.org/wiki/Roberts_cross">Roberts
 * operator</a>
 * <li><a href= "https://en.wikipedia.org/wiki/Laplace_operator">Laplacian
 * operator</a> </ui>
 * 
 * @author Azim Ahmadzadeh, updated by Dustin Kempton, Data Mining Lab, Georgia
 *         State University
 * 
 */
public class GradientCalculator implements IGradientCalculator {

	private static final double[][] SOBEL_MASK_H = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
	private static final double[][] SOBEL_MASK_V = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
	private static final double[][] Prewitt_MASK_H = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };
	private static final double[][] Prewitt_MASK_V = { { 1, 1, 1 }, { 0, 0, 0 }, { -1, -1, -1 } };

	private static final double[][] Robert_MASK_H = { { 1, 0 }, { 0, -1 } };
	private static final double[][] Robert_MASK_V = { { 0, 1 }, { -1, 0 } };

	double[][] xKernel;
	double[][] yKernel;

	/**
	 * This constructor sets the gradient operator based on the given name.
	 * 
	 * @param gradientName name of the gradient operator among the following
	 *                     options: <ui>
	 *                     <li>"prewitt": <a href=
	 *                     "https://en.wikipedia.org/wiki/Prewitt_operator">Prewitt
	 *                     operator</a>
	 *                     <li>"sobel": <a href=
	 *                     "https://en.wikipedia.org/wiki/Sobel_operator">Sobel
	 *                     operator</a> </ui>
	 */
	public GradientCalculator(String gradientName) {

		String gName = gradientName.toLowerCase();
		switch (gName) {
		case "sobel": {
			this.xKernel = SOBEL_MASK_H;
			this.yKernel = SOBEL_MASK_V;
			// System.out.println("Sobel operator is set as the kernel.");
		}
			;
			break;
		case "prewitt": {
			this.xKernel = Prewitt_MASK_H;
			this.yKernel = Prewitt_MASK_V;
			// System.out.println("Prewitt operator is set as kernel.");
		}
			;
			break;
		case "robert": {
			this.xKernel = Robert_MASK_H;
			this.yKernel = Robert_MASK_V;
			// System.out.println("Robert operator is set as kernel.");
		}
			;
			break;
		default:
			throw new IllegalAccessError(gName + " is not known. See the documentation for available operators.");
		}
	}

	@Override
	public Gradient calculateGradientPolar(double[][] image) {

		Gradient gCart = this.calculateGradientCart(image);
		double[][] gx = gCart.gx;
		double[][] gy = gCart.gy;
		double[][] gd = gCart.gd;

		if (gx.length != gy.length || gx[0].length != gy[0].length)
			throw new IllegalArgumentException(
					"The two matrices, gx and gy, must be of the same size in both dimensions.");

		int imageH = gx.length;
		int imageW = gx[0].length;

		/*
		 * Angles of the gradient
		 */
		double[][] t = new double[imageH][imageW];
		double[] t_f = new double[imageH * imageW];
		/*
		 * Radius (magnitude) of the gradient
		 */
		double[][] r = new double[imageH][imageW];
		double[] r_f = new double[imageH * imageW];

		int i = 0;
		for (int row = 0; row < imageH; row++) {
			for (int col = 0; col < imageW; col++) {

				r[row][col] = Math.hypot(gx[row][col], gy[row][col]);
				r_f[i] = r[row][col];
				/*
				 * As the definition of atan2 indicates (Wikipedia/atan2), this function returns
				 * zero in 2 cases (this was also tested and it confirms that there are ONLY
				 * these two cases:
				 * 
				 * 1. If x > 0, y = 0: This is a meaningful zero, since in a triangle, the angle
				 * against an edge of length zero (y = 0) is zero. 2. If x = y = 0: This is
				 * mathematically undefined, but in Math library, atan2(0,0) returns zero. This
				 * zero represent a solid region with no particular texture. This zero should be
				 * treated differently.
				 * 
				 * If we do not distinguish these two cases, then in the histogram of angles, we
				 * will not be able to ignore the bin at hist[0] which is disproportionately
				 * larger than other bins.
				 * 
				 * NOTE: In TDirectionalityParamCalculator, we set will hist[0] to zero to avoid
				 * detecting this bin as a dominant peak.
				 */

				/*
				 * In case x=y=0, the followings statements are needed to distinguish horizontal
				 * (or vertical) lines on a solid bg.
				 */

				if (gx[row][col] == 0 && gy[row][col] == 0) {
					// If this pixel lies on a solid region
					if (gd[row][col] == 0) {
						t[row][col] = -Math.PI;// This constant is reserved for meaningless zeros
												// on solid (direction-less) regions
					}
					// If this pixel lies on a vertical line
					else if (gd[row][col] > 0) {
						t[row][col] = Math.PI;
					}
					// If this pixel lies on an horizontal line
					else {
						t[row][col] = Math.PI / 2;
					}
				} else if (gx[row][col] > 0 && gy[row][col] == 0) {
					t[row][col] = Math.PI; // This constant is reserved for meaningful zeros
				} else {
					t[row][col] = Math.atan2(gy[row][col], gx[row][col]);
				}
				t_f[i] = t[row][col];
				i++;
			}
		}

		Gradient gPolar = new Gradient();
		gPolar.gx = t;
		gPolar.gx_f = t_f;
		gPolar.gy = r;
		gPolar.gy_f = r_f;
		gPolar.gd = gCart.gd;
		gPolar.gd_f = gCart.gd_f;

		return gPolar;
	}

	/*
	 * The method below is inspired from:
	 * https://github.com/mkaggrey/Prewitt/blob/master/Main.java
	 */
	@Override
	public Gradient calculateGradientCart(double[][] image) {

		Gradient g = new Gradient();

		if (image == null) {
			throw new IllegalArgumentException("The image matrix cannot be null!");
		}

		if (image.length == 0 || image[0].length == 0) {
			throw new IllegalArgumentException("The image matrix cannot be of length zero in any of its dimensions!");
		}

		int imageH = image.length;
		int imageW = image[0].length;

		/*
		 * gx: horizontal differences
		 */
		double[][] gx = new double[imageH][imageW];
		double[] gx_f = new double[imageH * imageW];
		/*
		 * gx: vertical differences
		 */
		double[][] gy = new double[imageH][imageW];
		double[] gy_f = new double[imageH * imageW];
		/*
		 * gx: diagonal differences
		 */
		double[][] gd = new double[imageH][imageW];
		double[] gd_f = new double[imageH * imageW];

		int i = 0;
		for (int r = 0; r < imageH; r++) {
			for (int c = 0; c < imageW; c++) {

				if (r == 0 || r == imageH - 1 || c == 0 || c == imageW - 1) {
					gx[r][c] = 0;
					gx_f[i] = 0;
					gy[r][c] = 0;
					gy_f[i] = 0;
					gd[r][c] = 0;
					gd_f[i] = 0;
				} else {
					double xSum = 0, ySum = 0;
//					for(int kr = -1; kr < 2; kr++) {
//						for(int kc = -1 ; kc < 2; kc++) {
					for (int kr = -1; kr < this.xKernel.length - 1; kr++) {
						for (int kc = -1; kc < this.xKernel.length - 1; kc++) {
							xSum += image[r + kr][c + kc] * this.xKernel[kr + 1][kc + 1];
							ySum += image[r + kr][c + kc] * this.yKernel[kr + 1][kc + 1];
						}
					}
					gx[r][c] = xSum;
					gx_f[i] = xSum;
					gy[r][c] = ySum;
					gy_f[i] = ySum;
					gd[r][c] = Math.abs(image[r + 1][c] - image[r][c + 1]);
					gd_f[i] = gd[r][c];
				}
				i++;
			}
		}

		g.gx = gx;
		g.gx_f = gx_f;
		g.gy = gy;
		g.gy_f = gy_f;
		g.gd = gd;
		g.gd_f = gd_f;

		return g;
	}

}