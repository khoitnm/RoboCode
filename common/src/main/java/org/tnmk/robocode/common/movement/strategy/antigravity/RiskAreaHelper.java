package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class RiskAreaHelper {
    public static List<RiskArea> excludeAreas(List<RiskArea> riskAreas, Rectangle2D excludedArea) {
        List<RiskArea> excludedRiskAreas = new ArrayList<>();
        for (RiskArea riskArea : riskAreas) {
            if (!riskArea.getArea().intersects(excludedArea)) {
                excludedRiskAreas.add(riskArea);
            }
        }
        return excludedRiskAreas;
    }
}
