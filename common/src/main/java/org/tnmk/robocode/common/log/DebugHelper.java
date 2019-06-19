package org.tnmk.robocode.common.log;

import java.awt.Color;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;

public class DebugHelper {

    public static void debugMoveRandomTowardEnemy(AdvancedRobot robot) {
        robot.setBodyColor(Color.RED);
    }

    public static void debugMoveRandomFarAwayEnemy(AdvancedRobot robot) {
        robot.setBodyColor(Color.WHITE);
    }

    public static void debugMoveRandomPerpendicularEnemy(AdvancedRobot robot) {
        robot.setBodyColor(HiTechDecorator.ROBOT_BORDY_COLOR);
    }

    public static boolean isDebugMoveStrategy() {
        return false;
    }

    public static boolean isDebugGunStrategy() {
        return false;
    }

    public static boolean isDebugMoveDirection() {
        return false;
    }
}
