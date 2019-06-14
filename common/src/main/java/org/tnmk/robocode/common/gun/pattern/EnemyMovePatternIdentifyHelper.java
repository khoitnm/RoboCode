package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;
import org.tnmk.robocode.common.helper.prediction.EnemyPrediction;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.model.enemy.*;

/**
 * While {@link PatternPredictionUtils} is just do simple prediction at one point of time (that time could be in the future or any time in the past).
 * Then this class will use the above prediction result to compare with the history data so that it can make sure the above prediction is correct or not.
 */
public class EnemyMovePatternIdentifyHelper {
    private static final int MIN_HISTORY_ITEMS_FOR_PREDICTION = 4;
    private static final int IDEAL_HISTORY_ITEMS_FOR_PREDICTION = 5;
    private static final double ACCEPTABLE_PREDICTION_DIFF_PER_TICK = 0.5;//RobotPhysics.ROBOT_SIZE / 2;
    /**
     * Measure unit: ticks.<br/>
     * <p/>
     * If the last time we identified the enemy pattern older than this expiration period, we should identify the enemy's pattern again.
     */
    private static final long ENEMY_PATTERN_IDENTIFICATION_EXPIRATION_PERIOD = 30;

    /**
     * @param predictionTime         the current time when we do prediction.
     * @param enemyStatisticContext at this moment, the first item in this {@link EnemyStatisticContext#getEnemyHistory()} is the current enemy data which has just added in the same tick.
     */
    public static void identifyEnemyPatternIfNecessary(long predictionTime, EnemyStatisticContext enemyStatisticContext) {
        if (!hasNewIdentifiedPattern(predictionTime, enemyStatisticContext) && hasEnoughReliableHistoryData(enemyStatisticContext)) {
            Optional<EnemyMovePattern> historicalPatternOptional = EnemyMovePatternIdentifyHelper.predictHistoricalPattern(enemyStatisticContext.getEnemyHistory());
            System.out.println("Historical Prediction: Enemy name: " + enemyStatisticContext.getEnemyName() + ", historicalPattern: " + historicalPatternOptional.get() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());
            if (historicalPatternOptional.isPresent()) {
                EnemyMovePattern pattern = historicalPatternOptional.get();
                PatternIdentification patternIdentification = PatternIdentificationCertaintyCalculator.calculateHistoryCertainty(predictionTime, pattern, enemyStatisticContext.getEnemyPredictionHistory());
                enemyStatisticContext.setPatternIdentification(patternIdentification);
//                EnemyPrediction futurePrediction = PatternPredictionUtils.predictEnemy(enemyStatisticContext.getEnemyHistory().getLatestHistoryItems(IDEAL_HISTORY_ITEMS_FOR_PREDICTION), predictionTime);
//                //Historical prediction should be very reliable, so the future prediction should match with the historical prediction.
//                if (futurePrediction.getEnemyMovePattern() == historicalPatternOptional.get()) {
//                    System.out.println("Future prediction (ADD): Enemy name: " + enemyStatisticContext.getEnemyName() + ", historicalPattern: " + historicalPatternOptional.get() + ", historySize: " + enemyStatisticContext.getEnemyHistory().countHistoryItems());
//                    enemyStatisticContext.addPrediction(futurePrediction);
//                } else {
//                    System.out.println("Future prediction (don't add): Enemy name: " + enemyStatisticContext.getEnemyName() + ", newPattern: " + futurePrediction.getEnemyMovePattern());
//                    //Future prediction is not reliable, so don't add it.
//                    //Gun strategy should not rely on this pattern prediction.
//                }
            } else {
                //If cannot do historical prediction, then we cannot do futurePrediction.
                //It means we should add any prediction at this moment.
                //Gun strategy should not rely on this pattern prediction.
            }
        }
    }

    /**
     * @see #identifyEnemyPatternIfNecessary(long, EnemyStatisticContext)
     */
    private static boolean hasNewIdentifiedPattern(long predictionTime, EnemyStatisticContext enemyStatisticContext) {
        if (enemyStatisticContext.hasCertainPattern()) {
            long periodSinceTheLastPrediction = predictionTime - enemyStatisticContext.getLatestPredictionTime();
            return periodSinceTheLastPrediction < ENEMY_PATTERN_IDENTIFICATION_EXPIRATION_PERIOD;
        } else {
            return false;
        }
    }

    private static boolean hasEnoughReliableHistoryData(EnemyStatisticContext enemyStatisticContext) {
        return enemyStatisticContext.getEnemyHistory().countHistoryItems() >= MIN_HISTORY_ITEMS_FOR_PREDICTION;
    }

    /**
     * @param enemyHistory at this moment, the first item in this enemyHistory is the current enemy data which has just added in the same tick.
     * @return if not enough history data to predict, return {@link Optional#empty()}.
     */
    public static Optional<EnemyMovePattern> predictHistoricalPattern(EnemyHistory enemyHistory) {
        if (enemyHistory.countHistoryItems() < MIN_HISTORY_ITEMS_FOR_PREDICTION) {
            return Optional.empty();
        } else {
            HistoricalPredictionResult historicalPredictionResult = predictAtTheTimeOfAnExpectedHistoryItem(enemyHistory, 3, 0);
            if (predictMostlyCorrect(historicalPredictionResult.predictionDeltaTime, historicalPredictionResult.predictionPosition, historicalPredictionResult.actualPosition)) {
                debugPrintPredictedPositionAndActualPosition(enemyHistory.getName(), historicalPredictionResult.timeOfNewestItemForPrediction, historicalPredictionResult.itemOfExpectComparision, historicalPredictionResult.predictionDeltaTime, historicalPredictionResult.predictionPosition, historicalPredictionResult.actualPosition);
                return Optional.of(historicalPredictionResult.enemyMovePattern);
            } else {
                return Optional.of(EnemyMovePattern.UNIDENTIFIED);
            }
        }
    }

    private static EnemyPrediction toEnemyPrediction(HistoricalPredictionResult historicalPredictionResult) {
        EnemyPrediction enemyPrediction = new EnemyPrediction(historicalPredictionResult.enemyMovePattern, historicalPredictionResult.itemOfExpectComparision, historicalPredictionResult.predictionPosition);
        return enemyPrediction;
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
     *                                        This number must be less than {@link #MIN_HISTORY_ITEMS_FOR_PREDICTION} - 1
     * @param expectComparisionHistoryIndex   the index of history item will be used to get the predictionTiem. Then we hope that the prediction result at that time will match with the actual recored result at that time. Of courses, this index must be less (newer) than predictSinceHistoryItemIndex.
     * @return get history items with `predictSinceHistoryItemIndex` (and older history data), do prediction and then compare result with the history item with 'compareToActualHistoryItemIndex'
     */
    private static HistoricalPredictionResult predictAtTheTimeOfAnExpectedHistoryItem(EnemyHistory enemyHistory, int newestHistoryIndexForPrediction, int expectComparisionHistoryIndex) {
        List<Enemy> enemyList = enemyHistory.getLatestHistoryItems(newestHistoryIndexForPrediction + IDEAL_HISTORY_ITEMS_FOR_PREDICTION);
        List<Enemy> itemsToDoPrediction = enemyList.subList(newestHistoryIndexForPrediction, enemyList.size());

        Enemy newestItemForPrediction = itemsToDoPrediction.get(0);
        Enemy expectedEnemyData = enemyList.get(expectComparisionHistoryIndex);

        long timeOfNewestItemForPrediction = newestItemForPrediction.getTime();
        long itemOfExpectComparision = expectedEnemyData.getTime();
        long deltaTimeBetweenPredictionAndActual = itemOfExpectComparision - timeOfNewestItemForPrediction;

        EnemyPrediction enemyPrediction = PatternPredictionUtils.predictEnemy(itemsToDoPrediction, itemOfExpectComparision);
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