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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;

/** Find ellipses in an image with a Hough transform.
 */

@SuppressWarnings("unchecked")
class HoughEllipseOpImage  extends UntiledOpImage
{	
	private double  minA;	// major axis
	private double  maxA;
	
	private double  minB;	// minor axis
	private double  maxB;
	
	private boolean debug;
	
	private float maxPairs;
	
	private int 	MIN_VOTES_TO_DETECT_ELLIPSE;
	private int 	IDLE_STOP_SEARCH;
	
	private int[]	accumulator;
	
	private List<EllipseDescriptor> 	ellipses;
	//private int		quantizationBlockWidth;
	//private int		quantizationBlockHeigth;
	
	public HoughEllipseOpImage(RenderedImage source, ImageLayout layout, Integer minMajorAxis, Integer maxMajorAxis , Integer minMinorAxis ,Integer maxMinorAxis,Integer minVotes,Integer idleStop, Float maxPairs, Boolean debug)
    {
    	super(source, null, layout);                
        this.minA = (double)minMajorAxis.intValue();
        this.minB = (double)minMinorAxis.intValue();
        this.maxA = (double)maxMajorAxis.intValue();
        this.maxB = (double)maxMinorAxis.intValue();
        this.maxPairs = maxPairs;
        this.debug = debug;
        this.MIN_VOTES_TO_DETECT_ELLIPSE = minVotes.intValue();
        this.IDLE_STOP_SEARCH = idleStop.intValue();
        this.ellipses 	= new ArrayList<EllipseDescriptor>();
    }


    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
    	Raster 			src  = srcarr[0];
    	// get edge points
    	double edge[][];
    	ArrayList<Point> _edge  = new ArrayList<Point>();
    	int width  		= src.getWidth();
    	int heigth 		= src.getHeight();
    	    	
    	for(int i=0;i<width;i++)
    	{
    		for(int j=0;j<heigth;j++)
    		{
    			int sample = src.getSample(i,j,0);
    			if(sample==RoboRaster.WHITE)
    			//if(sample>=100)
    				_edge.add(new Point(i,j));
    		}
    	}
    	
    	if(_edge.isEmpty())
    		return;
    	
    	int edgePoints = _edge.size();   
    	edge = new double[_edge.size()][3];
    	for(int i=0;i<_edge.size();i++)
    	{
    		edge[i][0] = ((Point)_edge.get(i)).x;
    		edge[i][1] = ((Point)_edge.get(i)).y;
    		edge[i][2] = 1;									// punct activ
    	}
    	
    	if(maxB > maxA)
    		maxB = maxA;
    	
        // RHT	-	Randomized Hough Transform
        // max nr of pairs about 50 % of img        
    	if(this.debug) {
    		System.out.println("Max Points	: "+this.maxPairs+" %");
    	}
        int maxPairs 		= (int)((double)edgePoints * this.maxPairs);// - (int)((double)edgePoints * 0.15);
        //if(edgePoints > 3000)
        //	maxPairs = 1300;
        	
        int currentPair 	= 0;
        
        int accLength		= (int)(this.maxA - this.minB);
     	accumulator 		= new int[accLength];
     	clear(accumulator);
     	
     	if(this.debug) {
     		System.out.println("Searching   	: "+maxPairs);
     		System.out.println("Total 		: "+edgePoints);
     		System.out.println("Acc len   	: "+accLength);
     	}
        // for speed - // fara prea multe alocari de mem
        int 	p1 		= 0;
        int 	p2 		= 0;
        double 	dP1P2 	= 0.0;
        double 	cx		= 0.0;
        double 	cy		= 0.0;
        double 	a		= 0.0;
		double 	d		= 0.0;        
		double 	f		= 0.0;        
		double 	cos		= 0.0;        
		double 	b		= 0.0;   
		int    	bIDX	= 0; 
		int 	maxData[];
		int 	maxAccElement 		= 0;
		int 	houghMinorLength 	= 0;
		
        //******************************************************************************
        int idleStopCounter = 0;
        while(currentPair <= maxPairs)
        {        
        	if(idleStopCounter++ >= this.IDLE_STOP_SEARCH)
        		break;
        		
        	p1 = (int)Math.round(Math.random()*(double)(edgePoints-1));
        	while(edge[p1][2]==0)	// has been removed
        		p1 = (int)Math.round(Math.random()*(double)(edgePoints-1));
        		
        	p2 = p1;        	
        	while(p2==p1 || edge[p2][2]==0)
        		p2 = (int)Math.round(Math.random()*(double)(edgePoints-1));
        	
        	// am punctele
        	dP1P2 	= Point2D.distance(edge[p1][0],edge[p1][1],edge[p2][0],edge[p2][1]);
        	
        	if( dP1P2 >= 2*minA && dP1P2 <= 2*maxA )
			{        	
				currentPair++;	
        		dst.setSample((int)edge[p1][0],(int)edge[p1][1],0,RoboRaster.WHITE);
        		dst.setSample((int)edge[p2][0],(int)edge[p2][1],0,RoboRaster.WHITE);
        	
        		cx 			= (edge[p1][0] + edge[p2][0]) / 2.0;
        		cy 			= (edge[p1][1] + edge[p2][1]) / 2.0;
        		a			= dP1P2 / 2.0;
        		
        		for(int p3 =0 ;p3<edgePoints ; p3++)
				{
					if(p3==p1 || p3==p2 || edge[p3][2]==0)
						continue;					
					d 		= Point2D.distance(edge[p3][0],edge[p3][1],cx,cy);					
					
					if( d >= this.minB && d <= a )
					{
						f = Point2D.distance(edge[p3][0],edge[p3][1],edge[p2][0],edge[p2][1]);
						cos = (a*a + d*d - f*f) / (2*a*d);
						b   = Math.sqrt( (a*a*d*d*(1-cos*cos)) / (a*a - d*d*cos*cos));
						
						if( b > maxB )
							continue;
								
						bIDX   = (int)Math.round(b - minB);
						
						if(bIDX < 0 || bIDX >= accumulator.length)
							continue;
						accumulator[bIDX]++;
						
						//dst.setSample((int)edge[p3][0],(int)edge[p3][1],2,bIDX*16+50);
						//dst.setSample((int)edge[p3][0],(int)edge[p3][1],1,bIDX*8+50);
						//dst.setSample((int)edge[p3][0],(int)edge[p3][1],0,bIDX*2+50);
					}
											
	
				}
				
				//dst.setSample((int)edge[p1][0],(int)edge[p1][1],0,RoboRaster.WHITE);
        		//dst.setSample((int)edge[p2][0],(int)edge[p2][1],0,RoboRaster.WHITE);
        		
        		// print acc       
        		//for(int i=0;i<accumulator.length;i++) 		
        		//	System.out.println(" ["+(i+(int)minB)+"] = "+accumulator[i]);
        		
				//if(1==1)
				//	break;
				
				// find max in acc array
				maxData 			= this.max(accumulator);
				maxAccElement 		= maxData[1];
				houghMinorLength 	= maxData[0] + (int)minB;
				// am elipsa posibila cu val = raza mica
				
				if( maxAccElement >= this.MIN_VOTES_TO_DETECT_ELLIPSE )
				{
					// output ellipse
					//System.out.println(maxAccElement);
					
					EllipseDescriptor ellipse = new EllipseDescriptor(
						new Point2D.Double(edge[p1][0],edge[p1][1]),	// vertex 1
						new Point2D.Double(edge[p2][0],edge[p2][1]),	// vertex 2
						new Point2D.Double(cx,cy),						// center
						(int)a,											// major axis
						houghMinorLength);								// minor axis
										
					ellipses.add(ellipse);
					
					//System.out.println(maxAccElement);
					
					//drawPixel(0,(int)cx,(int)cy,dst,5);
					//drawPixel(1,(int)cx,(int)cy,dst,5);
					
					//drawPixel(1,(int)edge[p1][0],(int)edge[p1][1],dst,5);
					//drawPixel(1,(int)edge[p2][0],(int)edge[p2][1],dst,5);
					
					// rem pixels of ellipse from edge array
					//removeEllipse(edge,ellipse);
					
					idleStopCounter = 0;
				}
				clear(accumulator);
        	}
        	
        }
        //******************************************************************************
        
        // get Clustered Ellipses
        if(this.debug) {
        	System.out.println("Total Ellipses	: "+this.ellipses.size()+" ");
        }
		/*
		for(int i=0;i<this.ellipses.size();i++)
		{
			EllipseDescriptor desc = ellipses.elementAt(i);
			System.out.println(desc.toString());
		}
		*/				
		
		List<EllipseDescriptor> centroids = EllipseDescriptor.getCentroidalEllipses(ellipses);
        ellipses.clear();
        ellipses.addAll(centroids);  
        
        this.setProperty(EllipseDescriptor.DETECTED_ELLIPSES,ellipses);
        //for(int i=0;i<centroids.size();i++)
        //	ellipses.addElement(centroids.elementAt(i));
        
    	// draw ellipse center
        if(this.debug) {
        	System.out.println("Ellipses	: "+this.ellipses.size()+" ellipses");
        }
		for(int i=0;i<this.ellipses.size();i++)
		{
			EllipseDescriptor desc = ellipses.get(i);
			drawPixel(0,(int)desc.getCenter().getX(),(int)desc.getCenter().getY(),dst,5);
			drawPixel(1,(int)desc.getCenter().getX(),(int)desc.getCenter().getY(),dst,5);
			drawPixel(2,(int)desc.getCenter().getX(),(int)desc.getCenter().getY(),dst,5);
			
			drawPixel(1,(int)desc.getVertex1().getX(),(int)desc.getVertex1().getY(),dst,5);
			drawPixel(1,(int)desc.getVertex2().getX(),(int)desc.getVertex2().getY(),dst,5);
			if(this.debug) {
				System.out.println(desc.toString());
			}
		}
	}
   
   	private void drawPixel(int band,int x,int y,WritableRaster dst,int w)
   	{
   		int _x =  x-w/2;
   		int _y =  y-w/2;
   		
		for(int i=0;i<w;i++)
   			for(int j=0;j<w;j++)   			
   				dst.setSample(_x+i,_y+j,band,RoboRaster.WHITE);   		
   	}
   
   	@SuppressWarnings("unused")
	private void removeEllipse(double edge[][],EllipseDescriptor ellipse)
   	{
   		double a = ellipse.getHalfMajorAxis();
   		double b = ellipse.getHalfMinorAxis();
   		
   		double cx= ellipse.getCenter().getX();
   		double cy= ellipse.getCenter().getY();
   		
   		//double x1= ellipse.getVertex1().getX();
   		//double y1= ellipse.getVertex1().getY();
   		
   		double x2= ellipse.getVertex2().getX();
   		double y2= ellipse.getVertex2().getY();
   		
   		for(int p3=0; p3<edge.length; p3++)
   		{
   			if(edge[p3][2]==0)
   				continue;
   			
   			double d 		= Point2D.distance(edge[p3][0],edge[p3][1],cx,cy);
   			double f 		= Point2D.distance(edge[p3][0],edge[p3][1],x2,y2);
			double cos 		= (a*a + d*d - f*f) / (2*a*d);
			double calcB 	= (int)Math.round( Math.sqrt( (a*a*d*d*(1-cos*cos)) / (a*a - d*d*cos*cos)) );
			
			if(calcB == b) // remove
				edge[p3][2] = 0;
   		}
   	} 
   
    private int[] max(int elements[])
    {
    	int max = 0;
    	int idx = -1;
    	for(int i=0;i<elements.length;i++)
    		if( elements[i] > max )
    		{
    			max = elements[i];
    			idx = i;
    		}
    	return new int[]{idx,max};
    }
    
    private void clear(int elements[])
    {
    	for(int i=0;i<elements.length;i++)
    		elements[i] = 0;
    }
}
