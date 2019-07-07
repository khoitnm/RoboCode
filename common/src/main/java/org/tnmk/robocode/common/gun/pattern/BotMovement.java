package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class BotMovement {
    private boolean isMainMovement;
    private final Point2D currentPosition;
    private final double velocity;
    private final int normAcceleration;
    private final double headingRadians;
    private final double headingChangingRateRadians;

    private final Rectangle2D enemyMovementBoundary;

    public BotMovement(Point2D currentPosition, double velocity, int normAcceleration, double headingRadians, double headingChangingRateRadians, Rectangle2D enemyMovementBoundary) {
        this.currentPosition = currentPosition;
        this.velocity = velocity;
        this.normAcceleration = normAcceleration;
        this.headingRadians = headingRadians;
        this.headingChangingRateRadians = headingChangingRateRadians;
        this.enemyMovementBoundary = enemyMovementBoundary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotMovement that = (BotMovement) o;
        return Double.compare(that.velocity, velocity) == 0 &&
                normAcceleration == that.normAcceleration &&
                Double.compare(that.headingRadians, headingRadians) == 0 &&
                Double.compare(that.headingChangingRateRadians, headingChangingRateRadians) == 0 &&
                Objects.equals(currentPosition, that.currentPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPosition, velocity, normAcceleration, headingRadians, headingChangingRateRadians);
    }

    public Rectangle2D getEnemyMovementBoundary() {
        return enemyMovementBoundary;
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

    public boolean isMainMovement() {
        return isMainMovement;
    }

    public void setMainMovement(boolean mainMovement) {
        isMainMovement = mainMovement;
    }
}
