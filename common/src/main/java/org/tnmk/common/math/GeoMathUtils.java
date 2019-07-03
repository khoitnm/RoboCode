package org.tnmk.common.math;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.Line2D;

//import math.geom2d.Point2D;

//import math.geom2d.Point2D;

public final class GeoMathUtils {
    private GeoMathUtils() {
    }

    /**
     * @param value
     * @return For any value, return the sign of that value to indicate whether the value is positive or negative.<br/>
     * If the value is zero or positive, return 1.<br/>
     * Otherwise (negative), return -1<br/>
     */
    public static int sign(double value) {
        return value < 0 ? -1 : 1;
    }

    /**
     * @param linearSpeed  speed of linear movement, positive number.
     * @param angularSpeed speed of angular movement (direction turning speed), in degree, positive number
     * @return radius of movement follow circle sharp.
     * See more at http://www.efm.leeds.ac.uk/CIVE/CIVE1140/section01/linear_angular_motion.html
     */
    public static double reckonMovementRadius(double linearSpeed, double angularSpeed) {
        return linearSpeed / Math.toRadians(angularSpeed);
    }

    /**
     * @param pA
     * @param pB
     * @return angle of line pA-pB, in degree, always positive (0 -> 360), with Y-axis
     */
    public static double reckonAngle(Point pA, Point pB) {
        double angle = Math.toDegrees(Math.atan2(pB.x - pA.x, pB.y - pA.y));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * @param pA
     * @param pB
     * @return the same as {@link #reckonAngle(Point, Point)}. But the value is from -180 to 180
     */
    public static double reckonNormalizeAngle(math.geom2d.Point2D pA, math.geom2d.Point2D pB) {
        double angle = Math.toDegrees(Math.atan2(pB.x() - pA.x(), pB.y() - pA.y()));
        double normalizeAngle = AngleUtils.normalizeDegree(angle);
        return normalizeAngle;
    }

    public static double distance(LineSegment lineSegment, Point point) {
        Line2D l2D = MathConverter.toLine2D(lineSegment);
        math.geom2d.Point2D p2D = MathConverter.toPoint2D(point);
        return l2D.distance(p2D);
    }

    /**
     * http://2000clicks.com/mathhelp/GeometryConicSectionCircleIntersection.aspx
     *
     * @return
     */
    public static List<Point> intersectCircles(Circle cirA, Circle cirB) {
        Circle2D cir2DA = MathConverter.toCircle2D(cirA);
        Circle2D cir2DB = MathConverter.toCircle2D(cirB);
        Collection<math.geom2d.Point2D> intersections = cir2DA.intersections(cir2DB);
        return MathConverter.toPoints(intersections);
    }

    public static double calculateTurnRightDirectionToTarget(double currentAbsMoveAngle, Point pA, Point pB) {
        return calculateTurnRightDirectionToTarget(currentAbsMoveAngle, pA.getX(), pA.getY(), pB.getX(), pB.getY());
    }

    public static double calculateTurnRightDirectionToTarget(double currentAbsMoveAngle, double currentX, double currentY, double targetX, double targetY) {
        double absBearingToTarget = GeoMathUtils.absoluteBearing(currentX, currentY, targetX, targetY);
        double relativeBearing = absBearingToTarget - currentAbsMoveAngle;
        return AngleUtils.normalizeDegree(relativeBearing);
    }

    public static double distance(Point pA, Point pB) {
        return distance(pA.x, pA.y, pB.x, pB.y);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Calculate the angle from point01 to point02
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return positive angle degree from point01 to point02, compare to root (0 -> 360)
     * (North axis)
     */
    public static double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = math.geom2d.Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 -
            // ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 +
            // ang
        }

        return bearing;
    }

    public static final int POINT_CLOSE_DISTANCE = 2;//2 pixel

    public static boolean close(Point pointA, Point pointB) {
        return Math.abs(pointA.getX() - pointB.getX()) < POINT_CLOSE_DISTANCE && Math.abs(pointA.getY() - pointB.getY()) < POINT_CLOSE_DISTANCE;
    }

    public static boolean checkInsideRectangle(java.awt.geom.Point2D point2D, Rectangle2D rectangle2D) {
        boolean insideX = point2D.getX() >= rectangle2D.getMinX() && point2D.getX() <= rectangle2D.getMaxX();
        boolean insideY = point2D.getY() >= rectangle2D.getMinY() && point2D.getY() <= rectangle2D.getMaxY();
        return insideX && insideY;
    }

    /**
     * "Looks like Axe trapped himself over again! Because of this peculiarity of ClockMath I recommend you to implement these two functions in all your bots."
     * http://old.robowiki.net/robowiki?MinimumRiskMovement
     * @param source
     * @param target
     * @return
     */
//    static double absoluteBearing(Point2D source, Point2D target) {
//        return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
//    }

//    /**
//     * "Looks like Axe trapped himself over again! Because of this peculiarity of ClockMath I recommend you to implement these two functions in all your bots."
//     * http://old.robowiki.net/robowiki?MinimumRiskMovement
//     * @param sourceLocation
//     * @param angle
//     * @param length
//     * @return
//     */
//    public static Point2D vectorToLocation(Point2D sourceLocation, double angle, double length) {
//        return new Point2D(sourceLocation.getX() + Math.sin(angle) * length,
//                sourceLocation.getY() + Math.cos(angle) * length);
//    }

    /**
     * @param rootPosition
     * @param radian       the real geometry radian, not in-game radian.
     * @param distance
     */
    public static Point2D calculateDestinationPoint(Point2D rootPosition, double radian, double distance) {
        Point2D point2D = new Point2D.Double(rootPosition.getX() + Math.cos(radian) * distance, rootPosition.getY() + Math.sin(radian) * distance);
        return point2D;
    }

    /**
     * @param pointA
     * @param pointB
     * @param xC     x of PointC which is on the same line of PointA -> PointB
     * @return yC (y of PointC)
     */
    public static double calculateYOfPointCOnTheSameLine(Point2D pointA, Point2D pointB, double xC) {
        double yC = ((xC - pointA.getX()) / (pointB.getX() - pointA.getX()) * (pointB.getY() - pointA.getY())) + pointA.getY();
        return yC;
    }

    /**
     * @param pointA
     * @param pointB
     * @param yC     y of PointC which is on the same line of PointA -> PointB
     * @return xC (x of PointC)
     */
    public static double calculateXOfPointCOnTheSameLine(Point2D pointA, Point2D pointB, double yC) {
        double xC = ((yC - pointA.getY()) / (pointB.getY() - pointA.getY()) * (pointB.getX() - pointA.getX())) + pointA.getX();
        return xC;
    }

    public static double calculateDiagonal(Rectangle2D rectangle){
        double diagonal = Math.sqrt(Math.pow(rectangle.getWidth(), 2)+Math.pow(rectangle.getHeight(), 2));
        return diagonal;
    }

    public static Point2D reckonCenter(Rectangle2D rectangle2D) {
        double x = rectangle2D.getMinX() + rectangle2D.getWidth() / 2;
        double y = rectangle2D.getMinY() + rectangle2D.getHeight() / 2;
        return new Point2D.Double(x, y);
    }
}
