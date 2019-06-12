package org.tnmk.robocode.common.log;

import robocode.AdvancedRobot;
import robocode.Robot;

public class LogHelper {

    public static void logAdvanceRobot(AdvancedRobot robot, String message) {
        String finalMessage = String.format("[%s] " +
                "\t position {%.2f, %.2f}"+
//                        "\t velocity %.2f " +
//                        "\t distanceRemaining %.2f " +
//                        "\t heading %.2f" +
//                        "\t radarHeading %.2f" +
//                        "\t radarTurnRemaining %.2f" +
                        "\t gunTurnRemaining %.2f" +
                        "\t %s",
                robot.getTime(),
                robot.getX(), robot.getY(),
//                robot.getVelocity(),
//                robot.getDistanceRemaining(),
//                robot.getHeading(),
//                robot.getRadarHeading(),
//                robot.getRadarTurnRemaining(),
                robot.getGunTurnRemaining(),
                message);
        robot.out.println(finalMessage);
    }


    public static void logAdvanceRobot(AdvancedRobot robot, int loopIndex, String message) {
        String finalMessage = String.format("[%s] " +
                        "\t loop[%s] " +
                        "\t velocity %s " +
                        "\t distanceRemaining %s " +
                        "\t %s",
                robot.getTime(), loopIndex, robot.getVelocity(), robot.getDistanceRemaining(), message);
        robot.out.println(finalMessage);
    }

    public static void logRobot(Robot robot, int loopIndex, String message) {
        String finalMessage = String.format("[%s] " +
                        "\t loop[%s] " +
                        "\t velocity %s " +
                        "\t %s",
                robot.getTime(), loopIndex, robot.getVelocity(), message);
        robot.out.println(finalMessage);
    }
}
