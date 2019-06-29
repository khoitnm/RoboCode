package org.tnmk.robocode.common.gun.finishoff;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.GunUtils;
import org.tnmk.robocode.common.helper.prediction.RobotPrediction;
import org.tnmk.robocode.common.helper.prediction.RobotPredictionHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * Use this gun when the enemy has no more energy
 */
public class FinishOffGun implements LoopableRun, OnScannedRobotControl {
    private static final Color BULLET_COLOR = new Color(254, 255, 46);
    private static final int ENEMY_PREDICTION_TIMES = 3;
    private static final long AIMED_AGAIN_PERIOD = 30;
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final GunStateContext gunStateContext;

    /**
     * key: enemyName
     * value: time which we start to aimed the enemy
     */
    private Map<String, Long> aimedEnemiesTime = new HashMap<>();

    public FinishOffGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, GunStateContext gunStateContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = gunStateContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        double bulletPower = Math.max(0.1, scannedRobotEvent.getEnergy() / 4d + 0.01);
        if (bulletPower > 0) {
            aimGun(robot, scannedRobotEvent, bulletPower);
        }
    }

    /**
     * This function predicts the time of the intersection between the
     * bullet and the target based on a simple iteration.  It then moves
     * the gun to the correct angle to fire on the target.
     **/
    private void aimGun(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent, double bulletPower) {
        Long previousAimedTime = aimedEnemiesTime.get(scannedRobotEvent.getName());
        if (previousAimedTime != null && robot.getTime() - previousAimedTime < AIMED_AGAIN_PERIOD) {
            //Don't aim again before too soon.
            return;
        }

        Enemy enemy = allEnemiesObservationContext.getEnemy(scannedRobotEvent.getName());
        if (!gunStateContext.isAiming()) {
            Point2D currentRobotPosition = new Point2D.Double(robot.getX(), robot.getY());
            Point2D enemyPosition = enemy.getPosition();

            double gunBearing = 0d;
            long periodForTurningGun = 0;
            for (int i = 0; i < ENEMY_PREDICTION_TIMES; i++) {//this loop is used to improve the correctness of prediction.
                RobotPrediction robotPrediction = RobotPredictionHelper.predictPosition(periodForTurningGun, currentRobotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
                Point2D predictRobotPosition = robotPrediction.getPosition();

                gunBearing = GunUtils.reckonTurnGunLeftNormRadian(predictRobotPosition, enemyPosition, robot.getGunHeadingRadians());
                periodForTurningGun = (long) Math.ceil(Math.abs(gunBearing / AngleUtils.toRadian(RobotPhysics.GUN_TURN_VELOCITY)));
            }

            /**Turn the gun to the correct angle**/
            robot.setTurnGunLeftRadians(gunBearing);
            gunStateContext.saveSateAimGun(GunStrategy.FINISH_OFF, bulletPower);
            aimedEnemiesTime.put(enemy.getName(), robot.getTime());
            /** This code just aim the gun, don't fire it. The gun will be fired by loopRun() when finishing aiming.*/
        } else {
            /**
             * Don't aim the new target until the old target was done!
             * So don't need to do anything for now.
             */
        }
    }


    /**
     * Fire bullet when finish aiming.
     */
    @Override
    public void runLoop() {
        GunUtils.fireBulletWhenFinishAiming(robot, gunStateContext, BULLET_COLOR);
    }
}
