package org.tnmk.common.math;

/**
 * View images in this link to understand the definition.
 * https://owlcation.com/stem/How-to-Calculate-the-Arc-Length-of-a-Circle-Segment-and-Sector-Area
 */
public class CircleMathUtils {
    public static final double CIRCLE_ANGLE_RADIANS = 2 * Math.PI;

    /**
     * @param angleRadians
     * @param radius
     * @return
     */
    public static double calculateArc(double angleRadians, double radius) {
        return angleRadians * radius;
    }

    public static double calculateCircumference(double radius) {
        return calculateArc(CIRCLE_ANGLE_RADIANS, radius);
    }

    public static double calculateSectorArea(double angleRadians, double radius) {
        return angleRadians * Math.pow(radius, 2) / 2;
    }

    /**
     * When something moves in a circle, it's turn rate decides how long it will finish that circle, not it's velocity.
     * @param turnRateRadians
     * @return
     */
    public static double calculateTimeToFinishCircle(double turnRateRadians) {
        return CIRCLE_ANGLE_RADIANS / turnRateRadians;
    }

}
