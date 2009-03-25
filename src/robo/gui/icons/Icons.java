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
package robo.gui.icons;

import javax.swing.ImageIcon;

public class Icons {
	public static ImageIcon iconOpen;
	public static ImageIcon iconExit;
	public static ImageIcon iconDetect;
	public static ImageIcon iconAbout;
	
	static {
		Icons icns = new Icons(); 
		iconOpen = icns.createImageIcon("document-open.png", "Open Image");
		iconExit = icns.createImageIcon("system-log-out.png", "Exit Application");
		iconDetect = icns.createImageIcon("view-refresh.png", "Detect Ellipses");
		iconAbout = icns.createImageIcon("help-browser.png", "About RHED");
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
}
