package org.tnmk.common.math;

import java.awt.geom.Point2D;

public class Point2DUtils {
    public static Point2D plus(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
    }

    public static Point2D multiple(Point2D point, double length) {
        return new Point2D.Double(point.getX() * length, point.getY() * length);
    }

    /**
     * @param pA
     * @param pB
     * @return the same as {@link GeoMathUtils#reckonAngle(Point, Point)} and {@link #reckonPositiveAngle(Point2D, Point2D)}. But the value is from -180 to 180
     */
    public static double reckonNormalizeAngle(Point2D pA, Point2D pB) {
        double angle = Math.toDegrees(Math.atan2(pB.getX() - pA.getX(), pB.getY() - pA.getY()));
        double normalizeAngle = AngleUtils.normalizeDegree(angle);
        return normalizeAngle;
    }

    public static double reckonPositiveAngle(Point2D pA, Point2D pB) {
        double angle = Math.toDegrees(Math.atan2(pB.getX() - pA.getX(), pB.getY() - pA.getY()));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }
}
