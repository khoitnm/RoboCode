package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.prediction.MovePredictionHelper;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * https://www.ibm.com/developerworks/library/j-circular/index.html
 */
public class CircularPatternGun implements LoopableRun, Scannable {
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
        double firePower = GunHelper.findFirePowerByDistance(enemy.getDistance());
        aimGun(robot, enemyPatternPrediction.getEnemyHistory(), firePower);
    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        Point2D enemyPosition = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, firePower);
        Point2D robotPosition = new Point2D.Double(robot.getX(), robot.getY());
        /**Turn the gun to the correct angle**/

        double gunBearing = reckonTurnGunLeftNormRadian(robotPosition, enemyPosition, robot.getGunHeadingRadians());
        if (!gunStateContext.isAiming()) {
            robot.setTurnGunLeftRadians(gunBearing);
            gunStateContext.aimGun(firePower);
            LogHelper.logAdvanceRobot(robot, "AimGun " + gunStateContext.getGunState());
            //Gun will be fired by loopRun() when finishing aiming.
        } else {
            // Don't aim the new target until the old target was done!
            // So don't need to do anything for now.
        }
    }

    private static double reckonTurnGunLeftNormRadian(Point2D robotPosition, Point2D enemyPosition, double gunHeadingRadians) {
        double robotToEnemyRadian = Math.PI / 2 - Math.atan2(enemyPosition.getY() - robotPosition.getY(), enemyPosition.getX() - robotPosition.getX());
        double gunOffset = gunHeadingRadians - robotToEnemyRadian;
        gunOffset = AngleUtils.normaliseRadian(gunOffset);
        return gunOffset;
    }

    private Point2D predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        List<Enemy> latestHistoryItems = enemyHistory.getLatestHistoryItems(3);
        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());
        long periodForTurningGun = 0;
        for (int i = 0; i < ENEMY_PREDICTION_TIMES; i++) {//this loop is used to improve the correctness of prediction.
            RobotPrediction robotPrediction = MovePredictionHelper.predictPosition(periodForTurningGun, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
            Point2D predictRobotPosition = robotPrediction.getPosition();
//            String message = String.format("Predict at time %s, position {%.2f, %.2f}", (robot.getTime() + periodForTurningGun), predictRobotPosition.getX(), predictRobotPosition.getY());
//            LogHelper.logAdvanceRobot(robot, message);

            RobotPrediction testRobotPredictionAfter5 = MovePredictionHelper.predictPosition(5, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
            String message = String.format("Predict at time %s, position {%.2f, %.2f}", (robot.getTime() + 5), testRobotPredictionAfter5.getPosition().getX(), testRobotPredictionAfter5.getPosition().getY());
            LogHelper.logAdvanceRobot(robot, message);


            double distanceRobotToEnemy = predictRobotPosition.distance(enemyPosition);
            double bulletVelocity = GunUtils.reckonBulletVelocity(firePower);
            long periodForBulletToReachEnemy = (long) Math.ceil(Math.abs(distanceRobotToEnemy / bulletVelocity));

            double gunBearing = reckonTurnGunLeftNormRadian(predictRobotPosition, enemyPosition, robot.getGunHeadingRadians());
            periodForTurningGun = (long) Math.ceil(Math.abs(gunBearing / AngleUtils.toRadian(RobotPhysics.GUN_TURN_VELOCITY)));
            long totalPeriodGun = periodForTurningGun + periodForBulletToReachEnemy;
            long timeWhenBulletReachEnemy = robot.getTime() + Math.round(totalPeriodGun);

            enemyPosition = CircularGuessUtils.guessPosition(latestHistoryItems, timeWhenBulletReachEnemy);
        }
        return enemyPosition;
    }

    @Override
    public void runLoop() {
        LogHelper.logAdvanceRobot(robot, "GunState: " + gunStateContext.getGunState() + " gunTurnRemaining: " + robot.getGunTurnRemaining());
        if (gunStateContext.isAiming()) {
            if (DoubleUtils.isConsideredZero(robot.getGunTurnRemaining())) {
                robot.setFire(gunStateContext.getBulletPower());
                gunStateContext.rest();
                LogHelper.logAdvanceRobot(robot, "Fire!!! " + gunStateContext.getBulletPower());
            }
        }
    }
}
