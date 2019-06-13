package org.tnmk.common.math;

import static java.lang.Math.PI;

public class AngleUtils {
    /**
     * A non-normalized bearing could be smaller than -180 or larger than 180.
     * We like to work with normalized bearings because they make for more
     * efficient movement. To normalize a bearing, use the following function:
     *
     * @param angleDegree angle in degree
     * @return normalized angle (-180 to 180)
     */
    public static double normalizeDegree(double angleDegree) {
        while (angleDegree > 180) {
            angleDegree -= 360;
        }
        while (angleDegree < -180) {
            angleDegree += 360;
        }
        return angleDegree;
    }

    public static double normalizeRadian(double angleRadian) {
        while (angleRadian > PI) {
            angleRadian -= 2 * PI;
        }
        while (angleRadian < -PI) {
            angleRadian += 2 * PI;
        }
        return angleRadian;
    }

    public static double toRadian(double angleDegree) {
        double radian = angleDegree * (2 * PI) / 360;
        return radian;
    }

    public static double toDegree(double angleRadian) {
        double degree = angleRadian * 360 / (2 * PI);
        return degree;
    }

    public static double reverseDegree(double degree){
        return normalizeDegree(degree + 180);
    }

    public static double reverseRadian(double radian){
        return normalizeRadian(radian + PI);
    }
}
