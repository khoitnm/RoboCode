package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.common.collection.ListUtils;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPredictionHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;
import robocode.AdvancedRobot;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 * <br/>
 * This class try to predict enemy pattern.<br/>
 * View more at {@link EnemyMovePatternIdentifyHelper}
 */
public class PatternPredictionUtils {
    private static final int ENEMY_PREDICTION_TIMES = 3;

    /**
     * @param robot
     * @param enemyHistory
     * @param firePower
     * @param patternPredictionFunc we have 2 options for now:
     *                              <ul>
     *                              <li> {@link #predictEnemyBasedOnAllEnemyPotentialPositions(List, long, Rectangle2D)}</li>
     *                              <li> {@link #predictEnemyBasedOnAvgVelocityAndAvgHeadingDelta(List, long, Rectangle2D)}</li>
     *                              </ul>
     * @return
     */
    public static AimPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower, PatternPredictionFunction patternPredictionFunc) {
        EnemyPrediction enemyPrediction = null;
        List<Enemy> latestHistoryItems = enemyHistory.getLatestHistoryItems(5);
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());

        Rectangle2D enemyBoundaryMovementArea = BattleFieldUtils.constructBattleField(robot);

        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        long periodForTurningGun = 0;
        long periodForBulletToReachEnemy = 0;
        double gunTurnLeftRadian = 0;
        long timeWhenBulletReachEnemy = 0;
        long totalPeriodGun = 0;

        Point2D predictRobotPosition = currentRobotPosition;
        for (int i = 0; i < ENEMY_PREDICTION_TIMES; i++) {//this loop is used to improve the correctness of prediction.
            RobotPrediction robotPrediction = RobotPredictionHelper.predictPosition(periodForTurningGun, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
            predictRobotPosition = robotPrediction.getPosition();
//            String message = String.format("Predict at time %s, position {%.2f, %.2f}", (robot.getTime() + periodForTurningGun), predictRobotPosition.getX(), predictRobotPosition.getY());
//            LogHelper.logRobotMovement(robot, message);

            double distanceRobotToEnemy = predictRobotPosition.distance(enemyPosition);
            double bulletVelocity = GunUtils.reckonBulletVelocity(firePower);
            periodForBulletToReachEnemy = (long) Math.ceil(Math.abs(distanceRobotToEnemy / bulletVelocity));
            gunTurnLeftRadian = GunUtils.reckonTurnGunLeftNormRadian(predictRobotPosition, enemyPosition, robot.getGunHeadingRadians());
            periodForTurningGun = GunUtils.reckonTicksToTurnGun(gunTurnLeftRadian);
            totalPeriodGun = periodForTurningGun + periodForBulletToReachEnemy;
            timeWhenBulletReachEnemy = robot.getTime() + Math.round(totalPeriodGun);

            enemyPrediction = patternPredictionFunc.predictEnemy(latestHistoryItems, timeWhenBulletReachEnemy, enemyBoundaryMovementArea);
            enemyPosition = enemyPrediction.getPredictionPosition();
        }
        if (enemyPrediction == null) {
            throw new IllegalStateException("Enemy Prediction should never be null");
        }
        debugPredictEnemy(robot, latestHistoryItems, enemyBoundaryMovementArea);

//        String message = String.format("Final predict enemy at %s, position {%.2f, %.2f}", enemyPrediction.getTime(), enemyPrediction.getPosition().getX(), enemyPrediction.getPosition().getY());
//        LogHelper.logRobotMovement(robot, message);
        return new AimPrediction(enemyPrediction, predictRobotPosition, gunTurnLeftRadian, firePower, robot.getTime(), timeWhenBulletReachEnemy, totalPeriodGun, periodForTurningGun, periodForBulletToReachEnemy);
    }

    /**
     * @param historyItems      must be not empty
     * @param predictionTime    when is the time that we think the bullet will reach the target.
     * @param enemyMovementArea the area enemy always moving inside. It never move to outside this area (usually the battle field).
     * @return guess new enemy's position and also identify pattern at the predictionTime.
     */
    public static EnemyPrediction predictEnemyBasedOnAvgVelocityAndAvgHeadingDelta(List<Enemy> historyItems, long predictionTime, Rectangle2D enemyMovementArea) {
        Enemy enemy = historyItems.get(0);
        double avgHeadingChangeRateRadian = EnemyHistoryUtils.averageChangeHeadingRadian(historyItems);
        double avgVelocity = EnemyHistoryUtils.averageVelocity(historyItems);
        return PatternPredictionUtils.predictEnemy(enemy, avgVelocity, avgHeadingChangeRateRadian, predictionTime, enemyMovementArea);
    }

    /**
     * @param enemy              latest data in history
     * @param headingChangeRateRadian changing heading of the enemy per tick based on the recent history items.
     * @param predictionTime     the time that we think the bullet will reach the target.
     * @param velocity           the velocity of enemy
     * @return guess new enemy's position and moving pattern at the predictionTime based on the latest enemy data and average changing heading.
     */
    public static EnemyPrediction predictEnemy(Enemy enemy, double velocity, double headingChangeRateRadian, long predictionTime, Rectangle2D enemyMovementArea) {
        double diff = predictionTime - enemy.getTime();
        double newX, newY;

        EnemyMovePattern enemyMovePattern;
        /**if there is a significant change in heading, use circular path prediction**/
        double enemyHeadingRadian = AngleUtils.toRadian(enemy.getHeading());
        if (Math.abs(headingChangeRateRadian) > 0.00001) {
            enemyMovePattern = EnemyMovePattern.CIRCULAR;
            double radius = velocity / headingChangeRateRadian;
            double totalHeadingChangeRadian = diff * headingChangeRateRadian;
            newY = enemy.getPosition().getY() +
                    Math.sin(enemyHeadingRadian + totalHeadingChangeRadian) * radius -
                    Math.sin(enemyHeadingRadian) * radius
            ;
            newX = enemy.getPosition().getX() + (Math.cos(enemyHeadingRadian) * radius) - (Math.cos(enemyHeadingRadian + totalHeadingChangeRadian) * radius);
        }
        /**if the change in heading is insignificant, use linear path prediction**/
        else {
            if (velocity < 1) {
                enemyMovePattern = EnemyMovePattern.STAY_STILL;
                newY = enemy.getPosition().getY();
                newX = enemy.getPosition().getX();
            } else {
                enemyMovePattern = EnemyMovePattern.LINEAR;
                newY = enemy.getPosition().getY() + Math.cos(enemyHeadingRadian) * enemy.getVelocity() * diff;
                newX = enemy.getPosition().getX() + Math.sin(enemyHeadingRadian) * enemy.getVelocity() * diff;
            }
        }
        Point2D predictionPosition = new Point2D.Double(newX, newY);
        predictionPosition = Move2DUtils.reckonMaximumDestination(enemy.getPosition(), predictionPosition, enemyMovementArea);
        debugPredictionPositionOutsideBattleField(enemy, predictionPosition, enemyMovementArea);
        EnemyPrediction patternPredictionResult = new EnemyPrediction(enemyMovePattern, predictionTime, predictionPosition, headingChangeRateRadian, velocity);
        return patternPredictionResult;
    }

    /**
     * TODO the logic is quite duplicated to {@link #predictEnemy(Enemy, double, double, long, Rectangle2D)}
     * @param enemyPosition
     * @param enemyHeadingRadian
     * @param velocity
     * @param headingChangeRateRadian
     * @param ticks
     * @param enemyMovementArea
     * @return
     */
    public static Point2D predictPosition(Point2D enemyPosition, double enemyHeadingRadian, double velocity, double headingChangeRateRadian, long ticks, Rectangle2D enemyMovementArea) {
        double newX, newY;

        EnemyMovePattern enemyMovePattern;
        /**if there is a significant change in heading, use circular path prediction**/
        if (Math.abs(headingChangeRateRadian) > 0.00001) {
            enemyMovePattern = EnemyMovePattern.CIRCULAR;
            double radius = velocity / headingChangeRateRadian;
            double totalHeadingChangeRadian = ticks * headingChangeRateRadian;
            newY = enemyPosition.getY() +
                    Math.sin(enemyHeadingRadian + totalHeadingChangeRadian) * radius -
                    Math.sin(enemyHeadingRadian) * radius
            ;
            newX = enemyPosition.getX() + (Math.cos(enemyHeadingRadian) * radius) - (Math.cos(enemyHeadingRadian + totalHeadingChangeRadian) * radius);
        }
        /**if the change in heading is insignificant, use linear path prediction**/
        else {
            if (velocity < 1) {
                enemyMovePattern = EnemyMovePattern.STAY_STILL;
                newY = enemyPosition.getY();
                newX = enemyPosition.getX();
            } else {
                enemyMovePattern = EnemyMovePattern.LINEAR;
                newY = enemyPosition.getY() + Math.cos(enemyHeadingRadian) * velocity * ticks;
                newX = enemyPosition.getX() + Math.sin(enemyHeadingRadian) * velocity * ticks;
            }
        }
        Point2D predictionPosition = new Point2D.Double(newX, newY);
        predictionPosition = Move2DUtils.reckonMaximumDestination(enemyPosition, predictionPosition, enemyMovementArea);
        return predictionPosition;
    }

    private static void debugPredictionPositionOutsideBattleField(Enemy enemy, Point2D predictionPosition, Rectangle2D enemyMovementArea) {
        if (!GeoMathUtils.checkInsideRectangle(predictionPosition, enemyMovementArea)) {
            String message = String.format("This case should never happens. Predict position's outside battle field: from:" + LogHelper.toString(enemy.getPosition()) + ", to:" + LogHelper.toString(predictionPosition) + ", area:" + LogHelper.toString(enemyMovementArea));
            System.out.println(message);
        }
    }

    private static void debugPredictSelfRobot(AdvancedRobot robot) {
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());
        RobotPrediction testRobotPredictionAfter5 = RobotPredictionHelper.predictPosition(5, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
        String message = String.format("Predict self at time %s, position {%.2f, %.2f}", (robot.getTime() + 5), testRobotPredictionAfter5.getPosition().getX(), testRobotPredictionAfter5.getPosition().getY());
        LogHelper.logRobotMovement(robot, message);
    }

    private static void debugPredictEnemy(AdvancedRobot robot, List<Enemy> latestHistoryItems, Rectangle2D enemyMovementArea) {
        for (int i = 0; i < 5; i++) {
            EnemyPrediction enemyPrediction = PatternPredictionUtils.predictEnemyBasedOnAvgVelocityAndAvgHeadingDelta(latestHistoryItems, robot.getTime() + i, enemyMovementArea);
            Point2D testEnemyPosition = enemyPrediction.getPredictionPosition();
            String message = String.format("predict enemy at time %s, position {%.2f, %.2f}", robot.getTime() + i, testEnemyPosition.getX(), testEnemyPosition.getY());
            LogHelper.logRobotMovement(robot, message);
        }
    }

}
