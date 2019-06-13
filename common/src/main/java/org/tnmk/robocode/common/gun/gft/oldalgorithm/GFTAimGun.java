package org.tnmk.robocode.common.gun.gft.oldalgorithm;

import org.tnmk.common.math.MathUtils;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.geom.Point2D;

/**
 * Copied from http://old.robowiki.net/robowiki?GFTargetingBot
 */
public class GFTAimGun implements OnScannedRobotControl {
    //TODO make dynamic bullet power based on the distance.
    private static final double BULLET_POWER = 1.9;

    private static double lateralDirection;
    private static double lastEnemyVelocity;

    private final AdvancedRobot robot;

    public GFTAimGun(AdvancedRobot robot) {
        this.robot = robot;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        double enemyAbsoluteBearing = robot.getHeadingRadians() + scannedRobotEvent.getBearingRadians();
        double enemyDistance = scannedRobotEvent.getDistance();
        double enemyVelocity = scannedRobotEvent.getVelocity();
        if (enemyVelocity != 0) {
            lateralDirection = MathUtils.sign(enemyVelocity * Math.sin(scannedRobotEvent.getHeadingRadians() - enemyAbsoluteBearing));
        }

        GFTWave wave = new GFTWave(robot);
        wave.gunLocation = new Point2D.Double(robot.getX(), robot.getY());
        GFTWave.targetLocation = GFTUtils.project(wave.gunLocation, enemyAbsoluteBearing, enemyDistance);
        wave.lateralDirection = lateralDirection;
        wave.bulletPower = BULLET_POWER;
        wave.setSegmentations(enemyDistance, enemyVelocity, lastEnemyVelocity);
        lastEnemyVelocity = enemyVelocity;
        wave.bearing = enemyAbsoluteBearing;
        robot.setTurnGunRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() + wave.mostVisitedBearingOffset()));
        robot.setFire(wave.bulletPower);
        if (robot.getEnergy() >= BULLET_POWER) {
            robot.addCustomEvent(wave);
        }
    }
}