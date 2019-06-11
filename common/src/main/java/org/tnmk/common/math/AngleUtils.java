package org.tnmk.common.math;

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
        while (angleDegree > 180){
            angleDegree -= 360;
        }
        while (angleDegree < -180){
            angleDegree += 360;
        }
        return angleDegree;
    }
}
