package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Rectangle2D;

public class RiskArea {
    private final Rectangle2D area;
    private final double risk;

    public RiskArea(Rectangle2D area, double risk) {
        this.area = area;
        this.risk = risk;
    }

    public Rectangle2D getArea() {
        return area;
    }

    public double getRisk() {
        return risk;
    }
}
