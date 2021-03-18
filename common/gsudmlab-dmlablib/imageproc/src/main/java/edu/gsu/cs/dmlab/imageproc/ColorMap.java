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
package edu.gsu.cs.dmlab.imageproc;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.stream.IntStream;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import edu.gsu.cs.dmlab.exceptions.InvalidConfigException;
import edu.gsu.cs.dmlab.imageproc.colormaps.Autumn;
import edu.gsu.cs.dmlab.imageproc.colormaps.Bone;
import edu.gsu.cs.dmlab.imageproc.colormaps.Hot;
import edu.gsu.cs.dmlab.imageproc.colormaps.Jet;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_131;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_1600;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_1700;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_171;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_193;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_211;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_304;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_335;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_4500;
import edu.gsu.cs.dmlab.imageproc.colormaps.SDO_AIA_94;
import edu.gsu.cs.dmlab.imageproc.colormaps.Spring;
import edu.gsu.cs.dmlab.imageproc.colormaps.Summer;
import edu.gsu.cs.dmlab.imageproc.colormaps.Winter;

/**
 * @author Dustin Kempton, Data Mining Lab, Georgia State University
 * 
 */
public abstract class ColorMap {

	public enum COLORMAP {
		AUTUMN, BONE, JET, WINTER, HOT, SUMMER, SPRING, SDO_AIA_131, SDO_AIA_1600, SDO_AIA_1700, SDO_AIA_171, SDO_AIA_193, SDO_AIA_211, SDO_AIA_304, SDO_AIA_335, SDO_AIA_4500, SDO_AIA_94
	}

	@Override
	public void finalize() {
		this._lut = null;
	}

	protected IndexColorModel _lut;

	protected static double[] linspace(double x0, double x1, int n) {
		double step = (x1 - x0) / (n - 1);
		double[] vals = new double[n];
		for (int i = 0; i < n; i++) {
			vals[i] = x0 + i * step;
		}
		return vals;
	}

	// Interpolates from a base colormap.
	protected static IndexColorModel linear_colormap(double[] X, double[] r, double[] g, double[] b, double[] xi)
			throws InvalidConfigException {
		byte[] r_byte = new byte[xi.length];
		byte[] g_byte = new byte[xi.length];
		byte[] b_byte = new byte[xi.length];
		LinearInterpolator li_interpolator = new LinearInterpolator();
		PolynomialSplineFunction r_interp = li_interpolator.interpolate(X, r);
		PolynomialSplineFunction g_interp = li_interpolator.interpolate(X, g);
		PolynomialSplineFunction b_interp = li_interpolator.interpolate(X, b);

		IntStream.range(0, xi.length).forEach(i -> {
			r_byte[i] = (byte) (int) (r_interp.value(xi[i]) * 255);
			g_byte[i] = (byte) (int) (g_interp.value(xi[i]) * 255);
			b_byte[i] = (byte) (int) (b_interp.value(xi[i]) * 255);
		});

		return new IndexColorModel(8, xi.length, r_byte, g_byte, b_byte);
	}

	// Interpolates from a base colormap.
	protected static IndexColorModel linear_colormap(double[] X, double[] r, double[] g, double[] b, int n)
			throws InvalidConfigException {
		return linear_colormap(X, r, g, b, linspace(0, 0.999999, n));
	}

	public static BufferedImage applyColorMap(BufferedImage src, COLORMAP colormap) throws InvalidConfigException {
		ColorMap cm = null;
		switch (colormap) {
		case AUTUMN:
			cm = new Autumn();
			break;
		case BONE:
			cm = new Bone();
			break;
		case JET:
			cm = new Jet();
			break;
		case WINTER:
			cm = new Winter();
			break;
		case HOT:
			cm = new Hot();
			break;
		case SUMMER:
			cm = new Summer();
			break;
		case SPRING:
			cm = new Spring();
			break;
		case SDO_AIA_131:
			cm = new SDO_AIA_131();
			break;
		case SDO_AIA_1600:
			cm = new SDO_AIA_1600();
			break;
		case SDO_AIA_1700:
			cm = new SDO_AIA_1700();
			break;
		case SDO_AIA_193:
			cm = new SDO_AIA_193();
			break;
		case SDO_AIA_211:
			cm = new SDO_AIA_211();
			break;
		case SDO_AIA_304:
			cm = new SDO_AIA_304();
			break;
		case SDO_AIA_171:
			cm = new SDO_AIA_171();
			break;
		case SDO_AIA_335:
			cm = new SDO_AIA_335();
			break;
		case SDO_AIA_4500:
			cm = new SDO_AIA_4500();
			break;
		case SDO_AIA_94:
			cm = new SDO_AIA_94();
			break;
		}

		if (cm != null) {
			WritableRaster raster = src.getRaster();
			BufferedImage tmpImg = new BufferedImage(cm._lut, raster, false, null);
			BufferedImage tmpImg2 = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D gphic = tmpImg2.createGraphics();
			gphic.drawImage(tmpImg, 0, 0, null);

			tmpImg.flush();
			tmpImg = null;
			raster = null;
			gphic.dispose();
			gphic = null;
			cm = null;

			return tmpImg2;
		}
		return src;

	}

	// Setup base map to interpolate from.
	protected abstract void init(int n) throws InvalidConfigException;

}