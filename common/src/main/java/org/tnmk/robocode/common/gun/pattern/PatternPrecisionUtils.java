package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Point2D;
import java.util.List;
import org.tnmk.robocode.common.helper.Move2DUtils;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyHistoryUtils;

public class PatternPrecisionUtils {

    public static List<Point2D> findPotentialPositionsAfterTimePeriod(EnemyHistory enemyHistory, long ticks){


        Enemy enemy = enemyHistory.getLatestHistoryItem();
        List<Enemy> latestHistory = enemyHistory.getLatestHistoryItems(2);
        double avgChangeHeadingRadian = EnemyHistoryUtils.averageChangeHeadingRadian(latestHistory);
        double latestVelocity = enemy.getVelocity();
        double acceleration = 0;
        if (enemyHistory.countHistoryItems() > 1) {
            Enemy previousHistoryItem = latestHistory.get(1);
            double previousVelocity = previousHistoryItem.getVelocity();
            double timePeriod = enemy.getTime() - previousHistoryItem.getTime();
            acceleration = (latestVelocity - previousVelocity) / timePeriod;
            acceleration = Move2DUtils.normalizeAcceleration(acceleration);
        }
//        double avgVelocity = EnemyHistoryUtils.averageVelocity(historyItems);
//        return PatternPredictionUtils.predictEnemy(enemy, avgVelocity, avgChangeHeadingRadian, predictionTime, enemyMovementArea);
    }

}
