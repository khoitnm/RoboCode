package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Point2D;
import java.util.List;

public class ForceResult {
    private Point2D finalForce;
    private List<Point2D> forces;

    public ForceResult(List<Point2D> forces, Point2D finalForce) {
        this.finalForce = finalForce;
        this.forces = forces;
    }

    public Point2D getFinalForce() {
        return finalForce;
    }

    public void setFinalForce(Point2D finalForce) {
        this.finalForce = finalForce;
    }

    public List<Point2D> getForces() {
        return forces;
    }

    public void setForces(List<Point2D> forces) {
        this.forces = forces;
    }
}