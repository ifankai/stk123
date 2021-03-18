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
package edu.gsu.cs.dmlab.imageproc.imageparam.interfaces;

/**
 * This interface provides the pre-defined patch-sizes used for processing
 * images patch by patch, and the pre-defined byte-shifts necessary for
 * converting matrices to BufferedImage objects, and vice-versa.
 * 
 * @author Azim Ahmadzadeh, updated by Dustin Kempton, Data Mining Lab, Georgia
 *         State University
 * 
 *
 */
public interface IMeasures {

	public final int INTENSITY = 256;
	public final int INTENSITY_MAX = 255;

	/**
	 * This contains all possible sizes of a patch that parameters are going to be
	 * computed for. <br>
	 * <b>Note:</b> For adding new items to the list, make sure that the input
	 * images can be divided by the new number. (bImage.getWidth % newItem == 0)
	 */
	public enum PatchSize {
		_0(0), _1(1), _4(4), _16(16), _32(32), _64(64), _128(128), _256(256), _512(512), _1024(1024);

		private int size;

		private PatchSize(int size) {
			this.size = size;
		}

		public int getSize() {
			return this.size;
		}
	}

	/**
	 * 
	 * The color channel <br>
	 * <code>
	 * +--------+--------+--------+--------+ bits <br>
	 * |AAAAAAAA|RRRRRRRR|GGGGGGGG|BBBBBBBB| <br>
	 * </code><br>
	 * To get the green channel from 'color' (hex);<br>
	 * <code>int green = (color &amp; 0xff00) &lt;&lt; 8</code> <br>
	 * To convert an intensity to a color (hex):
	 * <code>int value = (0xFF000000 &amp; color) &lt;&lt; 24 | (0x000000FF &amp; color) &lt;&lt; 16 |
	 *					  (0x000000FF &amp; color) &lt;&lt; 8 | (0x000000FF &amp; color)
	 *</code><br>
	 * <br>
	 */
	public enum Channel {
		A('A'), R('R'), G('G'), B('B');

		private int channel;

		private Channel(char channel) {
			this.channel = channel;
		}

		public int getShiftSize() {

			int shift = 0;
			switch (this) {
			case A: {
				shift = 24;
				break;
			}
			case R: {
				shift = 16;
				break;
			}
			case G: {
				shift = 8;
				break;
			}
			case B: {
				shift = 0;
				break;
			}
			default: {
				throw new IllegalArgumentException(
						channel + " is not a valid argument. (Valid arguments are 'R', 'G' and 'B')");
			}
			}
			return shift;
		}

		public int getChannelByte() {
			int b = 0;
			switch (this) {
			case A: {
				b = 0xff000000;
				break;
			}
			case R: {
				b = 0xff0000;
				break;
			}
			case G: {
				b = 0xff00;
				break;
			}
			case B: {
				b = 0xff;
				break;
			}
			default: {
				throw new IllegalArgumentException(
						channel + " is not a valid argument. (Valid arguments are 'R', 'G' and 'B')");
			}
			}
			return b;

		}
	}
}