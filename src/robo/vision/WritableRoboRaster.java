package robo.vision;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/** A simple extension of WritableRaster which adds some convenience methods
 *  for setting pixel values in a more intuitive way (setGrey(), setRed(),
 */
public class WritableRoboRaster extends WritableRaster
{

        /*
           CONSTRUCTORS
        */

        /** constructs a WritableRaster2 by calling parent constructor with the given
         * parameters.
         */
        protected WritableRoboRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin)
        {
                super(sampleModel, dataBuffer, origin);
        }

        /** constructs a WritableRaster2 by calling parent constructor with the given
          * parameters.
          */
        protected WritableRoboRaster(SampleModel sampleModel, DataBuffer dataBuffer, Rectangle aRegion, Point sampleModelTranslate, WritableRaster parent)
        {
                super(sampleModel, dataBuffer, aRegion, sampleModelTranslate, parent);
        }

        /** constructs a WritableRaster2 by calling parent constructor with the given
          * parameters.
          */
        protected WritableRoboRaster(SampleModel sampleModel, Point origin)
        {
                super(sampleModel, origin);
        }

        /** Effectively type-casts a Raster as a Raster2 by calling parent
          *  constructor with the given parameters.
          *  @param src the Raster to cast
          */
        public WritableRoboRaster(WritableRaster src)
        {
                super(src.getSampleModel(), src.getDataBuffer(), new Point(0, 0));
        }

        /*
           PUBLIC INTERFACE
         */

        /* Class Constants */

        public static final int BLACK = 0;
        public static final int WHITE = 255;

        /* Mutators */

        /** sets the pixel value in band 0 at the given position.
         * @param u horizontal raster position.
         * @param v vertical raster position.
         * @param value the pixel value to store at position u,v.
         */
        public void setGrey(int u, int v, int value)
        {
                int b;
                if ((b = getNumBands()) == 1)
                        setSample(u, v, 0, value);
                else
                {
                        for (int x = 0; x < b; x++)
                        {
                                setSample(u, v, x, value);
                        }
                }
        }

        /** sets the pixel value in band 0 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @param value the red pixel value to store at position u,v.
          */
        public void setRed(int u, int v, int value)
        {
                setSample(u, v, 0, value);
        }

        /** sets the pixel value in band 0 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @param value the green pixel value to store at position u,v.
          */
        public void setGreen(int u, int v, int value)
        {
                setSample(u, v, 1, value);
        }

        /** sets the pixel value in band 0 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @param value the blue pixel value to store at position u,v.
          */
        public void setBlue(int u, int v, int value)
        {
                setSample(u, v, 2, value);
        }

        /** sets the pixel value in band 0 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @param r the red pixel value to store at position u,v.
          * @param g the green pixel value to store at position u,v.
          * @param b the blue pixel value to store at position u,v.
          */
        public void setRGB(int u, int v, int r, int g, int b)
        {
                int rgb[] = { r, g, b };
                setPixel(u, v, rgb);
        }

        /* Accessors */

        /* Common Interface */

        //public String toString();
        //public boolean equals(Object obj);
        //protected Object clone() throws CloneNotSupportedException;
        //protected void finalize() throws Throwable;

        /*
          PRIVATE METHODS
        */

        /*
          CLASS AND OBJECT ATTRIBUTES
        */

        /*
           TEST METHODS
        */

        //public static void main(String arg[]);
}

