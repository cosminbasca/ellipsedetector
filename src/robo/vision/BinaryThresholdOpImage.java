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

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;

@SuppressWarnings("unchecked")
public class BinaryThresholdOpImage extends UntiledOpImage
{
	private double[] low;
	private double[] high;
	
	public BinaryThresholdOpImage(RenderedImage source, ImageLayout layout, double[] l, double[] h)
    {
        super(source, null, layout);
        low	=	new double[3];
        if(l.length<3)
        {
        	low[0] = l[0];
        	low[1] = l[0];
        	low[2] = l[0];
        }
        else
        {
        	low[0] = l[0];
        	low[1] = l[1];
        	low[2] = l[2];
        }
        
        high	=	new double[3];
        if(h.length<3)
        {
        	high[0] = h[0];
        	high[1] = h[0];
        	high[2] = h[0];
        }
        else
        {
        	high[0] = h[0];
        	high[1] = h[1];
        	high[2] = h[2];
        }
    }

    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
        Raster src = srcarr[0];
        int width = src.getWidth();
        int heigth= src.getHeight();
        int bands = src.getNumBands();
        
        for(int x=0;x<width;x++)
        {
        	for(int y=0;y<heigth;y++)
        	{
        		for(int b=0;b<bands;b++)
        		{
        			int sample = src.getSample(x,y,b);
        			if( sample <= high[b] && sample >=low[b] )
        				sample = 255;
        			else 
        				sample = 0;
        			dst.setSample(x,y,b,sample);
        		}
        	}
        }
    }
}
