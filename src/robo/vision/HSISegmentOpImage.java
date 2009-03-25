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

import javax.media.jai.DataBufferDouble;
import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;


@SuppressWarnings("unchecked")
public class HSISegmentOpImage extends UntiledOpImage
{

        private int aBins = 0, bBins = 0;

        class ColorBin extends Object
        {
                double minH;
                double maxH;
                double minS;
                double maxS;
                int count;
                int r;
                int g;
                int b;
        }

        /** Constructs HSISegmentOpImage.  Image dimensions are copied from the
          * source image.  The tile grid layout, SampleModel, and ColorModel may
          * optionally be specified by an ImageLayout object
          * @param source a RenderedImage
          * @param layout an ImageLayout optionally containing the tile grid layout,
          * SampleModel, and ColorModel or null.
          */
        public HSISegmentOpImage(RenderedImage source, ImageLayout layout, Integer aBins, Integer bBins)
        {
                super(source, null, layout);
                this.aBins = aBins.intValue();
                this.bBins = bBins.intValue();
        }

        /** HSI segments the image.
          * @param src the source raster.
          * @param dst the resultant connected component image.
          * @param destRect the rectangle within the OpImage to be computed */
        protected void computeImage(Raster[] srcarr, WritableRaster dst, Rectangle destRect)
        {
                Raster src = srcarr[0];
                RoboRaster source = new RoboRaster(src);
                WritableRoboRaster dest = new WritableRoboRaster(dst);
                int width = source.getWidth(), height = source.getHeight();

                /* Implement op here */

                /* perform HSI transform */
                DataBufferDouble hsi = new DataBufferDouble(width * height, 3);
                @SuppressWarnings("unused")
				float hsb[] = new float[3];
                double minH = Double.MAX_VALUE, maxH = Double.MIN_VALUE;
                double minS = Double.MAX_VALUE, maxS = Double.MIN_VALUE;

                for (int v = 0; v < height; v++)
                {
                        for (int u = 0; u < width; u++)
                        {
                                int p = v * width + u;
                                @SuppressWarnings("unused")
								double H, S, I;
                                double r = source.red(u, v);
                                double g = source.green(u, v);
                                double b = source.blue(u, v);
                                double minRGB = (r < g) ? r : g;
                                minRGB = (minRGB < b) ? minRGB : b;
                                // 	Color.RGBtoHSB(r, g, b, hsb);
                                // 	hsi.setElemDouble(0, p, H = hsb[0]);
                                // 	hsi.setElemDouble(1, p, S = hsb[1]);
                                // 	hsi.setElemDouble(2, p, I = hsb[2]);
                                hsi.setElemDouble(0, p, H = Math.acos(.5 * ((r - g) + (r - b)) / Math.sqrt((r - g) * (r - g) + (r - b) * (g - b))));
                                hsi.setElemDouble(1, p, S = 1. - 3 / (r + g + b) * minRGB);
                                hsi.setElemDouble(2, p, I = (r + g + b) / 3.);
                                if (minH > H)
                                        minH = H;
                                if (maxH < H)
                                        maxH = H;
                                if (minS > S)
                                        minS = S;
                                if (maxS < S)
                                        maxS = S;
                        }
                }
                System.out.println(minH + "-"+maxH + " "+minS + "-"+maxS);
                double aInc = (maxH - minH) / aBins;
                double bInc = (maxS - minS) / bBins;

                ColorBin bin[] = new ColorBin[aBins * bBins];
                for (int x = 0; x < aBins; x++)
                {
                        for (int y = 0; y < bBins; y++)
                        {
                                int p = x * bBins + y;
                                bin[p] = new ColorBin();
                                bin[p].minH = minH + x * aInc;
                                bin[p].maxH = minH + (x + 1) * aInc;
                                bin[p].minS = minS + y * bInc;
                                bin[p].maxS = minS + (y + 1) * bInc;
                                bin[p].count = 0;
                                bin[p].r = 0;
                                bin[p].g = 0;
                                bin[p].b = 0;
                        }
                }

                int seg[] = new int[width * height];
                for (int v = 0; v < height; v++)
                {
                        for (int u = 0; u < width; u++)
                        {
                                int p = v * width + u;
                                for (int x = 0; x < aBins * bBins; x++)
                                {
                                        if (hsi.getElemDouble(0, p) >= bin[x].minH && hsi.getElemDouble(0, p) <= bin[x].maxH && hsi.getElemDouble(1, p) >= bin[x].minS && hsi.getElemDouble(1, p) <= bin[x].maxS)
                                        {
                                                seg[p] = x;
                                                bin[x].r += source.red(u, v);
                                                bin[x].g += source.green(u, v);
                                                bin[x].b += source.blue(u, v);
                                                bin[x].count++;
                                                break;
                                        }
                                }
                        }
                }

                for (int x = 0; x < aBins * bBins; x++)
                {
                        if (bin[x].count != 0)
                        {
                                bin[x].r /= bin[x].count;
                                bin[x].g /= bin[x].count;
                                bin[x].b /= bin[x].count;
                        }
                }

                for (int v = 0; v < height; v++)
                {
                        for (int u = 0; u < width; u++)
                        {
                                int p = v * width + u;
                                dest.setRed(u, v, bin[seg[p]].r);
                                dest.setGreen(u, v, bin[seg[p]].g);
                                dest.setBlue(u, v, bin[seg[p]].b);
                        }
                }
        }

}


