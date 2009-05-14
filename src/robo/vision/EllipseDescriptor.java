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

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

public class EllipseDescriptor 
{
	public static final	String	DETECTED_ELLIPSES	=	"DETECTED ELLIPSES";
	private double	ELLIPSE_SIMILARITY_DISTANCE	=	20.0;
	
	private Point2D	center;
	private Point2D	v1;
	private Point2D	v2;
	private int		a;
	private int 	b;
	//private double	alfa;
	
	public EllipseDescriptor()
	{
	}
	
	public EllipseDescriptor(Point2D v1,Point2D v2,Point2D c,int a,int b)
	{
		this.v1		= new Point2D.Double(v1.getX(),v1.getY());
		this.v2		= new Point2D.Double(v2.getX(),v2.getY());
		this.center = new Point2D.Double(c.getX(),c.getY());
		this.a		= a;
		this.b		= b;
	}
	
	public void setHalfMinorAxis(int b)
	{
		this.b = b;
	}
	
	public int getHalfMinorAxis()
	{
		return this.b;
	}
	
	public void setHalfMajorAxis(int a)
	{
		this.a = a;
	}
	
	public int getHalfMajorAxis()
	{
		return this.a;
	}
	
	public void setCenter(Point2D c)
	{
		this.center = new Point2D.Double(c.getX(),c.getY());
	}
	
	public void setVertex1(Point2D v)
	{
		this.v1 = new Point2D.Double(v.getX(),v.getY());
	}
	
	public void setVertex2(Point2D v)
	{
		this.v2 = new Point2D.Double(v.getX(),v.getY());
	}
	
	public Point2D getCenter()
	{
		return this.center;
	}
	
	public Point2D getVertex1()
	{
		return this.v1;
	}
	
	public Point2D getVertex2()
	{
		return this.v2;
	}
	
	// in radiani
	public double getAlfa()
	{
		double alfa = 0.0;
		
		if(v1.getX() == v2.getX())
			alfa = Math.PI / 2.0;	// vertical - 90
		else
			alfa = Math.tan(( v1.getY() - v2.getY() ) / ( v1.getX() - v2.getX() ));
		
		return alfa;
	}
	
	public double distance(EllipseDescriptor ed)
	{
		double dCen 	= this.center.distance(ed.getCenter());
		double dAlfa	= Math.sqrt( ( this.getAlfa()-ed.getAlfa() )*( this.getAlfa()-ed.getAlfa() ) );
		
		return ( 	Math.sqrt( (a-ed.getHalfMajorAxis())*(a-ed.getHalfMajorAxis()) )+
			   		Math.sqrt( (b-ed.getHalfMinorAxis())*(b-ed.getHalfMinorAxis()) )+
					dCen + 
					dAlfa	) / 4.0;
	}
	
	public static List<EllipseDescriptor> getCentroidalEllipses(List<EllipseDescriptor> allEllipses)
	{
		// build clusters;
		Vector<Vector<EllipseDescriptor>> clusters 		= 	new Vector<Vector<EllipseDescriptor>>();
		Vector<EllipseDescriptor>		  tmpCluster	=	null;		
		
		for(int i=0;i<allEllipses.size();i++)
		{
			EllipseDescriptor clusterRep = allEllipses.get(i);
			
			tmpCluster = new Vector<EllipseDescriptor>();
			
			for(int j=0;j<allEllipses.size();j++)
			{	
				if(clusterRep.equals(allEllipses.get(j)))	// have one
				{
					tmpCluster.addElement(allEllipses.get(j));
					allEllipses.remove(j--);
					clusterRep = EllipseDescriptor.getCentroid(tmpCluster);
				}								
			}
			if(tmpCluster.size() > 1)
			{
				clusters.addElement(tmpCluster);	
				i = 0;
			}
		}
		
		// get Centroids and return them
		Vector<EllipseDescriptor>		  centroids		=	new Vector<EllipseDescriptor>();
		for(int i=0;i<clusters.size();i++)
			centroids.addElement(EllipseDescriptor.getCentroid(clusters.elementAt(i)));
		
		return centroids;
	}
	
	public static EllipseDescriptor getCentroid(Vector<EllipseDescriptor> ellipses)
	{
		// find the mean x and mean y of centers
		double cx[] = 	new double[ellipses.size()];
		double cy[] = 	new double[ellipses.size()];
		for(int i=0;i<ellipses.size();i++)
		{
			cx[i] =  ((EllipseDescriptor)ellipses.elementAt(i)).center.getX();
			cy[i] =  ((EllipseDescriptor)ellipses.elementAt(i)).center.getY();
		}
		double meanX	=	robo.util.Util.getHarmonicMean(cx);
		double meanY	=	robo.util.Util.getHarmonicMean(cy);
		
		int centroid 	= 	-1;
		double	dist	= 	Double.MAX_VALUE;
		// find the closest to the center
		for(int i=0;i<ellipses.size();i++)
			if( Point2D.distance(cx[i],cy[i],meanX,meanY) < dist )
			{
				dist 		= 	Point2D.distance(cx[i],cy[i],meanX,meanY);
				centroid	=	i;
			}
			
		if(centroid != -1)
			return ellipses.elementAt(centroid);
		return null;
	}
	
	public boolean equals(Object o)
	{
		boolean equal = false;
		if(o instanceof EllipseDescriptor)
		{
			//System.out.println(((EllipseDescriptor)o).distance(this));
			if( ((EllipseDescriptor)o).distance(this) <= ELLIPSE_SIMILARITY_DISTANCE )
				equal = true;
		}
		
		return equal;
	}
	
	
	public double getPerimeter()
	{
		return Math.PI * ( (3*a + 3*b) - Math.sqrt( (a+3*b)*(b+3*a) ) );
	}
	
	public double getExcentricity()
	{
		return Math.sqrt(1 - (double)(b*b)/(double)(a*a) );
	}
	
	public String toString()
	{
		String str = "";
		str += "<"+"a: "	+ String.valueOf(a);
		str += " ,b: "		+ String.valueOf(b);
		str += " ,C: ("	+ center.getX()+","+center.getY()+")";
		str += " ,V1 : ("	+ v1.getX()+","+v1.getY()+")";
		str += " ,V2 : ("	+ v2.getX()+","+v2.getY()+")";
		str += " ,Alfa: "	+ getAlfa();
		str += " ,P: "	+ getPerimeter();
		str += " ,Ex: "	+ getExcentricity();
		str += " >";
		return str;	
	}
}
