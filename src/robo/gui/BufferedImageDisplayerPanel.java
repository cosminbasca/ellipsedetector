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
package robo.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BufferedImageDisplayerPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6800064884111245568L;
	private JLabel			img;
	
	public BufferedImageDisplayerPanel(BufferedImage bi)
	{
		this();
		this.img.setIcon( new ImageIcon(bi) );
	}
	
	public BufferedImageDisplayerPanel()
	{
		this.preInit();		
		this.buildGUI();	
		this.postInit();		
	}
	
	protected void preInit()
	{				
		this.img = new JLabel();
		this.img.setDoubleBuffered(true);
	}
	
	protected void postInit()
	{
		this.setPreferredSize(img.getSize());
	}
		
	protected void buildGUI()
	{		
		this.add("Center",img);
	}
	
	public void setImage(BufferedImage image)
	{
		BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics	=	(Graphics2D)buffer.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		this.img.setIcon(new ImageIcon(buffer) );		
		this.img.repaint();
		this.setPreferredSize(img.getSize());
	}		
	
	public void setImage(RenderedImage image)
	{
		this.setImage(new RenderedImageAdapter(image) );		
	}
	
	public void setImage(PlanarImage image)
	{
		this.setImage(image.getAsBufferedImage());
	}
	
	public void clearImage() 
	{
		this.img.setIcon(null);
	}
}
