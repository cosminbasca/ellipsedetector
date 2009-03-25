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

public class QColorSegmentDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7640731151580189029L;


	private static final String[][] resources = { {"GlobalName", "QColorSegment"}, {"LocalName", "QColorSegment"}, {"Vendor",""}, {"Description","segmentare pe baza de culoare - spatiu cuantizat"}, {"DocURL",
        ""}, {"Version",""}, };


    private static final String[] supportedModes = { "rendered" };

    private static final int numSources = 1;

    private static final String[] paramNames = { "Quantization" };

    @SuppressWarnings("unchecked")
	private static final Class[] paramClasses = { Integer.class };

    private static final Object[] paramDefaults = { new Integer(10) };


    public QColorSegmentDescriptor()
    {
            super(resources, supportedModes, numSources , paramNames, paramClasses, paramDefaults, null);
    }


    public RenderedImage create(ParameterBlock pb, RenderingHints hints)
    {
            if (!validateParameters(pb))
            {
                    return null;
            }
            return new QColorSegmentOpImage(pb.getRenderedSource(0), new ImageLayout(),
            		(Integer) pb.getObjectParameter(0));
    }

    public boolean validateParameters(ParameterBlock pb)
    {
            return true;
    }
}
