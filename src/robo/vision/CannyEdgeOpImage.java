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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Vector;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

@SuppressWarnings("unchecked")
public class CannyEdgeOpImage extends UntiledOpImage
{

        @SuppressWarnings("unused")
		private int[] mag;
        @SuppressWarnings("unused")
		private int[] gradx;
        @SuppressWarnings("unused")
		private int[] grady;

        int bands;
        int height;
        int width;

        
        public CannyEdgeOpImage(Vector sources, java.util.Map configuration, ImageLayout layout)
        {
                super(sources, configuration, layout);
        }

        //public String toString();
        //public boolean equals(Object obj);
        //protected Object clone() throws CloneNotSupportedException;
        //protected void finalize() throws Throwable;

        /** computes mean values for a tile of image data
         */
        protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect)
        {
                Raster src = sources[0];

                bands = src.getNumBands();
                height = src.getHeight();
                width = src.getWidth();

                mag = new int[width * height * bands];
                gradx = new int[width * height * bands];
                grady = new int[width * height * bands];
                /*

                 				buildMagAndGrad(src);
                                 Raster nms = nullmaxSupp(dest);
                                 applyHysteresis(nms, 0.33F, 0.9F, dest);*/
        }
        // Build mag and grad from derivative function
        @SuppressWarnings("unused")
		private void buildMagAndGrad(Raster src)
        {
                /*
                                         for (int i = 0; i < width; i++) {
                 		                        for (int j = 0; j < height; j++) {
                 		                        for (int j = 0; j < height; j++) {

                 		                                sum1 = 0;
                 		                                sum2 = 0;
                 		                                sum11 = 0;
                 		                                sum22 = 0;
                 		                                sum12 = 0;
                 		                                for (int k = -dv; k < dv ; k++) {
                 		                                        for (int l = -dh; l < dh ; l++) {
                 		                                                if (i + k >= 0 &&
                 		                                                        i + k < ww &&
                 		                                                        j + l >= 0 &&
                 		                                                        j + l < hh) {
                 		                                                        pix =
                 		                                                                db_zone.getElem(
                 		                                                                i + k +
                 		                                                                (j + l) *
                 		                                                                ww);

                 		                                                }
                 		                                        }
                 		                                }
                 		                                }
                 		                        }
                                 }*/
        }

        @SuppressWarnings("unused")
		private Raster nullmaxSupp(Raster mag, Raster gradx, Raster grady, WritableRaster dest)
        {

                // used to access the source image
                RandomIter itermag = RandomIterFactory.create(mag, null);
                RandomIter itergradx = RandomIterFactory.create(gradx, null);
                RandomIter itergrady = RandomIterFactory.create(grady, null);

                int m00, gx, gy, z1, z2;
                float mag1, mag2, xperp, yperp;
                int mw, mnw, mn, mne, me, mse, ms, msw;
                for (int band = 0; band < bands; band++)
                        for (int samp = 0; samp < width; samp++)
                        {
                                dest.setSample(samp, 0, band, 0);
                                dest.setSample(samp, height - 1, band, 0);
                        }

                for (int band = 0; band < bands; band++)
                        for (int line = 0; line < height; line++)
                        {
                                dest.setSample(0, line, band, 0);
                                dest.setSample(width - 1, line, band, 0);
                        }

                for (int band = 0; band < bands; band++)
                        for (int samp = 1; samp < width - 2; samp++)
                                for (int line = 1; line < height - 2; line++)
                                {

                                        m00 = itermag.getSample(samp, line, band);

                                        mw = itermag.getSample(samp - 1, line, band);
                                        mnw = itermag.getSample(samp - 1, line - 1, band);
                                        mn = itermag.getSample(samp, line - 1, band);
                                        mne = itermag.getSample(samp + 1, line - 1, band);
                                        me = itermag.getSample(samp + 1, line, band);
                                        mse = itermag.getSample(samp + 1, line + 1, band);
                                        ms = itermag.getSample(samp, line + 1, band);
                                        msw = itermag.getSample(samp - 1, line + 1, band);

                                        if (m00 == 0)
                                        {
                                                dest.setSample(samp, line, band, 255);
                                        }
                                        else
                                        {
                                                gx = itergradx.getSample(samp, line, band);
                                                gy = itergrady.getSample(samp, line, band);

                                                xperp = -gx / ((float) m00);
                                                yperp = gy / ((float) m00);

                                                if (gx >= 0)
                                                {
                                                        if (gy >= 0)
                                                        {
                                                                if (gx >= gy)
                                                                {
                                                                        /* 111 */
                                                                        /* Left point */
                                                                        z1 = mw;
                                                                        z2 = mnw;

                                                                        mag1 = (m00 - z1) * xperp + (z2 - z1) * yperp;

                                                                        /* Right point */
                                                                        z1 = me;
                                                                        z2 = mse;

                                                                        mag2 = (m00 - z1) * xperp + (z2 - z1) * yperp;
                                                                }
                                                                else
                                                                {
                                                                        /* 110 */
                                                                        /* Left point */
                                                                        z1 = mn;
                                                                        z2 = mnw;

                                                                        mag1 = (z1 - z2) * xperp + (z1 - m00) * yperp;

                                                                        /* Right point */
                                                                        z1 = ms;
                                                                        z2 = mse;

                                                                        mag2 = (z1 - z2) * xperp + (z1 - m00) * yperp;
                                                                }
                                                        }
                                                        else
                                                        {
                                                                if (gx >= -gy)
                                                                {
                                                                        /* 101 */
                                                                        /* Left point */
                                                                        z1 = mw;
                                                                        z2 = msw;

                                                                        mag1 = (m00 - z1) * xperp + (z1 - z2) * yperp;

                                                                        /* Right point */
                                                                        z1 = me;
                                                                        z2 = mne;

                                                                        mag2 = (m00 - z1) * xperp + (z1 - z2) * yperp;
                                                                }
                                                                else
                                                                {
                                                                        /* 100 */
                                                                        /* Left point */
                                                                        z1 = ms;
                                                                        z2 = msw;

                                                                        mag1 = (z1 - z2) * xperp + (m00 - z1) * yperp;

                                                                        /* Right point */
                                                                        z1 = mn;
                                                                        z2 = mne;

                                                                        mag2 = (z1 - z2) * xperp + (m00 - z1) * yperp;
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        gy = itergrady.getSample(samp, line, band);
                                                        if (gy >= 0)
                                                        {
                                                                if (-gx >= gy)
                                                                {
                                                                        /* 011 */
                                                                        /* Left point */
                                                                        z1 = me;
                                                                        z2 = mne;

                                                                        mag1 = (z1 - m00) * xperp + (z2 - z1) * yperp;

                                                                        /* Right point */
                                                                        z1 = mw;
                                                                        z2 = msw;

                                                                        mag2 = (z1 - m00) * xperp + (z2 - z1) * yperp;
                                                                }
                                                                else
                                                                {
                                                                        /* 010 */
                                                                        /* Left point */
                                                                        z1 = mn;
                                                                        z2 = mne;

                                                                        mag1 = (z2 - z1) * xperp + (z1 - m00) * yperp;

                                                                        /* Right point */
                                                                        z1 = ms;
                                                                        z2 = msw;

                                                                        mag2 = (z2 - z1) * xperp + (z1 - m00) * yperp;
                                                                }
                                                        }
                                                        else
                                                        {
                                                                if (-gx > -gy)
                                                                {
                                                                        /* 001 */
                                                                        /* Left point */
                                                                        z1 = me;
                                                                        z2 = mse;

                                                                        mag1 = (z1 - m00) * xperp + (z1 - z2) * yperp;

                                                                        /* Right point */
                                                                        z1 = mw;
                                                                        z2 = mnw;

                                                                        mag2 = (z1 - m00) * xperp + (z1 - z2) * yperp;
                                                                }
                                                                else
                                                                {
                                                                        /* 000 */
                                                                        /* Left point */
                                                                        z1 = me;
                                                                        z2 = mne;

                                                                        mag1 = (z2 - z1) * xperp + (m00 - z1) * yperp;

                                                                        /* Right point */
                                                                        z1 = mn;
                                                                        z2 = mnw;

                                                                        mag2 = (z2 - z1) * xperp + (m00 - z1) * yperp;
                                                                }
                                                        }
                                                }

                                                /* Now determine if the current point is a maximum point */

                                                if ((mag1 > 0.0) || (mag2 > 0.0))
                                                {
                                                        dest.setSample(samp, line, band, 255);
                                                }
                                                else
                                                {
                                                        if (mag2 == 0.0)
                                                                dest.setSample(samp, line, band, 255);
                                                        else
                                                                dest.setSample(samp, line, band, 128);
                                                }


                                        }
                                }

                return dest;
        }

        /*******************************************************************************
             * PROCEDURE: follow_edges
             * PURPOSE: This procedure edges is a recursive routine that traces edgs along
             * all paths whose magnitude values remain above some specifyable lower
             * threshhold.
             * NAME: Mike Heath
             * DATE: 2/15/96
             *******************************************************************************/
        private void followEdges(WritableRaster edge, Raster mag, int samp, int line, int band, int lowval)
        {
                int pix, pixmag;
                int[] x = {1, 1, 0, -1, -1, -1, 0, 1};
                int[] y = {0, 1, 1, 1, 0, -1, -1, -1};

                for (int i = 0; i < 8; i++)
                {
                        pix = edge.getSample(samp + x[i], line - y[i], band);
                        pixmag = mag.getSample(samp + x[i], line - y[i], band);

                        if ((pix == 128) && (pixmag > lowval))
                        {
                                edge.setSample(samp + x[i], line - y[i], band, 0);
                                followEdges(edge, mag, samp + x[i], line - y[i], band, lowval);
                        }
                }
        }

        /*******************************************************************************
             * PROCEDURE: apply_hysteresis
             * PURPOSE: This routine finds edges that are above some high threshhold or
             * are connected to a high pixel by a path of pixels greater than a low
             * threshold.
             * NAME: Mike Heath
             * DATE: 2/15/96
             *******************************************************************************/
        @SuppressWarnings("unused")
		private void applyHysteresis(Raster mag, Raster nms, float tlow, float thigh, WritableRaster edge)
        {
                int pix, pixmag, r, c, pos, numedges, lowcount, highcount, i, rr, cc;
                int maximum_mag, sumpix;
                RandomIter itermag = RandomIterFactory.create(mag, null);
                RandomIter iternms = RandomIterFactory.create(nms, null);
                RandomIter iteredge = RandomIterFactory.create(edge, null);
                int[] hist = new int[32768];
                int lowthreshold;
                int highthreshold;



                /****************************************************************************
                * Initialize the edge map to possible edges everywhere the non-maximal
                * suppression suggested there could be an edge except for the border. At
                * the border we say there can not be an edge because it makes the
                * follow_edges algorithm more efficient to not worry about tracking an
                * edge off the side of the image.
                ****************************************************************************/
                edge.setRect(nms);

                /****************************************************************************
                 * Compute the histogram of the magnitude image. Then use the histogram to
                 * compute hysteresis thresholds.
                 ****************************************************************************/

                for (int band = 0; band < bands; band++)
                {
                        for (r = 0; r < 32768; r++)
                                hist[r] = 0;

                        pos = 0;
                        for (int samp = 0; samp < width; samp++)
                                for (int line = 0; line < height; line++)
                                {
                                        pix = edge.getSample(samp, line, band);
                                        if (pix == 128)
                                        {
                                                pixmag = mag.getSample(samp, line, band);
                                                hist[pixmag]++;

                                        }
                                        pos++;
                                }



                        /****************************************************************************
                             * Compute the number of pixels that passed the nonmaximal suppression.
                             ****************************************************************************/

                        numedges = 0;
                        maximum_mag = 0;
                        for (r = 1; r < 32768; r++)
                        {
                                if (hist[r] != 0)
                                        maximum_mag = r;
                                numedges += hist[r];
                        }

                        highcount = (int)(numedges * thigh + 0.5);

                        /****************************************************************************
                        * Compute the high threshold value as the (100 * thigh) percentage point
                        * in the magnitude of the gradient histogram of all the pixels that passes
                        * non-maximal suppression. Then calculate the low threshold as a fraction
                        * of the computed high threshold value. John Canny said in his paper
                        * "A Computational Approach to Edge Detection" that "The ratio of the
                        * high to low threshold in the implementation is in the range two or three
                        * to one." That means that in terms of this implementation, we should
                        * choose tlow ~= 0.5 or 0.33333.
                        ****************************************************************************/
                        r = 1;
                        numedges = hist[1];
                        while ((r < (maximum_mag - 1)) && (numedges < highcount))
                        {
                                r++;
                                numedges += hist[r];
                        }
                        highthreshold = r;
                        lowthreshold = (int)(highthreshold * tlow + 0.5);


                        /*
                                   printf("The input low and high fractions of %f and %f computed to\n", tlow, thigh);
                                   printf("magnitude of the gradient threshold values of: %d %d\n", lowthreshold, highthreshold);
                          */

                        /****************************************************************************
                         * This loop looks for pixels above the highthreshold to locate edges and
                         * then calls follow_edges to continue the edge.
                         ****************************************************************************/


                        pos = 0;
                        for (int samp = 0; samp < width; samp++)
                                for (int line = 0; line < height; line++)
                                {
                                        pix = edge.getSample(samp, line, band);
                                        pixmag = mag.getSample(samp, line, band);
                                        if ((pix == 128) && (pixmag >= highthreshold))
                                        {
                                                edge.setSample(samp, line, band, 0);
                                                followEdges(edge, mag, samp, line, band, lowthreshold);
                                        }
                                        pos++;
                                }


                        /****************************************************************************
                             * Set all the remaining possible edges to non-edges.
                             ****************************************************************************/


                        for (int samp = 0; samp < width; samp++)
                                for (int line = 0; line < height; line++)
                                {
                                        pix = edge.getSample(samp, line, band);
                                        if (pix != 0)
                                                edge.setSample(samp, line, band, 255);
                                }
                }
        }
}






