package org.tnmk.robocode.common.log;

import java.awt.Color;
import org.tnmk.robocode.common.movement.MovementContext;
import robocode.AdvancedRobot;

public class DebugHelper {

    public static boolean isDebugMoveStrategy() {
        return false;
    }

    public static boolean isDebugGunStrategy() {
        return false;
    }

    public static boolean isDebugMoveDirection() {
        return false;
    }

    private static boolean isDebugStateMoveStrategy() {
        return true;
    }

    public static void debugMoveRandomTowardEnemy(AdvancedRobot robot) {
        robot.setBodyColor(Color.RED);
    }

    public static void debugMoveRandomFarAwayEnemy(AdvancedRobot robot) {
        robot.setBodyColor(Color.YELLOW);
    }

    public static void debugMoveRandomPerpendicularEnemy(AdvancedRobot robot) {
        robot.setBodyColor(Color.WHITE);
    }

    public static void debugMoveWandering(AdvancedRobot robot) {
        robot.setBodyColor(Color.GRAY);
    }

    public static void debugStateMoveStrategy(AdvancedRobot robot, MovementContext movementContext) {
        if (isDebugStateMoveStrategy()) {
            LogHelper.logRobotMovement(robot, "moveStrategy: " + movementContext.getMoveStrategy());
        }
    }

}
