package org.tnmk.robocode.common.helper;

public class TimeUtils {

    public static long toTicks(double timePeriod) {
        return (long) Math.ceil(timePeriod);
    }
}
