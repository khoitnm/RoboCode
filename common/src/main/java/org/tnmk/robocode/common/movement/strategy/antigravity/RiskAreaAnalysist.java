package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Rectangle2D;
import java.util.List;
import org.tnmk.common.collection.ListUtils;

public class RiskAreaAnalysist {
    public static List<RiskArea> findLeastRiskyArea(Rectangle2D battleField) {
        List<RiskArea> areas = analyzeAreaRisks(battleField);
        return findLeastRiskyArea(areas);
    }

    private static List<RiskArea> analyzeAreaRisks(Rectangle2D battleField) {
    }

    private static List<RiskArea> findLeastRiskyArea(List<RiskArea> areas) {
        List<RiskArea> leastRiskAreas = ListUtils.findLeastValueItems(areas, RiskArea::getRisk);
        return leastRiskAreas;
    }
}
