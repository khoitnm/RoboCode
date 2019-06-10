package org.tnmk.common.math;

import java.awt.geom.Point2D;

public class Point2DUtils {
    public static Point2D plus(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
    }

    public static Point2D multiple(Point2D point, int length) {
        return new Point2D.Double(point.getX() * length, point.getY() * length);
    }
}
