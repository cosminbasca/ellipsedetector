package robo.vision.widgets;

import java.awt.Color;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;

import robo.vision.RoboRaster;


public class VisionUtils {

    /** produce a 3 band luminance image from a 3 band color image */
    public static PlanarImage convertColorToGray(PlanarImage src, int brightness) {
        PlanarImage dst = null;
        double b = (double) brightness;
        double[][] matrix = {
                                { .114D, 0.587D, 0.299D, b },
                                { .114D, 0.587D, 0.299D, b },
                                { .114D, 0.587D, 0.299D, b }
                            };

        if ( src != null ) {
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(src);
            pb.add(matrix);
            dst = JAI.create("bandcombine", pb, null);
        }

        return dst;
    }

    /** produce a 3 band image from a single band gray scale image */
    public static PlanarImage convertGrayToColor(PlanarImage src, int brightness) {
        PlanarImage dst = null;
        double b = (double) brightness;
        double[][] matrix = {
                                { 1.0D, b },
                                { 1.0D, b },
                                { 1.0D, b }
                            };

        if ( src != null ) {
            int nbands = src.getSampleModel().getNumBands();

           if ( nbands == 1 ) {
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(src);
                pb.add(matrix);
                dst = JAI.create("bandcombine", pb, null);
            } else {
                dst = src;
            }
        }

        return dst;
    }
    
    
    public static Histogram createHistogram(int data[][],int w,int h,ROI roi,int bins[])
    {
    	int bands = bins.length;
    	
    	WritableRaster raster = Raster.createBandedRaster(DataBuffer.TYPE_INT,
    				w,				// width
    				h,				// heigth
    				bands,			// bands
    				null);			// (0,0)
    	
    	/*raster.setDataElements(
    				0,		// x
    				0,		// y
    				w,		// w
    				h,		// h
    				data);	// data*/
    	
    	double minV[] = new double[bands];
    	double maxV[] = new double[bands];
    	
    	for(int i=0;i<bands;i++)		
    	{	
    		raster.setSamples(0,0,w,h,i,data[i]);
    		minV[i]	=	0.0;
    		maxV[i] = 	bins[i];
    	}
    	    	    	    	
    	//				bins minVal maxVal bands
    	Histogram hist = new Histogram(bins, minV, maxV ,bands);
    	hist.countPixels(raster,
    				roi,	// null = all
    				0,//(int)roi.getBounds().getX(),		// x
    				0,//(int)roi.getBounds().getY(),		// y
    				1,		// sample x
    				1);		// sample y
    	
    	return hist;
    }
    
    public static Histogram createHistogram(Raster src,ROI roi,int bins[])
    {
    	int bands 	= src.getNumBands();
    	double minV[] = new double[bands];
    	double maxV[] = new double[bands];
    	
    	for(int i=0;i<bands;i++)		
    	{	
    		minV[i]	=	0.0;
    		maxV[i] = 	bins[i];
    	}
    	
    	//				bins minVal maxVal bands
    	Histogram hist = new Histogram(bins, minV, maxV ,bands);
    	hist.countPixels(src,
    				roi,	// null = all
    				0,//(int)roi.getBounds().getX(),		// x
    				0,//(int)roi.getBounds().getY(),		// y
    				1,		// sample x
    				1);		// sample y
    	
    	return hist;
    }
    
    public static Histogram getHueSatHistogram(Raster src,ROI roi)
    {
    	int 	w		=	src.getWidth();
    	int		h		=	src.getHeight();
    	
    	float	hsb[]	=	new float[3];
    	int 	r		= 	0;
    	int 	g		= 	0;
    	int 	b		= 	0;
    	
    	int		hs[][]	=	new int[2][w*h]; 
    	
    	// get hue
    	for(int x = 0; x<w ; x++)
    	{
    		for(int y = 0; y<h ; y++)
	    	{
	    		r = src.getSample(x,y,0);
	    		g = src.getSample(x,y,1);
	    		b = src.getSample(x,y,2);
	    		Color.RGBtoHSB( r, g, b , hsb );
	    		
	    		hs[0][x+y*w] = (int)Math.floor(hsb[0]*359);	// hue
	    		hs[1][x+y*w] = (int)Math.floor(hsb[1]*255);	// sat
	    	}
    	}
    	
    	return VisionUtils.createHistogram(hs,w,h,roi,new int[]{360,256});
    }
    
    public static int[]	getHistogramData(Histogram hist,int band)
    {
    	int[] local_array = new int[hist.getNumBins(band)];
    	
    	for ( int i = 0; i < hist.getNumBins(band); i++ ) 
            local_array[i] = hist.getBinSize(band, i);
        
        return local_array;
    }
    
    public static double[] getHistogramDistributions(Histogram hist,int band,int w,int h)
    {
    	int[] data = VisionUtils.getHistogramData(hist,band);
    	int   len  = w*h;
    	
    	double[] dist = new double[data.length];
    	for(int i=0;i<data.length;i++)
    		dist[i] = (double)data[i] / (double)len;
    	return dist;
    }
    
    // entropia maxima
    public static int	getHistogramMEC(double dist[])
    {
    	int 	val = 0;
    	double 	P 	= 0.0;
    	double  t1	= 0;
    	double  max	= 0;
    	double  H	= 0;
    	double  H1	= 0;
    	
    	for(int i=0;i<dist.length;i++)
    	{
    		for(int j = 0;j<i;j++)
    		{
    			P+=dist[j];
    			if(dist[j] > 0)
    				H+=dist[j] * Math.log(dist[j]);
    		}
    		H = -H;
    		
    		for(int j =0; j<dist.length;j++)
    		{
    			if(dist[j] > 0)
    				H1+=dist[j] * Math.log(dist[j]);
    		}
    		H1 = -H1;
    		
    		if(P >0 && P <1)
    			t1 = Math.log(P*(1-P))+H/P+(H1/(1-P) );
    		if(t1 >= max)
    		{
    			max = t1;
    			val = i;
    		}    		
    	}
    	
    	return val;
    }
    
    // corelatia maxima
    public static int	getHistogramMCC(double dist[])
    {
    	int 	val = 0;
    	double 	P 	= 0.0;
    	double  t1	= 0;
    	double  max	= 0;
    	double  H	= 0;
    	double  H1	= 0;
    	
    	for(int i=0;i<dist.length;i++ , P=0, H=0 ,H1=0)
    	{    		
    		if(dist[i] >0) 		
    		{
    			for(int j=0;j<i;j++)	
    			{
    				P+=dist[j];
    				H+=Math.pow(dist[j],2);
    			}
    			for(int j=0;j<i;j++)
    				H1+=Math.pow(dist[j],2);
    			if(P>0 && P<1 && H1>0 && H>0)
    				t1 = 2 * Math.log(P*(1-P))-Math.log(H*H1);
    			if(t1>=max)
    			{
    				max = t1;
    				val = i;
    			}
    		}
    	}
    	
    	return val;
    }
    
    public static void drawPixel(int band,int x,int y,WritableRaster dst,int w)
   	{
   		int _x =  x-w/2;
   		int _y =  y-w/2;
   		
		for(int i=0;i<w;i++)
   			for(int j=0;j<w;j++)   			
   				dst.setSample(_x+i,_y+j,band,RoboRaster.WHITE);   		
   	}
    
    /*public static LookupTableJAI HueSatHistogramToRGBLookupTable(Histogram hist,float brigth)
    {
    	int 	rgb		=	0;
    	float 	hsb[]	=	new float[3];
    	
    	// get the hue and saturation bands
    	int[]	hue	=	VisionUtils.getHistogramData(hist,0);
    	int[]	sat	=	VisionUtils.getHistogramData(hist,1);
    	
    	int[][]	rgbT=	new int[3][256];
    	
    	for(int i=0;i<256;i++)
    	{
    		rgbT[0][i] = 0;
    		rgbT[1][i] = 0;
    		rgbT[2][i] = 0;
    	}
    	
    	float 	fhue= 	0;
    	float	fsat=	0;
    	Color 	tmpCol;
    	
    	for(int i=0;i<360;i++)
    	{
    		fhue = (float)i	/ 360.0F;
    		
    		rgb  = Color.HSBtoRGB(fhue,0.5F,brigth);
    		tmpCol = new Color(rgb);
    		
    		//rgbT[(rgb / 0x10000) % 0x100][0]++;
    		//rgbT[(rgb / 0x100  ) % 0x100][1]++;
    		//rgbT[(rgb / 0x1    ) % 0x100][2]++;
    		
    		rgbT[0][tmpCol.getRed()]++;
    		rgbT[1][tmpCol.getGreen()]++;
    		rgbT[2][tmpCol.getBlue()]++;
    	}
    	
    	return new LookupTableJAI(rgbT);
    }*/
    
    /*public static PlanarImage applySmoothHueHistogram(PlanarImage src,int brightness)
    {
    	Histogram 		hsHist 	= VisionUtils.getHueSatHistogram(src.getData(),null);
    	LookupTableJAI 	lTab	= VisionUtils.HueSatHistogramToRGBLookupTable(hsHist, ((float)brightness / 256.0F) );
    	
    	ParameterBlock pb = new ParameterBlock();
        pb.addSource(src);
        pb.add(lTab);

        return (PlanarImage)JAI.create("lookup", pb);
    }*/
}
