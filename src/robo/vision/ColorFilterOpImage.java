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
public class ColorFilterOpImage extends UntiledOpImage
{
	private 	int 	filterType;
	private 	float	hsbFilterMin[][];
	private 	float	hsbFilterMax[][];
	
	private 	float 	minB	=	30.0F / 255.0F;
	private 	float 	minS	=	20.0F / 255.0F;
		
	public ColorFilterOpImage(RenderedImage source, ImageLayout layout,Color minfilColors[],Color maxfilColors[],Integer type)
    {
    	super(source, null, layout);                
    	this.filterType	=	type.intValue();
		// set colors to be filtered    	
		hsbFilterMin		=	new float[minfilColors.length][3];
		hsbFilterMax		=	new float[maxfilColors.length][3];
		for(int i=0;i<hsbFilterMin.length;i++)
		{
			Color.RGBtoHSB( minfilColors[i].getRed(),
							minfilColors[i].getGreen(),
							minfilColors[i].getBlue(),
							hsbFilterMin[i]); 
							
			Color.RGBtoHSB( maxfilColors[i].getRed(),
							maxfilColors[i].getGreen(),
							maxfilColors[i].getBlue(),
							hsbFilterMax[i]); 
		}
    }


    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
    	Raster 	src 	= 	srcarr[0];
    	int		width	=	src.getWidth();
    	int 	height	=	src.getHeight();
    	
    	float	hsb[]	=	new float[3];
    	int 	r		= 	0;
    	int 	g		= 	0;
    	int 	b		= 	0;
    	boolean	filtered=	false;
    	
    	for(int x = 0; x<width ; x++)
    	{
    		for(int y = 0; y<height ; y++)
	    	{
	    		// filtering
	    		r = src.getSample(x,y,0);
	    		g = src.getSample(x,y,1);
	    		b = src.getSample(x,y,2);
	    		Color.RGBtoHSB( r, g, b , hsb );
	    						
	    		for(int i=0 ;i<hsbFilterMin.length; i++)		
	    		{
	    			// brigthness above certain value
	    			if( (hsb[2] > minB) &&  
	    				(hsb[1] > minS) &&
	    				(hsb[0] >= hsbFilterMin[i][0] && hsb[0] <= hsbFilterMax[i][0]) 
	    			  )
	    			{	// filter
	    				if(this.filterType==ColorFilter.FILTER_IN)
	    				{
	    					// o las
	    					dst.setSample(x,y,0,r);
	    					dst.setSample(x,y,1,g);
	    					dst.setSample(x,y,2,b);
	    				}
	    				else if(this.filterType==ColorFilter.FILTER_OUT)
	    				{
	    					// nu o las
	    					dst.setSample(x,y,0,0);
	    					dst.setSample(x,y,1,0);
	    					dst.setSample(x,y,2,0);
	    				}
	    				
	    				filtered	=	true;
	    				break;
	    			}
	    			  
	    			  
	    		}
	    		if(!filtered)
	    		{
	    			// don't filter
    				if(this.filterType==ColorFilter.FILTER_IN)
    				{
    					// nu o las
    					dst.setSample(x,y,0,0);
    					dst.setSample(x,y,1,0);
    					dst.setSample(x,y,2,0);
    				}
    				else if(this.filterType==ColorFilter.FILTER_OUT)
    				{
    					// o las
    					dst.setSample(x,y,0,r);
    					dst.setSample(x,y,1,g);
    					dst.setSample(x,y,2,b);
    				}
	    		}
	    		
	    		filtered = false;
	    			    		
	    	}
    	}
    	
    	
    }   
        
    /*
	switch(this.filterType)
    	{
    		case ColorFilter.FILTER_IN	:
    			break;
    		case ColorFilter.FILTER_OUT	:
    			break;
    	}
	*/
	
}
