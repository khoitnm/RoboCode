package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;

public class EnemyPatternTypeUtils {
    private static final int MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN = 3;
    private static final double ACCEPTABLE_PREDICTION_DIFF_PER_TICK = 0.5;//RobotPhysics.ROBOT_SIZE / 2;
    /**
     * Measure unit: ticks.<br/>
     * <p/>
     * If the last time we identified the enemy pattern older than this expiration period, we should identify the enemy's pattern again.
     */
    private static final long ENEMY_PATTERN_IDENTIFICATION_EXPIRATION_PERIOD = 30;

    /**
     * @param predictionTime         the current time when we do prediction.
     * @param enemyPatternPrediction at this moment, the first item in this {@link EnemyPatternPrediction#getEnemyHistory()} is the current enemy data which has just added in the same tick.
     */
    public static void identifyPatternIfNecessary(long predictionTime, EnemyPatternPrediction enemyPatternPrediction) {
        if (!hasNewIdentifiedPattern(predictionTime, enemyPatternPrediction) && hasEnoughReliableHistoryData(enemyPatternPrediction)) {
            EnemyPatternType enemyPatternType = EnemyPatternTypeUtils.identifyPattern(enemyPatternPrediction.getEnemyHistory());
            System.out.println("Enemy name: " + enemyPatternPrediction.getEnemyName() + ", pattern: " + enemyPatternType + ", historySize: " + enemyPatternPrediction.getEnemyHistory().countHistoryItems());
            enemyPatternPrediction.setEnemyPatternType(predictionTime, enemyPatternType);
        }
    }

    /**
     * @see #identifyPatternIfNecessary(long, EnemyPatternPrediction)
     */
    private static boolean hasNewIdentifiedPattern(long predictionTime, EnemyPatternPrediction enemyPatternPrediction) {
        if (enemyPatternPrediction.isIdentifiedPattern()) {
            long periodSinceTheLastPrediction = predictionTime - enemyPatternPrediction.getPredictedTime();
            return periodSinceTheLastPrediction < ENEMY_PATTERN_IDENTIFICATION_EXPIRATION_PERIOD;
        } else {
            return false;
        }
    }

    private static boolean hasEnoughReliableHistoryData(EnemyPatternPrediction enemyPatternPrediction) {
        return enemyPatternPrediction.getEnemyHistory().countHistoryItems() >= MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN;
    }

    /**
     * @param enemyHistory at this moment, the first item in this enemyHistory is the current enemy data which has just added in the same tick.
     * @return
     */
    public static EnemyPatternType identifyPattern(EnemyHistory enemyHistory) {
        if (enemyHistory.countHistoryItems() < MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN) {
            return EnemyPatternType.UNIDENTIFIED;
        } else {
            List<Enemy> enemyList = enemyHistory.getLatestHistoryItems(4);
            List<Enemy> historyExcludeCurrentEnemyData = enemyList.subList(1, enemyList.size());
            Enemy currentEnemyData = enemyList.get(0);
            long timeOfLatestDataForPrediction = historyExcludeCurrentEnemyData.get(0).getTime();
            long currentTime = currentEnemyData.getTime();
            long deltaTimeBetweenPredictionAndActual = currentTime - timeOfLatestDataForPrediction;
            Point2D predictedEnemyBasedOnHistoryExcludeCurrentData = CircularGuessUtils.guessPosition(historyExcludeCurrentEnemyData, currentEnemyData.getTime());
            Point2D actualEnemyPosition = currentEnemyData.getPosition();
            if (predictMostlyCorrect(deltaTimeBetweenPredictionAndActual, predictedEnemyBasedOnHistoryExcludeCurrentData, actualEnemyPosition)) {
                debugPrintPredictedPositionAndActualPosition(timeOfLatestDataForPrediction, currentTime, deltaTimeBetweenPredictionAndActual, predictedEnemyBasedOnHistoryExcludeCurrentData, actualEnemyPosition);
                return EnemyPatternType.CIRCULAR;
            } else {
                //TODO predict Linear
                return EnemyPatternType.UNIDENTIFIED;
            }
        }
    }

    private static void debugPrintPredictedPositionAndActualPosition(long timeOfLatestDataForPrediction, long currentTime, long deltaTimeBetweenPredictionAndActual, Point2D predictedEnemyPosition, Point2D actualEnemyPosition) {
        System.out.println("Predicted position: " + LogHelper.toString(predictedEnemyPosition)
                + "\tPredicted time: " + timeOfLatestDataForPrediction + "\tCurrent time: " + currentTime + "\tdeltaTime: " + deltaTimeBetweenPredictionAndActual
                + "\n\tActual position: " + LogHelper.toString(actualEnemyPosition)
                + "\n\tPrediction diff: " + predictedEnemyPosition.distance(actualEnemyPosition)
                + "\n\tPrediction diff per tick: " + predictedEnemyPosition.distance(actualEnemyPosition) / deltaTimeBetweenPredictionAndActual
        );
    }

    private static boolean predictMostlyCorrect(long deltaTimeBetweenPredictionAndActual, Point2D predictPosition, Point2D actualPosition) {
        boolean isCloselyPrediction = actualPosition.distance(predictPosition) / deltaTimeBetweenPredictionAndActual <= ACCEPTABLE_PREDICTION_DIFF_PER_TICK;
        return isCloselyPrediction;
    }
}
