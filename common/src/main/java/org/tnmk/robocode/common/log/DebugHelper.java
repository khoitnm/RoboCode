package org.tnmk.robocode.common.log;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.model.enemy.PatternIdentification;
import org.tnmk.robocode.common.movement.MoveAreaHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.strategy.antigravity.RiskArea;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.stream.Collectors;

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
//            LogHelper.logPosition(robot, "Moving too long in one area " + LogHelper.toString(moveAreaTooLongResult.getMoveArea()));
            PaintHelper.paintRectangle(robot.getGraphics(), moveAreaTooLongResult.getMoveArea(), Color.RED);
        }
    }

    public static boolean isDebugOneAreaTooLong() {
        return true;
    }

    public static void debugLeastRiskAreas(AdvancedRobot robot, List<RiskArea> leastRiskAreas) {
        if (!isDebugOneAreaTooLong()) {
            return;
        }
        for (RiskArea leastRiskArea : leastRiskAreas) {
            PaintHelper.paintRectangle(robot.getGraphics(), leastRiskArea.getArea(), Color.GREEN);
        }
    }

    public static void debugMoveArea(Graphics2D graphics, Rectangle2D moveArea, double actualDiagonal) {
        if (DebugHelper.isDebugOneAreaTooLong()) {
            PaintHelper.paintRectangle(graphics, moveArea, Color.ORANGE, "" + actualDiagonal);
        }
    }

    public static void debugClosestRiskArea(AdvancedRobot robot, RiskArea closestRiskArea) {
        if (DebugHelper.isDebugOneAreaTooLong()) {
            PaintHelper.paintRectangle(robot.getGraphics(), closestRiskArea.getArea(), Color.magenta, "");
        }
    }

    private static boolean isDebugPatternPredictionGun() {
        return false;
    }

    public static void debug_PatternPredictionGun_TurnGun(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext, GunStateContext gunStateContext, PatternIdentification patternIdentification, EnemyPrediction enemyPrediction, EnemyPredictionHistory enemyPredictionHistory) {
        if (DebugHelper.isDebugPatternPredictionGun()) {
            LogHelper.logSimple(robot, "AimGun(YES): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
                    "\n\tidentifiedPattern: " + patternIdentification +
                    "\n\tnewPrediction: " + enemyPrediction +
                    "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
            );
        }
    }

    public static void debug_PatternPredictionGun_DontTurnGun(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext, GunStateContext gunStateContext, PatternIdentification patternIdentification, EnemyPrediction enemyPrediction, EnemyPredictionHistory enemyPredictionHistory) {
        if (DebugHelper.isDebugPatternPredictionGun()) {
            LogHelper.logSimple(robot, "AimGun(NO): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
                    "\n\tidentifiedPattern: " + patternIdentification +
                    "\n\tnewPrediction: " + enemyPrediction +
                    "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
            );
        }
    }

    public static void debug_PatternPredictionGun_predictionPattern(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext, EnemyPrediction enemyPrediction) {
        if (DebugHelper.isDebugPatternPredictionGun()) {
            LogHelper.logRobotMovement(robot, "Future prediction: Enemy name: " + enemyStatisticContext.getEnemyName() + ", predictionPattern: " + enemyPrediction.getEnemyMovePattern() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());
        }
    }
}
