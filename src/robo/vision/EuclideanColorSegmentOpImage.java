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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;


@SuppressWarnings("unchecked")
public class EuclideanColorSegmentOpImage extends UntiledOpImage
{
	
	// se lucreaza in hsv - cu s si v constante
	private int 	classes[][];	// YRGB
	private int		rgbClasses[][];
		
	public EuclideanColorSegmentOpImage(RenderedImage source, ImageLayout layout,Color[] cls)
    {
    	super(source, null, layout);                    	
    	this.classes		= 	new int[cls.length][4];		// spatiul YRGB
    	this.rgbClasses		= 	new int[cls.length][3];		// spatiul RGB
    	
    	for(int i=0;i<cls.length;i++)
    	{
    		this.toYRGB(cls[i],this.classes[i]);    
    				    	
    		this.rgbClasses[i][0] = cls[i].getRed();
    		this.rgbClasses[i][1] = cls[i].getGreen();
    		this.rgbClasses[i][2] = cls[i].getBlue();
    	}    	
    }


    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
    	Raster 			src  = srcarr[0];
    	int width	=	src.getWidth();
    	int height	=	src.getHeight();
    	
    	
    	int yrgb[]	=	new int[4];
    	int cls		=   -1;
    	
    	for(int x = 0; x<width; x++)
    	{
    		for(int y=0; y<height ; y++)
    		{    			    		
    			this.toYRGB(src.getSample(x,y,0),	// R
    						src.getSample(x,y,1),	// G
    						src.getSample(x,y,2),	// B
    						yrgb);					// to YRGB	- extract intencity
    			
    			cls	= this.getColorClass(yrgb);
    			if(cls == -1)
    			{	// unknown == black
    				dst.setSample(x,y,0, 0);
					dst.setSample(x,y,1, 0);
					dst.setSample(x,y,2, 0);
    			}
    			else
    			{
					dst.setSample(x,y,0, rgbClasses[cls][0]);
					dst.setSample(x,y,1, rgbClasses[cls][1]);
					dst.setSample(x,y,2, rgbClasses[cls][2]);
    			}    			
    		}
    	}
    	
    }   
    
    private int getColorClass(int yrgb[])
    {
    	int clsIDX 		= -1 ; // no class;
    	double MinDist 	= Double.MAX_VALUE;
    	double d 		= 0.0;
    	
    	for(int i=0;i<this.classes.length;i++)
    	{
    		d = this.getColorDistance(yrgb[0],yrgb[1],yrgb[2],yrgb[3],classes[i][0],classes[i][1],classes[i][2],classes[i][3]);
    		if(d < MinDist)
    		{
    			MinDist = d;
    			clsIDX 	= i;
    		}
    	}
    	
    	return clsIDX;
    }
    
    private double getColorDistance(int y1,int r1,int g1,int b1,int y2,int r2,int g2,int b2)
    {
    	return Math.sqrt( (y1-y2)*(y1-y2)+(r1-r2)*(r1-r2)+(g1-g2)*(g1-g2)+(b1-b2)*(b1-b2) );
    }
    
    private void toYRGB(int r,int g,int b,int yrgb[])
    {
    	yrgb[0] = (int)Math.round( (double)(r + g + b) / 3.0 );
    	yrgb[1] = (int)Math.round( (double)(r) / (double)yrgb[0] );
    	yrgb[2] = (int)Math.round( (double)(g) / (double)yrgb[0] );
    	yrgb[3] = (int)Math.round( (double)(b) / (double)yrgb[0] );
    }
    
    private void toYRGB(Color c,int yrgb[])
    {
    	this.toYRGB(c.getRed(),c.getGreen(),c.getBlue(),yrgb);
    }
}
