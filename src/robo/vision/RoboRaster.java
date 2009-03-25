package robo.vision;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

/** A simple extension of Raster which adds some convenience methods for
 *  access pixel values in a more intuitive way (grey(), red(), green(),
 *  blue(), etc.) and a set of statistical operations on pixel values.
 */

public class RoboRaster extends Raster
{

        /*
           CONSTRUCTORS
        */

        /** constructs a Raster2 by calling parent constructor with the given
         * parameters.
         */
        public RoboRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin)
        {
                super(sampleModel, dataBuffer, origin);
        }

        /** constructs a Raster2 by calling parent constructor with the given
          * parameters.
          */
        public RoboRaster(SampleModel sampleModel, DataBuffer dataBuffer, Rectangle aRegion, Point sampleModelTranslate, Raster parent)
        {
                super(sampleModel, dataBuffer, aRegion, sampleModelTranslate, parent);
        }

        /** constructs a Raster2 by calling parent constructor with the given
          * parameters.
          */
        public RoboRaster(SampleModel sampleModel, Point origin)
        {
                super(sampleModel, origin);
        }

        /** Effectively type-casts a Raster as a Raster2 by calling parent
          *  constructor with the given parameters.
          *  @param src the Raster to cast
          */
        public RoboRaster(Raster src)
        {
                super(src.getSampleModel(), src.getDataBuffer(), new Point(0, 0));
        }

        /*
           PUBLIC INTERFACE
         */

        /* Class Constants */

        public static final int BLACK = 0;
        public static final int WHITE = 255;

        /* Accessors */

        /** returns the pixel value in band 0 at the given position.
         * @param u horizontal raster position.
         * @param v vertical raster position.
         * @return the pixel value at position u,v.
         */
        public int grey(int u, int v)
        {
                return (getSample(u, v, 0));
        }

        /** returns the pixel value in band 0 at position west of the given
          *  position, if it exists, otherwise returns -1.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the pixel value at position u-1,v or -1.
          */
        public int westNeighbor(int u, int v)
        {
                if (u > 0)
                        return (getSample(u - 1, v, 0));
                else
                        return -1;
        }

        /** returns the pixel value in band 0 at position east of the given
          *  position, if it exists, otherwise returns -1.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the pixel value at position u+1,v or -1.
          */
        public int eastNeighbor(int u, int v)
        {
                if (u < getWidth() - 1)
                        return (getSample(u + 1, v, 0));
                else
                        return -1;
        }

        /** returns the pixel value in band 0 at position north of the given
          *  position, if it exists, otherwise returns -1.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the pixel value at position u,v-1, or -1.
          */
        public int northNeighbor(int u, int v)
        {
                if (v > 0)
                        return (getSample(u, v - 1, 0));
                else
                        return -1;
        }

        /** returns the pixel value in band 0 at position south of the given
          *  position, if it exists, otherwise returns -1.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the pixel value at position u,v+1.
          */
        public int southNeighbor(int u, int v)
        {
                if (v < getHeight() - 1)
                        return (getSample(u, v + 1, 0));
                else
                        return -1;
        }

        /** returns the blue pixel value in band 2 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the blue pixel value at position u,v.
          */
        public int blue(int u, int v)
        {
                return (getSample(u, v, 2));
        }

        /** returns the green pixel value in band 1 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the green pixel value at position u,v.
          */
        public int green(int u, int v)
        {
                return (getSample(u, v, 1));
        }

        /** returns the red pixel value in band 0 at the given position.
          * @param u horizontal raster position.
          * @param v vertical raster position.
          * @return the red pixel value at position u,v.
          */
        public int red(int u, int v)
        {
                return (getSample(u, v, 0));
        }

        /** returns the minimum pixel value in band 0.
          * @return the minimum pixel value.
          */
        public int minGrey()
        {
                int m = Integer.MAX_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (grey(u, v) < m)
                                        m = grey(u, v);
                        }
                }
                return m;
        }

        /** returns the minimum red pixel value in band 0.
          * @return the minimum red pixel value.
          */
        public int minRed()
        {
                int m = Integer.MAX_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (red(u, v) < m)
                                        m = red(u, v);
                        }
                }
                return m;
        }

        /** returns the minimum green pixel value in band 1.
          * @return the minimum green pixel value.
          */
        public int minGreen()
        {
                int m = Integer.MAX_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (green(u, v) < m)
                                        m = green(u, v);
                        }
                }
                return m;
        }

        /** returns the minimum blue pixel value in band 2.
          * @return the minimum blue pixel value.
          */
        public int minBlue()
        {
                int m = Integer.MAX_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (blue(u, v) < m)
                                        m = blue(u, v);
                        }
                }
                return m;
        }

        /** returns the maximum pixel value in band 0
          * @return the maximum pixel value.
          */
        public int maxGrey()
        {
                int m = Integer.MIN_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (grey(u, v) > m)
                                        m = grey(u, v);
                        }
                }
                return m;
        }

        /** returns the maximum red pixel value in band 0.
          * @return the maximum red pixel value.
          */
        public int maxRed()
        {
                int m = Integer.MIN_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (red(u, v) > m)
                                        m = red(u, v);
                        }
                }
                return m;
        }

        /** returns the maximum green pixel value in band 1.
          * @return the maximum green pixel value.
          */
        public int maxGreen()
        {
                int m = Integer.MIN_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (green(u, v) > m)
                                        m = green(u, v);
                        }
                }
                return m;
        }

        /** returns the maximum blue pixel value in band 2.
          * @return the maximum blue pixel value.
          */
        public int maxBlue()
        {
                int m = Integer.MIN_VALUE;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                if (blue(u, v) > m)
                                        m = blue(u, v);
                        }
                }
                return m;
        }

        /** returns the mean red pixel value in band 0.
          * @return the mean red pixel value.
          */
        public double redMean()
        {
                double mean = 0.f;

                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                mean += red(u, v);
                        }
                }
                mean /= getWidth() * getHeight();
                return mean;
        }

        /** returns the covariance of the red pixels in band 0.
          * @return the red pixel value covariance.
          */
        public double redCovariance()
        {
                double mean = redMean(), covariance = 0.f, temp;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                temp = red(u, v) - mean;
                                covariance += temp * temp;
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

        /** returns the mean green pixel value in band 1.
          * @return the mean green pixel value.
          */
        public double greenMean()
        {
                double mean = 0.f;

                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                mean += green(u, v);
                        }
                }
                mean /= getWidth() * getHeight();
                return mean;
        }

        /** returns the covariance of the green pixels in band 1.
          * @return the green pixel value covariance.
          */
        public double greenCovariance()
        {
                double mean = greenMean(), covariance = 0.f, temp;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                temp = green(u, v) - mean;
                                covariance += temp * temp;
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

        /** returns the mean blue pixel value in band 2.
          * @return the mean blue pixel value.
          */
        public double blueMean()
        {
                double mean = 0.f;

                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                mean += blue(u, v);
                        }
                }
                mean /= getWidth() * getHeight();
                return mean;
        }

        /** returns the covariance of the blue pixels in band 2.
          * @return the blue pixel value covariance.
          */
        public double blueCovariance()
        {
                double mean = blueMean(), covariance = 0.f, temp;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                temp = blue(u, v) - mean;
                                covariance += temp * temp;
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

        /** returns the mean pixel value in band 0.
          * @return the mean pixel value.
          */
        public double greyMean()
        {
                double mean = 0.f;

                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                mean += grey(u, v);
                        }
                }
                mean /= getWidth() * getHeight();
                return mean;
        }

        /** returns the covariance of the pixels in band 0.
          * @return the pixel value covariance.
          */
        public double greyCovariance()
        {
                double mean = greyMean(), covariance = 0.f, temp;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                temp = grey(u, v) - mean;
                                covariance += temp * temp;
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

        /** returns the covariance between the pixel values in bands 0 and 1 (red, green).
          * @return the covariance between the red and green pixel values.
          */
        public double redGreenCrossCovariance()
        {
                double mean1 = redMean(), mean2 = greenMean(), covariance = 0.;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                covariance += (red(u, v) - mean1) * (green(u, v) - mean2);
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

        /** returns the covariance between the pixel values in bands 1 and 2 (blue, green).
          * @return the covariance between the blue and green pixel values.
          */
        public double blueGreenCrossCovariance()
        {
                double mean1 = blueMean(), mean2 = greenMean(), covariance = 0.;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                covariance += (blue(u, v) - mean1) * (green(u, v) - mean2);
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

        /** returns the covariance between the pixel values in bands 0 and 2 (red, blue).
          * @return the covariance between the red and blue pixel values.
          */
        public double redBlueCrossCovariance()
        {
                double mean1 = redMean(), mean2 = blueMean(), covariance = 0.;
                for (int v = 0; v < getHeight(); v++)
                {
                        for (int u = 0; u < getWidth(); u++)
                        {
                                covariance += (red(u, v) - mean1) * (blue(u, v) - mean2);
                        }
                }
                covariance /= getWidth() * getHeight();
                return covariance;
        }

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

