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


public class LineDescriptor 
{
	private int theta;
	private int r;
	
	public LineDescriptor(int t,int r)
	{
		this.theta 	= t;
		this.r		= r;
	}

	
	public void setTheta(int theta) 
	{
		this.theta = theta; 
	}

	public void setLength(int r) 
	{
		this.r = r; 
	}

	public int getTheta() 
	{
		return (this.theta); 
	}

	public int getLength() 
	{
		return (this.r); 
	}
	
	
}
