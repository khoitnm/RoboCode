package org.tnmk.robocode.common.robot.gft.newalgorithm;

import robocode.util.Utils;

import java.awt.geom.Point2D;

public class WaveBullet {
    private double startX, startY, startBearing, power;
    private long fireTime;
    private int direction;
    private int[] returnSegment;

    public WaveBullet(double x, double y, double bearing, double power,
                      int direction, long time, int[] segment) {
        startX = x;
        startY = y;
        startBearing = bearing;
        this.power = power;
        this.direction = direction;
        fireTime = time;
        returnSegment = segment;
    }

    public double getBulletSpeed() {
        return 20 - power * 3;
    }

    public double maxEscapeAngle() {
        return Math.asin(8 / getBulletSpeed());
    }

    public boolean checkHit(double enemyX, double enemyY, long currentTime) {
        // if the distance from the wave origin to our enemy has passed
        // the distance the bullet would have traveled...
        if (Point2D.distance(startX, startY, enemyX, enemyY) <=
                (currentTime - fireTime) * getBulletSpeed()) {
            double desiredDirection = Math.atan2(enemyX - startX, enemyY - startY);
            double angleOffset = Utils.normalRelativeAngle(desiredDirection - startBearing);
            double guessFactor =
                    Math.max(-1, Math.min(1, angleOffset / maxEscapeAngle())) * direction;
            int index = (int) Math.round((returnSegment.length - 1) / 2 * (guessFactor + 1));
            returnSegment[index]++;
            return true;
        }
        return false;
    }
}