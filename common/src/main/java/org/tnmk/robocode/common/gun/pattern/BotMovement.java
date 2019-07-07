package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;

public class BotMovement {
    private final Point2D currentPosition;
    private final double velocity;
    private final int normAcceleration;
    private final double headingRadians;
    private final double headingChangingRateRadians;

    public BotMovement(Point2D currentPosition, double velocity, int normAcceleration, double headingRadians, double headingChangingRateRadians) {
        this.currentPosition = currentPosition;
        this.velocity = velocity;
        this.normAcceleration = normAcceleration;
        this.headingRadians = headingRadians;
        this.headingChangingRateRadians = headingChangingRateRadians;
    }

    public Point2D getCurrentPosition() {
        return currentPosition;
    }

    public double getVelocity() {
        return velocity;
    }

    public int getNormAcceleration() {
        return normAcceleration;
    }

    public double getHeadingRadians() {
        return headingRadians;
    }

    public double getHeadingChangingRateRadians() {
        return headingChangingRateRadians;
    }
}
