package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.prediction.EnemyPositionPrediction;
import org.tnmk.robocode.common.helper.prediction.MovePredictionHelper;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 */
public class CircularPatternGun implements LoopableRun, OnScannedRobotControl {
    private static final int ENEMY_PREDICTION_TIMES = 3;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final GunStateContext gunStateContext;

    public CircularPatternGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, GunStateContext gunStateContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = gunStateContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        String enemyName = scannedRobotEvent.getName();
        EnemyPatternPrediction enemyPatternPrediction = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
        Enemy enemy = enemyPatternPrediction.getEnemyHistory().getLatestHistoryItem();
        double bulletPower = GunHelper.findFirePowerByDistance(enemy.getDistance());
        LogHelper.logAdvanceRobot(robot, "Aim Circular. bulletPower: "+bulletPower +", distance: "+enemy.getDistance());
        aimGun(robot, enemyPatternPrediction.getEnemyHistory(), bulletPower);
    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyHistory enemyHistory, double bulletPower) {
        EnemyPositionPrediction enemyPositionPrediction = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, bulletPower);
        Point2D enemyPosition = enemyPositionPrediction.getPosition();
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        /**Turn the gun to the correct angle**/

        double gunBearing = reckonTurnGunLeftNormRadian(robotPosition, enemyPosition, robot.getGunHeadingRadians());
        if (!gunStateContext.isAiming()) {
            robot.setTurnGunLeftRadians(gunBearing);
            gunStateContext.aimGun(GunStrategy.CIRCULAR, bulletPower);
//            LogHelper.logAdvanceRobot(robot, "AimGun " + gunStateContext.getGunStrategy());
            //Gun will be fired by loopRun() when finishing aiming.
        } else {
            // Don't aim the new target until the old target was done!
            // So don't need to do anything for now.
        }
    }

    private static double reckonTurnGunLeftNormRadian(Point2D robotPosition, Point2D enemyPosition, double gunHeadingRadians) {
        double robotToEnemyRadian = Math.PI / 2 - Math.atan2(enemyPosition.getY() - robotPosition.getY(), enemyPosition.getX() - robotPosition.getX());
        double gunOffset = gunHeadingRadians - robotToEnemyRadian;
        gunOffset = AngleUtils.normalizeRadian(gunOffset);
        return gunOffset;
    }

    private EnemyPositionPrediction predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        EnemyPositionPrediction enemyPositionPrediction = new EnemyPositionPrediction();
        List<Enemy> latestHistoryItems = enemyHistory.getLatestHistoryItems(5);
        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());

//        debugPredictSelfRobot(robot);

        long periodForTurningGun = 0;
        for (int i = 0; i < ENEMY_PREDICTION_TIMES; i++) {//this loop is used to improve the correctness of prediction.
            RobotPrediction robotPrediction = MovePredictionHelper.predictPosition(periodForTurningGun, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
            Point2D predictRobotPosition = robotPrediction.getPosition();
//            String message = String.format("Predict at time %s, position {%.2f, %.2f}", (robot.getTime() + periodForTurningGun), predictRobotPosition.getX(), predictRobotPosition.getY());
//            LogHelper.logAdvanceRobot(robot, message);

            double distanceRobotToEnemy = predictRobotPosition.distance(enemyPosition);
            double bulletVelocity = GunUtils.reckonBulletVelocity(firePower);
            long periodForBulletToReachEnemy = (long) Math.ceil(Math.abs(distanceRobotToEnemy / bulletVelocity));

            double gunBearing = reckonTurnGunLeftNormRadian(predictRobotPosition, enemyPosition, robot.getGunHeadingRadians());
            periodForTurningGun = (long) Math.ceil(Math.abs(gunBearing / AngleUtils.toRadian(RobotPhysics.GUN_TURN_VELOCITY)));
            long totalPeriodGun = periodForTurningGun + periodForBulletToReachEnemy;
            long timeWhenBulletReachEnemy = robot.getTime() + Math.round(totalPeriodGun);

            enemyPosition = CircularGuessUtils.guessPosition(latestHistoryItems, timeWhenBulletReachEnemy);

            enemyPositionPrediction.setPosition(enemyPosition);
            enemyPositionPrediction.setTime(timeWhenBulletReachEnemy);
        }
//        debugPredictEnemy(latestHistoryItems);

//        String message = String.format("Final predict enemy at %s, position {%.2f, %.2f}", enemyPositionPrediction.getTime(), enemyPositionPrediction.getPosition().getX(), enemyPositionPrediction.getPosition().getY());
//        LogHelper.logAdvanceRobot(robot, message);
        return enemyPositionPrediction;
    }

    private void debugPredictSelfRobot(AdvancedRobot robot){
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());
        RobotPrediction testRobotPredictionAfter5 = MovePredictionHelper.predictPosition(5, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
        String message = String.format("Predict self at time %s, position {%.2f, %.2f}", (robot.getTime() + 5), testRobotPredictionAfter5.getPosition().getX(), testRobotPredictionAfter5.getPosition().getY());
        LogHelper.logAdvanceRobot(robot, message);
    }

    private void debugPredictEnemy(List<Enemy> latestHistoryItems){
        for (int i = 0; i < 5; i++) {
            Point2D testEnemyPosition = CircularGuessUtils.guessPosition(latestHistoryItems, robot.getTime()+i);
            String message = String.format("predict enemy at time %s, position {%.2f, %.2f}", robot.getTime()+i, testEnemyPosition.getX(), testEnemyPosition.getY());
            LogHelper.logAdvanceRobot(robot, message);
        }
    }

    @Override
    public void runLoop() {
//        LogHelper.logAdvanceRobot(robot, "GunStrategy: " + gunStateContext.getGunStrategy() + " gunTurnRemaining: " + robot.getGunTurnRemaining());
        if (gunStateContext.isAiming()) {
            if (DoubleUtils.isConsideredZero(robot.getGunTurnRemaining())) {
                robot.setFire(gunStateContext.getBulletPower());
                gunStateContext.rest();
//                LogHelper.logAdvanceRobot(robot, "Fire!!! " + gunStateContext.getBulletPower());
            }
        }
    }
}
