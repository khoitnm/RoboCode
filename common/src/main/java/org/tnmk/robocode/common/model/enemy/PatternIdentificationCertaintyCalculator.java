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

    /**
     * <pre>
     * Below are some example data with coefficients:
     *  - countGoodAvgVelocity: 0.5
     *  - countGoodAvgHeadingChangeRadian: 0.015 -> I may want to change it to 0.0125 (the lower number, the lower certainty)
     *
     * Mobius: it usually move a short distance and change direction quite frequently, but not change much direction.
     * certainty velocity: 0.5	 certainty headingChange: 0.6875	 certainty movement: 0.59375
     * certainty velocity: 0.47619047619047616	 certainty headingChange: 0.9523809523809523	 certainty movement: 0.7142857142857142
     * certainty velocity: 0.46153846153846156	 certainty headingChange: 0.6923076923076923	 certainty movement: 0.5769230769230769
     * certainty velocity: 0.4444444444444444	 certainty headingChange: 0.6666666666666666	 certainty movement: 0.5555555555555556
     * certainty velocity: 0.5	 certainty headingChange: 0.9	 certainty movement: 0.7
     * certainty velocity: 0.5333333333333333	 certainty headingChange: 0.9333333333333333	 certainty movement: 0.7333333333333334
     * certainty velocity: 0.6333333333333333	 certainty headingChange: 0.8666666666666667	 certainty movement: 0.75
     *
     * SpinBot: run circle, not change velocity:
     * certainty velocity: 1.0	 certainty headingChange: 1.0	 certainty movement: 1.0
     * certainty velocity: 1.0	 certainty headingChange: 1.0	 certainty movement: 1.0
     * certainty velocity: 1.0	 certainty headingChange: 1.0	 certainty movement: 1.0
     *
     * Briareos: run at random direction. But it keeps velocity quite often, unless it has some crash (quite often because it moves a lot and don't avoid enemies), sometimes run on the strange line for awhile.
     * certainty velocity: 0.4482758620689655	 certainty headingChange: 0.7241379310344828	 certainty movement: 0.5862068965517242
     * certainty velocity: 0.36666666666666664	 certainty headingChange: 0.7333333333333333	 certainty movement: 0.5499999999999999
     * certainty velocity: 1.0	 certainty headingChange: 0.7666666666666667	 certainty movement: 0.8833333333333333
     * certainty velocity: 1.0	 certainty headingChange: 0.8695652173913043	 certainty movement: 0.9347826086956521
     * certainty velocity: 1.0	 certainty headingChange: 0.8571428571428571	 certainty movement: 0.9285714285714286
     * </pre>
     *
     * @param enemyPredictionsWithTheSamePattern
     * @return
     */
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
                .filter(enemyPrediction -> enemyPrediction.getEnemyAvgChangeHeadingRadian() - avgAbsAvgHeadingChangeRadian <= Math.max(avgDeltaAbsAvgHeadingChangeRadian, 0.0125))
                .count();
        double certaintyGoodAvgHeadingChangeRadian = countGoodAvgHeadingChangeRadian / predictionsCount;
        double certaintyGoodPrediction = (certaintyGoodAvgVelocity + certaintyGoodAvgHeadingChangeRadian) / 2;
        System.out.println("certainty velocity: " + certaintyGoodAvgVelocity +
                "\t certainty headingChange: " + certaintyGoodAvgHeadingChangeRadian +
                "\t certainty movement: " + certaintyGoodPrediction);
        return certaintyGoodPrediction;
    }
}
