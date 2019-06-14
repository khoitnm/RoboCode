package org.tnmk.robocode.common.gun.gft.oldalgorithm;

import java.awt.geom.Point2D;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * Copied from http://old.robowiki.net/robowiki?GFTargetingBot
 */
public class GFTAimGun implements OnScannedRobotControl {
    //TODO make dynamic bullet power based on the distance.
    private static final double BULLET_POWER = 1.9;

    private static double lateralDirection;
    private static double lastEnemyVelocity;

    private final AdvancedRobot robot;
    private final GunStateContext gunStateContext;

    public GFTAimGun(AdvancedRobot robot, GunStateContext gunStateContext) {
        this.robot = robot;
        this.gunStateContext = gunStateContext;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        double enemyAbsoluteBearing = robot.getHeadingRadians() + scannedRobotEvent.getBearingRadians();
        double enemyDistance = scannedRobotEvent.getDistance();
        double enemyVelocity = scannedRobotEvent.getVelocity();
        if (enemyVelocity != 0) {
            lateralDirection = GeoMathUtils.sign(enemyVelocity * Math.sin(scannedRobotEvent.getHeadingRadians() - enemyAbsoluteBearing));
        }

        GFTWave wave = new GFTWave(robot);
        wave.gunLocation = new Point2D.Double(robot.getX(), robot.getY());
        GFTWave.targetLocation = GFTUtils.project(wave.gunLocation, enemyAbsoluteBearing, enemyDistance);
        wave.lateralDirection = lateralDirection;
        wave.bulletPower = BULLET_POWER;
        wave.setSegmentations(enemyDistance, enemyVelocity, lastEnemyVelocity);
        lastEnemyVelocity = enemyVelocity;
        wave.bearing = enemyAbsoluteBearing;
        if (!gunStateContext.isAiming()) {
            gunStateContext.aimGun(GunStrategy.GFT, wave.bulletPower);
            robot.setTurnGunRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() + wave.mostVisitedBearingOffset()));
            robot.setFire(wave.bulletPower);
//            LogHelper.logAdvanceRobot(robot, "Aim GFT. bulletPower: " + wave.bulletPower + ", distance: " + enemyDistance);
            gunStateContext.rest();
            if (robot.getEnergy() >= BULLET_POWER) {
                robot.addCustomEvent(wave);
            }
        }
    }
}