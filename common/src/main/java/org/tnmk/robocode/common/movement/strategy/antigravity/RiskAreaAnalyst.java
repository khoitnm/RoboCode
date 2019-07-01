package org.tnmk.robocode.common.movement.strategy.antigravity;

import com.sun.xml.internal.bind.v2.TODO;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.tnmk.common.collection.ListUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;

public class RiskAreaAnalyst {
    public static final int NUM_OF_ANALYST_LEVELS = 2;

    public static List<RiskArea> findLeastRiskyArea(Rectangle2D battleField, Collection<Enemy> enemies) {
        List<RiskArea> areas = analyzeAreaRisks(battleField, NUM_OF_ANALYST_LEVELS, enemies);
        return findLeastRiskyArea(areas);
    }

    /**
     * @param battleField
     * @param numAnalystLevels for each level, each side of the area will be split into 2.
     * @return
     */
    private static List<RiskArea> analyzeAreaRisks(Rectangle2D battleField, int numAnalystLevels, Collection<Enemy> enemies) {
        double initRisk = 0;
        int initAnalystLevel = 0;
        List<RiskArea> analyzedArea = analyzeAreaRisks(new RiskArea(battleField, initRisk), initAnalystLevel, numAnalystLevels, enemies);
        return analyzedArea;
    }

    /**
     * @param area
     * @param analystLevel     current level we want to analyze. Must be less than or equals (numAnalystLevels - 1). Otherwise, return an empty list.
     * @param numAnalystLevels total levels we want to analyse
     * @return
     */
    private static List<RiskArea> analyzeAreaRisks(RiskArea area, int analystLevel, int numAnalystLevels, Collection<Enemy> enemies) {
        if (analystLevel > numAnalystLevels - 1) {
            //stop:
            return Collections.emptyList();
        }
        Rectangle2D[][] childAreas = BattleFieldUtils.splitToParts(area.getArea(), 2, 2);
        List<Rectangle2D> childAreasList = ListUtils.toList(childAreas);
        //FIXME not accumulate risk value between levels yet.
        List<RiskArea> riskAreas = analystRisk(childAreasList, analystLevel, enemies);

        List<RiskArea> allNextLevelRiskAreas = new ArrayList<>();
        for (RiskArea riskArea : riskAreas) {
            List<RiskArea> nextLevelRiskAreas = analyzeAreaRisks(riskArea, analystLevel + 1, numAnalystLevels, enemies);
            allNextLevelRiskAreas.addAll(nextLevelRiskAreas);
        }
        return allNextLevelRiskAreas;
    }

    private static List<RiskArea> analystRisk(List<Rectangle2D> areasList, int analystLevel, Collection<Enemy> enemies) {
        List<RiskArea> riskAreas = areasList.stream()
                .map(area -> analyseRisk(area, analystLevel, enemies))
                .collect(Collectors.toList());
        return riskAreas;
    }

    private static RiskArea analyseRisk(Rectangle2D area, int analystLevel, Collection<Enemy> enemies) {
        double risk =
    }


    private static List<RiskArea> findLeastRiskyArea(List<RiskArea> areas) {
        List<RiskArea> leastRiskAreas = ListUtils.findLeastValueItems(areas, RiskArea::getRisk);
        return leastRiskAreas;
    }
}
