package org.tnmk.common.number;

public class DoubleUtils {

    private static final double COMPARE_DOUBLE_MARGIN = 0.001;

    public static boolean isNearlyZero(double a) {
        return isNearlyEquals(a, 0);
    }

    public static boolean isNearlyEquals(double a, double b) {
        return a - b < COMPARE_DOUBLE_MARGIN || b - a < COMPARE_DOUBLE_MARGIN;
    }
}
