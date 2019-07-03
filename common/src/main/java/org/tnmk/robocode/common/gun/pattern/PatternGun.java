package org.tnmk.robocode.common.gun.pattern;

import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPredictionHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class PatternGun implements LoopableRun, OnScannedRobotControl {
    private static final int ENEMY_PREDICTION_TIMES = 3;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final GunStateContext gunStateContext;

    public PatternGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, GunStateContext gunStateContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = gunStateContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        String enemyName = scannedRobotEvent.getName();
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
//        Enemy enemy = enemyStatisticContext.getEnemyHistory().getLatestHistoryItem();
        double bulletPower = 1;
        aimGun(robot, enemyStatisticContext, bulletPower);

    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyStatisticContext enemyStatisticContext, double bulletPower) {
        EnemyHistory enemyHistory = enemyStatisticContext.getEnemyHistory();

        EnemyPrediction enemyPrediction = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower);
//            LogHelper.logRobotMovement(robot, "Future prediction: Enemy name: " + enemyStatisticContext.getEnemyName() + ", predictionPattern: " + enemyPrediction.getEnemyMovePattern() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());

        /** No matter what is the prediction, always add it into predictionHistory.*/
        EnemyPredictionHistory enemyPredictionHistory = enemyStatisticContext.getEnemyPredictionHistory();
        enemyPredictionHistory.addToHistory(enemyPrediction);

        Point2D enemyPosition = enemyPrediction.getPredictionPosition();
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());

        /**Turn the gun to the correct angle**/
        double gunBearing = GunUtils.reckonTurnGunLeftNormRadian(robotPosition, enemyPosition, robot.getGunHeadingRadians());
        robot.setTurnGunLeftRadians(gunBearing);
        gunStateContext.saveSateAimGun(GunStrategy.PATTERN_PREDICTION, bulletPower, enemyHistory.getName());
//                LogHelper.logSimple(robot, "AimGun(YES): enemyName: " + enemyStatisticContext.getEnemyName() + ", gunStrategy: " + gunStateContext.getGunStrategy() +
//                        "\n\tidentifiedPattern: " + patternIdentification +
//                        "\n\tnewPrediction: " + enemyPrediction +
//                        "\n\tpredictionHistory: " + enemyPredictionHistory.getAllHistoryItems()
//                );

        /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/

    }


    private EnemyPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        EnemyPrediction enemyPrediction = null;
        List<Enemy> latestHistoryItems = enemyHistory.getLatestHistoryItems(5);
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());

        Rectangle2D battleField = BattleFieldUtils.constructBattleField(robot);
//        debugPredictSelfRobot(robot);

        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        long periodForTurningGun = 0;
        for (int i = 0; i < ENEMY_PREDICTION_TIMES; i++) {//this loop is used to improve the correctness of prediction.
            RobotPrediction robotPrediction = RobotPredictionHelper.predictPosition(periodForTurningGun, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
            Point2D predictRobotPosition = robotPrediction.getPosition();
//            String message = String.format("Predict at time %s, position {%.2f, %.2f}", (robot.getTime() + periodForTurningGun), predictRobotPosition.getX(), predictRobotPosition.getY());
//            LogHelper.logRobotMovement(robot, message);

            double distanceRobotToEnemy = predictRobotPosition.distance(enemyPosition);
            double bulletVelocity = GunUtils.reckonBulletVelocity(firePower);
            long periodForBulletToReachEnemy = (long) Math.ceil(Math.abs(distanceRobotToEnemy / bulletVelocity));

            double gunBearing = GunUtils.reckonTurnGunLeftNormRadian(predictRobotPosition, enemyPosition, robot.getGunHeadingRadians());
            periodForTurningGun = (long) Math.ceil(Math.abs(gunBearing / AngleUtils.toRadian(RobotPhysics.GUN_TURN_VELOCITY)));
            long totalPeriodGun = periodForTurningGun + periodForBulletToReachEnemy;
            long timeWhenBulletReachEnemy = robot.getTime() + Math.round(totalPeriodGun);

            enemyPrediction = PatternPredictionUtils.predictEnemy(latestHistoryItems, timeWhenBulletReachEnemy, battleField);
            enemyPosition = enemyPrediction.getPredictionPosition();
        }
        if (enemyPrediction == null) {
            throw new IllegalStateException("Enemy Prediction should never be null");
        }
//        debugPredictEnemy(latestHistoryItems);

//        String message = String.format("Final predict enemy at %s, position {%.2f, %.2f}", enemyPrediction.getTime(), enemyPrediction.getPosition().getX(), enemyPrediction.getPosition().getY());
//        LogHelper.logRobotMovement(robot, message);
        return enemyPrediction;
    }

    private void debugPredictSelfRobot(AdvancedRobot robot) {
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());
        RobotPrediction testRobotPredictionAfter5 = RobotPredictionHelper.predictPosition(5, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
        String message = String.format("Predict self at time %s, position {%.2f, %.2f}", (robot.getTime() + 5), testRobotPredictionAfter5.getPosition().getX(), testRobotPredictionAfter5.getPosition().getY());
        LogHelper.logRobotMovement(robot, message);
    }

    private void debugPredictEnemy(List<Enemy> latestHistoryItems, Rectangle2D enemyMovementArea) {
        for (int i = 0; i < 5; i++) {
            EnemyPrediction enemyPrediction = PatternPredictionUtils.predictEnemy(latestHistoryItems, robot.getTime() + i, enemyMovementArea);
            Point2D testEnemyPosition = enemyPrediction.getPredictionPosition();
            String message = String.format("predict enemy at time %s, position {%.2f, %.2f}", robot.getTime() + i, testEnemyPosition.getX(), testEnemyPosition.getY());
            LogHelper.logRobotMovement(robot, message);
        }
    }

    /**
     * Fire bullet when finish aiming.
     */
    @Override
    public void runLoop() {
        GunUtils.fireBulletWhenFinishAiming(robot, gunStateContext, HiTechDecorator.BULLET_COLOR);
    }
}
