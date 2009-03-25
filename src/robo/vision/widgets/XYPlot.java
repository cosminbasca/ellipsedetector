package robo.vision.widgets;
/*
 * @(#)XYPlot.java	1.6 99/06/19 00:18:00
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * A class to plot histograms (primarily)
 * Single band per object (bar chart)
 *
 * @author Dennis Sigel
 */

public class XYPlot extends JPanel {

    private int[] data;
    private int max;

   /**
    * Default constructor
    */
    public XYPlot() {
        super();
        this.setPreferredSize(new Dimension(400,200) );
       	this.setBackground(Color.black);
    }

    private void setData(int[] array) {
        data = new int[array.length];
        max = -1;

        for ( int i = 0; i < array.length; i++ ) {
            data[i] = array[i];

            if ( data[i] > max ) {
                max = data[i];
            }
        }
    }

    public void plot(int[] array) {
        setData(array);
        repaint();        
    }

    /**
     * Plotter
     */
    public void paintComponent(Graphics g) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D)g;
        } else {
            return;
        }

        if ( data == null ) return;

        int width = getSize().width;
        int height = getSize().height;

        g2D.setColor(getBackground());
        g2D.fillRect(0, 0, width, height);
        g2D.setColor(Color.white);

        int length = data.length;
        float slope_x = (float) width / (float) length;
        float slope_y = (float) height / (float) max;

        for ( int i = 0; i < length; i++ ) {
           int x = (int) ((float)i*slope_x);
           int y = (int) ((float)data[i]*slope_y);
           g.drawLine(x, height, x, height - y);
        }
    }
}
