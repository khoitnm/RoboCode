package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPredictionHistory;

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
     * @param enemyPredictionHistory at this moment, the first item in this {@link EnemyPredictionHistory#getEnemyHistory()} is the current enemy data which has just added in the same tick.
     */
    public static void identifyPatternIfNecessary(long predictionTime, EnemyPredictionHistory enemyPredictionHistory) {
        if (!hasNewIdentifiedPattern(predictionTime, enemyPredictionHistory) && hasEnoughReliableHistoryData(enemyPredictionHistory)) {
            EnemyMovePattern enemyMovePattern = EnemyMovePatternIdentifyHelper.identifyPattern(enemyPredictionHistory.getEnemyHistory());
            System.out.println("Enemy name: " + enemyPredictionHistory.getEnemyName() + ", pattern: " + enemyMovePattern + ", historySize: " + enemyPredictionHistory.getEnemyHistory().countHistoryItems());
            enemyPredictionHistory.setEnemyPatternType(predictionTime, enemyMovePattern);
        }
    }

    /**
     * @see #identifyPatternIfNecessary(long, EnemyPredictionHistory)
     */
    private static boolean hasNewIdentifiedPattern(long predictionTime, EnemyPredictionHistory enemyPredictionHistory) {
        if (enemyPredictionHistory.isIdentifiedPattern()) {
            long periodSinceTheLastPrediction = predictionTime - enemyPredictionHistory.getPredictedTime();
            return periodSinceTheLastPrediction < ENEMY_PATTERN_IDENTIFICATION_EXPIRATION_PERIOD;
        } else {
            return false;
        }
    }

    private static boolean hasEnoughReliableHistoryData(EnemyPredictionHistory enemyPredictionHistory) {
        return enemyPredictionHistory.getEnemyHistory().countHistoryItems() >= MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN;
    }

    /**
     * @param enemyHistory at this moment, the first item in this enemyHistory is the current enemy data which has just added in the same tick.
     * @return
     */
    public static EnemyMovePattern identifyPattern(EnemyHistory enemyHistory) {
        if (enemyHistory.countHistoryItems() < MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN) {
            return EnemyMovePattern.UNIDENTIFIED;
        } else {
            HistoricalPredictionResult historicalPredictionResult = predictAtTheTimeOfAnExpectedHistoryItem(enemyHistory, 3, 0);
            if (predictMostlyCorrect(historicalPredictionResult.predictionDeltaTime, historicalPredictionResult.predictionPosition, historicalPredictionResult.actualPosition)) {
                debugPrintPredictedPositionAndActualPosition(enemyHistory.getName(), historicalPredictionResult.timeOfNewestItemForPrediction, historicalPredictionResult.itemOfExpectComparision, historicalPredictionResult.predictionDeltaTime, historicalPredictionResult.predictionPosition, historicalPredictionResult.actualPosition);
                return historicalPredictionResult.enemyMovePattern;
            } else {
                return EnemyMovePattern.UNIDENTIFIED;
            }
        }
    }

    /**
     * <pre>
     * The general idea:
     *
     * Let say we have 7 history items:
     * [0][1][2][3][4][5][6]
     *
     * expectComparisionHistoryIndex = 0. It means we'll get item [0].time
     * newestHistoryIndexForPrediction = 2. It means that we will get history items [2][3][4][5][6] and then do prediction position & pattern what should have been the data at [0].time
     *
     * If the prediction data at [0].time is mostly the same at data of [0], it means the prediction is correct.
     *
     * Now, this method doesn't do comparision between history data [0] and prediction data at [0].time
     * It just do prediction and return the data.
     * </pre>
     *
     * @param enemyHistory
     * @param newestHistoryIndexForPrediction the index of history item we will use to do prediction (and also include older history items).
     *                                        This number must be less than {@link #MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN} - 1
     * @param expectComparisionHistoryIndex   the index of history item will be used to get the predictionTiem. Then we hope that the prediction result at that time will match with the actual recored result at that time. Of courses, this index must be less (newer) than predictSinceHistoryItemIndex.
     * @return get history items with `predictSinceHistoryItemIndex` (and older history data), do prediction and then compare result with the history item with 'compareToActualHistoryItemIndex'
     */
    private static HistoricalPredictionResult predictAtTheTimeOfAnExpectedHistoryItem(EnemyHistory enemyHistory, int newestHistoryIndexForPrediction, int expectComparisionHistoryIndex) {
        List<Enemy> enemyList = enemyHistory.getLatestHistoryItems(newestHistoryIndexForPrediction + 4);
        List<Enemy> itemsToDoPrediction = enemyList.subList(newestHistoryIndexForPrediction, enemyList.size());

        Enemy newestItemForPrediction = itemsToDoPrediction.get(0);
        Enemy expectedEnemyData = enemyList.get(expectComparisionHistoryIndex);

        long timeOfNewestItemForPrediction = newestItemForPrediction.getTime();
        long itemOfExpectComparision = expectedEnemyData.getTime();
        long deltaTimeBetweenPredictionAndActual = itemOfExpectComparision - timeOfNewestItemForPrediction;

        EnemyPrediction enemyPrediction =PatternPredictionUtils.predictEnemy(itemsToDoPrediction, itemOfExpectComparision);
        Point2D predictedEnemyPosition = enemyPrediction.getPredictionPosition();
        Point2D actualEnemyPosition = expectedEnemyData.getPosition();
        return new HistoricalPredictionResult(enemyPrediction.getEnemyMovePattern(), predictedEnemyPosition, actualEnemyPosition, deltaTimeBetweenPredictionAndActual, timeOfNewestItemForPrediction, itemOfExpectComparision);
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

    /**
     * Represent the prediction based on historical items.
     * And then we'll use it to compare with another (newer) historical item and expect they are matching together.
     */
    public static class HistoricalPredictionResult {
        private final EnemyMovePattern enemyMovePattern;
        /**
         * The delta between the time of the newest data used for prediction and the time of actual data.
         */
        private final long predictionDeltaTime;
        private final Point2D predictionPosition;
        private final Point2D actualPosition;

        private final long timeOfNewestItemForPrediction;
        private final long itemOfExpectComparision;

        public HistoricalPredictionResult(EnemyMovePattern enemyMovePattern, Point2D predictionPosition, Point2D actualPosition, long predictionDeltaTime, long timeOfNewestItemForPrediction, long itemOfExpectComparision) {
            this.enemyMovePattern = enemyMovePattern;
            this.predictionDeltaTime = predictionDeltaTime;
            this.predictionPosition = predictionPosition;
            this.actualPosition = actualPosition;
            this.timeOfNewestItemForPrediction = timeOfNewestItemForPrediction;
            this.itemOfExpectComparision = itemOfExpectComparision;
        }
    }
}
