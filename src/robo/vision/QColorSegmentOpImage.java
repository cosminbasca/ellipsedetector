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
public class QColorSegmentOpImage extends UntiledOpImage
{
	private 	final int		WHITE	=	255;
	private 	final int 		BLACK	=	0;
	
	private 	float 	minB	=	20.0F 	/ 255.0F;		// below - BLACK
	private 	float 	maxB	=	230.0F 	/ 255.0F;		// above - WHITE
	@SuppressWarnings("unused")
	private 	float 	minS	=	20.0F / 255.0F;
	
	private 	float 	hueClasses[];
	private 	float	qty;
		
	public QColorSegmentOpImage(RenderedImage source, ImageLayout layout,Integer qt)
    {
    	super(source, null, layout);                
    	
    	this.qty		=	(float)qt.intValue();
    	this.hueClasses	=	new float[(int)qty];
    	for(int i=0;i<hueClasses.length;i++)
    		hueClasses[i]	=	1.0F / qty * (float)i;
    		
    	
    }    


    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
    	Raster src = srcarr[0];
    	int		width	=	src.getWidth();
    	int 	height	=	src.getHeight();
    	float	hsb[]	=	new float[3];
    	int 	r		= 	0;
    	int 	g		= 	0;
    	int 	b		= 	0;
    	int 	cls		=	0;
    	int		rgb		= 	0;
    	float	qtyStep	=	1.0F / qty;
    	
    	for(int x = 0; x<width ; x++)
    	{
    		for(int y = 0; y<height ; y++)
	    	{
	    		// set each color to its class
	    		r = src.getSample(x,y,0);
	    		g = src.getSample(x,y,1);
	    		b = src.getSample(x,y,2);
	    		Color.RGBtoHSB( r, g, b , hsb );
	    		
	    		cls	=	(int)Math.floor(hsb[0] / qtyStep);
	    		
	    		if(hsb[2] <= minB)	// it's BLACK
	    		{
	    			dst.setSample(x,y,0,BLACK);
	    			dst.setSample(x,y,1,BLACK);
	    			dst.setSample(x,y,2,BLACK);
	    		}
	    		else if(hsb[2] >= maxB)	// it's WHITE
	    		{
	    			dst.setSample(x,y,0,WHITE);
	    			dst.setSample(x,y,1,WHITE);
	    			dst.setSample(x,y,2,WHITE);
	    		}
	    		else
	    		{
	    			// saturatie maxima - luminozitate / 2
	    			rgb = Color.HSBtoRGB(this.hueClasses[cls],	1.0F,	0.5F);
	    			dst.setSample(x,y,0,(rgb / 0x10000) % 0x100 );
	    			dst.setSample(x,y,1,(rgb / 0x100) 	% 0x100 );
	    			dst.setSample(x,y,2,(rgb / 0x1) 	% 0x100 );
	    		}
	    	}
	    }
	    
    }   
    
}
