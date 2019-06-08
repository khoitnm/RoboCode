package org.tnmk.robocode.common.movement.edm;

import java.awt.geom.Point2D;

/**
 * Class to represent Point in EDM method. Extends Point2D.Double and adds field <code>avgDistance</code>,
 * which means averegene distance to enemies in EDM 
 *
 * @author jdev
 */
public class EDMPoint extends Point2D.Double {
    public double avgDistance;
    public EDMPoint(double x, double y) {
        super(x, y);
    }
}