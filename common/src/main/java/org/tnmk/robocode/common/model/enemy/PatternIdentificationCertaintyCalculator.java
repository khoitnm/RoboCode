package org.tnmk.robocode.common.model.enemy;

import java.util.List;
import java.util.stream.Collectors;
import org.tnmk.robocode.common.gun.pattern.EnemyMovePattern;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;

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
        List<EnemyPrediction> enemyPredictionsWithSamePattern = enemyPredictionHistory.historyItems.stream()
                .filter(item -> item.getEnemyMovePattern() == enemyMovePattern)
                .collect(Collectors.toList());
        long countPredictionWithSameMovePattern = enemyPredictionsWithSamePattern.size();

        double certaintyPredictionWithSameMovePattern = (double) countPredictionWithSameMovePattern / enemyPredictionHistory.countHistoryItems();
        double certaintyPredictionWithSimilarVelocityAndHeadingChange = reckonCertaintySimilarAvgVelocityOrAvgHeadingChanges(enemyPredictionsWithSamePattern);
        double finalCertainty = certaintyPredictionWithSameMovePattern * certaintyPredictionWithSimilarVelocityAndHeadingChange;

        PatternIdentification patternIdentification = new PatternIdentification(certaintyCalculationTime, enemyMovePattern, finalCertainty);
        return patternIdentification;
    }

    private static double reckonCertaintySimilarAvgVelocityOrAvgHeadingChanges(List<EnemyPrediction> enemyPredictionsWithTheSamePattern) {
        if (enemyPredictionsWithTheSamePattern.isEmpty()) {
            return 0;
        }
        double predictionsCount = (double) enemyPredictionsWithTheSamePattern.size();

        double minAvgVelocity = Double.MAX_VALUE;
        double maxAvgVelocity = 0;
        double minAbsAvgHeadingChangeRadian = Double.MAX_VALUE;
        double maxAbsAvgHeadingChangeRadian = 0;

        double totalAvgVelocity = 0;
        double totalAbsAvgHeadingChangeRadian = 0;
        for (EnemyPrediction enemyPrediction : enemyPredictionsWithTheSamePattern) {
            double absAvgHeadingChangeRadian = Math.abs(enemyPrediction.getEnemyAvgChangeHeadingRadian());

            if (minAvgVelocity > enemyPrediction.getEnemyAvgVelocity()) {
                minAvgVelocity = enemyPrediction.getEnemyAvgVelocity();
            }
            if (maxAvgVelocity < enemyPrediction.getEnemyAvgVelocity()) {
                maxAvgVelocity = enemyPrediction.getEnemyAvgVelocity();
            }
            totalAvgVelocity += enemyPrediction.getEnemyAvgVelocity();

            if (minAbsAvgHeadingChangeRadian > absAvgHeadingChangeRadian) {
                minAbsAvgHeadingChangeRadian = absAvgHeadingChangeRadian;
            }
            if (maxAbsAvgHeadingChangeRadian < absAvgHeadingChangeRadian) {
                maxAbsAvgHeadingChangeRadian = absAvgHeadingChangeRadian;
            }
            totalAbsAvgHeadingChangeRadian += absAvgHeadingChangeRadian;
        }
        double avgAvgVelocity = totalAvgVelocity / predictionsCount;
        double avgAbsAvgHeadingChangeRadian = totalAbsAvgHeadingChangeRadian / predictionsCount;


        double deltaAvgVelocity = maxAvgVelocity - minAvgVelocity;
        double deltaAbsAvgHeadingChangeRadian = maxAbsAvgHeadingChangeRadian - minAbsAvgHeadingChangeRadian;

        double avgDeltaAvgVelocity = deltaAvgVelocity / predictionsCount;
        double avgDeltaAbsAvgHeadingChangeRadian = deltaAbsAvgHeadingChangeRadian / predictionsCount;

        /** If velocity doesn't change more than 0.5, I think it's good enough consider most of the time velocity change from 1 to 8. */
        long countGoodAvgVelocity = enemyPredictionsWithTheSamePattern.stream()
                .filter(enemyPrediction ->
                        enemyPrediction.getEnemyAvgVelocity() - avgAvgVelocity <= Math.max(avgDeltaAvgVelocity, 0.5))
                .count();
        double certaintyGoodAvgVelocity = countGoodAvgVelocity / predictionsCount;

        /** If headingChange doesn't change more than 0.015, the usual data is around 0.09 (?) */
        long countGoodAvgHeadingChangeRadian = enemyPredictionsWithTheSamePattern.stream()
                .filter(enemyPrediction -> enemyPrediction.getEnemyAvgChangeHeadingRadian() - avgAbsAvgHeadingChangeRadian <= Math.max(avgDeltaAbsAvgHeadingChangeRadian, 0.015))
                .count();
        double certaintyGoodAvgHeadingChangeRadian = countGoodAvgHeadingChangeRadian / predictionsCount;
        double certaintyGoodPrediction = (certaintyGoodAvgVelocity + certaintyGoodAvgHeadingChangeRadian) / 2;
        return certaintyGoodPrediction;
    }
}
