package robo.vision.widgets;
/*
 * @(#)Iconizer.java	1.5 99/06/19 00:17:56
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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.Icon;

/**
 * A class to create icons from Planar Images
 *
 * @author Dennis Sigel
 */

public class Iconizer implements Icon {

    protected int width  = 64;
    protected int height = 64;
    protected BufferedImage icon = null;

   /**
    * Default constructor
    */
    public Iconizer() {
    }

   /**
     * @param source a PlanarImage to be displayed.
     * @param width is the icon width
     * @param height is the icon height
     */
    public Iconizer(PlanarImage image, int width, int height) {
        this.width  = width;
        this.height = height;

        icon = iconify(image);
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

    /**
     * Paint the icon
     */
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {

        Graphics2D g2D = null;
        if (g instanceof Graphics2D) {
            g2D = (Graphics2D)g;
        } else {
            return;
        }

        AffineTransform transform = AffineTransform.getTranslateInstance(0,0);
        g2D.drawRenderedImage(icon, transform);
    }

    private BufferedImage iconify(PlanarImage image) {
        float scale = 1.0F;

        float s1 = (float)width / (float)image.getWidth();
        float s2 = (float)height / (float)image.getHeight();

        if ( s1 > s2 ) {
            scale = s1;
        } else {
            scale = s2;
        }

        InterpolationBilinear interp = new InterpolationBilinear();

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(scale);
		pb.add(scale);
		pb.add(0.0F);
		pb.add(0.0F);
		pb.add(interp);
		/*PlanarImage temp = JAI.create("scale",
										scale,
										scale,
										0.0F,
										0.0F,
                                       interp);*/
        PlanarImage temp = JAI.create("scale",
                                       pb);

        return temp.getAsBufferedImage();
    }

    public void save(String filename, String format) {
        JAI.create("filestore", icon, filename, format, null);
    }
}
