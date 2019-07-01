package org.tnmk.robocode.common.log;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.tnmk.common.properties.PropertiesReader;
import robocode.AdvancedRobot;
import robocode.Robot;

public class LogHelper {
    public static String toString(Point2D point2D) {
        return String.format("{%.2f, %.2f}", point2D.getX(), point2D.getY());
    }

    public static String toString(Rectangle2D rectangle2D) {
        return String.format("{%.2f, %.2f} -> {%.2f, %.2f}", rectangle2D.getMinX(), rectangle2D.getMinY(), rectangle2D.getMaxX(), rectangle2D.getMaxY());
    }

    public static void logSimple(AdvancedRobot robot, String message) {
        String finalMessage = String.format("[%s] " +
//                        "\t position {%.2f, %.2f}" +
//                        "\t velocity %.2f " +
//                        "\t distanceRemaining %.2f " +
//                        "\t heading %.2f" +
//                        "\t radarHeading %.2f" +
//                        "\t radarTurnRemaining %.2f" +
//                        "\t gunTurnRemaining %.2f" +
                        "\t %s",
                robot.getTime(),
//                robot.getX(), robot.getY(),
//                robot.getVelocity(),
//                robot.getDistanceRemaining(),
//                robot.getHeading(),
//                robot.getRadarHeading(),
//                robot.getRadarTurnRemaining(),
//                robot.getGunTurnRemaining(),
                message);
        robot.out.println(finalMessage);
    }

    public static void logRobotMovement(AdvancedRobot robot, String message) {
        String finalMessage = String.format("[%s] " +
                        "\t position {%.2f, %.2f}" +
                        "\t velocity %.2f " +
                        "\t distanceRemaining %.2f " +
                        "\t heading %.2f" +
                        "\t %s",
                robot.getTime(),
                robot.getX(), robot.getY(),
                robot.getVelocity(),
                robot.getDistanceRemaining(),
                robot.getHeading(),
                message);
        robot.out.println(finalMessage);
    }

    public static void logRobotRadar(AdvancedRobot robot, String message) {
        String finalMessage = String.format("[%s] " +
//                        "\t position {%.2f, %.2f}" +
                        "\t radarHeading %.2f" +
                        "\t radarTurnRemaining %.2f" +
//                        "\t gunTurnRemaining %.2f" +
                        "\t %s",
                robot.getTime(),
//                robot.getX(), robot.getY(),
                robot.getRadarHeading(),
                robot.getRadarTurnRemaining(),
//                robot.getGunTurnRemaining(),
                message);
        robot.out.println(finalMessage);
    }

    public static void logRobotMovement(AdvancedRobot robot, int loopIndex, String message) {
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

    public static void logPosition(AdvancedRobot robot, String message) {
        String finalMessage = String.format("[%s] " +
                        "\t position {%.2f, %.2f}" +
                        "\t %s",
                robot.getTime(),
                robot.getX(), robot.getY(),
                message);
        robot.out.println(finalMessage);
    }

    public static String appendGitInfo(String message) {
        return String.format("%s\t %s\t %s\t %s", message, getBuildNumber(), getGitRevision(), getGitTag());
    }

    private static Object getGitTag() {
        return getProjectPropertyIfExist("git.tag");
    }

    private static String getBuildNumber() {
        return getProjectPropertyIfExist("git.build.number");
    }

    private static String getGitRevision() {
        return getProjectPropertyIfExist("git.revision");
    }

    private static String getProjectPropertyIfExist(String property) {
        if (PropertiesReader.PROJECT_PROPERTIES.isPresent()) {
            return PropertiesReader.PROJECT_PROPERTIES.get().getProperty(property);
        } else {
            return "";
        }
    }
}
