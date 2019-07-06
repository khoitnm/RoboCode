package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;

public class EnemyPotentialPositions {
    private double timePeriod;
    private Point2D bestPotentialPosition;

    public double getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(double timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Point2D getBestPotentialPosition() {
        return bestPotentialPosition;
    }

    public void setBestPotentialPosition(Point2D bestPotentialPosition) {
        this.bestPotentialPosition = bestPotentialPosition;
    }
}
