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
/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 * 
 * PURPOSE: Used to calculate image derivatives.
 * 
 * GRADIENTS: 
 *    left               (f_n)   - (f_n-1)
 *    right              (f_n+1) - (f_n)
 *    simple_symmetric   (f_n+1) - (f_n-1)
 *    double-symmetric   (f_n+1) - 2 * (f_n) + (f_n-1)
 *    
 *************************************************************************/

/************************************************************************
 * This class was originally implemented by Jason Altschuler at  
 * www.mit.edu/~jasonalt/.
 * 
 * Jason kindly let us modify and employ his implementation to our open-source
 * library, DMlabLib.
 ************************************************************************/
package edu.gsu.cs.dmlab.imageproc.edgedetection.imagederivatives;

/**
 * 
 * @author Azim Ahmadzadeh Data Mining Lab, Georgia State University
 * 
 */
public enum Gradient {
	LEFT, RIGHT, SIMPLE_SYMMETRIC, DOUBLE_SYMMETRIC;

	@Override
	public String toString() {
		switch (this) {
		case LEFT:
			return "Left gradient: (f_n) - (f_n-1)";
		case RIGHT:
			return "Right gradient: (f_n+1) - (f_n)";
		case SIMPLE_SYMMETRIC:
			return "Simple_symmetric gradient: (f_n+1) - (f_n-1)";
		case DOUBLE_SYMMETRIC:
			return "Double_symmetric gradient: (f_n+1) - 2 * (f_n) + (f_n-1)";
		}
		return super.toString();
	}
}
