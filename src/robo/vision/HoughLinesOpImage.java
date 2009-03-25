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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;

/** Find lines in an image with a Hough transform.
 */
@SuppressWarnings("unchecked")
public class HoughLinesOpImage extends UntiledOpImage
{

        @SuppressWarnings("unused")
		private int houghThreshold = 5, magnitudeThreshold = 5, greyOut = 255;

		private int accumulator[][];
		private Vector<LineDescriptor> lines;

        public HoughLinesOpImage(RenderedImage source, ImageLayout layout, Integer edgeThreshold, Integer maximaThreshold, Integer outputIntensity)
        {
                super(source, null, layout);
                this.magnitudeThreshold = edgeThreshold.intValue();
                this.houghThreshold = maximaThreshold.intValue();
                this.greyOut = outputIntensity.intValue();
        }


        protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
        {
        	@SuppressWarnings("unused")
			int IDLE_STOP_SEARCH = 2000;
        	//*************************************
        	Raster src = srcarr[0];
        	
        	System.out.println(DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));    	
	    	// get edge points
	    	double edge[][];
	    	Vector<Point> _edge  = new Vector<Point>();
	    	int width  		= src.getWidth();
	    	int heigth 		= src.getHeight();
	    	    	
	    	for(int i=0;i<width;i++)
	    	{
	    		for(int j=0;j<heigth;j++)
	    		{
	    			int sample = src.getSample(i,j,0);
	    			if(sample==RoboRaster.WHITE)
	    			//if(sample>=100)
	    				_edge.addElement(new Point(i,j));
	    		}
	    	}
	    	
	    	int edgePoints = _edge.size();    	
	    	edge = new double[_edge.size()][3];
	    	for(int i=0;i<_edge.size();i++)
	    	{
	    		edge[i][0] = ((Point)_edge.elementAt(i)).x;
	    		edge[i][1] = ((Point)_edge.elementAt(i)).y;
	    		edge[i][2] = 1;									// punct activ
	    	}
	    	
	    	// RHT	-	Randomized Hough Transform
	        // max nr of pairs about 50 % of img        
	        int maxPairs 		= (int)((double)edgePoints * 0.5) - (int)((double)edgePoints * 0.15);
	        if(edgePoints > 3000)
	        	maxPairs = 1300;
	        	
	        @SuppressWarnings("unused")
			int currentPair 	= 0;
	        
	        // cuantizare spatiu hough
	        int houghWidth	=	500;
	        int houghHeigth = 	(int)(Math.sqrt(2)*(double)Math.max(width,heigth))*2 ;
	        int thetaStep	=	(int)(Math.PI / (double)houghWidth);	        
	        accumulator 		= new int[houghWidth][houghHeigth];
	     	clear(accumulator);
	     	
	     	System.out.println("Pairs     : "+maxPairs);
	        System.out.println("Searching : "+edgePoints);
	        System.out.println("Acc len   : ["+houghWidth+","+houghHeigth+"]");
	        
	        @SuppressWarnings("unused")
			int 	idleStopCounter = 0;
	        int 	p1 		= 0;
        	@SuppressWarnings("unused")
			int 	p2 		= 0;
        	int		r		= 0;
        	int 	minR	= 40;
        	int 	maxR	= 200;
        	@SuppressWarnings("unused")
			double  dP1P2	= 0.0;
        	int		validLineThreshold = 100;
        	
        	// detect lines        
        	for(p1 = 0; p1<edgePoints ;p1++)
        	{        
        		for(int th = 0; th<houghWidth ;th++)
        		{
        			r = (int)Math.round(edge[p1][0] * Math.cos(thetaStep * th) + edge[p1][1] * Math.sin(thetaStep * th));
        			if( r > maxR || r < minR )
        				continue;
        			accumulator[th][r] ++;
        		}        		
        	}
        	// treshold lines
        	
        	for(int th = 0; th<houghWidth ;th++)
        	{
        		for(r = 0; r<houghHeigth ;r++)
        		{
        			if(accumulator[th][r] >= validLineThreshold)
        			{
        				// salvez info despre linie
        				LineDescriptor ld = new LineDescriptor(th,r);
        				lines.addElement(ld);
        			}
        		}
        	}
        	
        	// find intersection point of all lines
        	// and draw lines to int points
        }
        
        
        private void clear(int acc[][])
        {
        	for(int i=0;i<acc.length;i++)
        		for(int j=0;j<acc[i].length;j++)
        			acc[i][j] = 0;
        }

}

/*
 // old implementation
 
				Raster src = srcarr[0];
                RoboRaster source = new RoboRaster(src);
                WritableRoboRaster dest = new WritableRoboRaster(dst);
                int width = source.getWidth(), height = source.getHeight();
                int length = width * height;

                int gradient;
                double direction;
                double maxR = 1.5 * width, minR = -1.5 * width;
                double maxTheta = Math.PI / 2., minTheta = -1.*Math.PI / 2.;
                int quant = 360;
                double rangeTheta = maxTheta - minTheta, rangeR = maxR - minR;
                double incTheta = rangeTheta / quant, incR = rangeR / quant;
                int accum[] = new int[quant * quant];
                int thresh[] = new int[quant * quant];

                dest.setRect(getSourceImage(0).getData());
                for (int v = 1; v < height - 1; v++)
                {
                        for (int u = 1; u < width - 1; u++)
                        {
                                double sy = (source.grey(u - 1, v) * -2 + source.grey(u + 1, v) * 2 + source.grey(u - 1, v - 1) * -1 + source.grey(u + 1, v - 1) + source.grey(u - 1, v + 1) * -1 + source.grey(u + 1, v - 1)) / 8.0;
                                double sx = (source.grey(u - 1, v - 1) + source.grey(u, v - 1) * 2 + source.grey(u + 1, v - 1) + source.grey(u - 1, v + 1) * -1 + source.grey(u, v + 1) * -2 + source.grey(u + 1, v - 1) * -1) / 8.0;

                                gradient = (int) Math.sqrt(sx * sx + sy * sy);

                                if (sy == 0)
                                {
                                        if (sx == 0)
                                                direction = 0.;
                                        else
                                                direction = (sx > 0) ? 0.5 * Math.PI : -0.5 * Math.PI;
                                }
                                else if (sx == 0)
                                {
                                        direction = 0.0;
                                }
                                else
                                {
                                        direction = Math.atan(sy / sx);
                                }
                                direction += Math.PI / 2.0;
                                //force direction to go from PI/2 to -PI/2
                                if (direction > Math.PI / 2.0)
                                        direction -= Math.PI;
                                if (direction < Math.PI / -2.0)
                                        direction += Math.PI;

                                //if the mag is bigger than threshold, increment accumulator array
                                if (gradient > magnitudeThreshold)
                                {
                                        double theta = direction;
                                        double r = u * Math.cos(theta) + v * Math.sin(theta);
                                        try
                                        {
                                                accum[(int)(quant * (int)((r + maxR) / incR) + //rounding is good!
                                                            (theta + maxTheta) / incTheta)]++;
                                        }
                                        catch (ArrayIndexOutOfBoundsException e)
                                        {
                                                //System.out.println("r= " + r + ", theta= " + theta);
                                        }
                                }
                        }
                }

                //threshold the hough space; find the local maxima
                for (int x = 0; x < quant * quant; x++)
                {
                        if (x % quant > 1 && x % quant < quant - 1 && x > quant && x < quant * quant - quant && accum[x] >= houghThreshold && accum[x] > accum[x + 1] && accum[x] > accum[x - 1] && accum[x] > accum[x + quant] && accum[x] > accum[x - quant] &&
                                accum[x] > accum[x + quant + 1] && accum[x] > accum[x - quant + 1] && accum[x] > accum[x + quant - 1] && accum[x] > accum[x - quant - 1])
                        {
                                thresh[x] = 255;
                        }
                }

                for (int x = 0; x < quant * quant; x++)
                {
                        if (thresh[x] != 0)
                        {
                                for (int x0 = 0; x0 < width; x0++)
                                {
                                        double r = minR + incR * (int)(x / quant);
                                        double theta = minTheta + incTheta * (x % quant);
                                        int y0 = -1 * (int)((x0 * Math.cos(theta) - r) / Math.sin(theta));
                                        if (y0 * width + x0 >= 0 && y0 * width + x0 < length)
                                        {
                                                dest.setGrey(x0, y0, greyOut);
                                        }
                                }
                        }
                }
*/