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

import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;
import javax.media.jai.ROI;
import javax.media.jai.UntiledOpImage;

import robo.vision.widgets.VisionUtils;


@SuppressWarnings("unchecked")
public class HueHistogramThresholdOpImage extends UntiledOpImage
{
	private		int		WHITE	=	255;
	private		int		BLACK	=	0;
	
	@SuppressWarnings("unused")
	private 	float 	minB	=	10.0F / 255.0F;
	@SuppressWarnings("unused")
	private 	float 	minS	=	10.0F / 255.0F;
		
	@SuppressWarnings("unused")
	private 	float 	hueHistogram[]	=	new float[360];
	@SuppressWarnings("unused")
	private		float	satHistogram[]	=	new float[256];
	
	private		ROI		roi;
	@SuppressWarnings("unused")
	private		float	hueThreshold;
	@SuppressWarnings("unused")
	private		float	satThreshold;
		
	public HueHistogramThresholdOpImage(RenderedImage source, ImageLayout layout,Integer hTh,Integer sTh,ROI ri)
    {
    	super(source, null, layout);                    	    	    	
    	this.roi			=	ri;
    	this.hueThreshold	=	(float)hTh.intValue();
    	this.satThreshold	=	(float)sTh.intValue();
    }

	@SuppressWarnings("unused")
	private void biModalObstacleDetector(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
	{
		Raster 	src 	= 	srcarr[0];
    	int 	w		=	src.getWidth();
    	int		h		=	src.getHeight();
    	
    	int 	lines[] = 	new int[h];
    	int 	cols[] 	= 	new int[w];
    	for(int i=0;i<h;i++)
    		lines[i] = 0;
    	for(int i=0;i<w;i++)
    		cols[i] = 0;
    	
    	
    	for(int i=0;i<w;i++)
    		for(int j=0;j<h;j++)
    			lines[j]++;
    	
    	for(int j=0;j<h;j++)
    		for(int i=0;i<w;i++)    		
    			cols[i]++;    
    	 	
	}

    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
    	//biModalObstacleDetector(srcarr, dst, destRect);
    	//if(1==1)
    	//	return;
    	
    	Raster 	src 	= 	srcarr[0];
    	
    	Histogram hueH 	= VisionUtils.getHueSatHistogram(src,roi);
    	Histogram fullH = VisionUtils.getHueSatHistogram(src,null);
    	
    	int 	w		=	src.getWidth();
    	int		h		=	src.getHeight();
    	
    	float	hsb[]	=	new float[3];
    	int 	r		= 	0;
    	int 	g		= 	0;
    	int 	b		= 	0;
    	
    	// GAUSS
    	//System.out.println(hueH.getStandardDeviation()[0]);
    	//hueH = hueH.getGaussianSmoothed(hueH.getStandardDeviation()[0]);
    	// MEDIAN
    	fullH 	= fullH.getSmoothed(true,20);	// median smoothing
    	hueH 	= hueH.getSmoothed(true,20);	// median smoothing
    	@SuppressWarnings("unused")
		double mean = hueH.getMean()[0];
    	
    	double t1	= hueH.getIterativeThreshold()[0];
    	double t2	= hueH.getMaxEntropyThreshold()[0];
    	double t3	= hueH.getMaxVarianceThreshold()[0];
    	double t4	= hueH.getMinErrorThreshold()[0];
    	double t5	= hueH.getMinFuzzinessThreshold()[0];    	
    	double t6	= hueH.getEntropy()[0];
    	double t7	= hueH.getMean()[0];
    	
    	double _t1	= fullH.getIterativeThreshold()[0];
    	double _t2	= fullH.getMaxEntropyThreshold()[0];
    	double _t3	= fullH.getMaxVarianceThreshold()[0];
    	double _t4	= fullH.getMinErrorThreshold()[0];
    	double _t5	= fullH.getMinFuzzinessThreshold()[0];    	
    	double _t6	= fullH.getEntropy()[0];
    	double _t7	= fullH.getMean()[0];
    	
    	System.out.println("---------------------");
    	System.out.println(t1);
    	System.out.println(t2);
    	System.out.println(t3);
    	System.out.println(t4);
    	System.out.println(t5);
    	System.out.println(t6);
    	System.out.println(t7);
    	System.out.println("========");
    	System.out.println(_t1);
    	System.out.println(_t2);
    	System.out.println(_t3);
    	System.out.println(_t4);
    	System.out.println(_t5);
    	System.out.println(_t6);
    	System.out.println(_t7);
    	
    	int[] hue 	= VisionUtils.getHistogramData(hueH,0);
        
        //HistogramDisplayer disp = new HistogramDisplayer("Obstacle Detection");
	    //disp.setData(hueH,new String[]{"Hue","Saturation"});
	    //disp.setLocation(350,400);
	    
	    //HistogramDisplayer dispF = new HistogramDisplayer("Full Img");
	    //dispF.setData(fullH,new String[]{"Hue","Saturation"});
	    //dispF.setLocation(0,400);
        
    	// have histogram values;
    	// filter image
    	for(int x = 0; x<w ; x++)
    	{
    		for(int y = 0; y<h ; y++)
	    	{
	    		// filtering
	    		r = src.getSample(x,y,0);
	    		g = src.getSample(x,y,1);
	    		b = src.getSample(x,y,2);
	    		Color.RGBtoHSB( r, g, b , hsb );
	    			    		
	    		
	    		if( (hue[(int)Math.floor(hsb[0]*359)] <= t6*3)	)	    		
	    		{
	    			// is OBSTACLE
	    			dst.setSample(x,y,0,WHITE);
	    			dst.setSample(x,y,1,WHITE);
	    			dst.setSample(x,y,2,WHITE);
	    		}
	    		else
	    		{
	    			// is GROUND
	    			dst.setSample(x,y,0,BLACK);
	    			dst.setSample(x,y,1,BLACK);
	    			dst.setSample(x,y,2,BLACK);
	    		}
	    	}
    	}
    	
    	/*
    	
    	int		width	=	src.getWidth();
    	int 	height	=	src.getHeight();
    	
    	int		roiWidth	=	(int)roi.getBounds().getWidth();
    	int		roiHeight	=	(int)roi.getBounds().getHeight();
    	int		roiX		=	(int)roi.getBounds().getX();
    	int		roiY		=	(int)roi.getBounds().getY();
    	
    	if(roiWidth==0)
    	{
    		roiWidth	=	width;
	    	roiHeight	=	height;
	    	roiX		=	0;
	    	roiY		=	0;
    	}
    	
    	float	hsb[]	=	new float[3];
    	int 	r		= 	0;
    	int 	g		= 	0;
    	int 	b		= 	0;
    	
    	// create histogram for image in HSB space    	
    	for(int x = roiX; x<roiX+roiWidth ; x++)
    	{
    		for(int y = roiY; y<roiY+roiHeight ; y++)
	    	{
	    		// filtering
	    		r = src.getSample(x,y,0);
	    		g = src.getSample(x,y,1);
	    		b = src.getSample(x,y,2);
	    		Color.RGBtoHSB( r, g, b , hsb );
	    		
	    		// valid histogram params here
	    		if(hsb[2] >= minB)
	    		{
	    			if(hsb[1] >=minS)
	    				this.hueHistogram[ (int)Math.floor(hsb[0]*359) ] ++;
	    			this.satHistogram[ (int)Math.floor(hsb[1]*255) ] ++;
	    		}
	    	}
    	}
    	     	
    	// apply low pass average filtering
    	float hTresh	 = 2.0F;
    	float sTresh	 = 2.0F;
    	
    	float hueAverage = 0.0F;    	
    	float satAverage = 0.0F;
    	int	  hCount	 = 0;
    	int	  sCount	 = 0;
    	
    	System.out.println("--------------");
    	for(int i=0;i<360;i++)
    	{
    		if(this.hueHistogram[i] >= hTresh)
    		{
    			System.out.println(i+" - "+hueHistogram[i]);
    			hueAverage += this.hueHistogram[i];
    			hCount ++;
    		}
    		
    		try
    		{
    			if(this.satHistogram[i] >= sTresh)
	    		{
	    			satAverage +=this.satHistogram[i];
	    			sCount ++;
	    		}    			
    		}
    		catch(ArrayIndexOutOfBoundsException e){}
    	}
    	hueAverage	/= (float)hCount;
    	satAverage	/= (float)sCount;
    	
    	// filter histogram
    	for(int i=5;i<40;i++)
    	{
    		//hueHistogram[i] = (hueHistogram[i] >= hueAverage) ? hueAverage : hueHistogram[i] ;
    		hueHistogram[i] = hueAverage ;
    		
    		try
    		{
    			//satHistogram[i] = (satHistogram[i] >= satAverage) ? satAverage : satHistogram[i] ;
    			satHistogram[i] = satAverage ;
    		}
    		catch(ArrayIndexOutOfBoundsException e){}
    	}
    	hueThreshold = hueAverage / 2.0F;
    	
    	// have histogram values;
    	// filter image
    	for(int x = 0; x<width ; x++)
    	{
    		for(int y = 0; y<height ; y++)
	    	{
	    		// filtering
	    		r = src.getSample(x,y,0);
	    		g = src.getSample(x,y,1);
	    		b = src.getSample(x,y,2);
	    		Color.RGBtoHSB( r, g, b , hsb );
	    			    		
	    		
	    		//if( (this.hueHistogram[(int)Math.floor(hsb[0]*359)] <= this.hueThreshold) ||
	    		//	(this.satHistogram[(int)Math.floor(hsb[1]*255)] <= this.satThreshold) )
	    		if( (this.hueHistogram[(int)Math.floor(hsb[0]*359)] <= this.hueThreshold) )
	    		{
	    			// is OBSTACLE
	    			dst.setSample(x,y,0,WHITE);
	    			dst.setSample(x,y,1,WHITE);
	    			dst.setSample(x,y,2,WHITE);
	    		}
	    		else
	    		{
	    			// is GROUND
	    			dst.setSample(x,y,0,BLACK);
	    			dst.setSample(x,y,1,BLACK);
	    			dst.setSample(x,y,2,BLACK);
	    		}
	    	}
    	}
    	*/
    	    	
    }   
	
}
