/*
 * Copyright 2005, 2009 Cosmin Basca.
 * e-mail: cosmin.basca@gmail.com
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package robo.vision;

import javax.media.jai.KernelJAI;

public class Kernel 
{
	private Kernel()
	{
		
	}
	// SOBEL
	public final static float[] SOBEL_V_DATA = {
		-1.0F, -2.0F, -1.0F,
		 0.0F,  0.0F,  0.0F,
		 1.0F,  2.0F,  1.0F
		};
		
	public final static float[] SOBEL_H_DATA = {
		 1.0F,  0.0F, -1.0F,
		 2.0F,  0.0F, -2.0F,
		 1.0F,  0.0F, -1.0F
		};
		
	//ROBERTS
	public final static float[] ROBERTS_V_DATA = {
		-1.0F, 0.0F, 0.0F,
		 0.0F, 1.0F, 0.0F,
		 0.0F, 0.0F, 0.0F
		};
	
	public final static float[] ROBERTS_H_DATA = { 
		 0.0F, 0.0F, -1.0F,
		 0.0F, 1.0F, 0.0F,
		 0.0F, 0.0F, 0.0F
		};
		
	//PREWITT
	
	public final static float[] PREWITT_V_DATA = {
		-1.0F, -1.0F, -1.0F,
		 0.0F, 0.0F, 0.0F,
		 1.0F, 1.0F, 1.0F
		};
		
	public final static float[] PREWITT_H_DATA = { 
		 1.0F, 0.0F, -1.0F,
		 1.0F, 0.0F, -1.0F,
		 1.0F, 0.0F, -1.0F
		};
		
	// FREI-CHEN
	public final static float[] FREICHEN_V_DATA = {
		-1.0F, -1.414F, -1.0F,
		 0.0F, 0.0F, 0.0F,
		 1.0F, 1.414F, 1.0F
		};
		
	public final static float[] FREICHEN_H_DATA = { 
		 1.0F, 0.0F, -1.0F,
		 1.414F, 0.0F, -1.414F,
		 1.0F, 0.0F, -1.0F
		};
		
	// KIRSH	
	public final static float[] KIRSH_V_DATA = { 
		-3.0F, -3.0F, 5.0F,
		-3.0F,  0.0F, 5.0F,
		-3.0F, -3.0F, 5.0F
		};
		
	public final static float[] KIRSH_H_DATA = {
		-3.0F, -3.0F, -3.0F,
		-3.0F,  0.0F, -3.0F,
		 5.0F,  5.0F,  5.0F
		};
		
		
	public final static float[] Sharp1_DATA ={
				 0.0F, -1.0F,  0.0F,
                -1.0F,  5.0F, -1.0F,
                 0.0F, -1.0F,  0.0F
            };

	public final static float[] Sharp2_DATA ={
                -1.0F, -1.0F, -1.0F,
                -1.0F,  9.0F, -1.0F,
                -1.0F, -1.0F, -1.0F
            };

	public final static float[] Sharp3_DATA ={
                 1.0F, -2.0F, 1.0F,
                -2.0F,  5.0F,-2.0F,
                 1.0F, -2.0F, 1.0F
            };

	public final static float[] Sharp4_DATA ={
                -1.0F, 1.0F,-1.0F,
                 1.0F, 1.0F, 1.0F,
                -1.0F, 1.0F,-1.0F
            };

	public final static float[] Laplace1_DATA ={
                -1.0F,-1.0F,-1.0F,
                -1.0F, 8.0F,-1.0F,
                -1.0F,-1.0F,-1.0F
            };

	public final static float[] Laplace2_DATA ={
                 0.0F,-1.0F, 0.0F,
                -1.0F, 4.0F,-1.0F,
                 0.0F,-1.0F, 0.0F
            };

	public final static float[] Box_DATA ={
                1.0F, 1.0F, 1.0F,
                1.0F, 1.0F, 1.0F,
                1.0F, 1.0F, 1.0F
            };

	public final static float[] Low_Pass_DATA ={
                1.0F, 2.0F, 1.0F,
                2.0F, 4.0F, 2.0F,
                1.0F, 2.0F, 1.0F
            };

	public final static float[] Emboss_DATA ={
                -1.0F,-2.0F, 0.0F,
                -2.0F, 0.0F, 2.0F,
                 0.0F, 2.0F, 1.0F
            };
		
		
	// inited kernels
	public static KernelJAI SOBEL_V 	= new KernelJAI(3,3,Kernel.SOBEL_V_DATA);
	public static KernelJAI SOBEL_H 	= new KernelJAI(3,3,Kernel.SOBEL_H_DATA);

	public static KernelJAI ROBERTS_V 	= new KernelJAI(3,3,Kernel.ROBERTS_V_DATA);
	public static KernelJAI ROBERTS_H 	= new KernelJAI(3,3,Kernel.ROBERTS_H_DATA);
	
	public static KernelJAI PREWITT_V 	= new KernelJAI(3,3,Kernel.PREWITT_V_DATA);
	public static KernelJAI PREWITT_H 	= new KernelJAI(3,3,Kernel.PREWITT_H_DATA);
	
	public static KernelJAI FREICHEN_V 	= new KernelJAI(3,3,Kernel.FREICHEN_V_DATA);
	public static KernelJAI FREICHEN_H 	= new KernelJAI(3,3,Kernel.FREICHEN_H_DATA);
	
	public static KernelJAI KIRSH_V 	= new KernelJAI(3,3,Kernel.KIRSH_V_DATA);
	public static KernelJAI KIRSH_H 	= new KernelJAI(3,3,Kernel.KIRSH_H_DATA);
	
	public static KernelJAI Sharp1   	= new KernelJAI(3,3,Kernel.Sharp1_DATA);
	public static KernelJAI Sharp2   	= new KernelJAI(3,3,Kernel.Sharp2_DATA);
	public static KernelJAI Sharp3   	= new KernelJAI(3,3,Kernel.Sharp3_DATA);
	public static KernelJAI Sharp4   	= new KernelJAI(3,3,Kernel.Sharp4_DATA);	
	public static KernelJAI Laplace1   	= new KernelJAI(3,3,Kernel.Laplace1_DATA);
	public static KernelJAI Laplace2   	= new KernelJAI(3,3,Kernel.Laplace2_DATA);
	public static KernelJAI Box		   	= new KernelJAI(3,3,Kernel.Box_DATA);
	public static KernelJAI Low_Pass   	= new KernelJAI(3,3,Kernel.Low_Pass_DATA);
	public static KernelJAI Emboss   	= new KernelJAI(3,3,Kernel.Emboss_DATA);
	

	/**
	* Calculates the discrete value at x,y of the 
	* 2D gaussian distribution.
	*
	* @param theta     the theta value for the gaussian distribution
	* @param x         the point at which to calculate the discrete value
	* @param y         the point at which to calculate the discrete value
	* @return          the discrete gaussian value
	*/
	private static float gaussianDiscrete2D(float theta, int x, int y)
	{
		float g = 0;
		for(float ySubPixel = y - 0.5F; ySubPixel < y + 0.6F; ySubPixel += 0.1F)
		{
	  		for(float xSubPixel = x - 0.5F; xSubPixel < x + 0.6F; xSubPixel += 0.1F)
	  		{
				g = g + (float)((1/(2*Math.PI*theta*theta)) * Math.pow(Math.E,-(xSubPixel*xSubPixel+ySubPixel*ySubPixel)/(2*theta*theta)));
	  		}
		}
		g = g/121;
		//System.out.println(g);
		return g;
	}
	
	/**
	* Calculates several discrete values of the 2D gaussian distribution.
	*
	* @param theta     the theta value for the gaussian distribution
	* @param size      the number of discrete values to calculate (pixels)
	* @return          2Darray (size*size) containing the calculated 
	* discrete values
	*/
	private static float[] gaussian2D(float theta, int size)
	{
		float[][] kernel = new float [size][size];
		for(int j=0;j<size;++j)
		{
	  		for(int i=0;i<size;++i)
	  		{
				kernel[i][j]=gaussianDiscrete2D(theta,i-(size/2),j-(size/2));
	  		}
		}
	
		float sum = 0;
		for(int j=0;j<size;++j)
		{
	  		for(int i=0;i<size;++i)
	  		{
				sum = sum + kernel[i][j];
	
	  		}	
		}
		
		float[] kern = new float[size*size];
		for(int j=0;j<size;++j)
		{
	  		for(int i=0;i<size;++i)
	  		{
				kern[i+j*size] = kernel[i][j];
	  		}	
		}
	
		return kern;
	}	
	
	public static KernelJAI getGaussian(double theta,int size)
	{				
		return new KernelJAI(size,size,Kernel.gaussian2D((float)theta,size));
	}
}
