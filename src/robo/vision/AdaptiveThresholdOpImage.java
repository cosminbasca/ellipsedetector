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

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;

import robo.vision.widgets.VisionUtils;

@SuppressWarnings("unchecked")
public class AdaptiveThresholdOpImage extends UntiledOpImage
{
	private int size;
	private int constant;
	private int method;	
	
	public AdaptiveThresholdOpImage(RenderedImage source, ImageLayout layout, Integer s, Integer c,Integer m)
    {
        super(source, null, layout);
     	this.size		=	s.intValue();
     	this.constant	=	c.intValue();   
     	this.method		=	m.intValue();
    }

    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
        Raster src = srcarr[0];
        
        switch(method)
        {
        	case AdaptiveThresholdMethod.MEAN_THRESHOLD :
        		applyMean(src,dst);
        		break;
        	case AdaptiveThresholdMethod.MEDIAN_THRESHOLD :
        		applyMedian(src,dst);
        		break;
        	case AdaptiveThresholdMethod.MEAN_MAXMIN_THRESHOLD :
        		applyMeanMaxMin(src,dst);
        		break;
        	case AdaptiveThresholdMethod.MEC_THRESHOLD :
        		applyMEC(src,dst);
        		break;
        	case AdaptiveThresholdMethod.MCC_THRESHOLD :
        		applyMCC(src,dst);
        		break;
        }                
    }
    
    private void applyMEC(Raster src,WritableRaster dst)
    {
    	int width = src.getWidth();
	    int height= src.getHeight();
	    
	    Histogram hist = VisionUtils.createHistogram(src,null,new int[]{256,256,256});
	    //double entropy[] = hist.getEntropy();
	    	    
	    //get histogram	    
	    int mec	  = VisionUtils.getHistogramMEC(VisionUtils.getHistogramDistributions(hist,0,width,height));
	    int tmpSample	= 0;			    
	    
	    
	    for(int x = 0; x < width; x++)
	    {
			for(int y = 0; y < height; y++)
			{
				tmpSample = src.getSample(x,y,0);
				if(tmpSample < mec)
				{
					setBlack(dst,x,y);
				}
				else 
				{
					setWhite(dst,x,y);
				}
			}
		}			
    }
    
    private void applyMCC(Raster src,WritableRaster dst)
    {
    	int width = src.getWidth();
	    int height= src.getHeight();
		
		Histogram hist = VisionUtils.createHistogram(src,null,new int[]{256,256,256});
		
		//get histogram	    
	    int mcc	  = VisionUtils.getHistogramMCC(VisionUtils.getHistogramDistributions(hist,0,width,height));
		int tmpSample	= 0;			    
			    
	    for(int x = 0; x < width; x++)
	    {
			for(int y = 0; y < height; y++)
			{
				tmpSample = src.getSample(x,y,0);
				if(tmpSample < mcc)
				{
					setBlack(dst,x,y);
				}
				else 
				{
					setWhite(dst,x,y);
				}
			}
		}			
    }
    
    private void applyMean(Raster src,WritableRaster dst)
    {
    	int width = src.getWidth();
	    int height= src.getHeight();
	    
	    int mean 		= 0;
	    int count 		= 0;
	    int tmpSample 	= 0;
	    
	    //Now find the sum of values in the size X size neigbourhood 
	    for(int j = 0; j < height; j++)
	    {
			for(int i = 0; i < width; i++)
			{
				mean 	= 0;
				count 	= 0;
				//Check the local neighbourhood
				for(int k = 0; k < size; k++)
				{
					for(int l = 0; l < size; l++)
					{
				    	try
				    	{
				    		tmpSample = src.getSample((i-((int)(size/2))+k),(j-((int)(size/2))+l),0);
				      		mean = mean + tmpSample;
				      		count++;
				    	}			    	
				    	catch(ArrayIndexOutOfBoundsException e)	//If out of bounds then ignore pixel
				    	{
				    	}
					}
				}
				//Find the mean value
				mean = (int)(mean /count) - this.constant;
				
				//Threshold below the mean
				tmpSample = src.getSample(i,j,0);
				if(tmpSample > mean)
				{
					setBlack(dst,i,j);
				}
				else 
				{
					setWhite(dst,i,j);
				}
			}
	    }
    }
    
    private void applyMedian(Raster src,WritableRaster dst)
    {
    	int width 		= src.getWidth();
	    int height		= src.getHeight();
	    int median 		= 0;
	    int tmpSample 	= 0;
	    int [] values 	= new int[size*size];
	    int count 		= 0;
	    
	    //Now find the values in the size X size neigbourhood 
	    for(int j = 0; j < height; j++)
	    {
			for(int i = 0; i < width; i++)
			{
				median 	= 0;
				count 	= 0;
				values 	= new int[size*size];
				//Check the local neighbourhood
				for(int k = 0; k < size; k++)
				{
					for(int l = 0; l < size; l++)
					{
						try
						{
							tmpSample	= src.getSample((i-((int)(size/2))+k),(j-((int)(size/2))+l),0);
							values[count] = tmpSample;
							count++;
						}
						//If out of bounds then ignore pixel
						catch(ArrayIndexOutOfBoundsException e)
						{
						}
					}
				}
				//Find the median value
				
				//First Sort the array
				Arrays.sort(values);
				
				//Then select the median
				count = count / 2;
				median = values[count] - this.constant;
				
				//Threshold below the median
				tmpSample = src.getSample(i,j,0);
				if(tmpSample >= median)
				{
					setBlack(dst,i,j);
				}
				else 
				{
					setWhite(dst,i,j);
				}
			}
	    }
    }
    
    private void applyMeanMaxMin(Raster src,WritableRaster dst)
    {
    	int width 		= src.getWidth();
	    int height		= src.getHeight();
	    int mean 		= 0;
	    int max, min;
	    //int [] values 	= new int[size*size];
	    int tmp			= 0;
	    int tmpSample	= 0;
	
	    //Now find the max and min of values in the size X size neigbourhood 
	    for(int j = 0; j < height; j++)
	    {
			for(int i = 0; i < width; i++)
			{
				mean	= 0;
				max 	= src.getSample(i,j,0);
				min 	= src.getSample(i,j,0);
				//Check the local neighbourhood
				for(int k = 0; k < size; k++)
				{
					for(int l = 0; l < size; l++)
					{
						try
						{
							tmpSample = src.getSample((i-((int)(size/2))+k),(j-((int)(size/2))+l),0);
							tmp = tmpSample;
							if(tmp > max)
							{
								max = tmp;
							}
							if(tmp < min)
							{
								min = tmp;
							}
						}
						//If out of bounds then ignore pixel
						catch(ArrayIndexOutOfBoundsException e)
						{
						}
					}
				}
				//Find the mean value
				
				tmp = max + min;
				tmp = tmp / 2;
				mean = tmp - this.constant;
				
				//Threshold below the mean
				tmpSample = src.getSample(i,j,0);
				if(tmpSample >= mean)
				{
					setBlack(dst,i,j);
				}
				else 
				{
					setWhite(dst,i,j);
				}
			}
	    }
	    
    }
    
    private void setWhite(WritableRaster dst,int i,int j)
    {
    	dst.setSample(i,j,0,255);
		dst.setSample(i,j,1,255);
		dst.setSample(i,j,2,255);	
    }
    
    private void setBlack(WritableRaster dst,int i,int j)
    {
    	dst.setSample(i,j,0,0);
		dst.setSample(i,j,1,0);
		dst.setSample(i,j,2,0);	
    }
}
