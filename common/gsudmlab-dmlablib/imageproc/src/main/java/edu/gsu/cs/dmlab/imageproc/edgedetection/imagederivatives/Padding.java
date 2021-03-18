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
 * PURPOSE: Used to calculate image derivatives
 * 
 * PADDING: 
 *    zeros       0  ,  z_1,  z_2,  ...,  z_n-1,  z_n,  0
 *    same        z_1,  z_1,  z_2,  ...,  z_n-1,  z_n,  z_n
 *    symmetric   z_2,  z_1,  z_2,  ...,  z_n-1,  z_n,  z_n-1
 *    circular    z_n,  z_1,  z_2,  ...,  z_n-1,  z_n1, z_1
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
 * @author Jason Altschuler, modified by Azim Ahmadzadeh of
 *         Data Mining Lab, Georgia State University
 * 
 */
public enum Padding {
   ZEROS, SAME, SYMMETRIC, CIRCULAR;

   @Override
   public String toString() {   
      switch (this) {
      case ZEROS:     return "Zeros padding: 0, z_1, z_2, ..., z_n-1, z_n, 0";
      case SAME:      return "Same padding: z_1, z_1, z_2, ..., z_n-1, z_n, z_n";
      case SYMMETRIC: return "Symmetric padding: z_2, z_1, z_2, ..., z_n-1, z_n, z_n-1";
      case CIRCULAR:  return "Circular padding: z_n, z_1, z_2, ..., z_n-1, z_n1, z_1";
      }
      return super.toString();
   }
}

