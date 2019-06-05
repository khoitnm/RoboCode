package org.tnmk.common.math;

import java.util.Collection;
import java.util.List;

import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.Line2D;

public final class MathUtils {
	private MathUtils() {
	}
	/**
	 * @see http://www.efm.leeds.ac.uk/CIVE/CIVE1140/section01/linear_angular_motion.html
	 * @param linearSpeed speed of linear movement, positive number.
	 * @param angularSpeed speed of angular movement (direction turning speed), in degree, positive number
	 * @return radius of movement follow circle sharp.
	 */
	public static double reckonMovementRadius(double linearSpeed, double angularSpeed){
		return linearSpeed/Math.toRadians(angularSpeed);
	}
	/**
	 * @param pA
	 * @param pB
	 * @return angle of line pA-pB, in degree, always positive, with Y-axis
	 */
	public static double reckonAngle(Point pA,Point pB) {
	    double angle = Math.toDegrees(Math.atan2(pB.x - pA.x, pB.y - pA.y));
	    if(angle < 0){
	        angle += 360;
	    }
	    return angle;
	}

	public static double distance(LineSegment lineSegment, Point point){
		Line2D l2D = MathConverter.toLine2D(lineSegment);
		Point2D p2D = MathConverter.toPoint2D(point);		
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
		Collection<Point2D> intersections = cir2DA.intersections(cir2DB);
		return MathConverter.toPoints(intersections);
	}
	public static double calculateTurnRightDirectionToTarget(double currentAbsMoveAngle, Point pA, Point pB) {
		return calculateTurnRightDirectionToTarget(currentAbsMoveAngle, pA.getX(), pA.getY(), pB.getX(), pB.getY());
	}
	public static double calculateTurnRightDirectionToTarget(double currentAbsMoveAngle, double currentX, double currentY, double targetX, double targetY) {
		double absBearingToTarget = MathUtils.absoluteBearing(currentX, currentY, targetX, targetY);
		double relativeBearing = absBearingToTarget - currentAbsMoveAngle;
		return MathUtils.normalizeDegree(relativeBearing);
	}

	/**
	 * A non-normalized bearing could be smaller than -180 or larger than 180.
	 * We like to work with normalized bearings because they make for more
	 * efficient movement. To normalize a bearing, use the following function:
	 * 
	 * @param angleDegree
	 *            angle in degree
	 * @return
	 */
	public static double normalizeDegree(double angleDegree) {
		while (angleDegree > 180)
			angleDegree -= 360;
		while (angleDegree < -180)
			angleDegree += 360;
		return angleDegree;
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
	 * @return positive angle degree from point01 to point02, compare to root
	 *         (North axis)
	 */
	public static double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
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
}
