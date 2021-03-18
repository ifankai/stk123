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
 * Provides convolution kernels for Gaussian image smoothing / blurring
 **************************************************************************/

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
 * @author Azim Ahmadzadeh, updated by Dustin Kempton, Data Mining Lab, Georgia
 *         State University
 * 
 */
public class ConvolutionKernel {

   public static final double[][] GAUSSIAN_KERNEL = {{2/159.0, 4/159.0 , 5/159.0 , 4/159.0 , 2/159.0},
                                                     {4/159.0, 9/159.0 , 12/159.0, 9/159.0 , 4/159.0}, 
                                                     {5/159.0, 12/159.0, 15/159.0, 12/159.0, 5/159.0}, 
                                                     {4/159.0, 9/159.0 , 12/159.0, 9/159.0 , 4/159.0}, 
                                                     {2/159.0, 4/159.0 , 5/159.0 , 4/159.0 , 2/159.0}};
   
   /**
    * Generates a 1D averaging kernel with user-defined dimensions
    */
   public static double[] averagingKernel(int r) {
      double[] kernel = new double[r];
      double entry = 1.0 / r; 

         for (int i = 0; i < r; i++) 
            kernel[i] = entry;

      return kernel;
   } 


   /**
    * Generates a 2D averaging kernel with user-defined dimensions
    */
   public static double[][] averagingKernel(int r, int c) {
      double[][] kernel = new double[r][c];
      double entry = 1.0 / (r * c);

      for (int i = 0; i < r; i++)
         for (int j = 0; j < c; j++)
            kernel[i][j] = entry;

      return kernel;
   }

}
