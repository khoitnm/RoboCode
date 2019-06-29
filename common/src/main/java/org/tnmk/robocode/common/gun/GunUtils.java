package org.tnmk.robocode.common.gun;

import java.awt.Color;
import java.awt.geom.Point2D;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.number.DoubleUtils;
import robocode.AdvancedRobot;

public class GunUtils {
    /**
     * The formular is defined inside this page: http://robowiki.net/wiki/Selecting_Fire_Power
     *
     * @param bulletPower a bullet can be fired with any amount of energy between 0.1 and 3.0.
     * @return
     */
    public static double reckonBulletVelocity(double bulletPower) {
        return 20 - (3 * bulletPower);
    }

    /**
     * Reckon the angle to turn to gun to the enemyPosition
     * @param robotPosition the current position of our robot
     * @param enemyPosition the enemy position we want to fire to
     * @param gunHeadingRadians the current gun angle (radian) of our robot
     * @return
     */
    public static double reckonTurnGunLeftNormRadian(Point2D robotPosition, Point2D enemyPosition, double gunHeadingRadians) {
        double robotToEnemyRadian = Math.PI / 2 - Math.atan2(enemyPosition.getY() - robotPosition.getY(), enemyPosition.getX() - robotPosition.getX());
        double gunOffset = gunHeadingRadians - robotToEnemyRadian;
        gunOffset = AngleUtils.normalizeRadian(gunOffset);
        return gunOffset;
    }

    /**
     * @param robot
     * @param gunStateContext
     * @return if fired bullet or not
     */
    public static boolean fireBulletWhenFinishAiming(AdvancedRobot robot, GunStateContext gunStateContext, Color bulletColor){
        if (gunStateContext.isAiming()) {
            //TODO Something weird happens here!!! look at the below code.
            // I just added a condition ```DoubleUtils.isConsideredZero(robot.getGunHeat()) && ```, which looks like totally makes sense, right?
            // But then the gun totally predicts wrong: you can test with SpintBot, it failed. It even fail against Walls (cannot predict linear correctly)
            // I still don't know why, but removing it fix problem!!!
            // -----------------------------------------
            // OK, now I understand. Because this is the correct time to fire.
            // If you don't fire this time, next time the status is still aiming, the gunTurnRemain is 0, so it will fire bullet.
            // But at that time, it was too lat.
            // So, if we don't fire, we must reset aiming become false.
//          if (DoubleUtils.isConsideredZero(robot.getGunHeat()) && DoubleUtils.isConsideredZero(robot.getGunTurnRemaining())) {
            if (DoubleUtils.isConsideredZero(robot.getGunTurnRemaining())) {
                if (DoubleUtils.isConsideredZero(robot.getGunHeat())) {
                    robot.setBulletColor(bulletColor);
                    robot.setFire(gunStateContext.getBulletPower());
                    gunStateContext.saveStateFinishedAiming();
                    return true;
//                LogHelper.logRobotMovement(robot, "Fire!!! " + gunStateContext.getBulletPower());
                } else {
                    gunStateContext.saveStateFinishedAiming();
                }
            }
        }
        return false;
    }
}
