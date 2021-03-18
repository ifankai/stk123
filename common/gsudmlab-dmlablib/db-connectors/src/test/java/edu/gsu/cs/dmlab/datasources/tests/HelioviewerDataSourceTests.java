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
package edu.gsu.cs.dmlab.datasources.tests;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import edu.gsu.cs.dmlab.datasources.HelioviewerImageFileDatasource;
import edu.gsu.cs.dmlab.datasources.interfaces.IImageFileDataSource;
import edu.gsu.cs.dmlab.datatypes.Waveband;

class HelioviewerDataSourceTests {

//	@Test
//	void test() throws FileNotFoundException, IOException {
//		IImageFileDataSource src = new HelioviewerImageDatasource();
//
//		DateTime time = new DateTime(2018, 1, 1, 2, 1, 1);
//		Pair<byte[], String> pr = src.getImageAtTime(time, Waveband.AIA171);
//
//		System.out.println(new String(pr.getFirst(), pr.getFirst().length-3, 3));
//		try (FileOutputStream fos = new FileOutputStream(pr.getSecond())) {
//			fos.write(pr.getFirst());
//		}
//		System.out.println(this.checkJP2(pr.getFirst()));
//		InputStream imgStr = new ByteArrayInputStream(pr.getFirst());
//		BufferedImage img = ImageIO.read(imgStr);
//		ImageIO.write(img, "jpg", new File(pr.getSecond()+".jpg"));
//	}

	static final int JP2_SIGNATURE_BOX = 0x6a502020;
	static final byte JP2_CODESTREAM_MARKER_1 = (byte)0xFF;
	static final byte JP2_CODESTREAM_MARKER_2 = (byte)0xd9;
	static final int FILE_TYPE_BOX = 0x66747970;
	static final int FT_BR = 0x6a703220;

	
	private boolean checkJP2(byte[] byteBuffer) {
		// We make sure the first 12 bytes are the JP2 signature box
		int idx = 0;

		if ((idx + 4) < byteBuffer.length) {
			if (this.readInt(byteBuffer, idx) != 0x0000000c)
				return false;
			idx += 4;
		} else {
			return false;
		}

		if ((idx + 4) < byteBuffer.length) {
			if (this.readInt(byteBuffer, idx) != JP2_SIGNATURE_BOX)
				return false;
			idx += 4;
		} else {
			return false;
		}

		if ((idx + 4) < byteBuffer.length) {
			if (this.readInt(byteBuffer, idx) != 0x0d0a870a)
				return false;
			idx += 4;
		} else {
			return false;
		}
		
		if(byteBuffer[byteBuffer.length-2] != JP2_CODESTREAM_MARKER_1 ||
				byteBuffer[byteBuffer.length-1] != JP2_CODESTREAM_MARKER_2) {
			System.out.println("Bad EOF");
			return false;
		}

		return this.readFileTypeBox(byteBuffer, idx);
	}
	
	/**
	 * Reads a signed int (i.e., 32 bit) from the input. Prior to reading, the input
	 * should be realigned at the byte level.
	 * 
	 * @return The next byte-aligned signed int (32 bit) from the input.
	 */
	public final int readInt(byte[] byteBuffer, int pos) {
		return (((byteBuffer[pos++] & 0xFF) << 24) | ((byteBuffer[pos++] & 0xFF) << 16)
				| ((byteBuffer[pos++] & 0xFF) << 8) | (byteBuffer[pos++] & 0xFF));
	}

	final long readLong(byte[] byteBuffer, int pos) {
		return (((long) (byteBuffer[pos++] & 0xFF) << 56) | ((long) (byteBuffer[pos++] & 0xFF) << 48)
				| ((long) (byteBuffer[pos++] & 0xFF) << 40) | ((long) (byteBuffer[pos++] & 0xFF) << 32)
				| ((long) (byteBuffer[pos++] & 0xFF) << 24) | ((long) (byteBuffer[pos++] & 0xFF) << 16)
				| ((long) (byteBuffer[pos++] & 0xFF) << 8) | ((long) (byteBuffer[pos++] & 0xFF)));
	}
	
	/**
	 * This method reads the File Type box.
	 *
	 * @return false if the File Type box was not found or invalid else true
	 *
	 * @exception java.io.IOException If an I/O error occurred.
	 * @exception java.io.EOFException If the end of file was reached
	 */
	public boolean readFileTypeBox(byte[] byteBuffer, int pos) {
		int length;

		int nComp;
		boolean foundComp = false;

		// Read box length (LBox)
		length = this.readInt(byteBuffer, pos);
		if (length == 0) { // This can not be last box
			System.out.println("Zero-length of Profile Box");
			return false;
		}
		pos += 4;

		// Check that this is a File Type box (TBox)
		if (this.readInt(byteBuffer, pos) != FILE_TYPE_BOX) {
			System.out.println("Bad File Type Box");
			return false;
		}
		pos += 4;

		// Check for XLBox
		if (length == 1) { // Box has 8 byte length;
			System.out.println("File Too Big");
			return false;
		}

		// Read Brand field
		// in.readInt();
		pos += 4;

		// Read MinV field
		// in.readInt();
		pos += 4;

		// Check that there is at least one FT_BR entry in in
		// compatibility list
		nComp = (length - 16) / 4; // Number of compatibilities.
		for (int i = nComp; i > 0; i--) {
			if (this.readInt(byteBuffer, pos) == FT_BR)
				foundComp = true;
			pos += 4;
		}
		if (!foundComp) {
			System.out.println("No FT_BR entry.");

			return false;
		}

		return true;
	}

}
