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
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.ImageLayout;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;


public class HueHistogramThresholdDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7971312983710758773L;


	private static final String[][] resources = { {"GlobalName", "HueHistogramThreshold"}, {"LocalName", "HueHistogramThreshold"}, {"Vendor",""}, {"Description","prag pe histograma hsb"}, {"DocURL",
        ""}, {"Version",""}, };


    private static final String[] supportedModes = { "rendered" };

    private static final int numSources = 1;

    private static final String[] paramNames = { "Prag Hue" , "Prag Intensitate" , "Roi" };

    @SuppressWarnings("unchecked")
	private static final Class[] paramClasses = { Integer.class , Integer.class, ROI.class };

    private static final Object[] paramDefaults = { new Integer(60),new Integer(80), new ROIShape(new Rectangle() ) };


    public HueHistogramThresholdDescriptor()
    {
            super(resources, supportedModes, numSources , paramNames, paramClasses, paramDefaults, null);
    }


    public RenderedImage create(ParameterBlock pb, RenderingHints hints)
    {
            if (!validateParameters(pb))
            {
                    return null;
            }
            return new HueHistogramThresholdOpImage(pb.getRenderedSource(0), new ImageLayout(),
            		(Integer) pb.getObjectParameter(0),
            		(Integer) pb.getObjectParameter(1),
            		(ROI) pb.getObjectParameter(2)
            		);
    }

    public boolean validateParameters(ParameterBlock pb)
    {
            return true;
    }
}
