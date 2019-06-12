package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;

public class EnemyPatternTypeUtils {
    private static final int MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN = 3;
    private static final double ACCEPTABLE_PREDICTION_DIFF = RobotPhysics.ROBOT_SIZE / 2;

    public static void identifyPatternIfNecessary(EnemyPatternPrediction enemyPatternPrediction) {
        if (!enemyPatternPrediction.isIdentifiedPattern() && enemyPatternPrediction.getEnemyHistory().countHistoryItems() >= MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN) {
            EnemyPatternType enemyPatternType = EnemyPatternTypeUtils.identifyPattern(enemyPatternPrediction.getEnemyHistory());
            System.out.println("Enemy name: " + enemyPatternPrediction.getEnemyName() + ", pattern: " + enemyPatternType + ", historySize: " + enemyPatternPrediction.getEnemyHistory().countHistoryItems());
            enemyPatternPrediction.setEnemyPatternType(enemyPatternType);
        }
    }

    public static EnemyPatternType identifyPattern(EnemyHistory enemyHistory) {
        if (enemyHistory.countHistoryItems() < MIN_HISTORY_ITEMS_TO_PREDICT_PATTERN) {
            return EnemyPatternType.UNIDENTIFIED;
        } else {
            List<Enemy> enemyList = enemyHistory.getLatestHistoryItems(4);
            List<Enemy> historyFromSecondItem = enemyList.subList(1, enemyList.size());
            Enemy latestHistoryItem = enemyList.get(0);

            Point2D predictedEnemyBasedOnHistoryFromSecondItem = CircularGuessUtils.guessPosition(historyFromSecondItem, latestHistoryItem.getTime());
            Point2D actualEnemyPosition = latestHistoryItem.getPosition();
            if (predictMostlyCorrect(predictedEnemyBasedOnHistoryFromSecondItem, actualEnemyPosition)) {
                return EnemyPatternType.CIRCULAR;
            } else {
                //TODO predict Linear
                return EnemyPatternType.UNIDENTIFIED;
            }
        }
    }

    private static boolean predictMostlyCorrect(Point2D predictPosition, Point2D actualPosition) {
        boolean isCloselyPrediction = actualPosition.distance(predictPosition) <= ACCEPTABLE_PREDICTION_DIFF;
        return isCloselyPrediction;
    }
}
