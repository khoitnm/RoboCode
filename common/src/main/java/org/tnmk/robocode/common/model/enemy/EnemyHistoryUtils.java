package org.tnmk.robocode.common.model.enemy;

import java.util.ArrayList;
import java.util.List;
import org.tnmk.common.math.AngleUtils;

public class EnemyHistoryUtils {
    public static double averageChangeHeadingRadian(EnemyHistory enemyHistory, int historyItemsCount) {
        List<Enemy> latestEnemyHistoryItems = enemyHistory.getLatestHistoryItems(historyItemsCount);
        return averageChangeHeadingRadian(latestEnemyHistoryItems);
    }

    public static double averageChangeHeadingRadian(List<Enemy> latestEnemyHistoryItems) {
        List<Double> changeHeadingRadians = new ArrayList<>();
        Enemy previousHistoryItem = null;
        for (Enemy latestEnemyHistoryItem : latestEnemyHistoryItems) {
            if (previousHistoryItem != null) {
                double changeHeadingDegree = latestEnemyHistoryItem.getHeading() - previousHistoryItem.getHeading();
                double changeHeadingRadian = AngleUtils.toRadian(changeHeadingDegree);
                double changeTime = latestEnemyHistoryItem.getTime() - previousHistoryItem.getTime();
                double changeHeadingRadianPerTick = changeHeadingRadian / changeTime;//angular velocity
                changeHeadingRadians.add(changeHeadingRadianPerTick);
            }
            previousHistoryItem = latestEnemyHistoryItem;
        }
        double avgChangeHeading = 0;
        if (!changeHeadingRadians.isEmpty()) {
            avgChangeHeading = changeHeadingRadians.stream().mapToDouble(changeHeading -> changeHeading).average().getAsDouble();
        }
        return avgChangeHeading;
    }

    public static double averageVelocity(List<Enemy> historyItems) {
        double avgVelocity = historyItems.stream().mapToDouble(Enemy::getVelocity).average().getAsDouble();
        return avgVelocity;
    }
}
