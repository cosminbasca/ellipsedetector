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

public class CannyEdgeDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{


        /**
	 * 
	 */
	private static final long serialVersionUID = 4381286586337267191L;
		private static final String[][] resources = { {"GlobalName", "CannyEdge"}, {"LocalName", "CannyEdge"}, {"Vendor",""}, {"Description","Canny Edge detector."}, {"DocURL",""}, {"Version",
        "beta"}, };
        private static final String[] supportedModes = { "rendered" };

        private static final int numSources = 1;

        private static final String[] paramNames = { };

        @SuppressWarnings("unchecked")
		private static final Class[] paramClasses = { };

        private static final Object[] paramDefaults = { };

        public CannyEdgeDescriptor()
        {
                super(resources, supportedModes, numSources, paramNames, paramClasses, paramDefaults, null);
        }

        public RenderedImage create(ParameterBlock pb, RenderingHints hints)
        {
                if (!validateParameters(pb))
                {
                        return null;
                }
                return new CannyEdgeOpImage(pb.getSources(), null, new ImageLayout());
        }

        public boolean validateParameters(ParameterBlock pb)
        {
                return true;
        }

        /* Common Interface */

        //public String toString();
        //public boolean equals(Object obj);
        //protected Object clone() throws CloneNotSupportedException;
        //protected void finalize() throws Throwable;

}

