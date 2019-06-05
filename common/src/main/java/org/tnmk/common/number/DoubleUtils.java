package org.tnmk.common.number;

public class DoubleUtils {

    private static final double COMPARE_DOUBLE_EPSILON = 0.001;

    /**
     * View {@link #isConsideredEqual(double, double)}
     *
     * @param a
     * @return
     */
    public static boolean isConsideredZero(double a) {
        return isConsideredEqual(a, 0d);
    }

    /**
     * Returns true if two doubles are considered equal.<br/>
     * Check if the absolute difference between two doubles has a difference less then {@link #COMPARE_DOUBLE_EPSILON}.
     * <p/>
     * The reason why we need this method is some case when calculation, the double values become not precisely adjusted.<br/>
     * For example:<br/>
     * In the beginning, you have input=5.1.<br/>
     * After some calculation, you expect return value of someCalculation(input) is 6.1,
     * but the actual result could be 6.100000000001 (that's how Java handle double number, unfortunately).<br/>
     * And you still want to consider that actual result is correct.
     * Then you'll need this method to compare values.
     *
     * @param a double to compare.
     * @param b double to compare.
     * @return true if two doubles are considered equal.
     */
    public static boolean isConsideredEqual(double a, double b) {
        return a == b ? true : Math.abs(a - b) < COMPARE_DOUBLE_EPSILON;
    }
}
