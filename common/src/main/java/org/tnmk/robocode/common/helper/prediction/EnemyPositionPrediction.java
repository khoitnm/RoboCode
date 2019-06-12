package org.tnmk.robocode.common.helper.prediction;

import java.awt.geom.Point2D;

public class EnemyPositionPrediction {
    private long time;
    private Point2D position;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }
}
