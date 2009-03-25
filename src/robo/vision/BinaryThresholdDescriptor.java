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

public class BinaryThresholdDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{

        /**
	 * 
	 */
	private static final long serialVersionUID = -2157909397780925498L;

		private static final String[][] resources = { {"GlobalName", "BinaryThreshold"}, {"LocalName", "BinaryThreshold"}, {"Vendor",""}, {"Description","Aplica Prag => Imagine Binara"}, {"DocURL",
        ""}, {"Version",""}, };

        private static final String[] supportedModes = { "rendered" };

        private static final int numSources = 1;

        private static final String[] paramNames = { "Low", "High" };

        @SuppressWarnings("unchecked")
		private static final Class[] paramClasses = { double[].class , double[].class };

        private static final Object[] paramDefaults = { new double[]{200,200,200 }, 
        												new double[]{255,255,255 } };

        public BinaryThresholdDescriptor()
        {
                super(resources, supportedModes, numSources, paramNames, paramClasses, paramDefaults, null);
        }


        public RenderedImage create(ParameterBlock pb, RenderingHints hints)
        {
                if (!validateParameters(pb))
                {
                        return null;
                }
                return new BinaryThresholdOpImage(pb.getRenderedSource(0), new ImageLayout(), 
                		(double[]) pb.getObjectParameter(0), 
                		(double[]) pb.getObjectParameter(1)
                	);
        }

        public boolean validateParameters(ParameterBlock pb)
        {
                return true;
        }
}
