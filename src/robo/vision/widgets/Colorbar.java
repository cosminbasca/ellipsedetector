package robo.vision.widgets;
/*
 * @(#)Colorbar.java	1.8 99/06/19 00:17:55
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
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * A output widget used to display a colormap, derived from the
 * javax.swing.JComponent, and can be used in any context that calls for a
 * JComponent.
 *
 * @author Dennis Sigel
 */

public class Colorbar extends JComponent {

    protected int componentWidth;
    protected int componentHeight;
    protected int direction = SwingConstants.HORIZONTAL;

    /** Brightness control */
    protected byte[][] lut;

    /**
     * Default constructor
     */
    public Colorbar() {
        lut = new byte[3][256];

        for ( int i = 0; i < 256; i++ ) {
            lut[0][i] = (byte) i;
            lut[1][i] = (byte) i;
            lut[2][i] = (byte) i;
        }
    }

    /** 
     * Constructs a Colorbar object 
     *
     * @param d used for orientation
     */
    public Colorbar(int d) {
        lut = new byte[3][256];

        for ( int i = 0; i < 256; i++ ) {
            lut[0][i] = (byte) i;
            lut[1][i] = (byte) i;
            lut[2][i] = (byte) i;
        }

        direction = d;
    }

    /** changes the contents of the lookup table */
    public synchronized void setLut(byte[][] newlut) {
        for ( int i = 0; i < newlut[0].length; i++ ) {
            lut[0][i] = newlut[0][i];
            lut[1][i] = newlut[1][i];
            lut[2][i] = newlut[2][i];
        }

        repaint();
    }

    /** Records a new size.  Called by the AWT. */
    public void setBounds(int x, int y, int width, int height) {
        componentWidth  = width;
        componentHeight = height;
        super.setBounds(x, y, width, height);
    }

    /**
     * Paint the Colorbar onto a Graphics object.
     */
    public synchronized void paintComponent(Graphics g) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D)g;
        } else {
            return;
        }

        g2D.setColor(getBackground());
        g2D.fillRect(0, 0, componentWidth, componentHeight);

        if ( direction == SwingConstants.HORIZONTAL ) {
            float slope = (float)componentWidth / 256.0F;

            for ( int n = 0; n < lut[0].length; n++ ) {
                int w = componentWidth - (int)((float)n*slope);
                int v = lut[0].length - n - 1;
                int red   = lut[0][v]&0xFF;
                int green = lut[1][v]&0xFF;
                int blue  = lut[2][v]&0xFF;
                g.setColor(new Color(red, green, blue));
                g.fillRect(0, 0, w, componentHeight);
            }
        } else if ( direction == SwingConstants.VERTICAL ) {
            float slope = (float)componentHeight / 256.0F;

            for ( int n = 0; n < lut[0].length; n++ ) {
                int h = componentHeight - (int)((float)n*slope);
                int red   = lut[0][n]&0xFF;
                int green = lut[1][n]&0xFF;
                int blue  = lut[2][n]&0xFF;
                g.setColor(new Color(red, green, blue));
                g.fillRect(0, 0, componentWidth, h);
            }
        }
    }
}
