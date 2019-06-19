package org.tnmk.robocode.common.log;

import java.awt.Color;
import java.util.stream.Collectors;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import robocode.AdvancedRobot;

public class DebugHelper {

    public static boolean isDebugMoveStrategyChange() {
        return true;
    }

    private static boolean isDebugMoveStrategyState() {
        return true;
    }

    public static boolean isDebugGunStrategy() {
        return false;
    }

    public static boolean isDebugMoveDirection() {
        return false;
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
        if (isDebugMoveStrategyState()) {
            LogHelper.logRobotMovement(robot, "moveStrategy: " + movementContext.getMoveStrategy());
        }
    }

    public static void debugEnemyEnergy(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, String enemyName, int historyCount) {
        String energies = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName)
                .getEnemyHistory()
                .getLatestHistoryItems(historyCount).stream()
                .map(enemy -> "" + enemy.getEnergy())
                .collect(Collectors.joining(","));
//        System.out.println("Enemies " + energies);
    }
}
