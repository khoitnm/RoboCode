package org.tnmk.robocode.common.model.enemy;

import java.util.ArrayList;
import java.util.List;

public class EnemyHistoryUtils {
    public static double averageChangeHeadings(EnemyHistory enemyHistory, int historyItemsCount) {
        List<Enemy> latestEnemyHistoryItems = enemyHistory.getLatestHistoryItems(historyItemsCount);
        List<Double> changeHeadings = new ArrayList<>();
        Enemy previousHistoryItem = null;
        for (Enemy latestEnemyHistoryItem : latestEnemyHistoryItems) {
            if (previousHistoryItem != null) {
                double changeHeading = latestEnemyHistoryItem.getHeading() - previousHistoryItem.getHeading();
                double changeTime = latestEnemyHistoryItem.getTime() - previousHistoryItem.getTime();
                double changeHeadingPerTick = changeHeading / changeTime;
                changeHeadings.add(changeHeadingPerTick);
            }
            previousHistoryItem = latestEnemyHistoryItem;
        }
        double avgChangeHeading = 0;
        if (!changeHeadings.isEmpty()) {
            avgChangeHeading = changeHeadings.stream().mapToDouble(ichangeHeading -> ichangeHeading).average().getAsDouble();
        }
        return avgChangeHeading;
    }
}
