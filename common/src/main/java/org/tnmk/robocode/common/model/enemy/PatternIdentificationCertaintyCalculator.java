package org.tnmk.robocode.common.model.enemy;

import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;

public class PatternIdentificationCertaintyCalculator {
    /**
     * @param certaintyCalculationTime
     * @param enemyMovePattern
     * @param enemyPredictionHistory
     * @return calculate the certainty of enemyMovePattern based on the data inside enemyPredictionHistory
     */
    public static PatternIdentification calculateHistoryCertainty(long certaintyCalculationTime, EnemyMovePattern enemyMovePattern, EnemyPredictionHistory enemyPredictionHistory) {
        if (enemyPredictionHistory.countHistoryItems() == 0) {
            return new PatternIdentification(certaintyCalculationTime, enemyMovePattern, PatternIdentification.DEFAULT_CERTAINTY_WHEN_NO_PREDICTION_HISTORY);
        }
        long countPredictionWithSameMovePattern = enemyPredictionHistory.historyItems.stream()
                .filter(item -> item.getEnemyMovePattern() == enemyMovePattern)
                .count();

        double certaintyPredictionWithSameMovePattern = countPredictionWithSameMovePattern / enemyPredictionHistory.countHistoryItems();

        PatternIdentification patternIdentification = new PatternIdentification(certaintyCalculationTime, enemyMovePattern, certaintyPredictionWithSameMovePattern);
        return patternIdentification;
    }
}
