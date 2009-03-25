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

/** Hough Transform line finder
 */
public class HoughLinesDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{

        /**
	 * 
	 */
	private static final long serialVersionUID = -3230152846021104232L;

		private static final String[][] resources = { {"GlobalName", "HoughLines"}, {"LocalName", "HoughLines"}, {"Vendor",""}, {"Description","Cauta Linii folosind transformata hough"}, {"DocURL",
        ""}, {"Version",""}, };

        private static final String[] supportedModes = { "rendered" };

        private static final int numSources = 1;

        private static final String[] paramNames = { "EdgeThreshold", "MaximaThreshold", "OutputIntensity" };

        @SuppressWarnings("unchecked")
		private static final Class[] paramClasses = { java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class };

        private static final Object[] paramDefaults = { new Integer(5), new Integer(5), new Integer(255)};

        public HoughLinesDescriptor()
        {
                super(resources, supportedModes, numSources, paramNames, paramClasses, paramDefaults, null);
        }


        /** creates a HoughLinesOpImage.
          *  @param pb operation parameters (thresholds, output intensity).
          *  @param hints result image rendering parameters.
          *  @return an instance of HoughLinesOpImage.
          */
        public RenderedImage create(ParameterBlock pb, RenderingHints hints)
        {
                if (!validateParameters(pb))
                {
                        return null;
                }
                return new HoughLinesOpImage(pb.getRenderedSource(0), new ImageLayout(), (Integer) pb.getObjectParameter(0), (Integer) pb.getObjectParameter(1), (Integer) pb.getObjectParameter(2));
        }

        /** validate the parameter types for this operation.
          *  @param pb the operation parameters.
          *  @return validation assessment (boolean)
          */
        public boolean validateParameters(ParameterBlock pb)
        {
                return true;
        }
}

