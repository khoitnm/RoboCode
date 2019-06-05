package org.tnmk.robocode.common.log;

import robocode.Robot;

public class LogHelper {

    public static void logRobot(Robot robot, int loopIndex, String message) {
        String finalMessage = String.format("[%s] \t loop[%s] \t velocity %s \t %s", robot.getTime(), loopIndex, robot.getVelocity(), message);
        robot.out.println(finalMessage);
    }
}
