package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunUtils;
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
        aimGun(robot, enemyPatternPrediction.getEnemyHistory(), 1.9);
    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        Point2D enemyPosition = predictEnemyPositionWhenBulletReachEnemy(robot, enemyHistory, firePower);

        /**Turn the gun to the correct angle**/
        double gunBearing = reckonTurnGunLeftNormRadian(robot, enemyPosition);
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

    private double reckonTurnGunLeftNormRadian(AdvancedRobot robot, Point2D enemyPosition) {
        double robotToEnemyRadian = Math.PI / 2 - Math.atan2(enemyPosition.getY() - robot.getY(), enemyPosition.getX() - robot.getX());
        double gunOffset = robot.getGunHeadingRadians() - robotToEnemyRadian;
        gunOffset = AngleUtils.normaliseRadian(gunOffset);
        return gunOffset;
    }

    private Point2D predictEnemyPositionWhenBulletReachEnemy(AdvancedRobot robot, EnemyHistory enemyHistory, double firePower) {
        List<Enemy> latestHistoryItems = enemyHistory.getLatestHistoryItems(3);
        Point2D enemyPosition = enemyHistory.getLatestHistoryItem().getPosition();
        for (int i = 0; i < 10; i++) {//this loop is used to improve the correctness of prediction.
            //TODO after sometime, the robot position also changed, not just enemyPosition.
            double distanceRobotToEnemy = MathUtils.distance(robot.getX(), robot.getY(), enemyPosition.getX(), enemyPosition.getY());
            double bulletVelocity = GunUtils.reckonBulletVelocity(firePower);
            double periodForBulletToReachEnemy = distanceRobotToEnemy / bulletVelocity;

            double gunBearing = reckonTurnGunLeftNormRadian(robot, enemyPosition);
            double periodForTurningGun = gunBearing / AngleUtils.toRadian(RobotPhysics.GUN_TURN_VELOCITY);
            double totalPeriodGun = periodForTurningGun + periodForBulletToReachEnemy;
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
