package org.tnmk.robocode.common.log;

import robocode.Robot;

public class LogHelper {

    public static void logRobot(Robot robot, int loopIndex, String message) {
        String finalMessage = String.format("[%s] \t loop[%s] \t %s \t velocity %s", robot.getTime(), loopIndex, message, robot.getVelocity());
        robot.out.println(finalMessage);
    }
}
