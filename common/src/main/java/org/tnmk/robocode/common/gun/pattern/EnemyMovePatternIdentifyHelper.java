package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;

public class EnemyMovePatternIdentifyHelper {
    private static final int MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN = 5;
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
            EnemyMovePattern enemyMovePattern = EnemyMovePatternIdentifyHelper.identifyPattern(enemyPatternPrediction.getEnemyHistory());
            System.out.println("Enemy name: " + enemyPatternPrediction.getEnemyName() + ", pattern: " + enemyMovePattern + ", historySize: " + enemyPatternPrediction.getEnemyHistory().countHistoryItems());
            enemyPatternPrediction.setEnemyPatternType(predictionTime, enemyMovePattern);
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
    public static EnemyMovePattern identifyPattern(EnemyHistory enemyHistory) {
        if (enemyHistory.countHistoryItems() < MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN) {
            return EnemyMovePattern.UNIDENTIFIED;
        } else {
            PredictionResult predictionResult = comparePredictionAndActualBetweenHistoryData(enemyHistory, 3, 0);
            if (predictMostlyCorrect(predictionResult.predictionDeltaTime, predictionResult.predictionPosition, predictionResult.actualPosition)) {
                debugPrintPredictedPositionAndActualPosition(enemyHistory.getName(), predictionResult.timeOfNewestItemForPrediction, predictionResult.itemOfExpectComparision, predictionResult.predictionDeltaTime, predictionResult.predictionPosition, predictionResult.actualPosition);
                return EnemyMovePattern.CIRCULAR_AND_LINEAR;
            } else {
                //TODO predict Linear
                return EnemyMovePattern.UNIDENTIFIED;
            }
        }
    }

    /**
     * @param enemyHistory
     * @param newestHistoryIndexForPrediction the index of history item we will use to do prediction (and also include older history items).
     *                                        This number must be less than {@link #MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN} - 1
     * @param expectComparisionHistoryIndex   the index of history item will used to compare with the prediction position, and expect that the actual data and prediction data is close enough. Of courses, this index must be less (newer) than predictSinceHistoryItemIndex.
     * @return get history items with `predictSinceHistoryItemIndex` (and older history data), do prediction and then compare result with the history item with 'compareToActualHistoryItemIndex'
     */
    private static PredictionResult comparePredictionAndActualBetweenHistoryData(EnemyHistory enemyHistory, int newestHistoryIndexForPrediction, int expectComparisionHistoryIndex) {
        List<Enemy> enemyList = enemyHistory.getLatestHistoryItems(newestHistoryIndexForPrediction + 4);
        List<Enemy> itemsToDoPrediction = enemyList.subList(newestHistoryIndexForPrediction, enemyList.size());

        Enemy newestItemForPrediction = itemsToDoPrediction.get(0);
        Enemy expectedEnemyData = enemyList.get(expectComparisionHistoryIndex);

        long timeOfNewestItemForPrediction = newestItemForPrediction.getTime();
        long itemOfExpectComparision = expectedEnemyData.getTime();
        long deltaTimeBetweenPredictionAndActual = itemOfExpectComparision - timeOfNewestItemForPrediction;

        Point2D predictedEnemyPosition = CircularAndLinearGuessUtils.guessPosition(itemsToDoPrediction, itemOfExpectComparision);
        Point2D actualEnemyPosition = expectedEnemyData.getPosition();
        return new PredictionResult(predictedEnemyPosition, actualEnemyPosition, deltaTimeBetweenPredictionAndActual, timeOfNewestItemForPrediction, itemOfExpectComparision);
    }

    private static boolean predictMostlyCorrect(long deltaTimeBetweenPredictionAndActual, Point2D predictPosition, Point2D actualPosition) {
        boolean isCloselyPrediction = actualPosition.distance(predictPosition) / deltaTimeBetweenPredictionAndActual <= ACCEPTABLE_PREDICTION_DIFF_PER_TICK;
        return isCloselyPrediction;
    }

    private static void debugPrintPredictedPositionAndActualPosition(String enemyName, long timeOfNewestItemForPrediction, long timeOfComparisionItem, long deltaTimeBetweenPredictionAndActual, Point2D predictedEnemyPosition, Point2D actualEnemyPosition) {
        System.out.println("Predicted enemy: " + enemyName
                + "\n\tposition: " + LogHelper.toString(predictedEnemyPosition)
                + "\tPredicted time: " + timeOfNewestItemForPrediction + "\tCurrent time: " + timeOfComparisionItem + "\tdeltaTime: " + deltaTimeBetweenPredictionAndActual
                + "\n\tActual position: " + LogHelper.toString(actualEnemyPosition)
                + "\n\tPrediction diff: " + String.format("%.2f", predictedEnemyPosition.distance(actualEnemyPosition))
                + "\n\tPrediction diff per tick: " + String.format("%.2f", predictedEnemyPosition.distance(actualEnemyPosition) / deltaTimeBetweenPredictionAndActual)
        );
    }

    public static class PredictionResult {
        /**
         * The delta between the time of the newest data used for prediction and the time of actual data.
         */
        private final long predictionDeltaTime;
        private final Point2D predictionPosition;
        private final Point2D actualPosition;

        private final long timeOfNewestItemForPrediction;
        private final long itemOfExpectComparision;

        public PredictionResult(Point2D predictionPosition, Point2D actualPosition, long predictionDeltaTime, long timeOfNewestItemForPrediction, long itemOfExpectComparision) {
            this.predictionDeltaTime = predictionDeltaTime;
            this.predictionPosition = predictionPosition;
            this.actualPosition = actualPosition;
            this.timeOfNewestItemForPrediction = timeOfNewestItemForPrediction;
            this.itemOfExpectComparision = itemOfExpectComparision;
        }
    }
}
