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

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;
import javax.media.jai.OperationDescriptorImpl;

class HoughEllipseDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2422060000393070997L;


	private static final String[][] resources = { {"GlobalName", "HoughEllipses"}, {"LocalName", "HoughEllipses"}, {"Vendor",""}, {"Description","Gaseste elipse folosind transformata hough"}, {"DocURL",
        ""}, {"Version",""}, };


    private static final String[] supportedModes = { "rendered" };

    private static final int numSources = 1;

    private static final String[] paramNames = { "MinMajorAxis", "MaxMajorAxis" ,"MinMinorAxis", "MaxMinorAxis" , "MinVotes" , "IdleStopSearch" };

    @SuppressWarnings("unchecked")
	private static final Class[] paramClasses = { java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class ,java.lang.Integer.class };

    private static final Object[] paramDefaults = { new Integer(100), new Integer(130), new Integer(50), new Integer(60), new Integer(180), new Integer(1000)};


    public HoughEllipseDescriptor()
    {
            super(resources, supportedModes, numSources , paramNames, paramClasses, paramDefaults, null);
    }


    public RenderedImage create(ParameterBlock pb, RenderingHints hints)
    {
            if (!validateParameters(pb))
            {
                    return null;
            }
            return new HoughEllipseOpImage(pb.getRenderedSource(0), new ImageLayout(), 
            			(Integer) pb.getObjectParameter(0), 
            			(Integer) pb.getObjectParameter(1), 
            			(Integer) pb.getObjectParameter(2), 
            			(Integer) pb.getObjectParameter(3), 
            			(Integer) pb.getObjectParameter(4), 
            			(Integer) pb.getObjectParameter(5)
            			);
    }

    public boolean validateParameters(ParameterBlock pb)
    {
            return true;
    }
}
