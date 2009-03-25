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
public class StentifordThinningOpImage extends UntiledOpImage
{
	private int width;
	@SuppressWarnings("unused")
	private int heigth;
	private final int WHITE = 255;
	@SuppressWarnings("unused")
	private static final int BLACK = 0;
	
	public StentifordThinningOpImage(RenderedImage source, ImageLayout layout)
    {
        super(source, null, layout);
    }

    protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
    {
        Raster src 	= srcarr[0];
        this.width 	= src.getWidth();
        this.height = src.getHeight();
        this.thinning(src,dst);
    }    
    
    // Return the k indexed neighbour of a given pixel (x, y).
	// The k indexed neighbour table is given as below:
	// 4 3 2
	// 5 0 1
	// 6 7 8
	private int neighbour(int x, int y, int k,Raster src)
	{
		assert (x>=0 && x<width && y>=0 && y<height && k>=0 && k<=8): "x ="+x+" y ="+y+" k ="+k; 
		
		switch(k)
		{
		case 0:
			break;
		case 1:
			x++;
			break;
                case 2:
			x++;
			y--;
                        break;
                case 3:
			y--;
                        break;
                case 4:
			x--;
			y--;
                        break;
                case 5:
			x--;
                        break;
                case 6:
			x--;
			y++;
                        break;
                case 7:
			y++;
                        break;
                case 8:
			x++;
			y++;
                        break;

		}
		if(x<0) x = 0;
		else if(x>=width) x = width-1;

		if(y<0) y = 0;
		else if(y>=height) y = height - 1;

		//return src.getSample(x, y, 0);
		return this.get(src,x,y,0);
	}

	private int connectivity(int x, int y,Raster src)
	{
 		assert (x>=0 && x<width && y>=0 && y<height): "x ="+x+" y ="+y;

		// As in paper http://www.eng.fiu.edu/me/robotics/elib/am_st_fiu_ppr_2000.pdf, 
		// 0 is object value (foreground) and 1 is background value. This is opposite
		// from the binarize operation in JAI where equation
		// dst(x, y) = src(x, y) >= threshold ? 1 : 0;
		// is used. Hence, we will just invert the value to fit JAI.
		int p1 = neighbour(x, y, 1,src)==1?0:1;
		int p2 = neighbour(x, y, 2,src)==1?0:1;
		int p3 = neighbour(x, y, 3,src)==1?0:1;
		int p4 = neighbour(x, y, 4,src)==1?0:1;
		int p5 = neighbour(x, y, 5,src)==1?0:1;
		int p6 = neighbour(x, y, 6,src)==1?0:1;
		int p7 = neighbour(x, y, 7,src)==1?0:1;
		int p8 = neighbour(x, y, 8,src)==1?0:1;

		return p1*(1-p2*p3) + p3*(1-p4*p5) + p5*(1-p6*p7) + p7*(1-p8*p1);
	}
	
	private boolean endPoint(int x, int y,Raster src)
	{
		assert (x>=0 && x<width && y>=0 && y<height): "x ="+x+" y ="+y;

		//int pixel = src.getSample(x, y, 0);
		int pixel = this.get(src,x,y,0);
		int count = 0;	

		if(pixel == neighbour(x, y, 1,src)) count++;
        if(pixel == neighbour(x, y, 2,src)) count++;
        if(pixel == neighbour(x, y, 3,src)) count++;
        if(pixel == neighbour(x, y, 4,src)) count++;
        if(pixel == neighbour(x, y, 5,src)) count++;
        if(pixel == neighbour(x, y, 6,src)) count++;
        if(pixel == neighbour(x, y, 7,src)) count++;
        if(pixel == neighbour(x, y, 8,src)) count++;
	
		return count == 1;
	}    
	
	private void thinning(Raster src,WritableRaster dst)
	{
		int[] foregroundNeighbour = {7,1,3,5};
		int[] backgroundNeighbour = {3,5,7,1};		
		boolean[][] m = new boolean[width][height];
		
		boolean remain = true;

		for(int i=0; i<m.length; i++)
		{
			for(int j=0; j<m[i].length; j++)
			{
				m[i][j] = false; 	// uneraseable
			}
		}

		while (remain)
		{
			remain = false;
			for(int k=0; k<foregroundNeighbour.length; k++)
			{
				// We are not interest at the image boundary.
				for(int y=1; y<height-1; y++)
				{
					for(int x=1; x<width-1; x++)
					{
						//int p = src.getSample(x, y, 0);
						int p = get(src,x,y,0);
	
						//if(p == COLOR_FOREGROUND)
						if(p == 1)
						{
							int p1 = neighbour(x, y, foregroundNeighbour[k],src);
							int p2 = neighbour(x, y, backgroundNeighbour[k],src);			
						
							// Pattern matched.
							//if(p1 == COLOR_FOREGROUND && p2 == COLOR_BACKGROUND)
							if(p1 == 1 && p2 == 0)
							{
								if(!endPoint(x, y,src) && connectivity(x, y,src) == 1)
								{
									// Mark for deletion.
									m[x][y] = true; // eraseable
									remain = true;
								}
							}
						}
					}// for(int x=1; x<width-1; x++)
				}// for(int y=1; y<height-1; y++)
			
                // We are not interest at the image boundary.
                for(int y=1; y<height-1; y++)
                {
                  	for(int x=1; x<width-1; x++)
                    {
						if(m[x][y]) // is eraseable
						{
							//System.out.println("DELETE POINT AT "+x+" "+y);
							//src.setSample(x, y, 0, COLOR_BACKGROUND);
							this.setBlack(dst,x,y);
							m[x][y] = false;
						}
						else
						{
							this.setWhite(dst,x,y);
						}
					}
				}
			}// for(int k=0; k<foregroundNeighbour.length; k++)
		}// while (remain)
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
    
    private int get(Raster src,int x,int y,int b)
    {
    	return (src.getSample(x,y,b) == this.WHITE ) ? 1 : 0;
    }
    
}
