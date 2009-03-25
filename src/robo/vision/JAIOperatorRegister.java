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

// JAI
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;

public class JAIOperatorRegister 
{
	private static OperationRegistry 	opRegistry;
	
	static
	{
		registerOperators();
	}
	
	public static void registerOperators() 
	{
		opRegistry = JAI.getDefaultInstance().getOperationRegistry();
		
        try 
        {
            RenderedImageFactory rif = null;
            String operationName = null;
            
            //*********************************************************************
            //********	houghlines
            //*********************************************************************
            HoughLinesDescriptor linesDescriptor = new HoughLinesDescriptor();
            operationName = "houghlines";
            rif = linesDescriptor;
            opRegistry.registerDescriptor(linesDescriptor);
            RIFRegistry.register(opRegistry, operationName, "", rif);
			
			//*********************************************************************
            //********	houghcircles
            //*********************************************************************
            HoughCirclesDescriptor circlesDescriptor = new HoughCirclesDescriptor();
            operationName = "houghcircles";
            rif = circlesDescriptor;
            opRegistry.registerDescriptor(circlesDescriptor);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	houghellipses
            //*********************************************************************            
            HoughEllipseDescriptor ellipseDescriptor = new HoughEllipseDescriptor();
            operationName = "houghellipses";
            rif = ellipseDescriptor;
            opRegistry.registerDescriptor(ellipseDescriptor);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	cannyedge
            //*********************************************************************
            CannyEdgeDescriptor cannyedgeDescriptor = new CannyEdgeDescriptor();
            operationName = "cannyedge";
            rif = cannyedgeDescriptor;
            opRegistry.registerDescriptor(cannyedgeDescriptor);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	binarythreshold
            //*********************************************************************
            BinaryThresholdDescriptor bThreshDescriptor = new BinaryThresholdDescriptor();
            operationName = "binarythreshold";
            rif = bThreshDescriptor;
            opRegistry.registerDescriptor(bThreshDescriptor);
            RIFRegistry.register(opRegistry, operationName, "", rif);
			
			//*********************************************************************
            //********	adaptivethreshold
            //*********************************************************************
			AdaptiveThresholdDescriptor adThreshDescriptor = new AdaptiveThresholdDescriptor();
            operationName = "adaptivethreshold";
            rif = adThreshDescriptor;
            opRegistry.registerDescriptor(adThreshDescriptor);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	stentifordthinning
            //*********************************************************************
            StentifordThinningDescriptor stThinning = new StentifordThinningDescriptor();
            operationName = "stentifordthinning";
            rif = stThinning;
            opRegistry.registerDescriptor(stThinning);
            RIFRegistry.register(opRegistry, operationName, "", rif);
              
            //*********************************************************************
            //********	euclideancolorsegment
            //*********************************************************************            
            EuclideanColorSegmentDescriptor colSeg = new EuclideanColorSegmentDescriptor();
            operationName = "euclideancolorsegment";
            rif = colSeg;
            opRegistry.registerDescriptor(colSeg);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	qcolorsegment
            //*********************************************************************      
            QColorSegmentDescriptor qcolSeg = new QColorSegmentDescriptor();
            operationName = "qcolorsegment";
            rif =qcolSeg;
            opRegistry.registerDescriptor(qcolSeg);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	huecolorsegment
            //*********************************************************************
            HueColorSegmentDescriptor huecolSeg = new HueColorSegmentDescriptor();
            operationName = "huecolorsegment";
            rif =huecolSeg;
            opRegistry.registerDescriptor(huecolSeg);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	colorfilter
            //*********************************************************************
            ColorFilterDescriptor colFil = new ColorFilterDescriptor();
            operationName = "colorfilter";
            rif = colFil;
            opRegistry.registerDescriptor(colFil);
            RIFRegistry.register(opRegistry, operationName, "", rif);
                
            //*********************************************************************
            //********	huehistogramthreshold
            //*********************************************************************        
            HueHistogramThresholdDescriptor hHist = new HueHistogramThresholdDescriptor();
            operationName = "huehistogramthreshold";
            rif = hHist;
            opRegistry.registerDescriptor(hHist);
            RIFRegistry.register(opRegistry, operationName, "", rif);
            
            //*********************************************************************
            //********	hsiseg
            //*********************************************************************        
            HSISegmentDescriptor hsiSeg = new HSISegmentDescriptor();
            operationName = "HSISegment";
            rif = hsiSeg;
            opRegistry.registerDescriptor(hsiSeg);
            RIFRegistry.register(opRegistry, operationName, "", rif);
                        
        }
        catch (IllegalArgumentException e)
        {
        	//e.printStackTrace();
        }
    }
}
