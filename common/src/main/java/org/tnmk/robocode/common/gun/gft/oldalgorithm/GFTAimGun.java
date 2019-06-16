package org.tnmk.robocode.common.gun.gft.oldalgorithm;

import java.awt.geom.Point2D;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.GunStrategy;
import org.tnmk.robocode.common.gun.pattern.BulletPowerHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/**
 * Copied from http://old.robowiki.net/robowiki?GFTargetingBot
 */
public class GFTAimGun implements OnScannedRobotControl {
    //TODO make dynamic bullet power based on the distance.
    private static final double BULLET_POWER = 1.9;
    /**
     * For this algorithm, the bullet power should never be lower than this. Otherwise, the error will happens.
     */
    private static final double MIN_BULLET_POWER = 1.0d;

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

        if (enemyDistance > GFTWave.MAX_DISTANCE) {
            /** Don't aim or shot in this case. Otherwise, there will be a bug ArrayOutOfBoundIndexException.*/
            return;
        }

        double bulletPower = BulletPowerHelper.reckonBulletPower(enemyDistance, robot.getOthers(), robot.getEnergy());
        LogHelper.logAdvanceRobot(robot, "Aim GFT. bulletPower: " + bulletPower + ", distance: " + enemyDistance);
        if (bulletPower <= 0) {
            return;//if bulletPower is 0 (because low energy, or too risky), don't need to aim or fire bullet.
        } else if (bulletPower < MIN_BULLET_POWER) {
            bulletPower = MIN_BULLET_POWER;
        }

        if (enemyVelocity != 0) {
            lateralDirection = GeoMathUtils.sign(enemyVelocity * Math.sin(scannedRobotEvent.getHeadingRadians() - enemyAbsoluteBearing));
        }
        GFTWave wave = new GFTWave(robot);
        wave.gunLocation = new Point2D.Double(robot.getX(), robot.getY());
        GFTWave.targetLocation = GFTUtils.project(wave.gunLocation, enemyAbsoluteBearing, enemyDistance);
        wave.lateralDirection = lateralDirection;
        wave.bulletPower = bulletPower;

        wave.setSegmentations(enemyDistance, enemyVelocity, lastEnemyVelocity);
        lastEnemyVelocity = enemyVelocity;
        wave.bearing = enemyAbsoluteBearing;
        if (!gunStateContext.isAiming() || gunStateContext.isStrategy(GunStrategy.GFT)) {
            robot.setTurnGunRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() + wave.mostVisitedBearingOffset()));
            robot.setBulletColor(HiTechDecorator.BULLET_GFT_COLOR);
            if (robot.getGunHeat() == 0) {
                gunStateContext.saveSateAimGun(GunStrategy.GFT, wave.bulletPower);
                robot.setFire(wave.bulletPower);
            }
            gunStateContext.saveStateFinishedAiming();
            if (robot.getEnergy() >= bulletPower) {
                robot.addCustomEvent(wave);
            }
        }
    }
}