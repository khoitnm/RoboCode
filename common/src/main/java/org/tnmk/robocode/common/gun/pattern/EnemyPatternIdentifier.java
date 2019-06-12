package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyPatternPrediction;

public class EnemyPatternIdentifier {

    public static void indentifyPattern(EnemyPatternPrediction enemyPatternPrediction) {
        if (!enemyPatternPrediction.isIdentifiedPattern()) {
            EnemyPattern enemyPattern = EnemyPatternIdentifier.identifyPattern(enemyPatternPrediction.getEnemyHistory());
            enemyPatternPrediction.setEnemyPattern(enemyPattern);
        }
    }

    public static EnemyPattern identifyPattern(EnemyHistory enemyHistory) {
        if (enemyHistory.countHistoryItems() < 2) {
            return EnemyPattern.UNIDENTIFIED;
        } else {
            List<Enemy> enemyList = enemyHistory.getLatestHistoryItems(4);
            List<Enemy> historyFromSecondItem = enemyList.subList(1, enemyList.size());
            Enemy latestHistoryItem = enemyList.get(0);

            Point2D predictedEnemyBasedOnHistoryFromSecondItem = CircularGuessUtils.guessPosition(historyFromSecondItem, latestHistoryItem.getTime());
            Point2D actualEnemyPosition = latestHistoryItem.getPosition();
            if (predictMostlyCorrect(predictedEnemyBasedOnHistoryFromSecondItem, actualEnemyPosition)) {
                return EnemyPattern.CIRCULAR;
            } else {
                //TODO predict Linear
                return EnemyPattern.UNIDENTIFIED;
            }
        }
    }

    private static boolean predictMostlyCorrect(Point2D predictPosition, Point2D actualPosition) {
        boolean isCloselyPrediction = actualPosition.distance(predictPosition) <= 2;
        return isCloselyPrediction;
    }
}
