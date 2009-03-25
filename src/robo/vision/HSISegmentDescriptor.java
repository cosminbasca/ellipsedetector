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


public class HSISegmentDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{

        /**
	 * 
	 */
	private static final long serialVersionUID = -2191836024272331439L;
		private static final String[][] resources = { {"GlobalName", "HSISegment"}, {"LocalName", "HSISegment"}, {"Vendor","edu.usf.csee"}, {"Description","HSISegment from an intensity image of a calibration object."}, {"DocURL",
        ""}, {"Version",""}, };
        private static final String[] supportedModes = { "rendered" };

        private static final int numSources = 1;

        private static final String[] paramNames = { "aBins","bBins" };

        @SuppressWarnings("unchecked")
		private static final Class[] paramClasses = { java.lang.Integer.class, java.lang.Integer.class };

        private static final Object[] paramDefaults = { new Integer(0), new Integer(0)};

        /** Constructs a HSISegmentDescriptor.
         */
        public HSISegmentDescriptor()
        {
                super(resources, supportedModes, numSources, paramNames, paramClasses, paramDefaults, null);
        }

        /** creates a HSISegmentOpImage.
          *  @param pb operation parameters (numbers of bins).
          *  @param hints result image rendering parameters.
          *  @return an instance of HSISegmentOpImage.
          */
        public RenderedImage create(ParameterBlock pb, RenderingHints hints)
        {
                if (!validateParameters(pb))
                {
                        return null;
                }
                return new HSISegmentOpImage(pb.getRenderedSource(0), new ImageLayout(), (Integer) pb.getObjectParameter(0), (Integer) pb.getObjectParameter(1));
        }

        /** validate the parameter types for this operation.
          *  @param pb the operation parameters.
          *  @return validation assessment (boolean)
          */
        public boolean validateParameters(ParameterBlock pb)
        {
                return true;
        }

        //public String toString();
        //public boolean equals(Object obj);
        //protected Object clone() throws CloneNotSupportedException;
        //protected void finalize() throws Throwable;


}

