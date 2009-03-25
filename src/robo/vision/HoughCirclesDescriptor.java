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

/** Hough Transform circle finder
 */
public class HoughCirclesDescriptor extends OperationDescriptorImpl implements RenderedImageFactory
{

        /**
	 * 
	 */
	private static final long serialVersionUID = 7098501331692954906L;


		private static final String[][] resources = { {"GlobalName", "HoughCircles"}, {"LocalName", "HoughCircles"}, {"Vendor",""}, {"Description","Gaseste cercuri folosind transformata hough"}, {"DocURL",
        ""}, {"Version",""}, };


        private static final String[] supportedModes = { "rendered" };

        private static final int numSources = 1;

        private static final String[] paramNames = { "EdgeThreshold", "MaximaThreshold", "OutputIntensity", "MinRadius", "MaxRadius" };

        @SuppressWarnings("unchecked")
		private static final Class[] paramClasses = { java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class };

        private static final Object[] paramDefaults = { new Integer(5), new Integer(5), new Integer(255), new Integer(4), new Integer(12)};


        /** Constructs a HoughCirclesDescriptor.
         */
        public HoughCirclesDescriptor()
        {
                super(resources, supportedModes, numSources , paramNames, paramClasses, paramDefaults, null);
        }


        /** creates a HoughCirclesOpImage.
          *  @param pb operation parameters (thresholds, output intensity, radii).
          *  @param hints result image rendering parameters.
          *  @return an instance of HoughCirclesOpImage.
          */
        public RenderedImage create(ParameterBlock pb, RenderingHints hints)
        {
                if (!validateParameters(pb))
                {
                        return null;
                }
                return new HoughCirclesOpImage(pb.getRenderedSource(0), new ImageLayout(), (Integer) pb.getObjectParameter(0), (Integer) pb.getObjectParameter(1), (Integer) pb.getObjectParameter(2), (Integer) pb.getObjectParameter(3),
                        (Integer) pb.getObjectParameter(4));
        }

        /** validate the parameter types for this operation.
          *  @param pb the operation parameters.
          *  @return validation assessment (boolean)
          */
        public boolean validateParameters(ParameterBlock pb)
        {
                return true;
        }

        /* Class Constants */

        /* Mutators */

        /* Accessors */

        /* Common Interface */

        //public String toString();
        //public boolean equals(Object obj);
        //protected Object clone() throws CloneNotSupportedException;
        //protected void finalize() throws Throwable;

}

