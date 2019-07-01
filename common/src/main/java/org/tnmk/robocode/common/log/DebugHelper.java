package org.tnmk.robocode.common.log;

import java.awt.Color;
import java.util.stream.Collectors;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.movement.MoveAreaHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;

public class DebugHelper {

    public static boolean isDebugMoveStrategyChange() {
        return false;
    }

    private static boolean isDebugMoveStrategyState() {
        return false;
    }

    public static boolean isDebugGunStrategy() {
        return false;
    }

    public static boolean isDebugMoveDirection() {
        return false;
    }

    public static boolean isDebugRandomMovement() {
        return false;
    }

    private static boolean isDebugGunGFT() {
        return false;
    }

    private static boolean isDebugEnemyStatistic() {
        return false;
    }

    public static void debugMoveRandomTowardEnemy(AdvancedRobot robot) {
        if (isDebugRandomMovement()) {
            robot.setBodyColor(Color.RED);
        }
    }

    public static void debugMoveRandomFarAwayEnemy(AdvancedRobot robot) {
        if (isDebugRandomMovement()) {
            robot.setBodyColor(Color.YELLOW);
        }
    }

    public static void debugMoveRandomPerpendicularEnemy(AdvancedRobot robot) {
        if (isDebugRandomMovement()) {
            robot.setBodyColor(Color.WHITE);
        }
    }

    public static void debugMoveWandering(AdvancedRobot robot) {
        if (isDebugRandomMovement()) {
            robot.setBodyColor(Color.GRAY);
        }
    }

    public static void debugStateMoveStrategy(AdvancedRobot robot, MovementContext movementContext) {
        if (isDebugMoveStrategyState()) {
            LogHelper.logRobotMovement(robot, "moveStrategy: " + movementContext.getMoveStrategy());
        }
    }

    public static void debugEnemyEnergy(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, String enemyName, int historyCount) {
        if (isDebugRandomMovement()) {
            String energies = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName)
                    .getEnemyHistory()
                    .getLatestHistoryItems(historyCount).stream()
                    .map(enemy -> "" + enemy.getEnergy())
                    .collect(Collectors.joining(","));
            LogHelper.logRobotMovement(robot, "Enemies " + energies);
        }
    }

    public static void resetDebugMoveWandering(AdvancedRobot robot) {
        if (isDebugRandomMovement()) {
            robot.setBodyColor(HiTechDecorator.ROBOT_BORDY_COLOR);
        }
    }

    public static void debugGFTGunInRange(AdvancedRobot robot) {
        if (isDebugGunGFT()) {
            robot.setBodyColor(HiTechDecorator.ROBOT_BORDY_COLOR);
        }
    }

    public static void debugGFTGunNotInRange(AdvancedRobot robot) {
        if (isDebugGunGFT()) {
            robot.setBodyColor(Color.RED);
        }
    }

    public static void debugEnemyStatisticContext(AdvancedRobot robot, String enemyName, EnemyStatisticContext enemyStatisticContext) {
        if (isDebugEnemyStatistic()) {
            LogHelper.logSimple(robot, "Enemy: " + enemyName
                    + "\n\t\t Pattern: " + enemyStatisticContext.getPatternIdentification()
                    + "\n\t\t predictionHistory: \t" + enemyStatisticContext.getEnemyPredictionHistory().getAllHistoryItemsIterable()
            );
        }
    }

    public static void debugMovingTooLong(AdvancedRobot robot, MoveAreaHelper.MoveAreaTooLongResult moveAreaTooLongResult) {
        if (isDebugOneAreaTooLong()) {
            LogHelper.logSimple(robot, "Moving too long in one area " + LogHelper.toString(moveAreaTooLongResult.getMoveArea()));
            PaintHelper.paintRectangle(robot.getGraphics(), moveAreaTooLongResult.getMoveArea(), Color.RED);
        }
    }

    private static boolean isDebugOneAreaTooLong() {
        return true;
    }
}
