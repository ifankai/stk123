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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class loads images from a local disk into a <code>BufferedImage</code>.
 * It is created only to mock the process of loading the input image, since in
 * the main project we do not load images from a local machine.
 * 
 * 
 * @author Azim Ahmadzadeh, Data Mining Lab, Georgia State University
 * 
 *
 */
public class ImageLoader {

	private BufferedImage bImage;

	/**
	 * 
	 * @param imgDir
	 *            The directory of the input file.
	 * @throws FileNotFoundException
	 *             in case the directory doesn't exist.
	 * @throws IOException
	 *             in case of problem in loading the file.
	 */
	public ImageLoader(String imgDir) throws FileNotFoundException {

		this.bImage = null;
		try {
			this.bImage = ImageIO.read(new File(imgDir));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find the file!");
		} catch (IOException e) {
			System.out.println("I/O Error!");
		} finally {
			bImage.flush();
		}
	}

	public BufferedImage getBImage() {
		return bImage;
	}

}