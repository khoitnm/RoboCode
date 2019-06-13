package org.tnmk.robocode.common.movement.antigravity;

import java.awt.geom.Rectangle2D;

/**
 * This object contains all environment information to calculate the anti-gravity force.
 */
public class AntiGravityCalculationContext {
    private Rectangle2D safeMovementArea;
    private double maxPossibleMoveDistance;
    private double maxSafeMoveDistance;
    private int maxPossibleEnemiesCount;
    private int maxActualEnemiesCount;
    /**
     * Increase the movement distance for the force.
     */
    private double movementIncrement;

    public Rectangle2D getSafeMovementArea() {
        return safeMovementArea;
    }

    public void setSafeMovementArea(Rectangle2D safeMovementArea) {
        this.safeMovementArea = safeMovementArea;
    }

    public double getMaxPossibleMoveDistance() {
        return maxPossibleMoveDistance;
    }

    public void setMaxPossibleMoveDistance(double maxPossibleMoveDistance) {
        this.maxPossibleMoveDistance = maxPossibleMoveDistance;
    }

    public double getMaxSafeMoveDistance() {
        return maxSafeMoveDistance;
    }

    public void setMaxSafeMoveDistance(double maxSafeMoveDistance) {
        this.maxSafeMoveDistance = maxSafeMoveDistance;
    }

    public int getMaxPossibleEnemiesCount() {
        return maxPossibleEnemiesCount;
    }

    public void setMaxPossibleEnemiesCount(int maxPossibleEnemiesCount) {
        this.maxPossibleEnemiesCount = maxPossibleEnemiesCount;
    }

    public int getMaxActualEnemiesCount() {
        return maxActualEnemiesCount;
    }

    public void setMaxActualEnemiesCount(int maxActualEnemiesCount) {
        this.maxActualEnemiesCount = maxActualEnemiesCount;
    }

    public double getMovementIncrement() {
        return movementIncrement;
    }

    public void setMovementIncrement(double movementIncrement) {
        this.movementIncrement = movementIncrement;
    }
}
