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
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;

/** Find lines in an image with a Hough transform.
 */
@SuppressWarnings("unchecked")
public class HoughCirclesOpImage extends UntiledOpImage
{

        private int houghThreshold = 5, magnitudeThreshold = 5, greyOut = 255;
        private double maxR = 12., minR = 4.;


        /** Constructs HoughCirclesOpImage.  Image dimensions are copied from the
         * source image.  The tile grid layout, SampleModel, and ColorModel may
         * optionally be specified by an ImageLayout object
         * @param source a RenderedImage
         * @param layout an ImageLayout optionally containing the tile grid layout,
         * SampleModel, and ColorModel or null.
         */

        public HoughCirclesOpImage(RenderedImage source, ImageLayout layout, Integer edgeThreshold, Integer maximaThreshold, Integer outputIntensity, Integer minRadius, Integer maxRadius)
        {
                super(source, null, layout);
                this.magnitudeThreshold = edgeThreshold.intValue();
                this.houghThreshold = maximaThreshold.intValue();
                this.greyOut = outputIntensity.intValue();
                this.minR = (double) minRadius.intValue();
                this.maxR = (double) maxRadius.intValue();
        }


        //public String toString();
        //public boolean equals(Object obj);
        //protected Object clone() throws CloneNotSupportedException;
        //protected void finalize() throws Throwable;



        /** Computes the destination image.
         * @param src the source raster.
         * @param dst the resultant image.
         * @param destRect the rectangle within the OpImage to be computed */
        protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
        {
                Raster src = srcarr[0];
                RoboRaster source = new RoboRaster(src);
                WritableRoboRaster dest = new WritableRoboRaster(dst);
                int width = source.getWidth(), height = source.getHeight();
                int length = width * height;

                /* Implement op here */

                double direction;
                double minA = 0., maxA = width;
                double minB = 0., maxB = width;
                double rangeA = maxA - minA, rangeB = maxB - minB, rangeR = maxR - minR;
                int accum[][] = new int[(int) rangeR + 1][(int)(rangeA * rangeB)];
                int thresh[][] = new int[(int) rangeR + 1][(int)(rangeA * rangeB)];
                int gradient;

                dest.setRect(getSourceImage(0).getData());
                for (int v = (int) maxR; v < height - (int) maxR; v++)
                {
                        for (int u = (int) maxR; u < width - (int) maxR; u++)
                        {
                                //do Sobel operator
                                double sy = (source.grey(u - 1, v) * -2 + source.grey(u + 1, v) * 2 + source.grey(u - 1, v - 1) * -1 + source.grey(u + 1, v - 1) + source.grey(u - 1, v + 1) * -1 + source.grey(u + 1, v - 1)) / 8.0;
                                double sx = (source.grey(u - 1, v - 1) + source.grey(u, v - 1) * 2 + source.grey(u + 1, v - 1) + source.grey(u - 1, v + 1) * -1 + source.grey(u, v + 1) * -2 + source.grey(u + 1, v - 1) * -1) / 8.0;
                                //compute the gradient image from the original image
                                gradient = (int) Math.sqrt(sx * sx + sy * sy);
                                //compute the direction
                                if (sy == 0)
                                {
                                        if (sx == 0)
                                                direction = 0.;
                                        else
                                                direction = (sx > 0) ? 0.5 * Math.PI : -0.5 * Math.PI;
                                }
                                else if (sx == 0)
                                {
                                        direction = 0.0;
                                }
                                else
                                {
                                        direction = Math.atan(sy / sx);
                                }
                                direction += Math.PI / 2.0;
                                //force direction to go from PI/2 to -PI/2
                                if (direction > Math.PI / 2.0)
                                        direction -= Math.PI;
                                if (direction < Math.PI / -2.0)
                                        direction += Math.PI;

                                //if the mag is bigger than threshold, increment accumulator array
                                if (gradient > magnitudeThreshold)
                                {
                                        double theta = direction;
                                        for (double r = minR; r <= maxR; r += 1.)
                                        {
                                                double a = u - r * Math.cos(theta);
                                                double b = v - r * Math.sin(theta);
                                                try
                                                {
                                                        accum[(int)(r - minR)][(int) b * (int) rangeA + (int) a]++;
                                                }
                                                catch (ArrayIndexOutOfBoundsException e)
                                                {
                                                }
                                        }
                                }
                        }
                }

                //threshold the hough space; find the local maxima
                for (int r = 0; r <= rangeR; r++)
                {
                        for (int x = 0; x < rangeA * rangeB; x++)
                        {
                                if (x % rangeA > 1 && x % rangeA < rangeA - 1 && x > rangeA && x < rangeA * rangeB - rangeA && accum[r][x] >= houghThreshold && accum[r][x] > accum[r][x + 1] && accum[r][x] > accum[r][x - 1] &&
                                        accum[r][x] > accum[r][x + (int) rangeA] && accum[r][x] > accum[r][x - (int) rangeA] && accum[r][x] > accum[r][x + (int) rangeA + 1] && accum[r][x] > accum[r][x - (int) rangeA + 1] &&
                                        accum[r][x] > accum[r][x + (int) rangeA - 1] && accum[r][x] > accum[r][x - (int) rangeA - 1])
                                {
                                        thresh[r][x] = 255;
                                }
                        }
                }

                for (int r = (int) minR; r <= maxR; r++)
                {
                        for (int x = 0; x < rangeA * rangeB; x++)
                        {
                                if (thresh[r - (int) minR][x] != 0)
                                {
                                        for (double theta = 0.; theta < 2.*Math.PI; theta += Math.PI / 30.)
                                        {
                                                int x0 = (int)(r * Math.cos(theta)) + (int)(x % rangeA);
                                                int y0 = (int)(r * Math.sin(theta)) + (int)(x / rangeA);
                                                if (y0 * width + x0 >= 0 && y0 * width + x0 < length)
                                                {
                                                        dest.setGrey(x0, y0, greyOut);
                                                }
                                        }
                                }
                        }
                }
        }
}

