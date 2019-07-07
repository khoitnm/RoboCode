package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;

public class EnemyPotentialPositions {
    private final double timePeriod;
    private final Point2D bestPotentialPosition;

    public EnemyPotentialPositions(double timePeriod, Point2D bestPotentialPosition) {
        this.timePeriod = timePeriod;
        this.bestPotentialPosition = bestPotentialPosition;
    }

    public double getTimePeriod() {
        return timePeriod;
    }


    public Point2D getBestPotentialPosition() {
        return bestPotentialPosition;
    }


}
