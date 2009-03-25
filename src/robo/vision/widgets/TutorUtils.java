package robo.vision.widgets;
/*
 * @(#)TutorUtils.java	1.4 99/06/19 00:17:59
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;


public class TutorUtils {

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

// MUST check color model here
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
}
