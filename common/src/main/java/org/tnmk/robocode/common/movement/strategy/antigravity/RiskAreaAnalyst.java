package org.tnmk.robocode.common.movement.strategy.antigravity;

import java.awt.geom.Rectangle2D;
import java.util.*;
import org.tnmk.common.collection.ListUtils;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;

public class RiskAreaAnalyst {
    public static final int NUM_OF_ANALYST_LEVELS = 2;
    /**
     * For each analyst level, the coefficient is 10.
     */
    public static final int RISK_LEVEL_COEFFICIENT = 10;

    public static List<RiskArea> findLeastRiskyArea(Rectangle2D battleField, Collection<Enemy> enemies) {
        List<RiskArea> areas = analyzeAreaRisks(battleField, NUM_OF_ANALYST_LEVELS, enemies);
        return findLeastRiskyArea(areas);
    }

    /**
     * @param battleField
     * @param numAnalystLevels for each level, each side of the area will be split into 2. It must be less than 10.
     * @return
     */
    public static List<RiskArea> analyzeAreaRisks(Rectangle2D battleField, int numAnalystLevels, Collection<Enemy> enemies) {
        double initRisk = 0;
        int initAnalystLevel = 0;
        List<RiskArea> analyzedArea = analyzeAreaRisks(battleField, initRisk, initAnalystLevel, numAnalystLevels, enemies);
        return analyzedArea;
    }

    /**
     * @param area
     * @param analystLevel     current level we want to analyze. Must be less than or equals (numAnalystLevels - 1). Otherwise, return an empty list.
     * @param numAnalystLevels total levels we want to analyse
     * @return
     */
    private static List<RiskArea> analyzeAreaRisks(Rectangle2D area, double parentAreaRisk, int analystLevel, int numAnalystLevels, Collection<Enemy> enemies) {
        List<RiskArea> allAnalyzedRiskAreas = new ArrayList<>();
        if (analystLevel > numAnalystLevels - 1) {
            /** This case actually should never happens*/
            return Collections.emptyList();
        }

        double areaRiskIgnoreLevel = analyseRiskIgnoreLevel(area, enemies);
        double areaRiskByLevel = areaRiskIgnoreLevel * Math.pow(RISK_LEVEL_COEFFICIENT, numAnalystLevels - analystLevel);
        double totalAreaRisk = parentAreaRisk + areaRiskByLevel;
        RiskArea riskArea = new RiskArea(area, totalAreaRisk);

        if (analystLevel == numAnalystLevels - 1) {
            return Arrays.asList(riskArea);
        } else {
            int childAnalystLevel = analystLevel + 1;
            Rectangle2D[][] childAreas = BattleFieldUtils.splitToParts(area, 2, 2);
            List<Rectangle2D> childAreasList = ListUtils.toList(childAreas);
            for (Rectangle2D childArea : childAreasList) {
                List<RiskArea> analyzedChildRiskAreas = analyzeAreaRisks(childArea, riskArea.getRisk(), childAnalystLevel, numAnalystLevels, enemies);
                allAnalyzedRiskAreas.addAll(analyzedChildRiskAreas);
            }
            return allAnalyzedRiskAreas;
        }
    }

    /**
     * @param area
     * @param enemies
     * @return the result should less than {@link #RISK_LEVEL_COEFFICIENT}.
     */
    private static double analyseRiskIgnoreLevel(Rectangle2D area, Collection<Enemy> enemies) {
        double risk = 0;
        for (Enemy enemy : enemies) {
            if (GeoMathUtils.checkInsideRectangle(enemy.getPosition(), area)) {
                risk++;
            }
        }
        return risk;
    }


    private static List<RiskArea> findLeastRiskyArea(List<RiskArea> areas) {
        List<RiskArea> leastRiskAreas = ListUtils.findLeastValueItems(areas, RiskArea::getRisk);
        return leastRiskAreas;
    }
}
